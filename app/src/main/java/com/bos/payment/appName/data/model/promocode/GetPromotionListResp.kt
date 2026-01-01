package com.bos.payment.appName.data.model.promocode

import com.google.gson.annotations.SerializedName


data class GetPromotionListResp(

	@field:SerializedName("returnCode")
	val returnCode: String? = null,

	@field:SerializedName("data")
	val data: List<PromoDataItem?>? = null,

	@field:SerializedName("returnMessage")
	val returnMessage: String? = null,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean? = null
)

data class PromoDataItem(

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("applicationType")
	val applicationType: String? = null,

	@field:SerializedName("agentType")
	val agentType: String? = null,

	@field:SerializedName("endDate")
	val endDate: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("updatedDate")
	val updatedDate: String? = null,

	@field:SerializedName("usageLimitPerUser")
	val usageLimitPerUser: Int? = null,

	@field:SerializedName("applicableSubServices")
	val applicableSubServices: List<String?>? = null,

	@field:SerializedName("maxDiscountAmount")
	val maxDiscountAmount: Double? = null,

	@field:SerializedName("applicableOperators")
	val applicableOperators: List<String?>? = null,

	@field:SerializedName("adminCode")
	val adminCode: String? = null,

	@field:SerializedName("promoCode")
	val promoCode: String? = null,

	@field:SerializedName("discountType")
	val discountType: String? = null,

	@field:SerializedName("minTransactionAmount")
	val minTransactionAmount: Double? = null,

	@field:SerializedName("state")
	val state: List<String?>? = null,

	@field:SerializedName("applicableServices")
	val applicableServices: List<String?>? = null,

	@field:SerializedName("promotionType")
	val promotionType: String? = null,

	@field:SerializedName("updatedBy")
	val updatedBy: String? = null,

	@field:SerializedName("totalUsageLimit")
	val totalUsageLimit: Int? = null,

	@field:SerializedName("promoName")
	val promoName: String? = null,

	@field:SerializedName("createdDate")
	val createdDate: String? = null,

	@field:SerializedName("createdBy")
	val createdBy: String? = null,

	@field:SerializedName("applicationMode")
	val applicationMode: String? = null,

	@field:SerializedName("retailerCode")
	val retailerCode: String? = null,

	@field:SerializedName("promoID")
	val promoID: Int? = null,

	@field:SerializedName("userType")
	val userType: String? = null,

	@field:SerializedName("discountValue")
	val discountValue: Double? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
