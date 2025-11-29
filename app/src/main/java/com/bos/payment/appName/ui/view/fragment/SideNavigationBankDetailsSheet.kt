package com.bos.payment.appName.ui.view.fragment

import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.R
import com.bos.payment.appName.constant.ConstantClass
import com.bos.payment.appName.constant.CustomFuseLocationActivity
import com.bos.payment.appName.data.model.justpaymodel.CheckBankDetailsModel
import com.bos.payment.appName.data.model.justpaymodel.GenerateQRCodeReq
import com.bos.payment.appName.data.model.justpaymodel.GenerateVirtualAccountModel
import com.bos.payment.appName.data.model.justpaymodel.UpdateBankDetailsReq
import com.bos.payment.appName.data.model.travel.flight.FlightsItem
import com.bos.payment.appName.data.model.travel.flight.SegmentsItem
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.repository.MobileRechargeRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.data.viewModelFactory.MobileRechargeViewModelFactory
import com.bos.payment.appName.databinding.AddtravellersitemlayoutBinding
import com.bos.payment.appName.databinding.BankdetailslistlayoutBinding
import com.bos.payment.appName.databinding.ContactmobileItemlayoutBinding
import com.bos.payment.appName.databinding.FlightdetailsItemBottomsheetBinding
import com.bos.payment.appName.databinding.GstDetailsLayoutBinding
import com.bos.payment.appName.databinding.TravellersclassItemBottomsheetBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.Dashboard.activity.JustPeDashboard
import com.bos.payment.appName.ui.view.Dashboard.activity.JustPeDashboard.Companion.QRBimap
import com.bos.payment.appName.ui.view.Dashboard.activity.JustPeDashboard.Companion.vpa
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
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.AddDetailsPassangerActivity.Companion.segmentListPassangerDetail
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.ui.viewmodel.GetAllMobileRechargeViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils
import com.bos.payment.appName.utils.Utils.PD
import com.bos.payment.appName.utils.Utils.generateQrBitmap
import com.bos.payment.appName.utils.Utils.generateRandomNumber
import com.bos.payment.appName.utils.Utils.getStateCode
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

class SideNavigationBankDetailsSheet:BottomSheetDialogFragment() {
    private lateinit var binding : BankdetailslistlayoutBinding
    private var mStash: MStash? = null
    var holdername:String ? = ""
    var AccountNumber:String ? = ""
    var IFSC:String ?= ""
    var selleridentifier:String? = ""
    var mobilenumber:String ?= ""
    var emailid:String ? = ""
    var accounttype:String ?= ""
    var registrationID:String ?= ""
    var merchantcode:String ?= ""
    private lateinit var pd: AlertDialog
    private lateinit var MobileRechargeViewModel: GetAllMobileRechargeViewModel
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel

    private var customFuseLocation: CustomFuseLocationActivity? = null


    companion object {
        const val TAG = "AddBankAccountSheet"
        var latt : Double = 0.0
        var long : Double = 0.0
        var cityName : String = ""
        var district : String = ""
        var pincode : String = ""
        var Address : String = ""
        var statecode : String = ""
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BankdetailslistlayoutBinding.inflate(inflater, container, false)

        mStash = MStash.getInstance(requireContext())
        pd = PD(requireContext())

        MobileRechargeViewModel = ViewModelProvider(this, MobileRechargeViewModelFactory (MobileRechargeRepository(RetrofitClient.apiRechargeInterface)))[GetAllMobileRechargeViewModel::class.java]

        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]

        getFuseLocation()
        setinitData()
        setonclicklistner()


        return binding.root
    }


    fun setinitData(){
        holdername =  mStash!!.getStringValue(Constants.SettlementAccountName,"")
        AccountNumber =   mStash!!.getStringValue(Constants.SettlementAccountNumber,"")
        IFSC =   mStash!!.getStringValue(Constants.SettlementAccountIfsc,"")
        selleridentifier =   mStash!!.getStringValue(Constants.SellerIdentifier,"")
        mobilenumber =   mStash!!.getStringValue(Constants.BankMobileNumber,"")
        emailid =   mStash!!.getStringValue(Constants.EmailId,"")
        accounttype =   mStash!!.getStringValue(Constants.BankAccountType,"")
        merchantcode =   mStash!!.getStringValue(Constants.MerchantId,"")
        registrationID =   mStash!!.getStringValue(Constants.RegistrationId,"")

        binding.holdername.text= holdername
        binding.accountnumber.text= AccountNumber
        binding.ifsccode.text= IFSC
        binding.selleridentifier.text= selleridentifier
        binding.mobilenumber.text= mobilenumber
        binding.emailid.text= emailid
        binding.accountType.text= accounttype

        if(mStash!!.getStringValue(Constants.ISQRCodeGenerated,"").equals("No", ignoreCase = true)||mStash!!.getStringValue(Constants.ISQRCodeGenerated,"")!!.isBlank()){
            binding.confirmbutton.visibility= View.VISIBLE
        }
        else{
            binding.confirmbutton.visibility= View.GONE
        }

    }


    private fun setonclicklistner(){
        binding.cross.setOnClickListener {
            dialog!!.dismiss()
        }

        binding.confirmbutton.setOnClickListener {
            createVirtual()
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

    }


    fun createVirtual(){
        if(latt>0.0 && long> 0.0 ) {
           var address = getAddressFromLatLng(requireActivity(),latt,long)
            if(!address.isNullOrBlank()) {
                val requestId = generateRandomNumber()
                 val requestForCreateVirtualAccount = GenerateVirtualAccountModel(
                    apiId = "20261",
                    bankid = "2",
                    partnerReferenceNo = requestId,
                    p1businessName = holdername,
                    p2settlementAccountName = holdername,
                    p3sellerIdentifier = selleridentifier,
                    p4mobileNumber = mobilenumber,
                    p5emailId = emailid,
                    p6mcc = "6012",
                    p7turnoverType = "SMALL",
                    p8acceptanceType = "OFFLINE",
                    p9ownershipType = "PROPRIETARY",
                    p10city = cityName,
                    p11district = district,
                    p12stateCode = statecode,
                    p13pincode = pincode,
                    p14pan = "",
                    p15gstNumber = "",
                    p16settlementAccountNumber = AccountNumber,
                    p17settlementAccountIfsc = IFSC,
                    p18Latitude = "$latt",
                    p19Longitude = "$long",
                    p20addressLine1 = Address,
                    p21addressLine2 = Address,
                    p22LLPINCIN = "",
                    p26DOB = "28/05/1987",
                    p27dOI = "01/02/2024",
                    p28websiteURLAppPackageName = "www.boscenter.in",
                    RegistrationID = merchantcode
                )
                Log.d("virtualaccountreq", Gson().toJson(requestForCreateVirtualAccount))

                MobileRechargeViewModel.createVirtualAccount(requestForCreateVirtualAccount)
                    .observe(this) { resource ->
                        resource?.let {
                            when (it.apiStatus) {
                                ApiStatus.SUCCESS -> {
                                    pd.dismiss()
                                    it.data?.let { users ->
                                        users.body()?.let { response ->
                                            pd.dismiss()
                                            Log.d("virtualaccountresp", Gson().toJson(response))
                                            binding.responsemasg.text = Gson().toJson(response)

                                            if (response.status!!) {
                                                createQRCode()
                                            }
                                            else {
                                                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                                            }

                                        }
                                    }
                                }

                                ApiStatus.ERROR -> {
                                    pd.dismiss()
                                }

                                ApiStatus.LOADING -> {
                                    pd.show()
                                }
                            }
                        }
                    }
            }
        }

    }

    fun createQRCode(){

        val requestForCreateVirtualAccount = GenerateQRCodeReq(
                RegistrationId = registrationID,
                mobileNumber = mobilenumber,
                merchantCode = merchantcode)

        Log.d("qrcodereq", Gson().toJson(requestForCreateVirtualAccount))

        MobileRechargeViewModel.createQRCode(requestForCreateVirtualAccount).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                pd.dismiss()
                                Log.d("qrcoderesponse", Gson().toJson(response))

                                Toast.makeText(requireContext(),response.message,Toast.LENGTH_LONG).show()

                                if(response.status!!){
                                    var url = response.details!!.qrCode
                                    QRBimap = generateQrBitmap(url!!, 800)
                                    vpa = response.details.vpa
                                    hitapiForUpdateBankDetails(url,vpa!!,"Yes","Yes")
                                    Toast.makeText(requireContext(),response.message,Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    hitapiForUpdateBankDetails("","","No","No")
                                    Toast.makeText(requireContext(),response.message,Toast.LENGTH_SHORT).show()
                                }

                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        pd.dismiss()
                    }

                    ApiStatus.LOADING -> {
                        pd.show()
                    }
                }
            }
        }


    }

    fun hitapiForUpdateBankDetails(intent:String, vpa:String,isQRCodeActivate:String,isQRCodeGenerated:String){
        val updateBankDetails = UpdateBankDetailsReq(
            vpaid = vpa,
            isQRCodeActivate =  isQRCodeActivate,
            isQRCodeGenerated =  isQRCodeGenerated,
            retailerCode =  registrationID,
            staticQRCodeIntentUrl =  intent)

        Log.d("bankdetailereq", Gson().toJson(updateBankDetails))

        getAllApiServiceViewModel.updateBankDetails(updateBankDetails).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        pd.dismiss()
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("BankdetailsResponse",Gson().toJson(response))
                                if(response.isSuccess!!){
                                    (activity as JustPeDashboard)?.setQRCodeWithBankDetailsCodition()
                                     dialog!!.dismiss()

                                }
                                else{

                                }

                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        pd.dismiss()
                    }

                    ApiStatus.LOADING -> {
                        pd.dismiss()
                    }
                }
            }
        }
    }


    private fun getFuseLocation() {
        val fused = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fused.lastLocation.addOnSuccessListener {
            latt = it.latitude
            long = it.longitude

            Log.d("LatLongg" , "$latt $long")

        }
    }

    fun getAddressFromLatLng(context: Context, latitude: Double, longitude: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]

                Address = address.getAddressLine(0) ?: ""
                cityName = address.locality ?: ""
                district = address.subAdminArea ?: ""
                val state = address.adminArea ?: ""
                statecode = getStateCode(state ?: "")
                pincode = address.postalCode ?: ""

                // You can return full formatted string
                Log.d("Address", "Address: $Address\nCity: $cityName\nDistrict: $district\nState: $state\nPincode: $pincode")
                "Address: $Address\nCity: $cityName\nDistrict: $district\nState: $state\nPincode: $pincode"
            } else {
                "Address not found"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.message}"
        }
    }


}