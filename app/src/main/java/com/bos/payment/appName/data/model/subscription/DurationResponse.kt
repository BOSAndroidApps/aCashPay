package com.bos.payment.appName.data.model.subscription

import com.google.gson.annotations.SerializedName

data class DurationResponse(
	@field:SerializedName("returnCode")
	val returnCode: String? = null,

	@field:SerializedName("data")
	val data: List<DurationDataItem?>? = null,

	@field:SerializedName("returnMessage")
	val returnMessage: String? = null,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean? = null

)


data class DurationDataItem(
	@field:SerializedName("displayValue")
	val displayValue: String? = null,

	@field:SerializedName("displayText")
	val displayText: String? = null

)
