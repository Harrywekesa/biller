package com.mpesa.tracker.domain.parser

/**
 * Identifies which financial institution or wallet a transaction originated from.
 * Stored as a TEXT column in Room so it is human-readable and migration-safe.
 */
enum class AccountSource(val displayName: String, val shortLabel: String) {
    MPESA("M-Pesa", "M-Pesa"),
    EQUITY("Equity Bank", "Equity"),
    IM_BANK("I&M Bank", "I&M"),
    ABSA("Absa Bank", "Absa"),
    KCB("KCB Bank", "KCB"),
    COOP("Co-op Bank", "Co-op"),
    NCBA("NCBA", "NCBA"),
    STANBIC("Stanbic Bank", "Stanbic"),
    DTB("DTB Bank", "DTB"),
    FAMILY_BANK("Family Bank", "Family"),
    CASH("Cash Wallet", "Cash"),
    UNKNOWN_BANK("Bank", "Bank");

    companion object {
        fun fromName(name: String?): AccountSource =
            entries.firstOrNull { it.name == name } ?: MPESA
    }
}
