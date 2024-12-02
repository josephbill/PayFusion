package com.ezra.payfusion

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Base64
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MpesaViewModel : ViewModel() {

    private val api: MpesaApi

    private val consumerKey = "jkXBgjW27IKhblgEVsR5SOfTh6Am0MZ3IWs1xTPIyKgLA70x"
    private val consumerSecret = "z6WM04Fv88SyG8QeSgtva1GXdNyWPpbOi6vTNuEbuA6aL64l6sqAzxqpePAEXYvG"

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sandbox.safaricom.co.ke/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(MpesaApi::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateAccessToken(callback: (String?) -> Unit) {
        val credentials = "$consumerKey:$consumerSecret"
        val encodedCredentials = Base64.getEncoder().encodeToString(credentials.toByteArray())
        val authHeader = "Basic $encodedCredentials"

        viewModelScope.launch {
            try {
                val response = api.generateAccessToken(auth = authHeader)
                callback(response.access_token)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendStkPush(
        phoneNumber: String,
        amount: String,
        accessToken: String,
        callback: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
                val shortcode = "174379"
                val passkey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"
                val rawPassword = "$shortcode$passkey$timestamp"
                val password = Base64.getEncoder().encodeToString(rawPassword.toByteArray())

                val request = StkPushRequest(
                    BusinessShortCode = shortcode,
                    Password = password,
                    Timestamp = timestamp,
                    TransactionType = "CustomerPayBillOnline",
                    Amount = amount,
                    PartyA = phoneNumber,
                    PartyB = shortcode,
                    PhoneNumber = phoneNumber,
                    CallBackURL = "https://mpesa-requestbin.herokuapp.com/1hhy6391",
                    AccountReference = "Apen Softwares",
                    TransactionDesc = "Payment for services"
                )

                val response = api.sendStkPush("Bearer $accessToken", request)
                callback(response.ResponseDescription ?: "No response description received")
            } catch (e: Exception) {
                e.printStackTrace()
                callback("Error: ${e.message}")
            }
        }
    }
}
