package com.ibar.paydaybank.data

data class Account (val id: Long, val customer_id: Long, val iban: String, val type: String, val date_created: String, val active: Boolean)