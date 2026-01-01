package com.bos.payment.appName.ui.view.travel.flightBooking.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.travel.flight.FlightsItem
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.data.viewModelFactory.TravelViewModelFactory
import com.bos.payment.appName.databinding.FilterFlightItemLayoutBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.travel.adapter.AirlinesAdapter
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.busBoardingPointList
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.AllAirNameList
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.AllFlightList
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.DepartureDateAndTime
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.FlightList
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.FlightListForFilter
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.Stops
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.after12pmlayout
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.after6amlayout
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.after6pmlayout
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.airportNameList
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.before6layout
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.nonStops
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.FlightDetailListActivity.Companion.SearchFlightList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.FlightDetailListActivity.Companion.context
import com.bos.payment.appName.ui.viewmodel.TravelViewModel
import com.bos.payment.appName.utils.MStash
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class FlightFilterActivity: AppCompatActivity() , AirlinesAdapter.OnClickListener {
    private lateinit var binding : FilterFlightItemLayoutBinding
    lateinit var viewModel : TravelViewModel
    private var mStash: MStash? = null
    val code = "100"
    private var airlinesNameList: List<String> = listOf()
    val filteredList = mutableListOf<String>()


    companion object {
        const val TAG = "FlightFilterBottomSheet"
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        binding = FilterFlightItemLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mStash = MStash.getInstance(this)
        viewModel = ViewModelProvider(this, TravelViewModelFactory(TravelRepository(RetrofitClient.apiAllTravelAPI, RetrofitClient.apiBusAddRequestlAPI)))[TravelViewModel::class.java]

        if(!nonStops && !Stops){
            selectedFlightType()
        }

        if(nonStops){
            selectedFlightType()
            binding.nonstoplayout.background=resources.getDrawable(R.drawable.selectedfrombox)
            binding.nonstopicon.imageTintList=resources.getColorStateList(R.color.blue)
            binding.nonstoptxt.setTextColor(resources.getColor(R.color.blue))
        }

        if(Stops){
            selectedFlightType()
            binding.stopslayout.background=resources.getDrawable(R.drawable.selectedfrombox)
            binding.stopsicon.imageTintList=resources.getColorStateList(R.color.blue)
            binding.stopstext.setTextColor(resources.getColor(R.color.blue))
        }


        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

        val departureTime: Date? = try {
            formatter.parse(DepartureDateAndTime)
        }
        catch (e: Exception) {
            null
        }

        if (departureTime != null) {
            val currentCal = Calendar.getInstance()
            val departureCal = Calendar.getInstance().apply {
                time = departureTime
            }
                val isSameDay = currentCal.get(Calendar.YEAR) == departureCal.get(Calendar.YEAR) && currentCal.get(Calendar.DAY_OF_YEAR) == departureCal.get(Calendar.DAY_OF_YEAR)
                val currentHour = currentCal.get(Calendar.HOUR_OF_DAY)

            if (isSameDay) {
                // Enable and highlight based on current time
                binding.before6layout.apply {
                    isEnabled = currentHour < 6
                    alpha = if (isEnabled) 1f else 0.5f
                }

                binding.after6amlayout.apply {
                    isEnabled = currentHour < 12
                    alpha = if (isEnabled) 1f else 0.5f
                }

                binding.after12pmlayout.apply {
                    isEnabled = currentHour < 18
                    alpha = if (isEnabled) 1f else 0.5f
                }

                binding.after6pmlayout.apply {
                    isEnabled =  currentHour < 24
                    alpha = if (isEnabled) 1f else 0.5f
                }
            }
            else {
                // Future date - enable all with full visibility
                binding.before6layout.apply {
                    isEnabled = true
                    alpha = 1f
                }
                binding.after6amlayout.apply {
                    isEnabled = true
                    alpha = 1f
                }
                binding.after12pmlayout.apply {
                    isEnabled = true
                    alpha = 1f
                }
                binding.after6pmlayout.apply {
                    isEnabled = true
                    alpha = 1f
                }
            }

        }
        else {
                // Past time - disable all
                binding.before6layout.isEnabled = false
                binding.after6amlayout.isEnabled = false
                binding.after12pmlayout.isEnabled = false
                binding.after6pmlayout.isEnabled = false
            }


        if(before6layout){
            selectionHoverofTime ()
            highlightSelected(binding.before6layout,binding.before6icon,binding.before6txt)

        }

        if(after6amlayout){
            selectionHoverofTime ()
            highlightSelected(binding.after6amlayout,binding.after6amimage,binding.after6amtxt)

        }

        if(after12pmlayout){
            selectionHoverofTime ()
            highlightSelected(binding.after12pmlayout,binding.after12pmicon,binding.after12pmtxt)

        }

        if(after6pmlayout){
            selectionHoverofTime ()
            highlightSelected(binding.after6pmlayout,binding.after6pmicon,binding.after6pmtxt)

        }


        setDataOnView()

        setOnClickListner()


    }


    private fun setDataOnView(){
        binding.flightcount.text= FlightListForFilter.size.toString().plus(" Flights Found")
        var adapter = AirlinesAdapter(this,airportNameList,this)
        binding.showairlinelist.adapter=adapter


        binding.showairlinelist.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    if (binding.showairlinelist.childCount == 0) return

                    val itemHeight = binding.showairlinelist.getChildAt(0).measuredHeight
                    val visibleItems = minOf(airportNameList.size, 6)

                    val params = binding.showairlinelist.layoutParams
                    params.height = itemHeight * visibleItems
                    binding.showairlinelist.layoutParams = params

                    binding.showairlinelist.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        )

        binding.showairlinelist.apply {
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        binding.showairlinelist.addOnItemTouchListener(
            object : RecyclerView.OnItemTouchListener {

                override fun onInterceptTouchEvent(
                    rv: RecyclerView,
                    e: MotionEvent
                ): Boolean {

                    when (e.action) {
                        MotionEvent.ACTION_DOWN,
                        MotionEvent.ACTION_MOVE -> {
                            rv.parent.requestDisallowInterceptTouchEvent(true)
                        }

                        MotionEvent.ACTION_UP,
                        MotionEvent.ACTION_CANCEL -> {
                            rv.parent.requestDisallowInterceptTouchEvent(false)
                        }
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            }
        )


    }


    fun setOnClickListner(){

        binding.clearAllFilter.setOnClickListener {
            selectedFlightType()
            selectionHoverofTime()

            nonStops= false
            Stops= false
            before6layout = false
            after6amlayout= false
            after12pmlayout= false
            after6pmlayout= false

            FlightListForFilter = AllFlightList.toMutableList()
            SearchFlightList.clear()
            SearchFlightList.addAll(FlightListForFilter)


            // update list
            var updateAirList = airportNameList.map { it.first to false }.toMutableList()
            airportNameList=updateAirList
            setDataOnView()

            val resultIntent = Intent()
            resultIntent.putExtra("code", code)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()

        }


        binding.cross.setOnClickListener {
            finish()
        }

        binding.nonstoplayout.setOnClickListener {
            nonStops=true
            Stops=false
            selectedFlightType()
            applyFilters()
            binding.nonstoplayout.background=resources.getDrawable(R.drawable.selectedfrombox)
            binding.nonstopicon.imageTintList=resources.getColorStateList(R.color.blue)
            binding.nonstoptxt.setTextColor(resources.getColor(R.color.blue))

        }


        binding.stopslayout.setOnClickListener {
            nonStops= false
            Stops=true
            selectedFlightType()
            applyFilters()
            binding.stopslayout.background=resources.getDrawable(R.drawable.selectedfrombox)
            binding.stopsicon.imageTintList=resources.getColorStateList(R.color.blue)
            binding.stopstext.setTextColor(resources.getColor(R.color.blue))

        }

        binding.filterbyfliteoperator.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("code", code)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
            Log.d("FilterList",FlightListForFilter.size.toString().plus( ""))
        }

        binding.before6layout.setOnClickListener {
            selectionHoverofTime ()
            binding.before6layout.background= resources.getDrawable(R.drawable.selectedfrombox)
            binding.before6icon.imageTintList = ContextCompat.getColorStateList(this@FlightFilterActivity, R.color.blue)
            binding.before6txt.setTextColor(
                ContextCompat.getColorStateList(this@FlightFilterActivity, R.color.blue)
            )

            before6layout =true
            after6amlayout= false
            after12pmlayout= false
            after6pmlayout= false
            applyFilters()

        }

        binding.after6amlayout.setOnClickListener {
            selectionHoverofTime ()
            binding.after6amlayout.background= resources.getDrawable(R.drawable.selectedfrombox)
            binding.after6amimage.imageTintList = ContextCompat.getColorStateList(this@FlightFilterActivity,R.color.blue)
            binding.after6amtxt.setTextColor(
                ContextCompat.getColorStateList(this@FlightFilterActivity, R.color.blue)
            )
            before6layout =false
            after6amlayout= true
            after12pmlayout= false
            after6pmlayout= false

            applyFilters()

        }

        binding.after12pmlayout.setOnClickListener {
            selectionHoverofTime ()
            binding.after12pmlayout.background= resources.getDrawable(R.drawable.selectedfrombox)
            binding.after12pmicon.imageTintList = ContextCompat.getColorStateList(this@FlightFilterActivity,R.color.blue)
            binding.after12pmtxt.setTextColor(ContextCompat.getColorStateList(this@FlightFilterActivity, R.color.blue))
            before6layout =false
            after6amlayout= false
            after12pmlayout= true
            after6pmlayout= false
            applyFilters()

        }

        binding.after6pmlayout.setOnClickListener {
            selectionHoverofTime ()
            binding.after6pmlayout.background= resources.getDrawable(R.drawable.selectedfrombox)
            binding.after6pmicon.imageTintList = ContextCompat.getColorStateList(this@FlightFilterActivity,R.color.blue)
            binding.after6pmtxt.setTextColor(
                ContextCompat.getColorStateList(this@FlightFilterActivity, R.color.blue)
            )
            before6layout =false
            after6amlayout= false
            after12pmlayout= false
            after6pmlayout= true

            applyFilters()
        }

        binding.searchairlines.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()

                val filteredList = if (searchText.isNotEmpty()) {
                    airportNameList.filter { it.first.contains(searchText, ignoreCase = true) }.toMutableList()
                }
                else {
                    airportNameList // show full list if input is empty
                }

                var adapter = AirlinesAdapter(this@FlightFilterActivity,filteredList,this@FlightFilterActivity)
                binding.showairlinelist.adapter=adapter
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

    }




    fun selectedFlightType()
    {
        binding.nonstoplayout.background=resources.getDrawable(R.drawable.outerborder)
        binding.nonstopicon.imageTintList=resources.getColorStateList(R.color.black)
        binding.nonstoptxt.setTextColor(resources.getColor(R.color.edittext_color))

        binding.stopslayout.background=resources.getDrawable(R.drawable.outerborder)
        binding.stopsicon.imageTintList=resources.getColorStateList(R.color.black)
        binding.stopstext.setTextColor(resources.getColor(R.color.edittext_color))

    }




    fun selectionHoverofTime (){

        binding.before6layout.background= resources.getDrawable(R.drawable.outerborder)
        binding.after6amlayout.background= resources.getDrawable(R.drawable.outerborder)
        binding.after12pmlayout.background= resources.getDrawable(R.drawable.outerborder)
        binding.after6pmlayout.background= resources.getDrawable(R.drawable.outerborder)


        binding.before6icon.imageTintList = ContextCompat.getColorStateList(this@FlightFilterActivity, R.color.black)
        binding.after6amimage.imageTintList = ContextCompat.getColorStateList(this@FlightFilterActivity,R.color.black)
        binding.after12pmicon.imageTintList = ContextCompat.getColorStateList(this@FlightFilterActivity,R.color.black)
        binding.after6pmicon.imageTintList = ContextCompat.getColorStateList(this@FlightFilterActivity,R.color.black)



        binding.after6amtxt.setTextColor(
            ContextCompat.getColorStateList(this@FlightFilterActivity, R.color.text_color)
        )
        binding.after12pmtxt.setTextColor(
            ContextCompat.getColorStateList(this@FlightFilterActivity, R.color.text_color)
        )
        binding.after6pmtxt.setTextColor(
            ContextCompat.getColorStateList(this@FlightFilterActivity, R.color.text_color)
        )
        binding.before6txt.setTextColor(
            ContextCompat.getColorStateList(this@FlightFilterActivity, R.color.text_color)
        )

    }


    fun highlightSelected(layout: View, icon: ImageView, text: TextView) {
        layout.background = ContextCompat.getDrawable(this, R.drawable.selectedfrombox)
        icon.imageTintList = ContextCompat.getColorStateList(this, R.color.blue)
        text.setTextColor(ContextCompat.getColorStateList(this, R.color.blue))
    }



    override fun setonclicklistner(airlineNames: List<String>) {
        airlinesNameList = airlineNames
        applyFilters()
    }



    fun applyFilters() {
        // Start fresh every time
        FlightListForFilter = AllFlightList.toMutableList()

        // Filter by stops
        if (nonStops) {
            FlightListForFilter = FlightListForFilter.filter { it.segments?.size == 1 }.toMutableList()
        }
        else if (Stops) {
            FlightListForFilter = FlightListForFilter.filter { (it.segments?.size ?: 0) > 1 }.toMutableList()
        }


        val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault())


          //splitDateTime
        if (before6layout) {

            FlightListForFilter = FlightListForFilter.filter {
                var time = it.segments?.getOrNull(0)?.departureDateTime
                time != null && runCatching {
                    var date = dateFormat.parse(time)
                    var calendar = Calendar.getInstance()
                    calendar.time = date
                    var hour = calendar.get(Calendar.HOUR_OF_DAY)
                    hour < 6
                }.getOrDefault(false)
            }.toMutableList()

        }
        else if (after6amlayout) {
            FlightListForFilter = FlightListForFilter.filter {
                var time = it.segments?.getOrNull(0)?.departureDateTime
                time != null && runCatching {
                    val date = dateFormat.parse(time)
                    var calendar = Calendar.getInstance()
                    calendar.time = date
                    var hour = calendar.get(Calendar.HOUR_OF_DAY)
                    hour in 6..11
                }.getOrDefault(false)
            }.toMutableList()
        }else if (after12pmlayout) {

            FlightListForFilter = FlightListForFilter.filter {
                var time = it.segments?.getOrNull(0)?.departureDateTime
                time != null && runCatching {
                    val date = dateFormat.parse(time)
                    var calendar = Calendar.getInstance()
                    calendar.time = date
                    var hour = calendar.get(Calendar.HOUR_OF_DAY)
                    hour in 12..17
                }.getOrDefault(false)
            }.toMutableList()

        }
        else if (after6pmlayout) {
            FlightListForFilter = FlightListForFilter.filter {
                var time = it.segments?.getOrNull(0)?.departureDateTime
                time != null && runCatching {
                    val date = dateFormat.parse(time)
                    var calendar = Calendar.getInstance()
                    calendar.time = date
                    var hour = calendar.get(Calendar.HOUR_OF_DAY)
                    hour >= 18
                }.getOrDefault(false)
            }.toMutableList()

        }

        // Filter by airline
        if (airlinesNameList.isNotEmpty()) {
            FlightListForFilter = FlightListForFilter.filter {
                val airlineName = it.segments?.getOrNull(0)?.airlineName
                airlineName != null && airlinesNameList.contains(airlineName)
            }.toMutableList()
        }

        // Update search list
        SearchFlightList.clear()
        SearchFlightList.addAll(FlightListForFilter)

    }



}