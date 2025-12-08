package com.bos.payment.appName.ui.view.travel.busactivity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.merchant.apiServiceCharge.mobileCharge.GetCommercialReq
import com.bos.payment.appName.data.model.merchant.apiServiceCharge.mobileCharge.GetCommercialRes
import com.bos.payment.appName.data.model.recharge.recharge.TransferToAgentReq
import com.bos.payment.appName.data.model.transferAMountToAgent.TransferAmountToAgentsReq
import com.bos.payment.appName.data.model.transferAMountToAgent.TransferAmountToAgentsRes
import com.bos.payment.appName.data.model.travel.bus.addMoney.BusAddMoneyReq
import com.bos.payment.appName.data.model.travel.bus.addMoney.BusAddMoneyRes
import com.bos.payment.appName.data.model.travel.bus.busBooking.BusTempBookingReq
import com.bos.payment.appName.data.model.travel.bus.busBooking.BusTempBookingRes
import com.bos.payment.appName.data.model.travel.bus.busBooking.PaXDetails
import com.bos.payment.appName.data.model.travel.bus.busRequery.BusRequeryReq
import com.bos.payment.appName.data.model.travel.bus.busRequery.BusRequeryRes
import com.bos.payment.appName.data.model.travel.bus.busRequery.TicketDetailsForGenerateTicket
import com.bos.payment.appName.data.model.travel.bus.busSeatMap.BusSeatMapReq
import com.bos.payment.appName.data.model.travel.bus.busSeatMap.BusSeatMapRes
import com.bos.payment.appName.data.model.travel.bus.busSeatMap.SeatMap
import com.bos.payment.appName.data.model.travel.bus.busTicket.AddTicketReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.AddTicketResponseReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusPaxRequeryResponseReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTampBookTicketResponseRequest
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTampBookingResp
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTempBookingRequest
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketingReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketingRes
import com.bos.payment.appName.data.model.travel.bus.forservicecharge.BusCommissionReq
import com.bos.payment.appName.data.model.travel.bus.forservicecharge.BusCommissionResp
import com.bos.payment.appName.data.model.travel.bus.forservicecharge.DataItem
import com.bos.payment.appName.data.model.travel.bus.forservicecharge.SeattypeModel
import com.bos.payment.appName.data.model.walletBalance.merchantBal.GetMerchantBalanceReq
import com.bos.payment.appName.data.model.walletBalance.merchantBal.GetMerchantBalanceRes
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.GetBalanceReq
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.GetBalanceRes
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.data.viewModelFactory.TravelViewModelFactory
import com.bos.payment.appName.databinding.ActivityBusSeatingBinding
import com.bos.payment.appName.localdb.AppLog
import com.bos.payment.appName.localdb.AppLog.d
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.Dashboard.rechargeactivity.RechargeSuccessfulPageActivity
import com.bos.payment.appName.ui.view.Dashboard.rechargeactivity.RechargeSuccessfulPageActivity.Companion.rechargeStatus
import com.bos.payment.appName.ui.view.Dashboard.rechargeactivity.RechargeSuccessfulPageActivity.Companion.referenceId
import com.bos.payment.appName.ui.view.Dashboard.rechargeactivity.RechargeSuccessfulPageActivity.Companion.serviceChargeWithGST
import com.bos.payment.appName.ui.view.travel.adapter.UserPassengerDetailsAdapter
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.ui.viewmodel.TravelViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.QRCodeGenerator.generateQRPdf
import com.bos.payment.appName.utils.Utils
import com.bos.payment.appName.utils.Utils.calculateAgeFromDOB
import com.bos.payment.appName.utils.Utils.getChangeDateFormat
import com.bos.payment.appName.utils.Utils.getNextNumber
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.bos.payment.appName.utils.Utils.showLoadingDialog
import com.bos.payment.appName.utils.Utils.toast
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Hashtable
import java.util.Locale

class BusSeating : AppCompatActivity() {
    private lateinit var mStash: MStash
    private lateinit var viewModel: TravelViewModel
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    private lateinit var bin: ActivityBusSeatingBinding
    private val selectedSeats = mutableListOf<SeatMap>()
    private var boardingPointText: String? = null
    private val myCalender = Calendar.getInstance()
    val calendar = Calendar.getInstance()
    private var droppingPointText: String? = null
    private var titleText: String? = null
    private var genderText: String? = null
    private var qrText: String? = null
    private var selectedIndex: Int? = -1
    private var age: Int? = 0
    private var qrBitmap: Bitmap? = null
    private lateinit var list: ArrayList<String>
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private lateinit var userPassengerDetailsAdapter: UserPassengerDetailsAdapter
    private lateinit var passengerList: ArrayList<com.bos.payment.appName.data.model.travel.bus.busRequery.PaXDetails>
    var headers: Array<String> = arrayOf("Name", "Gender", "Seat No","Ticket Number")
    var data = mutableListOf<MutableList<String?>>()
    var ticketDetails = mutableListOf<TicketDetailsForGenerateTicket>()
    var forServiceChargeSeatType : MutableList<SeattypeModel> = mutableListOf()
    var rechargeAmount : String =""
    lateinit var dialog: Dialog

    var checkupperandlowerbirth : Boolean = false

    private val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        view.maxDate = System.currentTimeMillis()
        myCalender.add(Calendar.YEAR, -18)
        myCalender[Calendar.YEAR] = year
        myCalender[Calendar.MONTH] = monthOfYear
        myCalender[Calendar.DAY_OF_MONTH] = dayOfMonth
        val minAdultAge: Calendar = GregorianCalendar()
        val minAdultAge1: Calendar = GregorianCalendar()
        minAdultAge.add(Calendar.YEAR, -18)
        minAdultAge1.add(Calendar.YEAR, -61)
        when {
            minAdultAge.before(myCalender) -> {
                Toast.makeText(applicationContext, resources.getString(R.string.min_age_person), Toast.LENGTH_LONG).show()
                bin.passengerDetails.passengerDob.setText("")
            }

            minAdultAge1.after(myCalender) -> {
                Toast.makeText(applicationContext, resources.getString(R.string.max_age_person), Toast.LENGTH_LONG).show()
                bin.passengerDetails.passengerDob.setText("")
            }

            else -> {
                bin.passengerDetails.passengerDob.let {
                    Utils.updateLabel(it, myCalender, "Enter Date of Birth")
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bin = ActivityBusSeatingBinding.inflate(layoutInflater)
        setContentView(bin.root)

        setupUI()
        setupViewModel()
        getAllMappingSeat()
        setDropDown()
        setClickListeners()

    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, TravelViewModelFactory(TravelRepository(RetrofitClient.apiAllTravelAPI, RetrofitClient.apiBusAddRequestlAPI)))[TravelViewModel::class.java]
        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun setClickListeners() {
        bin.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        bin.NextBtn.setOnClickListener {
            validationSeat()
        }

        bin.proceedBtn.setOnClickListener {
            validationBoarding()
        }

        bin.proceedToBookBtn.setOnClickListener {
            //tempbooking .................................
            validationPassenger()
        }

        bin.passengerDetails.passengerDob.parent.requestDisallowInterceptTouchEvent(true)

        bin.passengerDetails.passengerDob.setOnClickListener {
            Toast.makeText(this@BusSeating,"Hii",Toast.LENGTH_SHORT).show()
            Utils.hideKeyboard(this)
            DatePickerDialog(
                this,
                date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        bin.seatSelectBtn.setOnClickListener {
            getAllMappingSeat()
        }

        bin.reviewBookingInclude.backBtn.setOnClickListener {
            onBackPressed()
        }

        bin.reviewBookingInclude.proceedToPay.setOnClickListener {
            // checking  commission slab for ticket booking.........................................
            getCommissionRequestRetailer()
        }

        bin.reviewBookingInclude.showTicketBtn.setOnClickListener {
            getAllBusRequaryTicket()
        }

        bin.printBtn.setOnClickListener {
            toast("Show Toast")
            qrBitmap?.let {
               // createTicketPdf(context = this, fileName = "MyBusTicket_${System.currentTimeMillis()}", qrBitmap = it, ticketData = qrText.toString())
                openDialogForGenerateTicket(context = this,fileName = "MyBusTicket_${System.currentTimeMillis()}",qrBitmap!!,data,ticketDetails)
            }
        }

        bin.confirmBookingLayout.backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private fun getAllBusRequaryTicket() {
        val busRequery = BusRequeryReq(
            bookingRefNo = mStash.getStringValue(Constants.booking_RefNo, ""),
            iPAddress = mStash.getStringValue(Constants.deviceIPAddress, ""),
            requestId = mStash.getStringValue(Constants.requestId, ""),
            imeINumber = "215237488",
            registrationID = mStash.getStringValue(Constants.MerchantId, ""))

        viewModel.getAllBusRequary(busRequery).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                getAllBusRequaryTicketRes(response)
                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                            Constants.dialog.dismiss()
                        }
                    }

                    ApiStatus.LOADING -> {
                        Constants.OpenPopUpForVeryfyOTP(this)
                    }
                }
            }
        }

    }


    private fun getSeatTypes(): List<Pair<String,String>> {
        val types = mutableListOf<Pair<String,String>>()

        val sleeper = forServiceChargeSeatType[0].sleeper
        val seater = forServiceChargeSeatType[0].seater

        if (sleeper != 0) types.add( Pair("2","SLEEPER"))
        if (seater != 0) types.add(Pair("1","SEATER"))

        return types // size 1 (single) or 2 (both)
    }


    // for adding commission and service charge
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale", "SetTextI18n", "SuspiciousIndentation")
    private fun getCommissionRequestRetailer(){
        var adminCode =  mStash.getStringValue(Constants.AdminCode,"")
        var retailerId =  mStash.getStringValue(Constants.RegistrationId,"")
        var buscategory =  forServiceChargeSeatType.get(0).busType

        var seattype = getSeatTypes()

        if (seattype.size == 1) {
            // Only Sleeper or only Seater
            hitCommissionAPIRetailer(seattype[0].second, retailerId!!, adminCode!!, buscategory, rechargeAmount)
        }
        else {
            // Both Sleeper + Seater
            hitBothSeatTypeApiRetailer(seattype, retailerId!!, adminCode!!, buscategory, rechargeAmount)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale", "SetTextI18n", "SuspiciousIndentation")
    fun hitCommissionAPIRetailer(seatType: String, retailerId: String, adminCode: String, busCategory: String, rechargeAmount: String) {

        val req = BusCommissionReq(
            productSource = "F0133",
            seatType = seatType,
            retailerType = "SPECIFIC",
            busCategory = busCategory,
            admincode = adminCode,
            userType = "B2B",
            retailerID = retailerId,
        )
        Log.d("RetailerCommissionReq",Gson().toJson(req))

        viewModel.getBusCommissionRequest(req).observe(this) { resource ->
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
                ApiStatus.LOADING ->  Constants.OpenPopUpForVeryfyOTP(this)
            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale", "SetTextI18n", "SuspiciousIndentation")
    fun hitCommissionAPIRetailerForBothCondition(seatType: String, retailerId: String, adminCode: String, busCategory: String, rechargeAmount: String  , callback: ApiCallback<BusCommissionResp>) {

        val req = BusCommissionReq(
            productSource = "F0133",
            seatType = seatType,
            retailerType = "SPECIFIC",
            busCategory = busCategory,
            admincode = adminCode,
            userType = "B2B",
            retailerID = retailerId,
        )
        Log.d("RetailerCommissionReq",Gson().toJson(req))

        viewModel.getBusCommissionRequest(req).observe(this) { resource ->
            when (resource.apiStatus) {
                ApiStatus.SUCCESS -> {
                    val response = resource.data?.body()
                    if (response != null && response.isSuccess == true) {
                        callback.onSuccess(response)   // 🔥 return data
                    } else {
                        callback.onError("Slab not found") // 🔥 return error
                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                            Constants.dialog.dismiss()
                        }
                    }
                   /* val response = resource.data?.body()
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

                    }*/
                }
                ApiStatus.ERROR ->  if(Constants.dialog!=null && Constants.dialog.isShowing){
                    Constants.dialog.dismiss()
                }
                ApiStatus.LOADING ->    Constants.OpenPopUpForVeryfyOTP(this)
            }
        }

    }



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale", "SetTextI18n", "SuspiciousIndentation")
    fun hitBothSeatTypeApiRetailer(seatTypes: List<Pair<String,String>>, retailerId: String, adminCode: String, busCategory: String, rechargeAmount: String) {

        var commissionResp1: BusCommissionResp? = null
        var commissionResp2: BusCommissionResp? = null


        fun tryMerge() {
            if (commissionResp1 != null && commissionResp2 != null) {
                mergeAndCalculateRetailer(commissionResp1!!, commissionResp2!!, rechargeAmount)
            }
        }


        hitCommissionAPIRetailerForBothCondition(seatTypes[0].second, retailerId, adminCode, busCategory, rechargeAmount,object : ApiCallback<BusCommissionResp>{
            override fun onSuccess(response: BusCommissionResp) {
                commissionResp1 = response
                Log.d("RetailerBothCommissionResp", Gson().toJson(response))
                tryMerge()
            }

            override fun onError(message: String) {
                if(Constants.dialog!=null && Constants.dialog.isShowing){
                    Constants.dialog.dismiss()
                }
            }

        })


        hitCommissionAPIRetailerForBothCondition(seatTypes[1].second, retailerId, adminCode, busCategory, rechargeAmount,object :ApiCallback<BusCommissionResp>{
            override fun onSuccess(response: BusCommissionResp) {
                commissionResp2 = response
                Log.d("RetailerBothCommissionResp", Gson().toJson(response))
                tryMerge()
            }

            override fun onError(message: String) {
                if(Constants.dialog!=null && Constants.dialog.isShowing){
                    Constants.dialog.dismiss()
                }
            }

        })

    }



    interface ApiCallback<T> {
        fun onSuccess(response: T)
        fun onError(message: String)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale", "SetTextI18n", "SuspiciousIndentation")
    private fun mergeAndCalculateRetailer(resp1: BusCommissionResp, resp2: BusCommissionResp, rechargeAmount: String) {

        val combined = BusCommissionResp(
            isSuccess = true,
            data = listOf(
                DataItem(
                    commissionValue = (resp1.data!![0].commissionValue ?: 0.0) + (resp2.data[0].commissionValue ?: 0.0),
                    commissionType = resp1.data!![0].commissionType, // assume same type
                    servicesValue = (resp1.data!![0].servicesValue ?: 0.0) + (resp2.data[0].servicesValue ?: 0.0),
                    servicesType = resp1.data!![0].servicesType // assume same
                 )
            )
        )

        getAllServiceChargeApiResRetailer(combined, rechargeAmount)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale", "SetTextI18n", "SuspiciousIndentation")
    private fun getAllServiceChargeApiResRetailer(response: BusCommissionResp, rechargeAmount: String) {
        if (response.isSuccess!!) {
            if(Constants.dialog!=null && Constants.dialog.isShowing){
                Constants.dialog.dismiss()
            }
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
                var type =  response.data[0].commissionType!!.trim()?.lowercase() ?: ""
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

            if(Constants.dialog!=null && Constants.dialog.isShowing){
                Constants.dialog.dismiss()
            }

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



    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun serviceChargeCalculation(serviceCharge: Double, gstRate: Double, rechargeAmount: String, response: BusCommissionResp): Double {

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


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    fun openDialogForPayout(transferAmount: Double, servicechargeGst: Double, totalRechargeAmount: Double, retailerCommission: Double, msg: String) {
        dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.busticketcommissionlayout)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        dialog.setCanceledOnTouchOutside(false)

        val transferamttxt = dialog.findViewById<TextView>(R.id.actualamt)
        val servicechargewithgst = dialog.findViewById<TextView>(R.id.servicechargewithgst)
        val warningmsg = dialog.findViewById<TextView>(R.id.warningmsg)
        val transferamt = dialog.findViewById<TextView>(R.id.transferamt)
        val serviceChargeamount = dialog.findViewById<TextView>(R.id.servicescharge)
        val retailercommission = dialog.findViewById<TextView>(R.id.retailercommission)
        val gstamount = dialog.findViewById<TextView>(R.id.gstamount)
        val basefaretxt = dialog.findViewById<TextView>(R.id.basefaretxt)
        val cancel = dialog.findViewById<ImageView>(R.id.cancel)
        val done = dialog.findViewById<LinearLayout>(R.id.Proceedbtn)
        val viewBreakLayout = dialog.findViewById<LinearLayout>(R.id.viewbreaklayout)
        val servicechargelayout = dialog.findViewById<LinearLayout>(R.id.servicechargelayout)
        val detailsgstserviceslayout = dialog.findViewById<LinearLayout>(R.id.chargesdetailslayout)
        val retailercommissionlayout = dialog.findViewById<LinearLayout>(R.id.retailercommissionlayout)

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

        var totalcount = forServiceChargeSeatType[0].seater + forServiceChargeSeatType[0].sleeper

        basefaretxt.text = "Base Fare (${totalcount})"
        transferamt.text = String.format("%.2f", totalRechargeAmount)

        if(servicechargeGst==0.0){
            servicechargelayout.visibility=View.GONE
        }else{
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
                Toast.makeText(this, "Transfer amount must be greater than the base fare.", Toast.LENGTH_LONG).show()
            }

        }

        cancel.setOnClickListener {
            if (dialog != null && dialog.isShowing) {
                dialog.dismiss()
            }

        }

        dialog.setOnDismissListener {
            dialog.dismiss()
        }

        dialog.show() // ✅ REQUIRED
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAllWalletBalance() {
       runIfConnected {
            val walletBalanceReq = GetBalanceReq(
                parmUser = mStash!!.getStringValue(Constants.RegistrationId, ""),
                flag = "CreditBalance"
            )
            getAllApiServiceViewModel.getWalletBalance(walletBalanceReq)
                .observe(this) { resource ->
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
                                Constants.OpenPopUpForVeryfyOTP(this)
                            }
                        }
                    }
                }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun getAllWalletBalanceRes(response: GetBalanceRes) {
        if (response.isSuccess == true) {
            val mainBalance = (response.data[0].result!!.toDoubleOrNull() ?: 0.0)
            getMerchantBalance(mainBalance)

            Log.d("actualBalance", "main = $mainBalance")

            val totalAmount = mStash!!.getStringValue(Constants.totalTransaction, "")?.toDoubleOrNull() ?: 0.0

        } else {
            if(Constants.dialog!=null && Constants.dialog.isShowing){
                Constants.dialog.dismiss()
            }
            toast(response.returnMessage.toString())
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMerchantBalance(mainBalance: Double) {
        val getMerchantBalanceReq = GetMerchantBalanceReq(
            parmUser = mStash!!.getStringValue(Constants.MerchantId, ""),
            flag = "DebitBalance"
        )
        getAllApiServiceViewModel.getAllMerchantBalance(getMerchantBalanceReq)
            .observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    getAllMerchantBalanceRes(response, mainBalance)
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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAllMerchantBalanceRes(response: GetMerchantBalanceRes, mainBalance: Double) {
        if (response.isSuccess == true) {
            Log.d(TAG, "getAllMerchantBalanceRes: ${response.data[0].debitBalance}")
            mStash!!.setStringValue(Constants.merchantBalance, response.data[0].debitBalance)

            val totalAmount = mStash!!.getStringValue(Constants.totalTransaction, "")?.toDoubleOrNull() ?: 0.0
            val merchantBalance = response.data[0].debitBalance?.toDoubleOrNull() ?: 0.0

            Log.d("balanceCheck", "MainBal = $mainBalance, merchantBal = $merchantBalance,totalAmount = $totalAmount, Status = ${totalAmount <= mainBalance && totalAmount <= merchantBalance}")

            if (totalAmount <= mainBalance && totalAmount <= merchantBalance) {
                 getAllBusAddMoney(mStash.getStringValue(Constants.booking_RefNo, "").toString())
            } else {
                if(Constants.dialog!=null && Constants.dialog.isShowing){
                    Constants.dialog.dismiss()
                }
                Toast.makeText(this, "Your merchant balance is low. Please contact the administrator", Toast.LENGTH_LONG).show()
            }
        } else {
            if(Constants.dialog!=null && Constants.dialog.isShowing){
                Constants.dialog.dismiss()
            }
            Toast.makeText(this, response.returnMessage.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun getAllBusRequaryTicketRes(response: BusRequeryRes?) {
        if (response?.responseHeader?.errorCode == "0000") {
            bin.fullLayout.visibility = View.GONE
            bin.reviewLayout.visibility = View.GONE
            bin.confirmBookingLayoutList.visibility = View.VISIBLE

            passengerList.clear()
            data.clear()
            ticketDetails.clear()

            response.paXDetails.forEach { passengerData ->
                passengerList.add(passengerData)
            }

            userPassengerDetailsAdapter.notifyDataSetChanged()


            // for ticket generation in pdf format....................................
            for(passenger in passengerList){
                if(passenger.gender==0){
                    data.add(mutableListOf(passenger.paXName,"Male",passenger.seatNumber,passenger.ticketNumber))
                }else{
                    data.add(mutableListOf(passenger.paXName,"Female",passenger.seatNumber,passenger.ticketNumber))
                }

            }


            qrText=  buildQrDataWithPassengers(response.transportPNR,response.bookingRefNo ,response.busDetail?.fromCity,response.busDetail?.toCity,response.busDetail?.travelDate, response.busDetail?.busType,passengerList)

            Log.d("qrData", qrText.toString())

            qrBitmap = generateQrCode(qrText!!)

            qrBitmap?.let { bin.confirmBookingLayout.ticketQRCode.setImageBitmap(it) }

            bin.confirmBookingLayout.fromLocation.text = response.busDetail?.fromCity.toString()
            bin.confirmBookingLayout.toLocation.text = response.busDetail?.toCity.toString() + " at " + response.busDetail?.droppingDetails?.droppingTime.toString()
            bin.confirmBookingLayout.departureTime.text = response.busDetail?.departureTime.toString() + " " + response.busDetail?.travelDate.toString()

            bin.confirmBookingLayout.ticketStatus.text = response.ticketStatusDesc.toString()
            bin.confirmBookingLayout.referenceNumber.text = response.bookingRefNo.toString()
            bin.confirmBookingLayout.supplierRefNo.text = response.busDetail?.supplierRefNo.toString()
            bin.confirmBookingLayout.transportPNR.text = response.transportPNR.toString()
            bin.confirmBookingLayout.busOperatorName.text = response.busDetail?.operatorName.toString()
            bin.confirmBookingLayout.boardingAddress.text = response.busDetail?.boardingDetails?.boardingAddress.toString()
            bin.confirmBookingLayout.droppingAddress.text = response.busDetail?.droppingDetails?.droppingAddress.toString()
            bin.confirmBookingLayout.boardingTime.text = response.busDetail?.boardingDetails?.boardingTime.toString()
            bin.confirmBookingLayout.droppingingTime.text = response.busDetail?.droppingDetails?.droppingTime.toString()
            bin.confirmBookingLayout.busType.text = response.busDetail?.busType.toString()

            response.paXDetails.forEach { fareDetails ->

                // Extract and parse individual amounts safely
                val basicAmount = fareDetails.fare?.basicAmount ?: 0
                val gst = fareDetails.fare?.gst ?: 0
                val otherAmount = fareDetails.fare?.otherAmount ?: 0

// Extract convenience fee from the TextView (e.g., "₹ 50"), remove non-digits
                val convenienceFeeText = bin.confirmBookingLayout.conveniencesFees.text.toString().trim()
                val convenienceFee = convenienceFeeText.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0

// Add all values
                val totalAmount = basicAmount.toDouble() + gst.toDouble() + otherAmount.toDouble() + convenienceFee.toDouble()

// Set values to TextViews
                bin.confirmBookingLayout.baseFare.text = "₹ $basicAmount"
                bin.confirmBookingLayout.gstCharge.text = "₹ $gst"
                bin.confirmBookingLayout.otherCharge.text = "₹ $otherAmount"
                bin.confirmBookingLayout.conveniencesFees.text = bin.confirmBookingLayout.conveniencesFees.text.toString().trim()
                bin.confirmBookingLayout.totalAmount.text = "₹ $totalAmount"



                ticketDetails.add(TicketDetailsForGenerateTicket(response.cancellationPolicy,response.transportPNR.toString(), response.bookingRefNo.toString(),response.busDetail?.fromCity.toString(),response.busDetail?.toCity.toString(),
                                                    response.busDetail?.departureTime.toString() , response.busDetail?.travelDate.toString() , response.busDetail?.arrivalTime.toString(),"",response.busDetail?.operatorName.toString(),
                    "You will get the driver contact number and vehicle number 30 mins to 1 hours before departure",response.busDetail?.boardingDetails?.boardingAddress.toString(),response.busDetail?.droppingDetails?.droppingAddress.toString() ,"",response.busDetail?.boardingDetails?.boardingTime.toString(),
                    response.busDetail?.busType.toString(),"₹ $basicAmount", "₹ $gst","₹ $otherAmount","₹ " + bin.confirmBookingLayout.conveniencesFees.text.toString().trim(),"₹ $totalAmount"))
           }

            var PaxRequeryResponseReq = BusPaxRequeryResponseReq(
                loginId = mStash!!.getStringValue(Constants.RegistrationId, ""),
                bookingRefNo = response.bookingRefNo,
                ipAddress =mStash?.getStringValue(Constants.deviceIPAddress, "") ,
                requestId = mStash!!.getStringValue(Constants.requestId, ""),
                imeINumber = "0054748569",
                registrationId = mStash?.getStringValue(Constants.MerchantId, ""),
                transportPNR =response.transportPNR ,
                ticketStatusId =response.ticketStatusId ,
                ticketStatusDesc = response.ticketStatusDesc ,
                apiResponse = Gson().toJson(response),
                paramUser =mStash!!.getStringValue(Constants.RegistrationId, "")
            )

            hitApiforPassDetailsListResponse(PaxRequeryResponseReq)


        }

        else {
            if(Constants.dialog!=null && Constants.dialog.isShowing){
                Constants.dialog.dismiss()
            }
            Toast.makeText(this, response?.responseHeader?.errorInnerException.toString(), Toast.LENGTH_SHORT).show()
         }
    }


    fun hitApiforPassDetailsListResponse(PaxRequeryResponseReq: BusPaxRequeryResponseReq){
        Log.d("BusRequeryRequest", Gson().toJson(PaxRequeryResponseReq))

        viewModel.getPassangerDetailsRequest(PaxRequeryResponseReq).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {

                        it.data?.let { users ->
                            users.body()?.let { response ->
                                if(Constants.dialog!=null && Constants.dialog.isShowing){
                                    Constants.dialog.dismiss()
                                }
                                Constants.uploadDataOnFirebaseConsole(Gson().toJson(response),"BusSeatingPassangerDetailsRequest",this@BusSeating)
                                Log.d("RequeryResponse", Gson().toJson(response))
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


    fun generateQrCode(content: String, size: Int = 512): Bitmap {
        val hints = Hashtable<EncodeHintType, String>().apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
        }

        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            size,
            size,
            hints
        )

        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }

        return bitmap
    }


    private fun buildQrDataWithPassengers(pnr: String?, bookingRefNo: String?, fromCity: String?, toCity: String?, travelDate: String?, busType: String?, passengerList: ArrayList<com.bos.payment.appName.data.model.travel.bus.busRequery.PaXDetails>): String {
        val builder = StringBuilder()

        builder.appendLine("🚌 Bus Ticket")
        builder.appendLine("PNR: ${pnr ?: "N/A"}")
        builder.appendLine("Booking Ref No: ${bookingRefNo ?: "N/A"}")
        builder.appendLine("From: ${fromCity ?: "N/A"}")
        builder.appendLine("To: ${toCity ?: "N/A"}")
        builder.appendLine("Date: ${travelDate ?: "N/A"}")
        builder.appendLine("Bus Type: ${busType ?: "N/A"}")
        builder.appendLine()
        builder.appendLine("👤 Passengers:")

        passengerList.forEachIndexed { index, passenger ->
            builder.appendLine("${index + 1}. Name: ${passenger.paXName ?: "N/A"}")
            builder.appendLine("   Age: ${passenger.age ?: "N/A"}")
            builder.appendLine("   Gender: ${if (passenger.gender == 0) "Male" else "Female"}")
            builder.appendLine("   Seat: ${passenger.seatNumber ?: "N/A"}")
            builder.appendLine()
        }

        return builder.toString().trim()
    }


    fun openDialogForGenerateTicket(context: Context,fileName: String,bitmap:Bitmap,data:MutableList<MutableList<String?>>, ticketDetails :MutableList<TicketDetailsForGenerateTicket>){
        if(!ticketDetails.isEmpty()&&ticketDetails.size>0) {
            var ticketDetails = ticketDetails.get(0)
            generateQRPdf(context,bitmap,  fileName,data, headers,ticketDetails){it.second.let {
                Toast.makeText(context, "PDF saved to Downloads", Toast.LENGTH_SHORT).show()
            }

            }

        }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun validationPassenger() {
        val titlePos = bin.passengerDetails.titleSp.selectedItemPosition
        val genderPos = bin.passengerDetails.passengerGender.selectedItemPosition

        when {
            bin.userEmailId.text.isNullOrBlank() -> {
                bin.userEmailId.requestFocus()
                toast("Please enter email ID")
            }

            bin.userMobileNo.text.isNullOrBlank() -> {
                bin.userMobileNo.requestFocus()
                toast("Please enter mobile number")
            }

            else -> {
                getAllDetailsBusBooking()
            }
        }
    }

    private fun validationBoarding() {
        val boardingTextPos = bin.boardingPointSp.selectedItemPosition
        val droppingTextPos = bin.droppingPointSp.selectedItemPosition

        if (boardingTextPos == 0) {
            Toast.makeText(this, "Please select your boarding point", Toast.LENGTH_SHORT).show()
        } else if (droppingTextPos == 0) {
            Toast.makeText(this, "Please select your dropping point", Toast.LENGTH_SHORT).show()
        }
        else {
            bin.seatBookingLayout.visibility = View.GONE
            bin.boardingPointLayout.visibility = View.GONE
            bin.NextBtn.visibility = View.GONE
            bin.proceedBtn.visibility = View.GONE
            bin.proceedToBookBtn.visibility = View.VISIBLE
            bin.passengerDetails.passengerDetailsLayout.visibility = View.VISIBLE
            setSpinnerForGender()
        }
    }

    private fun validationSeat() {
        if (selectedSeats.isEmpty()) {
            toast("Choose your seat")
        } else {
            bin.seatBookingLayout.visibility = View.GONE
            bin.boardingPointLayout.visibility = View.VISIBLE
            bin.NextBtn.visibility = View.GONE
            bin.proceedBtn.visibility = View.VISIBLE
            bin.proceedToBookBtn.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAllDetailsBusBooking() {
        val busTempBookingReq = BusTempBookingReq(
            boardingId = intent.getStringExtra(Constants.boarding_Id),
            corporatePaymentMode = 0,
            corporateStatus = "0",
            costCenterId = 0,
            customerMobile = bin.userMobileNo.text.toString().trim(),
            dealKey = "",
            droppingId = intent.getStringExtra(Constants.dropping_Id),
            gst = false,
            gstin = "",
            gstinHolderAddress = "",
            gstinHolderName = "",
            paXDetails = getPassengerDetailsList(),
            PassengerEmail = bin.userEmailId.text.toString().trim(),
            PassengerMobile = bin.userMobileNo.text.toString().trim(),
            ProjectId = 0,
            Remarks = "testing",
            BusKey = intent.getStringExtra(Constants.busKey),
            SearchKey = mStash.getStringValue(Constants.searchKey, ""),
            SeatMapKey = mStash.getStringValue(Constants.seatMap_Key, ""),
            sendEmail = false,
            sendSMS = false,
            iPAddress = mStash.getStringValue(Constants.deviceIPAddress, ""),
            requestId = mStash.getStringValue(Constants.requestId, ""),
            imeINumber = "1234567890",
            registrationID = mStash.getStringValue(Constants.MerchantId, "")
        )

        Log.d("busTempBookingReq", Gson().toJson(busTempBookingReq))

        viewModel.getAllBusTempBooking(busTempBookingReq).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                hitApiForGetTampBookingRequest( Gson().toJson(busTempBookingReq))
                                getAllDetailsBusBookingRes(response)
                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        hitApiForGetTampBookingRequest( Gson().toJson(busTempBookingReq))
                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                            Constants.dialog.dismiss()
                        }
                    }

                    ApiStatus.LOADING -> {
                        Constants.OpenPopUpForVeryfyOTP(this)
                    }
                }
            }
        }
    }


    private fun hitApiForGetTampBookingRequest( request:String){
        val busTempBookingReq = BusTempBookingRequest(
            iPAddress = mStash.getStringValue(Constants.deviceIPAddress, ""),
            requestId = mStash.getStringValue(Constants.requestId, ""),
            imeINumber = "1234567890",
            corporatePaymentMode = "0",
            corporateStatus = "0",
            costCenterId = "0",
            customer_Mobile = bin.userMobileNo.text.toString().trim(),
            dealKey = "",
            boardingId = intent.getStringExtra(Constants.boarding_Id),
            droppingId = intent.getStringExtra(Constants.dropping_Id),
            gst = "",
            gstin = "",
            gstinHolderAddress = "",
            gstinHolderName = "",
            busKey = intent.getStringExtra(Constants.busKey),
            searchKey = mStash.getStringValue(Constants.searchKey, ""),
            seatMapKey = mStash.getStringValue(Constants.seatMap_Key, ""),
            sendEmail = "",
            sendSMS = "",
            createdBy = mStash.getStringValue(Constants.RegistrationId, ""),
            registrationID = mStash.getStringValue(Constants.MerchantId, ""),
            loginId = mStash.getStringValue(Constants.RegistrationId, ""),
            apiRequest =request
        )

        Log.d("TampBookRequest",busTempBookingReq.toString())
        viewModel.getBusTampBookRequest(busTempBookingReq).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {

                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Constants.uploadDataOnFirebaseConsole(Gson().toJson(response),"BusSeatingBusTampBookRequest",this@BusSeating)
                                Log.d("TampBookReqResopnse",response.toString())
                            }
                        }
                    }

                    ApiStatus.ERROR -> {

                    }

                    ApiStatus.LOADING -> {

                    }

                }
            }
        }

    }


    private fun hitApiForGetTampBookingResponse( response:String, apiResponse:BusTempBookingRes){
        val busTempBookingReq = BusTampBookTicketResponseRequest(
            iPAddress = mStash.getStringValue(Constants.deviceIPAddress, ""),
            requestId = mStash.getStringValue(Constants.requestId, ""),
            imeINumber = "1234567890",
            bookingRefNo = apiResponse.bookingRefNo,
            errorCode = apiResponse.responseHeader!!.errorCode,
            errorDesc = apiResponse.responseHeader!!.errorDesc,
            errorInnerException = apiResponse.responseHeader!!.errorInnerException,
            createdBy = mStash.getStringValue(Constants.RegistrationId, ""),
            registrationID = mStash.getStringValue(Constants.MerchantId, ""),
            loginId = mStash.getStringValue(Constants.RegistrationId, ""),
            apiResponse =response
        )

        Log.d("TampBookResponseRequest",Gson().toJson(busTempBookingReq))

        viewModel.getTampBusTicketResponse(busTempBookingReq).observe(this)
        {
            resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Constants.uploadDataOnFirebaseConsole(Gson().toJson(response),"BusSeatingTampBusTicketResponse",this@BusSeating)
                                Log.d("TampBookResponseRes",response.toString())
                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                    }

                    ApiStatus.LOADING -> {

                    }

                }
            }
        }

    }


    @SuppressLint("SetTextI18n")
    private fun getAllDetailsBusBookingRes(response: BusTempBookingRes?) {
        if(Constants.dialog!=null && Constants.dialog.isShowing){
            Constants.dialog.dismiss()
        }
        if (response?.responseHeader?.errorCode == "0000") {
            hitApiForGetTampBookingResponse(Gson().toJson(response),response)
            bin.fullLayout.visibility = View.GONE
            bin.reviewLayout.visibility = View.VISIBLE
            bin.reviewBookingInclude.userName.text = bin.passengerDetails.passengerName.text.trim()
            bin.reviewBookingInclude.userAge.text = age.toString() + " Yrs"
            bin.reviewBookingInclude.seatNumber.text = "Seat No. " + mStash.getStringValue(Constants.seatNumber, "")
            bin.reviewBookingInclude.mobileNo.text = bin.userMobileNo.text.trim()
            bin.reviewBookingInclude.emailId.text = bin.userEmailId.text.trim()
            bin.reviewBookingInclude.companyName.text =
                intent.getStringExtra(Constants.travelCompanyName).orEmpty()
            bin.reviewBookingInclude.busName.text =
                intent.getStringExtra(Constants.busName).orEmpty()
            bin.reviewBookingInclude.fromLocation.text =
                mStash.getStringValue(Constants.fromDesignationName, "")
            bin.reviewBookingInclude.toLocation.text =
                mStash.getStringValue(Constants.toDesignationName, "")
            bin.reviewBookingInclude.boardingPoint.text =
                mStash.getStringValue(Constants.boardingPoint, "")
            bin.reviewBookingInclude.droppingPoint.text =
                mStash.getStringValue(Constants.droppingPoint, "")
            bin.reviewBookingInclude.arrivalTime.text =
                intent.getStringExtra(Constants.arrivalTime).orEmpty()
            bin.reviewBookingInclude.totalTime.text =
                intent.getStringExtra(Constants.travelTime).orEmpty()
            mStash.setStringValue(Constants.booking_RefNo, response.bookingRefNo.toString())

            if( !response.bookingRefNo.isNullOrEmpty()){
                bin.reviewBookingInclude.finalSeatLayout.visibility=View.VISIBLE
            }

            Log.d("bookingRefNo", response.bookingRefNo.toString())
            toast(response.responseHeader?.errorCode.toString())
           // getAllBusAddMoney(response.bookingRefNo.toString())

        }
        else {
            Toast.makeText(this, response?.responseHeader?.errorInnerException.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    private fun getAllBusAddMoney(referenceId: String) {
        val busAddMoneyReq = BusAddMoneyReq(
            clientRefNo = referenceId,
            refNo = referenceId,
            transactionType = 0,
            productId = "0",
            iPAddress = mStash.getStringValue(Constants.deviceIPAddress, ""),
            requestId = mStash.getStringValue(Constants.requestId, ""),
            imeINumber = "1232972532",
            registrationID = mStash.getStringValue(Constants.MerchantId, "")
        )
        Log.d("BusAddMoneyReq", Gson().toJson(busAddMoneyReq))
        AppLog.d("BusAddMoneyReq",Gson().toJson(busAddMoneyReq))

        viewModel.getAllAddMoney(busAddMoneyReq).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                getAllBusAddMoneyRes(response)
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


    private fun getAllBusAddMoneyRes(response: BusAddMoneyRes?) {
        AppLog.d("BusAddMoneyResponse",Gson().toJson(response))
        if(Constants.dialog!=null && Constants.dialog.isShowing){
            Constants.dialog.dismiss()
        }
        if (response?.responseHeader?.errorCode == "0000") {
            getAllBusTicketing(mStash.getStringValue(Constants.booking_RefNo, "").toString())
        }
        else {
            Toast.makeText(this, response?.responseHeader?.errorInnerException.plus("Amount: ").plus(response!!.amount), Toast.LENGTH_SHORT).show()
        }
    }


    private fun getAllBusTicketing(referenceId: String) {
        val busTicketingReq = BusTicketingReq(
            bookingRefNo = referenceId,
            iPAddress = mStash.getStringValue(Constants.deviceIPAddress, ""),
            requestId = mStash.getStringValue(Constants.requestId, ""),
            imeINumber = "125424463",
            registrationID = mStash.getStringValue(Constants.MerchantId, ""))

        Log.d("busTempBookingReq", Gson().toJson(busTicketingReq))
        AppLog.d("busTempBookingReq",Gson().toJson(busTicketingReq))

        viewModel.getAllBusTicketing(busTicketingReq).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                AppLog.d("busTempBookingRes",Gson().toJson(response))
                                if(Constants.dialog!=null && Constants.dialog.isShowing){
                                    Constants.dialog.dismiss()
                                }
                                mStash.setStringValue(Constants.ForPayoutBookingRefId, response.bookingRefNo.toString())
                                getAllBusTicketingRes(response)
                                getAddBusTicketRequest(mStash.getStringValue(Constants.booking_RefNo, "").toString(),Gson().toJson(busTicketingReq))
                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                            Constants.dialog.dismiss()
                        }
                        getAddBusTicketRequest(mStash.getStringValue(Constants.booking_RefNo, "").toString(),Gson().toJson(busTicketingReq))
                    }

                    ApiStatus.LOADING -> {
                        Constants.OpenPopUpForVeryfyOTP(this)
                    }
                }
            }
        }

    }


    // hit api for add bus ticket request.............................................................................................
    private fun getAddBusTicketRequest(referenceId: String,apirequest:String) {
        val busTicketingReq = AddTicketReq(
            requestId =mStash.getStringValue(Constants.requestId, "") ,
            iPAddress = mStash.getStringValue(Constants.deviceIPAddress, ""),
            imeINumber = "125424463",
            bookingRefNo = referenceId,
            createdBy = mStash.getStringValue(Constants.RegistrationId, ""),
            registrationID = mStash.getStringValue(Constants.MerchantId, ""),
            loginID = mStash.getStringValue(Constants.RegistrationId, ""),
            apiRequest = apirequest)

    Log.d("busAddTicketRequest", Gson().toJson(busTicketingReq))

    viewModel.getAddBusTicketRequest(busTicketingReq).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        Constants.uploadDataOnFirebaseConsole(Gson().toJson(resource.data),"BusSeatingAddBusTicketRequest",this@BusSeating)
                        Log.d("AddBusTicketRequest",resource.data.toString())
                    }

                    ApiStatus.ERROR -> {
                    }

                    ApiStatus.LOADING -> {
                    }
                }
            }
        }


    }

    private fun hitApiForBusTicketResponse(response: BusTicketingRes){
        val busTicketingReq = AddTicketResponseReq(
            requestId =mStash.getStringValue(Constants.requestId, "") ,
            iPAddress = mStash.getStringValue(Constants.deviceIPAddress, ""),
            imeINumber = "125424463",
            bookingRefNo = mStash.getStringValue(Constants.booking_RefNo, ""),
            errorCode      = response.responseHeader!!.errorCode,
            errorDesc      = response.responseHeader!!.errorDesc,
            errorInnerException  = response.responseHeader!!.errorInnerException,
            createdBy = mStash.getStringValue(Constants.RegistrationId, ""),
            statusId      = response.responseHeader!!.statusId,
            supplierRefNo      = response.supplierRefno,
            transportPNR      = response.transportPNR,
            registrationID = mStash.getStringValue(Constants.MerchantId, ""),
            loginID = mStash.getStringValue(Constants.RegistrationId, ""),
            apiResponse  =  Gson().toJson(response)
        )

        Log.d("busAddTicketRequest", Gson().toJson(busTicketingReq))
        Log.d("apiResponse", Gson().toJson(response))

        viewModel.getAddBusTicketResponse(busTicketingReq).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                            Constants.dialog.dismiss()
                        }
                        getTransferAmountToAgentWithCal()
                        Constants.uploadDataOnFirebaseConsole(Gson().toJson(resource.data),"BusSeatingAddBusTicketResponse",this@BusSeating)
                        Log.d("AddBusTicketResponse",resource.data.toString())
                    }

                    ApiStatus.ERROR -> {
                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                            Constants.dialog.dismiss()
                        }
                    }

                    ApiStatus.LOADING -> {
                       // pd.show()
                    }
                }
            }
        }

    }


    private fun getTransferAmountToAgentWithCal() {
        try {
            var transferamt =   mStash!!.getStringValue(Constants.totalTransaction,"")
            var actualamt =  mStash!!.getStringValue(Constants.actualbusticketamt,"").toString()
            var bookingRefId =   mStash.getStringValue(Constants.ForPayoutBookingRefId, "")

            val transferAmountToAgentsReq = TransferAmountToAgentsReq(
                transferFrom = mStash!!.getStringValue(Constants.RegistrationId, ""),
                transferTo = "Admin",
                transferAmt = transferamt,
                remark = "Bus Booking  Api",
                transferFromMsg = "Your account has been debited by ₹$transferamt for a bus booking Reference Number: ${bookingRefId}.",
                transferToMsg = "Your account has been credited by ₹$transferamt for a bus booking Reference Number: ${bookingRefId}.",
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
                                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                                            Constants.dialog.dismiss()
                                        }
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
                                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                            }

                            ApiStatus.LOADING -> {

                            }
                        }
                    }
                }
         }  catch (e: NumberFormatException) {
            e.printStackTrace()
            if(Constants.dialog!=null && Constants.dialog.isShowing){
                Constants.dialog.dismiss()
            }
            Toast.makeText(this, e.message.toString() + " " + e.localizedMessage?.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun getAllBusTicketingRes(response: BusTicketingRes) {
        hitApiForBusTicketResponse(response)
        if (response.responseHeader?.errorCode == "0000") {
            bin.reviewBookingInclude.finalSeatLayout.visibility = View.GONE
            bin.reviewBookingInclude.reviewBookingDetailsLayout.visibility = View.VISIBLE
            bin.reviewBookingInclude.showTicketLayout.visibility = View.GONE
            bin.reviewBookingInclude.toolbarHeaderName.text = "Booking Details"
            bin.reviewBookingInclude.referenceNumber.text = response.bookingRefNo.toString()
            bin.reviewBookingInclude.supplierRefNo.text = response.supplierRefno.toString()
            bin.reviewBookingInclude.transportPNR.text = response.transportPNR.toString()
            getAllBusRequaryTicket()
           // toast("Your seat booking is successful")
        }
        else {
            Toast.makeText(this, response.responseHeader?.errorInnerException.toString(), Toast.LENGTH_SHORT).show()
        }

    }


    private fun setDropDown() {
        bin.passengerDetails.passengerDetailsLayout.visibility = View.GONE
        try {
            Constants.getAllBoardingPointAdapter = ArrayAdapter<String>(this, R.layout.spinner_right_aligned, Constants.boardingPointName!!)
            Constants.getAllBoardingPointAdapter!!.setDropDownViewResource(R.layout.spinner_right_aligned)
            bin.boardingPointSp.adapter = Constants.getAllBoardingPointAdapter
            bin.boardingPointSp.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
                        if (pos >= 0) {
                            try {
                                boardingPointText = parent.getItemAtPosition(pos).toString()
                                mStash.setStringValue(
                                    Constants.boardingPoint,
                                    boardingPointText.toString()
                                )
                                // If you need to perform further actions with selectedAllOperator, do it here.
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            boardingPointText = ""
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Handle the case where nothing is selected, if needed
                    }
                }

// Notify the adapter that the data set has changed (if data is dynamically added/removed)
            Constants.getAllBoardingPointAdapter!!.notifyDataSetChanged()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

        try {
            Constants.getAllDroppingPointAdapter = ArrayAdapter<String>(
                this,
                R.layout.spinner_right_aligned,
                Constants.droppingPointName!!
            )


            Constants.getAllDroppingPointAdapter!!.setDropDownViewResource(R.layout.spinner_right_aligned)
            bin.droppingPointSp.adapter = Constants.getAllDroppingPointAdapter
            bin.droppingPointSp.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                        if (pos >= 0) {
                            try {
                                droppingPointText = parent.getItemAtPosition(pos).toString()
                                mStash.setStringValue(
                                    Constants.droppingPoint,
                                    droppingPointText.toString()
                                )

                                // If you need to perform further actions with selectedAllOperator, do it here.
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            droppingPointText = ""
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Handle the case where nothing is selected, if needed
                    }
                }
            Constants.getAllDroppingPointAdapter!!.notifyDataSetChanged()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        /*************************************************** title Spinner ************************************/
//        Constants.titleName = arrayListOf("Select your title", "Mr", "Mrs","Ms")

        val arrayListSpinner = resources.getStringArray(R.array.gender_array)//title_type
        val adapters = ArrayAdapter(this, R.layout.spinner_right_aligned, arrayListSpinner)
        adapters.setDropDownViewResource(R.layout.spinner_right_aligned)
        bin.passengerDetails.titleSp.adapter = adapters
        bin.passengerDetails.titleSp.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    titleText = if (position > 0) {
                        parent!!.getItemAtPosition(position).toString()
                    } else {
                        null
                    }
                    Log.e("TAG", "onItemSelected: $titleText")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        bin.passengerDetails.titleSp.setSelection(0)

        /************************************************ gender Spinner *****************************/
//        Constants.genderName = arrayListOf("Select your gender", "Male", "Female")

/*        bin.passengerDetails.passengerGender.visibility=View.VISIBLE
        val arrayGenderSpinner = resources.getStringArray(R.array.gender_array)
        val genderadapters = ArrayAdapter(this@BusSeating, R.layout.spinner_right_aligned, arrayGenderSpinner)
        genderadapters.setDropDownViewResource(R.layout.spinner_right_aligned)
        bin.passengerDetails.passengerGender.adapter = genderadapters

      *//*  bin.passengerDetails.passengerGender.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position > 0) {
                        genderText = parent!!.getItemAtPosition(position).toString()
                        selectedIndex = if (position == 1) 0 else 1
                        toast(selectedIndex.toString())
                        Log.d("TAG", "SelectedGender: $genderText, Index: $selectedIndex")
                    }
                    else {
                        genderText = ""
                        selectedIndex = -1 // Or reset state
                        Log.e("TAG", "Please select gender")
                    }

                    Log.e("TAG", "onItemSelected: $genderText")

                }


                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedIndex = -1
                }

            }*//*

        bin.passengerDetails.passengerGender.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    Log.d("Spinner", "Item selected at position $position")
                    if (position > 0) {
                        genderText = parent!!.getItemAtPosition(position).toString()
                        selectedIndex = if (position == 1) 0 else 1
                        toast(selectedIndex.toString())
                    } else {
                        genderText = ""
                        selectedIndex = -1
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

        bin.passengerDetails.passengerGender.setSelection(0)*/

    }


    private fun setSpinnerForGender(){
        val arrayGenderSpinner = resources.getStringArray(R.array.gender_array)
        val genderadapters = ArrayAdapter(this@BusSeating, R.layout.spinner_right_aligned, arrayGenderSpinner)
        genderadapters.setDropDownViewResource(R.layout.spinner_right_aligned)
        bin.passengerDetails.passengerGender.adapter = genderadapters

        bin.passengerDetails.passengerGender.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    Log.d("Spinner", "Item selected at position $position")
                    if (position > 0) {
                        genderText = parent!!.getItemAtPosition(position).toString()
                        selectedIndex = if (position == 1) 0 else 1
                        toast(selectedIndex.toString())
                        Log.d("Gender Data",genderText+"")
                    }
                    else {
                        genderText = ""
                        selectedIndex = -1
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

        bin.passengerDetails.passengerGender.setSelection(0)


        /*  bin.passengerDetails.passengerGender.onItemSelectedListener =
             object : AdapterView.OnItemSelectedListener {

                 override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                     if (position > 0) {
                         genderText = parent!!.getItemAtPosition(position).toString()
                         selectedIndex = if (position == 1) 0 else 1
                         toast(selectedIndex.toString())
                         Log.d("TAG", "SelectedGender: $genderText, Index: $selectedIndex")
                     }
                     else {
                         genderText = ""
                         selectedIndex = -1 // Or reset state
                         Log.e("TAG", "Please select gender")
                     }

                     Log.e("TAG", "onItemSelected: $genderText")

                 }


                 override fun onNothingSelected(parent: AdapterView<*>?) {
                     selectedIndex = -1
                 }

             }*/
    }


    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        mStash = MStash.getInstance(this)!!
        Constants.titleName = ArrayList()
        Constants.genderName = ArrayList()
        Constants.genderName?.add("Select your gender")
        Constants.genderName?.add("Select your title")
        list = ArrayList()
        passengerList = ArrayList()

        bin.seatBookingLayout.visibility = View.VISIBLE
        bin.fromLocation.text = mStash.getStringValue(Constants.fromDesignationName, "")
        bin.fromLocation.text = mStash.getStringValue(Constants.fromDesignationName, "")
        bin.toLocation.text = mStash.getStringValue(Constants.toDesignationName, "")
        bin.dateAndTime.text = mStash.getStringValue(Constants.dateAndTime, "")
        bin.companyName.text = intent.getStringExtra(Constants.travelCompanyName).orEmpty()
        bin.busName.text = intent.getStringExtra(Constants.busName).orEmpty()
        bin.arrivalTime.text = intent.getStringExtra(Constants.arrivalTime).orEmpty()
        bin.travelAmount.text = "₹" + intent.getStringExtra(Constants.travelAmount).orEmpty()
        bin.totalTime.text = intent.getStringExtra(Constants.travelTime).orEmpty()

        Constants.boardingPointName = ArrayList()
        Constants.droppingPointName = ArrayList()
        Constants.boardingPointName?.add("Select your boarding Point")
        Constants.droppingPointName?.add("Select your dropping Point")

        bin.confirmBookingLayout.recyclerViewPassenger.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        userPassengerDetailsAdapter = UserPassengerDetailsAdapter(this, passengerList)
        bin.confirmBookingLayout.recyclerViewPassenger.adapter = userPassengerDetailsAdapter
    }


    private fun getAllMappingSeat() {
        val request = BusSeatMapReq(
            boardingId = intent.getStringExtra(Constants.boarding_Id),
            droppingId = intent.getStringExtra(Constants.dropping_Id),
            busKey = intent.getStringExtra(Constants.busKey),
            searchKey = mStash.getStringValue(Constants.searchKey, ""),
            iPAddress = mStash.getStringValue(Constants.deviceIPAddress, ""),
            requestId = mStash?.getStringValue(Constants.requestId, ""),
            imeINumber = "2232323232323",
            registrationID = mStash.getStringValue(Constants.MerchantId, "")
        )

        Log.d("busTempBookingReq", Gson().toJson(request))

        viewModel.getAllBusSeatMap(request).observe(this) { resource ->
            when (resource.apiStatus) {
                ApiStatus.SUCCESS -> {
                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                        Constants.dialog.dismiss()
                    }

                    resource.data?.body()?.
                    let {
                        setupSeatGrid(it)
                    }
                }

                ApiStatus.ERROR -> {
                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                        Constants.dialog.dismiss()
                    }
                    Toast.makeText(this, "Failed to load seats", Toast.LENGTH_SHORT).show()
                }

                ApiStatus.LOADING -> Constants.OpenPopUpForVeryfyOTP(this)
            }
        }
    }


    private fun setupSeatGrid(response: BusSeatMapRes) {
        val lowerGrid = bin.seatLayout.lowerBerthGrid
        val upperGrid = bin.seatLayout.upperBerthGrid
        var upperlayout = bin.seatLayout.upperlayout
        var lowerlayout = bin.seatLayout.lowerlayout

        bin.seatBookingLayout.visibility = View.VISIBLE
        bin.boardingPointLayout.visibility = View.GONE
        bin.NextBtn.visibility = View.VISIBLE
        bin.proceedBtn.visibility = View.GONE
        bin.proceedToBookBtn.visibility = View.GONE


        lowerGrid.removeAllViews()
        upperGrid.removeAllViews()
        mStash.setStringValue(Constants.seatMap_Key, response.seatMapKey.toString())

        Log.d("removeAllViews", mStash.getStringValue(Constants.seatMap_Key, "").toString())

        try {
            Constants.boardingPointName?.clear()
            Constants.droppingPointName?.clear()
            Constants.boardingPointName?.add("Select your boarding Point")
            Constants.droppingPointName?.add("Select your dropping Point")
            response.boardingDetails.forEach { boardingPoint ->
                Constants.boardingPointName?.add(boardingPoint.boardingAddress!!)
            }
            Constants.getAllBoardingPointAdapter?.notifyDataSetChanged()
            response.droppingDetails.forEach { droppingPoint ->
                Constants.droppingPointName?.add(droppingPoint.droppingAddress!!)
            }
            Constants.getAllDroppingPointAdapter?.notifyDataSetChanged()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }


        response.seatMap.forEach { seat ->
            val row = seat.row?.toIntOrNull() ?: 0
            val col = seat.column ?: 0
            val zIndex = seat.zIndex?.toIntOrNull() ?: 0
            val length = seat.length?.toIntOrNull() ?: 1
            val amount = seat.fareMaster?.basicAmount ?: 0.0
            val seatName = "₹" + String.format("%.2f", amount)

            val maxColumn = response.seatMap.maxOf { it.column ?: 0 } + 1

            lowerGrid.columnCount = 2
            upperGrid.columnCount = 2

            mStash.setStringValue(Constants.seatNumber, seat.seatNumber.toString())

            val seatView = createSeatView(seatName.toString(), this, row, col, length, seat)

            if (zIndex == 1) {
                upperGrid.addView(seatView)
            }
            else {
                lowerGrid.addView(seatView)
            }

            if(upperGrid.size>0){
                upperlayout.visibility = View.VISIBLE
            }else {
                upperlayout.visibility = View.GONE
            }

        }

    }


    @SuppressLint("InflateParams", "SetTextI18n")
    private fun createSeatView(seatName: String, context: Context, row: Int, column: Int, length: Int, seat: SeatMap): View {

        val seatView = LayoutInflater.from(context).inflate(R.layout.seat_item, null)
        val tvPrice = seatView.findViewById<TextView>(R.id.tvPrice)
        val pillow = seatView.findViewById<View>(R.id.pillowView)
        val lefthandle = seatView.findViewById<View>(R.id.lefthandle)
        val righthandle = seatView.findViewById<View>(R.id.righthandle)
        val bottomHandle = seatView.findViewById<View>(R.id.bottomHandle)

        tvPrice.gravity = Gravity.CENTER
        tvPrice.setPadding(6, 6, 6, 6)
        tvPrice.maxLines = 1
        tvPrice.isSingleLine = true
        tvPrice.ellipsize = TextUtils.TruncateAt.END
        tvPrice.textSize = 8f
        tvPrice.text = seatName

        val isSleeper = length == 2

        val widthh = if (isSleeper) 100 else 100
        val heightt = if (isSleeper) 150 else 100


        val params = GridLayout.LayoutParams().apply {
            width = widthh
            height = heightt
            setMargins(10,10,10,10)
            setGravity(Gravity.CENTER)
        }

        if (isSleeper) {
            pillow.visibility = View.VISIBLE
            lefthandle.visibility= View.GONE
            righthandle.visibility= View.GONE
            bottomHandle.visibility= View.GONE
            params.width = widthh   // wider seat
            params.height = heightt
        } else {
            pillow.visibility = View.GONE
            lefthandle.visibility= View.VISIBLE
            righthandle.visibility= View.VISIBLE
            bottomHandle.visibility= View.VISIBLE
        }

        seatView.layoutParams = params


        // Set initial background
        when {
            seat.ladiesSeat == true -> seatView.setBackgroundResource(R.drawable.bg_ladies_seat)
            seat.bookable == true -> seatView.setBackgroundResource(R.drawable.bg_edittext)
            else -> seatView.setBackgroundResource(R.drawable.booked_seat)
        }

        seatView.setOnClickListener {
            if (seat.bookable != true) {
                Toast.makeText(context, "Seat not available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedSeats.contains(seat)) {
                selectedSeats.remove(seat)

                // Set background after deselection
                if (seat.ladiesSeat == true) {
                    seatView.setBackgroundResource(R.drawable.bg_ladies_seat)
                } else {
                    seatView.setBackgroundResource(R.drawable.bg_edittext)
                }

                // Remove passenger form
                bin.passengerDetails.passengerDetailsLayout.findViewWithTag<View>(seat.seatNumber)
                    ?.let {
                        bin.passengerDetails.passengerDetailsLayout.removeView(it)
                    }

            }
            else {
                selectedSeats.add(seat)
                bin.finalSeatLayout.visibility = View.VISIBLE
                seatView.setBackgroundResource(R.drawable.your_seat)

                // Add passenger form
                val formView = LayoutInflater.from(context).inflate(R.layout.passenger_details, null)
                formView.tag = seat.seatNumber
                val typePrefix = if ((seat.zIndex?.toIntOrNull() ?: 0) == 1) "U" else "L"
                formView.findViewById<TextView>(R.id.seatTitle).text = "Seat $typePrefix${seat.seatNumber}"
                bin.passengerDetails.passengerDetailsLayout.addView(formView)
            }

            // Keep passenger forms count in sync
            while (bin.passengerDetails.passengerDetailsLayout.childCount > selectedSeats.size) {
                bin.passengerDetails.passengerDetailsLayout.removeViewAt(0)
            }

            // Update selected seat label and amount
            val seatLabels = selectedSeats.map {
                val type = if ((it.zIndex?.toIntOrNull() ?: 0) == 1) "U" else "L"
                "$type${it.seatNumber}"
            }

            val lengthCount = selectedSeats.groupingBy { it.length }.eachCount()

            val countLen1 = lengthCount["1"] ?: 0   // for seater
            val countLen2 = lengthCount["2"] ?: 0  // for sleeper

            Log.d("count1","$countLen1")
            Log.d("count2","$countLen2")
            forServiceChargeSeatType.clear()

            var busType: String

            if(intent.getStringExtra(Constants.busName)!!.contains("NON A/C Sleeper"))
            {
                busType="NON-AC"
            }else{
                busType="AC"
            }

            forServiceChargeSeatType.add(SeattypeModel(
                countLen2,countLen1,busType.trim()
            ))

            Log.d("servicetypelist",Gson().toJson(forServiceChargeSeatType))

            bin.selectedSeat.text = seatLabels.joinToString(" ")

            val totalAmount = selectedSeats.sumOf { it.fareMaster?.basicAmount ?: 0.0 }
            bin.finalAmount.text = "₹ $totalAmount"
            bin.reviewBookingInclude.totalprice.text = "₹ $totalAmount"
            rechargeAmount = totalAmount.toString()
            Log.d("Total Amount", " $totalAmount")
        }

        return seatView
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPassengerDetailsList(): List<PaXDetails> {

        val paxList = mutableListOf<PaXDetails>()

        for (i in 0 until bin.passengerDetails.passengerDetailsLayout.childCount) {
            val formView = bin.passengerDetails.passengerDetailsLayout.getChildAt(i)

            val fullName = formView.findViewById<EditText>(R.id.passengerName).text.toString().trim()
            val gender = formView.findViewById<Spinner>(R.id.passengerGender).selectedItem.toString()
            val dob = formView.findViewById<EditText>(R.id.passengerDob).text.toString().trim()
            val seatNumber = formView.tag?.toString() ?: ""
            val title = formView.findViewById<Spinner>(R.id.titleSp).selectedItem.toString()
            val berth = seatNumber.firstOrNull().toString() // "U" or "L" based on your logic

            if(gender.equals("Male")){
                selectedIndex=0
            }

            if(gender.equals("Female")){
                selectedIndex=1
            }

            try {
                age = calculateAgeFromDOB(dob)
                val next = getNextNumber()
                val seatNumber = selectedSeats[i].seatNumber

                val dateOfBirth = getChangeDateFormat(dob)


                paxList.add(
                    PaXDetails(
                        Age = age,
                        DOB = dateOfBirth,
                        Fare = list,
                        Gender = selectedIndex.toString(),
                        IdNumber = "",
                        IdType = 0,
                        LadiesSeat = false,
                        PAXId = next.toString(),
                        PAXName = fullName,
                        PenaltyCharge = "",
                        Primary = true,
                        SeatNumber = seatNumber,
                        Status = "",
                        TicketNumber = "",
                        Title = title
                    )
                )

            } catch (e: DateTimeParseException) {
                e.printStackTrace()
            }
        }
        return paxList
    }


    // for payout transaction and commission entry
    private fun getTransferAmountToAgentInCommissionCal(response: TransferAmountToAgentsRes) {
        var withouttdscommissionamount = mStash!!.getStringValue(Constants.retailerCommissionWithoutTDS, "")
        var tdsamount = mStash!!.getStringValue(Constants.tds, "")
        var actualcommission = mStash!!.getStringValue(Constants.retailerCommission, "")
        var bookingRefId =   mStash.getStringValue(Constants.ForPayoutBookingRefId, "")

        Log.d("tdsamount", tdsamount.toString())
        Log.d("commissionamount", withouttdscommissionamount.toString())
        Log.d("actualcommission", actualcommission.toString())

        val transferAmountToAgentsReq = TransferToAgentReq(
            merchantCode = mStash!!.getStringValue(Constants.MerchantId, ""),
            transferFrom = "Admin",
            amountType = "Deposit",
            transIpAddress = mStash!!.getStringValue(Constants.deviceIPAddress, ""),
            remark = "Commission Deposit by Bus Api",
            transferTo = mStash!!.getStringValue(Constants.RegistrationId, ""),
            transferToMsg = "Your account has been credited by ₹${withouttdscommissionamount} as commission for the Bus booking of Against Reference Number: ${response.data!!.refTransID} for the Booking Reference Number: ${bookingRefId}",
            gstAmt = 0,
            parmUserName = mStash!!.getStringValue(Constants.RegistrationId, ""),
            servicesChargeGSTAmt = 0,
            servicesChargeWithoutGST = 0,
            actualTransactionAmount = withouttdscommissionamount?.toDouble() ?: 0.0,
            actualCommissionAmt = 0,
            commissionWithoutGST = 0,
            transferFromMsg = "Your account has been debited  by ₹ ${withouttdscommissionamount} as commission for the Bus booking of Against Reference Number: ${response.data!!.refTransID} for the Booking Reference Number: ${bookingRefId}",
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
                                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                                        Constants.dialog.dismiss()
                                    }
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            if(Constants.dialog!=null && Constants.dialog.isShowing){
                                Constants.dialog.dismiss()
                            }
                            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }

                        ApiStatus.LOADING -> {

                        }

                    }
                }
            }
    }


}


