package com.bos.payment.appName.ui.view.travel.flightBooking.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.travel.flight.AirRepriceRequests
import com.bos.payment.appName.data.model.travel.flight.FlightRePriceReq
import com.bos.payment.appName.data.model.travel.flight.FlightsItem
import com.bos.payment.appName.data.model.travel.flight.SegmentsItem
import com.bos.payment.appName.data.model.travel.flight.fareBreakup
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.data.viewModelFactory.TravelViewModelFactory
import com.bos.payment.appName.databinding.FlightdetailsItemBottomsheetBinding
import com.bos.payment.appName.databinding.TravellersclassItemBottomsheetBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.travel.adapter.FlightTicketDetailsAdapter
import com.bos.payment.appName.ui.view.travel.adapter.PassangerDataList
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.FlightDetails
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.adultCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.calculateTotalFlightDuration
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.childCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.className
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.infantCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.totalCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.totalDurationTime
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.fareList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.farebreakupList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.flightDetailsPassangerDetail
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.paxDetailsListFromReprice
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.segmentListPassangerDetail
import com.bos.payment.appName.ui.viewmodel.TravelViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Constants.scanForActivity
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.generateRandomNumber
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson

class FlightDetailsBottomSheet:BottomSheetDialogFragment() {
    private lateinit var binding : FlightdetailsItemBottomsheetBinding
    lateinit var flightadapter: FlightTicketDetailsAdapter
    var flightDetails :MutableList<FlightsItem?> = mutableListOf()
    var segmentList :MutableList<SegmentsItem?> = mutableListOf()

    lateinit var viewModel : TravelViewModel
    private var mStash: MStash? = null

    var fareId: String=""
    var flightKey: String= ""


    companion object {
        const val TAG = "FlightDetailsBottomSheet"

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FlightdetailsItemBottomsheetBinding.inflate(inflater, container, false)

        mStash = MStash.getInstance(requireContext())
        viewModel = ViewModelProvider(this, TravelViewModelFactory(TravelRepository(RetrofitClient.apiAllTravelAPI, RetrofitClient.apiBusAddRequestlAPI)))[TravelViewModel::class.java]


        if(FlightDetails.size>0){
            flightDetails.clear()
            flightDetails.addAll(FlightDetails)
        }

        if(FlightDetails.size>0){
            segmentList.clear()
            flightDetails[flightDetails.size-1]!!.segments?.let { segmentList.addAll(it) }
        }

        fareId =  flightDetails[0]!!.fares!![0].fareId
        flightKey = flightDetails[0]!!.flightKey

        setonclicklistner()
        setDataOnView()

        return binding.root
    }



    fun HitApiForFareBreakUp(){

        var flightFareReq= FlightRePriceReq(
            searchKey = mStash!!.getStringValue(Constants.FlightSearchKey,"") ,
            customerMobile = mStash!!.getStringValue(Constants.MobileNumber, ""),
            gstInput = false,
            singlePricing = false,
            ipAddress = mStash?.getStringValue(Constants.deviceIPAddress, ""),
            requestId = mStash?.getStringValue(Constants.requestId, ""),
            imeiNumber = "0054748569",
            registrationId = mStash?.getStringValue(Constants.MerchantId, ""),
            airRepriceRequests = getairRepriseRequests()
        )

        binding.progressbar.visibility= View.VISIBLE
        binding.booknow.visibility=View.GONE
        Log.d("FlightRePriseReq", Gson().toJson(flightFareReq))

        viewModel.getFlightRePriseRequest(flightFareReq).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("repriceResponse", response.responseHeader.errorCode)
                                Log.d("repriceResponse2", response.responseHeader.errorInnerException)
                                Log.d("repriseamount",Gson().toJson(response))

                                if(response.responseHeader.errorCode.equals("0000")){
                                    paxDetailsListFromReprice = response.airRepriceResponses!![0].requiredPAXDetails!!
                                    fareList = response.airRepriceResponses!![0].flight.fares!![0].fareDetails!!
                                    var flightKey = response.airRepriceResponses[0].flight.flightKey
                                    mStash?.setStringValue(Constants.FlightKey, flightKey)
                                    startActivity(Intent(requireContext(),AddDetailsPassangerActivity::class.java))
                                    binding.progressbar.visibility= View.GONE
                                    binding.booknow.visibility=View.VISIBLE
                                    dismiss()

                                }

                                if(response.responseHeader.errorCode.equals("0004")){
                                    binding.progressbar.visibility= View.GONE
                                    binding.booknow.visibility=View.VISIBLE
                                    Toast.makeText(requireContext(),response.responseHeader.errorDesc,Toast.LENGTH_SHORT).show()
                                }

                                Log.d("FlightRePriseResponse", Gson().toJson(response))

                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        binding.progressbar.visibility= View.GONE
                        binding.booknow.visibility=View.VISIBLE
                    }

                    ApiStatus.LOADING -> {
                        binding.progressbar.visibility= View.VISIBLE
                        binding.booknow.visibility=View.GONE
                    }
                }
            }
        }
    }

    fun getairRepriseRequests():List<AirRepriceRequests>{
        var repriseRequestList: MutableList<AirRepriceRequests> = mutableListOf()
        repriseRequestList.clear()
        repriseRequestList.add(
            AirRepriceRequests(flightKey = flightKey,
                fareId = fareId)
        )

        return repriseRequestList
    }

    private fun setDataOnView(){
        flightadapter= FlightTicketDetailsAdapter(requireContext(),flightDetails,segmentList)
        binding.showterminallist.adapter=flightadapter
        binding.price.text= "₹ ".plus(flightDetails[0]!!.fares?.get(0)?.fareDetails?.get(0)?.totalAmount)
        val segments = listOf(mapOf("departure_DateTime" to segmentList[0]!!.departureDateTime, "arrival_DateTime" to segmentList[0]!!.arrivalDateTime), mapOf("departure_DateTime" to segmentList[segmentList.size-1]!!.departureDateTime, "arrival_DateTime" to segmentList[segmentList.size-1]!!.arrivalDateTime))
        totalDurationTime= calculateTotalFlightDuration(segments)

    }


    private fun setonclicklistner(){

        binding.cross.setOnClickListener {
            dialog!!.dismiss()
        }

        binding.booknow.setOnClickListener {
            segmentListPassangerDetail.clear()
            flightDetailsPassangerDetail.clear()
            segmentListPassangerDetail.addAll(segmentList)
            flightDetailsPassangerDetail.addAll(flightDetails)
            HitApiForFareBreakUp()

        }


    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // used to show the bottom sheet dialog
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


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
       // (activity as? FlightMainActivity)?.setData()

       /* if(context is FlightMainActivity){
            (context as? FlightMainActivity)?.setData()
        }
        else {*/
            (scanForActivity(context)?.supportFragmentManager?.findFragmentByTag("FlightMainFragment") as? FlightMainFragment)?.setData()
       // }

    }



}