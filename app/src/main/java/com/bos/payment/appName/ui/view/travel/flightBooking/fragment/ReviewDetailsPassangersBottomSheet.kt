package com.bos.payment.appName.ui.view.travel.flightBooking.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.recharge.recharge.TransferToAgentReq
import com.bos.payment.appName.data.model.transferAMountToAgent.TransferAmountToAgentsReq
import com.bos.payment.appName.data.model.transferAMountToAgent.TransferAmountToAgentsRes
import com.bos.payment.appName.data.model.travel.bus.forservicecharge.BusCommissionReq
import com.bos.payment.appName.data.model.travel.bus.forservicecharge.BusCommissionResp
import com.bos.payment.appName.data.model.travel.flight.AirCommissionReq
import com.bos.payment.appName.data.model.travel.flight.AirCommissionResp
import com.bos.payment.appName.data.model.travel.flight.AirRepriceRequests
import com.bos.payment.appName.data.model.travel.flight.AirReprintReq
import com.bos.payment.appName.data.model.travel.flight.AirTicketBookingRequest
import com.bos.payment.appName.data.model.travel.flight.AirTicketBookingResponseRequest
import com.bos.payment.appName.data.model.travel.flight.AirTicketingReq
import com.bos.payment.appName.data.model.travel.flight.AirTicketingResponse
import com.bos.payment.appName.data.model.travel.flight.BookingFlightDetails
import com.bos.payment.appName.data.model.travel.flight.BookingSSRDetails
import com.bos.payment.appName.data.model.travel.flight.FlightAddPaymentReq
import com.bos.payment.appName.data.model.travel.flight.FlightRequeryReq
import com.bos.payment.appName.data.model.travel.flight.FlightTempBookingReq
import com.bos.payment.appName.data.model.travel.flight.FlightsItem
import com.bos.payment.appName.data.model.travel.flight.PaXDetailsFlight
import com.bos.payment.appName.data.model.travel.flight.SegmentsItem
import com.bos.payment.appName.data.model.walletBalance.merchantBal.GetMerchantBalanceReq
import com.bos.payment.appName.data.model.walletBalance.merchantBal.GetMerchantBalanceRes
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.GetBalanceReq
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.GetBalanceRes
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.data.viewModelFactory.TravelViewModelFactory
import com.bos.payment.appName.databinding.AddtravellersitemlayoutBinding
import com.bos.payment.appName.databinding.ContactmobileItemlayoutBinding
import com.bos.payment.appName.databinding.FlightdetailsItemBottomsheetBinding
import com.bos.payment.appName.databinding.GstDetailsLayoutBinding
import com.bos.payment.appName.databinding.ReviewDetailsPassangerItemlayoutBinding
import com.bos.payment.appName.databinding.TravellersclassItemBottomsheetBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.Dashboard.rechargeactivity.RechargeSuccessfulPageActivity.Companion.serviceChargeWithGST
import com.bos.payment.appName.ui.view.travel.adapter.FlightTicketDetailsAdapter
import com.bos.payment.appName.ui.view.travel.adapter.PassangerDataList
import com.bos.payment.appName.ui.view.travel.adapter.PassangerDetailsListShownAdapter
import com.bos.payment.appName.ui.view.travel.adapter.TempBookingPassangerDetails
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.FlightDetails
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.adultCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.calculateTotalFlightDuration
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.childCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.className
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.infantCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.totalCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.totalDurationTime
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.travelType
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.adultList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.childList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.fareList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.flightDetailsPassangerDetail
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.infantList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.paxDetailsListFromReprice
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.segmentListPassangerDetail
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.FlightBookedTicketActivity
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.FlightBookedTicketActivity.Companion.BookedTicketList
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.ui.viewmodel.TravelViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Constants.scanForActivity
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils
import com.bos.payment.appName.utils.Utils.generateRandomNumber
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.bos.payment.appName.utils.Utils.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

class ReviewDetailsPassangersBottomSheet:BottomSheetDialogFragment() {
    private lateinit var binding : ReviewDetailsPassangerItemlayoutBinding

    lateinit var viewModel : TravelViewModel
    private var mStash: MStash? = null

    lateinit var dialogg: Dialog
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel

    companion object {
        const val TAG = "ReviewDetailsBottomSheet"
        var passangerDetailsList: MutableList<PassangerDataList> = mutableListOf()
        var tempBookingPassangerDetails: MutableList<TempBookingPassangerDetails> = mutableListOf()
        var BaseFareAmount : String = ""
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ReviewDetailsPassangerItemlayoutBinding.inflate(inflater, container, false)


        mStash = MStash.getInstance(requireContext())
        viewModel = ViewModelProvider(this, TravelViewModelFactory(TravelRepository(RetrofitClient.apiAllTravelAPI, RetrofitClient.apiBusAddRequestlAPI)))[TravelViewModel::class.java]
        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]
        mStash?.setStringValue(Constants.MerchantId, "AOP-554")


        setonclicklistner()
        setdataonview()
        return binding.root
    }


    fun setdataonview(){
        var adapter = PassangerDetailsListShownAdapter(requireContext(), passangerDetailsList)
        binding.showingpassangerlist.adapter= adapter
        adapter.notifyDataSetChanged()
    }



    private fun setonclicklistner(){

        binding.edittxt.setOnClickListener {
            dialog!!.dismiss()
        }

        binding.confrmtxt.setOnClickListener {
            hitApiForFlightTempBooking()
        }

    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.setOnShowListener { it ->
            val d = it as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                    sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED

                val layoutParams = sheet.layoutParams
                val windowHeight = Resources.getSystem().displayMetrics.heightPixels
                layoutParams.height = windowHeight
                sheet.layoutParams = layoutParams
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }



    fun hitApiForFlightTempBooking(){
        var customMob =  mStash!!.getStringValue(Constants.MobileNumber, "")!!
        var passangermob = tempBookingPassangerDetails[0].passengerMobile
        var passemail = tempBookingPassangerDetails[0].passengerEmail
        var passgst = tempBookingPassangerDetails[0].gst
        var passgstnumber = tempBookingPassangerDetails[0].gsT_Number
        var passgstholdername = tempBookingPassangerDetails[0].gsTHolderName
        var passgstaddress = tempBookingPassangerDetails[0].gsTAddress

        var flightTempBookingreq = FlightTempBookingReq(
            customerMobile = customMob , // for testing purpose
            passengerMobile =passangermob ,
            whatsAPPMobile = passangermob,
            passengerEmail = passemail,
            gst = passgst,
            gsT_Number = passgstnumber,
            gsTHolderName =passgstholdername ,
            gsTAddress = passgstaddress,
            costCenterId = 0,
            projectId = 0,
            bookingRemark = "",
            corporateStatus = 0,
            corporatePaymentMode = 0,
            missedSavingReason = "",
            corpTripType = "",
            corpTripSubType = "",
            tripRequestId = "",
            bookingAlertIds = "",
            iPAddress = mStash?.getStringValue(Constants.deviceIPAddress, ""),
            requestId =  mStash?.getStringValue(Constants.requestId, ""),
            imeINumber = "0054748569",
            registrationID = mStash?.getStringValue(Constants.MerchantId, ""),
            bookingFlightDetails = getBookingFlightDetails(),
            paX_Details = getpaxDetails()
        )

        Log.d("FlightTempBookingReq", Gson().toJson(flightTempBookingreq))

        viewModel.getFlightTempBookingRequest(flightTempBookingreq).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                if(Constants.dialog!=null && Constants.dialog.isShowing){
                                    Constants.dialog.dismiss()
                                }
                                Log.d("tempresp",Gson().toJson(response))

                                Log.d("FlightTempResponse", response.responseHeader.errorCode)

                                mStash!!.setStringValue(Constants.BookingRefNo,response.bookingRefNo)

                                if(response.responseHeader.errorCode.equals("0000")){
                                    // check commission slab ....................................

                                    getCommissionRequestRetailer()

                                    //getAllWalletBalance()
                                }

                                if(response.responseHeader.errorCode.equals("0004")){

                                    Toast.makeText(requireContext(),response.responseHeader.errorDesc,Toast.LENGTH_SHORT).show()
                                }

                                if(response.responseHeader.errorCode.equals("0006")){

                                    Toast.makeText(requireContext(),response.responseHeader.errorDesc,Toast.LENGTH_SHORT).show()
                                }

                                Log.d("FlightRePriseResponse", Gson().toJson(response))

                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                            Constants.dialog.dismiss()
                        }
                    }

                    ApiStatus.LOADING -> {
                        Constants.OpenPopUpForVeryfyOTP(requireContext())
                    }
                }
            }
        }

    }


    private fun getAllWalletBalance() {
        requireContext().runIfConnected {
            val walletBalanceReq = GetBalanceReq(
                parmUser = mStash!!.getStringValue(Constants.RegistrationId, ""),
                flag = "CreditBalance"
            )
            getAllApiServiceViewModel.getWalletBalance(walletBalanceReq)
                .observe(viewLifecycleOwner) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                it.data?.let { users ->
                                    users.body()?.let { response ->
                                        getAllWalletBalanceRes(response)
                                    }
                                }
                            }

                            ApiStatus.ERROR -> {
                                if(Constants.dialog!=null && Constants.dialog.isShowing){
                                    Constants.dialog.dismiss()
                                }
                            }

                            ApiStatus.LOADING -> {
                                Constants.OpenPopUpForVeryfyOTP(requireContext())
                            }
                        }
                    }
                }
        }
    }


    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun getAllWalletBalanceRes(response: GetBalanceRes) {
        if (response.isSuccess == true) {
            val mainBalance = (response.data[0].result!!.toDoubleOrNull() ?: 0.0)
            val totalamoutFlightTicket =   mStash!!.getStringValue(Constants.AirTotalTicketPrice, "")!!.toDoubleOrNull() ?: 0.0

            if(totalamoutFlightTicket<= mainBalance){
                getMerchantBalance(totalamoutFlightTicket)
            }
            else{
                if(Constants.dialog!=null && Constants.dialog.isShowing){
                    Constants.dialog.dismiss()
                }
                Toast.makeText(requireContext(), "Wallet balance is low. VBal = $mainBalance,  totalAmt = $totalamoutFlightTicket", Toast.LENGTH_LONG).show()
            }

            Log.d("actualBalance", "main = $mainBalance")

        } else {
            toast(response.returnMessage.toString())
            if(Constants.dialog!=null && Constants.dialog.isShowing){
                Constants.dialog.dismiss()
            }
        }

    }


    private fun getMerchantBalance(flightBalance: Double) {
        val getMerchantBalanceReq = GetMerchantBalanceReq(
            parmUser = mStash!!.getStringValue(Constants.MerchantId, "")/*"AOP-554"*/, // for testing purpose only
            flag = "DebitBalance"
        )
        getAllApiServiceViewModel.getAllMerchantBalance(getMerchantBalanceReq)
            .observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    getAllMerchantBalanceRes(response, flightBalance)
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            if(Constants.dialog!=null && Constants.dialog.isShowing){
                                Constants.dialog.dismiss()
                            }
                        }

                        ApiStatus.LOADING -> {
                        }
                    }
                }
            }
    }


    private fun getAllMerchantBalanceRes(response: GetMerchantBalanceRes, flightBalance: Double) {
        if (response.isSuccess == true) {
            Log.d(ContentValues.TAG, "getAllMerchantBalanceRes: ${response.data[0].debitBalance}")
            mStash!!.setStringValue(Constants.merchantBalance, response.data[0].debitBalance)

            val merchantBalance = response.data[0].debitBalance?.toDoubleOrNull() ?: 0.0
            val totalamoutFlightTicket =   mStash!!.getStringValue(Constants.AirTotalTicketPrice, "")!!.toDoubleOrNull() ?: 0.0

            if (totalamoutFlightTicket <= merchantBalance) {

                HitApiForFlightAddPayment()
            }
            else {
                if(Constants.dialog!=null && Constants.dialog.isShowing){
                    Constants.dialog.dismiss()
                }
                Toast.makeText(requireContext(), "Booking is currently unavailable due to a technical issue. Please try again shortly.", Toast.LENGTH_LONG).show()
            }
        }
        else {
            if(Constants.dialog!=null && Constants.dialog.isShowing){
                Constants.dialog.dismiss()
            }
            Toast.makeText(requireContext(), response.returnMessage.toString(), Toast.LENGTH_SHORT)
                .show()
        }

    }


    fun HitApiForFlightAddPayment(){
        val requestId = Constants.generateRequestId()

        val flightaddpaymentreq = FlightAddPaymentReq(
            clientRefNo = requestId,
            refNo =  mStash!!.getStringValue(Constants.BookingRefNo,""),
            transactionType = 0,
            productId = "1",
            iPAddress = mStash?.getStringValue(Constants.deviceIPAddress, ""),
            requestId =  mStash?.getStringValue(Constants.requestId, ""),
            imeINumber = "2232323232323",
            registrationID = mStash?.getStringValue(Constants.MerchantId, "")
        )

        Log.d("AddPaymentReq", Gson().toJson(flightaddpaymentreq))

        viewModel.getFlightAddPaymentRequest(flightaddpaymentreq)
            .observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->

                                    if(response.responseHeader!!.errorCode.equals("204")){
                                        HitApiForFlightAddPayment()
                                    }

                                    if(response.responseHeader!!.errorCode.equals("0000")) {
                                        Log.d("AddPaymentResponse",response.paymentID!!)
                                        hitApiForAirTicketing()
                                    }

                                    else{
                                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                                            Constants.dialog.dismiss()
                                        }
                                        Toast.makeText(requireContext(),response.responseHeader.errorDesc,Toast.LENGTH_SHORT).show()
                                    }

                                    Log.d("AddPaymentResp",Gson().toJson(response))
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            if(Constants.dialog!=null && Constants.dialog.isShowing){
                                Constants.dialog.dismiss()
                            }
                        }

                        ApiStatus.LOADING -> {

                        }

                    }
                }
            }

    }


    fun hitApiForAirTicketing(){
        val airTicketingreq = AirTicketingReq(
            BookingRefNo = mStash!!.getStringValue(Constants.BookingRefNo,""),
            TicketingType =  "1",
            iPAddress = mStash?.getStringValue(Constants.deviceIPAddress, ""),
            requestId =  mStash?.getStringValue(Constants.requestId, ""),
            imeINumber = "2232323232323",
            registrationID = mStash?.getStringValue(Constants.MerchantId, "")
        )

        Log.d("airticketreq", Gson().toJson(airTicketingreq) )

        hitApiForUploadAirTicketingRequest(airTicketingreq)



    }


    fun hitApiForUploadAirTicketingRequest(apiresponse:AirTicketingReq){
        //val requestId = generateRandomNumber()
        val req = AirTicketBookingRequest(
            bookingRefNo = mStash!!.getStringValue(Constants.BookingRefNo,""),
            ticketingType = "1",
            loginID = mStash?.getStringValue(Constants.requestId, ""),
            imeINumber = "0054748569",
            createdBy = mStash!!.getStringValue(Constants.RegistrationId, ""),
            apiResponse = Gson().toJson(apiresponse),
            registrationID = mStash?.getStringValue(Constants.MerchantId, ""),
            requestId = mStash?.getStringValue(Constants.requestId, ""),
            iPAddress = mStash?.getStringValue(Constants.deviceIPAddress, ""),
        )

        Log.d("UploadAirTicketReq",Gson().toJson(req))

        getAllApiServiceViewModel.uploadAirBookingTicketRequest(req).observe(this) { resource ->
            when (resource.apiStatus) {
                ApiStatus.SUCCESS -> {
                    val response = resource.data?.body()
                    Log.d("UploadAirTicketResp",Gson().toJson(response))
                    Log.d("AirTicketingReq",Gson().toJson(apiresponse))
                    uploadDataForAirTicketing(apiresponse)
                }
                ApiStatus.ERROR -> if(Constants.dialog!=null && Constants.dialog.isShowing){
                    Constants.dialog.dismiss()
                }
                ApiStatus.LOADING -> {}
            }
        }

    }

    fun uploadDataForAirTicketing(apiresponse:AirTicketingReq){
        viewModel.getAirTicketingRequest(apiresponse).observe(viewLifecycleOwner) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("airticketingresponse",response.toString())

                                if(response.responseHeader.statusId.equals("22")){
                                    Toast.makeText(context,response.responseHeader.errorDesc,Toast.LENGTH_LONG).show()
                                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                                        Constants.dialog.dismiss()
                                    }
                                }
                                else{
                                    hitApiForUploadAirTicketingResponseRequest(response)
                                }

                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                            Constants.dialog.dismiss()
                        }
                    }

                    ApiStatus.LOADING -> {

                    }

                }
            }
        }
    }

    fun hitApiForUploadAirTicketingResponseRequest(apiresponse:AirTicketingResponse){

        var apiresponsevalue = Gson().toJson(apiresponse)

        val req = AirTicketBookingResponseRequest(
            bookingRefNo = apiresponse.bookingRefNo,
            supplierRefno =apiresponse.airlinePNRDetails!![0].airlinePNRs!![0].supplierRefNo ,
            airlineCode=apiresponse.airlinePNRDetails!![0].airlinePNRs!![0].airlineCode,
            loginID = mStash?.getStringValue(Constants.requestId, ""),
            holdValidity = apiresponse.airlinePNRDetails!![0].holdValidity,
            failureRemark = apiresponse.airlinePNRDetails!![0].failureRemark,
            airlinePNR = apiresponse.airlinePNRDetails!![0].airlinePNRs!![0].airlinePNR,
            flightId = apiresponse.airlinePNRDetails!![0].flightId,
            statusId = apiresponse.responseHeader.statusId,
            errorCode = apiresponse.responseHeader.errorCode,
            createdBy = mStash!!.getStringValue(Constants.RegistrationId, ""),
            crSPNR = apiresponse.airlinePNRDetails!![0].airlinePNRs!![0].crSPNR,
            apiResponse = apiresponsevalue,
            errorDesc = apiresponse.responseHeader.errorDesc,
            registrationID = mStash?.getStringValue(Constants.MerchantId, ""),
            errorInnerException = apiresponse.responseHeader.errorInnerException,
            requestId = mStash?.getStringValue(Constants.requestId, ""),
            iPAddress = mStash?.getStringValue(Constants.deviceIPAddress, ""),
            recordLocator = apiresponse.airlinePNRDetails!![0].airlinePNRs!![0].recordLocator,
            crSCode = apiresponse.airlinePNRDetails!![0].airlinePNRs!![0].crSCode,
        )

        Log.d("UploadAirTicketResponseReq",Gson().toJson(req))

        getAllApiServiceViewModel.uploadAirBookingTicketResponseRequest(req).observe(this) { resource ->
            when (resource.apiStatus) {
                ApiStatus.SUCCESS -> {
                    val response = resource.data?.body()
                    Log.d("UploadAirTicketResponseResp",Gson().toJson(response))
                    if (response != null && response.isSuccess!!) {
                        if(apiresponse.responseHeader.errorCode.equals("0000")){

                            getTransferAmountToAgentWithCal()

                            val airTicketReprintreq = AirReprintReq(
                                BookingRefNo = apiresponse.bookingRefNo,
                                airlinePNR =  apiresponse.airlinePNRDetails!![0].airlinePNRs!![0].airlinePNR,
                                ipAddress = mStash?.getStringValue(Constants.deviceIPAddress, ""),
                                requestId =  mStash?.getStringValue(Constants.requestId, ""),
                                imeNumber = "0054748569",
                                registerId = mStash?.getStringValue(Constants.MerchantId, "")/*"AOP-554"*/
                            )

                            hitApiForTicketReprint(airTicketReprintreq)
                        }

                        else{
                            if(Constants.dialog!=null && Constants.dialog.isShowing){
                                Constants.dialog.dismiss()
                            }
                            Toast.makeText(context,apiresponse.responseHeader.errorInnerException,Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {

                    }
                }
                ApiStatus.ERROR -> if(Constants.dialog!=null && Constants.dialog.isShowing){
                    Constants.dialog.dismiss()
                }
                ApiStatus.LOADING -> {}
            }
        }

    }

    fun hitApiForTicketReprint(airReprintTicketReq:AirReprintReq){

        Log.d("ticketreprintreq", Gson().toJson(airReprintTicketReq))

        viewModel.getAirTicketReprintRequest(airReprintTicketReq)
            .observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    BookedTicketList=response

                                    var requeryReq = FlightRequeryReq(
                                        loginId = mStash!!.getStringValue(Constants.RegistrationId, ""),
                                        bookingRefNo = response.bookingRefNo,
                                        ipAddress = mStash?.getStringValue(Constants.deviceIPAddress, ""),
                                        requestId = mStash?.getStringValue(Constants.requestId, ""),
                                        imeinumber = "0054748569",
                                        registrationID = mStash!!.getStringValue(Constants.RegistrationId, ""),
                                        airPnr = response.airPNRDetails!![0]?.airlinePNR,
                                        flightNumber = response.airPNRDetails[0]!!.flights!![0].flightId, //flight_Id
                                        travelDate = response.airPNRDetails[0]!!.flights!![0].travelDate,
                                        ticketStatusId = response.airPNRDetails[0]!!.ticketStatusId,
                                        ticketStatusDesc = response.airPNRDetails[0]!!.ticketStatusDesc,
                                        apiResponse = Gson().toJson(response),
                                        createdBy = mStash!!.getStringValue(Constants.RegistrationId, ""),
                                        destination = response.airPNRDetails[0]!!.flights!![0].destination,
                                        origin = response.airPNRDetails[0]!!.flights!![0].origin
                                     )

                                    Log.d("ticketrequeryreq",Gson().toJson(requeryReq))
                                    hitApiForRequeryRequest(requeryReq)
                                    Log.d("ticketreprintresponse",response.bookingRefNo)

                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            if(Constants.dialog!=null && Constants.dialog.isShowing){
                                Constants.dialog.dismiss()
                            }
                        }

                        ApiStatus.LOADING -> {

                        }

                    }
                }
            }
    }

    fun getBookingFlightDetails() : MutableList<BookingFlightDetails>{
        var flightsearchkey = mStash!!.getStringValue(Constants.FlightSearchKey,"")
        var flightkey = mStash?.getStringValue(Constants.FlightKey,"")
        var bookingFlightDetails: MutableList<BookingFlightDetails> = mutableListOf()
        var bookingSSRLits : MutableList<BookingSSRDetails> = mutableListOf()
        bookingFlightDetails.clear()

        bookingFlightDetails.add(
            BookingFlightDetails(searchKey = flightsearchkey.toString(),
                flightKey = flightkey,
                bookingSSRDetails = bookingSSRLits,
                )
        )

        return bookingFlightDetails
    }

    fun getpaxDetails(): MutableList<PaXDetailsFlight> {
        val paxDetailsList = mutableListOf<PaXDetailsFlight>()

        passangerDetailsList.forEachIndexed { index, passenger ->

            val paxType = when (passenger.passangerType.uppercase()) {
                "ADULT" -> 0
                "CHILD" -> 1
                "INFANT" -> 2
                else -> 0 // fallback default
            }

            val genderCode = when (passenger.gender.trim().lowercase()) {
                "male" -> 0
                "female" -> 1
                else -> 0 // default to Male
            }

            // Basic validation (adjust as needed)
            if (passenger.firstName.isNullOrBlank() || passenger.lastName.isNullOrBlank()) {
                // Skip invalid entries
                return@forEachIndexed
            }

            val pax = PaXDetailsFlight(
                pax_Id = index + 1, // 1-based index
                paxtype = paxType,
                title = passenger.title ?: "",
                firstName = passenger.firstName ?: "",
                lastName = passenger.lastName ?: "",
                gender = genderCode,
                age = "", // optional, can calculate if needed
                dob = passenger.dob ?: "",
                passportNumber = passenger.passportno ?: "",
                passportIssuingCountry = passenger.passportissuecountryname ?: "",
                passportExpiry = passenger.passportexpirydate ?: "",
                nationality = "",
                frequentFlyerDetails = ""
            )

            paxDetailsList.add(pax)
        }

        return paxDetailsList
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
       // (activity as? FlightMainActivity)?.setData()

        /*if(context is FlightMainActivity){
            (context as? FlightMainActivity)?.setData()
        }
        else {*/
            (scanForActivity(context)?.supportFragmentManager?.findFragmentByTag("FlightMainFragment") as? FlightMainFragment)?.setData()
       // }

    }

    fun hitApiForRequeryRequest(requeryReq : FlightRequeryReq){
        getAllApiServiceViewModel.getAirRequeryRequest(requeryReq)
            .observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                                        Constants.dialog.dismiss()
                                    }
                                    Constants.uploadDataOnFirebaseConsole(Gson().toJson(response),"ReviewDetailsBottomSheetRequeryRequest",requireContext())

                                    Log.d("ticketrequeryresp",Gson().toJson(requeryReq))
                                    startActivity(Intent(requireContext(),FlightBookedTicketActivity::class.java))
                                    Toast.makeText(context,"Reprint Success",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            if(Constants.dialog!=null && Constants.dialog.isShowing){
                                Constants.dialog.dismiss()
                            }
                        }

                        ApiStatus.LOADING -> {

                        }

                    }
                }
            }
        }

    private fun getCommissionRequestRetailer(){
        var adminCode =  mStash!!.getStringValue(Constants.AdminCode,"")
        var retailerId =  mStash!!.getStringValue(Constants.RegistrationId,"")
        var airCategory = ""
        if(travelType==0){
            airCategory = "DOMESTIC"
        }
        else{
            airCategory = "INTERNATIONAL"
        }
        var operatorId =  mStash!!.getStringValue(FlightConstant.airlinecode,"")

        var rechargeAmount = mStash!!.getStringValue(Constants.AirTotalTicketPrice, "")

        hitCommissionAPIRetailer(operatorId!!, retailerId!!, adminCode!!, airCategory, rechargeAmount!!)

    }

    fun hitCommissionAPIRetailer(operatorID: String, retailerId: String, adminCode: String, airCategory: String, rechargeAmount: String) {

        val req = AirCommissionReq(
            productSource = "F0134",
            retailerType = "SPECIFIC",
            airCategory = airCategory,
            adminCode = adminCode,
            userType = "B2B",
            retailerID = retailerId,
            operatorID = operatorID,
        )

        Log.d("RetailerFlightCommissionReq",Gson().toJson(req))

        getAllApiServiceViewModel.getFlightCommissionRequest(req).observe(this) { resource ->
            when (resource.apiStatus) {
                ApiStatus.SUCCESS -> {
                    val response = resource.data?.body()

                    Log.d("RetailerCommissionResp",Gson().toJson(response))
                    if (response != null && response.isSuccess!!) {
                        getAllServiceChargeApiResRetailer(response, rechargeAmount)
                    }
                    else {

                        // Save commission types in shared preferences
                        with(mStash!!) {
                            setStringValue(Constants.retailer_CommissionType, "")
                            setStringValue(Constants.serviceType, "")
                        }

                        mStash!!.setStringValue(Constants.retailerCommissionWithoutTDS, String.format("%.2f", 0.0))
                        mStash!!.setStringValue(Constants.retailerCommission, String.format("%.2f", 0.0))
                        mStash!!.setStringValue(Constants.tds, String.format("%.2f", 0.0))

                        val totalRechargeAmount = (rechargeAmount.toDoubleOrNull() ?: 0.0) + 0.0
                        mStash!!.setStringValue(Constants.totalTransaction, String.format("%.2f", totalRechargeAmount))

                        serviceChargeWithGST = mStash!!.getStringValue(Constants.serviceChargeWithGST, "")!!
                        mStash!!.setStringValue(Constants.gst, String.format("%.2f", 0.0))
                        mStash!!.setStringValue(Constants.serviceCharge, String.format("%.2f", 0.0))
                        mStash!!.setStringValue(Constants.retailerCommission, String.format("%.2f", 0.0))
                        mStash!!.setStringValue(Constants.tds, String.format("%.2f", 0.0))

                        var msg = "Warning : Slab structure not found. This transaction will proceed without any commission being credited."
                        openDialogForPayout(rechargeAmount.toDoubleOrNull() ?: 0.0, 0.0, totalRechargeAmount, 0.0, msg)

                    }
                }
                ApiStatus.ERROR -> if(Constants.dialog!=null && Constants.dialog.isShowing){
                    Constants.dialog.dismiss()
                }
                ApiStatus.LOADING -> {}
            }
        }

    }

    private fun getAllServiceChargeApiResRetailer(response: AirCommissionResp, rechargeAmount: String) {
        if (response.isSuccess!!) {
            // Parse values safely
            val rechargeAmountValue = rechargeAmount.toDoubleOrNull() ?: 0.0
            val retailerCommission = response!!.data!![0]?.commissionValue ?: 0.0

            val TDSTax = 5.0 // Fixed TDS rate

            // Function to calculate commission with TDS
            fun calculateCommission(amount: Double, type: String?, tdsRate: Double): Double {
                return when {
                    "percentage".equals(type, ignoreCase = true) -> {
                        val commissionAmount = rechargeAmountValue * (amount / 100)
                        val tdsAmount = commissionAmount * (tdsRate / 100)

                        mStash!!.setStringValue(Constants.retailerCommission, String.format("%.2f", commissionAmount))
                        mStash!!.setStringValue(Constants.tds, String.format("%.2f", tdsAmount))

                        commissionAmount - tdsAmount
                    }

                    "amount".equals(type, ignoreCase = true) -> {
                        val tdsAmount = amount * (tdsRate / 100)

                        mStash!!.setStringValue(Constants.retailerCommission, String.format("%.2f", amount))
                        mStash!!.setStringValue(Constants.tds, String.format("%.2f", tdsAmount))

                        amount - tdsAmount
                    }

                    else -> {
                        mStash!!.setStringValue(Constants.retailerCommission, "0.0")
                        mStash!!.setStringValue(Constants.tds, "0.0")
                        0.0
                    }
                }
            }

            // Calculate retailer commission
            var type =  response.data!![0]?.commissionType!!.trim()?.lowercase() ?: ""
            val finalRetailerCommission = calculateCommission(retailerCommission, type, TDSTax)

            // Log results
            Log.d("FinalRetailerCommission", String.format("%.2f", finalRetailerCommission))


            // Service charge calculation
            val gst = 18.0 // Fixed GST rate of 18%
            val serviceCharge = response!!.data!![0]?.servicesValue ?: 0.0

            val totalServiceChargeWithGst = serviceChargeCalculation(serviceCharge, gst, rechargeAmount, response)
            Log.d("servicechargewithgst", String.format("%.2f", totalServiceChargeWithGst))

//              Calculating the total recharge amount
            val totalRechargeAmount = (rechargeAmount.toDoubleOrNull() ?: 0.0) + totalServiceChargeWithGst
            Log.d("rechargeAmount", String.format("%.2f", totalRechargeAmount))

            // Save commission types in shared preferences
            with(mStash!!) {
                setStringValue(Constants.retailer_CommissionType, response.data!![0]?.commissionType.toString())
                setStringValue(Constants.serviceType, response.data!![0]?.servicesType.toString())
            }

            mStash!!.setStringValue(Constants.retailerCommissionWithoutTDS, String.format("%.2f", finalRetailerCommission))

            mStash!!.setStringValue(Constants.totalTransaction, String.format("%.2f", totalRechargeAmount))

            openDialogForPayout(rechargeAmount.toDoubleOrNull() ?: 0.0, totalServiceChargeWithGst, totalRechargeAmount, mStash!!.getStringValue(Constants.retailerCommission, "")!!.toDoubleOrNull()!!, "")

        }
        else {

            // Save commission types in shared preferences
            with(mStash!!) {
                setStringValue(Constants.retailer_CommissionType, "")
                setStringValue(Constants.serviceType, "")
            }

            mStash!!.setStringValue(Constants.retailerCommissionWithoutTDS, String.format("%.2f", 0.0))
            mStash!!.setStringValue(Constants.retailerCommission, String.format("%.2f", 0.0))
            mStash!!.setStringValue(Constants.tds, String.format("%.2f", 0.0))

            val totalRechargeAmount = (rechargeAmount.toDoubleOrNull() ?: 0.0) + 0.0
            mStash!!.setStringValue(Constants.totalTransaction, String.format("%.2f", totalRechargeAmount))

            serviceChargeWithGST = mStash!!.getStringValue(Constants.serviceChargeWithGST, "")!!
            mStash!!.setStringValue(Constants.gst, String.format("%.2f", 0.0))
            mStash!!.setStringValue(Constants.serviceCharge, String.format("%.2f", 0.0))
            mStash!!.setStringValue(Constants.retailerCommission, String.format("%.2f", 0.0))
            mStash!!.setStringValue(Constants.tds, String.format("%.2f", 0.0))

            var msg = "Warning : Slab structure not found. This transaction will proceed without any commission being credited."
            openDialogForPayout(rechargeAmount.toDoubleOrNull() ?: 0.0, 0.0, totalRechargeAmount, 0.0, msg)

        }

    }

    private fun serviceChargeCalculation(serviceCharge: Double, gstRate: Double, rechargeAmount: String, response: AirCommissionResp): Double {

        val rechargeAmountValue = rechargeAmount.toDoubleOrNull() ?: 0.0
        var type =  response.data!![0]?.servicesType!!.trim()?.lowercase() ?: ""
        val totalAmountWithGst = when (type) {
            "amount" -> {
                // Service charge is a fixed amount
                val serviceChargeWithGst = serviceCharge * (gstRate / 100)
                mStash!!.setStringValue(Constants.gst, String.format("%.2f", serviceChargeWithGst))
                mStash!!.setStringValue(Constants.serviceCharge, String.format("%.2f", serviceCharge))
                serviceCharge + serviceChargeWithGst
            }

            "percentage" -> {
                // Service charge is a percentage of the recharge amount
                val serviceInAmount = rechargeAmountValue * (serviceCharge / 100)
                val serviceWithGst = serviceInAmount * (gstRate / 100)
                mStash!!.setStringValue(Constants.gst, String.format("%.2f", serviceWithGst))
                mStash!!.setStringValue(Constants.serviceCharge, String.format("%.2f", serviceInAmount))
                serviceInAmount + serviceWithGst

            }
            else -> {
                mStash!!.setStringValue(Constants.gst, String.format("%.2f", 0.0))
                mStash!!.setStringValue(Constants.serviceCharge, String.format("%.2f", 0.0))
                0.0
            }
        }

        mStash!!.setStringValue(Constants.serviceChargeWithGST, String.format("%.2f", totalAmountWithGst))

        Log.d("gstamount", mStash!!.getStringValue(Constants.gst, "").toString())
        Log.d("servicecharge", mStash!!.getStringValue(Constants.serviceCharge, "").toString())
        Log.d("totalAmountWithGst", mStash!!.getStringValue(Constants.serviceChargeWithGST, "").toString())

        // Return the total service charge (with GST) to include in the final transaction
        return totalAmountWithGst
    }

    fun openDialogForPayout(transferAmount: Double, servicechargeGst: Double, totalRechargeAmount: Double, retailerCommission: Double, msg: String) {
        dialogg = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialogg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogg.setContentView(R.layout.busticketcommissionlayout)

        dialogg.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        dialogg.setCanceledOnTouchOutside(false)

        val transferamttxt = dialogg.findViewById<TextView>(R.id.actualamt)
        val servicechargewithgst = dialogg.findViewById<TextView>(R.id.servicechargewithgst)
        val warningmsg = dialogg.findViewById<TextView>(R.id.warningmsg)
        val transferamt = dialogg.findViewById<TextView>(R.id.transferamt)
        val operatorgst = dialogg.findViewById<TextView>(R.id.operatorgst)
        val flightbasefareamount = dialogg.findViewById<TextView>(R.id.flightbasefareamount)
        val serviceChargeamount = dialogg.findViewById<TextView>(R.id.servicescharge)
        val retailercommission = dialogg.findViewById<TextView>(R.id.retailercommission)
        val gstamount = dialogg.findViewById<TextView>(R.id.gstamount)
        val basefaretxt = dialogg.findViewById<TextView>(R.id.flightbasefaretxt)
        val cancel = dialogg.findViewById<ImageView>(R.id.cancel)
        val done = dialogg.findViewById<LinearLayout>(R.id.Proceedbtn)
        val viewBreakLayout = dialogg.findViewById<LinearLayout>(R.id.viewbreaklayout)
        val servicechargelayout = dialogg.findViewById<LinearLayout>(R.id.servicechargelayout)
        val detailsgstserviceslayout = dialogg.findViewById<LinearLayout>(R.id.chargesdetailslayout)
        val retailercommissionlayout = dialogg.findViewById<LinearLayout>(R.id.retailercommissionlayout)
        val seaterlayout = dialogg.findViewById<LinearLayout>(R.id.seaterlayout)
        val sleeperlayout = dialogg.findViewById<LinearLayout>(R.id.sleeperlayout)
        val flightfarelayout = dialogg.findViewById<LinearLayout>(R.id.flightbasefarelayout)

        seaterlayout.visibility=View.GONE
        sleeperlayout.visibility=View.GONE
        flightfarelayout.visibility=View.VISIBLE

        if (msg.isNotEmpty()) {
            warningmsg.visibility = View.VISIBLE
            warningmsg.text = msg
        }
        else {
            warningmsg.visibility = View.GONE
        }

        val gst = mStash!!.getStringValue(Constants.gst, "")

        mStash!!.setStringValue(Constants.serviceChargewithgst, String.format("%.2f", servicechargeGst)).toString()
        mStash!!.setStringValue(Constants.actualbusticketamt, String.format("%.2f", transferAmount)).toString()

        transferamttxt.text = "$transferAmount"
        servicechargewithgst.text = String.format("%.2f", servicechargeGst)
        retailercommission.text = String.format("%.2f", retailerCommission)
        serviceChargeamount.text = mStash!!.getStringValue(Constants.serviceCharge, "").toString()
        gstamount.text = "$gst"

        var totalcount = adultCount + childCount + infantCount
        basefaretxt.text = "Base Fare (${totalcount})"


       var flightOperator =  mStash!!.getStringValue(Constants.AirTotalOperatorPrice, "")!!.toDoubleOrNull() ?: 0.0
       var basicFare =  mStash!!.getStringValue(Constants.AirTotalBasicPrice, "")!!.toDoubleOrNull() ?: 0.0

        flightbasefareamount.text = String.format("%.2f", basicFare)
        operatorgst.text = String.format("%.2f", flightOperator)
        transferamt.text = String.format("%.2f", totalRechargeAmount)

        if(servicechargeGst==0.0){
            servicechargelayout.visibility=View.GONE
        }
        else{
            servicechargelayout.visibility=View.VISIBLE
        }

        if(retailerCommission==0.0){
            retailercommissionlayout.visibility = View.GONE
        }
        else{
            retailercommissionlayout.visibility = View.VISIBLE
        }

        var checkView: Boolean = false

        viewBreakLayout.setOnClickListener {
            if (checkView) {
                detailsgstserviceslayout.visibility = View.GONE
                checkView = false
            } else {
                detailsgstserviceslayout.visibility = View.VISIBLE
                checkView = true
            }
            if(Constants.dialog!=null && Constants.dialog.isShowing){
                Constants.dialog.dismiss()
            }
        }

        done.setOnClickListener {
            if (totalRechargeAmount > 0) {
                mStash!!.setStringValue(Constants.totalTransaction, String.format("%.2f", totalRechargeAmount))
                mStash!!.setStringValue(Constants.serviceChargeWithGST, String.format("%.2f", servicechargeGst))
                mStash!!.setStringValue(Constants.actualRechargeAmount, String.format("%.2f", transferAmount))
                getAllWalletBalance()
            } else {
                Toast.makeText(requireContext(), "Transfer amount must be greater than the base fare.", Toast.LENGTH_LONG).show()
            }

        }

        cancel.setOnClickListener {
            if (dialogg != null && dialogg.isShowing) {
                dialogg.dismiss()
            }

        }

        dialogg.setOnDismissListener {
            dialogg.dismiss()
        }

        dialogg.show() // ✅ REQUIRED
    }

    private fun getTransferAmountToAgentWithCal() {
        try {
            var transferamt =   mStash!!.getStringValue(Constants.totalTransaction,"")
            var actualamt =  mStash!!.getStringValue(Constants.actualbusticketamt,"").toString()
            var bookingRefId =   mStash!!.getStringValue(Constants.ForPayoutBookingRefId, "")

            val transferAmountToAgentsReq = TransferAmountToAgentsReq(
                transferFrom = mStash!!.getStringValue(Constants.RegistrationId, ""),
                transferTo = "Admin",
                transferAmt = transferamt,
                remark = "Flight Booking  Api",
                transferFromMsg = "Your account has been debited by ₹$transferamt for a flight booking Reference Number: ${bookingRefId}.",
                transferToMsg = "Your account has been credited by ₹$transferamt for a flight booking Reference Number: ${bookingRefId}.",
                amountType = "Payout",
                actualTransactionAmount = actualamt,
                transIpAddress = mStash!!.getStringValue(Constants.deviceIPAddress, ""),
                parmUserName = mStash!!.getStringValue(Constants.RegistrationId, "") ,
                merchantCode = mStash!!.getStringValue(Constants.MerchantId, "") ,
                servicesChargeAmt =mStash!!.getStringValue(Constants.serviceChargewithgst, ""),
                servicesChargeGSTAmt = mStash!!.getStringValue(Constants.gst, ""),
                servicesChargeWithoutGST = mStash!!.getStringValue(Constants.serviceCharge, ""),
                customerVirtualAddress = "",
                retailerCommissionAmt = "0.00",
                retailerId = "",
                paymentMode = "",
                depositBankName = "",
                branchCodeChecqueNo = "",
                apporvedStatus = "Approved",
                registrationId = mStash!!.getStringValue(Constants.RegistrationId, ""),
                benfiid = "",
                accountHolder = "",
                flag = "Y"
            )

            Log.d("getAllGsonFromAPI", Gson().toJson(transferAmountToAgentsReq))
            getAllApiServiceViewModel.getTransferAmountToAgents(transferAmountToAgentsReq)
                .observe(this) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                it.data?.let { users ->
                                    users.body()?.let { response ->

                                        Log.d("BosPayoutTransaction", response.toString())

                                        val commission = mStash!!.getStringValue(Constants.retailerCommission, "0.00")?.trim()?.toDoubleOrNull() ?: 0.0

                                        Log.d("retailercommissionforpayout", commission.toString())

                                        if (commission > 0.0) {
                                            Log.d("checkconditionforcommission", "Commissionmethod")
                                            getTransferAmountToAgentInCommissionCal(response)
                                        }
                                    }
                                }
                            }

                            ApiStatus.ERROR -> {
                                if(Constants.dialog!=null && Constants.dialog.isShowing){
                                    Constants.dialog.dismiss()
                                }
                                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                            }

                            ApiStatus.LOADING -> {

                            }
                        }
                    }
                }
        } catch (e: NumberFormatException) {
            e.printStackTrace()

            Toast.makeText(requireContext(), e.message.toString() + " " + e.localizedMessage?.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    // for payout transaction and commission entry
    private fun getTransferAmountToAgentInCommissionCal(response: TransferAmountToAgentsRes) {
        var withouttdscommissionamount = mStash!!.getStringValue(Constants.retailerCommissionWithoutTDS, "")
        var tdsamount = mStash!!.getStringValue(Constants.tds, "")
        var actualcommission = mStash!!.getStringValue(Constants.retailerCommission, "")
        var bookingRefId =   mStash!!.getStringValue(Constants.ForPayoutBookingRefId, "")

        Log.d("tdsamount", tdsamount.toString())
        Log.d("commissionamount", withouttdscommissionamount.toString())
        Log.d("actualcommission", actualcommission.toString())

        val transferAmountToAgentsReq = TransferToAgentReq(
            merchantCode = mStash!!.getStringValue(Constants.MerchantId, ""),
            transferFrom = "Admin",
            amountType = "Deposit",
            transIpAddress = mStash!!.getStringValue(Constants.deviceIPAddress, ""),
            remark = "Commission Deposit by Flight Api",
            transferTo = mStash!!.getStringValue(Constants.RegistrationId, ""),
            transferToMsg = "Your account has been credited by ₹${withouttdscommissionamount} as commission for the Flight booking of Against Reference Number: ${response.data!!.refTransID} for the Booking Reference Number: ${bookingRefId}",
            gstAmt = 0,
            parmUserName = mStash!!.getStringValue(Constants.RegistrationId, ""),
            servicesChargeGSTAmt = 0,
            servicesChargeWithoutGST = 0,
            actualTransactionAmount = withouttdscommissionamount?.toDouble() ?: 0.0,
            actualCommissionAmt = 0,
            commissionWithoutGST = 0,
            transferFromMsg = "Your account has been debited  by ₹ ${withouttdscommissionamount} as commission for the Flight booking of Against Reference Number: ${response.data!!.refTransID} for the Booking Reference Number: ${bookingRefId}",
            netCommissionAmt = 0,
            tdSAmt = tdsamount?.toDouble() ?: 0.0,
            servicesChargeAmt = 0,
            customerVirtualAddress = "",
            transferAmt = actualcommission?.toDouble() ?: 0.0,
        )

        Log.d("transferAmountToAgentcommissionreq", Gson().toJson(transferAmountToAgentsReq))

        getAllApiServiceViewModel.transferToAgentReq(transferAmountToAgentsReq)
            .observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {

                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { commissionresp ->


                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            if(Constants.dialog!=null && Constants.dialog.isShowing){
                                Constants.dialog.dismiss()
                            }
                            Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                        }

                        ApiStatus.LOADING -> {
                        }

                    }
                }
            }
    }


}