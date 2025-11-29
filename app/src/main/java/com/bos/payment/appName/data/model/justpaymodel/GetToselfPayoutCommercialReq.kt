package com.bos.payment.appName.data.model.justpaymodel

import com.google.gson.annotations.SerializedName

data class GetToselfPayoutCommercialReq(

	@field:SerializedName("txtslabamtfrom")
	val txtslabamtfrom: Double? = null,

	@field:SerializedName("txtslabamtto")
	val txtslabamtto: Double? = null,

	@field:SerializedName("merchant")
	val merchant: String? = null,

	@field:SerializedName("modeofPayment")
	val modeofPayment: String? = null,

	@field:SerializedName("productId")
	val productId: String? = null

)
