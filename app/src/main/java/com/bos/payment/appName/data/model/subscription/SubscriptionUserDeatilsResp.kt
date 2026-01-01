package com.bos.payment.appName.data.model.subscription

import com.google.gson.annotations.SerializedName

data class SubscriptionUserDeatilsResp(

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

	@field:SerializedName("pincode")
	val pincode: Any? = null,

	@field:SerializedName("merchantCode")
	val merchantCode: String? = null,

	@field:SerializedName("userFullName")
	val userFullName: String? = null,

	@field:SerializedName("kycUpdate")
	val kycUpdate: String? = null,

	@field:SerializedName("emailID")
	val emailID: String? = null,

	@field:SerializedName("mobileNo")
	val mobileNo: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("agencyName")
	val agencyName: String? = null,

	@field:SerializedName("expiryDate")
	val expiryDate: Any? = null,

	@field:SerializedName("companyNameText")
	val companyNameText: String? = null,

	@field:SerializedName("createdDate")
	val createdDate: Any? = null,

	@field:SerializedName("activeStatus")
	val activeStatus: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("companyTypeCode")
	val companyTypeCode: String? = null,

	@field:SerializedName("companyTypeName")
	val companyTypeName: String? = null
)
