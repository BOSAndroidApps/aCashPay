package com.bos.payment.appName.ui.view.travel.busactivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bos.payment.appName.data.model.travel.bus.searchBus.BusSearchReq
import com.bos.payment.appName.data.model.travel.bus.searchBus.BusSearchRes
import com.bos.payment.appName.data.model.travel.bus.searchBus.Buses
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.data.viewModelFactory.TravelViewModelFactory
import com.bos.payment.appName.databinding.ActivityBusSearchDetailsBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.travel.adapter.BusSearchAdapter
import com.bos.payment.appName.ui.viewmodel.TravelViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.PD
import com.bos.payment.appName.utils.Utils.generateRandomNumber
import com.bos.payment.appName.utils.Utils.toast
import com.google.gson.Gson

class BusSearchDetails : AppCompatActivity() {

    private var mStash: MStash? = null

    private lateinit var viewModel: TravelViewModel
    private lateinit var bin: ActivityBusSearchDetailsBinding
    private lateinit var busDataList: ArrayList<Buses>
    private lateinit var busSearchAdapter: BusSearchAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bin = ActivityBusSearchDetailsBinding.inflate(layoutInflater)
        setContentView(bin.root)
        intiView()
        getAllBusSearchList()
        btnListener()

    }

    private fun getAllBusSearchList() {

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
        if (response.responseHeader?.errorCode == "0000"){
            mStash?.setStringValue(Constants.searchKey, response.searchKey.toString())
            Log.d("responseHeader", mStash?.getStringValue(Constants.searchKey, "").toString())
            busDataList.clear()
            response.buses.forEach { busList -> busDataList.add(busList) }
            busSearchAdapter.notifyDataSetChanged()
        }
        else {
            Log.d("BookList",response.responseHeader?.errorDesc.toString())
            Toast.makeText(this, response.responseHeader?.errorDesc.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun btnListener() {
        bin.backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private fun intiView() {

        mStash = MStash.getInstance(this)
        busDataList = ArrayList()

        bin.fromLocation.text = mStash?.getStringValue(Constants.fromDesignationName, "")
        bin.toLocation.text = mStash?.getStringValue(Constants.toDesignationName, "")
        bin.dateAndTime.text = mStash?.getStringValue(Constants.dateAndTime, "")

        viewModel = ViewModelProvider(this, TravelViewModelFactory(TravelRepository(RetrofitClient.apiAllTravelAPI,RetrofitClient.apiBusAddRequestlAPI))
        )[TravelViewModel::class.java]

        bin.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        busSearchAdapter = BusSearchAdapter(this, busDataList)
        bin.recyclerView.adapter = busSearchAdapter

    }
}