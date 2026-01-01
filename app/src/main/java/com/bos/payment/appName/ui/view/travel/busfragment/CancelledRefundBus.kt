package com.bos.payment.appName.ui.view.travel.busfragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusBookingListReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusPassengerDetailsReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.CancelTicketDataItem
import com.bos.payment.appName.data.model.travel.bus.busTicket.DataItem
import com.bos.payment.appName.data.model.travel.bus.busTicket.PaXDetailsItem
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.data.viewModelFactory.TravelViewModelFactory
import com.bos.payment.appName.databinding.FragmentCancelledRefundBusBinding
import com.bos.payment.appName.localdb.AppLog
import com.bos.payment.appName.localdb.AppLog.d
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.travel.adapter.BusTicketCancelledAdapter
import com.bos.payment.appName.ui.view.travel.adapter.BusTicketUpcomingAdapter
import com.bos.payment.appName.ui.view.travel.adapter.ViewPagerAdapter
import com.bos.payment.appName.ui.view.travel.busactivity.BusTicketConsListClass
import com.bos.payment.appName.ui.view.travel.busactivity.MyBookingBusActivity
import com.bos.payment.appName.ui.viewmodel.TravelViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.PD
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson


class CancelledRefundBus : Fragment() , MyBookingBusActivity.BookingRefreshListener {
   lateinit var binding: FragmentCancelledRefundBusBinding
    var BusCancelList: MutableList<CancelTicketDataItem> = mutableListOf()
    var CancelTicketList : MutableList<CancelTicketDataItem> = mutableListOf()
    lateinit var upcomingadapter: BusTicketCancelledAdapter
    var tableData : MutableList<MutableList<String?>> = arrayListOf()
    var checkSelectedPassangerList : MutableList<Int> = mutableListOf()
    var selectedCardPosition:Int = 0
    private var mStash: MStash? = null
    private var passangerList : MutableList<PaXDetailsItem> = mutableListOf()
    private lateinit var viewModel: TravelViewModel
    private var isViewReady = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this, TravelViewModelFactory(TravelRepository(RetrofitClient.apiAllTravelAPI, RetrofitClient.apiBusAddRequestlAPI)))[TravelViewModel::class.java]
        mStash = MStash.getInstance(context)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding= FragmentCancelledRefundBusBinding.inflate(inflater,container,false)
        isViewReady = true
        return binding.root
    }



    private fun  setRecyclerview(){
        if(CancelTicketList.size>0){
            binding.notfounddatalayout.visibility=View.GONE
            binding.showingCancelList.visibility=View.VISIBLE
            upcomingadapter= BusTicketCancelledAdapter(requireContext(),CancelTicketList,this)
            binding.showingCancelList.apply { layoutManager= LinearLayoutManager(context)
                adapter=upcomingadapter
            }
            upcomingadapter.notifyDataSetChanged()
        }
        else{
            binding.notfounddatalayout.visibility=View.VISIBLE
            binding.showingCancelList.visibility=View.GONE
        }

    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun hitApiForPassangerDetails(position:Int, bookingrefNo:String){
        var requestForPassengerDetails = BusPassengerDetailsReq(
            bookingRefNo = bookingrefNo
        )
        Log.d("BusPassangerReq", Gson().toJson(requestForPassengerDetails))

        viewModel.getPassangerDetailsRequest(requestForPassengerDetails).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                            Constants.dialog.dismiss()
                        }
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                if(response.isSuccess){
                                    Constants.uploadDataOnFirebaseConsole(Gson().toJson(response),"CancelledRefundBusPassangerDetailsRequest",requireContext())
                                    Log.d("PaxDetailsResponse", Gson().toJson(response))
                                    var getdata = response.data!![0].apiData
                                    passangerList!!.clear()
                                    passangerList!!.addAll(response.data!![0].apiData.paXDetails!!)
                                    var boardingTime = getdata.busDetail.departureTime
                                    var fromCity = getdata.busDetail.fromCity
                                    var toCity = getdata.busDetail.toCity
                                    var busOperatorName= getdata.busDetail.operatorName
                                    var travelDate = getdata.busDetail.travelDate
                                    var travelType = getdata.busDetail.busType
                                    var droppingTime = getdata.busDetail.arrivalTime
                                    var passangerQunatity = getdata.noofPax

                                    tableData.clear()
                                    var index = 1
                                    for (passenger in passangerList) {
                                        if (passenger.status.equals("Canceled", ignoreCase = true) || passenger.status.equals("Payment Pending", ignoreCase = true) ) {
                                            if (passenger.gender == "0" || passenger.gender == "0.00") {
                                                tableData.add(
                                                    mutableListOf(
                                                        index.toString(),
                                                        passenger.paXName,
                                                        "Male",
                                                        passenger.seatNumber,
                                                        passenger.ticketNumber
                                                    )
                                                )
                                            } else {
                                                tableData.add(
                                                    mutableListOf(
                                                        index.toString(),
                                                        passenger.paXName,
                                                        "Female",
                                                        passenger.seatNumber,
                                                        passenger.ticketNumber
                                                    )
                                                )
                                            }
                                            index++
                                        }
                                    }


                                    CancelTicketList.set(position,CancelTicketDataItem(CancelTicketList[position].bookingRefNo,CancelTicketList[position].loginId,CancelTicketList[position].apiData,CancelTicketList[position].registrationID,CancelTicketList[position].statusdesc,CancelTicketList[position].statusCode,
                                        passangerList, droppingTime,boardingTime,fromCity,toCity,busOperatorName,travelType,travelDate,passangerQunatity,tableData,true))


                                    if(upcomingadapter!=null){
                                       upcomingadapter.updateList(CancelTicketList,position)
                                    }


                                }
                                else{
                                    Toast.makeText(context,response.returnMessage, Toast.LENGTH_SHORT).show()
                                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                                        Constants.dialog.dismiss()
                                    }
                                    Constants.uploadDataOnFirebaseConsole(response.returnMessage,"CancelledRefundBusPassangerDetailsRequest",requireContext())
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
                        Constants.OpenPopUpForVeryfyOTP(requireContext())
                    }
                }
            }

        }

    }


    override fun refreshData(startDate: String, endDate: String) {
        // Call cancel API only
        if (!isAdded || !isViewReady) {
            BusTicketConsListClass.startDate = startDate
            BusTicketConsListClass.endDate = endDate
            return
        }
        hitApiForBusTicketCancelList(startDate, endDate)
    }


    override fun onResume() {
        super.onResume()

        val start = BusTicketConsListClass.startDate
        val end = BusTicketConsListClass.endDate

        if (!start.isNullOrEmpty() && !end.isNullOrEmpty()) {

            // Prevent duplicate API calls
            Log.d("CancelledBus", "Calling booking API from onResume")
            if (BusTicketConsListClass.startDate.isNotBlank() && BusTicketConsListClass.endDate.isNotBlank())
                hitApiForBusTicketCancelList(BusTicketConsListClass.startDate, BusTicketConsListClass.endDate)

        }
    }


    fun hitApiForBusTicketCancelList(startDate: String, endDate: String){

        val busRequery = BusBookingListReq(
            loginID = mStash!!.getStringValue(Constants.RegistrationId, ""),
            startDate = startDate,
            endDate = endDate
        )
        AppLog.d("BookingListReq", Gson().toJson(busRequery))
        viewModel.getBusCancelTicketRequest(busRequery).observe(viewLifecycleOwner) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        // pd.dismiss()
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                if (Constants.dialog != null && Constants.dialog.isShowing) {
                                    Constants.dialog.dismiss()
                                }
                                Log.d("CanceledTicket", response.toString())
                                Constants.uploadDataOnFirebaseConsole(Gson().toJson(response),"MyBookingBusActivity",requireContext())
                                AppLog.d("BookingListReqResponse", response.toString())

                                var dataItem = response.data
                                CancelTicketList.clear()
                                dataItem?.let { it1 ->
                                    BusCancelList.addAll(it1)
                                    CancelTicketList= BusCancelList
                                }
                                setRecyclerview()

                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        if (Constants.dialog != null && Constants.dialog.isShowing) {
                            Constants.dialog.dismiss()
                        }
                        binding.notfounddatalayout.visibility = View.VISIBLE
                        binding.showingCancelList.visibility = View.GONE
                    }

                    ApiStatus.LOADING -> {
                        Constants.OpenPopUpForVeryfyOTP(requireContext())
                    }
                }
            }
        }

    }


}