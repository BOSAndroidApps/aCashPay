package com.bos.payment.appName.data.repository

import com.bos.payment.appName.data.model.fastTag.viewBillPayment.FetchBilPaymentDetailsReq
import com.bos.payment.appName.data.model.justpaymodel.CheckBankDetailsModel
import com.bos.payment.appName.data.model.justpaymodel.GenerateVirtualAccountModel
import com.bos.payment.appName.data.model.recharge.BillOperationPaymentReq
import com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.MobileWiseRechargeReq
import com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.RechargeCategoryReq
import com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.RechargeOperatorsReq
import com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.RechargePlanReq
import com.bos.payment.appName.data.model.recharge.qrCode.GenerateQRCodeReq
import com.bos.payment.appName.data.model.recharge.recharge.DthInfoReq
import com.bos.payment.appName.data.model.recharge.recharge.MobileRechargeReq
import com.bos.payment.appName.data.model.recharge.recharge.UploadRechargeMobileRespReq
import com.bos.payment.appName.data.model.recharge.recharge.UploadRechargeMobileRespRespReq
import com.bos.payment.appName.data.model.travel.flight.FlightRequeryReq
import com.bos.payment.appName.data.model.travel.flight.GetAirTicketListReq
import com.bos.payment.appName.network.ApiInterface
import okhttp3.RequestBody

class MobileRechargeRepository(private var apiInterface: ApiInterface) {

    suspend fun getRechargeCategoryRequest(req: RechargeCategoryReq)= apiInterface.getRechargeCategory(req)

    suspend fun getRechargeOperatorsNameReq(req: RechargeOperatorsReq)= apiInterface.getRechargeOperatorNameReq(req)

    suspend fun getMobileWiseRechargeReq(req: MobileWiseRechargeReq)= apiInterface.getMobileWiseRechargeReq(req)

    suspend fun getDthInfoReq(req:DthInfoReq)= apiInterface.getDthInfoPlanReq(req)

    suspend fun getMobileRechargeReq(req: com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.MobileRechargeReq)= apiInterface.getMobileRechargeReq(req)

    suspend fun getOperatorList(req: BillOperationPaymentReq) = apiInterface.getOperatorList(req)

    suspend fun createVirtualAccount(req: GenerateVirtualAccountModel)= apiInterface.createVirtualAccount(req)

    suspend fun createQRCode(req: com.bos.payment.appName.data.model.justpaymodel.GenerateQRCodeReq)= apiInterface.createQRCode(req)

    suspend fun viewBill(req: FetchBilPaymentDetailsReq) = apiInterface.viewBill(req)
}