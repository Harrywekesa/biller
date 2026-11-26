package com.mpesa.tracker.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.Chat
import com.mpesa.tracker.data.repository.TransactionRepository
import com.mpesa.tracker.domain.intelligence.FinancialIntelligenceEngine
import com.mpesa.tracker.domain.models.ChatMessage
import com.mpesa.tracker.domain.models.ChatRole
import com.mpesa.tracker.domain.models.ReportPeriod
import com.mpesa.tracker.domain.parser.AccountSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val financialEngine: FinancialIntelligenceEngine
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSessionReady = MutableStateFlow(false)
    val isSessionReady: StateFlow<Boolean> = _isSessionReady.asStateFlow()

    private var chatSession: Chat? = null

    /**
     * Initializes the chat session with live financial context (M-Pesa + connected banks).
     */
    fun initializeSession(currentBalance: String) {
        if (chatSession != null) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val spent = repository.getTotalSpentForPeriod(ReportPeriod.THIS_MONTH)
                    .firstOrNull() ?: 0.0
                val income = repository.getTotalIncomeForPeriod(ReportPeriod.THIS_MONTH)
                    .firstOrNull() ?: 0.0
                val topCategories = repository.getExpensesByCategory(ReportPeriod.THIS_MONTH)
                    .firstOrNull() ?: emptyList()

                // Compute live M-Pesa balance from DB when caller passes empty string
                val resolvedBalance = if (currentBalance.isNotBlank()) {
                    currentBalance
                } else {
                    val txs = repository.getAllTransactions().firstOrNull() ?: emptyList()
                    val mpesaLatest = txs.firstOrNull {
                        it.accountSource == AccountSource.MPESA.name && it.balance != null && it.recipientName != "Fuliza M-PESA"
                    }
                    val fmt = NumberFormat.getNumberInstance(Locale.US).apply {
                        minimumFractionDigits = 2; maximumFractionDigits = 2
                    }
                    if (mpesaLatest?.balance != null) "Ksh ${fmt.format(mpesaLatest.balance)}" else "Unknown"
                }

                val systemPrompt = financialEngine.buildChatSystemPrompt(
                    totalSpentThisMonth = spent,
                    totalIncomeThisMonth = income,
                    topCategories = topCategories,
                    currentBalance = resolvedBalance
                )

                chatSession = financialEngine.startChatSession(systemPrompt)
                _isSessionReady.value = true

                addModelMessage("Hi! I'm your financial assistant. I can see your spending across M-Pesa and linked bank SMS. Ask me things like \"What did I spend the most on?\" or \"Am I within budget this month?\"")
            } catch (e: Exception) {
                addModelMessage("Sorry, I couldn't connect to the AI service. Please check your internet connection.", isError = true)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendMessage(userText: String) {
        val session = chatSession ?: return
        if (userText.isBlank()) return

        val userMsg = ChatMessage(role = ChatRole.USER, content = userText)
        _messages.value = _messages.value + userMsg

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val reply = financialEngine.sendChatMessage(session, userText)
                if (reply != null) {
                    addModelMessage(reply)
                } else {
                    addModelMessage("I couldn't generate a response. Please try again.", isError = true)
                }
            } catch (e: Exception) {
                addModelMessage("Something went wrong. Please try again.", isError = true)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSession() {
        chatSession = null
        _messages.value = emptyList()
        _isSessionReady.value = false
    }

    private fun addModelMessage(text: String, isError: Boolean = false) {
        val msg = ChatMessage(role = ChatRole.MODEL, content = text, isError = isError)
        _messages.value = _messages.value + msg
    }
}
