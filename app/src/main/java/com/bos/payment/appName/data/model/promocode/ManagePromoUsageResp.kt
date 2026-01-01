package com.bos.payment.appName.data.model.promocode

import com.google.gson.annotations.SerializedName

data class ManagePromoUsageResp(

	@field:SerializedName("summary")
	val summary: Any? = null,

	@field:SerializedName("returnCode")
	val returnCode: String? = null,

	@field:SerializedName("returnMessage")
	val returnMessage: String? = null,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean? = null,

	@field:SerializedName("usageList")
	val usageList: List<Any?>? = null
)
