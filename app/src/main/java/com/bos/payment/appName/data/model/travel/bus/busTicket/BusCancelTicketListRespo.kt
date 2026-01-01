package com.bos.payment.appName.data.model.travel.bus.busTicket

import com.google.gson.annotations.SerializedName


data class BusCancelTicketListRespo(

	@field:SerializedName("returnCode")
	val returnCode: String? = null,

	@field:SerializedName("data")
	val data: List<CancelTicketDataItem>? = null,

	@field:SerializedName("returnMessage")
	val returnMessage: String? = null,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean? = null
)


data class CancelBusTicketApiData(

	@field:SerializedName("response_Header")
	val responseHeader: BusCancelTicketResponseHeader? = null,

	@field:SerializedName("cancellationPenaltyValues")
	val cancellationPenaltyValues: Any? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statuss")
	val statuss: String? = null,

	@field:SerializedName("value")
	val value: String? = null
)


data class BusCancelTicketResponseHeader(

	@field:SerializedName("status_Id")
	val statusId: String? = null,

	@field:SerializedName("error_Code")
	val errorCode: String? = null,

	@field:SerializedName("error_Desc")
	val errorDesc: String? = null,

	@field:SerializedName("error_InnerException")
	val errorInnerException: String? = null,

	@field:SerializedName("request_Id")
	val requestId: String? = null
)



data class CancelTicketDataItem(

	@field:SerializedName("booking_RefNo")
	val bookingRefNo: String? = null,

	@field:SerializedName("loginId")
	val loginId: String? = null,

	@field:SerializedName("apiData")
	val apiData: CancelBusTicketApiData? = null,

	@field:SerializedName("registrationID")
	val registrationID: String? = null,

	@field:SerializedName("statusdesc")
	val statusdesc: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: String? = null,

	var passangerList : MutableList<PaXDetailsItem> ? = mutableListOf(),

	var droppingTime  :String,
	val boardingTime : String,
	val fromCity :String,
	val toCity:String,
	val busoperatorname:String,
	val busType:String,
	val travelDate:String,
	val passangerquantity:String,
	var tableData: MutableList<MutableList<String?>>,
	val dataUpdate: Boolean
)
