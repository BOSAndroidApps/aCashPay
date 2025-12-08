package com.example.theemiclub.data.model.loginsignup.verification

import com.google.gson.annotations.SerializedName

data class AAdhaarDetailesReq(
    @SerializedName("TransactionID")
    var transactionID:String,

    @SerializedName("RegistrationID")
    var registrationID:String
)
