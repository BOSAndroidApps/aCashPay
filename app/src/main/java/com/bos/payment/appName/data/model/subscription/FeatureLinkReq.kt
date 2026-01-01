package com.bos.payment.appName.data.model.subscription

import com.google.gson.annotations.SerializedName

data class FeatureLinkReq(

	@field:SerializedName("companyCode")
	val companyCode: String? = null,

	@field:SerializedName("limitValue")
	val limitValue: Int? = null,

	@field:SerializedName("flag")
	val flag: String? = null,

	@field:SerializedName("featureCode")
	val featureCode: String? = null,

	@field:SerializedName("featureDuration")
	val featureDuration: String? = null,

	@field:SerializedName("loginId")
	val loginId: String? = null,

	@field:SerializedName("companyFeatureCode")
	val companyFeatureCode: String? = null,

	@field:SerializedName("programCode")
	val programCode: String? = null,

	@field:SerializedName("smsServiceType")
	val smsServiceType: String? = null,

	@field:SerializedName("smsDirection")
	val smsDirection: String? = null,

	@field:SerializedName("remarks")
	val remarks: String? = null
)
