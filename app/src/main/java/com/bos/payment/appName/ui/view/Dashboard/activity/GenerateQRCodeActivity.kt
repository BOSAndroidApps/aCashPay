package com.bos.payment.appName.ui.view.Dashboard.activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.data.model.recharge.qrCode.GenerateQRCodeReq
import com.bos.payment.appName.data.model.recharge.qrCode.GenerateQRCodeRes
import com.bos.payment.appName.data.repository.MoneyTransferRepository
import com.bos.payment.appName.data.viewModelFactory.MoneyTransferViewModelFactory
import com.bos.payment.appName.databinding.ActivityGenerateQrcodeBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.moneyTransfer.ScannerFragment
import com.bos.payment.appName.ui.viewmodel.MoneyTransferViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.PD
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.bos.payment.appName.utils.Utils.toast
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class GenerateQRCodeActivity : AppCompatActivity() {
    private lateinit var bin: ActivityGenerateQrcodeBinding
    private lateinit var viewModel: MoneyTransferViewModel
    private var mStash: MStash? = null
    private lateinit var pd: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bin = ActivityGenerateQrcodeBinding.inflate(layoutInflater)
        setContentView(bin.root)
        intiView()
        getQRCode()
        btnListener()

    }

    private fun btnListener() {
        bin.ivBack.setOnClickListener {
            onBackPressed()
        }
        bin.openScanner.setOnClickListener {
            startActivity(Intent(this, ScannerFragment::class.java))
            finish()
        }

    }

    private fun intiView() {
        pd = PD(this)
        mStash = MStash.getInstance(this)
        viewModel = ViewModelProvider(this, MoneyTransferViewModelFactory(MoneyTransferRepository(RetrofitClient.apiAllAPIService)))[MoneyTransferViewModel::class.java]
    }


    private fun getQRCode() {
        this.runIfConnected {
            val generateQRCodeReq =
                GenerateQRCodeReq(
                    RegistrationID = mStash!!.getStringValue(
                        Constants.MerchantId,
                        ""
                    )
                )
            Log.d("generateQRCodeReq", Gson().toJson(generateQRCodeReq))
            viewModel.generateQRCode(generateQRCodeReq).observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            pd.dismiss()
                            it.data?.body().let { response ->
                                if (response != null) {
                                    Log.d("intentUrl", response.details!!.intentUrl.toString())
                                    handleQRCodeResponse(response)

                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            pd.dismiss()
                            toast("Something went wrong")
                        }

                        ApiStatus.LOADING -> {
                            pd.show()
                        }
                    }
                }
            }
        }
    }

    private fun handleQRCodeResponse(response: GenerateQRCodeRes) {
        if (response.status == true) {
            response.details?.intentUrl.let { upiIntend ->
                generateQRCode(upiIntend.toString())
//                toast(response.message ?: "QR Code Generated")
            } ?: run {
                toast("Invalid UPI Intent")
            }
        } else {
            toast(response.message ?: "Failed to generate QR code")
        }
    }

    private fun generateQRCode(url: String) {
        val qrCodeWriter = QRCodeWriter()
        try {
            val bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 512, 512)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
            bin.qrCodeImageView.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
            toast("Failed to generate QR code")
        }


    }

}