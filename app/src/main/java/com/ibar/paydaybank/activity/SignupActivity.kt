package com.ibar.paydaybank.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.ibar.paydaybank.R
import com.ibar.paydaybank.data.Customer
import com.ibar.paydaybank.util.Constants
import kotlinx.android.synthetic.main.activity_signup.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class SignupActivity : AppCompatActivity() {

    lateinit var genderList : List<String>
    lateinit var phoneNumberPattern : Pattern

    lateinit var mBirthdayInZuluTime:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        initializeGenderSpinnerAndActions()
        initializeBirthdateDialog()
        initializeSignupAction()

        phoneNumberPattern = Pattern.compile(Constants.PHONE_NUMBER_PATTERN_STRING)
    }

    private fun initializeSignupAction() {
        btnSignUpCreateAccount.setOnClickListener {
            if (areAllFieldsValid()) {
                Constants.ENDPOINT_BASE.plus(Constants.SUFFIX_CUSTOMERS).httpPost(listOf("First Name" to etSignupFirstname.text.toString(),
                    "Last Name" to etSignupLastname.text.toString(),
                    "gender" to genderList.get(spinnerSignupGender.selectedItemPosition),
                    "email" to etSignupEmailAddress.text.toString(),
                    "password" to etSignupPassword.text.toString(),
                    "phone" to etSignupPhoneNumber.text.toString(),
                    "dob" to mBirthdayInZuluTime
                ))
                    .responseString { request, response, result ->  doParseSignUpResult(result)}
            }
        }
    }

    private fun doParseSignUpResult(result: Result<String, FuelError>) {
        result.fold(success = {
            runOnUiThread {
                val customer = Gson().fromJson(result.component1(), Customer::class.java)

                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra(Constants.EXTRA_CUSTOMER, customer)
                }
                startActivity(intent)

                this@SignupActivity.finish()
            }
        }, failure = {
            runOnUiThread {
                Toast.makeText(this@SignupActivity, getString(R.string.errorOccurredOnAccountCreate), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun areAllFieldsValid(): Boolean {
        return isFirstNameValid() && isLastNameValid() && isPhoneNumberValid() && isEmailAddressValid() && isPasswordFieldValid() && isBirtdateFieldValid()
    }

    private fun isBirtdateFieldValid(): Boolean {
        if (tvSelectedBirthdate.text.length == 0) {
            Toast.makeText(this, getString(R.string.youMustSelectBirthDate), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun isPasswordFieldValid(): Boolean {
        if (etSignupPassword.text == null || !Constants.isPasswordValid(etSignupEmailAddress.text.toString())) {
            etSignupPassword.setError(getString(R.string.passwordMinLength, Constants.MINIMUM_PASSWORD_LENGTH))
            return false
        }
        return true
    }

    private fun isEmailAddressValid(): Boolean {
        if (etSignupEmailAddress.text == null || !Constants.isValidEmailAddress(etSignupEmailAddress.text.toString())) {
            etSignupEmailAddress.setError(getString(R.string.youMustEnterValidEmailAddress))
            return false
        }
        return true
    }

    private fun isPhoneNumberValid(): Boolean {
        if (etSignupPhoneNumber.text == null || !phoneNumberPattern.matcher(etSignupPhoneNumber.text.toString()).find()) {
            etSignupPhoneNumber.setError(getString(R.string.youMustEnterYourPhoneNumber))
            return false
        }


        return true
    }

    private fun isLastNameValid(): Boolean {
        if (etSignupLastname.text == null || etSignupLastname.text.toString().length == 0) {
            etSignupLastname.setError(getString(R.string.youMustEnterYourLastName))
            return false
        }
        return true
    }

    private fun isFirstNameValid(): Boolean {
        if (etSignupFirstname.text == null || etSignupFirstname.text.toString().length == 0) {
            etSignupFirstname.setError(getString(R.string.youMustEnterYourFirstName))
            return false
        }
        return true
    }

    private fun initializeBirthdateDialog() {
        val cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.US)
            tvSelectedBirthdate.text = sdf.format(cal.time)

            val zuluDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            mBirthdayInZuluTime = zuluDateFormat.format(cal.time)
        }

        tvPickBirthdate.setOnClickListener {
            DatePickerDialog(this@SignupActivity, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

    }

    private fun initializeGenderSpinnerAndActions() {
        genderList = listOf(getString(R.string.male), getString(R.string.female))

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSignupGender.adapter = adapter
    }

}