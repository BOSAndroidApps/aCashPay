package com.bos.payment.appName.ui.view.Dashboard.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.managekyc.CountryStateDistrictReq
import com.bos.payment.appName.data.model.managekyc.RetailerProfileReq
import com.bos.payment.appName.data.model.managekyc.RetailerProfileResp
import com.bos.payment.appName.data.model.managekyc.UpdateKycReq
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.data.viewModelFactory.TravelViewModelFactory
import com.bos.payment.appName.databinding.ActivityManageKycBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.Dashboard.activity.AadharCardWebViewDIGILockerPage.Companion.digilockerLink
import com.bos.payment.appName.ui.view.LoginActivity
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.ui.viewmodel.TravelViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Constants.AadharTransactionIdNo
import com.bos.payment.appName.utils.Constants.AadharVerified
import com.bos.payment.appName.utils.Constants.BillRechargeCard
import com.bos.payment.appName.utils.Constants.FinanceCard
import com.bos.payment.appName.utils.Constants.TravelCard
import com.bos.payment.appName.utils.Constants.isValidPAN
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.example.theemiclub.data.model.loginsignup.verification.AadharVerificationReq
import com.example.theemiclub.data.model.loginsignup.verification.PanVerificationReq
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class ManageKYC : AppCompatActivity() {

    lateinit var binding : ActivityManageKycBinding
    private var mStash: MStash? = null
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    private lateinit var viewModel: TravelViewModel
    private  var state: ArrayList<String> = arrayListOf()
    private  var district: ArrayList<String> = arrayListOf()
    var checkKYCStatus : Boolean = false
    var checkPanVerification : Boolean = false
    lateinit var dialog: Dialog

    companion object{
        var CountryName = ""
        var AadhaarDOB = ""
        var  AadhaarName = ""
        var AadharHouse = ""
        var fullAddress = ""
        var  Aadhardist = ""
        var AadharPin = ""
        var AadharState = ""
        var AadharCountry = ""
        var AadharImage = ""
        var AadhaarResponse = ""
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityManageKycBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mStash = MStash.getInstance(this)

        viewModel = ViewModelProvider(this, TravelViewModelFactory(TravelRepository(RetrofitClient.apiInterfacePAN,null )))[TravelViewModel::class.java]

        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]

        hitApiForState()

        setClickListner()

    }

    override fun onResume() {
        super.onResume()

        if(AadharVerified.equals("yes")){
            with(binding) {

                if(AadhaarName.isNotEmpty()){
                    firstname.setText(AadhaarName)
                }

                if(AadhaarDOB.isNotEmpty()){
                    dob.setText(AadhaarDOB)
                }

                if (state.isNotEmpty()) {
                    val selectedState = AadharState

                    state.forEachIndexed { index, item ->
                        if (item.equals(selectedState, ignoreCase = true)) {
                            statespinner.setSelection(index)
                            return@forEachIndexed
                        }
                    }
                }

                if(AadharPin.isNotEmpty()){
                    pincode.setText(AadharPin)
                }

                aadhaarcardverifiedicon.visibility=View.VISIBLE
                verifyaadhaarcard.visibility=View.GONE


                if(fullAddress.isNotEmpty()){
                    address.setText(fullAddress)
                }

                setDisableField()

            }

        }

        else{
            if(binding.aadhaarnumber.text.isBlank()){
                binding.aadhaarcardverifiedicon.visibility=View.GONE
                binding.verifyaadhaarcard.visibility=View.GONE
                setEnableField()
            }else{
                binding.aadhaarcardverifiedicon.visibility=View.GONE
                binding.verifyaadhaarcard.visibility=View.VISIBLE
                setEnableField()
            }

        }

    }

    fun setDisableField(){
        binding.aadhaarnumber.isEnabled=false
        binding.pannumber.isEnabled=false
        binding.firstname.isEnabled = false
        binding.lastname.isEnabled = false
        binding.dob.isEnabled = false
        binding.address.isEnabled = false
        binding.pincode.isEnabled = false
        binding.statespinner.isEnabled = false
        binding.districtspinner.isEnabled = false

        // set background

        binding.aadhaarlayout.background=this.resources.getDrawable(R.drawable.bg_black_dark_border)
        binding.panlayout.background=this.resources.getDrawable(R.drawable.bg_black_dark_border)
        binding.firstname.background=this.resources.getDrawable(R.drawable.bg_black_dark_border)
        binding.lastname.background=this.resources.getDrawable(R.drawable.bg_black_dark_border)
        binding.address.background=this.resources.getDrawable(R.drawable.bg_black_dark_border)
        binding.pincode.background=this.resources.getDrawable(R.drawable.bg_black_dark_border)
        binding.dob.background=this.resources.getDrawable(R.drawable.bg_black_dark_border)
        binding.gstnumber.background=this.resources.getDrawable(R.drawable.bg_black_dark_border)
        binding.companyName.background=this.resources.getDrawable(R.drawable.bg_black_dark_border)
    }



    fun setEnableField(){
        binding.aadhaarnumber.isEnabled=true
        binding.pannumber.isEnabled=true
        binding.firstname.isEnabled = true
        binding.lastname.isEnabled = true
        binding.dob.isEnabled = true
        binding.address.isEnabled = true
        binding.pincode.isEnabled = true
        binding.statespinner.isEnabled = true
        binding.districtspinner.isEnabled = true

        binding.aadhaarlayout.background=this.resources.getDrawable(R.drawable.bg_black_border)
        binding.panlayout.background=this.resources.getDrawable(R.drawable.bg_black_border)
        binding.firstname.background=this.resources.getDrawable(R.drawable.bg_black_border)
        binding.lastname.background=this.resources.getDrawable(R.drawable.bg_black_border)
        binding.address.background=this.resources.getDrawable(R.drawable.bg_black_border)
        binding.pincode.background=this.resources.getDrawable(R.drawable.bg_black_border)
        binding.dob.background=this.resources.getDrawable(R.drawable.bg_black_border)
        binding.gstnumber.background=this.resources.getDrawable(R.drawable.bg_black_border)
        binding.companyName.background=this.resources.getDrawable(R.drawable.bg_black_border)


        binding.emailid.background=getDrawable(R.drawable.bg_black_border)
        binding.mobilenumber.background=getDrawable(R.drawable.bg_black_border)
        binding.cityname.background=getDrawable(R.drawable.bg_black_border)

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun setClickListner(){
        with(binding){

            back.setOnClickListener {
                AadharVerified=""
                AadhaarName=""
                AadhaarDOB=""
                AadharState=""
                AadharPin=""
                finish()
            }

            dob.setOnClickListener {
                showDatePicker(dob)
            }

            aadhaarnumber.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    val input = s?.toString()?.trim() ?: ""

                    // Check if Aadhaar is valid
                    if(!input.isNullOrBlank()){
                        if (validateAadhaar(input)) {
                            verifyaadhaarcard.visibility = View.VISIBLE

                        } else {
                            verifyaadhaarcard.visibility = View.GONE
                            if(input.length==12) {
                                Toast.makeText(
                                    this@ManageKYC,
                                    "Enter valid aadhaar number",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }

                }

            })

            pannumber.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    val input = s.toString().trim().uppercase()   // convert to uppercase
                        // keep cursor at correct position
                    if(!input.isNullOrBlank()) {
                        if (isValidPAN(input)) {
                            verifypancard.visibility = View.VISIBLE
                        }
                        else {
                            verifypancard.visibility = View.GONE
                            if(input.length==10) {
                                Toast.makeText(this@ManageKYC, "Enter valid pan number", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    else{
                        verifypancard.visibility = View.GONE
                    }
                }
             })

            verifyaadhaarcard.setOnClickListener {
                if(lastname.text.isNotEmpty()){
                    hitApiForAadharVerification()
                }else{
                    Toast.makeText(this@ManageKYC,"Please enter last name",Toast.LENGTH_SHORT).show()
                }

            }

            verifypancard.setOnClickListener {
                if(pannumber.text.isNotEmpty() && AadharVerified.equals("yes")){
                    hitApiForPanVerification(pannumber.text.toString())
                }else{
                    if( AadharVerified.equals("")){
                        Toast.makeText(this@ManageKYC,"Verify aadhaar number first!!",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@ManageKYC,"Enter pan number!!",Toast.LENGTH_SHORT).show()
                    }

                }
            }

            updateBtn.setOnClickListener {
                var emailid = emailid.text.toString().trim()
                var aadhaarnumber = aadhaarnumber.text.toString().trim()
                var pannumber = pannumber.text.toString().trim()

                val (isValid, errorMessage) = isValidForm(emailid,aadhaarnumber,pannumber)

               if(isValid) {
                   OpenPopUpForVAlert()
               }
                else {
                   Toast.makeText(this@ManageKYC, errorMessage, Toast.LENGTH_SHORT).show()
               }

            }

        }

    }


    private fun showDatePicker(dob: TextView)
    {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        var exactAge: Int = 0

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                // Selected date
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDayOfMonth)

                // Format and set DOB
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = sdf.format(selectedCalendar.time)
                dob.text = formattedDate

                // ✅ Age validation
                val today = Calendar.getInstance()
                val age = today.get(Calendar.YEAR) - selectedYear

                // Adjust if birthday hasn't occurred yet this year
                val hasBirthdayPassed = (today.get(Calendar.DAY_OF_YEAR) >= selectedCalendar.get(
                    Calendar.DAY_OF_YEAR))
                exactAge = if (hasBirthdayPassed) age else age - 1



            }, year, month, day
        )


        // Set min age 18
        calendar.add(Calendar.YEAR, -18)
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        // Set max age 65
        val minCalendar = Calendar.getInstance()
        minCalendar.add(Calendar.YEAR, -65)
        datePickerDialog.datePicker.minDate = minCalendar.timeInMillis

        datePickerDialog.show()

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun setuiData(response : RetailerProfileResp){
        with(binding) {
            companyName.setText(response.data!![0]?.agencyName)
            gstnumber.setText(response.data!![0]?.gstno)
            firstname.setText(response.data!![0]?.firstName)
            lastname.setText(response.data!![0]?.lastName)
            aadhaarnumber.setText(response.data!![0]?.addharCardNo)
            pannumber.setText(response.data!![0]?.panCardNumber)
            emailid.setText(response.data!![0]?.emailID)
            mobilenumber.setText(response.data!![0]?.mobileNo)
            address.setText(response.data!![0]?.permanentAddress)

            val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")


            val date = LocalDateTime.parse(response.data!![0]?.dob, inputFormat)
            val formattedDate = date.format(outputFormat)

            dob.setText(formattedDate)

            if (state.isNotEmpty()) {
                val selectedState = response.data!![0]?.state

                state.forEachIndexed { index, item ->
                    if (item.equals(selectedState, ignoreCase = true)) {
                        statespinner.setSelection(index)
                        return@forEachIndexed
                    }
                }
            }

            if (district.isNotEmpty()) {
                val selectedState = response.data!![0]?.district

                district.forEachIndexed { index, item ->
                    if (item.equals(selectedState, ignoreCase = true)) {
                        districtspinner.setSelection(index)
                        return@forEachIndexed
                    }
                }
            }

            cityname.setText(response.data!![0]?.city)

            pincode.setText(response.data!![0]?.pincode)

            if(response.data[0]!!.kycUpdate!!.lowercase().equals("verified",ignoreCase = true)){
                updateBtn.visibility=View.INVISIBLE
                verifyaadhaarcard.visibility= View.GONE
                verifypancard.visibility=View.GONE
                setDisableField()
                gstnumber.isEnabled=false
                emailid.isEnabled=false
                mobilenumber.isEnabled=false
                cityname.isEnabled=false

                binding.emailid.background=getDrawable(R.drawable.bg_black_dark_border)
                binding.mobilenumber.background=getDrawable(R.drawable.bg_black_dark_border)
                binding.cityname.background=getDrawable(R.drawable.bg_black_dark_border)

            }else{
                updateBtn.visibility=View.VISIBLE
                setEnableField()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun GetRetailerDataForEdit(){
        var userCode = mStash!!.getStringValue(Constants.RegistrationId, "").toString()


        runIfConnected {
            val request = RetailerProfileReq(
                fromDate = null,
                parmFlag = "detail",
                agentType = "",
                searchType = "",
                toDate = null,
                adminid = "",
                searchValue = "",
                userID = userCode,
            )

            Log.d("GetProfileReq", Gson().toJson(request))

            getAllApiServiceViewModel.retailerprofileKycReq(request).observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    Log.d("GetProfileResponse", Gson().toJson(response))
                                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                                        Constants.dialog.dismiss()
                                    }
                                    if (response.isSuccess!!) {

                                        setuiData(response)
                                    }
                                    else {
                                        Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            if(Constants.dialog!=null && Constants.dialog.isShowing){
                                Constants.dialog.dismiss()
                            }
                        }

                        ApiStatus.LOADING -> {
                            Constants.OpenPopUpForVeryfyOTP(this)
                        }

                    }
                }
            }

        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun hitApiForState(){
        var request = CountryStateDistrictReq(
            pParmFlag = "state",
            pParmFlag1 =CountryName,
            pParmFlag2 = ""
        )
        Log.d("countryName", CountryName)

        getAllApiServiceViewModel.countryStateDistrictListReq(request).observe(this){
            resources-> resources.let {
                when(it.apiStatus){

                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("getstatelist", Gson().toJson(response))
                                if (response.isSuccess!!) {
                                    state.clear()
                                    var getdata = response.data
                                    getdata!!.forEach { it->
                                       state.add(it!!.displayText!!)
                                    }
                                    if(state.isNotEmpty() && state.size== getdata.size){
                                        setStateList()
                                    }

                                }
                                else {
                                    GetRetailerDataForEdit()
                                    Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                    }

                    ApiStatus.ERROR -> {
                        GetRetailerDataForEdit()
                    }

                    ApiStatus.LOADING -> {

                    }
                }
        }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun setStateList(){
        val genderadapters = ArrayAdapter(this@ManageKYC, R.layout.spinner_right_aligned, state)
        genderadapters.setDropDownViewResource(R.layout.spinner_right_aligned)
        binding.statespinner.adapter = genderadapters



        binding.statespinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    Log.d("Spinner", "Item selected at position $position")

                       var state = parent!!.getItemAtPosition(position).toString()
                       hitApiForDistrict(state)

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }


        GetRetailerDataForEdit()

    }


    fun setDistrictList(){
        val genderadapters = ArrayAdapter(this@ManageKYC, R.layout.spinner_right_aligned, district)
        genderadapters.setDropDownViewResource(R.layout.spinner_right_aligned)
        binding.districtspinner.adapter = genderadapters
        if(Aadhardist.isNotEmpty()){
            district.forEachIndexed { index, item ->
                if (item.equals(Aadhardist, ignoreCase = true)) {
                    binding.districtspinner.setSelection(index)
                    return@forEachIndexed
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun hitApiForDistrict(stateName:String){
        var request = CountryStateDistrictReq(
            pParmFlag = "district",
            pParmFlag1 =stateName,
            pParmFlag2 = ""
        )
        Log.d("countryName", stateName)

        getAllApiServiceViewModel.countryStateDistrictListReq(request).observe(this){
                resources-> resources.let {
            when(it.apiStatus){

                ApiStatus.SUCCESS -> {
                    it.data?.let { users ->
                        users.body()?.let { response ->
                            Log.d("getdistrictlist", Gson().toJson(response))
                            if (response.isSuccess!!) {
                                district.clear()
                                var getdata = response.data
                                getdata!!.forEach { it->
                                    district.add(it!!.displayText!!)
                                }
                                if(district.isNotEmpty() && district.size== getdata.size){
                                    setDistrictList()
                                }

                                GetRetailerDataForEdit()

                            }
                            else {
                               // Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }

                ApiStatus.ERROR -> {
                    GetRetailerDataForEdit()
                }

                ApiStatus.LOADING -> {

                }
            }
        }
        }
    }


    fun hitApiForAadharVerification() {
        val firstName = binding.firstname.text.toString()
        val lastName = binding.lastname.text.toString()
        val emailId = binding.emailid.text.toString()
        val mob = binding.mobilenumber.text.toString()

        var aadharverificationreq = AadharVerificationReq(
            firstName = firstName,
            lastName = lastName,
            mobileNumber = mob,
            emailId = emailId,
            registrationId = Constants.PAN_VERIFICATION_REGISTRATION_ID,
        )

        Log.d("AadharVerificationreq", Gson().toJson(aadharverificationreq))

        viewModel.getAadharVerificationRequest(aadharverificationreq).observe(this) { resources ->
            resources.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data.let { users ->
                            users!!.body().let { response ->
                                Constants.dialog.dismiss()
                                Log.d("AadharVerificationResp", Gson().toJson(response))

                                if (response!!.code == null) {
                                    Toast.makeText(this@ManageKYC, response.message, Toast.LENGTH_SHORT).show()
                                }
                                if (response!!.code.equals("200")) {
                                      digilockerLink = response!!.model.kycUrl
                                      AadharTransactionIdNo = response.model.transactionId
                                    Log.d("digilockeurl", digilockerLink)
                                    startActivity(Intent(this@ManageKYC, AadharCardWebViewDIGILockerPage::class.java))
                                }
                                else {
                                    Toast.makeText(this@ManageKYC, response.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        Constants.dialog.dismiss()
                    }

                    ApiStatus.LOADING -> {
                        Constants.OpenPopUpForVeryfyOTP(this)
                    }
                }
            }
        }

    }


    fun hitApiForPanVerification(pannumber: String) {
        var panverificationreq = PanVerificationReq(
            panNumber = pannumber,
            registrationId = Constants.PAN_VERIFICATION_REGISTRATION_ID,
        )
        Log.d("PanVerificationreq", Gson().toJson(panverificationreq))

        viewModel.getPanVerificationReq(panverificationreq).observe(this) { resources ->
            resources.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data.let { users ->
                            users!!.body().let { response ->
                                Constants.dialog.dismiss()
                                Log.d("PanVerificationResp", Gson().toJson(response))

                                if (response!!.httpResponseCode == 203) {
                                    Toast.makeText(this@ManageKYC, "Please enter valid pan number!!", Toast.LENGTH_SHORT).show()
                                }
                                if (response!!.httpResponseCode == 205) {
                                    Toast.makeText(this@ManageKYC, "Please enter valid pan number!!", Toast.LENGTH_SHORT).show()
                                }

                                if (response!!.httpResponseCode == 0) {
                                    Toast.makeText(this@ManageKYC, response.message, Toast.LENGTH_SHORT).show()
                                }

                                if (response.status.equals("True", ignoreCase = true)) {
                                    binding.aadhaarnumber.setText(response.result!!.aadhaarNumber!!)
                                    if(response.result!!.email!!.isNotEmpty()){
                                        binding.emailid.setText(response.result!!.email!!)
                                    }

                                    if(response.result!!.mobile!!.isNotEmpty()){
                                        binding.mobilenumber.setText(response.result!!.mobile!!)
                                    }

                                    var  DOB = response.result!!.dob!! // 10/07/1997 dd/mm/yyyy
                                    val input = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    val output = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    val parsedDate = input.parse(DOB)
                                    val formattedDate = output.format(parsedDate!!)
                                    if (!formattedDate.equals(AadhaarDOB)) {
                                        checkKYCStatus=false
                                        binding.updateBtn.text = "UPDATE"
                                        binding.txtmsgpandobnotmatch.visibility=View.VISIBLE
                                        Toast.makeText(this@ManageKYC,"DOB does not match",Toast.LENGTH_SHORT).show()
                                    }
                                    else {
                                        Log.d("lastname",response.result!!.lastName!!)
                                        checkKYCStatus=true
                                        binding.updateBtn.text = "UPDATE KYC"
                                        binding.txtmsgpandobnotmatch.visibility=View.GONE
                                    }

                                    binding.pannumber.isEnabled = false
                                    binding.pancardverifiedicon.visibility=View.VISIBLE
                                    binding.verifypancard.visibility = View.GONE

                                }
                                else {
                                    binding.pannumber.isEnabled = true
                                    binding.pancardverifiedicon.visibility=View.GONE
                                    binding.verifypancard.visibility = View.VISIBLE
                                    checkKYCStatus=false
                                    binding.updateBtn.text = "UPDATE"
                                    Toast.makeText(this@ManageKYC, "Please enter valid pan number!!", Toast.LENGTH_SHORT).show()
                                }

                            }

                        }

                    }

                    ApiStatus.ERROR -> {
                        Constants.dialog.dismiss()
                    }

                    ApiStatus.LOADING -> {
                         Constants.OpenPopUpForVeryfyOTP(this)
                    }

                }

            }

        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun GetRetailerProfileKYCUpdate(KYCStatus: String){
        var userCode = mStash!!.getStringValue(Constants.RegistrationId, "").toString()
        var dob = binding.dob.text.toString().trim()

        runIfConnected {
            val request = UpdateKycReq(
                firstName = binding.firstname.text.toString(),
                lastName = binding.lastname.text.toString(),
                pincode = binding.pincode.text.toString(),
                city = binding.cityname.text.toString(),
                gstno = binding.gstnumber.text.toString(),
                emailID = binding.emailid.text.toString(),
                mobileNo = binding.mobilenumber.text.toString(),
                userID = userCode,
                alternateMobileNo = "",
                paramUser = userCode,
                updatekycStatus = KYCStatus,
                addharCardNo = binding.aadhaarnumber.text.toString(),
                panCardNumber = binding.pannumber.text.toString(),
                dob = dob,
                district = binding.districtspinner.selectedItem.toString().trim(),
                permanentAddress = binding.address.text.toString().trim(),
                state = binding.statespinner.selectedItem.toString().trim(),
            )

            Log.d("GetProfileKYCReq", Gson().toJson(request))

            getAllApiServiceViewModel.UpdateKycReq(request).observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    Log.d("GetProfileKYCResponse", Gson().toJson(response))
                                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                                        Constants.dialog.dismiss()
                                    }
                                    if (response.isSuccess!!) {
                                        mStash!!.clear()
                                        TravelCard =false
                                        FinanceCard =false
                                        BillRechargeCard =false
                                        startActivity(Intent(this, LoginActivity::class.java))
                                        finish()
                                        Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
                                    }
                                    else {
                                        Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            if(Constants.dialog!=null && Constants.dialog.isShowing){
                                Constants.dialog.dismiss()
                            }
                        }

                        ApiStatus.LOADING -> {
                            Constants.OpenPopUpForVeryfyOTP(this)
                        }

                    }
                }
            }

        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    fun OpenPopUpForVAlert() {
        dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.signoutalert)


        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }


        dialog.setCanceledOnTouchOutside(false)

        val cancel = dialog.findViewById<Button>(R.id.btnCancel)
        val done = dialog.findViewById<Button>(R.id.btnLogout)
        val txt = dialog.findViewById<TextView>(R.id.dialog_message)
        val image = dialog.findViewById<ImageView>(R.id.imageview)

        image.visibility = View.VISIBLE

        txt.text = "If you update your profile, you will be logged out of the app"

        done.setOnClickListener {
            if(checkKYCStatus){
                GetRetailerProfileKYCUpdate("Verified")
            }

            else{
                GetRetailerProfileKYCUpdate("Pending")
            }

        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }


    fun isValidForm(
        emailID: String,
        aadharNumber: String,
        panNumber: String): Pair<Boolean, String?> {


        if (!emailID.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")))
            return Pair(false, "Enter valid email address")

        if (aadharNumber.isBlank() ||!validateAadhaar(aadharNumber)) {
            return Pair(false, "Enter a valid 12-digit Aadhaar number")
        }

        val panRegex = Regex("[A-Z]{5}[0-9]{4}[A-Z]{1}")

        if (panNumber.isBlank() || !panRegex.matches(panNumber.uppercase())) {
            return Pair(false, "Enter a valid PAN number (e.g., ABCDE1234F)")
        }

        return Pair(true, null)
    }

}


fun validateAadhaar(input: String): Boolean {

    val fullAadhaarRegex = Regex("^\\d{12}$")  // Full Aadhaar 12 digits
    val maskedAadhaarRegex = Regex("^(x{8}|X{8}|\\*{8})([- ]?)(\\d{4})$") // Masked Aadhaar

    return when {
        fullAadhaarRegex.matches(input) -> true        // Full Aadhaar valid
        maskedAadhaarRegex.matches(input) -> true      // Masked Aadhaar valid
        else -> false                                   // Invalid format
    }
}

