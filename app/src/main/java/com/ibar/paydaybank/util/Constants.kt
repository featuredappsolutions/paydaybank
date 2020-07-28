package com.ibar.paydaybank.util

import java.text.SimpleDateFormat
import java.util.*

class Constants {

    companion object {
        const val EXTRA_CUSTOMER = "com.ibar.paydaybank.CUSTOMER"

        const val EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        const val PHONE_NUMBER_PATTERN_STRING = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$"

        const val MINIMUM_PASSWORD_LENGTH = 6

        const val ENDPOINT_BASE = "http://192.168.1.10:3000/"
        const val SUFFIX_AUTHENTICATE = "authenticate"
        const val SUFFIX_TRANSACTIONS = "transactions"
        const val SUFFIX_ACCOUNTS = "accounts"
        const val SUFFIX_CUSTOMERS = "customers"

        val ZULU_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val DATETIME_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val YEAR_MONTH_DATE_FORMAT = SimpleDateFormat("yyyy-MM", Locale.getDefault())

        fun isValidEmailAddress(address: String) : Boolean {
            return address.matches(Regex(EMAIL_PATTERN))
        }

        fun isPasswordValid(password: String) : Boolean {
            return password.length >= MINIMUM_PASSWORD_LENGTH
        }
    }
}