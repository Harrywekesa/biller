package com.mpesa.tracker.domain.intelligence

import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.TransactionNature
import com.mpesa.tracker.data.local.entities.TransactionType
import com.mpesa.tracker.domain.parser.AccountSource

/**
 * Pure wallet balance calculators shared across Overview, AI Chat, and unit tests.
 *
 * Solves two structural issues:
 * 1) Real-time SMS received via BroadcastReceiver on OEM ROMs can arrive with
 *    `simSubscriptionId = null`. Previously, once historical inbox sync populated
 *    `simSubscriptionId` for older rows, newer `null`-SIM SMS were ignored and the
 *    live M-Pesa balance appeared frozen.
 * 2) Cash Wallet (`AccountSource.CASH`) holds no SMS balance, so Cash on Hand is
 *    derived cleanly from M-Pesa cash withdrawals + manual Cash inflows/outflows.
 */
object WalletBalanceCalculator {

    fun computeMpesaBalance(mpesaTxs: List<TransactionEntity>): Double {
        if (mpesaTxs.isEmpty()) return 0.0
        val sorted = mpesaTxs.sortedByDescending { it.dateTimestamp }
        val knownSimTxs = sorted.filter { it.simSubscriptionId != null }
        if (knownSimTxs.isEmpty()) {
            return singleStreamBalance(sorted)
        }

        val bySim = knownSimTxs.groupBy { it.simSubscriptionId!! }
        // Single-SIM device: consider ALL M-Pesa transactions (including null-SIM real-time SMS)
        if (bySim.size == 1) {
            return singleStreamBalance(sorted)
        }

        // Dual-SIM device: sum latest balance per SIM, and if a newer real-time null-SIM SMS
        // arrived after the latest known-SIM transaction, update the most recently active SIM slot.
        val simBalances = bySim.mapValues { (_, list) -> singleStreamBalance(list) }.toMutableMap()
        val latestKnownBalanceTx = knownSimTxs.firstOrNull { it.balance != null }
        val latestUnknownBalanceTx = sorted.firstOrNull { it.simSubscriptionId == null && it.balance != null }

        if (latestUnknownBalanceTx != null &&
            (latestKnownBalanceTx == null || latestUnknownBalanceTx.dateTimestamp > latestKnownBalanceTx.dateTimestamp)
        ) {
            val mostActiveSim = latestKnownBalanceTx?.simSubscriptionId ?: bySim.keys.first()
            val mergedSlotTxs = (bySim[mostActiveSim].orEmpty() + latestUnknownBalanceTx)
                .sortedByDescending { it.dateTimestamp }
            simBalances[mostActiveSim] = singleStreamBalance(mergedSlotTxs)
        }

        return simBalances.values.sum()
    }

    /** Computes closed-loop estimated Cash on Hand from M-Pesa cash withdrawals + manual Cash entries. */
    fun computeEstimatedCashBalance(allTxs: List<TransactionEntity>): Double? {
        val withdrawnCash = allTxs.filter {
            it.type == TransactionType.WITHDRAW_CASH && it.natureEnum != TransactionNature.REVERSAL
        }.sumOf { it.amount }
        val cashTxs = allTxs.filter {
            it.accountSource == AccountSource.CASH.name && it.natureEnum != TransactionNature.REVERSAL
        }
        if (withdrawnCash == 0.0 && cashTxs.isEmpty()) return null
        val cashIn = cashTxs.filter {
            it.natureEnum == TransactionNature.INCOME || it.natureEnum == TransactionNature.TRANSFER
        }.sumOf { it.amount }
        val cashOut = cashTxs.filter {
            it.natureEnum == TransactionNature.EXPENSE
        }.sumOf { it.amount + it.transactionCost }
        return withdrawnCash + cashIn - cashOut
    }

    private fun singleStreamBalance(txs: List<TransactionEntity>): Double {
        val latest = txs.firstOrNull { it.balance != null } ?: return 0.0
        return when {
            latest.recipientName == "Fuliza M-PESA" && latest.balance != null -> -(latest.balance)
            latest.balance == 0.0 -> {
                val recentFuliza = txs.firstOrNull { it.recipientName == "Fuliza M-PESA" && it.balance != null }
                when {
                    recentFuliza?.balance != null && recentFuliza.balance > 0 -> -(recentFuliza.balance)
                    latest.fulizaAmount != null -> -(latest.fulizaAmount)
                    else -> 0.0
                }
            }
            else -> latest.balance ?: 0.0
        }
    }
}
