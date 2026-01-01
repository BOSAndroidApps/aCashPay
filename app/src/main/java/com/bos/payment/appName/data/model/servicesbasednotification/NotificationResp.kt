package com.bos.payment.appName.data.model.servicesbasednotification

import com.google.gson.annotations.SerializedName

data class NotificationResp(

	@field:SerializedName("returnCode")
	val returnCode: String? = null,

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("returnMessage")
	val returnMessage: String? = null,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean? = null
)

data class DataItem(

	@field:SerializedName("expiryDate")
	val expiryDate: String? = null,

	@field:SerializedName("activeYN")
	val activeYN: String? = null,

	@field:SerializedName("daysRemaining")
	val daysRemaining: Int? = null,

	@field:SerializedName("expiryStatus")
	val expiryStatus: String? = null,

	@field:SerializedName("featureCode")
	val featureCode: String? = null,

	@field:SerializedName("featureDuration")
	val featureDuration: String? = null,

	@field:SerializedName("featureName")
	val featureName: String? = null,

	@field:SerializedName("cflId")
	val cflId: Int? = null,

	@field:SerializedName("notificationMessage")
	val notificationMessage: String? = null,

	@field:SerializedName("planName")
	val planName: String? = null,

	@field:SerializedName("retailerCode")
	val retailerCode: String? = null
)
