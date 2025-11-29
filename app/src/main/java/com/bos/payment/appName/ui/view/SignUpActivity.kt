package com.bos.payment.appName.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.data.model.loginSignUp.signUp.SignUpReq
import com.bos.payment.appName.data.model.loginSignUp.signUp.SignUpRes
import com.bos.payment.appName.data.repository.LoginSignUpRepository
import com.bos.payment.appName.data.viewModelFactory.LoginSignUpViewModelFactory
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.viewmodel.LoginSignUpViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Utils.toast
import com.bos.payment.appName.R
import com.bos.payment.appName.databinding.ActivitySignUpBinding
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.PD
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.google.gson.Gson

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val context: Context = this@SignUpActivity
    private var agentType: String? = null
    private var applicationModeTxt: String? = null
    private lateinit var viewModel: LoginSignUpViewModel
    private lateinit var pd: AlertDialog
    private var mStash: MStash? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        btnListener()
        dropDown()
    }

    private fun dropDown() {
        val referenceListSpinner = arrayListOf("Select Application mode", "Banking", "Travel")
        val referenceAdapter =
            ArrayAdapter(context, R.layout.spinner_right_aligned, referenceListSpinner)
        referenceAdapter.setDropDownViewResource(R.layout.spinner_right_aligned)
        binding.signUpLayout.spinnerReferralCode.adapter = referenceAdapter

        binding.signUpLayout.spinnerReferralCode.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                @SuppressLint("SetTextI18n")
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position > 0) {
                        applicationModeTxt = parent!!.getItemAtPosition(position).toString()
                        toast(applicationModeTxt.toString())
                    }
                    else {
                        applicationModeTxt = null
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        binding.signUpLayout.spinnerReferralCode.setSelection(0)

        val arrayListSpinner = arrayListOf("Select Your Type", "Customer", "Retailer")
        val adapters = ArrayAdapter(context, R.layout.spinner_right_aligned, arrayListSpinner)
        adapters.setDropDownViewResource(R.layout.spinner_right_aligned)
        binding.signUpLayout.spinnerAgentType.adapter = adapters
        binding.signUpLayout.spinnerAgentType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                @SuppressLint("SetTextI18n")
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    if (position > 0) {
                        agentType = parent!!.getItemAtPosition(position).toString()
                        toast(agentType.toString())
                    } else {
                        agentType = null
                    }
//                    when (position) {
//                        1 -> {
////                            binding.signUpLayout.referenceId.text.clear()
////                            binding.signUpLayout.agencyName.visibility = View.GONE
////                            binding.signUpLayout.agencyText.visibility = View.GONE
////                            binding.signUpLayout.agencyText.visibility = View.GONE
////                            binding.signUpLayout.referenceIdText.visibility = View.VISIBLE
////                            binding.signUpLayout.referenceType.text.clear()
////                            binding.signUpLayout.agencyName.text.clear()
//                            binding.signUpLayout.referenceSpLayout.visibility = View.VISIBLE
//                            binding.signUpLayout.referenceCodeText.visibility = View.VISIBLE
//                        }
//
//                        2 -> {
////                            binding.referenceId.setText("278")
////                            binding.referenceType.setText("Distributor")
////                            binding.signUpLayout.referenceIdText.visibility = View.VISIBLE
//                            binding.signUpLayout.referenceSpLayout.visibility = View.GONE
//                            binding.signUpLayout.referenceCodeText.visibility = View.GONE
////                            binding.signUpLayout.agencyName.visibility = View.GONE
////                            binding.signUpLayout.agencyText.visibility = View.GONE
//                        }
//
//                        else -> {
//
//                        }
//                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        binding.signUpLayout.spinnerAgentType.setSelection(0)
    }

    private fun btnListener() {
        binding.signUpLayout.tvBtnSubmit.setOnClickListener { validationSignUp() }
        binding.signUpLayout.tvBtnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

    private fun validationSignUp() {
        when {
            agentType.isNullOrBlank() -> {
                toast("Please select Agent Type")
            }
//            binding.referenceId.text.isNullOrBlank() -> {
//                toast("Please enter reference Id")
//            }
//            agentType!!.contains("Customer") && applicationModeTxt.isNullOrBlank() -> {
//                toast("Please select reference code")
//            }

            binding.signUpLayout.firstName.text.isNullOrBlank() -> {
                toast("Enter your name")
            }

            binding.signUpLayout.mobileNo.text.isNullOrBlank() || binding.signUpLayout.mobileNo.text.length != 10 -> {
                toast("Enter your mobile Number")
            }

            binding.signUpLayout.emailID.text.isNullOrBlank() || !binding.signUpLayout.emailID.text!!.contains(
                "@"
            ) -> {
                toast("Enter your email Id")
            }

            else -> {
                signUpCall()
            }
        }
    }

    private fun signUpCall() {
        this.runIfConnected {
            val signUpReq = SignUpReq(
                agentType = agentType,
                applicationType = applicationModeTxt,
                userID = "",
                agentPassword = "",
                firstName = binding.signUpLayout.firstName.text.toString().trim(),
                lastName = binding.signUpLayout.lastName.text.toString().trim(),
                mobileNo = binding.signUpLayout.mobileNo.text.toString().trim(),
                emailID = binding.signUpLayout.emailID.text.toString().trim(),
                companyCode = binding.signUpLayout.companyName.text.toString(),
                agencyName = "",
                refrenceType = "",
                refCode = "",
                refrenceID = "",
                ipAddress = mStash!!.getStringValue(Constants.deviceIPAddress, "")

//            agentType = agentType.toString(),
//            companyCode = mStash!!.getStringValue(Constants.CompanyCode, ""),
//            companyName = mStash!!.getStringValue(Constants.CompanyName, ""),
//            RefranceID = binding.referenceId.text.toString().trim(),
//            RefranceType = binding.referenceType.text.toString().trim(),
//            RefranceCode = referenceCodeText.toString(),
//            FirstName = binding.firstName.text.toString().trim(),
//            LastName = binding.lastName.text.toString().trim(),
//            MobileNo = binding.mobileNo.text.toString().trim(),
//            EmailID = binding.emailID.text.toString().trim()
            )
            Log.d("TAG", "signUpCall: $signUpReq")
            Log.d("UserDetailPost", Gson().toJson(signUpReq))
            viewModel.signUp(
                signUpReq
            ).observe(this@SignUpActivity) { resource ->
                resource.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            pd.dismiss()
                            it.data?.let { users ->
                                users.body()?.let { response -> signUpRes(response) }
                            }
                        }

                        ApiStatus.ERROR -> {
                            pd.dismiss()
                            toast("Something went wrong.!")
                        }

                        ApiStatus.LOADING -> {
                            pd.show()
                            // Show loading indicator if needed
                        }
                    }
                }
            }
        }
    }

    private fun signUpRes(response: SignUpRes?) {
        if (response?.isSuccess == true) {
            Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
            mStash!!.setStringValue("mobileNo", response.data[0].mobileNo)
            Log.d("customer number",response.data[0].mobileNo.toString() )
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java)
//                    .putExtra("Amount", response.ServiceCharge.toString())
            )
        } else {
            toast(response?.returnMessage.toString())
        }
    }

    private fun initView() {
        pd = PD(this)
        mStash = MStash.getInstance(this)
//        binding.signUpLayout.agencyName.visibility = View.GONE
//        binding.signUpLayout.agencyText.visibility = View.GONE

        viewModel = ViewModelProvider(
            this, LoginSignUpViewModelFactory(LoginSignUpRepository(RetrofitClient.apiAllInterface))
        )[LoginSignUpViewModel::class.java]

//        val imageUrl = mStash!!.getStringValue(Constants.CompanyLogo, "")
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
//                    .into(binding.imageView3)
//            } else {
//                Picasso.get()
//                    .load(R.drawable.no_image)
////            .placeholder(R.drawable.placeholder)  // Optional: placeholder while loading
//                    .error(R.drawable.no_image)        // Optional: error image if load fails
//                    .into(binding.imageView3)
//            }
//
////        Glide.with(this)
////            .load(imageUrl)
//////            .placeholder(R.drawable.placeholder)  // Optional: placeholder while loading
////            .error(R.drawable.ic_error)        // Optional: error image if load fails
////            .into(binding.imageView3)
//        } catch (e: IllegalArgumentException) {
//            e.printStackTrace()
//        }
//        binding.signUpLayout.companyCode.text = mStash!!.getStringValue(Constants.CompanyCode, "")
//        binding.signUpLayout.companyName.text = mStash!!.getStringValue(Constants.CompanyName, "")
        binding.signUpLayout.emailID.setText(mStash!!.getStringValue(Constants.EmailID, ""))
        binding.signUpLayout.mobileNo.setText(mStash!!.getStringValue(Constants.MobileNumber, ""))
        // textWatcher is for watching any changes in editText
//        var textWatcher: TextWatcher = object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//                // this function is called before text is edited
//            }
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                validateReferenceIdCalling()
//                // this function is called when text is edited
////                toast("text is edited and onTextChangedListener is called.")
//            }
//
//            override fun afterTextChanged(s: Editable) {
//                // this function is called after text is edited
//            }
//
//        }
////        binding.signUpLayout.referenceId.addTextChangedListener(textWatcher)
    }

//    private fun validateReferenceIdCalling() {
//        viewModel.validateReferenceId(
//            OtpSubmitReq(
//                "", RegistrationID = binding.signUpLayout.referenceId.text.toString().trim()
//            )
//        ).observe(this) { resource ->
//            resource.let {
//                when (it.apiStatus) {
//                    ApiStatus.SUCCESS -> {
//                        it.data?.let { users ->
//                            users.body().let { it1 -> referenceIdRes(it1) }
//                        }
//                    }
//
//                    ApiStatus.ERROR -> {
//                        toast("Something went wrong.!")
//                    }
//
//                    ApiStatus.LOADING -> {
//
//                    }
//                }
//            }
//        }
//    }

//    @SuppressLint("ResourceAsColor", "SetTextI18n")
//    private fun referenceIdRes(response: ValidateReferenceIdRes?) {
//        if (response!!.Status!!) {
//            binding.referenceIdText.setTextColor(
//                ContextCompat.getColor(
//                    this@SignUpActivity, R.color.colorPrimary
//                )
//            )
//            binding.referenceIdText.text = response.Name.toString()
//            binding.referenceType.setText(response.AgentType.toString())
//            toast(response.message.toString())
//        } else {
//            binding.referenceIdText.text = "INVALID REFER ID"
//            binding.referenceIdText.setTextColor(
//                ContextCompat.getColor(
//                    this@SignUpActivity, R.color.red
//                )
//            )
////            toast(response.message.toString())
//        }
//    }
}
