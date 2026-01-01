package com.bos.payment.appName.data.model.promocode

import com.google.gson.annotations.SerializedName

data class ManagePromoUsageReq(

	@field:SerializedName("serviceType")
	val serviceType: String? = null,

	@field:SerializedName("fromDate")
	val fromDate: Any? = null,

	@field:SerializedName("taskType")
	val taskType: String? = null,

	@field:SerializedName("transactionAmount")
	val transactionAmount: Double? = null,

	@field:SerializedName("toDate")
	val toDate: Any? = null,

	@field:SerializedName("promoCode")
	val promoCode: String? = null,

	@field:SerializedName("retailerCode")
	val retailerCode: String? = null,

	@field:SerializedName("discountApplied")
	val discountApplied: Double? = null,

	@field:SerializedName("transactionID")
	val transactionID: String? = null,

	@field:SerializedName("remarks")
	val remarks: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
