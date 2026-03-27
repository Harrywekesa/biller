package com.mpesa.tracker.framework.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.mpesa.tracker.data.local.dao.TransactionDao
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.TransactionType
import com.mpesa.tracker.domain.parser.MpesaParser
import dagger.hilt.android.AndroidEntryPoint
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mpesa.tracker.framework.workers.MpesaSmsWorker
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {

    // transactionDao is no longer needed directly in Receiver

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val mpesaSmsBody = StringBuilder()
        var isMpesa = false

        for (sms in messages) {
            val sender = sms.originatingAddress
            // Common M-Pesa sender IDs
            if (sender.equals("MPESA", ignoreCase = true) || 
                sender.equals("M-PESA", ignoreCase = true)) {
                isMpesa = true
                mpesaSmsBody.append(sms.messageBody)
            }
        }

        if (isMpesa && mpesaSmsBody.isNotEmpty()) {
            val fullMessage = mpesaSmsBody.toString()
            Log.d("SmsReceiver", "Received M-Pesa SMS: $fullMessage")
            
            val parsedResult = MpesaParser.parseMessage(fullMessage)
            if (parsedResult != null) {
                 // Launch WorkManager to process SMS reliably
                val simId = intent.getIntExtra("subscription", -1)
                
                val workData = Data.Builder()
                    .putString("sms_body", fullMessage)
                    .putInt("sim_id", simId)
                    .build()
                
                val workRequest = OneTimeWorkRequestBuilder<MpesaSmsWorker>()
                    .setInputData(workData)
                    .build()
                    
                WorkManager.getInstance(context).enqueue(workRequest)
                Log.d("SmsReceiver", "Enqueued M-Pesa SMS for WorkManager processing")
            } else {
                 Log.w("SmsReceiver", "Failed to Regex match an incoming M-Pesa message.")
            }
        }
    }
}
