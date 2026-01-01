package com.bos.payment.appName.data.model.promocode

import com.google.gson.annotations.SerializedName

data class GetEligibleResp(

	@field:SerializedName("totalTransactionAmount")
	val totalTransactionAmount: Double? = null,

	@field:SerializedName("returnMessage")
	val returnMessage: String? = null,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean? = null
)
