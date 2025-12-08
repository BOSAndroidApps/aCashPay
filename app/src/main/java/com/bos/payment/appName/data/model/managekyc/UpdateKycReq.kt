package com.bos.payment.appName.data.model.managekyc

import com.google.gson.annotations.SerializedName

data class UpdateKycReq(

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("lastName")
	val lastName: String? = null,

	@field:SerializedName("pincode")
	val pincode: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("gstno")
	val gstno: String? = null,

	@field:SerializedName("emailID")
	val emailID: String? = null,

	@field:SerializedName("mobileNo")
	val mobileNo: String? = null,

	@field:SerializedName("userID")
	val userID: String? = null,

	@field:SerializedName("alternateMobileNo")
	val alternateMobileNo: String? = null,

	@field:SerializedName("paramUser")
	val paramUser: String? = null,

	@field:SerializedName("updatekycStatus")
	val updatekycStatus: String? = null,

	@field:SerializedName("addharCardNo")
	val addharCardNo: String? = null,

	@field:SerializedName("panCardNumber")
	val panCardNumber: String? = null,

	@field:SerializedName("dob")
	val dob: String? = null,

	@field:SerializedName("district")
	val district: String? = null,

	@field:SerializedName("permanentAddress")
	val permanentAddress: String? = null,

	@field:SerializedName("state")
	val state: String? = null
)
