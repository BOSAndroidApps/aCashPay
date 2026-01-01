package com.bos.payment.appName.data.model.travel.bus.busTicket

import com.google.gson.annotations.SerializedName

data class BusPassengerDetailsReq (
    @SerializedName("booking_RefNo")
    var bookingRefNo  : String?
)


data class BusPaxRequeryResponseReq (

    @SerializedName("loginID")
    var loginId  : String?,

    @SerializedName("booking_RefNo")
    var bookingRefNo  : String?,

    @SerializedName("iP_Address")
    var ipAddress  : String?,

    @SerializedName("request_Id")
    var requestId  : String?,

    @SerializedName("imeI_Number")
    var imeINumber  : String?,

    @SerializedName("registrationID")
    var registrationId  : String?,

    @SerializedName("transport_PNR")
    var transportPNR  : String?,

    @SerializedName("ticket_Status_Id")
    var ticketStatusId  : String?,

    @SerializedName("ticket_Status_Desc")
    var ticketStatusDesc  : String?,

    @SerializedName("apiResponse")
    var apiResponse  : String?,

    @SerializedName("paramuser")
    var paramUser  : String?,

    @SerializedName("Ticketcancelstatus")
    var ticketcancelstatus  : String?,

)