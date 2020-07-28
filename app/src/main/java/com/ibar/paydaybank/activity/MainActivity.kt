package com.ibar.paydaybank.activity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibar.paydaybank.R
import com.ibar.paydaybank.adapter.TransactionsAdapter
import com.ibar.paydaybank.data.Customer
import com.ibar.paydaybank.data.Transaction
import com.ibar.paydaybank.util.Constants
import com.ibar.paydaybank.viewmodel.MainViewModel
import com.ibar.paydaybank.viewmodel.MainViewModelDelegate
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainViewModelDelegate {

    private lateinit var mLinearLayoutManager: LinearLayoutManager

    private lateinit var mCustomer: Customer
    private lateinit var mMainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mCustomer = intent.extras?.getSerializable(Constants.EXTRA_CUSTOMER) as Customer
        mMainViewModel = MainViewModel(mCustomer, this)
        mMainViewModel.fetchCustomerData()

        tvCustomerName.text = mCustomer.firstName.plus(" ").plus(mCustomer.lastName)

        initializeLatestMonthsSpinner()
        initializeRecyclerView()

        setCategorySpinnerAction()
        setMonthSpinnerAction()
    }

    private fun initializeRecyclerView() {
        mLinearLayoutManager = LinearLayoutManager(this)
        rvLatestTransactions.layoutManager = mLinearLayoutManager

    }

    private fun setMonthSpinnerAction() {
        spinnerMainMonths.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (mMainViewModel.isCategoriesInitialized()) {
                    mMainViewModel.calculateTotalEarningAndSpentAndUpdateUI(
                        spinnerMainCategories.selectedItemPosition,
                        position
                    )
                }
            }
        }
    }

    private fun setCategorySpinnerAction() {
        spinnerMainCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (mMainViewModel.isCategoriesInitialized()) {
                    mMainViewModel.calculateTotalEarningAndSpentAndUpdateUI(
                        position,
                        spinnerMainMonths.selectedItemPosition
                    )
                }
            }
        }
    }

    private fun initializeLatestMonthsSpinner() {
        val months = listOf(getString(R.string.thisMonth), getString(R.string.previousMonth), getString(R.string.monthsAgo, 2), getString(R.string.monthsAgo, 3))

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMainMonths.adapter = adapter
    }

    override fun onQueryAccountsFailed() {
        runOnUiThread {
            Toast.makeText(this@MainActivity, getString(R.string.errorOccurredOnGettingTransactions), Toast.LENGTH_LONG).show()
        }
    }

    override fun onQueryTransactionsSucceded(mTransactionList: List<Transaction>, mCategories: Array<Any>) {
        runOnUiThread {
            rvLatestTransactions.adapter = TransactionsAdapter(mTransactionList)

            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mCategories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerMainCategories.adapter = adapter
        }

        mMainViewModel.calculateTotalEarningAndSpentAndUpdateUI(0, 0)
    }

    override fun onQueryTransactionsFailed() {
        runOnUiThread {
            Toast.makeText(this@MainActivity, getString(R.string.errorOccurredOnGettingTransactions), Toast.LENGTH_LONG).show()
        }
    }

    override fun onTotalEarnedAndSpentCalculated(totalEarneds: String, totalSpent: String) {
        tvMainTotalEarned.text = totalEarneds
        tvMainTotalSpent.text = totalSpent
    }
}
