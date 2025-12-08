package com.bos.payment.appName.data.model.managekyc

import com.google.gson.annotations.SerializedName

data class CountryStateDistrictResp(

	@field:SerializedName("returnCode")
	val returnCode: String? = null,

	@field:SerializedName("data")
	val data: List<StateDistDataItem?>? = null,

	@field:SerializedName("returnMessage")
	val returnMessage: String? = null,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean? = null
)


data class StateDistDataItem(

	@field:SerializedName("displayValue")
	val displayValue: String? = null,

	@field:SerializedName("displayText")
	val displayText: String? = null
)
