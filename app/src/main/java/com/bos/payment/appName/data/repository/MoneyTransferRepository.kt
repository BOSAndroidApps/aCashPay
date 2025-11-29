package com.bos.payment.appName.data.repository

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
import com.bos.payment.appName.data.model.makePayment.GetMakePaymentReq
import com.bos.payment.appName.data.model.merchant.activeInActiveStatus.GetAPIActiveInactiveStatusReq
import com.bos.payment.appName.data.model.merchant.apiServiceCharge.GetAPIServiceChargeReq
import com.bos.payment.appName.data.model.merchant.apiServiceCharge.GetPayoutCommercialReq
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
import com.bos.payment.appName.data.model.walletBalance.ReturnWalletBalanceReq
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.WalletBalanceReq
import com.bos.payment.appName.network.ApiInterface

class MoneyTransferRepository(private val apiInterface: ApiInterface) {
    suspend fun creditCardDetails(req: CreditCardBillPaymentReq) = apiInterface.creditCardDetails(req)
    suspend fun doRecharge(req: MobileRechargeReq) = apiInterface.doRecharge(req)
    suspend fun rechargeStatus(req: RechargeStatusReq) = apiInterface.rechargeStatus(req)
    suspend fun getAllOperatorList(req: RechargeOperatorsListReq) = apiInterface.getAllOperatorList(req)
    suspend fun transactionHistoryList(req: RechargeHistoryReq) = apiInterface.transactionHistoryList(req)
    suspend fun generateQRCode(req: GenerateQRCodeReq) = apiInterface.generateQRCode(req)
    suspend fun getBeneficiary(req: AddBeneficiaryReq) = apiInterface.getBeneficiary(req)
    suspend fun getOperatorName(req: MobileCheckReq) = apiInterface.getOperatorName(req)
    suspend fun getAllPlanList(req: MobileBrowserPlanReq) = apiInterface.getAllPlanList(req)
    suspend fun getFastTagList(registrationID: String) = apiInterface.getFastTagList(registrationID)
    suspend fun getNotification(req: GetNotificationReq) = apiInterface.getNotification(req)
    suspend fun getReDirectUrl(req: RedirectUrlVerifyReq) = apiInterface.getReDirectUrl(req)
    suspend fun getAllMerchantList(req: GetApiListMarchentWiseReq) = apiInterface.getAllMerchantList(req)
    suspend fun getClientDetails(req: GetClientRegistrationReq) = apiInterface.getClientDetails(req)
    suspend fun getDMTDetailsByMobileNumber(req: QueryRemitterReq) = apiInterface.getDMTDetailsByMobileNumber(req)
    suspend fun getDMTFetchBeneficiary(req: FetchBeneficiaryReq) = apiInterface.getDMTFetchBeneficiary(req)
    suspend fun registerBeneficiary(req: RegisterBaneficiaryReq) = apiInterface.registerBeneficiary(req)
    suspend fun registerRemitters(req: RegisterRemitterReq) = apiInterface.registerRemitters(req)
    suspend fun getTransactionOtp(req: TransactionSendOtpReq) = apiInterface.getTransactionOtp(req)
    suspend fun transactionAmountFromDMT(req: TransactionReq) = apiInterface.transactionAmountFromDMT(req)
    suspend fun getAllMakePayment(req: GetMakePaymentReq) = apiInterface.getAllMakePayment(req)
    suspend fun getAllDMTBankList(req: DMTBankListReq) = apiInterface.getAllDMTBankList(req)
    suspend fun getAllTransactionStatus(req: TransactStatusReq) = apiInterface.getAllTransactionStatus(req)
    suspend fun getAllAPIRetailerWiseActiveInActive(req: GetAPIActiveInactiveStatusReq) = apiInterface.getAllAPIRetailerWiseActiveInActive(req)
    suspend fun getAllAPIServiceCharge(req: GetAPIServiceChargeReq) = apiInterface.getAllAPIServiceCharge(req)
    suspend fun getAllApiPayoutCommercialCharge(req: GetPayoutCommercialReq) = apiInterface.getAllApiPayoutCommercialCharge(req)
    suspend fun getAllRechargeAndBillServiceCharge(req: GetCommercialReq) = apiInterface.getAllRechargeAndBillServiceCharge(req)
    suspend fun getWalletBalance(req: ReturnWalletBalanceReq) = apiInterface.getWalletBalance(req)
    suspend fun getAllWalletBalance(req: WalletBalanceReq) = apiInterface.getAllWalletBalance(req)

//    suspend fun getTransferAmountToAgents(req: TransferAmountToAgentsReq) = apiInterface.getTransferAmountToAgents(req)
    suspend fun getTransferAmountToAgentWithCal(req: TransferAmountToAgentsWithCalculationReq) = apiInterface.getTransferAmountToAgentWithCal(req)
    suspend fun getAgentReferenceId(req: AgentRefrenceidReq) = apiInterface.getAgentReferenceId(req)



}