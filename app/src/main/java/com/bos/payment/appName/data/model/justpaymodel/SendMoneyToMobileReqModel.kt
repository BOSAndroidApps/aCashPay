package com.bos.payment.appName.data.model.justpaymodel

import com.google.gson.annotations.SerializedName

data class SendMoneyToMobileReqModel(

	@field:SerializedName("actual_Transaction_Amount")
	val actualTransactionAmount: Double? = null,

	@field:SerializedName("flag")
	val flag: String? = null,

	@field:SerializedName("amount_Type")
	val amountType: String? = null,

	@field:SerializedName("transferFromMsg")
	val transferFromMsg: String? = null,

	@field:SerializedName("transIpAddress")
	val transIpAddress: String? = null,

	@field:SerializedName("remark")
	val remark: String? = null,

	@field:SerializedName("transferTo")
	val transferTo: String? = null,

	@field:SerializedName("transferToMsg")
	val transferToMsg: String? = null,

	@field:SerializedName("transferAmt")
	val transferAmt: Double? = null,

	@field:SerializedName("parmUserName")
	val parmUserName: String? = null,

	@field:SerializedName("merchantCode")
	val merchantCode: String? = null
)
