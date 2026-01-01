package com.bos.payment.appName.data.model.promocode

import com.google.gson.annotations.SerializedName

data class GetPromotionListReq(

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("applicationType")
	val applicationType: String? = null,

	@field:SerializedName("applicableSubServices")
	val applicableSubServices: String? = null,

	@field:SerializedName("applicationMode")
	val applicationMode: String? = null,

	@field:SerializedName("adminCode")
	val adminCode: String? = null,

	@field:SerializedName("applicableOperators")
	val applicableOperators: String? = null,

	@field:SerializedName("promoCode")
	val promoCode: String? = null,

	@field:SerializedName("retailerCode")
	val retailerCode: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("applicableServices")
	val applicableServices: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
