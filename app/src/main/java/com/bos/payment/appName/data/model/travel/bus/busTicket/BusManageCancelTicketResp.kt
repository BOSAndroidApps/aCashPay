package com.bos.payment.appName.data.model.travel.bus.busTicket

import com.google.gson.annotations.SerializedName

data class BusManageCancelTicketResp(

	@field:SerializedName("outputMessage")
	val outputMessage: String? = null,

	@field:SerializedName("data")
	val data: List<Any?>? = null
)
