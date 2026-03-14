package com.mpesa.tracker.domain.parser;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\t\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J \u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0006H\u0002J\u0012\u0010\u0012\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0013\u001a\u00020\u0006H\u0002J\u0010\u0010\u0014\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0015\u001a\u00020\u0006J\u0012\u0010\u0016\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0013\u001a\u00020\u0006H\u0002J\u0012\u0010\u0017\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0013\u001a\u00020\u0006H\u0002J\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0006H\u0002J\u0012\u0010\u001b\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0013\u001a\u00020\u0006H\u0002J\u0012\u0010\u001c\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0013\u001a\u00020\u0006H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"Lcom/mpesa/tracker/domain/parser/MpesaParser;", "", "()V", "BUY_AIRTIME_REGEX", "Lkotlin/text/Regex;", "GENERIC_PATTERN", "", "PAYBILL_REGEX", "RECEIVED_MONEY_REGEX", "SEND_MONEY_REGEX", "WITHDRAW_CASH_REGEX", "buildResult", "Lcom/mpesa/tracker/domain/parser/ParsedMpesaResult;", "match", "Lkotlin/text/MatchResult;", "type", "Lcom/mpesa/tracker/data/local/entities/TransactionType;", "raw", "parseAirtime", "sms", "parseMessage", "smsBody", "parsePaybill", "parseReceivedMoney", "parseRobustDate", "", "dateString", "parseSendMoney", "parseWithdrawCash", "app_release"})
public final class MpesaParser {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String GENERIC_PATTERN = "^(?<receipt>[A-Z0-9]+)\\s+Confirmed\\.\\s+(?:Ksh|Kshs)\\s?(?<amount>[\\d,]+\\.\\d{2})\\s+(?<type>paid to|sent to|bought|withdrew from|received from)\\s+(?<recipient>.*?)(?:\\s+(?:on|from)\\s+|\\s+)(?<date>\\d{1,2}/\\d{1,2}/\\d{2}\\s+(?:at\\s+)?\\d{1,2}:\\d{2}\\s+(?:AM|PM))..*?(?:New\\s+M-PESA\\s+balance\\s+is\\s+(?:Ksh|Kshs)\\s?(?<balance>[\\d,]+\\.\\d{2}))..*?(?:Transaction\\s+cost,\\s+(?:Ksh|Kshs)\\s?(?<cost>[\\d,]+\\.\\d{2}))?";
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex PAYBILL_REGEX = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex SEND_MONEY_REGEX = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex BUY_AIRTIME_REGEX = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex RECEIVED_MONEY_REGEX = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex WITHDRAW_CASH_REGEX = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.mpesa.tracker.domain.parser.MpesaParser INSTANCE = null;
    
    private MpesaParser() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.mpesa.tracker.domain.parser.ParsedMpesaResult parseMessage(@org.jetbrains.annotations.NotNull()
    java.lang.String smsBody) {
        return null;
    }
    
    private final com.mpesa.tracker.domain.parser.ParsedMpesaResult parsePaybill(java.lang.String sms) {
        return null;
    }
    
    private final com.mpesa.tracker.domain.parser.ParsedMpesaResult parseSendMoney(java.lang.String sms) {
        return null;
    }
    
    private final com.mpesa.tracker.domain.parser.ParsedMpesaResult parseAirtime(java.lang.String sms) {
        return null;
    }
    
    private final com.mpesa.tracker.domain.parser.ParsedMpesaResult parseReceivedMoney(java.lang.String sms) {
        return null;
    }
    
    private final com.mpesa.tracker.domain.parser.ParsedMpesaResult parseWithdrawCash(java.lang.String sms) {
        return null;
    }
    
    private final com.mpesa.tracker.domain.parser.ParsedMpesaResult buildResult(kotlin.text.MatchResult match, com.mpesa.tracker.data.local.entities.TransactionType type, java.lang.String raw) {
        return null;
    }
    
    private final long parseRobustDate(java.lang.String dateString) {
        return 0L;
    }
}