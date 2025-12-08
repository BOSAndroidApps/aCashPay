package com.bos.payment.appName.data.model.managekyc

import com.google.gson.annotations.SerializedName

data class RetailerProfileResp(

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
	val lastName: String? = null,

	@field:SerializedName("applicationType")
	val applicationType: String? = null,

	@field:SerializedName("agentType")
	val agentType: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("userID")
	val userID: String? = null,

	@field:SerializedName("webSite")
	val webSite: String? = null,

	@field:SerializedName("variantType")
	val variantType: String? = null,

	@field:SerializedName("sNo")
	val sNo: Any? = null,

	@field:SerializedName("registrationDate")
	val registrationDate: String? = null,

	@field:SerializedName("adminCode")
	val adminCode: String? = null,

	@field:SerializedName("refrenceType")
	val refrenceType: String? = null,

	@field:SerializedName("permanentAddress")
	val permanentAddress: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("creditBalnceLimit")
	val creditBalnceLimit: Int? = null,

	@field:SerializedName("holdingAmt")
	val holdingAmt: Int? = null,

	@field:SerializedName("companyCode")
	val companyCode: String? = null,

	@field:SerializedName("pincode")
	val pincode: String? = null,

	@field:SerializedName("merchantCode")
	val merchantCode: String? = null,

	@field:SerializedName("companyType")
	val companyType: String? = null,

	@field:SerializedName("holdingRemarks")
	val holdingRemarks: Any? = null,

	@field:SerializedName("gstno")
	val gstno: String? = null,

	@field:SerializedName("kycUpdate")
	val kycUpdate: String? = null,

	@field:SerializedName("fullName")
	val fullName: Any? = null,

	@field:SerializedName("emailID")
	val emailID: String? = null,

	@field:SerializedName("mobileNo")
	val mobileNo: String? = null,

	@field:SerializedName("officeAddress")
	val officeAddress: String? = null,

	@field:SerializedName("refrenceID")
	val refrenceID: String? = null,

	@field:SerializedName("adminBaseUrl")
	val adminBaseUrl: String? = null,

	@field:SerializedName("agencyName")
	val agencyName: String? = null,

	@field:SerializedName("alternateMobileNo")
	val alternateMobileNo: String? = null,

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("activeStatus")
	val activeStatus: String? = null,

	@field:SerializedName("addharCardNo")
	val addharCardNo: String? = null,

	@field:SerializedName("panCardNumber")
	val panCardNumber: String? = null,

	@field:SerializedName("dob")
	val dob: String? = null,

	@field:SerializedName("applicationMode")
	val applicationMode: String? = null,

	@field:SerializedName("businessType")
	val businessType: String? = null,

	@field:SerializedName("ref_Code")
	val refCode: String? = null,

	@field:SerializedName("district")
	val district: String? = null,

	@field:SerializedName("status")
	val status: Any? = null
)
