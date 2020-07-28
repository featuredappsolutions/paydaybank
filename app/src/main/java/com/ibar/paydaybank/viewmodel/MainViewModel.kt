package com.ibar.paydaybank.viewmodel

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.ibar.paydaybank.data.Account
import com.ibar.paydaybank.data.Customer
import com.ibar.paydaybank.data.Transaction
import com.ibar.paydaybank.util.Constants
import java.util.*

class MainViewModel(private val mCustomer: Customer, val delegate: MainViewModelDelegate) {

    lateinit var mTransactionList:List<Transaction>
    lateinit var mAccountsList: List<Account>
    lateinit var mCategories: Array<Any>

    public fun fetchCustomerData() {
        Constants.ENDPOINT_BASE.plus(Constants.SUFFIX_ACCOUNTS).httpGet()
            .responseString { request, response, result ->  doParseAccountsResult(result)}
    }

    public fun isCategoriesInitialized() : Boolean {
        return ::mCategories.isInitialized
    }

    public fun calculateTotalEarningAndSpentAndUpdateUI(categoryPosition: Int, month: Int) {
        if (mCategories.size > 0) {
            val category = mCategories.get(categoryPosition)
            val categoryTransactions = mTransactionList.filter { it.category == category }

            var totalEarnings = 0.0
            var totalSpent = 0.0

            val relatedMonthString = getRelatedMonthAgoString(month)

            categoryTransactions.forEach {
                if (getMonthString(Constants.ZULU_DATE_FORMAT.parse(it.date)) == relatedMonthString) {
                    if (it.amount.toDouble() > 0) {
                        totalEarnings += it.amount.toDouble()
                    } else {
                        totalSpent += it.amount.toDouble()
                    }
                }
            }

            delegate.onTotalEarnedAndSpentCalculated(
                totalEarnings.toBigDecimal().toPlainString().plus(" $"),
                totalSpent.toBigDecimal().toPlainString().plus(" $")
            )
        }
    }

    private fun getRelatedMonthAgoString(month: Int) : String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -month)

        return getMonthString(cal.time)
    }

    private fun getMonthString(date: Date) : String {
        return Constants.YEAR_MONTH_DATE_FORMAT.format(date)
    }

    private fun doParseAccountsResult(result: Result<String, FuelError>) {
        result.fold(success = {
            val accountsList = Gson().fromJson(result.component1(), Array<Account>::class.java)

            mAccountsList = accountsList.filter { it.customer_id == mCustomer.id }

            fetchTransactions()
        }, failure = {
            delegate.onQueryAccountsFailed()
        })
    }

    private fun fetchTransactions() {
        Constants.ENDPOINT_BASE.plus(Constants.SUFFIX_TRANSACTIONS).httpGet()
            .responseString { request, response, result ->  doParseTransactionsResult(result)}
    }

    private fun doParseTransactionsResult(result: Result<String, FuelError>) {
        result.fold(success = {
            val transactionList = Gson().fromJson(result.component1(), Array<Transaction>::class.java)

            mTransactionList = transactionList.filter { transaction -> mAccountsList.any { it.id == transaction.account_id} }.sortedByDescending { it.date }

            val categories = HashSet<String>()

            mTransactionList.forEach {
                categories.add(it.category)
            }

            mCategories = categories.toArray()

            delegate.onQueryTransactionsSucceded(mTransactionList, mCategories)
        }, failure = {
            delegate.onQueryTransactionsFailed()
        })
    }

}

interface MainViewModelDelegate {
    fun onQueryAccountsFailed()
    fun onQueryTransactionsSucceded(mTransactionList: List<Transaction>, mCategories: Array<Any>)
    fun onQueryTransactionsFailed()
    fun onTotalEarnedAndSpentCalculated(totalEarneds: String, totalSpent: String)
}