package com.bos.payment.appName.ui.view.travel.busfragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.travel.bus.busRequery.BusRequeryReq
import com.bos.payment.appName.data.model.travel.bus.busRequery.BusRequeryRes
import com.bos.payment.appName.data.model.travel.bus.city.BusCityListReq
import com.bos.payment.appName.data.model.travel.bus.city.BusCityListRes
import com.bos.payment.appName.data.model.travel.bus.city.CityDetails
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.data.viewModelFactory.TravelViewModelFactory
import com.bos.payment.appName.databinding.ActivityBookingTravelBinding
import com.bos.payment.appName.databinding.ActivityFlightSearchBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.BusDepartureDateAndTime
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.fromBusCityName
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.toBusCityName
import com.bos.payment.appName.ui.view.travel.busactivity.BusSearchDetails
import com.bos.payment.appName.ui.view.travel.busactivity.BusTicketConsListClass
import com.bos.payment.appName.ui.view.travel.busactivity.MyBookingBusActivity
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.DepartureDateAndTime
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.checkFromBus
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.travelDate
import com.bos.payment.appName.ui.view.travel.model.DateModel
import com.bos.payment.appName.ui.viewmodel.TravelViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Constants.busListName
import com.bos.payment.appName.utils.Constants.toLocationName
import com.bos.payment.appName.utils.Constants.toLocationNameMap
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils
import com.bos.payment.appName.utils.Utils.PD
import com.bos.payment.appName.utils.Utils.generateRandomNumber
import com.bos.payment.appName.utils.Utils.toast
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BusBookingMainFragment : Fragment() {
    private lateinit var bin: ActivityBookingTravelBinding
    private var mStash: MStash? = null
    private lateinit var viewModel: TravelViewModel
    private lateinit var busList: ArrayList<CityDetails>
    private val myCalender = Calendar.getInstance()
    private var fromDesignation: String? = null
    private var toDesignation: String? = null
    private var checkReverse = false
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

    val dateList = mutableListOf<DateModel>()
    val calendar = Calendar.getInstance()
    val dateFormatDayNumber = SimpleDateFormat("dd", Locale.getDefault())
    val dateFormatDayName = SimpleDateFormat("EEE", Locale.getDefault())
    private var activestatus: String = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        bin = ActivityBookingTravelBinding.inflate(inflater, container, false)
        activestatus = arguments?.getString("ActiveStatus").toString()

        return bin.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mStash = MStash(requireContext())

        viewModel = ViewModelProvider(this, TravelViewModelFactory(TravelRepository(RetrofitClient.apiAllTravelAPI, RetrofitClient.apiBusAddRequestlAPI)))[TravelViewModel::class.java]

        initView()

        if(activestatus.equals("N")){
            bin.inactiveservicelayout.visibility=View.VISIBLE
        }else {
            bin.inactiveservicelayout.visibility=View.GONE
            getAllTravelBusList()
        }
        setCurrentDate()
        btnListener()
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

    private fun setDropDown() {
        /**************************************** Get All from location ***************************/
        if(!Constants.busListName!!.isNullOrEmpty()){
            Constants.getAllBusListAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_right_aligned, Constants.busListName!!)
            Constants.getAllBusListAdapter!!.setDropDownViewResource(R.layout.spinner_right_aligned)
            bin.fromDestinationSp.adapter = Constants.getAllBusListAdapter

            Constants.getAllBusListAdapter!!.notifyDataSetChanged()

            /**************************************** Get All to location ***************************/
            Constants.getAllBusListAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_right_aligned, Constants.toLocationName!!)

            Constants.getAllBusListAdapter!!.setDropDownViewResource(R.layout.spinner_right_aligned)

            bin.toDestinationSp.adapter = Constants.getAllBusListAdapter

            Constants.getAllBusListAdapter!!.notifyDataSetChanged()
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
        Log.d("busCityListReq",Gson().toJson(busCityListReq))
        viewModel.getAllBusCityList(busCityListReq).observe(requireActivity()) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                            Constants.dialog.dismiss()
                        }
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                getAllTravelBusListRes(response)
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


    private fun getAllTravelBusListRes(response: BusCityListRes?) {
        if (response?.responseHeader?.errorCode == "0000"){
            Constants.busListName?.clear()
            Constants.toLocationName?.clear()
            Constants.busListName?.add("Select from location")
            Constants.toLocationName?.add("Select to location")
            Log.d("response",Gson().toJson(response.cityDetails))

            response.cityDetails.forEach { busListData ->
                Constants.busListName?.add(busListData.cityName!!)
                Constants.toLocationName?.add(busListData.cityName!!)
                Constants.busListNameMap?.put(busListData.cityName!!, busListData.cityID!!)
                Constants.toLocationNameMap?.put(busListData.cityName!!, busListData.cityID!!)
            }

            setDropDown()


        }
        else {
            toast("Error Message")
            Toast.makeText(requireContext(), response?.responseHeader?.errorDesc.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("ResourceAsColor")
    private fun btnListener() {
        bin.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
//                R.id.nav_home -> {
////                    navController.popBackStack()
//                }

                R.id.bookingBtn -> {
                    startActivity(Intent(requireContext(), MyBookingBusActivity::class.java))
                    //getAllBusRequaryTicket()


                }

            }
            return@setOnItemSelectedListener true
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

            startActivity(Intent(requireContext(), BusSearchDetails::class.java))

        }
    }


    @SuppressLint("ResourceAsColor")
    private fun initView() {
        busList = ArrayList()
        mStash = MStash.getInstance(requireContext())

        viewModel = ViewModelProvider(this, TravelViewModelFactory(TravelRepository(RetrofitClient.apiAllTravelAPI,RetrofitClient.apiBusAddRequestlAPI)))[TravelViewModel::class.java]

       // mStash!!.setStringValue(Constants.MerchantId, "AOP-554") // only for testing

        for (i in 0..3) { // Today + next 3 days
            if (i == 0) {
                dateList.add(DateModel(dateFormatDayNumber.format(calendar.time), "Today"))
            } else {
                dateList.add(DateModel(dateFormatDayNumber.format(calendar.time), dateFormatDayName.format(calendar.time)))
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        dateList.add(DateModel("", "Calendar"))

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


    fun setSelectionTodayTomorrow(){
        bin.tomorrowlayout.background = resources.getDrawable(R.drawable.fromtoback)
        bin.tomorrowlayout.backgroundTintList =   ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        bin.tomorrowtxt.setTextColor(resources.getColor(R.color.mode_color))

        bin.todaylayout.background = resources.getDrawable(R.drawable.fromtoback)
        bin.todaylayout.backgroundTintList =   ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        bin.todaytxt.setTextColor(resources.getColor(R.color.mode_color))
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