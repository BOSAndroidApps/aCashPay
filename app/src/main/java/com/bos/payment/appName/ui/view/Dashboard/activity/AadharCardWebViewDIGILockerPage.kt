package com.bos.payment.appName.ui.view.Dashboard.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.repository.TravelRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.data.viewModelFactory.TravelViewModelFactory
import com.bos.payment.appName.databinding.ActivityAadharCardWebViewDigilockerPageBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.Dashboard.activity.ManageKYC.Companion.AadhaarDOB
import com.bos.payment.appName.ui.view.Dashboard.activity.ManageKYC.Companion.AadhaarName
import com.bos.payment.appName.ui.view.Dashboard.activity.ManageKYC.Companion.AadhaarResponse
import com.bos.payment.appName.ui.view.Dashboard.activity.ManageKYC.Companion.AadharCountry
import com.bos.payment.appName.ui.view.Dashboard.activity.ManageKYC.Companion.AadharHouse
import com.bos.payment.appName.ui.view.Dashboard.activity.ManageKYC.Companion.AadharImage
import com.bos.payment.appName.ui.view.Dashboard.activity.ManageKYC.Companion.AadharPin
import com.bos.payment.appName.ui.view.Dashboard.activity.ManageKYC.Companion.AadharState
import com.bos.payment.appName.ui.view.Dashboard.activity.ManageKYC.Companion.Aadhardist
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.ui.viewmodel.TravelViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Constants.AadharTransactionIdNo
import com.bos.payment.appName.utils.Constants.AadharVerified
import com.bos.payment.appName.utils.MStash
import com.example.theemiclub.data.model.loginsignup.verification.AAdhaarDetailesReq

import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale

class AadharCardWebViewDIGILockerPage : AppCompatActivity() {

    lateinit var binding: ActivityAadharCardWebViewDigilockerPageBinding

    lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    private lateinit var viewCibilModel: TravelViewModel
    private var mStash: MStash? = null


    companion object {
        var digilockerLink: String = ""
        var VerifiedID: String = ""
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAadharCardWebViewDigilockerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBarsInsets.left,
                systemBarsInsets.top,
                systemBarsInsets.right,
                systemBarsInsets.bottom
            )

            WindowInsetsCompat.CONSUMED
        }

        getAllApiServiceViewModel = ViewModelProvider(
            this,
            GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface))
        )[GetAllApiServiceViewModel::class.java]
        viewCibilModel = ViewModelProvider(
            this,
            TravelViewModelFactory(TravelRepository(RetrofitClient.apiInterfacePAN, null))
        )[TravelViewModel::class.java]

        mStash = MStash.getInstance(this)

        setDataInWebView()

    }


    override fun onResume() {
        super.onResume()


    }


    fun setDataInWebView() {
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.addJavascriptInterface(WebAppInterface(this), "AndroidInterface")
        binding.webview.webViewClient = WebViewClient()

        binding.webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // Script 1: Extract Transaction ID
                val jsTransactionId = """
            (function() {
                try {
                    var text = document.getElementById("transctionID").innerText;
                    var id = text.split(":")[1].trim(); // Extract only the ID
                    AndroidInterface.receiveTransactionId(id);
                } catch (e) {
                    AndroidInterface.receiveTransactionId("Error: " + e.message);
                }
            })();
        """.trimIndent()

                view?.evaluateJavascript(jsTransactionId, null)

                // Script 2: Detect button click
                val jsButtonListener = """
            (function() {
                var button = document.querySelector('.text-center.p-3 .btn.btn-outline-info.btn-block.digitap-blue-button-outline');
                if (button) {
                    button.addEventListener('click', function() {
                        AndroidInterface.onButtonClicked("YES, I want to exit");
                    });
                }
            })();
        """.trimIndent()

                view?.evaluateJavascript(jsButtonListener, null)
            }
        }



        binding.webview.loadUrl(digilockerLink)
    }


    override fun onBackPressed() {

        if (binding.webview.canGoBack()) {
            binding.webview.goBack()
        } else {
            if (VerifiedID.startsWith("Error")) {
                AadharVerified = "no"
            }
            super.onBackPressed()
        }


    }


    inner class WebAppInterface(private val context: Context) {
        var checkAdharNumber: Boolean = false

        @JavascriptInterface
        fun receiveTransactionId(transactionId: String) {
            Log.d("WebViewData", "Transaction ID: $transactionId")
            VerifiedID = transactionId
            if (!transactionId.isBlank() && !transactionId.startsWith("Error")) {
                if (context is Activity) {
                    if (transactionId.equals(AadharTransactionIdNo)) {
                        Constants.AadharVerified = "yes"

                        if (!checkAdharNumber) {
                            (context as Activity).runOnUiThread {
                                this@AadharCardWebViewDIGILockerPage.hitApiForAadharVerification(
                                    VerifiedID
                                )
                            }
                        }

                    } else {
                        Toast.makeText(context, "aadhaar not verified", Toast.LENGTH_SHORT).show()
                        Constants.AadharVerified = ""
                        context.finish()
                    }

                }
            } else {


            }

        }


        @JavascriptInterface
        fun onButtonClicked(message: String) {
            Log.d("WebViewData", "Button clicked: $message")
            if (context is Activity) {
                checkAdharNumber = true
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                (context as Activity).finish() // if you want to exit on button click
            }
        }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun hitApiForAadharVerification(transactionId: String) {

        var aadharverificationreq = AAdhaarDetailesReq(
            transactionID = transactionId,
            registrationID = Constants.PAN_VERIFICATION_REGISTRATION_ID,
        )

        Log.d("AadharDetailsreq", Gson().toJson(aadharverificationreq))

        this@AadharCardWebViewDIGILockerPage.viewCibilModel.getAAdhaarDetailesReq(aadharverificationreq).observe(this) { resources ->
            resources.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data.let { users ->
                            users!!.body().let { response ->
                                Constants.dialog.dismiss()
                                Log.d("AadharDetailsResp", Gson().toJson(response))
                                if (response!!.status.equals("True", ignoreCase = true)) {
                                    val input = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                    val output = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    val parsedDate = input.parse(response.model!!.dob!!)
                                    val formattedDate = output.format(parsedDate!!)
                                    AadhaarDOB = formattedDate.format(output)
                                    AadhaarName = response.model!!.name!!
                                    AadharHouse = response.model!!.address!!.house!!
                                    Aadhardist = response.model!!.address!!.dist!!
                                    AadharPin = response.model!!.address!!.pc!!
                                    AadharState = response.model!!.address!!.state!!
                                    AadharCountry = response.model!!.address!!.country!!
                                    AadharImage = response.model!!.image!!
                                    AadhaarResponse = Gson().toJson(response)

                                    val addressData = response.model?.address

                                    ManageKYC.fullAddress = listOf(
                                        addressData?.house,
                                        addressData?.street,
                                        addressData?.loc,
                                        addressData?.vtc,
                                        addressData?.po,
                                        addressData?.dist,
                                        addressData?.subdist,
                                        addressData?.state,
                                        addressData?.country,


                                        addressData?.pc
                                      ).filter { !it.isNullOrEmpty() }.joinToString(separator = ", ")
                                      finish()

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


}