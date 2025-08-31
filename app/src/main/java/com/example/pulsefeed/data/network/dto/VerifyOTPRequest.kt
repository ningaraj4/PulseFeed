package com.example.pulsefeed.data.network.dto

import com.google.gson.annotations.SerializedName

data class VerifyOTPRequest(
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("otp")
    val otp: String
)
