package com.bos.payment.appName.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.bos.payment.appName.data.model.travel.bus.addMoney.BusAddMoneyReq
import com.bos.payment.appName.data.model.travel.bus.busBooking.BusTempBookingReq
import com.bos.payment.appName.data.model.travel.bus.busRequery.BusRequeryReq
import com.bos.payment.appName.data.model.travel.bus.busSeatMap.BusSeatMapReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.AddTicketReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.AddTicketResponseReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusBookingListReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusPassengerDetailsReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusPaxRequeryResponseReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTampBookTicketResponseRequest
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTempBookingRequest
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketCancelReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketCancelResponseReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketCancellationChargeReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketingReq
import com.bos.payment.appName.data.model.travel.bus.city.BusCityListReq
import com.bos.payment.appName.data.model.travel.bus.city.BusCityListRes
import com.bos.payment.appName.data.model.travel.bus.forservicecharge.BusCommissionReq
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
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.utils.ApiResponse
import com.bos.payment.appName.utils.Resource
import com.example.theemiclub.data.model.loginsignup.verification.AAdhaarDetailesReq
import com.example.theemiclub.data.model.loginsignup.verification.AadharVerificationReq
import com.example.theemiclub.data.model.loginsignup.verification.PanVerificationReq
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import retrofit2.Response
import java.io.IOException

class TravelViewModel (private val travelRepository: TravelRepository) : ViewModel() {

    fun getAllBusCityList(req: BusCityListReq): LiveData<Resource<Response<BusCityListRes>>> {
        val liveData = MutableLiveData<Resource<Response<BusCityListRes>>>()

        liveData.postValue(Resource.loading(data = null)) // 🔥 Show loading state

        viewModelScope.launch {
            try {
                val response = withTimeout(30_000) {
                    travelRepository.getAllBusCityList(req)
                }
                if (response!!.isSuccessful) {
                    liveData.postValue(Resource.success(response))   // ✅ Success
                } else {
                    Log.d("CompanyDetailsReq", response.message())
                    liveData.postValue(Resource.error(message = "server error Investor Details: ${response.message()}")) // ❌ Error
                }
            } catch (e: TimeoutCancellationException) {
                liveData.postValue(Resource.error(message = "Request time out. Please try again."))
            } catch (e: IOException) {
                liveData.postValue(Resource.error(message = "No internet connection. Please check your network."))
            } catch (e: Exception) {
                liveData.postValue(Resource.error(message = "Something went wrong: ${e.localizedMessage}"))
            }
        }
        return liveData
    }

//    fun getAllBusCityList(req: BusCityListReq) = liveData(Dispatchers.IO) {
//        emit(ApiResponse.loading(data = null))
//        try {
//            val response = withTimeout(10_000) { // 10 seconds timeout
//                travelRepository.getAllBusCityList(req)
//            }
//            emit(ApiResponse.success(response))
//        } catch (e: TimeoutCancellationException) {
//            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
//        } catch (e: IOException) {
//            emit(
//                ApiResponse.error(
//                    data = null,
//                    message = "No internet connection. Please check your network."
//                )
//            )
//        } catch (e: Exception) {
//            emit(
//                ApiResponse.error(
//                    data = null,
//                    message = "Something went wrong: ${e.localizedMessage}"
//                )
//            )
//        }
//    }

    fun getAllBusSearchList(req: BusSearchReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val result = travelRepository.getAllBusSearchList(req)
            if (result!!.body() != null) {
                if (result?.body()?.buses != null) {
                    emit(ApiResponse.success(data = result))
                } else {
                    emit(
                        ApiResponse.error(
                            data = null,
                            message = result?.body()?.message ?: "Error Occurred!"
                        )
                    )
                }
            } else {
                emit(
                    ApiResponse.error(
                        data = null,
                        message = result.message() ?: "Error Occurred!"
                    )
                )
            }

        } catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

//    fun getAllBusSearchList(req: BusSearchReq) = liveData(Dispatchers.IO) {
//        emit(ApiResponse.loading(data = null))
//        try {
//            val response = withTimeout(30_000) { // 10 seconds timeout
//                travelRepository.getAllBusSearchList(req)
//            }
//            emit(ApiResponse.success(response))
//        } catch (e: TimeoutCancellationException) {
//            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
//        } catch (e: IOException) {
//            emit(
//                ApiResponse.error(
//                    data = null,
//                    message = "No internet connection. Please check your network."
//                )
//            )
//        } catch (e: Exception) {
//            emit(
//                ApiResponse.error(
//                    data = null,
//                    message = "Something went wrong: ${e.localizedMessage}"
//                )
//            )
//        }
//    }

    fun getAllBusSeatMap(req: BusSeatMapReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getAllBusSeatMap(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }


    fun getAllBusTempBooking(req: BusTempBookingReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getAllBusTempBooking(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }

    fun getAllAddMoney(req: BusAddMoneyReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getAllAddMoney(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }


    fun getAllBusTicketing(req: BusTicketingReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getAllBusTicketing(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }

// new api .......................................................................................................................
    fun getAddBusTicketRequest(req: AddTicketReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getAddBusTicketRequest(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }


    fun getBusCommissionRequest(req: BusCommissionReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getBusCommissionRequest(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }

    //

    fun getAddBusTicketResponse(req: AddTicketResponseReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getAddBusTicketResponse(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }


    fun getBusBookListResponse(req: BusBookingListReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getBusBookListResponse(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }


    fun getBusTicketCancelResponseReq(req: BusTicketCancelResponseReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getBusTicketCancelResponseReq(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }
        catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit( ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}"))
        }
    }

    fun getBusTampBookRequest(req: BusTempBookingRequest) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getBusTampBookRequest(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }

    fun getTampBusTicketResponse(req: BusTampBookTicketResponseRequest) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getBusTampBookResponse(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }

    //getBusTampBookResponse


    fun getAllBusRequary(req: BusRequeryReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getAllBusRequary(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }

    fun getAllBusTicketHistory(req: BusHistoryReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getAllBusHistory(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }

   //
    fun getBusTicketCancellationCharge(req: BusTicketCancellationChargeReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getBusTicketCancellationChargeRequest(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }



    fun getBusTicketCancelRequest(req: BusTicketCancelReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getBusTicketCancelRequest(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }

    //

    fun getPassangerDetailsRequest(req: BusPassengerDetailsReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getPassangerDetails(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }


    fun getBusCancelTicketRequest(req: BusBookingListReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getBusCancelTicketDetails(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }


    fun getPassangerDetailsRequest(req: BusPaxRequeryResponseReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getPaxRequeryResponseRequest(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }

   // for flight .................................................................................................................

    fun getAirportListRequet(req: AirportListReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getAirportListRequest(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }


    fun getFlightSearchRequest(req: FlightSearchReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getFlightSearchRequest(req)
            }
            emit(ApiResponse.success(response))
        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }


    fun getFlightRePriseRequest(req: FlightRePriceReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getFlightRepriceRequest(req)
            }
            emit(ApiResponse.success(response))
        }
        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }
        catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        }
        catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }


    fun getFlightTempBookingRequest(req: FlightTempBookingReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getFlightTempBookingRequestt(req)
            }
            emit(ApiResponse.success(response))
        }
        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }
        catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        }
        catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }



    fun getFlightAddPaymentRequest(req: FlightAddPaymentReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getFlightAddPaymentRequest(req)
            }
            emit(ApiResponse.success(response))
        }
        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }
        catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        }
        catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }


    fun getAirTicketingRequest(req: AirTicketingReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getAirTicketingRequest(req)
            }
            emit(ApiResponse.success(response))
        }
        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }
        catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        }
        catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }


    fun getAirTicketReprintRequest(req: AirReprintReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getAirTicketReprintRequest(req)
            }
            emit(ApiResponse.success(response))
        }
        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }
        catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        }
        catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }


    fun getAirTicketCancelRequest(req: AirTicketCancelReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getAirTicketCancelRequest(req)
            }
            emit(ApiResponse.success(response))
        }
        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }
        catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        }
        catch (e: Exception) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "Something went wrong: ${e.localizedMessage}"
                )
            )
        }
    }



    fun getAadharVerificationRequest(req: AadharVerificationReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getAadharVarificationRequest(req)
            }
            emit(ApiResponse.success(response))
        }
        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }
        catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        }
        catch (e: Exception) {
            emit(
                ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}")
            )
        }
    }


    fun getAAdhaarDetailesReq(req: AAdhaarDetailesReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getAadharDetailsReq(req)
            }
            emit(ApiResponse.success(response))
        }
        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }
        catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        }
        catch (e: Exception) {
            emit(
                ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}")
            )
        }
    }


    fun getPanVerificationReq(req: PanVerificationReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(30_000) { // 10 seconds timeout
                travelRepository.getPanVerificationReq(req)
            }
            emit(ApiResponse.success(response))
        }
        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }
        catch (e: IOException) {
            emit(
                ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        }
        catch (e: Exception) {
            emit(
                ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}")
            )
        }
    }



}