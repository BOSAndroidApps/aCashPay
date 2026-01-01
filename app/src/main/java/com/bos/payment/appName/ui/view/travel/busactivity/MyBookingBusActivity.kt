package com.bos.payment.appName.ui.view.travel.busactivity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager2.widget.ViewPager2
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.travel.bus.busRequery.BusRequeryReq
import com.bos.payment.appName.data.model.travel.bus.busRequery.BusRequeryRes
import com.bos.payment.appName.data.model.travel.bus.busRequery.TicketDetailsForGenerateTicket
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusBookingListReq
import com.bos.payment.appName.data.model.travel.bus.busTicket.BusBookingListRes
import com.bos.payment.appName.data.model.travel.bus.busTicket.CancelTicketDataItem
import com.bos.payment.appName.data.model.travel.bus.busTicket.DataItem
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.data.viewModelFactory.TravelViewModelFactory
import com.bos.payment.appName.databinding.ActivityMyBookingBusBinding
import com.bos.payment.appName.localdb.AppLog
import com.bos.payment.appName.localdb.AppLog.d
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.travel.adapter.ViewPagerAdapter
import com.bos.payment.appName.ui.viewmodel.TravelViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils
import com.bos.payment.appName.utils.Utils.showLoadingDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.xml.datatype.DatatypeConstants.MONTHS


class MyBookingBusActivity : AppCompatActivity() {

    lateinit var binding: ActivityMyBookingBusBinding
    private lateinit var viewModel: TravelViewModel
    private var mStash: MStash? = null


    //val statusArray = listOf("Upcoming", "Pending", "Cancelled", "Completed")
    val statusArray = listOf("Booked Tickets", "Cancelled")
    private var BookingRefNo: MutableList<String> = mutableListOf()
    var startDate: String = ""
    var endDate: String = ""
    var BookingList: MutableList<DataItem> = mutableListOf()
    var BusCancelList: MutableList<CancelTicketDataItem> = mutableListOf()
    lateinit var viewPager: ViewPager2
    lateinit var tabLayout: TabLayout
    lateinit var viewPagerAdapter: ViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBookingBusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mStash = MStash.getInstance(this@MyBookingBusActivity)

        // for demo

        setView()
        setupTabs()
        setupViewModel()
        setclicklistner()

        startDate = getTodayIsoUtc()
        endDate = getTodayIsoUtc()

        BusTicketConsListClass.startDate = startDate
        BusTicketConsListClass.endDate = endDate

        val uiFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.startdate.setText(uiFormat.format(Date()))
        binding.enddate.setText(uiFormat.format(Date()))

        if(startDate.isNotBlank()&&endDate.isNotBlank()){
            refreshSelectedTab(startDate!!, endDate!!)
        }


    }


    fun setclicklistner() {

        binding.backBtn.setOnClickListener { finish() }

        binding.startdatecalendar.setOnClickListener {
            // Define the format to ISO 8601 with UTC timezone
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")

            // Get current date for initializing DatePicker
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                this,
                R.style.MyDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->

                    // Create Calendar from selected date
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(Calendar.YEAR, selectedYear)
                    selectedCalendar.set(Calendar.MONTH, selectedMonth)
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, selectedDay)
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, 0)
                    selectedCalendar.set(Calendar.MINUTE, 0)
                    selectedCalendar.set(Calendar.SECOND, 0)
                    selectedCalendar.set(Calendar.MILLISECOND, 0)

                    // Convert to ISO 8601 format in UTC
                    val isoDate = sdf.format(selectedCalendar.time)

                    // Store or use the ISO date string
                    startDate = isoDate
                    BusTicketConsListClass.startDate = isoDate
                    Log.d("Start Date", "" + startDate)
                    if (!startDate!!.isNullOrEmpty() && !endDate!!.isNullOrEmpty()) {
                       // hitApiForBookingList(startDate!!, endDate!!)

                        refreshSelectedTab(startDate!!, endDate!!)

                    }

                    // Display in user-friendly format (optional)
                    binding.startdate.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")

                },
                year,
                month,
                day
            )

            dpd.setOnShowListener {
                dpd.getButton(DatePickerDialog.BUTTON_POSITIVE)
                    ?.setTextColor(ContextCompat.getColor(this, R.color.green))
                dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                    ?.setTextColor(ContextCompat.getColor(this, R.color.red))
            }


            dpd.show()
        }

        binding.enddatecalendar.setOnClickListener {
            // Define the format to ISO 8601 with UTC timezone
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")

            // Get current date for initializing DatePicker
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                this,
                R.style.MyDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->

                    // Create Calendar from selected date
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(Calendar.YEAR, selectedYear)
                    selectedCalendar.set(Calendar.MONTH, selectedMonth)
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, selectedDay)
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, 0)
                    selectedCalendar.set(Calendar.MINUTE, 0)
                    selectedCalendar.set(Calendar.SECOND, 0)
                    selectedCalendar.set(Calendar.MILLISECOND, 0)

                    // Convert to ISO 8601 format in UTC
                    val isoDate = sdf.format(selectedCalendar.time)

                    // Store or use the ISO date string
                    endDate = isoDate
                    BusTicketConsListClass.endDate = isoDate
                    Log.d("End Date", "" + endDate)

                    if (!startDate!!.isNullOrEmpty() && !endDate!!.isNullOrEmpty()) {
                       // hitApiForBookingList(startDate!!, endDate!!)
                        refreshSelectedTab(startDate!!, endDate!!)

                    }

                    // Display in user-friendly format (optional)
                    binding.enddate.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")

                },
                year,
                month,
                day
            )

            dpd.setOnShowListener {
                dpd.getButton(DatePickerDialog.BUTTON_POSITIVE)
                    ?.setTextColor(ContextCompat.getColor(this, R.color.green))
                dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                    ?.setTextColor(ContextCompat.getColor(this, R.color.red))
            }


            dpd.show()
        }

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, TravelViewModelFactory(TravelRepository(RetrofitClient.apiAllTravelAPI, RetrofitClient.apiBusAddRequestlAPI)))[TravelViewModel::class.java]

    }

    private fun setView() {

        viewPager = binding.viewPager
        tabLayout = binding.tablayout

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val textView = tab.customView ?.findViewById<TextView>(R.id.tabText)
                textView?.isSelected = true // triggers ColorStateList
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val textView = tab.customView ?.findViewById<TextView>(R.id.tabText)
                textView?.isSelected = false
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })


        // Also mark the initially selected tab (0)
        (tabLayout.getTabAt(tabLayout.selectedTabPosition)?.customView ?.findViewById<TextView>(R.id.tabText))?.isSelected = true

        /*binding.tablelayout.visibility = View.GONE
        binding.notfounddatalayout.visibility = View.VISIBLE*/

    }

    private fun setupTabs() {
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = viewPagerAdapter
        viewPager.isUserInputEnabled = true

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabView = LayoutInflater.from(tabLayout.context).
            inflate(R.layout.tab_title, tabLayout,false)
            tabView.findViewById<TextView>(R.id.tabText).text = statusArray[position]
            tab.customView = tabView
        }.attach()

        tabLayout.post {
            val tab = tabLayout.getTabAt(0)
            val textView = tab?.customView?.findViewById<TextView>(R.id.tabText)
            textView?.isSelected = true
        }
    }

    private fun refreshSelectedTab(startDate: String, endDate: String) {
        val currentPosition = viewPager.currentItem
        val fragment = viewPagerAdapter.getFragment(currentPosition)

        if (fragment is BookingRefreshListener) {
            fragment.refreshData(startDate, endDate)
        }
    }

    interface BookingRefreshListener {
        fun refreshData(startDate: String, endDate: String)
    }


    private fun getTodayIsoUtc(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        return sdf.format(cal.time)
    }



}


/*fun hitApiForBookingList(startDate: String, endDate: String) {

       val busRequery = BusBookingListReq(
           loginID = mStash!!.getStringValue(Constants.RegistrationId, ""),
           startDate = startDate,
           endDate = endDate
       )
       AppLog.d("BookingListReq", Gson().toJson(busRequery))
       Log.d("BusTicketList",Gson().toJson(busRequery))
       viewModel.getBusBookListResponse(busRequery).observe(this) { resource ->
           resource?.let {
               when (it.apiStatus) {
                   ApiStatus.SUCCESS -> {
                       it.data?.let { users ->
                           users.body()?.let { response ->
                               Log.d("bookinglist", Gson().toJson(response))

                               if(Constants.dialog!=null && Constants.dialog.isShowing){
                                   Constants.dialog.dismiss()
                               }

                               Constants.uploadDataOnFirebaseConsole(Gson().toJson(response),"MyBookingBusActivityBusBookListResponse",this@MyBookingBusActivity)
                               AppLog.d("BookingListReqResponse", response.toString())
                               BookingList.clear()
                               BusTicketConsListClass.UpcomingTicketList.clear()
                               binding.tablelayout.visibility = View.VISIBLE
                               binding.notfounddatalayout.visibility = View.GONE
                               var dataItem = response.data

                               if(dataItem!!.size>0){
                                   dataItem?.let { it1 ->
                                       BookingList.addAll(it1)
                                       BusTicketConsListClass.UpcomingTicketList.addAll(BookingList)
                                   }

                                   val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
                                   viewPager.isUserInputEnabled = true
                                   viewPager.adapter = adapter

                                   TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                                       val tabView = LayoutInflater.from(tabLayout.context).inflate(R.layout.tab_title, null)
                                       val text = tabView.findViewById<TextView>(R.id.tabText)
                                       text.text = statusArray[position]
                                       tab.customView = tabView
                                   }.attach()

                                   hitApiForBusTicketCancelList(startDate!!, endDate!!)
                               }
                               else{
                                   hitApiForBusTicketCancelList(startDate!!, endDate!!)
                               }
                           }
                       }
                   }

                   ApiStatus.ERROR -> {
                       if(Constants.dialog!=null && Constants.dialog.isShowing){
                           Constants.dialog.dismiss()
                       }
                       binding.tablelayout.visibility = View.GONE
                       binding.notfounddatalayout.visibility = View.VISIBLE
                       hitApiForBusTicketCancelList(startDate!!, endDate!!)
                   }

                   ApiStatus.LOADING -> {
                       Constants.OpenPopUpForVeryfyOTP(this)
                   }
               }
           }
       }

   }*/

/*fun hitApiForBusTicketCancelList(startDate: String, endDate: String){
    val busRequery = BusBookingListReq(
        loginID = mStash!!.getStringValue(Constants.RegistrationId, ""),
        startDate = startDate,
        endDate = endDate
    )
    AppLog.d("BookingListReq", Gson().toJson(busRequery))
    viewModel.getBusCancelTicketRequest(busRequery).observe(this) { resource ->
        resource?.let {
            when (it.apiStatus) {
                ApiStatus.SUCCESS -> {
                   // pd.dismiss()
                    it.data?.let { users ->
                        users.body()?.let { response ->
                            Log.d("Response", response.toString())
                            Constants.uploadDataOnFirebaseConsole(Gson().toJson(response),"MyBookingBusActivity",this@MyBookingBusActivity)
                            AppLog.d("BookingListReqResponse", response.toString())
                            BusCancelList.clear()
                            BusTicketConsListClass.CancelTicketList.clear()
                            binding.tablelayout.visibility = View.VISIBLE
                            binding.notfounddatalayout.visibility = View.GONE
                            var dataItem = response.data

                            dataItem?.let { it1 ->
                                BusCancelList.addAll(it1)
                                BusTicketConsListClass.CancelTicketList.addAll(BusCancelList)
                            }

                            val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
                            viewPager.isUserInputEnabled = true
                            viewPager.adapter = adapter

                            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                                val tabView = LayoutInflater.from(tabLayout.context).inflate(R.layout.tab_title, null)
                                val text = tabView.findViewById<TextView>(R.id.tabText)
                                text.text = statusArray[position]
                                tab.customView = tabView
                            }.attach()

                        }
                    }
                }

                ApiStatus.ERROR -> {
                  //  pd.dismiss()
                    binding.tablelayout.visibility = View.GONE
                    binding.notfounddatalayout.visibility = View.VISIBLE
                }

                ApiStatus.LOADING -> {
                   // pd.show()
                }
            }
        }
    }

}*/