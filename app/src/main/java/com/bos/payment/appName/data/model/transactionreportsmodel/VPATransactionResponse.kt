package com.bos.payment.appName.data.model.transactionreportsmodel

import com.google.gson.annotations.SerializedName

data class VPATransactionResponse(

	@field:SerializedName("returnCode")
	val returnCode: String? = null,

	@field:SerializedName("data")
	val data: List<VPADataItem?>? = null,

	@field:SerializedName("returnMessage")
	val returnMessage: String? = null,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean? = null
)

data class VPADataItem(

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("yppReferenceNumber")
	val yppReferenceNumber: Any? = null,

	@field:SerializedName("transactiondate")
	val transactiondate: String? = null,

	@field:SerializedName("payeevpa")
	val payeevpa: String? = null,

	@field:SerializedName("registrationID")
	val registrationID: String? = null,

	@field:SerializedName("payerName")
	val payerName: String? = null,

	@field:SerializedName("txnReferance")
	val txnReferance: String? = null,

	@field:SerializedName("rid")
	val rid: String? = null,

	@field:SerializedName("vpaEvent")
	val vpaEvent: String? = null,

	@field:SerializedName("payervpa")
	val payervpa: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("gatewayResponseMessage")
	val gatewayResponseMessage: String? = null
)
