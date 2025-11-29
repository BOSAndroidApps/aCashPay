package com.bos.payment.appName.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bos.payment.appName.data.model.fastTag.viewBillPayment.FetchBilPaymentDetailsReq
import com.bos.payment.appName.data.model.justpaymodel.CheckBankDetailsModel
import com.bos.payment.appName.data.model.justpaymodel.GenerateVirtualAccountModel
import com.bos.payment.appName.data.model.recharge.BillOperationPaymentReq
import com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.MobileWiseRechargeReq
import com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.RechargeCategoryReq
import com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.RechargeOperatorsReq
import com.bos.payment.appName.data.model.recharge.qrCode.GenerateQRCodeReq
import com.bos.payment.appName.data.model.recharge.recharge.DthInfoReq
import com.bos.payment.appName.data.model.recharge.recharge.MobileRechargeReq
import com.bos.payment.appName.data.model.recharge.recharge.UploadRechargeMobileRespReq
import com.bos.payment.appName.data.model.recharge.recharge.UploadRechargeMobileRespRespReq
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.repository.MobileRechargeRepository
import com.bos.payment.appName.utils.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import okhttp3.RequestBody
import java.io.IOException

class GetAllMobileRechargeViewModel(private val repository: MobileRechargeRepository) : ViewModel(){

    // Mobile Recharge New API.............................................................................................................

    fun getRechargeCategoryRequest(req: RechargeCategoryReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.getRechargeCategoryRequest(req)
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


    fun getRechargeOperatorsRequest(req: RechargeOperatorsReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.getRechargeOperatorsNameReq(req)
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


    fun getRechargeMobileWiseRequest(req: MobileWiseRechargeReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.getMobileWiseRechargeReq(req)
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


    fun getDthInfoRequest(req: DthInfoReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.getDthInfoReq(req)
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


    // for bbps api.................................................................................................

    fun getOperatorList(req: BillOperationPaymentReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getOperatorList(req)))
        } catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getMobileRechargeRequest(req: com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.MobileRechargeReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.getMobileRechargeReq(req)
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

    fun viewBill(req: FetchBilPaymentDetailsReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.viewBill(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }


    fun createVirtualAccount(req: GenerateVirtualAccountModel) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.createVirtualAccount(req)
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


    fun createQRCode(req: com.bos.payment.appName.data.model.justpaymodel.GenerateQRCodeReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            val response = withTimeout(10_0000) { // 10 seconds timeout
                repository.createQRCode(req)
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


}