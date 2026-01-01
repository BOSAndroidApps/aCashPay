package com.bos.payment.appName.data.model.travel.bus.forservicecharge

import com.google.gson.annotations.SerializedName

data class ServiceChargeResp(

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

	@field:SerializedName("updatedBy")
	val updatedBy: String? = null,

	@field:SerializedName("cancellationChargeValue")
	val cancellationChargeValue: Double? = null,

	@field:SerializedName("createdBy")
	val createdBy: String? = null,

	@field:SerializedName("adminCode")
	val adminCode: String? = null,

	@field:SerializedName("updatedOn")
	val updatedOn: String? = null,

	@field:SerializedName("rid")
	val rid: Int? = null,

	@field:SerializedName("createdOn")
	val createdOn: String? = null,

	@field:SerializedName("cancellationChargeType")
	val cancellationChargeType: String? = null
)
