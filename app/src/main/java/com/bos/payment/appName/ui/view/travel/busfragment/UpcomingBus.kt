package com.bos.payment.appName.ui.view.travel.busfragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bos.payment.appName.data.model.travel.bus.busRequery.BusRequeryReq
import com.bos.payment.appName.databinding.FragmentUpcomingBusBinding
import com.bos.payment.appName.ui.view.travel.adapter.BusTicketUpcomingAdapter
import com.bos.payment.appName.ui.view.travel.busactivity.BusTicketConsListClass
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusPassengerDetailsReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusPaxRequeryResponseReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketCancelReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketCancelResponseReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusTicketCancellationChargeReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.CancelTicketDetail
import com.bos.payment.appName.data.model.travel.bus.busTicket.DataItem
import com.bos.payment.appName.data.model.travel.bus.busTicket.PaXDetailsItem
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.data.viewModelFactory.TravelViewModelFactory
import com.bos.payment.appName.localdb.AppLog
import com.bos.payment.appName.localdb.AppLog.d
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.travel.busactivity.MyBookingBusActivity
import com.bos.payment.appName.ui.viewmodel.TravelViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.PD
import com.google.gson.Gson

class UpcomingBus : Fragment() {
    lateinit var binding: FragmentUpcomingBusBinding
    var UpcomingTicketList : MutableList<DataItem> = mutableListOf()
    lateinit var upcomingadapter:BusTicketUpcomingAdapter
    var tableData : MutableList<MutableList<String?>> = arrayListOf()
    var checkSelectedPassangerList : MutableList<Int> = mutableListOf()
    var selectedCardPosition:Int = 0
    private var mStash: MStash? = null
    private var passangerList : MutableList<PaXDetailsItem> = mutableListOf()
    private lateinit var viewModel: TravelViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding= FragmentUpcomingBusBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this, TravelViewModelFactory(TravelRepository(RetrofitClient.apiAllTravelAPI, RetrofitClient.apiBusAddRequestlAPI)))[TravelViewModel::class.java]
        mStash = MStash.getInstance(requireContext())


        if(BusTicketConsListClass.UpcomingTicketList!=null){
            UpcomingTicketList.clear()
            UpcomingTicketList=BusTicketConsListClass.UpcomingTicketList

        }

        setRecyclerview()

        return binding.root
    }


    private fun  setRecyclerview(){
        if(UpcomingTicketList.size>0){
            binding.notfounddatalayout.visibility=View.GONE
            binding.showingUpcomingList.visibility=View.VISIBLE
            upcomingadapter= BusTicketUpcomingAdapter(requireContext(),UpcomingTicketList,this)
            binding.showingUpcomingList.apply { layoutManager= LinearLayoutManager(context)
                adapter=upcomingadapter
            }
            upcomingadapter.notifyDataSetChanged()
        }
        else{
            binding.notfounddatalayout.visibility=View.VISIBLE
            binding.showingUpcomingList.visibility=View.GONE
        }

    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun hitApiForPassangerDetails(position:Int, bookingrefNo:String){
        var requestForPassengerDetails = BusPassengerDetailsReq(
            bookingRefNo = bookingrefNo
        )
        Log.d("BusRequery", Gson().toJson(requestForPassengerDetails))
        AppLog.d("PaxDetailsRequest",Gson().toJson(requestForPassengerDetails).toString())

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
                                   Log.d("PaxDetailsResponse", Gson().toJson(response))
                                   AppLog.d("PaxDetailsResponse",response.toString())
                                   Constants.uploadDataOnFirebaseConsole(Gson().toJson(response),"UpcomingBusPassangerDetailsRequest",requireContext())

                                   var getdata = response.data!![0].apiData
                                   if(getdata!=null){
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

                                       passangerList
                                           .filter { it.status.equals("Confirmed", true) || it.status.equals("Payment Pending", true) }
                                           .forEach { passenger ->
                                               val genderText = if (passenger.gender == "0" || passenger.gender == "0.00") "Male" else "Female"
                                               tableData.add(mutableListOf(passenger.paXName, genderText, passenger.seatNumber, passenger.ticketNumber))
                                           }


                                       UpcomingTicketList.set(position,DataItem(UpcomingTicketList[position].bookingRefNo,UpcomingTicketList[position].transportPNR,UpcomingTicketList[position].imeINumber,UpcomingTicketList[position].statusdesc,UpcomingTicketList[position].requestId,UpcomingTicketList[position].iPAddress,UpcomingTicketList[position].statusCode,
                                           passangerList,droppingTime,boardingTime,fromCity,toCity,busOperatorName,travelType,travelDate,passangerQunatity,tableData,true))


                                       if(upcomingadapter!=null){
                                           upcomingadapter.updateList(UpcomingTicketList,position)
                                       }
                                   }else{
                                       Toast.makeText(context,"Passenger details not found",Toast.LENGTH_SHORT).show()
                                   }

                               }
                                else{
                                    Toast.makeText(context,response.returnMessage,Toast.LENGTH_SHORT).show()
                                    Constants.uploadDataOnFirebaseConsole(response.returnMessage,"UpcomingBusPassangerDetailsRequest",requireContext())
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



    @RequiresApi(Build.VERSION_CODES.O)
    public fun hitApiForTicketCancellationCharge(selectionList:MutableList<Int>, position:Int){
        checkSelectedPassangerList = selectionList
        selectedCardPosition= position
        var cancellationReq = BusTicketCancellationChargeReq(
              bookingRefNo = UpcomingTicketList.get(position).bookingRefNo,
              ipAddress = mStash?.getStringValue(Constants.deviceIPAddress, ""),
              requestId = mStash!!.getStringValue(Constants.requestId, ""),
              imeiNumber = "0054748569",
              registrationId =  mStash?.getStringValue(Constants.MerchantId, ""),
              cancelTicketDetails =getCancelTicketList())

        Log.d("BusRequery", Gson().toJson(cancellationReq))

        AppLog.d("BusRequery",Gson().toJson(cancellationReq))

        viewModel.getBusTicketCancellationCharge(cancellationReq).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                if(response.statuss.equals("218")){
                                    Toast.makeText(context,response.message.toString(),Toast.LENGTH_SHORT).show()
                                }

                                Log.d("ChargeKey",response.cancellationChargeKey.toString())

                                Constants.uploadDataOnFirebaseConsole(Gson().toJson(response),"UpcomingBusTicketCancellationCharge", requireContext())
                                AppLog.d("ChargeKey",Gson().toJson(response))

                                 if(!response.cancellable.equals("false")){
                                     var cancellationReq = BusTicketCancelReq(
                                         bookingRefNo = UpcomingTicketList.get(position).bookingRefNo,
                                         ipAddress = mStash?.getStringValue(Constants.deviceIPAddress, ""),
                                         requestId = mStash!!.getStringValue(Constants.requestId, ""),
                                         imeiNumber = "0054748569",
                                         registrationId =  mStash?.getStringValue(Constants.MerchantId, ""),
                                         cancellationChargeKey = response.cancellationChargeKey.toString(),
                                         busTicketstoCancel =getCancelTicketList()
                                     )

                                     hitApiForCancelTicket(cancellationReq,  UpcomingTicketList.get(position).bookingRefNo)
                                 }
                                 else{
                                     Toast.makeText(context,response.responseHeader!!.errorInnerException,Toast.LENGTH_SHORT).show()
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


    private fun hitApiForCancelTicket(req:BusTicketCancelReq, bookingrefNo: String){
      Log.d("CancelBusRequest", Gson().toJson(req))
        viewModel.getBusTicketCancelRequest(req).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {

                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("CancelStatus",Gson().toJson(response))

                                if(response.responseHeader.errorCode.equals("0000")){
                                    Toast.makeText(context,response.message,Toast.LENGTH_SHORT).show()
                                    var cancellationResponseReq = BusTicketCancelResponseReq(
                                        bookingRefNo = bookingrefNo,
                                        loginId = mStash!!.getStringValue(Constants.RegistrationId, ""),
                                        errorCode = response.responseHeader.errorCode,
                                        createdby=mStash!!.getStringValue(Constants.RegistrationId, ""),
                                        apiResponse = Gson().toJson(response),
                                        registrationID = mStash!!.getStringValue(Constants.requestId, ""),
                                        errorDesc = response.responseHeader.errorDesc,
                                    )

                                    hitApiForUploadCancelResponseOnServer(cancellationResponseReq, bookingrefNo)

                                }

                                if(response.responseHeader.errorCode.equals("0010")){
                                    Log.d("ErrorResponseCancel",response.message)
                                    Toast.makeText(context,response.responseHeader.errorInnerException,Toast.LENGTH_SHORT).show()
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


    fun hitApiForUploadCancelResponseOnServer(cancelresponsereq:BusTicketCancelResponseReq,bookingrefNo:String){
      Log.d("CancelResponseReq", Gson().toJson(cancelresponsereq))
      viewModel.getBusTicketCancelResponseReq(cancelresponsereq).observe(this) { resource ->
          resource?.let {
              when (it.apiStatus) {
                  ApiStatus.SUCCESS -> {

                      it.data?.let { users ->
                          users.body()?.let { response ->
                              Log.d("CancelResponseResponse", Gson().toJson(response))
                              Constants.uploadDataOnFirebaseConsole(Gson().toJson(response),"UpcomingBusTicketCancelResponseReq",requireContext())
                              if(response.isSuccess!!){
                                    if(!BusTicketConsListClass.startDate!!.isNullOrEmpty()&& !BusTicketConsListClass.endDate!!.isNullOrEmpty()) {
                                        // hit requery api for status update
                                        getAllBusRequaryTicket(bookingrefNo)
                                    }
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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCancelTicketList(): List<CancelTicketDetail> {
        val ticketDetailsList = mutableListOf<CancelTicketDetail>()

        for (i in 0 until checkSelectedPassangerList.size) {

            var seatNo=passangerList.get(checkSelectedPassangerList.get(i)).seatNumber
            var ticketNo=passangerList.get(checkSelectedPassangerList.get(i)).ticketNumber
            var PNR= UpcomingTicketList.get(selectedCardPosition).transportPNR

            ticketDetailsList.add(
                CancelTicketDetail(
                    seatNumber  = seatNo!!,
                    ticketNumber= ticketNo!!,
                    transportPNR = PNR!!
                    )
                )

        }
        return ticketDetailsList
    }


    private fun getAllBusRequaryTicket(bookingRefNo : String) {
        val busRequery = BusRequeryReq(
            bookingRefNo = bookingRefNo,
            iPAddress = mStash!!.getStringValue(Constants.deviceIPAddress, ""),
            requestId = mStash!!.getStringValue(Constants.requestId, ""),
            imeINumber = "215237488",
            registrationID = mStash!!.getStringValue(Constants.MerchantId, ""))

        Log.d("BusRequery",Gson().toJson(busRequery))

        viewModel.getAllBusRequary(busRequery).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {

                        it.data?.let { users ->
                            users.body()?.let { response ->
                                 Log.d("RequeryRespo", Gson().toJson(response))

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


    fun hitApiforPassDetailsListResponse(PaxRequeryResponseReq: BusPaxRequeryResponseReq){
        Log.d("BusRequeryRequest", Gson().toJson(PaxRequeryResponseReq))

        viewModel.getPassangerDetailsRequest(PaxRequeryResponseReq).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                            Constants.dialog.dismiss()
                        }
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("RequeryResponse", Gson().toJson(response))
                                Constants.uploadDataOnFirebaseConsole(Gson().toJson(response),"UpcomingBusPassangerDetailsRequest",requireContext())
                                (activity as? MyBookingBusActivity)?.hitApiForBookingList(BusTicketConsListClass.startDate, BusTicketConsListClass.endDate)
                                (activity as? MyBookingBusActivity)?.hitApiForBusTicketCancelList(BusTicketConsListClass.startDate, BusTicketConsListClass.endDate)
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



}