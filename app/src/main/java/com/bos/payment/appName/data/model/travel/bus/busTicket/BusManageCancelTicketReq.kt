package com.bos.payment.appName.data.model.travel.bus.busTicket

import com.google.gson.annotations.SerializedName

data class BusManageCancelTicketReq(

	@field:SerializedName("pnR_Number")
	val pnRNumber: String? = null,

	@field:SerializedName("loginId")
	val loginId: String? = null,

	@field:SerializedName("cancellationCharge")
	val cancellationCharge: Double? = null,

	@field:SerializedName("bookingRefNo")
	val bookingRefNo: String? = null,

	@field:SerializedName("adminRemarks")
	val adminRemarks: String? = null,

	@field:SerializedName("toCity")
	val toCity: String? = null,

	@field:SerializedName("refundStatus")
	val refundStatus: String? = null,

	@field:SerializedName("fareAmount")
	val fareAmount: Int? = null,

	@field:SerializedName("taskType")
	val taskType: String? = null,

	@field:SerializedName("travelDate")
	val travelDate: String? = null,

	@field:SerializedName("adminId")
	val adminId: String? = null,

	@field:SerializedName("cancellationtype")
	val cancellationtype: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("fromCity")
	val fromCity: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("refundAmount")
	val refundAmount: Int? = null
)
