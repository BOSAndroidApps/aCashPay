package com.bos.payment.appName.ui.view.travel.busactivity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bos.payment.appName.data.model.travel.bus.searchBus.BusSearchReq
import com.bos.payment.appName.data.model.travel.bus.searchBus.BusSearchRes
import com.bos.payment.appName.data.model.travel.bus.searchBus.Buses
import com.bos.payment.appName.data.model.travel.flight.FlightsItem
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.data.viewModelFactory.TravelViewModelFactory
import com.bos.payment.appName.databinding.ActivityBusSearchDetailsBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.travel.adapter.BusSearchAdapter
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.AllBusList
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.BusListForFilter
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.busBoardingPointList
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.busDropingPointList
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.busOperatorNameList
import com.bos.payment.appName.ui.view.travel.busfragment.BusSearchBottomSheet
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.FlightListForFilter
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.TripDetailsList
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.after12pmlayout
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.after6amlayout
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.after6pmlayout
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.before6layout
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.FlightFilterActivity
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.FareBreakUpBottomSheet
import com.bos.payment.appName.ui.viewmodel.TravelViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Constants.boardingPoint
import com.bos.payment.appName.utils.Constants.convertDate
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.PD
import com.bos.payment.appName.utils.Utils.generateRandomNumber
import com.bos.payment.appName.utils.Utils.toast
import com.google.gson.Gson

class BusSearchDetails : AppCompatActivity() {
    private var mStash: MStash? = null
    private lateinit var viewModel: TravelViewModel
    private lateinit var bin: ActivityBusSearchDetailsBinding
    private lateinit var busDataList: MutableList<Buses>
    private lateinit var busSearchAdapter: BusSearchAdapter
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>


    companion object{
        var SearchBusesList: MutableList<Buses> = mutableListOf()
        lateinit var context: Context
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bin = ActivityBusSearchDetailsBinding.inflate(layoutInflater)
        setContentView(bin.root)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val code = result.data?.getStringExtra("code")
                if(code.equals("101")||code.equals("200")){
                    setdataonView()
                }
                else{

                }


                // Use the code
            }

        }

        intiView()
        getAllBusSearchList()
        btnListener()

    }


    fun setdataonView(){
        busDataList.clear()
        busDataList.addAll(SearchBusesList)
        if(busDataList.isNotEmpty()){
            bin.notfoundimage.visibility = View.GONE
            bin.recyclerView.visibility = View.VISIBLE
        }
        else {
            bin.notfoundimage.visibility = View.VISIBLE
            bin.recyclerView.visibility = View.GONE
        }
        busSearchAdapter.notifyDataSetChanged()
    }


    public fun getAllBusSearchList() {

        val busSearchReq = BusSearchReq(
            fromCity = mStash?.getStringValue(Constants.fromDesignationId,""),
            toCity = mStash?.getStringValue(Constants.toDesignationId, ""),
            travelDate = mStash?.getStringValue(Constants.dateAndTime, ""),
            iPAddress = mStash?.getStringValue(Constants.deviceIPAddress, ""),
            requestId = mStash?.getStringValue(Constants.requestId,""),
            imeINumber = "2232323232323",
            registrationID = mStash?.getStringValue(Constants.MerchantId, "")
        )

        Log.d("busSearchReq", Gson().toJson(busSearchReq))

        viewModel.getAllBusSearchList(busSearchReq).observe(this) { resource ->
            resource?.let {
                when(it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("BussearchList",response.buses[0]?.availableSeats.toString())
                                Log.d("BusList",Gson().toJson(response.buses))
                                Log.d("BusList",Gson().toJson(response))
                                getAllBusSearchListRes(response)
                            }
                        }
                    }
                    ApiStatus.ERROR -> {
                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                            Constants.dialog.dismiss()
                        }
                        bin.notfoundimage.visibility = View.VISIBLE
                        bin.recyclerView.visibility = View.GONE
                        Log.d("busListError",it.message.toString())
                        toast(it.message.toString())
                    }
                    ApiStatus.LOADING -> {
                        Constants.OpenPopUpForVeryfyOTP(this)
                    }
                }
            }
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun getAllBusSearchListRes(response: BusSearchRes) {
        if(Constants.dialog!=null && Constants.dialog.isShowing){
            Constants.dialog.dismiss()
        }

        busDataList.clear()
        AllBusList.clear()
        BusListForFilter.clear()
        busOperatorNameList.clear()
        busBoardingPointList.clear()
        busDropingPointList.clear()

        if (response.responseHeader?.errorCode == "0000"){
            mStash?.setStringValue(Constants.searchKey, response.searchKey.toString())
            Log.d("responseHeader", mStash?.getStringValue(Constants.searchKey, "").toString())

            response.buses.forEach { busList ->
                busDataList.add(busList)
                AllBusList.add(busList)
                BusListForFilter.add(busList)
            }
            busOperatorNameList.clear()
            busBoardingPointList.clear()
            busDropingPointList.clear()
            AllBusList.forEach{ bus->
                val name = bus.operatorName ?: return@forEach
                val boardingpoint = bus.boardingDetails
                val dropingpoint = bus.droppingDetails
                if (busOperatorNameList.none { it.first == name }) {
                    busOperatorNameList.add(name to false)
                }


                boardingpoint.forEach { it->
                    val name = it.boardingName ?: return@forEach
                    if (busBoardingPointList.none { it.first == name }) {
                        busBoardingPointList.add(name to false)
                    }
                }


                dropingpoint.forEach { it->
                    val name = it.droppingName ?: return@forEach
                    if (busDropingPointList.none { it.first == name }) {
                        busDropingPointList.add(name to false)
                    }
                }

            }


            before6layout = false
            after6amlayout = false
            after12pmlayout = false
            after6pmlayout = false

            if(busDataList.isNotEmpty()){
                bin.notfoundimage.visibility = View.GONE
                bin.recyclerView.visibility = View.VISIBLE
            }
            else {
                bin.notfoundimage.visibility = View.VISIBLE
                bin.recyclerView.visibility = View.GONE
            }

            Log.d("AllBusListResp", Gson().toJson(AllBusList))
            busSearchAdapter.notifyDataSetChanged()

        }
        else {
            bin.notfoundimage.visibility = View.VISIBLE
            bin.recyclerView.visibility = View.GONE

            Log.d("BookList",response.responseHeader?.errorDesc.toString())
            Toast.makeText(this, response.responseHeader?.errorDesc.toString(), Toast.LENGTH_SHORT).show()
        }

    }


    private fun btnListener() {

        bin.backBtn.setOnClickListener {
            onBackPressed()
        }

        bin.fab.setOnClickListener {
            Log.d("BusList", AllBusList.size.toString().plus( ""))
            val intent = Intent(this, BusFilterActivity::class.java)
            resultLauncher.launch(intent)
        }

        bin.edit.setOnClickListener {
            val bottomfrag = BusSearchBottomSheet()
            supportFragmentManager.let {
                bottomfrag.show(it, BusSearchBottomSheet.TAG)
            }
        }

    }


    private fun intiView() {

        mStash = MStash.getInstance(this)
        busDataList = ArrayList()

        bin.fromtocityname.text = "${mStash?.getStringValue(Constants.fromDesignationName, "")} to ${mStash?.getStringValue(Constants.toDesignationName, "")}"
        bin.dateAndTime.text = convertDate(mStash?.getStringValue(Constants.dateAndTime, ""))

        viewModel = ViewModelProvider(this, TravelViewModelFactory(TravelRepository(RetrofitClient.apiAllTravelAPI,RetrofitClient.apiBusAddRequestlAPI)))[TravelViewModel::class.java]

        bin.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        busSearchAdapter = BusSearchAdapter(this, busDataList)
        bin.recyclerView.adapter = busSearchAdapter

    }




}