package com.bos.payment.appName.data.model.subscription

import com.google.gson.annotations.SerializedName

data class BillingCostResp(

	@field:SerializedName("returnCode")
	val returnCode: String? = null,

	@field:SerializedName("data")
	val data: List<BillingDataItem?>? = null,

	@field:SerializedName("returnMessage")
	val returnMessage: String? = null,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean? = null
)

data class BillingDataItem(

	@field:SerializedName("billingCost")
	val billingCost: String? = null
)
