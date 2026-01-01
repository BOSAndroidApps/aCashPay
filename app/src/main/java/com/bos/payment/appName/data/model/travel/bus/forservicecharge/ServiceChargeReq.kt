package com.bos.payment.appName.data.model.travel.bus.forservicecharge

import com.google.gson.annotations.SerializedName

data class ServiceChargeReq(

	@field:SerializedName("taskType")
	val taskType: String? = null,

	@field:SerializedName("adminCode")
	val adminCode: String? = null
)
