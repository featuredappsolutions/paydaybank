package com.ibar.paydaybank.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.ibar.paydaybank.R
import com.ibar.paydaybank.data.Customer
import com.ibar.paydaybank.util.Constants
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setActions()
    }

    private fun setActions() {
        btnSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            if (areAllFieldsValid()) {
                Constants.ENDPOINT_BASE.plus(Constants.SUFFIX_AUTHENTICATE).httpPost(listOf("email" to etEmailAddress.text.toString(), "password" to etPassword.text.toString()))
                    .responseString { request, response, result ->  doParseAuthenticationResult(result)}
            }
        }
    }

    private fun areAllFieldsValid(): Boolean {
        return isEmailAddressValid() && isPasswordFieldValid()
    }

    private fun isEmailAddressValid(): Boolean {
        if (etEmailAddress.text == null || !Constants.isValidEmailAddress(etEmailAddress.text.toString())) {
            etEmailAddress.setError(getString(R.string.youMustEnterValidEmailAddress))
            return false
        }
        return true
    }

    private fun isPasswordFieldValid(): Boolean {
        if (etPassword.text == null || !Constants.isPasswordValid(etPassword.text.toString())) {
            etPassword.setError(getString(R.string.passwordMinLength, Constants.MINIMUM_PASSWORD_LENGTH))
            return false
        }
        return true
    }

    private fun doParseAuthenticationResult(result: Result<String, FuelError>) {
        result.fold(success = {
            runOnUiThread {
                val customer = Gson().fromJson(result.component1(), Customer::class.java)

                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra(Constants.EXTRA_CUSTOMER, customer)
                }
                startActivity(intent)

                this@LoginActivity.finish()
            }
        }, failure = {
            runOnUiThread {
                Toast.makeText(this@LoginActivity, getString(R.string.invalidEmailOrPassword), Toast.LENGTH_LONG).show()
            }
        })
    }

}