package com.bos.payment.appName.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bos.payment.appName.data.model.AgentRefrenceid.AgentRefrenceidReq
import com.bos.payment.appName.data.model.creditCard.CreditCardBillPaymentReq
import com.bos.payment.appName.data.model.dmt.bankList.DMTBankListReq
import com.bos.payment.appName.data.model.dmt.fetchBenificary.FetchBeneficiaryReq
import com.bos.payment.appName.data.model.dmt.queryRemitters.QueryRemitterReq
import com.bos.payment.appName.data.model.dmt.registerBeneficiary.RegisterBaneficiaryReq
import com.bos.payment.appName.data.model.dmt.registerRemitters.RegisterRemitterReq
import com.bos.payment.appName.data.model.dmt.transaction.TransactStatusReq
import com.bos.payment.appName.data.model.dmt.transaction.TransactionReq
import com.bos.payment.appName.data.model.dmt.transactionOtp.TransactionSendOtpReq
import com.bos.payment.appName.data.model.fastTag.viewBillPayment.FetchBilPaymentDetailsReq
import com.bos.payment.appName.data.model.justpaymodel.CheckBankDetailsModel
import com.bos.payment.appName.data.model.makePayment.GetMakePaymentReq
import com.bos.payment.appName.data.model.merchant.activeInActiveStatus.GetAPIActiveInactiveStatusReq
import com.bos.payment.appName.data.model.merchant.apiServiceCharge.GetAPIServiceChargeReq
import com.bos.payment.appName.data.model.merchant.apiServiceCharge.mobileCharge.GetCommercialReq
import com.bos.payment.appName.data.model.merchant.clientDetails.GetClientRegistrationReq
import com.bos.payment.appName.data.model.merchant.merchantList.GetApiListMarchentWiseReq
import com.bos.payment.appName.data.model.merchant.redirectUrl.RedirectUrlVerifyReq
import com.bos.payment.appName.data.model.notification.GetNotificationReq
import com.bos.payment.appName.data.model.recharge.BillOperationPaymentReq
import com.bos.payment.appName.data.model.recharge.beneficiary.AddBeneficiaryReq
import com.bos.payment.appName.data.model.recharge.mobile.MobileCheckReq
import com.bos.payment.appName.data.model.recharge.operator.RechargeOperatorsListReq
import com.bos.payment.appName.data.model.recharge.plan.MobileBrowserPlanReq
import com.bos.payment.appName.data.model.recharge.qrCode.GenerateQRCodeReq
import com.bos.payment.appName.data.model.recharge.recharge.MobileRechargeReq
import com.bos.payment.appName.data.model.recharge.rechargeHistory.RechargeHistoryReq
import com.bos.payment.appName.data.model.recharge.status.RechargeStatusReq
import com.bos.payment.appName.data.model.transferAMountToAgent.TransferAmountToAgentsWithCalculationReq
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.WalletBalanceReq
import com.bos.payment.appName.data.repository.MoneyTransferRepository
import com.bos.payment.appName.utils.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import java.io.IOException

class MoneyTransferViewModel (private val repository: MoneyTransferRepository) : ViewModel() {

    fun creditCardDetails(req: CreditCardBillPaymentReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.creditCardDetails(req)))
        } catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun doRecharge(req: MobileRechargeReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.doRecharge(req)))
        } catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun rechargeStatus(req: RechargeStatusReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.rechargeStatus(req)))
        } catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getAllOperatorList(req: RechargeOperatorsListReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getAllOperatorList(req)))
        } catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun transactionHistoryList(req: RechargeHistoryReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.transactionHistoryList(req)))
        } catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun generateQRCode(req: GenerateQRCodeReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.generateQRCode(req)))
        } catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getBeneficiary(req: AddBeneficiaryReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getBeneficiary(req)))
        } catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getOperatorName(req: MobileCheckReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getOperatorName(req)))
        } catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getAllPlanList(req: MobileBrowserPlanReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository!!.getAllPlanList(req)))
        } catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getFastTagList(registrationID: String) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getFastTagList(registrationID)))
        } catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getNotification(req: GetNotificationReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getNotification(req)))
        } catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
    fun getReDirectUrl(req: RedirectUrlVerifyReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            Log.d("getAllMerchantList","${req.url.toString()}")
            emit(ApiResponse.success(data = repository.getReDirectUrl(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }
    fun getAllMerchantList(req: GetApiListMarchentWiseReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            Log.d("getAllMerchantList","${req.MarchentID}")
            emit(ApiResponse.success(data = repository.getAllMerchantList(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }
    fun getClientDetails(req: GetClientRegistrationReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getClientDetails(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }
    fun getDMTDetailsByMobileNumber(req: QueryRemitterReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getDMTDetailsByMobileNumber(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }
    fun getDMTFetchBeneficiary(req: FetchBeneficiaryReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getDMTFetchBeneficiary(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }
    fun registerBeneficiary(req: RegisterBaneficiaryReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.registerBeneficiary(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }
    fun registerRemitters(req: RegisterRemitterReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.registerRemitters(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }
    fun getTransactionOtp(req: TransactionSendOtpReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getTransactionOtp(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }
    fun transactionAmountFromDMT(req: TransactionReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.transactionAmountFromDMT(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }

    fun getAllMakePayment(req: GetMakePaymentReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getAllMakePayment(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }
    fun getAllDMTBankList(req: DMTBankListReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getAllDMTBankList(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }
    fun getAllTransactionStatus(req: TransactStatusReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getAllTransactionStatus(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }
    fun getAllAPIRetailerWiseActiveInActive(req: GetAPIActiveInactiveStatusReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getAllAPIRetailerWiseActiveInActive(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }
    fun getAllAPIServiceCharge(req: GetAPIServiceChargeReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getAllAPIServiceCharge(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }
//    fun getAllApiPayoutCommercialCharge(req: GetPayoutCommercialReq) = liveData(Dispatchers.IO) {
//        emit(ApiResponse.loading(data = null))
//        try {
//            emit(ApiResponse.success(data = repository.getAllApiPayoutCommercialCharge(req)))
//        }catch (exception: Exception) {
//            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
//        }
//    }
    fun getAllRechargeAndBillServiceCharge(req: GetCommercialReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getAllRechargeAndBillServiceCharge(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }
//    fun getWalletBalance(req: ReturnWalletBalanceReq) = liveData(Dispatchers.IO) {
//        emit(ApiResponse.loading(data = null))
//        try {
//            emit(ApiResponse.success(data = repository.getWalletBalance(req)))
//        }catch (exception: Exception) {
//            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
//        }
//    }
//    fun getAllMerchantBalance(req: GetMerchantBalanceReq) = liveData(Dispatchers.IO) {
//        emit(ApiResponse.loading(data = null))
//        try {
//            emit(ApiResponse.success(data = repository.getAllMerchantBalance(req)))
//        }catch (exception: Exception) {
//            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
//        }
//    }
    fun getAllWalletBalance(req: WalletBalanceReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getAllWalletBalance(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }
//    fun getWalletBalance(req: GetBalanceReq) = liveData(Dispatchers.IO) {
//        emit(ApiResponse.loading(data = null))
//        try {
//            emit(ApiResponse.success(data = repository.getWalletBalance(req)))
//        }catch (exception: Exception) {
//            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
//        }
//    }
//    fun getTransferAmountToAgents(req: TransferAmountToAgentsReq) = liveData(Dispatchers.IO) {
//        emit(ApiResponse.loading(data = null))
//        try {
//            emit(ApiResponse.success(data = repository.getTransferAmountToAgents(req)))
//        }catch (exception: Exception) {
//            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
//        }
//    }
    fun getTransferAmountToAgentWithCal(req: TransferAmountToAgentsWithCalculationReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getTransferAmountToAgentWithCal(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }

    fun getAgentReferenceId(req: AgentRefrenceidReq) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = repository.getAgentReferenceId(req)))
        }catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }





}