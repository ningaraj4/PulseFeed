package com.example.pulsefeed.data.network.dto

import com.google.gson.annotations.SerializedName

data class SendOTPRequest(
    @SerializedName("phone_number")
    val phoneNumber: String
)
