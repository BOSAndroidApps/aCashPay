package com.bos.payment.appName.data.model.managekyc

import com.google.gson.annotations.SerializedName

data class RetailerProfileReq(

	@field:SerializedName("fromDate")
	val fromDate: Any? = null,

	@field:SerializedName("parmFlag")
	val parmFlag: String? = null,

	@field:SerializedName("agentType")
	val agentType: String? = null,

	@field:SerializedName("searchType")
	val searchType: String? = null,

	@field:SerializedName("toDate")
	val toDate: Any? = null,

	@field:SerializedName("adminid")
	val adminid: String? = null,

	@field:SerializedName("searchValue")
	val searchValue: String? = null,

	@field:SerializedName("userID")
	val userID: String? = null
)
