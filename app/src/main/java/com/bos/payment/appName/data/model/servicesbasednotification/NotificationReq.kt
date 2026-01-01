package com.bos.payment.appName.data.model.servicesbasednotification

import com.google.gson.annotations.SerializedName

data class NotificationReq(

	@field:SerializedName("companyCode")
	val companyCode: String? = null,

	@field:SerializedName("merchantCode")
	val merchantCode: String? = null
)
