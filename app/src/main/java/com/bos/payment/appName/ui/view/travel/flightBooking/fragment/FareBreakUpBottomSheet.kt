package com.bos.payment.appName.ui.view.travel.flightBooking.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.travel.flight.FlightsItem
import com.bos.payment.appName.data.model.travel.flight.SegmentsItem
import com.bos.payment.appName.data.model.travel.flight.fareBreakup
import com.bos.payment.appName.databinding.AddtravellersitemlayoutBinding
import com.bos.payment.appName.databinding.ContactmobileItemlayoutBinding
import com.bos.payment.appName.databinding.FarebreakupItemLayoutBinding
import com.bos.payment.appName.databinding.FlightdetailsItemBottomsheetBinding
import com.bos.payment.appName.databinding.TravellersclassItemBottomsheetBinding
import com.bos.payment.appName.ui.view.travel.adapter.FlightTicketDetailsAdapter
import com.bos.payment.appName.ui.view.travel.adapter.PassangerDataList
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.FlightDetails
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.adultCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.calculateTotalFlightDuration
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.childCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.className
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.infantCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.totalCount
import com.bos.payment.appName.ui.view.travel.flightBooking.FlightConstant.Companion.totalDurationTime
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.adultList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.childList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.farebreakupList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.flightDetailsPassangerDetail
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.infantList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.segmentListPassangerDetail
import com.bos.payment.appName.utils.Constants.scanForActivity
import com.bos.payment.appName.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

class FareBreakUpBottomSheet:BottomSheetDialogFragment() {
    private lateinit var binding : FarebreakupItemLayoutBinding
    var fareList : MutableList<fareBreakup> = arrayListOf()

    companion object {
        const val TAG = "FareBreakUpBottomSheet"


    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FarebreakupItemLayoutBinding.inflate(inflater, container, false)

        if(farebreakupList.size>0){
            fareList.clear()
            fareList.addAll(farebreakupList)
        }

        setonclicklistner()
        setDataOnView()
        return binding.root
    }


    fun setDataOnView(){

        toggleLayoutVisibility(adultCount, binding.adultlayout, binding.adultairporttaxlayout)
        toggleLayoutVisibility(childCount, binding.childlayout, binding.childairporttaxlayout)
        toggleLayoutVisibility(infantCount, binding.infantlayout, binding.infantairporttaxlayout)


        val adult = fareList.find { it.paxType == "0" }
        val child = fareList.find { it.paxType == "1" }
        val infant = fareList.find { it.paxType == "2" }

        binding.adultcount.text = "${adult?.quantity ?: 0} x ₹ ${adult?.basic_amount ?: "0"}"
        binding.childcount.text = "${child?.quantity ?: 0} x ₹ ${child?.basic_amount ?: "0"}"
        binding.infantcount.text = "${infant?.quantity ?: 0} x ₹ ${infant?.basic_amount ?: "0"}"

        binding.adultAirporttaxCount.text = "${adult?.quantity ?: 0} x ₹ ${adult?.airportTax_amount ?: "0"}"
        binding.childAirporttaxCount.text = "${child?.quantity ?: 0} x ₹ ${child?.airportTax_amount ?: "0"}"
        binding.infantAirporttaxCount.text = "${infant?.quantity ?: 0} x ₹ ${infant?.airportTax_amount ?: "0"}"


        val adultAmount = (adult?.quantity ?: 0) * (adult?.basic_amount?.toDoubleOrNull() ?: 0.0)
        val childAmount = (child?.quantity ?: 0) * (child?.basic_amount?.toDoubleOrNull() ?: 0.0)
        val infantAmount = (infant?.quantity ?: 0) * (infant?.basic_amount?.toDoubleOrNull() ?: 0.0)


        binding.totalamountofadult.text = "₹ %.2f" . format(adultAmount.toDouble())
        binding.totalamountofchild.text = "₹ %.2f" . format(childAmount.toDouble())
        binding.totalamountofinfant.text = "₹ %.2f" . format(infantAmount.toDouble())


        val airportTaxadultAmount = (adult?.quantity ?: 0) * (adult?.airportTax_amount?.toDoubleOrNull() ?: 0.0)
        val airportTaxchildAmount = (child?.quantity ?: 0) * (child?.airportTax_amount?.toDoubleOrNull() ?: 0.0)
        val airportTaxinfantAmount = (infant?.quantity ?: 0) * (infant?.airportTax_amount?.toDoubleOrNull() ?: 0.0)


        binding.totaltaxesofadult.text = "₹ %.2f".format(airportTaxadultAmount.toDouble())
        binding.totaltaxesofchild.text ="₹ %.2f " . format( airportTaxchildAmount.toDouble())
        binding.totaltaxesofinfant.text = "₹ %.2f " . format(airportTaxinfantAmount.toDouble())


        // Ensure all are Double or Int before addition
        val totalBaseFare = adultAmount + childAmount + infantAmount
        val totalTaxFare = airportTaxadultAmount + airportTaxchildAmount + airportTaxinfantAmount
        val overallTotalAmount = totalBaseFare + totalTaxFare

        // Set the text
        binding.totalbasefare.text = "₹ %.2f".format(totalBaseFare.toDouble())
        binding.totaltaxesfare.text = "₹ %.2f".format(totalTaxFare.toDouble())
        binding.overalltotalamount.text = "₹ %.2f".format(overallTotalAmount.toDouble())


    }


    private fun setonclicklistner(){

        binding.cross.setOnClickListener {
            dialog!!.dismiss()
        }



    }


    fun toggleLayoutVisibility(count: Int, layout: View, taxLayout: View) {
        if (count > 0) {
            layout.visibility = View.VISIBLE
            taxLayout.visibility = View.VISIBLE
        } else {
            layout.visibility = View.GONE
            taxLayout.visibility = View.GONE
        }
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // used to show the bottom sheet dialog
        dialog?.setOnShowListener { it ->
            val d = it as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                    sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED

                val layoutParams = sheet.layoutParams
                val windowHeight = Resources.getSystem().displayMetrics.heightPixels
                layoutParams.height = windowHeight
                sheet.layoutParams = layoutParams
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
       // (activity as? FlightMainActivity)?.setData()

      /*  if(context is FlightMainActivity){
            (context as? FlightMainActivity)?.setData()
        }
        else {*/
            (scanForActivity(context)?.supportFragmentManager?.findFragmentByTag("FlightMainFragment") as? FlightMainFragment)?.setData()
       // }

    }



}