package com.mymarchent.mymarchent.data.sms

import android.content.Context
import android.provider.Telephony
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A class responsible for reading SMS messages from the device.
 */
class SmsReader(private val context: Context) {

    /**
     * Reads all SMS messages from the last 24 hours.
     * This should be called from a background thread.
     *
     * @return A list of SMS message bodies.
     */
    suspend fun readSmsFromLast24Hours(): List<String> = withContext(Dispatchers.IO) {
        val smsList = mutableListOf<String>()
        val cursor = context.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            arrayOf(Telephony.Sms.Inbox.BODY, Telephony.Sms.Inbox.DATE),
            "${Telephony.Sms.Inbox.DATE} > ?",
            arrayOf((System.currentTimeMillis() - 24 * 60 * 60 * 1000).toString()),
            Telephony.Sms.Inbox.DEFAULT_SORT_ORDER
        )

        cursor?.use { c ->
            val bodyIndex = c.getColumnIndexOrThrow(Telephony.Sms.Inbox.BODY)
            while (c.moveToNext()) {
                smsList.add(c.getString(bodyIndex))
            }
        }

        return@withContext smsList
    }
}