package com.bos.payment.appName.ui.view.travel.busactivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bos.payment.appName.R
import com.bos.payment.appName.databinding.ActivityBusFilterBinding
import com.bos.payment.appName.ui.view.travel.adapter.AirlinesAdapter
import com.bos.payment.appName.ui.view.travel.adapter.BusAdapter
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.AC
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.AllBusList
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.BusDepartureDateAndTime
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.BusListForFilter
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.busBoardingPointList
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.busDropingPointList
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.busOperatorNameList
import com.bos.payment.appName.ui.view.travel.busactivity.BusConstant.Companion.nonAC
import com.bos.payment.appName.ui.view.travel.busactivity.BusSearchDetails.Companion.SearchBusesList
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.AllFlightList
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.FlightListForFilter
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.Stops
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.after12pmlayout
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.after6amlayout
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.after6pmlayout
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.airportNameList
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.before6layout
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.nonStops
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.FlightDetailListActivity.Companion.SearchFlightList
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class BusFilterActivity : AppCompatActivity(),BusAdapter.OnClickListener {

    private lateinit var binding: ActivityBusFilterBinding
    private var busoperatorNamelist: List<String> = listOf()
    val code = "101"

    private var currentBusFilterType: BusFilterType? = null

    private var selectedOperators = emptyList<String>()
    private var selectedBoardings = emptyList<String>()
    private var selectedDroppings = emptyList<String>()

    enum class BusFilterType {
        OPERATOR,
        BOARDING,
        DROPPING
    }

    companion object {
        const val TAG = "BusFilterBottomSheet"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBusFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setDataOnView()
        setOnClickListner()

        if(!nonAC && !AC){
            selectedBusType()
        }

        
        if(nonAC){
            selectedBusType()
            binding.nonaclayout.background=resources.getDrawable(R.drawable.selectedfrombox)
            binding.nonacicon.imageTintList=resources.getColorStateList(R.color.blue)
            binding.nonactext.setTextColor(resources.getColor(R.color.blue))
        }

        
        if(AC){
            selectedBusType()
            binding.aclayout.background=resources.getDrawable(R.drawable.selectedfrombox)
            binding.acicon.imageTintList=resources.getColorStateList(R.color.blue)
            binding.actxt.setTextColor(resources.getColor(R.color.blue))
        }


        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

        val departureTime: Date? = try {
            formatter.parse(BusDepartureDateAndTime)
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


    }


    fun selectionHoverofTime (){

        binding.before6layout.background= resources.getDrawable(R.drawable.outerborder)
        binding.after6amlayout.background= resources.getDrawable(R.drawable.outerborder)
        binding.after12pmlayout.background= resources.getDrawable(R.drawable.outerborder)
        binding.after6pmlayout.background= resources.getDrawable(R.drawable.outerborder)


        binding.before6icon.imageTintList = ContextCompat.getColorStateList(this@BusFilterActivity, R.color.black)
        binding.after6amimage.imageTintList = ContextCompat.getColorStateList(this@BusFilterActivity,R.color.black)
        binding.after12pmicon.imageTintList = ContextCompat.getColorStateList(this@BusFilterActivity,R.color.black)
        binding.after6pmicon.imageTintList = ContextCompat.getColorStateList(this@BusFilterActivity,R.color.black)



        binding.after6amtxt.setTextColor(
            ContextCompat.getColorStateList(this@BusFilterActivity, R.color.text_color)
        )
        binding.after12pmtxt.setTextColor(
            ContextCompat.getColorStateList(this@BusFilterActivity, R.color.text_color)
        )
        binding.after6pmtxt.setTextColor(
            ContextCompat.getColorStateList(this@BusFilterActivity, R.color.text_color)
        )
        binding.before6txt.setTextColor(
            ContextCompat.getColorStateList(this@BusFilterActivity, R.color.text_color)
        )

    }

    fun highlightSelected(layout: View, icon: ImageView, text: TextView) {
        layout.background = ContextCompat.getDrawable(this, R.drawable.selectedfrombox)
        icon.imageTintList = ContextCompat.getColorStateList(this, R.color.blue)
        text.setTextColor(ContextCompat.getColorStateList(this, R.color.blue))
    }


    fun selectedBusType()
    {
        binding.nonaclayout.background=resources.getDrawable(R.drawable.outerborder)
        binding.nonacicon.imageTintList=resources.getColorStateList(R.color.black)
        binding.nonactext.setTextColor(resources.getColor(R.color.edittext_color))

        binding.aclayout.background=resources.getDrawable(R.drawable.outerborder)
        binding.acicon.imageTintList=resources.getColorStateList(R.color.black)
        binding.actxt.setTextColor(resources.getColor(R.color.edittext_color))

    }


    private fun setDataOnView(){
        binding.flightcount.text=  BusListForFilter.size.toString().plus(" Buses Found")
        Log.d("AllBusList", Gson().toJson(AllBusList))
        binding.showbusoperatorlist.isNestedScrollingEnabled = false
        binding.showboardinglist.isNestedScrollingEnabled = false
        binding.showbusdroplist.isNestedScrollingEnabled = false


        var adapter = BusAdapter(this,busOperatorNameList,BusFilterType.OPERATOR,this)
        binding.showbusoperatorlist.adapter=adapter

        binding.showbusoperatorlist.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    if (binding.showbusoperatorlist.childCount == 0) return

                    val itemHeight = binding.showbusoperatorlist.getChildAt(0).measuredHeight
                    val visibleItems = minOf(busOperatorNameList.size, 6)

                    val params = binding.showbusoperatorlist.layoutParams
                    params.height = itemHeight * visibleItems
                    binding.showbusoperatorlist.layoutParams = params

                    binding.showbusoperatorlist.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        )

        binding.showbusoperatorlist.apply {
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        binding.showbusoperatorlist.addOnItemTouchListener(
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


        adapter = BusAdapter(this, busBoardingPointList,BusFilterType.BOARDING,this)
        binding.showboardinglist.adapter=adapter

        binding.showboardinglist.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    if (binding.showboardinglist.childCount == 0) return

                    val itemHeight = binding.showboardinglist.getChildAt(0).measuredHeight
                    val visibleItems = minOf(busBoardingPointList.size, 6)

                    val params = binding.showboardinglist.layoutParams
                    params.height = itemHeight * visibleItems
                    binding.showboardinglist.layoutParams = params

                    binding.showboardinglist.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        )

        binding.showboardinglist.apply {
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        binding.showboardinglist.addOnItemTouchListener(
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


        adapter = BusAdapter(this, busDropingPointList,BusFilterType.DROPPING,this)
        binding.showbusdroplist.adapter=adapter

        binding.showbusdroplist.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    if (binding.showbusdroplist.childCount == 0) return

                    val itemHeight = binding.showbusdroplist.getChildAt(0).measuredHeight
                    val visibleItems = minOf(busDropingPointList.size, 6)

                    val params = binding.showbusdroplist.layoutParams
                    params.height = itemHeight * visibleItems
                    binding.showbusdroplist.layoutParams = params

                    binding.showbusdroplist.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        )

        binding.showbusdroplist.apply {
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        binding.showbusdroplist.addOnItemTouchListener(
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

        binding.cross.setOnClickListener {
            finish()
        }

        binding.clearAllFilter.setOnClickListener {
            selectedBusType()
            selectionHoverofTime()

            nonAC = false
            AC = false
            before6layout = false
            after6amlayout= false
            after12pmlayout= false
            after6pmlayout= false

            BusListForFilter = AllBusList.toMutableList()
            SearchBusesList.clear()
            SearchBusesList.addAll(BusListForFilter)


            // update list
            var updateAirList = busOperatorNameList.map { it.first to false }.toMutableList()
            var updateboardingList = busBoardingPointList.map { it.first to false }.toMutableList()
            var updatedropList = busDropingPointList.map { it.first to false }.toMutableList()
            busOperatorNameList=updateAirList
            busBoardingPointList=updateboardingList
            busDropingPointList=updatedropList
            setDataOnView()

            val resultIntent = Intent()
            resultIntent.putExtra("code", code)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()

        }

        binding.nonaclayout.setOnClickListener {
            nonAC =true
            AC =false
            selectedBusType()
            applyFilters()
            binding.nonaclayout.background=resources.getDrawable(R.drawable.selectedfrombox)
            binding.nonacicon.imageTintList=resources.getColorStateList(R.color.blue)
            binding.nonactext.setTextColor(resources.getColor(R.color.blue))

        }

        binding.aclayout.setOnClickListener {
            nonAC = false
            AC =true
            selectedBusType()
            applyFilters()
            binding.aclayout.background=resources.getDrawable(R.drawable.selectedfrombox)
            binding.acicon.imageTintList=resources.getColorStateList(R.color.blue)
            binding.actxt.setTextColor(resources.getColor(R.color.blue))

        }

        binding.filterbyfliteoperator.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("code", code)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
            Log.d("FilterList", BusListForFilter.size.toString().plus( ""))
        }

        binding.searchairlines.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()

                val filteredList = if (searchText.isNotEmpty()) {
                    busOperatorNameList.filter { it.first.contains(searchText, ignoreCase = true) }.toMutableList()
                }
                else {
                    busOperatorNameList // show full list if input is empty
                }

                var adapter = BusAdapter(this@BusFilterActivity,filteredList,BusFilterType.OPERATOR,this@BusFilterActivity)
                binding.showbusoperatorlist.adapter=adapter
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.searchboardingpoints.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()

                val filteredList = if (searchText.isNotEmpty()) {
                    busBoardingPointList.filter { it.first.contains(searchText, ignoreCase = true) }.toMutableList()
                }
                else {
                    busBoardingPointList // show full list if input is empty
                }

                var adapter = BusAdapter(this@BusFilterActivity,filteredList,BusFilterType.BOARDING,this@BusFilterActivity)
                binding.showboardinglist.adapter=adapter
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.searchdroppoints.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()

                val filteredList = if (searchText.isNotEmpty()) {
                    busDropingPointList.filter { it.first.contains(searchText, ignoreCase = true) }.toMutableList()
                }
                else {
                    busDropingPointList // show full list if input is empty
                }

                var adapter = BusAdapter(this@BusFilterActivity,filteredList,BusFilterType.DROPPING,this@BusFilterActivity)
                binding.showbusdroplist.adapter=adapter
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.before6layout.setOnClickListener {
            selectionHoverofTime ()
            binding.before6layout.background= resources.getDrawable(R.drawable.selectedfrombox)
            binding.before6icon.imageTintList = ContextCompat.getColorStateList(this@BusFilterActivity, R.color.blue)
            binding.before6txt.setTextColor(
                ContextCompat.getColorStateList(this@BusFilterActivity, R.color.blue)
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
            binding.after6amimage.imageTintList = ContextCompat.getColorStateList(this@BusFilterActivity,R.color.blue)
            binding.after6amtxt.setTextColor(
                ContextCompat.getColorStateList(this@BusFilterActivity, R.color.blue)
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
            binding.after12pmicon.imageTintList = ContextCompat.getColorStateList(this@BusFilterActivity,R.color.blue)
            binding.after12pmtxt.setTextColor(ContextCompat.getColorStateList(this@BusFilterActivity, R.color.blue))
            before6layout =false
            after6amlayout= false
            after12pmlayout= true
            after6pmlayout= false
            applyFilters()

        }

        binding.after6pmlayout.setOnClickListener {
            selectionHoverofTime ()
            binding.after6pmlayout.background= resources.getDrawable(R.drawable.selectedfrombox)
            binding.after6pmicon.imageTintList = ContextCompat.getColorStateList(this@BusFilterActivity,R.color.blue)
            binding.after6pmtxt.setTextColor(
                ContextCompat.getColorStateList(this@BusFilterActivity, R.color.blue)
            )
            before6layout =false
            after6amlayout= false
            after12pmlayout= false
            after6pmlayout= true

            applyFilters()
        }


    }


    fun applyFilters( ) {
        // Start fresh every time
        BusListForFilter = AllBusList.toMutableList()

        // Filter by AC
        
        BusListForFilter = when {
            nonAC -> BusListForFilter.filter { it.ac == false }.toMutableList()
            AC -> BusListForFilter.filter { it.ac == true }.toMutableList()
            else -> BusListForFilter.toMutableList()
        }


        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())


        //splitDateTime
        if (before6layout) {

            BusListForFilter = BusListForFilter.filter {
                var time = it.departureTime
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
            BusListForFilter = BusListForFilter.filter {
                var time = it.departureTime
                time != null && runCatching {
                    val date = dateFormat.parse(time)
                    var calendar = Calendar.getInstance()
                    calendar.time = date
                    var hour = calendar.get(Calendar.HOUR_OF_DAY)
                    hour in 6..11
                }.getOrDefault(false)
            }.toMutableList()
        }else if (after12pmlayout) {
            BusListForFilter = BusListForFilter.filter {
                var time = it.departureTime
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
            BusListForFilter = BusListForFilter.filter {
                var time = it.departureTime
                time != null && runCatching {
                    val date = dateFormat.parse(time)
                    var calendar = Calendar.getInstance()
                    calendar.time = date
                    var hour = calendar.get(Calendar.HOUR_OF_DAY)
                    hour >= 18
                }.getOrDefault(false)
            }.toMutableList()

        }

        BusListForFilter = BusListForFilter.filter { bus ->

            val operatorMatch = selectedOperators.isEmpty() ||
                        (bus.operatorName != null && selectedOperators.contains(bus.operatorName))

            val boardingMatch = selectedBoardings.isEmpty() ||
                        bus.boardingDetails?.any {
                            selectedBoardings.contains(it.boardingName)
                        } == true

            val droppingMatch =
                selectedDroppings.isEmpty() ||
                        bus.droppingDetails?.any {
                            selectedDroppings.contains(it.droppingName)
                        } == true

            operatorMatch && boardingMatch && droppingMatch

        }.toMutableList()

        // Update search list
        SearchBusesList.clear()
        SearchBusesList.addAll(BusListForFilter)

    }



    override fun setonclicklistner(selectedNames: List<String>, filtertype: BusFilterType) {
        busoperatorNamelist = selectedNames

        when (filtertype) {
            BusFilterType.OPERATOR -> {selectedOperators = selectedNames}
            BusFilterType.BOARDING -> {selectedBoardings = selectedNames }
            BusFilterType.DROPPING ->  {selectedDroppings = selectedNames }
        }

        applyFilters()
    }


}