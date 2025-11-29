package com.bos.payment.appName.data.model.justpaymodel

import com.google.gson.annotations.SerializedName

data class GenerateVirtualBankDetailsResponseModel(

	@field:SerializedName("responsecode")
	val responsecode: Int? = null,

	@field:SerializedName("status_code")
	val statusCode: Int? = null,

	@field:SerializedName("details")
	val details: VPADetails? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class VPADetails(

	@field:SerializedName("merchantID")
	val merchantID: String? = null,

	@field:SerializedName("responsemessage")
	val responsemessage: String? = null,

	@field:SerializedName("monthlyCollectionLimit")
	val monthlyCollectionLimit: String? = null,

	@field:SerializedName("partnerreferencenumber")
	val partnerreferencenumber: String? = null,

	@field:SerializedName("selleridentifier")
	val selleridentifier: String? = null,

	@field:SerializedName("settlementAccountId")
	val settlementAccountId: String? = null,

	@field:SerializedName("ecollectaccountnumber")
	val ecollectaccountnumber: String? = null,

	@field:SerializedName("dueDiligenceStatus")
	val dueDiligenceStatus: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null,

	@field:SerializedName("yphubusername")
	val yphubusername: String? = null
)
