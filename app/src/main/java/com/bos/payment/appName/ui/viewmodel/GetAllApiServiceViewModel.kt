package com.bos.payment.appName.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.bos.payment.appName.data.model.justpaymodel.CheckBankDetailsModel
import com.bos.payment.appName.data.model.justpaymodel.GenerateVirtualAccountModel
import com.bos.payment.appName.data.model.justpaymodel.GetToselfPayoutCommercialReq
import com.bos.payment.appName.data.model.justpaymodel.RetailerContactListRequestModel
import com.bos.payment.appName.data.model.justpaymodel.SendMoneyToMobileReqModel
import com.bos.payment.appName.data.model.justpaymodel.UpdateBankDetailsReq
import com.bos.payment.appName.data.model.justpedashboard.DashboardBannerListModel
import com.bos.payment.appName.data.model.justpedashboard.ProfileReq
import com.bos.payment.appName.data.model.justpedashboard.RetailerWiseServicesRequest
import com.bos.payment.appName.data.model.justpedashboard.RetailerWiseServicesResponse
import com.bos.payment.appName.data.model.makepaymentnew.BankDetailsReq
import com.bos.payment.appName.data.model.makepaymentnew.RaiseMakePaymentReq
import com.bos.payment.appName.data.model.makepaymentnew.ReferenceIDGenerateReq
import com.bos.payment.appName.data.model.managekyc.CountryStateDistrictReq
import com.bos.payment.appName.data.model.managekyc.RetailerProfileReq
import com.bos.payment.appName.data.model.managekyc.UpdateKycReq
import com.bos.payment.appName.data.model.menuList.GetAllMenuListReq
import com.bos.payment.appName.data.model.merchant.apiServiceCharge.GetPayoutCommercialReq
import com.bos.payment.appName.data.model.merchant.apiServiceCharge.mobileCharge.GetCommercialReq
import com.bos.payment.appName.data.model.promocode.GetEligibleReq
import com.bos.payment.appName.data.model.promocode.GetPromotionListReq
import com.bos.payment.appName.data.model.promocode.ManagePromoUsageReq
import com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.MobileWiseRechargeReq
import com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.RechargeCategoryReq
import com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.RechargeOperatorsReq
import com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.RechargePlanReq
import com.bos.payment.appName.data.model.recharge.recharge.RechargeapiresponseReq
import com.bos.payment.appName.data.model.recharge.recharge.TransferToAgentReq
import com.bos.payment.appName.data.model.recharge.recharge.UploadRechargeMobileRespReq
import com.bos.payment.appName.data.model.recharge.recharge.UploadRechargeMobileRespRespReq
import com.bos.payment.appName.data.model.serviceWiseTrans.TransactionReportReq
import com.bos.payment.appName.data.model.servicesbasednotification.NotificationReq
import com.bos.payment.appName.data.model.subscription.BillingCostReq
import com.bos.payment.appName.data.model.subscription.FeatureLinkReq
import com.bos.payment.appName.data.model.subscription.FeatureListReq
import com.bos.payment.appName.data.model.subscription.SubscriptionUserDeatilsReq
import com.bos.payment.appName.data.model.supportmanagement.AddCommentReq
import com.bos.payment.appName.data.model.supportmanagement.TicketStatusReq
import com.bos.payment.appName.data.model.transactionreportsmodel.CheckRaiseTicketExistReq
import com.bos.payment.appName.data.model.transactionreportsmodel.RaiseTicketReq
import com.bos.payment.appName.data.model.transactionreportsmodel.ReportListReq
import com.bos.payment.appName.data.model.transactionreportsmodel.TransactionReportsReq
import com.bos.payment.appName.data.model.transactionreportsmodel.VPATransactionReq
import com.bos.payment.appName.data.model.transferAMountToAgent.TransferAmountToAgentsReq
import com.bos.payment.appName.data.model.travel.bus.forservicecharge.ServiceChargeReq
import com.bos.payment.appName.data.model.travel.flight.AirCommissionReq
import com.bos.payment.appName.data.model.travel.flight.AirTicketBookingRequest
import com.bos.payment.appName.data.model.travel.flight.AirTicketBookingResponseRequest
import com.bos.payment.appName.data.model.travel.flight.FlightRequeryReq
import com.bos.payment.appName.data.model.travel.flight.GetAirTicketListReq
import com.bos.payment.appName.data.model.walletBalance.merchantBal.GetMerchantBalanceReq
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.GetBalanceReq
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.utils.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.IOException

class GetAllApiServiceViewModel constructor(private val repository: GetAllAPIServiceRepository) : ViewModel() {

    fun getAllRechargeAndBillServiceCharge(req: GetCommercialReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getAllRechargeAndBillServiceCharge(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }

    fun getToSelfPayoutServiceCharge(req: GetToselfPayoutCommercialReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getToSelfPayoutServiceCharge(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }

    fun getAllMerchantBalance(req: GetMerchantBalanceReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getAllMerchantBalance(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }

    fun getWalletBalance(req: GetBalanceReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getWalletBalance(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }

    fun getTransferAmountToAgents(req: TransferAmountToAgentsReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getTransferAmountToAgents(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }


    fun getAllApiPayoutCommercialCharge(req: GetPayoutCommercialReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getAllApiPayoutCommercialCharge(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }

    fun getAllMenuList(req: GetAllMenuListReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getAllMenuList(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }


    fun getAllTransactionReport(req: TransactionReportReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getAllTransactionReport(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }

    // for air requery request

    fun getAirRequeryRequest(req: FlightRequeryReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.getAirRequeryRequest(req)
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


    // for air ticket request
    fun getAirTicketListRequest(req: GetAirTicketListReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_000) { // 100 seconds (1 minute 40 seconds)
                repository.getAirTicketListRequest(req)
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


    // for upload Air Booking Ticket Request
    fun uploadAirBookingTicketRequest(req: AirTicketBookingRequest) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_000) { // 100 seconds (1 minute 40 seconds)
                repository.uploadTicketBookingRequest(req)
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


    // for upload Air Booking Ticket Response
    fun uploadAirBookingTicketResponseRequest(req: AirTicketBookingResponseRequest) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_000) { // 100 seconds (1 minute 40 seconds)
                repository.uploadTicketBookingResponseRequest(req)
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


    fun getFlightCommissionRequest(req: AirCommissionReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_000) { // 100 seconds (1 minute 40 seconds)
                repository.getFlightCommissionReq(req)
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


    //dashboard banner
    fun getDashboardBannerRequest(
        rid: Int,
        task: String,
        marchentcode: String,
        admincode: String,
        retailercode: String,
        agentType: String
    ) = liveData(Dispatchers.IO) {

        emit(ApiResponse.loading(data = null))

        try {
            val response = withTimeout(10_000) {
                repository.getDashboardBanner(rid, task, marchentcode, admincode, retailercode, agentType)
            }

            if (response.isSuccessful) {
                val body = response.body()

                // Check API internal success (isSuccess = true/false)
                if (body?.isSuccess == true) {
                    emit(ApiResponse.success(body))
                } else {
                    emit(ApiResponse.error(
                        data = body,
                        message = body?.returnMessage ?: "Something went wrong"
                    ))
                }

            } else {
                emit(ApiResponse.error(data = null, message = "Server error: ${response.code()}"))
            }

        } catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        } catch (e: IOException) {
            emit(ApiResponse.error(data = null, message = "No internet connection. Please check your network."))
        } catch (e: Exception) {
            emit(ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}"))
        }
    }

    // services request for retailerwise
    fun getRetailerWiseServicesReq(req: RetailerWiseServicesRequest) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getRetailerWiseServicesRequest(req)))
            }
        catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
            }

    }

    fun getBankDetails(req: CheckBankDetailsModel) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.getBankDetails(req)
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

    fun updateBankDetails(req: UpdateBankDetailsReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.updateBankDetails(req)
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

    fun getRetailerContactList(req: RetailerContactListRequestModel) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.getRetailerContactList(req)
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

    fun sendMoneyToMobileReqModel(req: SendMoneyToMobileReqModel) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.sendMoneyToMobileReqModel(req)
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
            emit(ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}"))
        }
    }

    fun sendForReportListReq(req: ReportListReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.sendForReportListReq(req)
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
            emit(ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}"))
        }
    }

    fun sendTransactionReportsReq(req: TransactionReportsReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.sendTransactionReportsReq(req)
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
            emit(ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}"))
        }
    }

    fun sendTransactionRaiseTicketExitsReq(req: CheckRaiseTicketExistReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.sendTransactionRaiseTicketExitsReq(req)
            }
            emit(ApiResponse.success(response))
        }
        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }
        catch (e: IOException) {
            emit(ApiResponse.error(
                    data = null,
                    message = "No internet connection. Please check your network."
                )
            )
        }
        catch (e: Exception) {
            emit(ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}"))
        }
    }

    fun uploadRaiseTicketReq(req: RaiseTicketReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.uploadRaiseTicketReq(req)))
        }
        catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }

    fun sendTicketStatusReq(req: TicketStatusReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.sendTicketStatusReq(req)))
        }
        catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }

    fun sendTicketCommentListReq(complaintId: Int) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.sendTicketCommentListReq(complaintId)))
        }
        catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }

    fun sendTicketCommentListReq(commentreq: AddCommentReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.addcommentReq(commentreq)))
        }
        catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }

    fun getReferenceIdReq(referenceID: ReferenceIDGenerateReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getReferenceID(referenceID)))
        }
        catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }

    fun getbanklistreq(banklistreq: BankDetailsReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getbanklistreq(banklistreq)))
        }
        catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }

    fun RaisMakePaymentReq(req: RaiseMakePaymentReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.uploadDocumentForRaisAmountTransferAdminReq(req)))
        }
        catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }

    fun putRechargemobileReq(req: UploadRechargeMobileRespReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.putRechargemobileReq(req)
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

    fun putRechargemobileResponseReq(req: UploadRechargeMobileRespRespReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.putRechargemobileResponseReq(req)
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

    fun putRechargeapiresponseReq(req: RechargeapiresponseReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.putRechargeapiresponseReq(req)
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

    fun transferToAgentReq(req: TransferToAgentReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.transferToAgentReq(req)
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

    fun vpaTransactionReq(req: VPATransactionReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.vpaTransactionReq(req)
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

    fun profileReq(req: ProfileReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.uploadProfileImage(req)
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

    fun retailerprofileKycReq(req: RetailerProfileReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.retailerProfileReq(req)
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

    fun countryStateDistrictListReq(req: CountryStateDistrictReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.countryStateDistrictListReq(req)
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

    fun UpdateKycReq(req: UpdateKycReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.UpdateKycReq(req)
            }
            emit(ApiResponse.success(response))
        }

        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }

        catch (e: IOException) {
            emit(
                ApiResponse.error(data = null, message = "No internet connection. Please check your network."))
        }

        catch (e: Exception) {
            emit(
                ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}")
            )
        }
    }

    fun subscriptionDetailsReq(req: SubscriptionUserDeatilsReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.subscriptionDetailsReq(req)
            }
            emit(ApiResponse.success(response))
        }

        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }

        catch (e: IOException) {
            emit(
                ApiResponse.error(data = null, message = "No internet connection. Please check your network."))
        }

        catch (e: Exception) {
            emit(
                ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}")
            )
        }
    }

    fun featureListReq(req: FeatureListReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.featureListReq(req)
            }
            emit(ApiResponse.success(response))
        }

        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }

        catch (e: IOException) {
            emit(
                ApiResponse.error(data = null, message = "No internet connection. Please check your network."))
        }

        catch (e: Exception) {
            emit(
                ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}")
            )
        }
    }

    fun billingCostReq(req: BillingCostReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.billingCostReq(req)
            }
            emit(ApiResponse.success(response))
        }

        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }

        catch (e: IOException) {
            emit(
                ApiResponse.error(data = null, message = "No internet connection. Please check your network."))
        }

        catch (e: Exception) {
            emit(
                ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}")
            )
        }
    }

    fun featureLinkReq(req: FeatureLinkReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.featureLinkReq(req)
            }
            emit(ApiResponse.success(response))
        }

        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }

        catch (e: IOException) {
            emit(
                ApiResponse.error(data = null, message = "No internet connection. Please check your network."))
        }

        catch (e: Exception) {
            emit(
                ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}")
            )
        }
    }

    fun GetNotificationReq(req: NotificationReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.GetNotificationReq(req)
            }
            emit(ApiResponse.success(response))
        }

        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }

        catch (e: IOException) {
            emit(
                ApiResponse.error(data = null, message = "No internet connection. Please check your network."))
        }

        catch (e: Exception) {
            emit(
                ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}")
            )
        }
    }

    fun GetPromotionListReq(req: GetPromotionListReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.GetPromotionListReq(req)
            }
            emit(ApiResponse.success(response))
        }

        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }

        catch (e: IOException) {
            emit(
                ApiResponse.error(data = null, message = "No internet connection. Please check your network."))
        }

        catch (e: Exception) {
            emit(
                ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}")
            )
        }
    }

    fun GetEligibleReq(req: GetEligibleReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.GetEligibleReq(req)
            }
            emit(ApiResponse.success(response))
        }

        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }

        catch (e: IOException) {
            emit(
                ApiResponse.error(data = null, message = "No internet connection. Please check your network."))
        }

        catch (e: Exception) {
            emit(
                ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}")
            )
        }
    }



    fun GetManagePromoUsageReq(req: ManagePromoUsageReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.GetManagePromoUsageReq(req)
            }
            emit(ApiResponse.success(response))
        }

        catch (e: TimeoutCancellationException) {
            emit(ApiResponse.error(data = null, message = "Request timed out. Please try again."))
        }

        catch (e: IOException) {
            emit(
                ApiResponse.error(data = null, message = "No internet connection. Please check your network."))
        }

        catch (e: Exception) {
            emit(
                ApiResponse.error(data = null, message = "Something went wrong: ${e.localizedMessage}")
            )
        }
    }

}