package com.bos.payment.appName.ui.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bos.bos.app.ui.view.DeviceInfoHelper
import com.bos.payment.appName.data.model.justpaymodel.CheckBankDetailsModel
import com.bos.payment.appName.data.model.justpedashboard.RetailerWiseServicesRequest
import com.bos.payment.appName.data.model.loginSignUp.LoginRes
import com.bos.payment.appName.data.model.merchant.activeInActiveStatus.GetAPIActiveInactiveStatusReq
import com.bos.payment.appName.data.model.merchant.activeInActiveStatus.GetAPIActiveInactiveStatusRes
import com.bos.payment.appName.data.model.merchant.merchantList.GetApiListMarchentWiseReq
import com.bos.payment.appName.data.model.merchant.merchantList.GetApiListMarchentWiseRes
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.repository.LoginSignUpRepository
import com.bos.payment.appName.data.repository.MoneyTransferRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.data.viewModelFactory.LoginSignUpViewModelFactory
import com.bos.payment.appName.data.viewModelFactory.MoneyTransferViewModelFactory
import com.bos.payment.appName.databinding.ActivityLoginBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.Dashboard.activity.DashboardActivity
import com.bos.payment.appName.ui.view.Dashboard.activity.JustPeDashboard
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.ui.viewmodel.LoginSignUpViewModel
import com.bos.payment.appName.ui.viewmodel.MoneyTransferViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Constants.BillRechargeCard
import com.bos.payment.appName.utils.Constants.FinanceCard
import com.bos.payment.appName.utils.Constants.RETAILERALLSERVICES
import com.bos.payment.appName.utils.Constants.TravelCard
import com.bos.payment.appName.utils.Constants.getRetailerAllServices
import com.bos.payment.appName.utils.MStash
import com.example.example.LoginReq
import com.bos.payment.appName.utils.Utils.PD
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.bos.payment.appName.utils.Utils.toast
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val context: Context = this@LoginActivity
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var viewModel: LoginSignUpViewModel
    private lateinit var viewModel1: MoneyTransferViewModel
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    private lateinit var pd: AlertDialog
    private var loginText: String? = ""
    private var mStash: MStash? = null
    private var requestOption: RequestOptions? = null



    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!checkLocationPermission()) {
            requestLocationPermission()
        }

        initView()

        if (!mStash!!.getBoolanValue(Constants.isUpdate.toString(), false)) {
            showConsentDialog()
            showBiometricDialog()
        }
//      setDropDown()
        btnListener()

    }

    //    private fun setDropDown() {
//        val arrayListSpinner = resources.getStringArray(R.array.login_array)
//        val adapters = ArrayAdapter(
//            this@LoginActivity,
//            R.layout.spinner_right_aligned,
//            arrayListSpinner
//        )
//        adapters.setDropDownViewResource(R.layout.spinner_right_aligned)
//        binding.loginLayout.spinnerLoginType.adapter = adapters
//        binding.loginLayout.spinnerLoginType.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    loginText = if (position > 0) {
//                        parent!!.getItemAtPosition(position).toString()
//                    } else {
//                        null
//                    }
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {}
//            }
//        binding.loginLayout.spinnerLoginType.setSelection(0)
//    }
    private fun showBiometricDialog() {
        AlertDialog.Builder(this)
            .setTitle("Need your permission")
            .setMessage("LogIn using your screen lock biometric credential")
            .setPositiveButton("Allow") { dialog, which ->
                mStash!!.setBooleanValue(Constants.isUpdate.toString(), true)
                Log.d("setBooleanValue",mStash!!.getBoolanValue(Constants.isUpdate.toString(), true).toString())
            }
            .setNegativeButton("Deny") { dialog, which ->
                toast("You denied the biometric lock")
                mStash!!.setBooleanValue(Constants.isUpdate.toString(), false)

            }
            .setCancelable(false)
            .show()
    }

    private fun showConsentDialog() {
        AlertDialog.Builder(this)
            .setTitle("Need your permission")
            .setMessage(
                "We collect your device information (such as your IP address) to enhance the app's functionality. " +
                        "Your data will be securely stored and not shared with third parties."
            )
            .setPositiveButton("Allow") { dialog, which ->
                // User has given consent, proceed with data collection
                val deviceInfoHelper = DeviceInfoHelper(this)

                // Get the IP Address
                val ipAddress = deviceInfoHelper.getIpAddress()
                mStash!!.setStringValue(Constants.deviceIPAddress, ipAddress)
                mStash!!.setBooleanValue(Constants.isUpdate.toString(), true)

                Log.d(
                    "MainActivity",
                    "IP Address: " + mStash!!.getStringValue(Constants.deviceIPAddress, "")
                )
            }
            .setNegativeButton("Deny") { dialog, which ->
                mStash!!.setBooleanValue(Constants.isUpdate.toString(), false)
                startActivity(Intent(this, SplashActivity::class.java))
//                finish()
                // User denied consent, handle this appropriately
                toast("You denied the consent")
            }
            .setCancelable(false)
            .show()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun btnListener() {
//        binding.rememberId.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                // Condition when checkbox is checked
//                binding.loginId.setText(mStash!!.getStringValue(Constants.loginId, ""))
//                binding.tvPassword.setText(mStash!!.getStringValue(Constants.Password, ""))
////                toast("Checkbox is checked")
//                // Add any specific action you want here
//            } else {
//                binding.loginId.setText("")
//                binding.tvPassword.setText("")
//                // Condition when checkbox is unchecked
//                toast("Checkbox is unchecked")
//                // Add any specific action you want here
//            }
//        }

        binding.loginLayout.tvForgotPassword.setOnClickListener {
            startActivity(Intent(context, ForgotPasswordActivity::class.java))
        }
        binding.loginLayout.tvBtnSignUp.setOnClickListener {
            startActivity(Intent(context, SignUpActivity::class.java))
        }
        binding.loginLayout.tvBtnLogin.setOnClickListener {
            validation()

//            if (binding.loginId.text.toString().contains("RTE") && loginText == "Retailer"){
//                validation()
//            } else if (binding.loginId.text.toString().contains("CUS") && loginText == "Customer"){
//                validation()
//            } else {
//                toast("Login credential not found")
//            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initView() {
        pd = PD(this)
        mStash = MStash.getInstance(context)

        mStash!!.getStringValue(Constants.CompanyLogo, "")?.let { Log.d("merchantIdList", it) }
        requestOption = RequestOptions()
        Constants.merchantIdList = ArrayList()

        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(
            GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)
        )
        )[GetAllApiServiceViewModel::class.java]


        viewModel = ViewModelProvider(this, LoginSignUpViewModelFactory(LoginSignUpRepository(RetrofitClient.apiAllInterface)))[LoginSignUpViewModel::class.java]

        viewModel1 = ViewModelProvider(this, MoneyTransferViewModelFactory(MoneyTransferRepository(RetrofitClient.apiAllInterface)))[MoneyTransferViewModel::class.java]

//        try {
//
//            val imageUrl = mStash!!.getStringValue(Constants.CompanyLogo, "")
//            if (imageUrl != null) {
//
//                Log.d("MoneyTransferViewModel", Gson().toJson(imageUrl))
//
//                Picasso.get()
//                    .load(imageUrl)
////            .placeholder(R.drawable.placeholder)  // Optional: placeholder while loading
//                    .error(R.drawable.no_image)        // Optional: error image if load fails
//                    .into(binding.loginLayout.imageView3)
//            } else {
//                Picasso.get()
//                    .load(R.drawable.no_image)
////            .placeholder(R.drawable.placeholder)  // Optional: placeholder while loading
//                    .error(R.drawable.no_image)        // Optional: error image if load fails
//                    .into(binding.loginLayout.imageView3)
//            }
//        } catch (e: IllegalArgumentException) {
//            e.printStackTrace()
//        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun validation() {
        val loginId = binding.loginLayout.loginId.text.toString().uppercase()
        val password = binding.loginLayout.tvPassword.text.toString()
        Log.d("validation", loginId)
        when {
//            loginText.isNullOrEmpty() -> {
//                toast("Please select the login Type")
//            }
            loginId.isEmpty() || loginId.length < 2 -> {
                toast("Please enter valid LoginID")
            }

            password.isEmpty() || password.length < 4 -> {
                toast("Please enter valid Password")
            }
//            loginId.contains("RTE") && loginText == "Retailer" -> {
            loginId.contains("BOS") -> {
                loginCall()
            }

            loginId.contains("CUS") && loginText == "Customer" -> {
                loginCall()
            }

            else -> {
                toast("Invalid login Id or type")
            }
        }
    }

    private fun loginCall() {
        this.runIfConnected {
            val loginReq = LoginReq(
                userID = binding.loginLayout.loginId.text.toString().trim().uppercase(),
                agentPassword = binding.loginLayout.tvPassword.text.toString()
                    .trim() // Pass encrypted password
            )
            Log.d("loginCall", Gson().toJson(loginReq))
            viewModel.login(loginReq).observe(this) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                it.data?.let { users ->
                                    pd.dismiss()
                                    users.body()?.let { it1 -> loginRes(it1) }
                                }
                            }

                            ApiStatus.ERROR -> {
                                pd.dismiss()
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            ApiStatus.LOADING -> {
                                pd.show()
                            }
                        }
                    }
                }
            }
       }


    private fun loginRes(res: LoginRes) {
        if (res.isSuccess == true) {
            Log.d("LoginDataResponse",Gson().toJson(res))
            if(res.data[0].agentType.equals("Admin")){
                Toast.makeText(this@LoginActivity,"Invalid login or password",Toast.LENGTH_SHORT).show()
            }
            else{
                getAllMerchantList(res.data[0].merchantCode.toString())
                mStash!!.setStringValue(Constants.RegistrationId, res.data[0].userID.toString().uppercase())
                mStash!!.setStringValue(Constants.MobileNumber, res.data[0].mobileNo.toString())
                mStash!!.setStringValue(Constants.AdminCode, res.data[0].adminCode.toString())
                mStash!!.setStringValue(Constants.mailid, res.data[0].emailId.toString())
                mStash!!.setStringValue(Constants.applicationtype, res.data[0].applicationType.toString())
                mStash!!.setBooleanValue(Constants.IS_LOGIN, true)
                mStash!!.setStringValue(Constants.AgentName, res.data[0].agencyname.toString())
                mStash!!.setStringValue(Constants.AgentType, res.data[0].agentType.toString())
                mStash!!.setBooleanValue(Constants.IS_FIRST_LAUNCH.toString(), false)
                mStash!!.setBooleanValue(Constants.Status, res.isSuccess!!)
                mStash!!.setStringValue(Constants.Password, binding.loginLayout.tvPassword.text.toString().trim())
                mStash!!.setStringValue(Constants.MerchantId, res.data[0].merchantCode.toString())
                mStash!!.setStringValue(Constants.retailerName, res.data[0].fullName.toString())
                toast("Login Successful")
                Log.d("getAllMerchantList", res.data[0].merchantCode.toString())
                Log.d("loginId",res.data[0].userID.toString().uppercase())
                Log.d("mobileno",res.data[0].mobileNo.toString())
                getIntentOnDashboard()
            }

        }
        else {
            toast("Please correct login detail")
        }
    }



    fun getIntentOnDashboard(){
        startActivity(Intent(context, JustPeDashboard::class.java))
        // startActivity(Intent(context, DashboardActivity::class.java))
        finish()
    }



    private fun getAllMerchantList(merchantId: String)
    {
        val getAllMerchantList = GetApiListMarchentWiseReq(MarchentID = merchantId)
        Log.d("getAllMerchantList", Gson().toJson(getAllMerchantList))
        viewModel1.getAllMerchantList(getAllMerchantList).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        pd.dismiss()
                        it.data?.let { users ->
                            users.body()?.let { it1 -> getAllMerchantListRes(it1, merchantId) }
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

    private fun getAllMerchantListRes(response: GetApiListMarchentWiseRes, merchantId: String) {
        if (response.isSuccess == true) {
            response.data.forEach { item ->
                Constants.merchantIdList!!.add(item.featureCode!!.trim())
                mStash!!.setStringValue(Constants.APIName, item.featureName.toString())
                mStash!!.setStringValue(Constants.MerchantList, Constants.merchantIdList.toString())
                Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()

//                Log.d("APINameList", mStash!!.getStringValue(Constants.APIName, "").toString())

            }

//            toast(response[0].message.toString())
//            getAllAPIRetailerWiseActiveInActiveStatus()
//            Log.d("merchantList", mStash!!.getStringValue(Constants.MerchantList, "").toString())
//            Log.d("merchantList11", Constants.merchantIdList.toString())

//            startActivity(Intent(this, FingerprintActivity::class.java))

//            if (mStash!!.getBoolanValue(Constants.isUpdate.toString(), true)) {
//                // Enable the switch button and set it to checked
////                binding.nav.switchButton.isEnabled = true
////                binding.nav.switchButton.isChecked = true
//                startActivity(Intent(this, FingerprintActivity::class.java))
//                toast("Switch enabled: ${mStash!!.getBoolanValue(Constants.isUpdate.toString(), true)}")
//            } else {
//                // Disable the switch button and set it to unchecked
////                binding.nav.switchButton.isEnabled = false
////                binding.nav.switchButton.isChecked = false
//                toast("Switch disabled: ${mStash!!.getBoolanValue(Constants.isUpdate.toString(), false)}")
//            }

//            startActivity(Intent(context, DashboardActivity::class.java))
//            finish()
        } else {
            toast(response.returnMessage.toString())
        }
    }

    private fun getAllAPIRetailerWiseActiveInActiveStatus() {
        val getAPIActiveInactiveStatusReq = GetAPIActiveInactiveStatusReq(
            RegistrationId = mStash!!.getStringValue(Constants.RegistrationId, ""),
            CompanyCode = mStash!!.getStringValue(Constants.CompanyCode, "")
        )
        Log.d("getAPIActiveInactive", Gson().toJson(getAPIActiveInactiveStatusReq))
        viewModel1.getAllAPIRetailerWiseActiveInActive(getAPIActiveInactiveStatusReq)
            .observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            pd.dismiss()
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    getAllAPIRetailerWiseActiveInActiveStatusRes(response)
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




    private fun getAllAPIRetailerWiseActiveInActiveStatusRes(response: GetAPIActiveInactiveStatusRes) {
        if (response.Status == true) {
            mStash!!.setStringValue(
                Constants.RechargeAPI_Status,
                response.RechargeAPIStatus.toString()
            )
            mStash!!.setStringValue(
                Constants.RechargeAPI_2_Status,
                response.RechargeAPI2Status.toString()
            )
            mStash!!.setStringValue(
                Constants.MoneyTransferAPI_Status,
                response.MoneyTransferAPIStatus.toString()
            )
            mStash!!.setStringValue(
                Constants.MoneyTransferAPI_2_Status,
                response.MoneyTransferAPI2Status.toString()
            )
            mStash!!.setStringValue(
                Constants.Payout_API_Status,
                response.PayoutAPIStatus.toString()
            )
            mStash!!.setStringValue(
                Constants.Payout_API_2_Status,
                response.PayoutAPI2Status.toString()
            )
            mStash!!.setStringValue(Constants.Payin_API_Status, response.PayinAPIStatus.toString())
            mStash!!.setStringValue(
                Constants.Payin_API_2_Status,
                response.PayinAPI2Status.toString()
            )
            mStash!!.setStringValue(
                Constants.Fastag_API_Status,
                response.FastagAPIStatus.toString()
            )
            mStash!!.setStringValue(
                Constants.PANCardAPI_Status,
                response.PANCardAPIStatus.toString()
            )
            mStash!!.setStringValue(Constants.AEPS_API_Status, response.AEPSAPIStatus.toString())
            mStash!!.setStringValue(
                Constants.CreditCardAPI_Status,
                response.CreditCardAPIStatus.toString()
            )

//            toast("Login Successful")
//            startActivity(Intent(context, DashboardActivity::class.java))
//            finish()
        } else {
            toast(response.message.toString())
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now access the location
            } else {
                // Permission denied, handle accordingly (e.g., show an error message or disable location functionality)
            }
        }
    }


}
