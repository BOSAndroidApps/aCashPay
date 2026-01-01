package com.bos.payment.appName.data.model.travel.bus.busTicket

import com.bos.payment.appName.data.model.travel.bus.busRequery.PaXDetails
import com.google.gson.annotations.SerializedName

data class BusBookingListRes(@SerializedName("returnCode")
                             val returnCode: String = "",
                             @SerializedName("data")
                             val data: MutableList<DataItem>?,
                             @SerializedName("returnMessage")
                             val returnMessage: String = "",
                             @SerializedName("isSuccess")
                             val isSuccess: Boolean = false)


data class DataItem(@SerializedName("booking_RefNo")
                    val bookingRefNo: String = "",
                    @SerializedName("transport_PNR")
                    val transportPNR: String = "",
                    @SerializedName("imeI_Number")
                    val imeINumber: String = "",
                    @SerializedName("statusdesc")
                    val statusdesc: String = "",
                    @SerializedName("request_Id")
                    val requestId: String = "",
                    @SerializedName("iP_Address")
                    val iPAddress: String = "",
                    @SerializedName("statusCode")
                    val statusCode: String = "",
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