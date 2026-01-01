package com.bos.payment.appName.data.repository

import com.bos.payment.appName.data.model.promocode.GetPromotionListReq
import com.bos.payment.appName.data.model.travel.bus.addMoney.BusAddMoneyReq
import com.bos.payment.appName.data.model.travel.bus.busBooking.BusTempBookingReq
import com.bos.payment.appName.data.model.travel.bus.busRequery.BusRequeryReq
import com.bos.payment.appName.data.model.travel.bus.busSeatMap.BusSeatMapReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.AddTicketReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.AddTicketResponseReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusBookingListReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusManageCancelTicketReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusPassengerDetailsReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusPaxRequeryResponseReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTampBookTicketResponseRequest
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTempBookingRequest
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketCancelReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketCancelResponseReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketCancellationChargeReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketingReq
import com.bos.payment.appName.data.model.travel.bus.city.BusCityListReq
import com.bos.payment.appName.data.model.travel.bus.forservicecharge.BusCommissionReq
import com.bos.payment.appName.data.model.travel.bus.forservicecharge.ServiceChargeReq
import com.bos.payment.appName.data.model.travel.bus.history.BusHistoryReq
import com.bos.payment.appName.data.model.travel.bus.searchBus.BusSearchReq
import com.bos.payment.appName.data.model.travel.flight.AirCommissionReq
import com.bos.payment.appName.data.model.travel.flight.AirReprintReq
import com.bos.payment.appName.data.model.travel.flight.AirTicketCancelReq
import com.bos.payment.appName.data.model.travel.flight.AirTicketingReq
import com.bos.payment.appName.data.model.travel.flight.AirportListReq
import com.bos.payment.appName.data.model.travel.flight.FlightAddPaymentReq
import com.bos.payment.appName.data.model.travel.flight.FlightRePriceReq
import com.bos.payment.appName.data.model.travel.flight.FlightRequeryReq
import com.bos.payment.appName.data.model.travel.flight.FlightSearchReq
import com.bos.payment.appName.data.model.travel.flight.FlightTempBookingReq
import com.bos.payment.appName.network.ApiInterface
import com.bos.payment.appName.network.TravelInterface
import com.example.theemiclub.data.model.loginsignup.verification.AAdhaarDetailesReq
import com.example.theemiclub.data.model.loginsignup.verification.AadharVerificationReq
import com.example.theemiclub.data.model.loginsignup.verification.PanVerificationReq


class TravelRepository(private val travelInterface: TravelInterface, private val apiInterface: ApiInterface?) {

    suspend fun getAllBusCityList(req: BusCityListReq) = travelInterface.getAllBusCityList(req)
    suspend fun getAllBusSearchList(req: BusSearchReq) = travelInterface.getAllBusSearchList(req)
    suspend fun getAllBusSeatMap(req: BusSeatMapReq) = travelInterface.getAllBusSeatMap(req)
    suspend fun getAllBusTempBooking(req: BusTempBookingReq) = travelInterface.getAllBusTempBooking(req)

    suspend fun getAllAddMoney(req: BusAddMoneyReq) = travelInterface.getAllAddMoney(req)
    suspend fun getAllBusTicketing(req: BusTicketingReq) = travelInterface.getAllBusTicketing(req)
    suspend fun getAllBusRequary(req: BusRequeryReq) = travelInterface.getAllBusRequary(req)
    suspend fun getAllBusHistory(req: BusHistoryReq) = travelInterface.getAllBusHistory(req)

    suspend fun getBusTicketCancellationChargeRequest(req:BusTicketCancellationChargeReq)= travelInterface.getBucCancellationCharges(req)
    suspend fun getBusTicketCancelRequest(req:BusTicketCancelReq)= travelInterface.getBusTicketCancel(req)
    suspend fun getPassangerDetails(req: BusPassengerDetailsReq)= apiInterface!!.getPassangerDetails(req)
    suspend fun getBusCancelTicketDetails(req: BusBookingListReq)= apiInterface!!.getBusCancelList(req)
    suspend fun getBusManageCancelTicketReq(req: BusManageCancelTicketReq)= apiInterface!!.getManageBusCancelList(req)
    suspend fun getPaxRequeryResponseRequest(req: BusPaxRequeryResponseReq)= apiInterface!!.getPaxRequeryResponseReq(req)


    suspend fun getAddBusTicketRequest(req: AddTicketReq) = apiInterface!!.getAddTicketRequest(req)
    suspend fun getBusCommissionRequest(req: BusCommissionReq) = apiInterface!!.getBusCommissionRequest(req)
    suspend fun getAddBusTicketResponse(req: AddTicketResponseReq) = apiInterface!!.getAddTicketResponse(req)
    suspend fun getBusBookListResponse(req: BusBookingListReq) = apiInterface!!.getAllBusBookingList(req)
    suspend fun getBusTicketCancelResponseReq(req: BusTicketCancelResponseReq) = apiInterface!!.getAllBusTicketCancelResponseReq(req)
    suspend fun getBusTampBookRequest(req: BusTempBookingRequest) = apiInterface!!.getBusTempBookingRequest(req)
    suspend fun getBusTampBookResponse(req: BusTampBookTicketResponseRequest) = apiInterface!!.getBusTempBookingRequest(req)


    suspend fun getAirportListRequest(req: AirportListReq)= apiInterface!!.getAirPortList(req)
    suspend fun getFlightSearchRequest(req: FlightSearchReq)= travelInterface.getFlightSerahList(req)
    suspend fun getFlightRepriceRequest(req: FlightRePriceReq)= travelInterface.getAirRePriceLists(req)
    suspend fun getFlightTempBookingRequestt(req: FlightTempBookingReq)= travelInterface.getAirTempBookingReq(req)



    suspend fun getFlightAddPaymentRequest(req: FlightAddPaymentReq)= travelInterface.getAirTicketAddPaymentReq(req)
    suspend fun getAirTicketingRequest(req: AirTicketingReq)= travelInterface.getAirTicketingReq(req)
    suspend fun getAirTicketReprintRequest(req: AirReprintReq)= travelInterface.getAirTicketReprintReq(req)



    suspend fun getAirTicketCancelRequest(req: AirTicketCancelReq)= travelInterface.getAirTicketCancelationReq(req)

    suspend fun getAadharVarificationRequest(req: AadharVerificationReq)= travelInterface.getAadharVarification(req)

    suspend fun getAadharDetailsReq(req: AAdhaarDetailesReq) = travelInterface.getAadharDetails(req)

    suspend fun getPanVerificationReq(req: PanVerificationReq) = travelInterface.getPanVarification(req)


    suspend fun GetServiceChargeReq(req: ServiceChargeReq)= apiInterface!!.GetServiceChargeReq(req)











}