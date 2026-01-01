package com.bos.payment.appName.ui.view.travel.flightBooking.fragment

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.R
import com.bos.payment.appName.constant.ConstantClass
import com.bos.payment.appName.constant.CustomFuseLocationActivity
import com.bos.payment.appName.data.model.travel.flight.*
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.data.viewModelFactory.TravelViewModelFactory
import com.bos.payment.appName.databinding.ActivityFlightSearchBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.travel.adapter.FlightSpecialOfferAdapter
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.AllFlightList
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.DepartureDateAndTime
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.TripDetailsList
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.adultCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.after12pmlayout
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.after6amlayout
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.after6pmlayout
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.airportNameList
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.before6layout
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.bookingType
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.checkFrom
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.checkTo
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.childCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.className
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.classNumber
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
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.travelDate
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.travelType
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.adultList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.childList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.infantList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.FlightBookingPage
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.FlightDetailListActivity
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.AddGSTInformationBottomSheet.Companion.CheckBox
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.AddGSTInformationBottomSheet.Companion.companyName
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.AddGSTInformationBottomSheet.Companion.registrationNo
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.ReviewDetailsPassangersBottomSheet.Companion.passangerDetailsList
import com.bos.payment.appName.ui.viewmodel.TravelViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Constants.uploadDataOnFirebaseConsole
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.generateRandomNumber
import com.bos.payment.appName.utils.Utils.toast
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FlightMainFragment : Fragment(), FlightSpecialOfferAdapter.setClickListner {

    private var _binding: ActivityFlightSearchBinding? = null
    private val binding get() = _binding!!

    private var firstDateCalendar: Calendar? = null
    private lateinit var viewModel: TravelViewModel

    private lateinit var mStash: MStash
    private var customFuseLocation: CustomFuseLocationActivity? = null

    private var AirportDataList: MutableList<DataItem> = mutableListOf()
    private var specialFareList: MutableList<Pair<String, Boolean>> = mutableListOf()
    private var checkReverse = false
    private var activestatus: String = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View {
        _binding = ActivityFlightSearchBinding.inflate(inflater, container, false)
        activestatus = arguments?.getString("ActiveStatus").toString()



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mStash = MStash(requireContext())

        viewModel = ViewModelProvider(this, TravelViewModelFactory(TravelRepository(RetrofitClient.apiAllTravelAPI, RetrofitClient.apiBusAddRequestlAPI)))[TravelViewModel::class.java]


        setupUI()
        getFuseLocation()
        if(activestatus.equals("N")){
            binding.inactiveservicelayout.visibility=View.VISIBLE
        }else {
            binding.inactiveservicelayout.visibility=View.GONE
            hitApiForGetAirportList("")
        }

    }


    private fun setupUI() {
        selectionHoverColor()
        FlightConstant.bookingType = 0
        binding.onewaytxt.apply {
            setTextColor(resources.getColor(R.color.blue))
            background = resources.getDrawable(R.drawable.selectedbutton)
        }

        _binding!!.header.visibility=View.GONE
        setclickListner()
        setCurrentDate()
        addDataInList()
    }


    private fun getFuseLocation() {
        customFuseLocation = CustomFuseLocationActivity(requireActivity(), requireContext()) { location ->
            ConstantClass.latdouble = location.latitude
            ConstantClass.longdouble = location.longitude
        }
    }

    private fun setSpinnerData() {
        FlightConstant.FlightHint!!.clear()
        FlightConstant.FlightHint!!.add(DataItem("", "Select City", "", ""))

        FlightConstant.getAllFlightListAdapter = ArrayAdapter(
            requireContext(),
            R.layout.flightsearchspinnerlayout,
            FlightConstant.FlightHint!!
        )
        FlightConstant.getAllFlightListAdapter!!.setDropDownViewResource(R.layout.spinner_right_aligned)

        binding.searchableSp.adapter = FlightConstant.getAllFlightListAdapter
        binding.searchableSpto.adapter = FlightConstant.getAllFlightListAdapter
    }

    override fun onResume() {
        super.onResume()
        setData()
    }


    private fun addDataInList() {
        specialFareList.apply {
            clear()
            add(Pair("Defence Forces", false))
            add(Pair("Students", false))
            add(Pair("Senior Citizens", false))
            add(Pair("Others", false))
        }

        val adapter = FlightSpecialOfferAdapter(requireContext(), specialFareList, this)
        binding.extraofferList.adapter = adapter
        adapter.notifyDataSetChanged()
    }


    public fun setData() {

        binding.fromlayout.visibility = View.VISIBLE
        binding.searchableSp.visibility = View.GONE

        binding.searchableSpto.visibility = View.GONE
        binding.tolayout.visibility = View.VISIBLE

        totalCount = (adultCount + childCount + infantCount).toString()
        binding.totalcountpassanger.text = totalCount
        binding.classname.text = className

        if (childCount == 0 && infantCount == 0) {
            FlightConstant.datepassangerandclassstring = binding.datemonthdeparture.text.toString().plus("|").plus(adultCount).plus("Adult").plus("|").plus(
                className
            )
        } else {
            FlightConstant.datepassangerandclassstring = binding.datemonthdeparture.text.toString().plus("|").plus(totalCount).plus("Travellers").plus("|").plus(
                className
            )
        }

        if (checkFrom) {
            binding.fromcityname.text = fromCityName
            binding.fromairportcode.text = fromAirportCode
            binding.fromairportname.text = fromAirportName
            binding.fromcityLayout.background = resources.getDrawable(R.drawable.fromtoback)
        } else {
            binding.tocityname.text = toCityName
            binding.toairportcode.text = toAirportCode
            binding.toairportname.text = toAirportName
            binding.tocityLayout.background = resources.getDrawable(R.drawable.fromtoback)
        }

    }


    private fun setCurrentDate() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val formattedDate = String.format("%02d/%02d/%04d", day, month + 1, year)

        binding.datemonthdeparture.text = formatDate1(formattedDate)
        binding.dayyear.text = formatDate2(formattedDate)
        FlightConstant.travelDate = String.format("%02d/%02d/%04d", month + 1, day, year)
        FlightConstant.DepartureDateAndTime = FlightConstant.travelDate
    }


    private fun setclickListner() {

        binding.fromcityLayout.setOnClickListener {
            checkFrom = true
            checkTo = false
            binding.fromlayout.visibility = View.GONE
            binding.searchableSp.visibility = View.VISIBLE
            binding.fromcityLayout.background = resources.getDrawable(R.drawable.selectedbutton)
            binding.searchableSp.openDialog()
        }


        binding.tocityLayout.setOnClickListener {
            checkFrom = false
            checkTo = true
            binding.tolayout.visibility = View.GONE
            binding.searchableSpto.visibility = View.VISIBLE
            binding.tocityLayout.background = resources.getDrawable(R.drawable.selectedbutton)
            binding.searchableSpto.openDialog()
        }


        binding.onewaytxt.setOnClickListener {
            selectionHoverColor()
            bookingType = 0
            binding.onewaytxt.setTextColor(resources.getColor(R.color.blue))
            binding.onewaytxt.background = resources.getDrawable(R.drawable.selectedbutton)
        }


        binding.roundtriptxt.setOnClickListener {
            selectionHoverColor()
            bookingType = 1
            binding.roundtriptxt.setTextColor(resources.getColor(R.color.blue))
            binding.roundtriptxt.background = resources.getDrawable(R.drawable.selectedbutton)
        }


        binding.multicitytxt.setOnClickListener {
            selectionHoverColor()
            bookingType = 2
            binding.multicitytxt.setTextColor(resources.getColor(R.color.blue))
            binding.multicitytxt.background = resources.getDrawable(R.drawable.selectedbutton)
        }


        binding.departureDatelayout.setOnClickListener {

            // Get current date for initializing DatePicker0
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(
                requireContext(),
                R.style.MyDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                    firstDateCalendar = c
                    val formattedDate = String.format(
                        "%02d/%02d/%04d",
                        selectedDay,
                        selectedMonth + 1,
                        selectedYear
                    )
                    binding.datemonthdeparture.text = formatDate1(formattedDate)
                    binding.dayyear.text = formatDate2(formattedDate)
                    travelDate = String.format(
                        "%02d/%02d/%04d",
                        selectedMonth + 1,
                        selectedDay,
                        selectedYear
                    )

                    // Get current time
                    val calendar = Calendar.getInstance()
                    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                    val currentMinute = calendar.get(Calendar.MINUTE)

                    val time = String.format("%02d:%02d", currentHour, currentMinute)
                    DepartureDateAndTime = "$travelDate"

                },
                year,
                month,
                day
            )

            dpd!!.setOnShowListener {
                dpd!!.getButton(DatePickerDialog.BUTTON_POSITIVE)
                    ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                dpd!!.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                    ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
            dpd!!.datePicker.minDate = c.timeInMillis

            dpd!!.show()

        }


        binding.addreturndatelayout.setOnClickListener {

            binding.crossicon.visibility = View.GONE
            binding.addreturnlayout.visibility = View.VISIBLE
            binding.returndatelayout.visibility = View.INVISIBLE

            selectionHoverColor()
            binding.onewaytxt.setTextColor(resources.getColor(R.color.blue))
            binding.onewaytxt.background = resources.getDrawable(R.drawable.selectedbutton)
            bookingType = 0


            val calendar = firstDateCalendar?.clone() as? Calendar ?: Calendar.getInstance()

            val year2 = calendar.get(Calendar.YEAR)
            val month2 = calendar.get(Calendar.MONTH)
            val day2 = calendar.get(Calendar.DAY_OF_MONTH)

            val secondDatePicker = DatePickerDialog(
                requireContext(), R.style.MyDatePickerDialogTheme, { _, y, m, d ->
                    val formattedDate = String.format("%02d/%02d/%04d", d, m + 1, y)
                    binding.addreturnlayout.visibility = View.GONE
                    binding.returndatelayout.visibility = View.VISIBLE
                    binding.crossicon.visibility = View.VISIBLE
                    selectionHoverColor()
                    bookingType = 1
                    binding.roundtriptxt.setTextColor(resources.getColor(R.color.blue))
                    binding.roundtriptxt.background =
                        resources.getDrawable(R.drawable.selectedbutton)
                    binding.datemonthreturn.text = formatDate1(formattedDate)
                    binding.dayyearreturn.text = formatDate2(formattedDate)

                },
                year2, month2, day2
            )

            secondDatePicker!!.setOnShowListener {
                secondDatePicker!!.getButton(DatePickerDialog.BUTTON_POSITIVE)
                    ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                secondDatePicker!!.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                    ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
            secondDatePicker!!.datePicker.minDate = calendar.timeInMillis

            secondDatePicker.show()


        }


        binding.crossicon.setOnClickListener {

            binding.crossicon.visibility = View.GONE
            binding.addreturnlayout.visibility = View.VISIBLE
            binding.returndatelayout.visibility = View.INVISIBLE

            selectionHoverColor()
            binding.onewaytxt.setTextColor(resources.getColor(R.color.blue))
            binding.onewaytxt.background = resources.getDrawable(R.drawable.selectedbutton)
            bookingType = 0

        }


        binding.travellersandclasslayout.setOnClickListener {
          /*  val bottomfrag = SelectTravellersClassBottomSheet()
            supportFragmentManager.let { bottomfrag.show(it, SelectTravellersClassBottomSheet.TAG) }*/

            val bottomSheet = SelectTravellersClassBottomSheet()
            bottomSheet.show(childFragmentManager, SelectTravellersClassBottomSheet.TAG)
        }


        binding.reversetrip.setOnClickListener {

            fromCityName = binding.fromcityname.text.toString()
            fromAirportCode = binding.fromairportcode.text.toString()
            fromAirportName = binding.fromairportname.text.toString()

            toCityName = binding.tocityname.text.toString()
            toAirportCode = binding.toairportcode.text.toString()
            toAirportName = binding.toairportname.text.toString()

            var tempCountryName: String

            tempCountryName = fromCountryName
            fromCountryName = toCountryName
            toCountryName = tempCountryName


            if (checkReverse) {
                binding.fromcityname.text = toCityName
                binding.fromairportcode.text = toAirportCode
                binding.fromairportname.text = toAirportName

                binding.tocityname.text = fromCityName
                binding.toairportcode.text = fromAirportCode
                binding.toairportname.text = fromAirportName

                checkReverse = false
            }
            else {
                binding.tocityname.text = fromCityName
                binding.toairportcode.text = fromAirportCode
                binding.toairportname.text = fromAirportName

                binding.fromcityname.text = toCityName
                binding.fromairportcode.text = toAirportCode
                binding.fromairportname.text = toAirportName


                checkReverse = true
            }


        }


        binding.searchflightlayout.setOnClickListener {

            if (!fromCountryName.equals(
                    "India",
                    ignoreCase = true
                ) || !toCountryName.equals("India", ignoreCase = true)
            ) {
                travelType = 1
            } else {
                travelType = 0
            }



            if (bookingType !in 0..2) {
                toast("Invalid Booking Type")
                return@setOnClickListener
            }

            if (adultCount < 1) {
                toast("At least one adult must be selected")
                return@setOnClickListener
            }

            if (childCount < 0 || infantCount < 0) {
                toast("Child/Infant count cannot be negative")
                return@setOnClickListener
            }

            if (classNumber !in listOf("0", "1", "2", "3")) {
                toast("Invalid travel class selected")
                return@setOnClickListener
            }

            if (mStash?.getStringValue(Constants.MerchantId, "").isNullOrEmpty()) {
                toast("Merchant ID is missing")
                return@setOnClickListener
            }

            if (mStash?.getStringValue(Constants.deviceIPAddress, "").isNullOrEmpty()) {
                toast("Device IP address is missing")
                return@setOnClickListener
            }

            if (getTripInfoList().isEmpty()) {
                toast("Trip information is missing")
                return@setOnClickListener //
            }

            if (travelDate.equals("")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                travelDate = String.format("%02d/%02d/%04d", month + 1, day, year)
            }

            Log.d("TravelType", "$travelType")
            // All validations passed
            hitApiForSearchFlight()
        }


        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
//                R.id.nav_home -> {
////                    navController.popBackStack()
//                }

                R.id.bookingBtn -> {
                    startActivity(Intent(requireContext(), FlightBookingPage::class.java))
                }

            }
            return@setOnItemSelectedListener true
        }


    }


    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            requireContext(),
            R.style.MyDatePickerDialogTheme,
            { _, y, m, d ->
                val formattedDate = String.format("%02d/%02d/%04d", d, m + 1, y)
                binding.datemonthdeparture.text = formatDate1(formattedDate)
                binding.dayyear.text = formatDate2(formattedDate)
                FlightConstant.travelDate = String.format("%02d/%02d/%04d", m + 1, d, y)
            },
            year, month, day
        )

        dpd.datePicker.minDate = c.timeInMillis
        dpd.show()
    }


    private fun selectionHoverColor() {
        binding.onewaytxt.setTextColor(resources.getColor(R.color.text_color))
        binding.roundtriptxt.setTextColor(resources.getColor(R.color.text_color))
        binding.multicitytxt.setTextColor(resources.getColor(R.color.text_color))
        binding.onewaytxt.background = null
        binding.roundtriptxt.background = null
        binding.multicitytxt.background = null
    }


    override fun itemclickListner(position: Int) {
        when (specialFareList[position].first) {
            "Senior Citizens" -> {
                FlightConstant.srCitizen = true
                FlightConstant.studentFare = false
                FlightConstant.defenceFare = false
            }
            "Students" -> {
                FlightConstant.srCitizen = false
                FlightConstant.studentFare = true
                FlightConstant.defenceFare = false
            }
            "Defence Forces" -> {
                FlightConstant.srCitizen = false
                FlightConstant.studentFare = false
                FlightConstant.defenceFare = true
            }
            else -> {
                FlightConstant.srCitizen = false
                FlightConstant.studentFare = false
                FlightConstant.defenceFare = false
            }
        }
    }

    private fun hitApiForGetAirportList(airportFilterString: String) {
        val airportListReq = AirportListReq(searchTerm = airportFilterString)

        Log.d("AirportListRequest", Gson().toJson(airportListReq))
        viewModel.getAirportListRequet(airportListReq).observe(viewLifecycleOwner) { resource ->
            when (resource.apiStatus) {
                ApiStatus.SUCCESS -> {
                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                        Constants.dialog.dismiss()
                    }
                    val response = resource.data?.body()
                    Log.d("AirportListResponse", Gson().toJson(response))
                    uploadDataOnFirebaseConsole(Gson().toJson(response),
                        "FlightMainFragmentAirportList",
                        requireContext())

                    response?.data?.let { list ->
                        AirportDataList.clear()
                        FlightConstant.FlightList!!.clear()
                        AirportDataList.addAll(list)
                        FlightConstant.FlightList!!.addAll(AirportDataList)
                        setSpinnerData()
                    }
                }
                ApiStatus.ERROR -> if(Constants.dialog!=null && Constants.dialog.isShowing){
                    Constants.dialog.dismiss()
                }
                ApiStatus.LOADING ->   Constants.OpenPopUpForVeryfyOTP(requireContext())
            }
        }
    }

    private fun hitApiForSearchFlight() {
        val requestId = generateRandomNumber()
        mStash.setStringValue(Constants.requestId, requestId)

        val req = FlightSearchReq(
            requestId = mStash.getStringValue(Constants.requestId, ""),
            imeInumber = "0054748569",
            travelType = FlightConstant.travelType,
            bookingType = FlightConstant.bookingType,
            adultCount = FlightConstant.adultCount.toString(),
            childCount = FlightConstant.childCount.toString(),
            infantCount = FlightConstant.infantCount.toString(),
            classOfTravel = FlightConstant.classNumber,
            inventoryType = 0,
            sourceType = 0,
            srCitizenSearch = FlightConstant.srCitizen,
            studentFareSearch = FlightConstant.studentFare,
            defenceFareSearch = FlightConstant.defenceFare,
            registartionId = mStash.getStringValue(Constants.MerchantId, ""),
            ipAddress = mStash.getStringValue(Constants.deviceIPAddress, ""),
            filterAirlineList = getFilterAirLineList(),
            tripeInfoList = listOf(
                TripInfo(
                    origin = binding.fromairportcode.text.toString(),
                    destination = binding.toairportcode.text.toString(),
                    traveldate = FlightConstant.travelDate,
                    tripId = 0
                )
            )
        )

        Log.d("FlightSearchRequest", Gson().toJson(req))

        viewModel.getFlightSearchRequest(req).observe(viewLifecycleOwner) { resource ->
            when (resource.apiStatus) {
                ApiStatus.SUCCESS -> {
                    val response = resource.data?.body()
                    uploadDataOnFirebaseConsole(Gson().toJson(response),"FlightMainActivitySearchRequest",requireContext())
                    Log.d("FlightSearchResponse", Gson().toJson(response))

                    if (response?.responseHeader?.errorCode == "0000") {
                        adultList.clear()
                        childList.clear()
                        infantList.clear()
                        passangerDetailsList.clear()
                        companyName = ""
                        registrationNo = ""
                        CheckBox = false
                        before6layout = false
                        after6amlayout = false
                        after12pmlayout = false
                        after6pmlayout = false
                        if (childCount == 0 && infantCount == 0) {
                            FlightConstant.datepassangerandclassstring =
                                binding.datemonthdeparture.text.toString().plus("|").plus(adultCount).plus("Adult")
                                    .plus("|").plus(className)
                        } else {
                            FlightConstant.datepassangerandclassstring =
                                binding.datemonthdeparture.text.toString().plus("|").plus(totalCount)
                                    .plus("Travellers").plus("|").plus(className)
                        }
                        mStash.setStringValue(Constants.FlightSearchKey, response.searchKey)
                        Log.d("SearchKey", response.searchKey)
                        var getData = response.tripDetails
                        mStash.setFlightSearchData("flightsearchdata", getData, requireContext())
                        TripDetailsList.clear()
                        getData?.let { it1 -> TripDetailsList.addAll(it1) }

                        airportNameList.clear()

                        airportNameList = TripDetailsList[0]?.flights
                            ?.mapNotNull { it.segments?.getOrNull(0)?.airlineName }
                            ?.distinct()
                            ?.map { it to false }
                            ?.toMutableList() ?: mutableListOf()

                        Log.d("AirlineName",Gson().toJson(airportNameList))

                        AllFlightList.clear()

                        TripDetailsList[0]!!.flights?.let { AllFlightList.addAll(it) }

                        fromCityName = binding.fromcityname.text.toString()
                        toCityName = binding.tocityname.text.toString()

                        startActivity(Intent(requireContext(), FlightDetailListActivity::class.java))
                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                            Constants.dialog.dismiss()
                        }
                    }
                    else {
                        toast(response?.responseHeader?.errorDesc ?: "Error")
                    }
                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                        Constants.dialog.dismiss()
                    }
                }
                ApiStatus.ERROR -> {
                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                        Constants.dialog.dismiss()
                    }
                    Toast.makeText(requireContext(), "Server busy", Toast.LENGTH_SHORT).show()
                }
                ApiStatus.LOADING -> Constants.OpenPopUpForVeryfyOTP(requireContext())
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    fun getFilterAirLineList(): List<FilterAirLine> {
        var airlinecodeList: MutableList<FilterAirLine> = mutableListOf()
        airlinecodeList.add(FilterAirLine(airlineCode = ""))
        return airlinecodeList
    }

    fun getTripInfoList(): List<TripInfo> {
        var airlinecodeList: MutableList<TripInfo> = mutableListOf()
        airlinecodeList.add(
            TripInfo(
                origin = binding.fromairportcode.text.toString(),
                destination = binding.toairportcode.text.toString(),
                traveldate = travelDate,
                tripId = 0
            )
        )
        return airlinecodeList
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
