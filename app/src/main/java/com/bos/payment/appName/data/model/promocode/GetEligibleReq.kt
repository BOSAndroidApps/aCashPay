package com.bos.payment.appName.data.model.promocode

import com.google.gson.annotations.SerializedName

data class GetEligibleReq(

	@field:SerializedName("fromDate")
	val fromDate: String? = null,

	@field:SerializedName("toDate")
	val toDate: String? = null,

	@field:SerializedName("serviceCode")
	val serviceCode: String? = null,

	@field:SerializedName("retailerId")
	val retailerId: String? = null,

	@field:SerializedName("operatorCode")
	val operatorCode: String? = null,

	@field:SerializedName("subserviceCode")
	val subserviceCode: String? = null
)
