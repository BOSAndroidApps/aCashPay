package com.bos.payment.appName.data.model.subscription

import com.google.gson.annotations.SerializedName

data class FeatureListResp(

	@field:SerializedName("returnCode")
	val returnCode: String? = null,

	@field:SerializedName("data")
	val data: MutableList<FeatureDataItem?>? = null,

	@field:SerializedName("returnMessage")
	val returnMessage: String? = null,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean? = null
)

data class FeatureDataItem(

	@field:SerializedName("companyCode")
	val companyCode: String? = null,

	@field:SerializedName("serviceType")
	val serviceType: String? = null,

	@field:SerializedName("limitValue")
	val limitValue: Any? = null,

	@field:SerializedName("featureCode")
	val featureCode: Any? = null,

	@field:SerializedName("featureDuration")
	val featureDuration: Any? = null,

	@field:SerializedName("featureName")
	val featureName: String? = null,

	@field:SerializedName("smsServiceType")
	val smsServiceType: Any? = null,

	@field:SerializedName("billingCost")
	val billingCost: String? = null,

	@field:SerializedName("expiryDate")
	val expiryDate: String? = null,

	@field:SerializedName("activeYN")
	val activeYN: String? = null,

	@field:SerializedName("createdDate")
	val createdDate: Any? = null,

	@field:SerializedName("sNo")
	val sNo: Int? = null,

	@field:SerializedName("validityDuration")
	val validityDuration: String? = null,

	@field:SerializedName("enableYN")
	val enableYN: String? = null,

	@field:SerializedName("smsDirection")
	val smsDirection: String? = null,

	@field:SerializedName("cFeatureLinkCode")
	val cFeatureLinkCode: String? = null,

	@field:SerializedName("remarks")
	val remarks: Any? = null,

	@field:SerializedName("status")
	val status: String? = null
)
