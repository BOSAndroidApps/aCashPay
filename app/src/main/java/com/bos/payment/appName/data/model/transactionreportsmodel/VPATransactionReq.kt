package com.bos.payment.appName.data.model.transactionreportsmodel

import com.google.gson.annotations.SerializedName

data class VPATransactionReq(

	@field:SerializedName("endDate")
	val endDate: String? = null,

	@field:SerializedName("registrationID")
	val registrationID: String? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null
)
