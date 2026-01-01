package com.bos.payment.appName.ui.view.travel.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.travel.flight.DataItem
import com.bos.payment.appName.data.model.travel.flight.FaresItem
import com.bos.payment.appName.data.model.travel.flight.FlightsItem
import com.bos.payment.appName.data.model.travel.flight.SegmentsItem
import com.bos.payment.appName.databinding.AirportNameListLayoutBinding
import com.bos.payment.appName.databinding.FligetdetailsItemlayoutBinding
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.FlightDetails
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.GetAirlineLogo
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.animateCloseToTripCard
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.animateOpenFromTripCard
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.calculateTotalFlightDuration
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.convertDurationToReadableFormat
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.FlightDetailListActivity
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.FlightDetailsBottomSheet
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.SelectTravellersClassBottomSheet
import com.bos.payment.appName.utils.MStash
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.text.SimpleDateFormat
import java.util.Locale

class FlightDetailsAdapter (private val context: Context, private var flightList: MutableList<FlightsItem>):
    RecyclerView.Adapter<FlightDetailsAdapter.ViewHolder>() {

        var segmentList : MutableList<SegmentsItem> = mutableListOf()
        var fareList : MutableList<FaresItem> = mutableListOf()
        var selectionPosition= -1
        private var mStash: MStash? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bin = FligetdetailsItemlayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(bin)
    }



    override fun getItemCount(): Int {
       return flightList.size
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        segmentList.clear()
        fareList.clear()
        mStash = MStash.getInstance(context)

        flightList[position].segments?.let { segmentList.addAll(it) }

        flightList[position].fares?.let { fareList.addAll(it) }

        holder.flightname.text= segmentList!![0].airlineName

        var airportIcon = GetAirlineLogo(flightList[position].airlineCode)

        Log.d("IMAGE_URL", airportIcon)


        Glide.with(context)
            .load(airportIcon)
            .error(R.drawable.airplaceholder)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("GLIDE_ERROR", "Error: ${e?.rootCauses}")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

            })
            .into(holder.flighticon)

        if(segmentList.size==1){
            holder.fromcityname.text= segmentList[segmentList.size-1].originCity
            holder.tocityname.text= segmentList[segmentList.size-1].destinationCity
            holder.fromTime.text= FlightConstant.splitDateTime(segmentList[segmentList.size-1].departureDateTime).second
            holder.toTime.text= FlightConstant.splitDateTime(segmentList[segmentList.size-1].arrivalDateTime).second
            holder.stopsType.text="Non stop"
           //var duration= convertDurationToReadableFormat(segmentList[segmentList.size-1].duration)
            val segments = listOf(mapOf("departure_DateTime" to segmentList[segmentList.size-1].departureDateTime, "arrival_DateTime" to segmentList[segmentList.size-1].arrivalDateTime))
            holder.durationTime.text=  calculateTotalFlightDuration(segments)
        }
        else{
            holder.fromcityname.text= segmentList[0].originCity
            holder.tocityname.text= segmentList[segmentList.size-1].destinationCity
            holder.fromTime.text= FlightConstant.splitDateTime(segmentList[0].departureDateTime).second
            holder.toTime.text= FlightConstant.splitDateTime(segmentList[segmentList.size-1].arrivalDateTime).second
            holder.stopsType.text= (segmentList.size-1).toString().plus("stop")
            val segments = listOf(mapOf("departure_DateTime" to segmentList[0].departureDateTime, "arrival_DateTime" to segmentList[0].arrivalDateTime), mapOf("departure_DateTime" to segmentList[segmentList.size-1].departureDateTime, "arrival_DateTime" to segmentList[segmentList.size-1].arrivalDateTime))
            holder.durationTime.text= calculateTotalFlightDuration(segments)
        }

        holder.price.text= "₹ ".plus(fareList[0].fareDetails!![0].totalAmount.toString())
        holder.totalprice.text= "₹ ".plus(fareList[0].fareDetails!![0].totalAmount.toString())
        holder.flightnumber.text= flightList[position].segments!![0].flightNumber

        holder.cabinbag.text=fareList[0].fareDetails!![0].freeBaggage.handBaggage.plus(" / Adult")
        holder.checkin.text=fareList[0].fareDetails!![0].freeBaggage.checkInBaggage.plus(" / Adult")

        var meal= fareList[0].foodOnboard.toString()

        if(meal.contains("F")||meal.contains("Free")||meal.contains("free")){
            holder.meallayout.visibility=View.VISIBLE
        }else{
            holder.meallayout.visibility=View.GONE
        }


        if(selectionPosition== position){
            animateOpenFromTripCard(holder.booknowcard,holder.firstcard)
            holder.booknowcard.visibility= View.VISIBLE
            holder.maincard.setCardBackgroundColor(context.resources.getColor(R.color.teal_300))
        }
        else{
            animateCloseToTripCard(holder.booknowcard, holder.firstcard)
            holder.maincard.setCardBackgroundColor(android.graphics.Color.WHITE)
        }


        holder.hideabovelayout.setOnClickListener {
            animateCloseToTripCard(holder.booknowcard, holder.firstcard)
            holder.maincard.setCardBackgroundColor(android.graphics.Color.WHITE)
            selectionPosition = -1
            notifyItemChanged(position)
        }


        holder.firstcard.setOnClickListener {
            if (selectionPosition != position) {
                val previousSelection = selectionPosition
                selectionPosition = position
                notifyItemChanged(previousSelection)
                notifyItemChanged(selectionPosition)
            }


        }


        holder.booknow.setOnClickListener {
            FlightDetails.clear()
            FlightDetails.add(flightList[position])
            mStash!!.setStringValue(FlightConstant.airlinecode,flightList[position].airlineCode)
            val bottomfrag = FlightDetailsBottomSheet()
            (context as FlightDetailListActivity).supportFragmentManager.let { bottomfrag.show(it, FlightDetailsBottomSheet.TAG) }
        }


    }



    class ViewHolder(bind: FligetdetailsItemlayoutBinding) : RecyclerView.ViewHolder(bind.root) {
        var flightname = bind.flightName
        var fromTime = bind.fromtime
        var fromcityname = bind.fromcityName
        var toTime = bind.totime
        var tocityname = bind.tocityName
        var price = bind.price
        var flighticon = bind.flighticon
        var flightnumber = bind.flightnumber
        var stopsType = bind.stopsType
        var durationTime = bind.durationTime
        var firstcard = bind.triprelateddetailscard
        var maincard = bind.maincard
        var booknowcard = bind.booknowcard
        var cabinbag= bind.cabinbagweight
        var checkin= bind.checkinbagweight
        var meallayout= bind.meallayout
        var totalprice= bind.totalprice
        var booknow = bind.booknow
        var hideabovelayout = bind.hideabovelayout

    }



}