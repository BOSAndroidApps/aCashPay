package com.bos.payment.appName.ui.view.travel.flightBooking.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.travel.flight.FlightsItem
import com.bos.payment.appName.data.model.travel.flight.RequiredPAXDetailsItem
import com.bos.payment.appName.data.model.travel.flight.SegmentsItem
import com.bos.payment.appName.databinding.AddtravellersitemlayoutBinding
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
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.flightDetailsPassangerDetail
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.infantList
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.paxDetailsListFromReprice
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.segmentListPassangerDetail
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.ReviewDetailsPassangersBottomSheet.Companion.passangerDetailsList
import com.bos.payment.appName.utils.Constants.scanForActivity
import com.bos.payment.appName.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

class AddTravellerBottomSheet:BottomSheetDialogFragment() {
    private lateinit var binding : AddtravellersitemlayoutBinding
    val titles = listOf("MR", "MRS", "MS")
    var gender :String=""
    var title : String=""
    var dob: String=""
    var firstname:String=""
    var lastname:String=""
    var passportno:String=""
    var passportissuecountryname:String=""
    var passportexpirydate:String=""

    private val myCalender = Calendar.getInstance()
    val calendar = Calendar.getInstance()

    private val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        myCalender.set(Calendar.YEAR, year)
        myCalender.set(Calendar.MONTH, monthOfYear)
        myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val format = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val selectedDate = format.format(myCalender.time)

        binding.dob.setText(selectedDate)
    }

    var paxDetailsList: MutableList<RequiredPAXDetailsItem> = mutableListOf()


    companion object {
        const val TAG = "AddTravellerBottomSheet"
        var ClickAdult: Boolean = false
        var ClickChild: Boolean = false
        var ClickInfant: Boolean = false
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = AddtravellersitemlayoutBinding.inflate(inflater, container, false)

        if(paxDetailsListFromReprice.size>0){
            paxDetailsList.clear()
            paxDetailsList.addAll(paxDetailsListFromReprice)
            showAndHideView()
        }
        setonclicklistner()
        setDataOnView()
        clickforhoverbutton()

        return binding.root
    }


    fun showAndHideView(){

        val selectedPaxDetail = paxDetailsList.find {
            (ClickAdult && it.paxType == 0) ||
                    (ClickChild && it.paxType == 1) ||
                    (ClickInfant && it.paxType == 2)
        }

        selectedPaxDetail?.let { pax ->
            binding.genderlayout.visibility = if (pax.gender) View.VISIBLE else View.GONE
            binding.gendertitle.visibility = if (pax.gender) View.VISIBLE else View.GONE
            binding.titleSpinner.visibility = if (pax.title) View.VISIBLE else View.GONE
            binding.firstname.visibility = if (pax.firstName) View.VISIBLE else View.GONE
            binding.lastname.visibility = if (pax.lastName) View.VISIBLE else View.GONE
            binding.doblayout.visibility = if (pax.dob) View.VISIBLE else View.GONE
            binding.passportNo.visibility = if (pax.passportNumber) View.VISIBLE else View.GONE
            binding.passportIssuingCountry.visibility = if (pax.passportIssuingCountry) View.VISIBLE else View.GONE
            binding.passportexpirydatelayout.visibility = if (pax.passportExpiry) View.VISIBLE else View.GONE
        }

    }


    private fun setDataOnView(){

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, titles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.titleSpinner.adapter = adapter


    }


    private fun setonclicklistner(){

        binding.dobcalander.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        binding.cross.setOnClickListener {
            dialog!!.dismiss()
        }


        binding.titleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedTitle = titles[position]
                title = selectedTitle

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }



        binding.malecard.setOnClickListener {
            clickforhoverbutton()
            binding.malecard.setCardBackgroundColor(context?.resources!!.getColor(R.color.teal_700))
            binding.maletxt.setTextColor(context?.resources!!.getColor(R.color.white))
            gender="Male"
        }



        binding.femalecard.setOnClickListener {
            clickforhoverbutton()
            binding.femalecard.setCardBackgroundColor(context?.resources!!.getColor(R.color.teal_700))
            binding.femaletxt.setTextColor(context?.resources!!.getColor(R.color.white))
            gender="Female"

        }



        binding.confirmbutton.setOnClickListener {

            firstname= binding.firstname.text.toString()
            lastname= binding.lastname.text.toString()
            dob= binding.dob.text.toString()

            passportno = binding.passportNo.text.toString()
            passportissuecountryname = binding.passportIssuingCountry.text.toString()
            passportexpirydate = binding.passportExpireDate.text.toString()


            var isValid = true

            if (binding.firstname.visibility == View.VISIBLE && firstname.isEmpty()) {
                binding.firstname.error = "First name is required"
                isValid = false
            }

            if (binding.lastname.visibility == View.VISIBLE && lastname.isEmpty()) {
                binding.lastname.error = "Last name is required"
                isValid = false
            }

            if (binding.doblayout.visibility == View.VISIBLE && dob.isEmpty()) {
                binding.dob.error = "Date of birth is required"
                isValid = false
            }

            if (binding.passportNo.visibility == View.VISIBLE && passportno.isEmpty()) {
                binding.passportNo.error = "Passport number is required"
                isValid = false
            }

            if (binding.passportIssuingCountry.visibility == View.VISIBLE && passportissuecountryname.isEmpty()) {
                binding.passportIssuingCountry.error = "Issuing country is required"
                isValid = false
            }

            if (binding.passportexpirydatelayout.visibility == View.VISIBLE && passportexpirydate.isEmpty()) {
                binding.passportExpireDate.error = "Passport expiry date is required"
                isValid = false
            }

            // You can now use isValid to control submission logic
            if (isValid) {
                // proceed with form submission

                if (ClickAdult) {
                    val newPassenger = PassangerDataList(title, firstname, lastname, dob, gender, "ADULT",passportno,passportissuecountryname,passportexpirydate)
                    if (!adultList.contains(newPassenger)) {
                        adultList.add(newPassenger)
                        passangerDetailsList.add(newPassenger)
                        (context as AddDetailsPassangerActivity).setAdultData()
                    }
                }

                if (ClickChild) {
                    val newPassenger = PassangerDataList(title, firstname, lastname, dob, gender, "CHILD",passportno,passportissuecountryname,passportexpirydate)
                    if (!childList.contains(newPassenger)) {
                        childList.add(newPassenger)
                        passangerDetailsList.add(newPassenger)
                        (context as AddDetailsPassangerActivity).setChildData()
                    }
                }

                if (ClickInfant) {
                    val newPassenger = PassangerDataList(title, firstname, lastname, dob, gender, "INFANT",passportno,passportissuecountryname,passportexpirydate)
                    if (!infantList.contains(newPassenger)) {
                        infantList.add(newPassenger)
                        passangerDetailsList.add(newPassenger)
                        (context as AddDetailsPassangerActivity).setInfantData()
                    }
                }

                dismiss()
            }


        }

    }


    fun clickforhoverbutton()
    {
        binding.malecard.setCardBackgroundColor(context?.resources!!.getColor(R.color.white))
        binding.femalecard.setCardBackgroundColor(context?.resources!!.getColor(R.color.white))
        binding.maletxt.setTextColor(context?.resources!!.getColor(R.color.teal_700))
        binding.femaletxt.setTextColor(context?.resources!!.getColor(R.color.teal_700))
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
        //(activity as? FlightMainActivity)?.setData()
       /* if(context is FlightMainActivity){
            (context as? FlightMainActivity)?.setData()
        }
        else {*/
            (scanForActivity(context)?.supportFragmentManager?.findFragmentByTag("FlightMainFragment") as? FlightMainFragment)?.setData()
       // }
    }



}