package com.mpesa.tracker.domain.models

enum class ReportPeriod(val displayName: String) {
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    LAST_MONTH("Last Month"),
    ALL_TIME("All Time"),
    CUSTOM("Custom")
}
