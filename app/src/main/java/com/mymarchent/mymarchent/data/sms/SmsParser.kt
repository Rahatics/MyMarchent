package com.mymarchent.mymarchent.data.sms

data class ParsedSms(val trxId: String, val amount: Double)

object SmsParser {

    // bKash: Looks for "Tk" followed by a number, and "TrxID" followed by 10 chars.
    // Example: "...Tk450.00...TrxID 9A8B7C6D5E..."
    private val bKashRegex = "Tk([\\d,]+\\.\\d{2}).*?TrxID ([A-Z0-9]{10})".toRegex(RegexOption.IGNORE_CASE)
    
    // Nagad: Looks for "Amount: Tk" followed by a number, and "Tran. ID:" followed by 8 chars.
    // Example: "...Amount: Tk 500.00...Tran. ID: 1A2B3C4D..."
    private val nagadRegex = "Amount: Tk ([\\d,]+\\.\\d{2}).*?Tran\\. ID: ([A-Z0-9]{8})".toRegex(RegexOption.IGNORE_CASE)

    /**
     * Parses a given SMS message body to find a transaction ID and amount.
     *
     * @param smsBody The full text of the SMS message.
     * @return A [ParsedSms] object if a TrxID and amount are found, otherwise null.
     */
    fun parse(smsBody: String): ParsedSms? {
        val bKashMatch = bKashRegex.find(smsBody)
        if (bKashMatch != null && bKashMatch.groupValues.size > 2) {
            val amountStr = bKashMatch.groupValues[1].replace(",", "")
            val trxId = bKashMatch.groupValues[2]
            return ParsedSms(trxId, amountStr.toDoubleOrNull() ?: 0.0)
        }

        val nagadMatch = nagadRegex.find(smsBody)
        if (nagadMatch != null && nagadMatch.groupValues.size > 2) {
            val amountStr = nagadMatch.groupValues[1].replace(",", "")
            val trxId = nagadMatch.groupValues[2]
            return ParsedSms(trxId, amountStr.toDoubleOrNull() ?: 0.0)
        }

        return null
    }
}