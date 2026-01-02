package com.bos.payment.appName.ui.view.travel.flightBooking.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bos.payment.appName.data.model.travel.flight.FlightsItem
import com.bos.payment.appName.databinding.ActivityFlightDetailListBinding
import com.bos.payment.appName.ui.view.travel.adapter.FlightDetailsAdapter
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.FlightListForFilter
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.TripDetailsList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.adultList
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.ReviewDetailsPassangersBottomSheet.Companion.passangerDetailsList
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.ReviewDetailsPassangersBottomSheet.Companion.tempBookingPassangerDetails
import java.lang.System.`in`

class FlightDetailListActivity : AppCompatActivity() {

    lateinit  var binding: ActivityFlightDetailListBinding

    lateinit var adapter: FlightDetailsAdapter

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    companion object{
       var SearchFlightList: MutableList<FlightsItem> = mutableListOf()
       lateinit var context: Context
   }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityFlightDetailListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Register result callback
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val code = result.data?.getStringExtra("code")
                if(code.equals("100")||code.equals("200")){
                    setdataonView()
                }
                else{

                }

                // Use the code
            }

        }

        if(TripDetailsList.size>0){
            SearchFlightList.clear()
            TripDetailsList[0]!!.flights?.let { SearchFlightList.addAll(it) }
            Log.d("FlightList","List:"+SearchFlightList)
            setdataonView()
        }

        setClickListner()

    }


    fun setdataonView(){

        binding.fromtocityname.text= FlightConstant.fromCityName.plus(" to ").plus(FlightConstant.toCityName)

        binding.datepassangerclassname.text= FlightConstant.datepassangerandclassstring

        adapter = FlightDetailsAdapter(this,SearchFlightList)
        binding.flightlistshown.adapter= adapter
        adapter.notifyDataSetChanged()

    }


    fun setClickListner(){

        binding.back.setOnClickListener { finish() }

        binding.fab.setOnClickListener {
            Log.d("FilterList", FlightListForFilter.size.toString().plus( ""))
            val intent = Intent(this, FlightFilterActivity::class.java)
            resultLauncher.launch(intent)
           }


        }


    override fun onResume() {
        super.onResume()
        adultList.clear()
        tempBookingPassangerDetails.clear()
        passangerDetailsList.clear()
    }

}

