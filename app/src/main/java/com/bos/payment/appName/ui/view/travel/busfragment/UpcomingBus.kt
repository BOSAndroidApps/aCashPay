package com.bos.payment.appName.ui.view.travel.busfragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.travel.bus.busRequery.BusRequeryReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusBookingListReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusManageCancelTicketReq
import com.bos.payment.appName.databinding.FragmentUpcomingBusBinding
import com.bos.payment.appName.ui.view.travel.adapter.BusTicketUpcomingAdapter
import com.bos.payment.appName.ui.view.travel.busactivity.BusTicketConsListClass
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusPassengerDetailsReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusPaxRequeryResponseReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketCancelReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketCancelResponseReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketCancellationChargeReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.CancelTicketDetail
import com.bos.payment.appName.data.model.travel.bus.busTicket.CancellationPenaltyValue
import com.bos.payment.appName.data.model.travel.bus.busTicket.DataItem
import com.bos.payment.appName.data.model.travel.bus.busTicket.PaXDetailsItem
import com.bos.payment.appName.data.model.travel.bus.forservicecharge.BusCommissionResp
import com.bos.payment.appName.data.model.travel.bus.forservicecharge.ServiceChargeReq
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.data.viewModelFactory.TravelViewModelFactory
import com.bos.payment.appName.localdb.AppLog
import com.bos.payment.appName.localdb.AppLog.d
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.travel.adapter.ViewPagerAdapter
import com.bos.payment.appName.ui.view.travel.busactivity.MyBookingBusActivity
import com.bos.payment.appName.ui.viewmodel.TravelViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.PD
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class UpcomingBus : Fragment(), MyBookingBusActivity.BookingRefreshListener {
    lateinit var binding: FragmentUpcomingBusBinding
    var UpcomingTicketList: MutableList<DataItem> = mutableListOf()
    lateinit var upcomingadapter: BusTicketUpcomingAdapter
    var tableData: MutableList<MutableList<String?>> = arrayListOf()
    var checkSelectedPassangerList: MutableList<Int> = mutableListOf()
    var selectedCardPosition: Int = 0
    var cancellationPolicy: String = ""
    var cancellationPenaltyList: MutableList<CancellationPenaltyValue?>? = mutableListOf()
    var cancellationPenalty: Double = 0.0
    var TicketStatus:MutableList<String> = mutableListOf()
    var basefareamount: Int = 0
    private var mStash: MStash? = null
    private var passangerList: MutableList<PaXDetailsItem> = mutableListOf()
    private lateinit var viewModel: TravelViewModel
    lateinit var dialog: Dialog
    lateinit var finalticketcanceldialog: Dialog
    var serviceChargeValue: Double = 0.0
    var serviceChargeType: String = ""
    var toCity: String = ""
    var fromCity: String = ""
    var travelDate: String = ""
    var PNRNumber: String = ""
    private var isViewReady = false


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mStash = MStash.getInstance(context)
        viewModel = ViewModelProvider(this, TravelViewModelFactory(TravelRepository(RetrofitClient.apiAllTravelAPI, RetrofitClient.apiBusAddRequestlAPI)))[TravelViewModel::class.java]

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpcomingBusBinding.inflate(inflater, container, false)
        isViewReady = true
        hitApiforServiceCharge()

        return binding.root
    }


    private fun setRecyclerview() {
        if (UpcomingTicketList.size > 0) {
            binding.notfounddatalayout.visibility = View.GONE
            binding.showingUpcomingList.visibility = View.VISIBLE
            upcomingadapter = BusTicketUpcomingAdapter(requireContext(), UpcomingTicketList, this)
            binding.showingUpcomingList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = upcomingadapter
            }
            upcomingadapter.notifyDataSetChanged()
        } else {
            binding.notfounddatalayout.visibility = View.VISIBLE
            binding.showingUpcomingList.visibility = View.GONE
        }

    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun hitApiForPassangerDetails(position: Int, bookingrefNo: String) {
        var requestForPassengerDetails = BusPassengerDetailsReq(
            bookingRefNo = bookingrefNo
        )
        Log.d("BusRequery", Gson().toJson(requestForPassengerDetails))
        AppLog.d("PaxDetailsRequest", Gson().toJson(requestForPassengerDetails).toString())

        viewModel.getPassangerDetailsRequest(requestForPassengerDetails).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        if (Constants.dialog != null && Constants.dialog.isShowing) {
                            Constants.dialog.dismiss()
                        }
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                if (response.isSuccess) {
                                    Log.d("PaxDetailsResponse", Gson().toJson(response))
                                    AppLog.d("PaxDetailsResponse", response.toString())
                                    Constants.uploadDataOnFirebaseConsole(
                                        Gson().toJson(response),
                                        "UpcomingBusPassangerDetailsRequest",
                                        requireContext()
                                    )

                                    var getdata = response.data!![0].apiData
                                    if (getdata != null) {
                                        passangerList!!.clear()
                                        passangerList!!.addAll(response.data!![0].apiData.paXDetails!!)
                                        var boardingTime = getdata.busDetail.departureTime
                                        fromCity = getdata.busDetail.fromCity
                                        toCity = getdata.busDetail.toCity
                                        var busOperatorName = getdata.busDetail.operatorName
                                        travelDate = getdata.busDetail.travelDate
                                        var travelType = getdata.busDetail.busType
                                        var droppingTime = getdata.busDetail.arrivalTime
                                        var passangerQunatity = getdata.noofPax
                                        cancellationPolicy = getdata.cancellationPolicy
                                        tableData.clear()

                                        passangerList.filter {
                                            val status = it.status?.trim()?.lowercase()
                                            status == "confirmed" || status == "payment pending"
                                        }.forEach { passenger ->
                                                val genderText = if (passenger.gender == "0" || passenger.gender == "0.00") "Male" else "Female"
                                                tableData.add(
                                                    mutableListOf(
                                                        passenger.paXName,
                                                        genderText,
                                                        passenger.seatNumber,
                                                        passenger.ticketNumber
                                                    )
                                                )
                                            }

                                        UpcomingTicketList.set(position, DataItem(
                                                UpcomingTicketList[position].bookingRefNo,
                                                UpcomingTicketList[position].transportPNR,
                                                UpcomingTicketList[position].imeINumber,
                                                UpcomingTicketList[position].statusdesc,
                                                UpcomingTicketList[position].requestId,
                                                UpcomingTicketList[position].iPAddress,
                                                UpcomingTicketList[position].statusCode,
                                                passangerList,
                                                droppingTime,
                                                boardingTime,
                                                fromCity,
                                                toCity,
                                                busOperatorName,
                                                travelType,
                                                travelDate,
                                                passangerQunatity,
                                                tableData,
                                                true
                                            )
                                        )

                                        if (upcomingadapter != null) {
                                            upcomingadapter.updateList(UpcomingTicketList, position)
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Passenger details not found",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                } else {
                                    Toast.makeText(
                                        context,
                                        response.returnMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Constants.uploadDataOnFirebaseConsole(
                                        response.returnMessage,
                                        "UpcomingBusPassangerDetailsRequest",
                                        requireContext()
                                    )
                                }
                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        if (Constants.dialog != null && Constants.dialog.isShowing) {
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


    @RequiresApi(Build.VERSION_CODES.O)
    public fun hitApiForTicketCancellationCharge(selectionList: MutableList<Int>, position: Int) {
         checkSelectedPassangerList = selectionList
         selectedCardPosition = position
         getCancelTicketList()

        if(TicketStatus.isNotEmpty()){
            Log.d("TicketLength","${TicketStatus.size}")
            val result = isAllConfirmed(TicketStatus)
            if (result) {
                var cancellationReq = BusTicketCancellationChargeReq(
                    bookingRefNo = UpcomingTicketList.get(position).bookingRefNo,
                    ipAddress = mStash?.getStringValue(Constants.deviceIPAddress, ""),
                    requestId = mStash!!.getStringValue(Constants.requestId, ""),
                    imeiNumber = "0054748569",
                    registrationId = mStash?.getStringValue(Constants.MerchantId, ""),
                    cancelTicketDetails = getCancelTicketList()
                )

                Log.d("cancellationreq", Gson().toJson(cancellationReq))

                AppLog.d("BusRequery", Gson().toJson(cancellationReq))

                viewModel.getBusTicketCancellationCharge(cancellationReq).observe(this) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                it.data?.let { users ->
                                    users.body()?.let { response ->
                                        Log.d("cancelchargeresp", Gson().toJson(response))

                                        if (response.statuss.equals("218")) {
                                            Toast.makeText(
                                                context,
                                                response.message.toString(),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        Log.d("ChargeKey", response.cancellationChargeKey.toString())

                                        Constants.uploadDataOnFirebaseConsole(Gson().toJson(response), "UpcomingBusTicketCancellationCharge", requireContext())
                                        AppLog.d("ChargeKey", Gson().toJson(response))

                                        if (!response.cancellable.equals("false")) {
                                            if (Constants.dialog != null && Constants.dialog.isShowing) {
                                                Constants.dialog.dismiss()
                                            }
                                            cancellationPenaltyList!!.clear()
                                            cancellationPenaltyList!!.addAll(response.cancellationPenaltyValues!!)

                                            var cancellationReq = BusTicketCancelReq(
                                                bookingRefNo = UpcomingTicketList.get(position).bookingRefNo,
                                                ipAddress = mStash?.getStringValue(Constants.deviceIPAddress, ""),
                                                requestId = mStash!!.getStringValue(Constants.requestId, ""),
                                                imeiNumber = "0054748569",
                                                registrationId = mStash?.getStringValue(Constants.MerchantId, ""),
                                                cancellationChargeKey = response.cancellationChargeKey.toString(),
                                                busTicketstoCancel = getCancelTicketList()
                                            )
                                            Log.d("BusTicketReq", Gson().toJson(cancellationReq))
                                            showCancellationPopUp(cancellationReq, position)

                                        } else {
                                            if (Constants.dialog != null && Constants.dialog.isShowing) {
                                                Constants.dialog.dismiss()
                                            }
                                            Toast.makeText(context, response.responseHeader!!.errorInnerException, Toast.LENGTH_SHORT).show()
                                        }


                                    }
                                }
                            }

                            ApiStatus.ERROR -> {
                                if (Constants.dialog != null && Constants.dialog.isShowing) {
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
            else {
                Toast.makeText(requireContext(),"Your ticket is currently pending, so cancellation is not allowed",Toast.LENGTH_LONG).show()
            }
        }
        else{
            Toast.makeText(requireContext(),"Your ticket is currently pending, so cancellation is not allowed",Toast.LENGTH_LONG).show()
            Log.d("TicketLength","${TicketStatus.size}")
        }


    }


    fun isAllConfirmed(ticketStatus: MutableList<String>): Boolean {
        return ticketStatus.all {
            it.trim().equals("confirmed", ignoreCase = true)
        }
    }


    private fun hitApiForCancelTicket(req: BusTicketCancelReq, bookingrefNo: String) {
        Log.d("CancelBusRequest", Gson().toJson(req))
        viewModel.getBusTicketCancelRequest(req).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("CancelStatus", Gson().toJson(response))

                                if (response.responseHeader.errorCode.equals("0000")) {
                                    if (dialog != null && dialog.isShowing) {
                                        dialog.dismiss()
                                    }

                                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT)
                                        .show()

                                    var cancellationResponseReq = BusTicketCancelResponseReq(
                                        bookingRefNo = bookingrefNo,
                                        loginId = mStash!!.getStringValue(
                                            Constants.RegistrationId,
                                            ""
                                        ),
                                        errorCode = response.responseHeader.errorCode,
                                        createdby = mStash!!.getStringValue(
                                            Constants.RegistrationId,
                                            ""
                                        ),
                                        apiResponse = Gson().toJson(response),
                                        registrationID = mStash!!.getStringValue(
                                            Constants.requestId,
                                            ""
                                        ),
                                        errorDesc = response.responseHeader.errorDesc,
                                    )

                                    hitApiForManageTicketCancel(
                                        bookingrefNo,
                                        cancellationResponseReq
                                    )

                                }

                                if (response.responseHeader.errorCode.equals("0010")) {
                                    if (Constants.dialog != null && Constants.dialog.isShowing) {
                                        Constants.dialog.dismiss()
                                    }
                                    Log.d("ErrorResponseCancel", response.message)
                                    Toast.makeText(
                                        context,
                                        response.responseHeader.errorInnerException,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        if (Constants.dialog != null && Constants.dialog.isShowing) {
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


    fun hitApiForUploadCancelResponseOnServer(
        cancelresponsereq: BusTicketCancelResponseReq,
        bookingrefNo: String,
        PNR: String,
    ) {
        Log.d("CancelResponseReq", Gson().toJson(cancelresponsereq))
        viewModel.getBusTicketCancelResponseReq(cancelresponsereq).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("CancelResponseResponse", Gson().toJson(response))
                                Constants.uploadDataOnFirebaseConsole(
                                    Gson().toJson(response),
                                    "UpcomingBusTicketCancelResponseReq",
                                    requireContext()
                                )
                                if (response.isSuccess!!) {
                                    if (!BusTicketConsListClass.startDate!!.isNullOrEmpty() && !BusTicketConsListClass.endDate!!.isNullOrEmpty()) {
                                        // hit requery api for status update
                                        getAllBusRequaryTicket(bookingrefNo,PNR)
                                    }
                                }
                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        if (Constants.dialog != null && Constants.dialog.isShowing) {
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
    private fun getCancelTicketList(): List<CancelTicketDetail> {
        val ticketDetailsList = mutableListOf<CancelTicketDetail>()
        basefareamount = 0
        cancellationPenalty = 0.0
        TicketStatus.clear()

        for (i in 0 until checkSelectedPassangerList.size) {
            var seatNo = passangerList.get(checkSelectedPassangerList.get(i)).seatNumber
            var ticketNo = passangerList.get(checkSelectedPassangerList.get(i)).ticketNumber
            var PNR = UpcomingTicketList.get(selectedCardPosition).transportPNR
            var status = passangerList.get(checkSelectedPassangerList.get(i)).status
            TicketStatus.add(status)
            PNRNumber = PNR
            basefareamount += passangerList.get(checkSelectedPassangerList.get(i)).fare.basicAmount.toDouble().toInt()
            Log.d("Basicamount", "$basefareamount")

            cancellationPenaltyList!!.forEachIndexed { index, item ->
                if (passangerList.get(checkSelectedPassangerList.get(i)).seatNumber.equals(item!!.seatNumber)) {
                    cancellationPenalty += cancellationPenaltyList!![index]!!.cancellationPenalty.toDouble()
                }
            }

            Log.d("TicketStatusList", "$TicketStatus")

            Log.d("cancellationPenalty", "$cancellationPenalty")

            ticketDetailsList.add(CancelTicketDetail(
                    seatNumber = seatNo!!,
                    ticketNumber = ticketNo!!,
                    transportPNR = PNR!!
                )
            )

        }
        return ticketDetailsList
    }


    private fun getAllBusRequaryTicket(bookingRefNo: String,PNR:String) {
        val busRequery = BusRequeryReq(
            bookingRefNo = bookingRefNo,
            iPAddress = mStash!!.getStringValue(Constants.deviceIPAddress, ""),
            requestId = mStash!!.getStringValue(Constants.requestId, ""),
            imeINumber = "215237488",
            registrationID = mStash!!.getStringValue(Constants.MerchantId, "")
        )

        Log.d("BusRequery", Gson().toJson(busRequery))

        viewModel.getAllBusRequary(busRequery).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {

                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("RequeryRespo", Gson().toJson(response))

                                val ticketStatus = when {

                                    response.paXDetails.all { it.status.equals("Confirmed", true) } ->
                                        "Confirmed"

                                    response.paXDetails.any { it.status.equals("Confirmed", true) } ||
                                            response.paXDetails.any { it.status.equals("Payment Pending", true) } ->
                                        "Partial Cancelled"

                                    response.paXDetails.all { it.status.equals("Cancelled", true) } ->
                                        "Cancelled"

                                    response.paXDetails.any { it.status.equals("Cancelled", true) } &&
                                            response.paXDetails.any { it.status.equals("Confirmed", true) } ->
                                        "Partial Cancelled"

                                    else ->
                                        "Unknown"
                                }

                                var PaxRequeryResponseReq = BusPaxRequeryResponseReq(
                                    loginId = mStash!!.getStringValue(Constants.RegistrationId, ""),
                                    bookingRefNo = response.bookingRefNo,
                                    ipAddress = mStash?.getStringValue(Constants.deviceIPAddress, "") ,
                                    requestId = mStash!!.getStringValue(Constants.requestId, ""),
                                    imeINumber = "0054748569",
                                    registrationId = mStash?.getStringValue(Constants.MerchantId, ""),
                                    transportPNR = PNR ,
                                    ticketStatusId =response.ticketStatusId ,
                                    ticketStatusDesc = response.ticketStatusDesc ,
                                    apiResponse = Gson().toJson(response),
                                    paramUser =mStash!!.getStringValue(Constants.RegistrationId, ""),
                                    ticketcancelstatus= ticketStatus
                                )

                                Log.d("BusRequeryRequest", Gson().toJson(PaxRequeryResponseReq))

                                hitApiforPassDetailsListResponse(PaxRequeryResponseReq, response.bookingRefNo!!)

                            }

                        }

                    }

                    ApiStatus.ERROR -> {
                        if (Constants.dialog != null && Constants.dialog.isShowing) {
                            Constants.dialog.dismiss()
                        }
                    }

                    ApiStatus.LOADING -> {

                    }
                }
            }
        }
    }


    fun hitApiforPassDetailsListResponse(PaxRequeryResponseReq: BusPaxRequeryResponseReq, bookingrefnum: String) {

        viewModel.getPassangerDetailsRequest(PaxRequeryResponseReq).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("RequeryResponse", Gson().toJson(response))
                                FinalCancelTicketPopUp(bookingrefnum)

                                if (Constants.dialog != null && Constants.dialog.isShowing) {
                                    Constants.dialog.dismiss()
                                }

                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        if (Constants.dialog != null && Constants.dialog.isShowing) {
                            Constants.dialog.dismiss()
                        }
                        val errorMsg = it.message ?: "Something went wrong"
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                    }

                    ApiStatus.LOADING -> {

                    }
                }
            }
        }


    }


    @SuppressLint("SetTextI18n")
    fun showCancellationPopUp(cancellationReq: BusTicketCancelReq, position: Int) {
        dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.buscancellationpolicy)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        dialog.setCanceledOnTouchOutside(false)

        val cancel = dialog.findViewById<LinearLayout>(R.id.cancel)
        val done = dialog.findViewById<LinearLayout>(R.id.donebutton)
        val cancelpolicy = dialog.findViewById<TextView>(R.id.cancelpolicy)
        val servicechargepolicy = dialog.findViewById<TextView>(R.id.servicechargepolicy)
        val basefareamt = dialog.findViewById<TextView>(R.id.basefareamt)
        val cancellationcharge = dialog.findViewById<TextView>(R.id.cancellationcharge)
        val notemsg = dialog.findViewById<TextView>(R.id.notemsg)
        val operatorcharge = dialog.findViewById<TextView>(R.id.operatorcharge)
        val servicescharge = dialog.findViewById<TextView>(R.id.servicescharge)
        val gstamount = dialog.findViewById<TextView>(R.id.gstamount)
        val detailsgstserviceslayout = dialog.findViewById<LinearLayout>(R.id.chargesdetailslayout)
        val viewBreakLayout = dialog.findViewById<LinearLayout>(R.id.viewbreaklayout)


        // Service charge calculation
        val gst = 18.0 // Fixed GST rate of 18%
        val serviceCharge = serviceChargeValue ?: 0.0
        val cancellationPenalty = cancellationPenalty.toDouble() ?: 0.0
        val cancellationPenaltyAmount = basefareamount.toDouble() - cancellationPenalty
        Log.d("Penalty", "${cancellationPenaltyAmount}")

        val totalAdminServiceChargeWithGst = serviceChargeCalculation(
            serviceCharge,
            gst,
            basefareamount.toString(),
            serviceChargeType
        )

        val gstt = mStash!!.getStringValue(Constants.gst, "")
        val adminServiceCharge = mStash!!.getStringValue(Constants.serviceCharge, "")


        cancelpolicy.text = cancellationPolicy
        basefareamt.text = "%.2f".format(basefareamount.toDouble())
        operatorcharge.text = "%.2f".format(cancellationPenalty)

        //.....Admin......................................................................
        cancellationcharge.text = String.format("%.2f", totalAdminServiceChargeWithGst)
        servicescharge.text = adminServiceCharge
        gstamount.text = gstt
        // ...............................................................................

        if (serviceChargeType.equals("PERCENT")) {
            servicechargepolicy.text =
                "A ${serviceChargeValue}% cancellation fee, calculated on the total fare, will be applied by the Admin"
        }

        if (totalAdminServiceChargeWithGst >= cancellationPenaltyAmount) {
            notemsg.visibility = View.VISIBLE
        } else {
            notemsg.visibility = View.GONE
        }

        done.setOnClickListener {
            hitApiForCancelTicket(cancellationReq, UpcomingTicketList.get(position).bookingRefNo)
        }

        cancel.setOnClickListener {
            dialog.dismiss()

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
            if (Constants.dialog != null && Constants.dialog.isShowing) {
                Constants.dialog.dismiss()
            }
        }


        dialog.setOnDismissListener {

        }

        dialog.show() // ✅ REQUIRED

    }


    fun hitApiforServiceCharge() {
        var request = ServiceChargeReq(
            taskType = "SHOW",
            adminCode = mStash!!.getStringValue(Constants.AdminCode, "")
        )

        Log.d("servicechargereq", Gson().toJson(request))

        viewModel.ServiceChargeReq(request).observe(requireActivity()) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {

                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("servicechargeresp", Gson().toJson(response))
                                var data = response.data
                                data!!.forEach {
                                    serviceChargeType = it!!.cancellationChargeType!!
                                    serviceChargeValue = it!!.cancellationChargeValue!!
                                }
                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        if (Constants.dialog != null && Constants.dialog.isShowing) {
                            Constants.dialog.dismiss()
                        }
                    }

                    ApiStatus.LOADING -> {

                    }
                }
            }
        }

    }


    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun serviceChargeCalculation(serviceCharge: Double, gstRate: Double, rechargeAmount: String, servicesType: String): Double {
        val rechargeAmountValue = rechargeAmount.toDoubleOrNull() ?: 0.0
        var type = servicesType!!.trim()?.lowercase() ?: ""
        val totalAmountWithGst = when (type) {
            "amount" -> {
                // Service charge is a fixed amount
                val serviceChargeWithGst = serviceCharge * (gstRate / 100)
                mStash!!.setStringValue(Constants.gst, String.format("%.2f", serviceChargeWithGst))
                mStash!!.setStringValue(
                    Constants.serviceCharge,
                    String.format("%.2f", serviceCharge)
                )
                serviceCharge + serviceChargeWithGst
            }

            "percent" -> {
                // Service charge is a percentage of the recharge amount
                val serviceInAmount = rechargeAmountValue * (serviceCharge / 100)
                val serviceWithGst = serviceInAmount * (gstRate / 100)
                mStash!!.setStringValue(Constants.gst, String.format("%.2f", serviceWithGst))
                mStash!!.setStringValue(
                    Constants.serviceCharge,
                    String.format("%.2f", serviceInAmount)
                )
                serviceInAmount + serviceWithGst

            }

            else -> {
                mStash!!.setStringValue(Constants.gst, String.format("%.2f", 0.0))
                mStash!!.setStringValue(Constants.serviceCharge, String.format("%.2f", 0.0))
                0.0
            }
        }

        mStash!!.setStringValue(
            Constants.serviceChargeWithGST,
            String.format("%.2f", totalAmountWithGst)
        )

        Log.d("gstamount", mStash!!.getStringValue(Constants.gst, "").toString())
        Log.d("servicecharge", mStash!!.getStringValue(Constants.serviceCharge, "").toString())
        Log.d(
            "totalAmountWithGst",
            mStash!!.getStringValue(Constants.serviceChargeWithGST, "").toString()
        )

        // Return the total service charge (with GST) to include in the final transaction
        return totalAmountWithGst
    }


    public fun hitApiForManageTicketCancel(bookingrefNo: String, cancellationResponseReq: BusTicketCancelResponseReq) {
        var cancellationReq = BusManageCancelTicketReq(
            pnRNumber = PNRNumber,
            loginId = mStash?.getStringValue(Constants.RegistrationId, ""),
            cancellationCharge = cancellationPenalty,
            bookingRefNo = bookingrefNo,
            adminRemarks = "User requested cancellation",
            toCity = toCity,
            refundStatus = "Initiated",
            fareAmount = basefareamount,
            taskType = "INS",
            travelDate = convertDateToIso(travelDate),
            adminId = mStash?.getStringValue(Constants.AdminCode, ""),
            cancellationtype = "Bus",
            id = 0,
            fromCity = fromCity,
            status = "Pending",
            refundAmount = basefareamount
        )

        Log.d("cancellationreq", Gson().toJson(cancellationReq))

        AppLog.d("BusRequery", Gson().toJson(cancellationReq))

        viewModel.getBusManageCancelTicketRequest(cancellationReq).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("managecancelresp", response.outputMessage!!)
                                hitApiForUploadCancelResponseOnServer(cancellationResponseReq, bookingrefNo,PNRNumber)
                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        if (Constants.dialog != null && Constants.dialog.isShowing) {
                            Constants.dialog.dismiss()
                        }
                    }

                    ApiStatus.LOADING -> {

                    }

                }
            }

        }


    }


    @SuppressLint("SetTextI18n")
    fun FinalCancelTicketPopUp(bookingRefNum: String) {
        finalticketcanceldialog =
            Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        finalticketcanceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        finalticketcanceldialog.setContentView(R.layout.finalticketcanceldialog)

        finalticketcanceldialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }


        finalticketcanceldialog.setCanceledOnTouchOutside(false)


        var bookingrefnum = finalticketcanceldialog.findViewById<TextView>(R.id.bookingrefnumber)
        var buttonOk = finalticketcanceldialog.findViewById<Button>(R.id.btnOk)
        bookingrefnum.text = "Booking Reference:${bookingRefNum}"


        buttonOk.setOnClickListener {
            // (activity as? MyBookingBusActivity)?.hitApiForBookingList(BusTicketConsListClass.startDate, BusTicketConsListClass.endDate)
            hitApiForBookingList(BusTicketConsListClass.startDate, BusTicketConsListClass.endDate)
            // (activity as? MyBookingBusActivity)?.hitApiForBusTicketCancelList(BusTicketConsListClass.startDate, BusTicketConsListClass.endDate)
            finalticketcanceldialog.dismiss()
        }


        finalticketcanceldialog.show() // ✅ REQUIRED

    }


    fun convertDateToIso(dateStr: String): String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getDefault() // IST

        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val date = inputFormat.parse(dateStr)
        return outputFormat.format(date!!)
    }


    override fun refreshData(startDate: String, endDate: String) {
        if (!isAdded || !isViewReady) {
            BusTicketConsListClass.startDate = startDate
            BusTicketConsListClass.endDate = endDate
            return
        }
        hitApiForBookingList(BusTicketConsListClass.startDate, BusTicketConsListClass.endDate)
    }


    override fun onResume() {
        super.onResume()

        val start = BusTicketConsListClass.startDate
        val end = BusTicketConsListClass.endDate

        if (!start.isNullOrEmpty() && !end.isNullOrEmpty()) {

            // Prevent duplicate API calls
            Log.d("UpcomingBus", "Calling booking API from onResume")
            if (BusTicketConsListClass.startDate.isNotBlank() && BusTicketConsListClass.endDate.isNotBlank())
                hitApiForBookingList(BusTicketConsListClass.startDate, BusTicketConsListClass.endDate)

        }
    }


    fun hitApiForBookingList(startDate: String, endDate: String) {
        if (!isVisible) {
            Log.d("UpcomingBus", "Fragment not visible yet, skipping")
            return
        }
        val busRequery = BusBookingListReq(
            loginID = mStash!!.getStringValue(Constants.RegistrationId, ""),
            startDate = startDate,
            endDate = endDate
        )
        AppLog.d("BookingListReq", Gson().toJson(busRequery))
        Log.d("BusTicketList", Gson().toJson(busRequery))
        viewModel.getBusBookListResponse(busRequery).observe(viewLifecycleOwner) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("bookinglist", Gson().toJson(response))

                                if (Constants.dialog != null && Constants.dialog.isShowing) {
                                    Constants.dialog.dismiss()
                                }

                                Constants.uploadDataOnFirebaseConsole(Gson().toJson(response), "MyBookingBusActivityBusBookListResponse", requireContext())
                                AppLog.d("BookingListReqResponse", response.toString())
                                if (response.data != null) {
                                    UpcomingTicketList.clear()
                                    UpcomingTicketList.addAll(response.data!!)
                                    setRecyclerview()
                                }
                                else {
                                    binding.notfounddatalayout.visibility = View.VISIBLE
                                    binding.showingUpcomingList.visibility = View.GONE
                                }
                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        if (Constants.dialog != null && Constants.dialog.isShowing) {
                            Constants.dialog.dismiss()
                        }
                        binding.notfounddatalayout.visibility = View.VISIBLE
                        binding.showingUpcomingList.visibility = View.GONE
                    }

                    ApiStatus.LOADING -> {
                        Constants.OpenPopUpForVeryfyOTP(requireContext())
                    }
                }
            }
        }

    }


}