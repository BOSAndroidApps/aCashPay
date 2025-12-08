package com.bos.payment.appName.network

import com.bos.payment.appName.data.model.travel.bus.addMoney.BusAddMoneyReq
import com.bos.payment.appName.data.model.travel.bus.addMoney.BusAddMoneyRes
import com.bos.payment.appName.data.model.travel.bus.busBooking.BusTempBookingReq
import com.bos.payment.appName.data.model.travel.bus.busBooking.BusTempBookingRes
import com.bos.payment.appName.data.model.travel.bus.busRequery.BusRequeryReq
import com.bos.payment.appName.data.model.travel.bus.busRequery.BusRequeryRes
import com.bos.payment.appName.data.model.travel.bus.busSeatMap.BusSeatMapReq
import com.bos.payment.appName.data.model.travel.bus.busSeatMap.BusSeatMapRes
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusPassengerDetailsReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusPassangerDetailsRes
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketCancelReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketCancelRes
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketCancellationChargeReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketCancellationChargeRes
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketingReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketingRes
import com.bos.payment.appName.data.model.travel.bus.city.BusCityListReq
import com.bos.payment.appName.data.model.travel.bus.city.BusCityListRes
import com.bos.payment.appName.data.model.travel.bus.history.BusHistoryReq
import com.bos.payment.appName.data.model.travel.bus.history.BusHistoryRes
import com.bos.payment.appName.data.model.travel.bus.searchBus.BusSearchReq
import com.bos.payment.appName.data.model.travel.bus.searchBus.BusSearchRes
import com.bos.payment.appName.data.model.travel.flight.AirCommissionReq
import com.bos.payment.appName.data.model.travel.flight.AirCommissionResp
import com.bos.payment.appName.data.model.travel.flight.AirReprintReq
import com.bos.payment.appName.data.model.travel.flight.AirTicketCancelReq
import com.bos.payment.appName.data.model.travel.flight.AirTicketingReq
import com.bos.payment.appName.data.model.travel.flight.AirTicketingResponse
import com.bos.payment.appName.data.model.travel.flight.FlightAddPaymentReq
import com.bos.payment.appName.data.model.travel.flight.FlightAddPaymentResponse
import com.bos.payment.appName.data.model.travel.flight.FlightRePriceReq
import com.bos.payment.appName.data.model.travel.flight.FlightRepriceResponse
import com.bos.payment.appName.data.model.travel.flight.FlightRequeryReq
import com.bos.payment.appName.data.model.travel.flight.FlightRequeryResponse
import com.bos.payment.appName.data.model.travel.flight.FlightSearchReq
import com.bos.payment.appName.data.model.travel.flight.FlightSearchResp
import com.bos.payment.appName.data.model.travel.flight.FlightTempBookingReq
import com.bos.payment.appName.data.model.travel.flight.FlightTempBookingResponse
import com.bos.payment.appName.data.model.travel.flight.airReprintresponse.AirReprintRespo
import com.bos.payment.appName.data.model.travel.flight.flightticketcancelResponse.TicketCancelResponse
import com.example.theemiclub.data.model.loginsignup.verification.AAdhaarDetailesReq
import com.example.theemiclub.data.model.loginsignup.verification.AadhaarDetailsResponse
import com.example.theemiclub.data.model.loginsignup.verification.AadharVerificationReq
import com.example.theemiclub.data.model.loginsignup.verification.AadharVerificationResp
import com.example.theemiclub.data.model.loginsignup.verification.PanVerificationReq
import com.example.theemiclub.data.model.loginsignup.verification.PanVerificationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TravelInterface {

    @POST("api/V1/Bus/Travel/BusCityList")
    suspend fun getAllBusCityList(@Body req: BusCityListReq): Response<BusCityListRes>?


    @POST("api/V1/Bus/Travel/BusSearch")
    suspend fun getAllBusSearchList(@Body req: BusSearchReq): Response<BusSearchRes>?


    @POST("api/V1/Bus/Travel/BusSeatMap")
    suspend fun getAllBusSeatMap(@Body req: BusSeatMapReq) : Response<BusSeatMapRes>?


    @POST("api/V1/Bus/Travel/BusTempBooking")
    suspend fun getAllBusTempBooking(@Body req: BusTempBookingReq) : Response<BusTempBookingRes>?


    @POST("api/V1/Bus/Travel/BusAddMoney")
    suspend fun getAllAddMoney(@Body req: BusAddMoneyReq): Response<BusAddMoneyRes>?


    @POST("api/V1/Bus/Travel/BusTicketing")
    suspend fun getAllBusTicketing(@Body req: BusTicketingReq): Response<BusTicketingRes>?


    @POST("api/V1/Bus/Travel/BusRequery")
    suspend fun getAllBusRequary(@Body req: BusRequeryReq): Response<BusRequeryRes>


    @POST("api/V1/Bus/Travel/BusHistory")
    suspend fun getAllBusHistory(@Body req: BusHistoryReq): Response<BusHistoryRes>?


    @POST("api/V1/Bus/Travel/BusCancellationCharges")
    suspend fun getBucCancellationCharges(@Body req: BusTicketCancellationChargeReq): Response<BusTicketCancellationChargeRes>?


    @POST("api/V1/Bus/Travel/BusCancellation")
    suspend fun getBusTicketCancel(@Body req: BusTicketCancelReq): Response<BusTicketCancelRes>?


    @POST("api/V1/Air/Travel/AirSearch")
    suspend fun getFlightSerahList(@Body req: FlightSearchReq): Response<FlightSearchResp>? // Annu


    @POST("api/V1/Air/Travel/AirRePrice")
    suspend fun getAirRePriceLists(@Body req: FlightRePriceReq): Response<FlightRepriceResponse>? // Annu


    @POST("api/V1/Air/Travel/AirTempBooking")
    suspend fun getAirTempBookingReq(@Body req: FlightTempBookingReq): Response<FlightTempBookingResponse>? // Annu


    @POST("api/V1/Air/Travel/AddPayment")
    suspend fun getAirTicketAddPaymentReq(@Body req: FlightAddPaymentReq): Response<FlightAddPaymentResponse>? // Annu


    @POST("api/V1/Air/Travel/AirTicketing")
    suspend fun getAirTicketingReq(@Body req: AirTicketingReq): Response<AirTicketingResponse>? // Annu


    @POST("api/V1/Air/Travel/AirReprint")
    suspend fun getAirTicketReprintReq(@Body req: AirReprintReq): Response<AirReprintRespo>? // Annu


    @POST("api/V1/Air/Travel/AirTicketCancellation")
    suspend fun getAirTicketCancelationReq(@Body req: AirTicketCancelReq): Response<TicketCancelResponse>? // Annu


    // Adhar verification
    @POST("api/AOP/V1/Validation/AadhaarValidateUrl")
    suspend fun getAadharVarification(@Body req : AadharVerificationReq): Response<AadharVerificationResp>?

    // Adhar details
    @POST("api/AOP/V1/Fetch/Digilocker/TransactionID")
    suspend fun getAadharDetails(@Body req : AAdhaarDetailesReq): Response<AadhaarDetailsResponse>?

    @POST("api/AOP/V1/Validation/PanDetails")
    suspend fun getPanVarification(@Body req : PanVerificationReq): Response<PanVerificationResponse>?


}