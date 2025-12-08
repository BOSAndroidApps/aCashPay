package com.bos.payment.appName.data.model.justpedashboard

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

	@field:SerializedName("returnCode")
	val returnCode: String? = null,

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("returnMessage")
	val returnMessage: String? = null,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean? = null
)

data class Data(

	@field:SerializedName("profileImage")
	val profileImage: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("userID")
	val userID: String? = null,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean? = null
)
