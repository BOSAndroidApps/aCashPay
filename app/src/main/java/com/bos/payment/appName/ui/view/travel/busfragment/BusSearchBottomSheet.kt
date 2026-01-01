package com.bos.payment.appName.ui.view.travel.busfragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.travel.bus.city.BusCityListReq
import com.bos.payment.appName.data.model.travel.bus.city.BusCityListRes
import com.bos.payment.appName.data.model.travel.flight.FlightsItem
import com.bos.payment.appName.data.model.travel.flight.SegmentsItem
import com.bos.payment.appName.data.model.travel.flight.fareBreakup
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.data.viewModelFactory.TravelViewModelFactory
import com.bos.payment.appName.databinding.BusSearchListPopupBinding

import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.travel.adapter.FlightTicketDetailsAdapter
import com.bos.payment.appName.ui.view.travel.adapter.PassangerDataList
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.fromBusCityName
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.toBusCityName
import com.bos.payment.appName.ui.view.travel.busactivity.BusSearchDetails
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.FlightDetails
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.adultCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.calculateTotalFlightDuration
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.checkFrom
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.checkFromBus
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.childCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.className
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.formatDate1
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.formatDate2
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.fromAirportCode
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.fromAirportName
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.fromCityName
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.fromCountryName
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.infantCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.toAirportCode
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.toAirportName
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.toCityName
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.toCountryName
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.totalCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.totalDurationTime
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.adultList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.childList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.farebreakupList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.flightDetailsPassangerDetail
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.infantList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.segmentListPassangerDetail
import com.bos.payment.appName.ui.viewmodel.TravelViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Constants.scanForActivity
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils
import com.bos.payment.appName.utils.Utils.generateRandomNumber
import com.bos.payment.appName.utils.Utils.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

class BusSearchBottomSheet:BottomSheetDialogFragment() {
    private lateinit var bin : BusSearchListPopupBinding
    var fareList : MutableList<fareBreakup> = arrayListOf()
    private var mStash: MStash? = null
    private lateinit var viewModel: TravelViewModel
    private var checkReverse = false
    private val myCalender = Calendar.getInstance()
    private var fromDesignation: String? = null
    private var fromDesignationName: String? = null
    private var toDesignation: String? = null
    private var toDesignationName: String? = null


    private val bookingDate = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

        myCalender.set(Calendar.YEAR, year)
        myCalender.set(Calendar.MONTH, monthOfYear)
        myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        // For API / storage
        val apiSdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val selectedDate = apiSdf.format(myCalender.time)

        // For UI display
        val displaySdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = displaySdf.format(myCalender.time)

        bin.datemonthdeparture.text = formatDate1(formattedDate)
        bin.dayyear.text = formatDate2(formattedDate)

        mStash?.setStringValue(Constants.dateAndTime, selectedDate)

        Log.d("SelectedDate", selectedDate)
    }



    companion object {
        const val TAG = "BusSearchSheet"
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bin = BusSearchListPopupBinding.inflate(inflater, container, false)
        mStash = MStash.getInstance(requireContext())

        viewModel = ViewModelProvider(this, TravelViewModelFactory(TravelRepository(RetrofitClient.apiAllTravelAPI,RetrofitClient.apiBusAddRequestlAPI))
        )[TravelViewModel::class.java]

        setonclicklistner()
        setCurrentDate()
        getAllTravelBusList()
        setDropDown()
        init()
        return bin.root
    }


    public fun setData() {

        bin.fromlayout.visibility = View.VISIBLE
        bin.fromDestinationSp.visibility = View.GONE

        bin.toDestinationSp.visibility = View.GONE
        bin.tolayout.visibility = View.VISIBLE


        if (checkFromBus) {
            bin.fromcityname.text = fromBusCityName
            bin.fromcityLayout.background = resources.getDrawable(R.drawable.fromtoback)
        }
        else {
            bin.tocityname.text = toBusCityName
            bin.tocityLayout.background = resources.getDrawable(R.drawable.fromtoback)
        }

    }

    private fun getAllTravelBusList() {
        val requestId = generateRandomNumber()
        mStash?.setStringValue(Constants.requestId, requestId)
        val busCityListReq = BusCityListReq(
            iPAddress = mStash!!.getStringValue(Constants.deviceIPAddress, ""),
            requestId =  mStash?.getStringValue(Constants.requestId, ""),
            imeINumber = "2232323232323",
            registrationID = mStash!!.getStringValue(Constants.MerchantId, "")
        )
        Log.d("busCityListReq", Gson().toJson(busCityListReq))
        viewModel.getAllBusCityList(busCityListReq).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Constants.dialog.dismiss()
                                getAllTravelBusListRes(response)
                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        Constants.dialog.dismiss()
                    }

                    ApiStatus.LOADING -> {
                       Constants.OpenPopUpForVeryfyOTP(requireContext())
                    }
                }
            }
        }
    }

    private fun getAllTravelBusListRes(response: BusCityListRes?) {
        if (response?.responseHeader?.errorCode == "0000"){
            Constants.busListName?.clear()
            Constants.toLocationName?.clear()
            Constants.busListName?.add("Select from location")
            Constants.toLocationName?.add("Select to location")
            response.cityDetails.forEach { busListData ->
                Constants.busListName?.add(busListData.cityName!!)
                Constants.toLocationName?.add(busListData.cityName!!)
                Constants.busListNameMap?.put(busListData.cityName!!, busListData.cityID!!)
                Constants.toLocationNameMap?.put(busListData.cityName!!, busListData.cityID!!)
            }
            Constants.getAllBusListAdapter?.notifyDataSetChanged()
        }
        else {
            toast("Error Message")
            Toast.makeText(requireContext(), response?.responseHeader?.errorDesc.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    private fun setDropDown() {
        /**************************************** Get All from location ***************************/
        Constants.getAllBusListAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_right_aligned, Constants.busListName!!)
        Constants.getAllBusListAdapter!!.setDropDownViewResource(R.layout.spinner_right_aligned)
        bin.fromDestinationSp.adapter = Constants.getAllBusListAdapter

        bin.fromDestinationSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (pos > 0) {
                    try {
                        fromDesignation = Constants.busListNameMap?.get(parent?.getItemAtPosition(pos)).toString()
                        fromDesignationName = parent?.getItemAtPosition(pos).toString()
                        mStash?.setStringValue(Constants.fromDesignationName, fromDesignationName)
                        mStash?.setStringValue(Constants.fromDesignationId, fromDesignation)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    fromDesignation = ""
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        Constants.getAllBusListAdapter!!.notifyDataSetChanged()

        /**************************************** Get All to location ***************************/
        Constants.getAllBusListAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_right_aligned, Constants.toLocationName!!)

        Constants.getAllBusListAdapter!!.setDropDownViewResource(R.layout.spinner_right_aligned)

        bin.toDestinationSp.adapter = Constants.getAllBusListAdapter

        Constants.getAllBusListAdapter!!.notifyDataSetChanged()

    }


    private fun setCurrentDate() {
        val calendar = Calendar.getInstance()

        // For UI display
        val displaySdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = displaySdf.format(myCalender.time)

        bin.datemonthdeparture.text = formatDate1(formattedDate)
        bin.dayyear.text = formatDate2(formattedDate)

        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val selectedDate = sdf.format(calendar.time)
        mStash?.setStringValue(Constants.dateAndTime, selectedDate)


    }


    fun init(){
        setSelectionTodayTomorrow()
        bin.todaylayout.background = resources.getDrawable(R.drawable.selectedbutton)
        bin.todaytxt.setTextColor(resources.getColor(R.color.blue))
        bin.todaylayout.backgroundTintList=null

        fromBusCityName =  mStash?.getStringValue(Constants.fromDesignationName, "")!!
        toBusCityName =    mStash?.getStringValue(Constants.toDesignationName, "")!!
        bin.fromcityname.text = fromBusCityName
        bin.tocityname.text   = toBusCityName

        fromDesignation = Constants.toLocationNameMap?.get(fromBusCityName).toString()
        mStash?.setStringValue(Constants.fromDesignationId, fromDesignation)
        mStash?.setStringValue(Constants.fromDesignationName, fromBusCityName)

        toDesignation = Constants.toLocationNameMap?.get(toBusCityName).toString()
        mStash?.setStringValue(Constants.toDesignationId, toDesignation)
        mStash?.setStringValue(Constants.toDesignationName, toBusCityName)

    }


    private fun setonclicklistner(){
        bin.cross.setOnClickListener {
            dialog!!.dismiss()
        }

        bin.todaylayout.setOnClickListener {
           val calendar = Calendar.getInstance()

           val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
           val selectedDate = sdf.format(calendar.time)
           mStash?.setStringValue(Constants.dateAndTime, selectedDate)
           setSelectionTodayTomorrow()
           bin.todaylayout.background = resources.getDrawable(R.drawable.selectedbutton)
           bin.todaytxt.setTextColor(resources.getColor(R.color.blue))
           bin.todaylayout.backgroundTintList=null
       }


        bin.tomorrowlayout.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, 1) // ➜ tomorrow

            val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            val tomorrowDate = sdf.format(calendar.time)

            mStash?.setStringValue(Constants.dateAndTime, tomorrowDate)

            setSelectionTodayTomorrow()

            bin.tomorrowlayout.background = resources.getDrawable(R.drawable.selectedbutton)
            bin.tomorrowtxt.setTextColor(resources.getColor(R.color.blue))
            bin.tomorrowlayout.backgroundTintList=null

        }


        bin.fromcityLayout.setOnClickListener {
            checkFromBus = true
            bin.fromlayout.visibility = View.GONE
            bin.fromDestinationSp.visibility = View.VISIBLE
            bin.fromcityLayout.background = resources.getDrawable(R.drawable.selectedbutton)
            bin.fromDestinationSp.showDialog()
        }


        bin.tocityLayout.setOnClickListener {
            checkFromBus = false
            bin.tolayout.visibility = View.GONE
            bin.toDestinationSp.visibility = View.VISIBLE
            bin.tocityLayout.background = resources.getDrawable(R.drawable.selectedbutton)
            bin.toDestinationSp.showDialog()
        }


        bin.fromDestinationSp.setBusCitySelectListener { item, _ ->
            fromBusCityName = item as String // your bus model
            setData()
        }

        bin.toDestinationSp.setBusCitySelectListener { item, _ ->
            toBusCityName = item as String // your bus model
            setData()


        }

        bin.reversetrip.setOnClickListener {

            fromBusCityName = bin.fromcityname.text.toString()

            toBusCityName = bin.tocityname.text.toString()

            if (checkReverse) {
                bin.fromcityname.text = toBusCityName
                bin.tocityname.text = fromBusCityName
                checkReverse = false
            }
            else {
                bin.tocityname.text = fromBusCityName
                bin.fromcityname.text = toBusCityName
                checkReverse = true
            }


        }

        bin.departureDatelayout.setOnClickListener {
            Utils.hideKeyboard(requireActivity())
            DatePickerDialog(
                requireActivity(),
                bookingDate,
                myCalender[Calendar.YEAR],
                myCalender[Calendar.MONTH],
                myCalender[Calendar.DAY_OF_MONTH]
            ).show()
        }

        bin.searchbuseslayout.setOnClickListener {
            validationCity()
        }


    }


    private fun validationCity() {
        val errorMessage: String? = when{
            fromBusCityName.isNullOrBlank() -> "Please select from location"
            toBusCityName.isNullOrBlank() -> "Please select to location"
            else -> null
        }
        if (errorMessage != null){
            toast(errorMessage)
        }
        else {
            fromDesignation = Constants.toLocationNameMap?.get(bin.fromcityname.text.toString()).toString()
            mStash?.setStringValue(Constants.fromDesignationId, fromDesignation)
            mStash?.setStringValue(Constants.fromDesignationName, bin.fromcityname.text.toString())

            toDesignation = Constants.toLocationNameMap?.get(bin.tocityname.text.toString()).toString()
            mStash?.setStringValue(Constants.toDesignationId, toDesignation)
            mStash?.setStringValue(Constants.toDesignationName, bin.tocityname.text.toString())

            (requireActivity() as? BusSearchDetails)?.let {
                it.finish()
                it.startActivity(it.intent)
            }
            dismiss()

        }
    }


    fun setSelectionTodayTomorrow(){
        bin.tomorrowlayout.background = resources.getDrawable(R.drawable.fromtoback)
        bin.tomorrowlayout.backgroundTintList =   ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        bin.tomorrowtxt.setTextColor(resources.getColor(R.color.mode_color))

        bin.todaylayout.background = resources.getDrawable(R.drawable.fromtoback)
        bin.todaylayout.backgroundTintList =   ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        bin.todaytxt.setTextColor(resources.getColor(R.color.mode_color))



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


    }


    private fun formatDate1(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM ", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            "Invalid Date"
        }
    }


    private fun formatDate2(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEE, yyyy", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            "Invalid Date"
        }
    }



}