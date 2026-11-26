package com.mpesa.tracker.framework.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mpesa.tracker.domain.parser.BankSmsParser
import com.mpesa.tracker.framework.workers.BankSmsWorker
import com.mpesa.tracker.framework.workers.MpesaSmsWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return
        val simId = extractSimId(intent)

        // Group multipart SMS by originating sender
        val groupedBySender = linkedMapOf<String, StringBuilder>()
        for (sms in messages) {
            val sender = sms.originatingAddress ?: continue
            groupedBySender.getOrPut(sender) { StringBuilder() }.append(sms.messageBody ?: "")
        }

        for ((sender, bodyBuilder) in groupedBySender) {
            val fullBody = bodyBuilder.toString().trim()
            if (fullBody.isEmpty()) continue

            when {
                sender.equals("MPESA", ignoreCase = true) ||
                    sender.equals("M-PESA", ignoreCase = true) -> {
                    Log.d("SmsReceiver", "M-Pesa SMS received — enqueueing MpesaSmsWorker")
                    val workData = Data.Builder()
                        .putString("sms_body", fullBody)
                        .putString("sms_sender", sender)
                        .putInt("sim_id", simId)
                        .putLong("sms_date", System.currentTimeMillis())
                        .build()
                    WorkManager.getInstance(context)
                        .enqueue(OneTimeWorkRequestBuilder<MpesaSmsWorker>().setInputData(workData).build())
                }
                BankSmsParser.looksLikeSupportedBankSms(sender, fullBody) -> {
                    Log.d("SmsReceiver", "Bank SMS received from $sender — enqueueing BankSmsWorker")
                    val workData = Data.Builder()
                        .putString("sms_body", fullBody)
                        .putString("sms_sender", sender)
                        .putLong("sms_date", System.currentTimeMillis())
                        .build()
                    WorkManager.getInstance(context)
                        .enqueue(OneTimeWorkRequestBuilder<BankSmsWorker>().setInputData(workData).build())
                }
            }
        }
    }

    private fun extractSimId(intent: Intent): Int {
        val keys = listOf("subscription", "android.telephony.extra.SUBSCRIPTION_INDEX", "sim_id", "slot", "phone")
        for (key in keys) {
            val value = intent.getIntExtra(key, -1)
            if (value >= 0) return value
        }
        return -1
    }
}
