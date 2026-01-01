package com.bos.payment.appName.data.model.subscription

import com.google.gson.annotations.SerializedName

data class BillingCostReq(

	@field:SerializedName("companyCode")
	val companyCode: String? = null,

	@field:SerializedName("companyFeatureCode")
	val companyFeatureCode: String? = null,

	@field:SerializedName("paramFlag")
	val paramFlag: String? = null
)
