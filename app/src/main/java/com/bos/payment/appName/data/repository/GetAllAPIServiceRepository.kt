package com.bos.payment.appName.data.repository

import com.bos.payment.appName.data.model.justpaymodel.CheckBankDetailsModel
import com.bos.payment.appName.data.model.justpaymodel.GetToselfPayoutCommercialReq
import com.bos.payment.appName.data.model.justpaymodel.RetailerContactListRequestModel
import com.bos.payment.appName.data.model.justpaymodel.SendMoneyToMobileReqModel
import com.bos.payment.appName.data.model.justpaymodel.UpdateBankDetailsReq
import com.bos.payment.appName.data.model.justpedashboard.RetailerWiseServicesRequest
import com.bos.payment.appName.data.model.makepaymentnew.BankDetailsReq
import com.bos.payment.appName.data.model.makepaymentnew.MakePaymentReportResp
import com.bos.payment.appName.data.model.makepaymentnew.RaiseMakePaymentReq
import com.bos.payment.appName.data.model.makepaymentnew.ReferenceIDGenerateReq
import com.bos.payment.appName.data.model.menuList.GetAllMenuListReq
import com.bos.payment.appName.data.model.merchant.apiServiceCharge.GetPayoutCommercialReq
import com.bos.payment.appName.data.model.merchant.apiServiceCharge.mobileCharge.GetCommercialReq
import com.bos.payment.appName.data.model.recharge.recharge.RechargeapiresponseReq
import com.bos.payment.appName.data.model.recharge.recharge.TransferToAgentReq
import com.bos.payment.appName.data.model.recharge.recharge.UploadRechargeMobileRespReq
import com.bos.payment.appName.data.model.recharge.recharge.UploadRechargeMobileRespRespReq
import com.bos.payment.appName.data.model.serviceWiseTrans.TransactionReportReq
import com.bos.payment.appName.data.model.supportmanagement.AddCommentReq
import com.bos.payment.appName.data.model.supportmanagement.ChatCommentResp
import com.bos.payment.appName.data.model.supportmanagement.TicketStatusReq
import com.bos.payment.appName.data.model.supportmanagement.TicketStatusResp
import com.bos.payment.appName.data.model.transactionreportsmodel.CheckRaiseTicketExistReq
import com.bos.payment.appName.data.model.transactionreportsmodel.RaiseTicketReq
import com.bos.payment.appName.data.model.transactionreportsmodel.RaiseTicketResp
import com.bos.payment.appName.data.model.transactionreportsmodel.ReportListReq
import com.bos.payment.appName.data.model.transactionreportsmodel.TransactionReportsReq
import com.bos.payment.appName.data.model.transferAMountToAgent.TransferAmountToAgentsReq
import com.bos.payment.appName.data.model.travel.flight.AirCommissionReq
import com.bos.payment.appName.data.model.travel.flight.AirTicketBookingRequest
import com.bos.payment.appName.data.model.travel.flight.AirTicketBookingResponseRequest
import com.bos.payment.appName.data.model.travel.flight.FlightRequeryReq
import com.bos.payment.appName.data.model.travel.flight.GetAirTicketListReq
import com.bos.payment.appName.data.model.walletBalance.merchantBal.GetMerchantBalanceReq
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.GetBalanceReq
import com.bos.payment.appName.network.ApiInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class GetAllAPIServiceRepository(private val apiInterface: ApiInterface) {

    suspend fun getAllRechargeAndBillServiceCharge(req: GetCommercialReq) = apiInterface.getAllRechargeAndBillServiceCharge(req)
    suspend fun getToSelfPayoutServiceCharge(req: GetToselfPayoutCommercialReq) = apiInterface.getPayoutServiceCharge(req)
    suspend fun getWalletBalance(req: GetBalanceReq) = apiInterface.getWalletBalance(req)
    suspend fun getAllMerchantBalance(req: GetMerchantBalanceReq) = apiInterface.getAllMerchantBalance(req)

    suspend fun getTransferAmountToAgents(req: TransferAmountToAgentsReq) = apiInterface.getTransferAmountToAgents(req)
    suspend fun getAllApiPayoutCommercialCharge(req: GetPayoutCommercialReq) = apiInterface.getAllApiPayoutCommercialCharge(req)
    suspend fun getAllMenuList(req: GetAllMenuListReq) = apiInterface.getAllMenuList(req)
    suspend fun getAllTransactionReport(req: TransactionReportReq) = apiInterface.getAllTransactionReport(req)

    // air requery request ..........................
    suspend fun getAirRequeryRequest(req: FlightRequeryReq) = apiInterface.getAirTicketRequeryReq(req)

    suspend fun getAirTicketListRequest(req: GetAirTicketListReq) = apiInterface.getAirTicketListReq(req)

    suspend fun getFlightCommissionReq(req: AirCommissionReq)= apiInterface.getAirCommissionReq(req)

    suspend fun uploadTicketBookingRequest(req: AirTicketBookingRequest)= apiInterface.uploadTicketBookingRequest(req)

    suspend fun uploadTicketBookingResponseRequest(req: AirTicketBookingResponseRequest)= apiInterface.uploadTicketBookingResponse(req)

    suspend fun getDashboardBanner(rid: Int,task: String, marchentcode: String, admincode: String, retailercode: String,agentType: String) = apiInterface.getdashboardbanner(rid,task,marchentcode,admincode,retailercode,agentType)

    suspend fun getRetailerWiseServicesRequest(req: RetailerWiseServicesRequest) = apiInterface.getRetailerWiseServices(req)

    suspend fun getBankDetails(req: CheckBankDetailsModel)= apiInterface.getBankDetails(req)

    suspend fun updateBankDetails(req: UpdateBankDetailsReq)= apiInterface.updateBankDetails(req)

    suspend fun getRetailerContactList(req: RetailerContactListRequestModel)= apiInterface.gettingAllRetailerContactList(req)

    suspend fun sendMoneyToMobileReqModel(req: SendMoneyToMobileReqModel)= apiInterface.sendToMobileMoney(req)

    suspend fun sendForReportListReq(req: ReportListReq)= apiInterface.getReportListReq(req)

    suspend fun sendTransactionReportsReq(req: TransactionReportsReq)= apiInterface.getTransactionReportsReq(req)

    suspend fun sendTransactionRaiseTicketExitsReq(req: CheckRaiseTicketExistReq)= apiInterface.getcheckTransactionExitsReq(req)


    suspend fun uploadRaiseTicketReq(req: RaiseTicketReq): retrofit2.Response<RaiseTicketResp> {
        val userCode = req.userCode.toRequestBody("text/plain".toMediaTypeOrNull())
        val serviceCode = req.serviceCode.toRequestBody("text/plain".toMediaTypeOrNull())
        val subject = req.subject.toRequestBody("text/plain".toMediaTypeOrNull())
        val description = req.description.toRequestBody("text/plain".toMediaTypeOrNull())
        val priority = req.priority.toRequestBody("text/plain".toMediaTypeOrNull())
        val adminCode = req.adminCode.toRequestBody("text/plain".toMediaTypeOrNull())
        val imagePath1 = req.imagePath1.toRequestBody("text/plain".toMediaTypeOrNull())
        val imagePath2 = req.imagePath2.toRequestBody("text/plain".toMediaTypeOrNull())
        val imagePath3 = req.imagePath3.toRequestBody("text/plain".toMediaTypeOrNull())
        val transactionID = req.transactionID.toRequestBody("text/plain".toMediaTypeOrNull())
        val transactionSummary = req.transactionSummary.toRequestBody("text/plain".toMediaTypeOrNull())

        // Convert image file to MultipartBody.Part
        val imagePart1 = if (req.imageFile1 != null && req.imageFile1.exists()) {
            val requestFile = req.imageFile1.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("Image1_File", req.imageFile1.name, requestFile)
        } else {
            // send empty multipart field
            MultipartBody.Part.createFormData("Image1_File", "")
        }

        val imagePart2 = if (req.imageFile2 != null && req.imageFile2.exists()) {
            val requestFile = req.imageFile2.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("Image2_File", req.imageFile2.name, requestFile)
        } else {
            // send empty multipart field
            MultipartBody.Part.createFormData("Image2_File", "")
        }

        val imagePart3 = if (req.imageFile3 != null && req.imageFile3.exists()) {
            val requestFile = req.imageFile3.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("Image3_File", req.imageFile3.name, requestFile)
        } else {
            // send empty multipart field
            MultipartBody.Part.createFormData("Image3_File", "")
        }

        return apiInterface.uploadDocumentForRaiseTicket(
            userCode, serviceCode, subject,description,priority, adminCode, imagePath1, imagePath2,
            imagePath3, transactionID, transactionSummary, imagePart1, imagePart2, imagePart3
        )

    }


    suspend fun sendTicketStatusReq(req: TicketStatusReq): retrofit2.Response<TicketStatusResp> {
        return apiInterface.getticketstatusreq(req.adminCode,req.userCode)
    }

    suspend fun sendTicketCommentListReq(complaintId: Int): retrofit2.Response<ChatCommentResp> {
        return apiInterface.getTicketComments(complaintId)
    }

    suspend fun addcommentReq(commentreq: AddCommentReq)= apiInterface.addcommentReq(commentreq)


    suspend fun getReferenceID(commentreq: ReferenceIDGenerateReq)= apiInterface.getRandomReferenceID(commentreq)

    suspend fun getbanklistreq(banklistreq: BankDetailsReq)= apiInterface.getAdminBankList(banklistreq)


    suspend fun uploadDocumentForRaisAmountTransferAdminReq(req: RaiseMakePaymentReq): retrofit2.Response<MakePaymentReportResp> {
        val mode = req.Mode.toRequestBody("text/plain".toMediaTypeOrNull())
        val rid = req.RID.toRequestBody("text/plain".toMediaTypeOrNull())
        val referenceId = req.RefrenceID.toRequestBody("text/plain".toMediaTypeOrNull())
        val paymentmode = req.PaymentMode.toRequestBody("text/plain".toMediaTypeOrNull())
        val paymentdate = req.PaymentDate.toRequestBody("text/plain".toMediaTypeOrNull())
        val depositbankname = req.DepositBankName.toRequestBody("text/plain".toMediaTypeOrNull())
        val branchcode = req.BranchCode_ChecqueNo.toRequestBody("text/plain".toMediaTypeOrNull())
        val remarks = req.Remarks.toRequestBody("text/plain".toMediaTypeOrNull())
        val transactionId = req.TransactionID.toRequestBody("text/plain".toMediaTypeOrNull())
        val documentPath = req.DocumentPath.toRequestBody("text/plain".toMediaTypeOrNull())
        val recorddatetime = req.RecordDateTime.toRequestBody("text/plain".toMediaTypeOrNull())
        val updateby = req.UpdatedBy.toRequestBody("text/plain".toMediaTypeOrNull())
        val updateon = req.UpdatedOn.toRequestBody("text/plain".toMediaTypeOrNull())
        val approvedby = req.ApprovedBy.toRequestBody("text/plain".toMediaTypeOrNull())
        val approveddatetime = req.ApprovedDateTime.toRequestBody("text/plain".toMediaTypeOrNull())
        val approvedstatus = req.ApporvedStatus.toRequestBody("text/plain".toMediaTypeOrNull())
        val registrationid = req.RegistrationId.toRequestBody("text/plain".toMediaTypeOrNull())
        val approvedremarks = req.ApporveRemakrs.toRequestBody("text/plain".toMediaTypeOrNull())
        val amount = req.Amount.toRequestBody("text/plain".toMediaTypeOrNull())
        val companycode = req.CompanyCode.toRequestBody("text/plain".toMediaTypeOrNull())
        val beneId = req.BeneId.toRequestBody("text/plain".toMediaTypeOrNull())
        val accoundholder = req.AccountHolder.toRequestBody("text/plain".toMediaTypeOrNull())
        val paymenttype = req.PaymentType.toRequestBody("text/plain".toMediaTypeOrNull())
        val flag = req.Flag.toRequestBody("text/plain".toMediaTypeOrNull())
        val admincode = req.AdminCode.toRequestBody("text/plain".toMediaTypeOrNull())

        // Convert image file to MultipartBody.Part
        val imagePart1 = if (req.imagefile1 != null && req.imagefile1.exists()) {
            val requestFile = req.imagefile1.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("Image1_File", req.imagefile1.name, requestFile)
        } else {
            // send empty multipart field
            MultipartBody.Part.createFormData("Image1_File", "")
        }


        return apiInterface.uploadDocumentForRaisAmountTransferAdmin(
            mode, rid, referenceId,paymentmode,paymentdate, depositbankname, branchcode, remarks,
            transactionId, documentPath, recorddatetime, updateby, updateon,approvedby,approveddatetime,approvedstatus,registrationid
            ,approvedremarks,amount,companycode,beneId,accoundholder,paymenttype,flag,admincode,imagePart1)

    }


    suspend fun putRechargemobileReq(req: UploadRechargeMobileRespReq)= apiInterface.putRechargemobileReq(req)

    suspend fun putRechargemobileResponseReq(req: UploadRechargeMobileRespRespReq)= apiInterface.putRechargemobileResponseReq(req)

    suspend fun putRechargeapiresponseReq(req: RechargeapiresponseReq)= apiInterface.putRechargeapiresponseReq(req)

    suspend fun transferToAgentReq(req: TransferToAgentReq)= apiInterface.getTransferAmountToAgentsForCommission(req)

}