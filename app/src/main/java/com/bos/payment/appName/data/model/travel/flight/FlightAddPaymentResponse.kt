package com.bos.payment.appName.data.model.travel.flight

import com.google.gson.annotations.SerializedName

data class FlightAddPaymentResponse(

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("debitedAmount")
	val debitedAmount: String? = null,

	@field:SerializedName("paymentID")
	val paymentID: String? = null,

	@field:SerializedName("response_Header")
	val responseHeader: FlightResponseHeader? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statuss")
	val statuss: String? = null,

	@field:SerializedName("value")
	val value: String? = null
)

data class FlightResponseHeader(

	@field:SerializedName("status_Id")
	val statusId: String? = null,

	@field:SerializedName("error_Code")
	val errorCode: String? = null,

	@field:SerializedName("error_Desc")
	val errorDesc: String? = null,

	@field:SerializedName("error_InnerException")
	val errorInnerException: String? = null,

	@field:SerializedName("request_Id")
	val requestId: String? = null
)
