package com.bos.payment.appName.ui.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.R
import com.bos.payment.appName.data.repository.LoginSignUpRepository
import com.bos.payment.appName.data.viewModelFactory.LoginSignUpViewModelFactory
import com.bos.payment.appName.databinding.ActivitySplashBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.Dashboard.activity.DashboardActivity
import com.bos.payment.appName.ui.view.Dashboard.activity.JustPeDashboard
import com.bos.payment.appName.ui.viewmodel.LoginSignUpViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.bos.payment.appName.utils.Utils.toast
import com.example.example.LoginReq
import com.squareup.picasso.Picasso

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var mStash: MStash
    private lateinit var viewModel: LoginSignUpViewModel
    private lateinit var pd: AlertDialog

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding before calling setContentView
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        btnListener()
    }

    private fun splashCall() {
        this.runIfConnected {

            val loginRequest = LoginReq(
                userID = mStash.getStringValue(Constants.RegistrationId, ""),
                agentPassword = mStash.getStringValue(Constants.Password, "")
            )
            Log.d(TAG, "initView: $loginRequest")

            viewModel.login(loginRequest).observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            Log.d(TAG, "initView: SUCCESS")

                            if (mStash.getBoolanValue(Constants.IS_FIRST_LAUNCH.toString(), true)) {
//                            startActivity(Intent(this, IdentifyClient::class.java))
                                startActivity(Intent(this, LoginActivity::class.java))

                            } else {
                                // Regular flow: Check if user credentials are stored
                                val isLoggedIn = mStash.getBoolanValue(Constants.IS_LOGIN, false)
                                val useFingerprint = mStash.getBoolanValue(Constants.fingerPrintAction.toString(), false)

                                if (isLoggedIn || mStash.getBoolanValue(Constants.isUpdate.toString(), true)) {
                                    // User is logged in, proceed to the dashboard or fingerprint screen
                                    val targetActivity = if (useFingerprint) { FingerprintActivity::class.java }
                                    else {
                                        //DashboardActivity::class.java
                                        JustPeDashboard::class.java
                                    }
                                    startActivity(Intent(this, targetActivity))
                                    finish()
                                } else {
                                    // If not logged in, open LoginActivity
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish()
                                }
                            }
//                        }else if (mStash.getBoolanValue(Constants.IS_LOGIN.toString(), false)){
//                            startActivity(Intent(this, DashboardActivity::class.java))
//                        }else if (mStash.getBoolanValue(Constants.fingerPrintAction.toString(), false)){
//                            startActivity(Intent(this, FingerprintActivity::class.java))
//                        }else {
//                            startActivity(Intent(this, LoginActivity::class.java))
//                        }

//                        val status = mStash.getBoolanValue(Constants.Status, false)
//                        Log.d(TAG, "initView: $status")
//                        Log.d(TAG, "fingerPrintAction: ${ mStash.getBoolanValue(Constants.fingerPrintAction.toString(), false)}")
//
//                        val targetActivity = when {
//                            mStash.getBoolanValue(Constants.fingerPrintAction.toString(), false) -> FingerprintActivity::class.java
//                            status -> DashboardActivity::class.java
//                            else -> {
//                                Log.d(TAG, "initView: LoginActivity")
//                                IdentifyClient::class.java
//                            }
//                        }
//
//                        startActivity(Intent(this, targetActivity))
//                        finish()
                        }

                        ApiStatus.ERROR -> {
                            if (mStash.getBoolanValue(Constants.IS_FIRST_LAUNCH.toString(), true)) {
                                startActivity(Intent(this, IdentifyClient::class.java))
                            } else {
                                startActivity(Intent(this, LoginActivity::class.java))
                            }

                            toast("Something went wrong!")
                        }

                        ApiStatus.LOADING -> {
                            // Handle loading state if needed
                        }
                    }
                }
            }

        }
    }

    private fun btnListener() {
        binding.tvBtnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.tvBtnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        // Initialize MStash instance
        mStash = MStash.getInstance(this@SplashActivity)!!
        pd = Utils.PD(this)

        binding.tvBtnLogin.visibility = View.GONE
        binding.tvBtnSignUp.visibility = View.GONE

        binding.textView2.text = "Happy To See You "+ mStash.getStringValue(Constants.CompanyName, "")

        // Initialize ViewModel
        viewModel = ViewModelProvider(this, LoginSignUpViewModelFactory(LoginSignUpRepository(RetrofitClient.apiAllInterface)))[LoginSignUpViewModel::class.java]

        try {
            val imageUrl = mStash.getStringValue(Constants.CompanyLogo, "")

            Picasso.get().load(imageUrl)
//            .placeholder(R.drawable.placeholder)  // Optional: placeholder while loading
                .error(R.drawable.ic_error)        // Optional: error image if load fails
                .into(binding.textView1)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        // Delay and call splashCall after 1 second
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d(TAG, "initView: Handler")
            splashCall()
        }, 1000)
    }
}

//
//package com.bos.bos.app.ui.view
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.view.View
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.ViewModelProvider
//import com.bos.bos.app.data.repository.LoginSignUpRepository
//import com.bos.bos.app.data.viewModelFactory.LoginSignUpViewModelFactory
//import com.bos.bos.app.databinding.ActivitySplashBinding
//import com.bos.bos.app.network.RetrofitClient
//import com.bos.bos.app.ui.view.Dashboard.DashboardActivity
//import com.bos.bos.app.ui.viewmodel.LoginSignUpViewModel
//import com.bos.bos.app.utils.Constants
//import com.bos.bos.app.utils.MStash
//import com.bos.bos.app.utils.Utils
//
//@SuppressLint("CustomSplashScreen")
//class SplashActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivitySplashBinding
//    private lateinit var mStash: MStash
//    private lateinit var viewModel: LoginSignUpViewModel
//    private lateinit var pd: AlertDialog
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Initialize binding before calling setContentView
//        binding = ActivitySplashBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        initView()
//        btnListener()
//    }
//
//    private fun splashCall() {
//        // Check if it's the first launch
//        val isFirstLaunch = mStash.getBoolanValue(Constants.IS_FIRST_LAUNCH.toString(), true)
//
//        if (mStash.getBoolanValue(Constants.IS_FIRST_LAUNCH.toString(), true)) {
//            // First launch, open IdentifyClient and update the flag
//            startActivity(Intent(this, IdentifyClient::class.java))
//            mStash.setBooleanValue(
//                Constants.IS_FIRST_LAUNCH.toString(),
//                false
//            )  // Set to false after first launch
//            finish()
//        } else {
//            // Regular flow: Check if user credentials are stored
////            val isLoggedIn = mStash.getBoolanValue(Constants.IS_LOGIN, false)
//            val useFingerprint =
//                mStash.getBoolanValue(Constants.fingerPrintAction.toString(), false)
//
//            if (mStash.getBoolanValue(Constants.IS_LOGIN.toString(), false) && !intent.getBooleanExtra("UpdateProfile", false)) {
//                // User is logged in, proceed to the dashboard or fingerprint screen
//                val targetActivity = if (useFingerprint) {
//                    FingerprintActivity::class.java
//                } else {
//                    DashboardActivity::class.java
//                }
//                startActivity(Intent(this, targetActivity))
//                finish()
//            } else {
//                // If not logged in, open LoginActivity
//                startActivity(Intent(this, LoginActivity::class.java))
//                finish()
//            }
//        }
//    }
//
//    private fun btnListener() {
//        binding.tvBtnLogin.setOnClickListener {
//            startActivity(Intent(this, LoginActivity::class.java))
//        }
//        binding.tvBtnSignUp.setOnClickListener {
//            startActivity(Intent(this, SignUpActivity::class.java))
//        }
//    }
//
//    private fun initView() {
//        // Initialize MStash instance
//        mStash = MStash.getInstance(this@SplashActivity)!!
//        pd = Utils.PD(this)
//
//        binding.tvBtnLogin.visibility = View.GONE
//        binding.tvBtnSignUp.visibility = View.GONE
//
//        // Initialize ViewModel
//        viewModel = ViewModelProvider(
//            this,
//            LoginSignUpViewModelFactory(LoginSignUpRepository(RetrofitClient.apiAllInterface))
//        )[LoginSignUpViewModel::class.java]
//
//        // Delay and call splashCall after 1 second
//        Handler(Looper.getMainLooper()).postDelayed({
//            splashCall()
//        }, 1000)
//    }
//}



