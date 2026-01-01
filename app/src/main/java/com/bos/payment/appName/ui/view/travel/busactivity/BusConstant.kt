package com.bos.payment.appName.ui.view.travel.busactivity

import com.bos.payment.appName.data.model.travel.bus.searchBus.Buses
import com.bos.payment.appName.data.model.travel.flight.FlightsItem

class BusConstant {

    companion object{
        var BusListForFilter: MutableList<Buses> = mutableListOf()
        var AllBusList: MutableList<Buses> = mutableListOf()
        var busOperatorNameList: MutableList<Pair<String, Boolean>> = mutableListOf()
        var busBoardingPointList: MutableList<Pair<String, Boolean>> = mutableListOf()
        var busDropingPointList: MutableList<Pair<String, Boolean>> = mutableListOf()
        var nonAC : Boolean = false
        var AC : Boolean =false
        var BusDepartureDateAndTime:String= ""
        var  fromBusCityName: String = "Delhi"
        var toBusCityName: String = "Mumbai"

    }

}