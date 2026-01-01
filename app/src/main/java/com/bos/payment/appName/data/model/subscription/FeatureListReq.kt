package com.bos.payment.appName.data.model.subscription

import com.google.gson.annotations.SerializedName

data class FeatureListReq(

	@field:SerializedName("companyCode")
	val companyCode: String? = null,

	@field:SerializedName("flag")
	val flag: String? = null,

	@field:SerializedName("merchantcode")
	val merchantcode: String? = null,

	@field:SerializedName("companyFeatureCode")
	val companyFeatureCode: String? = null,

	@field:SerializedName("featureName")
	val featureName: Any? = null,

	@field:SerializedName("status")
	val status: String? = null
)
