package com.bos.payment.appName.ui.view.travel.airfragment

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
import com.bos.payment.appName.data.model.travel.flight.AirReprintReq
import com.bos.payment.appName.data.model.travel.flight.AirTicketCancelDetails
import com.bos.payment.appName.data.model.travel.flight.AirTicketCancelReq
import com.bos.payment.appName.data.model.travel.flight.FlightRequeryReq
import com.bos.payment.appName.data.model.travel.flight.airbookingticketList.AirTicketListResp
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.data.viewModelFactory.TravelViewModelFactory
import com.bos.payment.appName.databinding.FragmentUpcomingAirBinding
import com.bos.payment.appName.network.ApiInterface
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.travel.adapter.AirTicketUpcomingAdapter
import com.bos.payment.appName.ui.view.travel.adapter.AirTicketUpcomingAdapter.Companion.paxList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.FlightBookedTicketActivity.Companion.BookedTicketList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.FlightBookingPage
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.ui.viewmodel.TravelViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.PD
import com.google.gson.Gson
import kotlin.concurrent.thread

class UpcomingAir : Fragment() {
    lateinit var binding: FragmentUpcomingAirBinding
    private var mStash: MStash? = null
    private lateinit var viewModel: TravelViewModel
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    lateinit var upcominadapter : AirTicketUpcomingAdapter

    var checkSelectedPassangerList : MutableList<Int> = mutableListOf()
    var selectedCardPosition:Int = 0


    companion object{
        var AirBookingTicketList : MutableList<com.bos.payment.appName.data.model.travel.flight.airbookingticketList.DataItem> = mutableListOf()
        var AirUpcomingTicketList : MutableList<com.bos.payment.appName.data.model.travel.flight.airbookingticketList.DataItem> = mutableListOf()
        lateinit var BookingListResponse : AirTicketListResp
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding= FragmentUpcomingAirBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this, TravelViewModelFactory(TravelRepository(RetrofitClient.apiAllTravelAPI, RetrofitClient.apiBusAddRequestlAPI)))[TravelViewModel::class.java]
        getAllApiServiceViewModel = ViewModelProvider( this, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]

        mStash = MStash.getInstance(requireContext())


        setDataInList()

        return binding.root
    }



    fun setDataInList(){
        if(AirUpcomingTicketList!=null && AirUpcomingTicketList.size>0){
            upcominadapter = AirTicketUpcomingAdapter(requireContext(), AirUpcomingTicketList,this)
            binding.showtickets.adapter = upcominadapter
            binding.notfounddatalayout.visibility = View.GONE
            binding.showtickets.visibility = View.VISIBLE
            upcominadapter.notifyDataSetChanged()
        }
        else{
            binding.notfounddatalayout.visibility = View.VISIBLE
            binding.showtickets.visibility = View.GONE
        }

    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun hitApiForCancelAirTicket(selectionList:MutableList<Int>, position:Int){
        checkSelectedPassangerList= selectionList
        selectedCardPosition= position

        var airticketCancelReq = AirTicketCancelReq(
            ticketCancelDetailsList=getCancelTicketList(position),
            airlinePNR = AirUpcomingTicketList.get(position).airPNR,
            refNo = AirUpcomingTicketList.get(position).bookingRefNo,
            cancelCode = "005",
            reqRemarks = "Change in travel plans",
            cancellationType = 0,
            ipAddress = mStash?.getStringValue(Constants.deviceIPAddress, ""),
            requestId =  mStash!!.getStringValue(Constants.requestId, ""),
            imeINumber = "0054748569",
            registrationId = mStash?.getStringValue(Constants.MerchantId, "") /*"AOP-554"*/
        )
        Log.d("ticketcancelreq", Gson().toJson(airticketCancelReq))

        viewModel.getAirTicketCancelRequest(airticketCancelReq)
            .observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    if(response.responseHeader.errorCode.equals("0000")){
                                        Toast.makeText(requireContext(),"Ticket cancel successfully", Toast.LENGTH_SHORT).show()

                                        val airTicketReprintreq = AirReprintReq(
                                            BookingRefNo = AirUpcomingTicketList.get(position).bookingRefNo,
                                            airlinePNR =  AirUpcomingTicketList.get(position).airPNR,
                                            ipAddress = mStash?.getStringValue(Constants.deviceIPAddress, ""),
                                            requestId =  mStash?.getStringValue(Constants.requestId, ""),
                                            imeNumber = "2232323232323",
                                            registerId = mStash?.getStringValue(Constants.MerchantId, "")/*"AOP-554"*/
                                        )

                                        hitApiForTicketReprint(airTicketReprintreq)

                                    }
                                    else{
                                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                                            Constants.dialog.dismiss()
                                        }
                                        Toast.makeText(requireContext(),response.responseHeader.errorInnerException, Toast.LENGTH_SHORT).show()
                                    }

                                    Log.d("ticketcancelresp",Gson().toJson(response))

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
    private fun getCancelTicketList(position: Int): List<AirTicketCancelDetails>? {
        val ticketDetailsList = mutableListOf<AirTicketCancelDetails>()

            var flightId=AirUpcomingTicketList.get(position).apiData.airPNRDetails!![0].flights!![0].flightId

            for (i in 0 until checkSelectedPassangerList.size){
                var paxId = paxList.get(checkSelectedPassangerList.get(i)).paxId

                ticketDetailsList.add(AirTicketCancelDetails(
                        flightId  = flightId,
                        passangerId =  paxId,
                        segmentId ="0"
                    )
                )
            }

           return ticketDetailsList
    }


    fun hitApiForRequeryRequest(requeryReq : FlightRequeryReq){
        getAllApiServiceViewModel.getAirRequeryRequest(requeryReq)
            .observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->

                                    Constants.uploadDataOnFirebaseConsole(Gson().toJson(response),"UpcomingAirRequeryRequest",requireContext())

                                    Log.d("ticketrequeryresp",Gson().toJson(response))

                                    val mainActivity = requireActivity() as FlightBookingPage
                                    if(mainActivity !=null){
                                        mainActivity.hitApiForFlightTicketList()
                                    }
                                    //Toast.makeText(context,"Reprint Success",Toast.LENGTH_SHORT).show()
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


    fun hitApiForTicketReprint(airReprintTicketReq: AirReprintReq){

        Log.d("ticketreprintreq", Gson().toJson(airReprintTicketReq))

        viewModel.getAirTicketReprintRequest(airReprintTicketReq)
            .observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    BookedTicketList =response

                                    var requeryReq = FlightRequeryReq(
                                        loginId = mStash!!.getStringValue(Constants.RegistrationId, ""),
                                        bookingRefNo = response.bookingRefNo,
                                        ipAddress = mStash?.getStringValue(Constants.deviceIPAddress, ""),
                                        requestId = mStash?.getStringValue(Constants.requestId, ""),
                                        imeinumber = "2232323232323",
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


}