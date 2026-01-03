package com.bos.payment.appName.data.model.recharge.recharge

import com.google.gson.annotations.SerializedName

data class UploadRechargeMobileRespResp(

	@field:SerializedName("returnCode")
	val returnCode: String? = null,

	@field:SerializedName("data")
	val data: TransferToAgentDataItem? = null,

	@field:SerializedName("returnMessage")
	val returnMessage: String? = null,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean? = null
)

data class TransferToAgentDataItem(
	@field:SerializedName("refTransID")
	val refTransID: String? = null,

	@field:SerializedName("requestData")
	val requestData: Any? = null
)