package com.joseph.payfusion

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query


data class AccessTokenResponse(
    val access_token: String
)


data class StkPushRequest(
    val BusinessShortCode: String,
    val Password: String,
    val Timestamp: String,
    val TransactionType: String = "CustomerPayBillOnline",
    val Amount: String,
    val PartyA: String,
    val PartyB: String,
    val PhoneNumber: String,
    val CallBackURL: String,
    val AccountReference: String,
    val TransactionDesc: String
)


data class StkPushResponse(
    val CheckoutRequestID: String?,
    val ResponseDescription: String?
)


interface MpesaApi {


    @GET("oauth/v1/generate")
    suspend fun generateAccessToken(
        @Query("grant_type") grantType: String = "client_credentials",
        @Header("Authorization") auth: String
    ): AccessTokenResponse


    @POST("mpesa/stkpush/v1/processrequest")
    suspend fun sendStkPush(
        @Header("Authorization") token: String,
        @Body request: StkPushRequest
    ): StkPushResponse
}
