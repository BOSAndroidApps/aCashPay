package com.bos.payment.appName.data.model.recharge

import com.google.gson.annotations.SerializedName


data class BillOperationPaymentRes(
    @SerializedName("response_code" ) var responseCode: Int?            = null,
    @SerializedName("status"        ) var status: Boolean?              = null,
    @SerializedName("data"          ) var data: ArrayList<Data> = arrayListOf(),
    @SerializedName("message"       ) var message: String?         = null

)