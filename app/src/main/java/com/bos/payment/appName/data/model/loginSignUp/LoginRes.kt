package com.bos.payment.appName.data.model.loginSignUp

import com.google.gson.annotations.SerializedName

data class LoginRes(

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

	@field:SerializedName("lastName")
	val lastName: Any? = null,

	@field:SerializedName("applicationType")
	val applicationType: String? = null,

	@field:SerializedName("merchantCode")
	val merchantCode: String? = null,

	@field:SerializedName("agentType")
	val agentType: String? = null,

	@field:SerializedName("superAdminCode")
	val superAdminCode: Any? = null,

	@field:SerializedName("fullName")
	val fullName: String? = null,

	@field:SerializedName("emailId")
	val emailId: String? = null,

	@field:SerializedName("mobileNo")
	val mobileNo: String? = null,

	@field:SerializedName("userID")
	val userID: String? = null,

	@field:SerializedName("adminBaseUrl")
	val adminBaseUrl: String? = null,

	@field:SerializedName("refrenceID")
	val refrenceID: String? = null,

	@field:SerializedName("agentPassword")
	val agentPassword: String? = null,

	@field:SerializedName("kycApproved")
	val kycApproved: String? = null,

	@field:SerializedName("firstName")
	val firstName: Any? = null,

	@field:SerializedName("applicationMode")
	val applicationMode: String? = null,

	@field:SerializedName("adminCode")
	val adminCode: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("agencyname")
	val agencyname: String? = null
)
