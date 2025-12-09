package com.bos.payment.appName.ui.view.travel.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.travel.flight.DataItem
import com.bos.payment.appName.data.model.travel.flight.FlightsItem
import com.bos.payment.appName.data.model.travel.flight.SegmentsItem
import com.bos.payment.appName.databinding.AirportNameListLayoutBinding
import com.bos.payment.appName.databinding.FlightdetailsitemlayoutBinding
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.GetAirlineLogo
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.calculateLayoverTime
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.calculateTotalFlightDuration
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.className
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.convertDurationToReadableFormat
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.formatDate1
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.formatDate2
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.splitDateTime
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.totalDurationTime
import com.bumptech.glide.Glide

class FlightTicketDetailsAdapter (private val context: Context, private var flightDetails :MutableList<FlightsItem?>, private var segmentDataList :MutableList<SegmentsItem?>):


    RecyclerView.Adapter<FlightTicketDetailsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bin = FlightdetailsitemlayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(bin)
    }



    override fun getItemCount(): Int {
       return segmentDataList.size
    }


    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val segment = segmentDataList[position]


       holder.originAirPortCode.text = flightDetails[0]!!.origin
       holder.destinationAirPortCode.text = flightDetails[0]!!.destination


        if(segmentDataList.size==1){

            holder.topheader.visibility = View.VISIBLE
            holder.planechangeLayoverlayout.visibility = View.INVISIBLE
            holder.durationtime.visibility = View.INVISIBLE

            var airportIcon = GetAirlineLogo(segmentDataList[position]!!.airlineCode)
            Glide.with(context).load(airportIcon).error(R.drawable.airplaceholder).into(holder.airlineicon)

            holder.airlinename.text=segmentDataList[position]!!.airlineName.plus(" | ")
            holder.flightnumberwithcode.text= segmentDataList[position]!!.airlineCode.plus(" ").plus(segmentDataList[position]!!.flightNumber)

            var originTime = splitDateTime(segmentDataList[position]!!.departureDateTime)
            holder.origintime.text = originTime.second

            var originDate = formatDate1(originTime.first).plus(",") .plus(formatDate2(originTime.first))
            holder.origindate.text = originDate


            var destinationTime = splitDateTime(segmentDataList[position]!!.arrivalDateTime)
            holder.destinationtime.text = destinationTime.second

            var destinationDate = formatDate1(destinationTime.first).plus(",") .plus(formatDate2(destinationTime.first))
            holder.destinationdate.text = destinationDate

            holder.origincityname.text= segmentDataList[position]!!.originCity
            holder.destinationcityname.text=segmentDataList[position]!!.destinationCity

            holder.originterminal.text = "Terminal ".plus(segmentDataList[position]!!.originTerminal)
            holder.destinationterminal.text = "Terminal ".plus(segmentDataList[position]!!.destinationTerminal)

            //var duration =  convertDurationToReadableFormat(segmentDataList[position]!!.duration)

            val segments = listOf(mapOf("departure_DateTime" to segmentDataList[position]!!.departureDateTime, "arrival_DateTime" to segmentDataList[position]!!.arrivalDateTime))
            var detailsName = "Non stop".plus("|").plus(calculateTotalFlightDuration(segments)).plus("|").plus(className)
            holder.travelprimedetails.text = detailsName
            
        }
        else{
            if(position==0){
                holder.topheader.visibility = View.VISIBLE
            }else{
                holder.topheader.visibility = View.GONE
            }

            holder.planechangeLayoverlayout.visibility = View.VISIBLE
            holder.durationtime.visibility = View.VISIBLE
            // Show layover for all except the last item
            if (position < segmentDataList.size - 1) {
                val layover = calculateLayoverTime(segment!!.arrivalDateTime, segmentDataList[position + 1]!!.departureDateTime)
                holder.showingmsgforchnageplane.text= layover.plus(" ").plus("Layover at ").plus(segmentDataList[position + 1]!!.originCity)
            } else {
            }


            var airportIcon = GetAirlineLogo(segmentDataList[position]!!.airlineCode)
            Glide.with(context).load(airportIcon).error(R.drawable.airplaceholder).into(holder.airlineicon)

            holder.airlinename.text=segmentDataList[position]!!.airlineName.plus(" | ")
            holder.flightnumberwithcode.text= segmentDataList[position]!!.airlineCode.plus(" ").plus(segmentDataList[position]!!.flightNumber)

            var originTime = splitDateTime(segmentDataList[position]!!.departureDateTime)
            holder.origintime.text = originTime.second

            var originDate = formatDate1(originTime.first).plus(",") .plus(formatDate2(originTime.first))
            holder.origindate.text = originDate


            var destinationTime = splitDateTime(segmentDataList[position]!!.arrivalDateTime)
            holder.destinationtime.text = destinationTime.second

            var destinationDate = formatDate1(destinationTime.first).plus(",") .plus(formatDate2(destinationTime.first))
            holder.destinationdate.text = destinationDate

            holder.origincityname.text= segmentDataList[position]!!.originCity
            holder.destinationcityname.text=segmentDataList[position]!!.destinationCity

            holder.originterminal.text = "Terminal ".plus(segmentDataList[position]!!.originTerminal)
            holder.destinationterminal.text = "Terminal ".plus(segmentDataList[position]!!.destinationTerminal)

           // var duration =  convertDurationToReadableFormat(segmentDataList[position]!!.duration)

            val segments = listOf(mapOf("departure_DateTime" to segmentDataList[position]!!.departureDateTime, "arrival_DateTime" to segmentDataList[position]!!.arrivalDateTime))
            holder.durationtime.text= calculateTotalFlightDuration(segments)

            var detailsName = "Stop".plus("|").plus(totalDurationTime).plus("|").plus(className)
            holder.travelprimedetails.text = detailsName

        }


    }



    class ViewHolder(bind: FlightdetailsitemlayoutBinding) : RecyclerView.ViewHolder(bind.root) {
        var originAirPortCode = bind.fromAirportCode
        var destinationAirPortCode = bind.toAirportCode
        var travelprimedetails = bind.travelprimedetails
        var airlineicon = bind.airlineicon
        var airlinename = bind.airlinename
        var flightnumberwithcode = bind.flightnumberwithcode
        var origintime = bind.origintime
        var origindate = bind.origindate
        var origincityname = bind.origincityname
        var originairportname = bind.originairportname
        var originterminal = bind.originterminal
        var destinationtime = bind.destinationtime
        var destinationdate = bind.destinationdate
        var destinationcityname = bind.destinationcityname
        var destinationterminal = bind.destinationterminal
        var showingmsgforchnageplane = bind.showingmsgforchnageplane
        var topheader = bind.topheader
        var planechangeLayoverlayout = bind.planechangeLayoverlayout
        var durationtime = bind.durationtime
    }



}