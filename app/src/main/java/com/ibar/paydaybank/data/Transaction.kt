package com.ibar.paydaybank.data

import com.ibar.paydaybank.util.Constants
import java.text.SimpleDateFormat
import java.util.*

data class Transaction (val id: Long, val account_id: Long, val amount: String, val vendor: String, val category: String, val date: String)