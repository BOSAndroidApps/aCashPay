package com.bos.payment.appName.ui.view.travel.flightBooking.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.travel.flight.AirRepriceRequests
import com.bos.payment.appName.data.model.travel.flight.AirportListReq
import com.bos.payment.appName.data.model.travel.flight.FareDetailsFlightItem
import com.bos.payment.appName.data.model.travel.flight.FlightRePriceReq
import com.bos.payment.appName.data.model.travel.flight.FlightsItem
import com.bos.payment.appName.data.model.travel.flight.RequiredPAXDetailsItem
import com.bos.payment.appName.data.model.travel.flight.SegmentsItem
import com.bos.payment.appName.data.model.travel.flight.fareBreakup
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.data.viewModelFactory.TravelViewModelFactory
import com.bos.payment.appName.databinding.ActivityAddDetailsPassangerBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.travel.adapter.AddPassengerAdapter
import com.bos.payment.appName.ui.view.travel.adapter.PassangerDataList
import com.bos.payment.appName.ui.view.travel.adapter.TempBookingPassangerDetails
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.BaseFare
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.FlightList
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.GetAirlineLogo
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.GrossFare
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.TaxAndFees
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.adultCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.calculateTotalFlightDuration
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.childCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.className
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.formatDate1
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.formatDate2
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.infantCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.slideInFromTop
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.slideOutToTop
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.splitDateTime
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.totalDurationTime
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.AddContactAndMobileBottomSheet
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.AddContactAndMobileBottomSheet.Companion.contactNumber
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.AddContactAndMobileBottomSheet.Companion.emailid
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.AddGSTInformationBottomSheet
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.AddGSTInformationBottomSheet.Companion.CheckBox
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.AddGSTInformationBottomSheet.Companion.companyName
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.AddGSTInformationBottomSheet.Companion.registrationNo
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.AddTravellerBottomSheet
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.AddTravellerBottomSheet.Companion.ClickAdult
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.AddTravellerBottomSheet.Companion.ClickChild
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.AddTravellerBottomSheet.Companion.ClickInfant
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.FareBreakUpBottomSheet
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.FlightDetailsBottomSheet
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.ReviewDetailsPassangersBottomSheet
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.ReviewDetailsPassangersBottomSheet.Companion.passangerDetailsList
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.ReviewDetailsPassangersBottomSheet.Companion.tempBookingPassangerDetails
import com.bos.payment.appName.ui.viewmodel.TravelViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.generateRandomNumber
import com.bumptech.glide.Glide
import com.google.gson.Gson

class AddDetailsPassangerActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddDetailsPassangerBinding
    lateinit var adapter: AddPassengerAdapter
    var stops: String = ""

    var adultcount: Int = 0
    var childcount: Int = 0
    var infantcount: Int = 0


    var fromTocityname: String = ""
    var passangerdetails: String = ""
    lateinit var mStash : MStash



    companion object {
        var segmentListPassangerDetail: MutableList<SegmentsItem?> = mutableListOf()
        var flightDetailsPassangerDetail: MutableList<FlightsItem?> = mutableListOf()
        var adultList: MutableList<PassangerDataList> = mutableListOf()
        var childList: MutableList<PassangerDataList> = mutableListOf()
        var infantList: MutableList<PassangerDataList> = mutableListOf()
        var fareList: MutableList<FareDetailsFlightItem> = mutableListOf()
        var farebreakupList: MutableList<fareBreakup> = mutableListOf()
        var paxDetailsListFromReprice: MutableList<RequiredPAXDetailsItem> = mutableListOf()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDetailsPassangerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mStash= MStash(this)

        val segments = listOf(mapOf("departure_DateTime" to segmentListPassangerDetail[0]!!.departureDateTime, "arrival_DateTime" to segmentListPassangerDetail[0]!!.arrivalDateTime),
            mapOf(
                "departure_DateTime" to segmentListPassangerDetail[segmentListPassangerDetail.size - 1]!!.departureDateTime,
                "arrival_DateTime" to segmentListPassangerDetail[segmentListPassangerDetail.size - 1]!!.arrivalDateTime
            )
        )
        totalDurationTime = calculateTotalFlightDuration(segments)

        var originDateTime = splitDateTime(segmentListPassangerDetail[0]!!.departureDateTime)

        var originDate = formatDate1(originDateTime.first).plus(",").plus(formatDate2(originDateTime.first))

        var originTime = originDateTime.second

        var destinationDateTime = splitDateTime(segmentListPassangerDetail[segmentListPassangerDetail.size - 1]!!.arrivalDateTime)
        var DestinationTime = destinationDateTime.second


        adultcount = adultCount
        childcount = childCount
        infantcount = infantCount

        if (adultcount > 0) {
            binding.AdultLayout.visibility = View.VISIBLE
        }

        if (childcount > 0) {
            binding.ChildLayout.visibility = View.VISIBLE
        } else {
            binding.ChildLayout.visibility = View.GONE
        }

        if (infantcount > 0) {
            binding.InfantLayout.visibility = View.VISIBLE
        } else {
            binding.InfantLayout.visibility = View.GONE
        }

        fromTocityname = segmentListPassangerDetail[0]!!.originCity.plus(" to ")
            .plus(segmentListPassangerDetail[segmentListPassangerDetail.size - 1]!!.destinationCity)

        passangerdetails = originDate.plus(",").plus(adultCount).plus("Adult").plus(",").plus(childCount)
                .plus("Child").plus(",").plus(infantCount).plus("Infant").plus(",").plus(className)

        if (segmentListPassangerDetail.size == 1) {
            stops = "Non stops"
        } else {
            stops = (segmentListPassangerDetail.size - 1).toString().plus(" stop")
        }
        // "Fri,4 Jul| 19:10 - 01:05|5h 55m |1 Stop|Economy Class"
        var traveldetails = originDate.plus(" | ").plus(originTime).plus(" - ").plus(DestinationTime).plus(" | ").plus(totalDurationTime).plus(" | ").plus(stops).plus(" | ").plus(className)

        setDataOnView(traveldetails)
        setOnClickListner()
        setDataOfGst()
        HitApiForFareBreakUp()

    }


    fun setDataOnView(traveldetails: String) {
        binding.destinationCityName.text =
            segmentListPassangerDetail[segmentListPassangerDetail.size - 1]!!.destinationCity
        binding.fromAirportCode.text = flightDetailsPassangerDetail[0]!!.origin
        binding.toAirportCode.text = flightDetailsPassangerDetail[0]!!.destination
        binding.travelprimedetails.text = traveldetails

        binding.fromtocitytxt.text = fromTocityname
        binding.passangerdetailstxt.text = passangerdetails
        binding.cabinbagweight.text =
            flightDetailsPassangerDetail!![0]?.fares!![0].fareDetails!![0].freeBaggage.handBaggage.plus(
                " (1 piece only)/Adult"
            )
        binding.checkinbagweight.text =
            flightDetailsPassangerDetail!![0]?.fares!![0].fareDetails!![0].freeBaggage.checkInBaggage.plus(
                " (1 piece only)/Adult"
            )

        var airportIcon = GetAirlineLogo(flightDetailsPassangerDetail[0]!!.airlineCode)
        Glide.with(this).load(airportIcon).error(
            R.drawable.airplaceholder).into(binding.airlineicon)

        binding.adultcount.text = adultList.size.toString().plus("/").plus(adultcount)
        binding.childcount.text = childList.size.toString().plus("/").plus(childcount)
        binding.infantcount.text = infantList.size.toString().plus("/").plus(infantcount)

    }


    fun setAdultData() {
        if (adultList.size > 0) {
            binding.showadultlist.visibility = View.VISIBLE
            adapter = AddPassengerAdapter(adultList)
            binding.showadultlist.adapter = adapter
            adapter.notifyDataSetChanged()
            binding.adultcount.text = adultList.size.toString().plus("/").plus(adultcount)


        }

    }


    fun setChildData() {
        if (childList.size > 0) {
            binding.showchildlist.visibility = View.VISIBLE
            adapter = AddPassengerAdapter(childList)
            binding.showchildlist.adapter = adapter
            adapter.notifyDataSetChanged()
            binding.childcount.text = childList.size.toString().plus("/").plus(childcount)
        }

    }


    fun setInfantData() {
        if (infantList.size > 0) {
            binding.showinfantlist.visibility = View.VISIBLE
            adapter = AddPassengerAdapter(infantList)
            binding.showinfantlist.adapter = adapter
            adapter.notifyDataSetChanged()
            binding.infantcount.text = infantList.size.toString().plus("/").plus(infantcount)
        }

    }


    fun setDataForContactDetails() {
        binding.mailidTxt.text = emailid
        binding.mobileNumber.text = contactNumber
    }


    fun setDataOfGst() {
        if (CheckBox) {
            binding.gstCheckBox.isChecked = true
            binding.gstlayout.visibility = View.VISIBLE
            binding.companyName.text = companyName
            binding.gstnumber.text = registrationNo
        } else {
            if (!companyName.isNullOrBlank() || !registrationNo.isNullOrBlank()) {
                binding.gstCheckBox.isChecked = true
            } else {
                binding.gstCheckBox.isChecked = false
            }

        }
    }


    fun setOnClickListner() {


        binding.back.setOnClickListener { finish() }

        binding.back1.setOnClickListener { finish() }

        binding.addAdult.setOnClickListener {
            if (adultList.size < adultcount) {
                ClickAdult = true
                ClickChild = false
                ClickInfant = false
                val bottomfrag = AddTravellerBottomSheet()
                supportFragmentManager.let {
                    bottomfrag.show(it, AddTravellerBottomSheet.TAG)

                }
            } else {
                Toast.makeText(
                    this,
                    "You have already selected $adultcount ADULT",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding.addChild.setOnClickListener {
            if (childList.size < childcount) {
                ClickAdult = false
                ClickChild = true
                ClickInfant = false
                val bottomfrag = AddTravellerBottomSheet()
                supportFragmentManager.let {
                    bottomfrag.show(it, AddTravellerBottomSheet.TAG)

                }
            } else {
                Toast.makeText(
                    this,
                    "You have already selected $childcount CHILD",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding.addInfant.setOnClickListener {
            if (infantList.size < infantcount) {
                ClickAdult = false
                ClickChild = false
                ClickInfant = true
                val bottomfrag = AddTravellerBottomSheet()
                supportFragmentManager.let {
                    bottomfrag.show(it, AddTravellerBottomSheet.TAG)
                }
            } else {
                Toast.makeText(
                    this,
                    "You have already selected $infantcount INFANT",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding.sentmailphonelayout.setOnClickListener {
            val bottomfrag = AddContactAndMobileBottomSheet()
            supportFragmentManager.let {
                bottomfrag.show(it, AddContactAndMobileBottomSheet.TAG)
            }
        }

        binding.gstCheckBox.setOnClickListener {
            val bottomfrag = AddGSTInformationBottomSheet()
            supportFragmentManager.let {
                bottomfrag.show(it, AddGSTInformationBottomSheet.TAG)
            }
        }

        binding.editIcon.setOnClickListener {
            if (!companyName.isNullOrBlank() || !registrationNo.isNullOrBlank()) {
                CheckBox = true
            } else {
                CheckBox = false
            }
            val bottomfrag = AddGSTInformationBottomSheet()
            supportFragmentManager.let {
                bottomfrag.show(it, AddGSTInformationBottomSheet.TAG)
            }
        }

        binding.priceBreakup.setOnClickListener {
            val bottomfrag = FareBreakUpBottomSheet()
            supportFragmentManager.let {
                bottomfrag.show(it, FareBreakUpBottomSheet.TAG)
            }
        }


        binding.continuee.setOnClickListener {
            tempBookingPassangerDetails.clear()

            val mailId = binding.mailidTxt.text.toString().trim()
            val contactNo = binding.mobileNumber.text.toString().trim()
            val isGstChecked = binding.gstCheckBox.isChecked // Assuming your checkbox ID is gstCheckbox
            val gstNo = binding.gstnumber.text.toString().trim()
            val gstHolderName = binding.companyName.text.toString().trim()
            val gstAddress = binding.companyName.text.toString().trim()

            // Validate email
            if (mailId.isEmpty()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate mobile number
            if (contactNo.isEmpty() || contactNo.length < 10) {
                Toast.makeText(this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate GST fields if checkbox is selected
            if (isGstChecked) {
                if (gstNo.isEmpty()) {
                    Toast.makeText(this, "Please enter GST number", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (gstHolderName.isEmpty()) {
                    Toast.makeText(this, "Please enter GST holder name", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (gstAddress.isEmpty()) {
                    Toast.makeText(this, "Please enter GST address", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }


            var tempdata = TempBookingPassangerDetails(contactNo,contactNo,mailId,isGstChecked,gstNo,gstHolderName,gstAddress)
            tempBookingPassangerDetails.add(tempdata)

            if (adultList.size > 0) {
                val bottomfrag = ReviewDetailsPassangersBottomSheet()
                supportFragmentManager.let {
                    bottomfrag.show(it, ReviewDetailsPassangersBottomSheet.TAG)
                }
            } else {
                if (childList.size > 0 || infantList.size > 0) {
                    val bottomfrag = ReviewDetailsPassangersBottomSheet()
                    supportFragmentManager.let { bottomfrag.show(it, ReviewDetailsPassangersBottomSheet.TAG)
                    }
                } else {
                    Toast.makeText(this, "Please add passanger details", Toast.LENGTH_SHORT).show()
                }
            }

        }


    }


    fun HitApiForFareBreakUp() {
        farebreakupList.clear()
        if (fareList!!.size > 0) {
            fareList.forEach { it ->
                if (it.paXType.equals("0")) {
                    farebreakupList.add(
                        fareBreakup(
                            it.paXType,
                            it.airportTaxAmount,
                            it.basicAmount,
                            adultcount
                        )
                    )
                }

                if (it.paXType.equals("1")) {
                    farebreakupList.add(
                        fareBreakup(
                            it.paXType,
                            it.airportTaxAmount,
                            it.basicAmount,
                            childcount
                        )
                    )
                }

                if (it.paXType.equals("2")) {
                    farebreakupList.add(
                        fareBreakup(
                            it.paXType,
                            it.airportTaxAmount,
                            it.basicAmount,
                            infantcount
                        )
                    )
                }

            }

            Log.d("FareList", "" + farebreakupList)
            val total = calculateGrandTotalAmount(farebreakupList)

            mStash.setStringValue(Constants.AirTotalTicketPrice, total.toString())

            binding.price.text = " ₹ ".plus(total)

            if (adultcount > 0 && childcount == 0 && infantcount == 0) {
                binding.typePassanger.text = "FOR ".plus(adultcount).plus(" ADULT")
            } else {
                binding.typePassanger.text = "FOR ".plus((adultcount + childcount + infantcount)).plus("  PASSANGERS")
            }
            binding.priceBreakup.visibility = View.VISIBLE

            addValueForAirTicket(farebreakupList)


        }

    }


    fun calculateGrandTotalAmount(fareList: List<fareBreakup>): Double {
        return fareList.sumOf { item ->
            val airportTax = item.airportTax_amount.toDoubleOrNull() ?: 0.0
            val basic = item.basic_amount.toDoubleOrNull() ?: 0.0
            val quantity = item.quantity

            (airportTax + basic) * quantity
        }
    }


    fun addValueForAirTicket(farebreakupList: MutableList<fareBreakup>){

        val adult = farebreakupList.find { it.paxType == "0" }
        val child = farebreakupList.find { it.paxType == "1" }
        val infant = farebreakupList.find { it.paxType == "2" }

        val adultAmount = (adult?.quantity ?: 0) * (adult?.basic_amount?.toDoubleOrNull() ?: 0.0)
        val childAmount = (child?.quantity ?: 0) * (child?.basic_amount?.toDoubleOrNull() ?: 0.0)
        val infantAmount = (infant?.quantity ?: 0) * (infant?.basic_amount?.toDoubleOrNull() ?: 0.0)


        val airportTaxadultAmount = (adult?.quantity ?: 0) * (adult?.airportTax_amount?.toDoubleOrNull() ?: 0.0)
        val airportTaxchildAmount = (child?.quantity ?: 0) * (child?.airportTax_amount?.toDoubleOrNull() ?: 0.0)
        val airportTaxinfantAmount = (infant?.quantity ?: 0) * (infant?.airportTax_amount?.toDoubleOrNull() ?: 0.0)



        // Ensure all are Double or Int before addition
        val totalBaseFare = adultAmount + childAmount + infantAmount
        val totalTaxFare = airportTaxadultAmount + airportTaxchildAmount + airportTaxinfantAmount
        val overallTotalAmount = totalBaseFare + totalTaxFare

        BaseFare  = "₹ " . plus( totalBaseFare.toString() )
        TaxAndFees = "₹ " . plus( totalTaxFare.toString())
        GrossFare  = "₹ " . plus(overallTotalAmount.toString())


    }

}