package com.bos.payment.appName.ui.view.travel.flightBooking.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.travel.flight.GetAirTicketListReq
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.databinding.ActivityFlightBookingPageBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.travel.adapter.FlightViewPagerAdapter
import com.bos.payment.appName.ui.view.travel.adapter.ViewPagerAdapter
import com.bos.payment.appName.ui.view.travel.airfragment.CancelledRefundFlight.Companion.AirCancelTicketList
import com.bos.payment.appName.ui.view.travel.airfragment.CompletedAir.Companion.AirCompleteTicketList
import com.bos.payment.appName.ui.view.travel.airfragment.UpcomingAir.Companion.AirBookingTicketList
import com.bos.payment.appName.ui.view.travel.airfragment.UpcomingAir.Companion.AirUpcomingTicketList
import com.bos.payment.appName.ui.view.travel.airfragment.UpcomingAir.Companion.BookingListResponse
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FlightBookingPage : AppCompatActivity() {
    lateinit var binding : ActivityFlightBookingPageBinding
    val statusArray = listOf("Upcoming", "Cancelled", "Completed")
    lateinit var  viewPager: ViewPager2
    lateinit var  tabLayout: TabLayout
    private var mStash: MStash? = null
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         binding = ActivityFlightBookingPageBinding.inflate(layoutInflater)
         setContentView(binding.root)

         setView()
         mStash = MStash.getInstance(this)
         getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]
         hitApiForFlightTicketList()
         setOnClickListner()

    }

    fun setOnClickListner(){
        binding.backBtn.setOnClickListener{
            finish()
        }
    }


    private fun setView(){

        viewPager = binding.viewPager
        tabLayout = binding.tablayout

        val adapter = FlightViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.isUserInputEnabled = true
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabView =  LayoutInflater.from(tabLayout.context).inflate(R.layout.tab_title, null)
            val text=tabView.findViewById<TextView>(R.id.tabText)
            text.text = statusArray[position]
            tab.customView= tabView
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val textView = tab.customView as? TextView
                textView?.isSelected = true // triggers ColorStateList
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val textView = tab.customView as? TextView
                textView?.isSelected = false
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        // Also mark the initially selected tab (0)
        (tabLayout.getTabAt(tabLayout.selectedTabPosition)?.customView as? TextView)?.isSelected = true

    }


    fun hitApiForFlightTicketList(){
        var request = GetAirTicketListReq(
            loginId = mStash!!.getStringValue(Constants.RegistrationId, "")
        )

        Log.d("ticketListreq", Gson().toJson(request))

        getAllApiServiceViewModel.getAirTicketListRequest(request).observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    Constants.uploadDataOnFirebaseConsole(Gson().toJson(response),"FlightBookingPageAirTicketListRequest",this@FlightBookingPage)
                                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                                        Constants.dialog.dismiss()
                                    }
                                    var  AirBookingTicketList= response.data!!

                                    AirCancelTicketList = AirBookingTicketList.mapNotNull { booking ->
                                        val matchingAirPnrDetails = booking.apiData?.airPNRDetails?.mapNotNull { airPnrDetail ->
                                            val filteredPax = airPnrDetail.paxTicketDetails
                                                ?.filter { pax -> pax.ticketStatus.equals("Tocancel", ignoreCase = true) }
                                                ?.takeIf { it.isNotEmpty() }

                                            if (filteredPax != null) {
                                                airPnrDetail.copy(paxTicketDetails = filteredPax)
                                            } else null
                                        }?.takeIf { it.isNotEmpty() }

                                        if (matchingAirPnrDetails != null) {
                                            booking.copy(
                                                apiData = booking.apiData.copy(airPNRDetails = matchingAirPnrDetails)
                                            )
                                        } else null
                                    }.toMutableList()

                                    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

                                    val currentDateMinus24Hours = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, -24) }.time

                                    AirCompleteTicketList = AirBookingTicketList.mapNotNull { booking ->
                                        try {
                                            val travelDate = booking.travelDate?.let { dateFormat.parse(it) }

                                            // Check if travelDate is before (completed tickets)
                                            val isComplete = travelDate != null && travelDate.before(currentDateMinus24Hours)

                                            if (!isComplete) return@mapNotNull null

                                            // Filter LIVE-only pax details
                                            val filteredAirPnrDetails = booking.apiData?.airPNRDetails?.mapNotNull { airPnrDetail ->
                                                val livePaxList = airPnrDetail.paxTicketDetails?.filter { pax ->
                                                    pax.ticketStatus.equals("Live", ignoreCase = true)
                                                }

                                                if (!livePaxList.isNullOrEmpty()) {
                                                    airPnrDetail.copy(paxTicketDetails = livePaxList)
                                                } else {
                                                    null
                                                }
                                            }

                                            if (!filteredAirPnrDetails.isNullOrEmpty()) {
                                                booking.copy(apiData = booking.apiData?.copy(airPNRDetails = filteredAirPnrDetails)!!)
                                            } else {
                                                null
                                            }

                                        } catch (e: Exception) {
                                            null
                                        }
                                    }.toMutableList()

                                    // Get start of today (00:00)
                                    val today = Calendar.getInstance().apply {
                                        set(Calendar.HOUR_OF_DAY, 0)
                                        set(Calendar.MINUTE, 0)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }.time

                                    AirUpcomingTicketList = AirBookingTicketList.mapNotNull { booking ->
                                        try {
                                            val travelDate = booking.travelDate?.let { dateFormat.parse(it) }

                                            val isUpcoming = travelDate != null && !travelDate.before(today)

                                            if (!isUpcoming) return@mapNotNull null

                                            // Filter LIVE-only pax details
                                            val filteredAirPnrDetails = booking.apiData?.airPNRDetails?.mapNotNull { airPnrDetail ->
                                                val livePaxList = airPnrDetail.paxTicketDetails?.filter { pax ->
                                                    pax.ticketStatus.equals("Live", ignoreCase = true)
                                                }

                                                if (!livePaxList.isNullOrEmpty()) {
                                                    airPnrDetail.copy(paxTicketDetails = livePaxList)
                                                } else {
                                                    null
                                                }
                                            }

                                            if (!filteredAirPnrDetails.isNullOrEmpty()) {
                                                booking.copy(apiData = booking.apiData?.copy(
                                                    airPNRDetails = filteredAirPnrDetails
                                                )!!
                                                )
                                            } else {
                                                null
                                            }

                                        } catch (e: Exception) {
                                            null
                                        }
                                    }.toMutableList()

                                    Log.d("BookingList",Gson().toJson(AirBookingTicketList))
                                    setView()

                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            if(Constants.dialog!=null && Constants.dialog.isShowing){
                                Constants.dialog.dismiss()
                            }
                        }

                        ApiStatus.LOADING -> {
                            Constants.OpenPopUpForVeryfyOTP(this)
                        }

                    }
                }
            }

    }





}