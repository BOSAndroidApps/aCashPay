package com.bos.payment.appName.ui.view.idfcPayout

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.idfcPayout.AOPPayOutReq
import com.bos.payment.appName.data.model.idfcPayout.AOPPayOutRes
import com.bos.payment.appName.data.model.merchant.apiServiceCharge.GetPayoutCommercialReq
import com.bos.payment.appName.data.model.merchant.apiServiceCharge.GetPayoutCommercialRes
import com.bos.payment.appName.data.model.transferAMountToAgent.TransferAmountToAgentsReq
import com.bos.payment.appName.data.model.transferAMountToAgent.TransferAmountToAgentsRes
import com.bos.payment.appName.data.model.walletBalance.merchantBal.GetMerchantBalanceReq
import com.bos.payment.appName.data.model.walletBalance.merchantBal.GetMerchantBalanceRes
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.GetBalanceReq
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.GetBalanceRes
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.repository.PayoutRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.data.viewModelFactory.PayoutViewModelFactory
import com.bos.payment.appName.databinding.ActivityPayoutBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.Dashboard.activity.DashboardActivity
import com.bos.payment.appName.ui.view.Dashboard.dmt.DMTRechargeSuccessfulPage
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.ui.viewmodel.MoneyTransferViewModel
import com.bos.payment.appName.ui.viewmodel.PayoutViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils
import com.bos.payment.appName.utils.Utils.PD
import com.bos.payment.appName.utils.Utils.toast
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class Payout : AppCompatActivity() {
    private lateinit var bin: ActivityPayoutBinding
    private var mStash: MStash? = null
    private lateinit var pd: AlertDialog
    private lateinit var viewModel: MoneyTransferViewModel
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    private lateinit var viewModel1: PayoutViewModel
    private var serviceCharge: Double = 0.0
    var isSlabMatched = false // Flag to check if any slab matches

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_payout)
        bin = ActivityPayoutBinding.inflate(layoutInflater)
        setContentView(bin.root)
        initView()
        btnListener()
    }

    private fun initView() {
        mStash = MStash.getInstance(this)
        pd = PD(this)

        try {
            val imageUrl = mStash!!.getStringValue(Constants.CompanyLogo, "")

            Picasso.get().load(imageUrl)
//            .placeholder(R.drawable.placeholder)  // Optional: placeholder while loading
                .error(R.drawable.no_image)        // Optional: error image if load fails
                .into(bin.toolbar.tvToolbarName)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]

        viewModel1 = ViewModelProvider(this, PayoutViewModelFactory(PayoutRepository(RetrofitClient.apiAllPayoutAPI)))[PayoutViewModel::class.java]
    }


    private fun btnListener() {
        bin.toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }

        bin.toolbar.tvToolbarName.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        bin.CancelBtn.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        bin.transactionCancelBtn.setOnClickListener {
            bin.serviceChargeLayout.visibility = View.GONE
        }

        bin.payBtn.setOnClickListener {
            getAllWalletBalance()
        }

        bin.proceedPayBtn.setOnClickListener {
            transactionValidation()
        }

        bin.amount.setOnClickListener {
            bin.proceedBtnLayout.visibility = View.VISIBLE
            bin.serviceChargeLayout.visibility = View.GONE
        }

    }

    private fun transactionValidation() {
        if (bin.ownerName.text.length < 3){
            bin.ownerName.requestFocus()
            toast("Please enter atleast 3 char your name")
        }else if (bin.amount.text.toString().isEmpty()) {
            bin.amount.requestFocus()
            toast("Please enter your amount")
        } else if (bin.bankAccountNo.text.toString().isEmpty()) {
            bin.bankAccountNo.requestFocus()
            toast("Please enter bank account no")
        } else if (bin.ifscCode.text.toString().isEmpty()) {
            bin.ifscCode.requestFocus()
            toast("Please enter ifsc code")
        }else {
            val transferAmountText = bin.amount.text.toString().trim()
//            getAllServiceCharge(transferAmountText)
            getAllApiPayoutCommercialCharge(transferAmountText)
        }
    }

    private fun getAllApiPayoutCommercialCharge(rechargeAmount: String) {
        val getPayoutCommercialReq = GetPayoutCommercialReq(
            merchant = mStash!!.getStringValue(Constants.RegistrationId, ""),
            productId = "F0112",
            modeofPayment = "IMPS"
        )
        Log.d("getAllGsonFromAPI", Gson().toJson(getPayoutCommercialReq))
        getAllApiServiceViewModel.getAllApiPayoutCommercialCharge(getPayoutCommercialReq)
            .observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            pd.dismiss()
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    getAllApiPayoutCommercialChargeRes(response, rechargeAmount)
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

    private fun getAllApiPayoutCommercialChargeRes(response: GetPayoutCommercialRes, rechargeAmount: String) {

        if (response.isSuccess == true) {
            bin.serviceChargeLayout.visibility = View.VISIBLE
            bin.proceedBtnLayout.visibility = View.GONE

            try {
                val rechargeAmountValue = rechargeAmount.toDoubleOrNull() ?: 0.0
                val TDSTax = 5.0 // Fixed TDS rate of 5%

                for (i in response.data.indices) {
                    val gst = 18.0 // Fixed GST rate of 18%
                    // Get characters between ":" and "-" and convert to Double
                    // Extract the minimum value
                    val min = response.data[i].slabName
                        ?.substringAfter(": ") // Get the part after "With Slab: "
                        ?.substringBefore("-") // Get the part before "-"
                        ?.replace(",", "")     // Remove commas
                        ?.toDoubleOrNull()     // Convert to Double
                        ?: 0.0

                    // Extract the maximum value
                    val max = response.data[i].slabName
                        ?.substringAfter("-")  // Get the part after "-"
                        ?.replace(",", "")     // Remove commas
                        ?.toDoubleOrNull()     // Convert to Double
                        ?: 0.0

                    Log.d(TAG, "getAllApiPayoutCommercialChargeRes: $min, $max")

                    if (rechargeAmountValue in min..max) {
                        isSlabMatched = true   // Mark that a matching slab was found
                        serviceCharge = response.data[i].serviceCharge?.toDoubleOrNull() ?: 0.0
                        Log.d("serviceCharge", serviceCharge.toString())
                        serviceChargeCalculation(serviceCharge, gst, rechargeAmount, response, min, max)
                        break
                    }

                }
                //if no matching slab was found, show an error message
                if (!isSlabMatched) {
                    isSlabMatched = false
                    bin.serviceChargeLayout.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "No matching slab found for the amount: $rechargeAmountValue",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            pd.dismiss()
            Toast.makeText(this, response.returnMessage.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun serviceChargeCalculation(
        serviceCharge: Double,
        gstRate: Double,
        rechargeAmount: String,
//        response: List<GetAPIServiceChargeRes>,
        response: GetPayoutCommercialRes,
        min: Double,
        max: Double
    ): Double {
        val rechargeAmountValue = rechargeAmount.toDoubleOrNull() ?: 0.0
        var totalServiceChargeWithGst = 0.0
        var serviceChargeWithGst = 0.0
        var slabLimit: String? = null
        bin.serviceChargeLayout.visibility = View.VISIBLE

        if (mStash!!.getStringValue(Constants.RegistrationId, "")!!.isNotEmpty()) {
            slabLimit = when {
                rechargeAmountValue <= max -> max.toString()
                else -> {
                    Toast.makeText(this, "No valid slab found for the amount: $rechargeAmountValue", Toast.LENGTH_SHORT).show()
                    null
                }
            }
        }else {
            toast("No valid slab found for the amount: $rechargeAmountValue")

        }

        for (i in response.data.indices) {
            if (slabLimit != null) {
                Log.d("ServiceType", "${response.data[i].serviceType} - Index $i")
                serviceChargeWithGst = when (response.data[i].serviceType) {
                    "Amount" -> {
                        bin.serviceChargeName.text = "Service Charge Rs"
                        val serviceChargeGst = serviceCharge * (gstRate / 100)
                        mStash?.setStringValue(Constants.serviceCharge, String.format("%.2f", serviceCharge))
                        serviceCharge + serviceChargeGst
                    }
                    "Percentage" -> {
                        bin.serviceChargeName.text = "Service Charge %"
                        val serviceInAmount = rechargeAmountValue * (serviceCharge / 100)
                        val serviceWithGst = serviceInAmount * (gstRate / 100)
                        mStash?.setStringValue(Constants.serviceCharge, String.format("%.2f", serviceInAmount))
                        serviceInAmount + serviceWithGst
                    }
                    else -> 0.0
                }
                break
            }
        }

        if (serviceChargeWithGst == 0.0) {
            Toast.makeText(this, "No matching slab found for the entered amount", Toast.LENGTH_SHORT).show()
            return 0.0
        }

        val actualAmount = serviceChargeWithGst + rechargeAmountValue

        mStash?.apply {
            setStringValue(Constants.actualAmountServiceChargeWithGST, String.format("%.2f", actualAmount))
            setStringValue(Constants.serviceChargeWithGST, String.format("%.2f", serviceChargeWithGst))
            setStringValue(Constants.totalTransaction, String.format("%.2f", actualAmount))
        }

        bin.apply {
            gstWithServiceCharge.setText(String.format("%.2f", serviceChargeWithGst))
            serviceChargeAmount.setText(String.format("%.2f", serviceCharge))
            totalTransferText.setText(String.format("%.2f", actualAmount))
        }

        Log.d("TotalRechargeAmount", String.format("%.2f", actualAmount))
        Log.d("totalAmountWithGst", String.format("%.2f", serviceChargeWithGst))

        return serviceChargeWithGst
    }


    private fun sendAllPayoutAmount() {
        val aopPayOutReq = AOPPayOutReq(
            accountNumber = bin.bankAccountNo.text.toString().trim(),
            amount = bin.amount.text.toString().trim(),
            transactionType = "IMPS",
            beneficiaryIFSC = bin.ifscCode.text.toString().trim(),
            beneficiaryName = bin.ownerName.text.toString().trim(),
            emailID = mStash?.getStringValue(Constants.EmailID, ""),
            mobileNo = mStash?.getStringValue(Constants.MobileNumber, ""),
            registrationID = mStash?.getStringValue(Constants.MerchantId, "")
//            registrationID = "AOP-554"
        )
        Log.d("getAllGsonFromAPI", Gson().toJson(aopPayOutReq))
        viewModel1.sendAllPayoutAmount(aopPayOutReq).observe(this) { resource ->
            resource?.let {
                when(it.apiStatus){
                    ApiStatus.SUCCESS -> {
                        pd.dismiss()
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                sendAllPayoutAmountRes(response)
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


    private fun sendAllPayoutAmountRes(response: AOPPayOutRes) {
        if (response.statuss == "SUCCESS"){

            pd.dismiss()
            // UTR received, process the response and move to the success page
            val intent = Intent(this, DMTRechargeSuccessfulPage::class.java).apply {

                putExtra("transactionId", response.initiateAuthGenericFundTransferAPIResp?.resourceData?.transactionID.toString())
//                putExtra("operatorName", response.operatorname.toString())
//                putExtra("mobileNumber", mobileNo.toString())
                putExtra("upiId", intent.getStringExtra("upiId")) // Fix: Use correct Intent value
                putExtra("amount", bin.amount.text.toString())
                putExtra(
                    "payeeName",
                    intent.getStringExtra("payeeName")
                ) // Fix: Use correct Intent value
//                putExtra("totalAmount", totalAmountNet.toString())
                putExtra("transactionDate", response.initiateAuthGenericFundTransferAPIResp?.metaData?.time.toString())
                putExtra("referenceId", response.initiateAuthGenericFundTransferAPIResp?.resourceData?.transactionReferenceNo.toString())
//                putExtra("operationId", body.data?.operatorid.toString())
//                    putExtra("dateAndTime", response.daterefunded.toString())
//                    putExtra("tdsTax", response.tds.toString())
                putExtra("Status", response.statuss.toString())
                putExtra("message", response.message.toString())
//                putExtra("utrNo", response.data!![0].utr.toString())
//                putExtra("utrNo", response.data!![0].payoutRef.toString())
                putExtra("customerBankName", response.initiateAuthGenericFundTransferAPIResp?.resourceData?.beneficiaryName.toString())
                putExtra("bankIFSC", bin.ifscCode.text.toString())
                putExtra("bankAccountNO", bin.bankAccountNo.text.toString())


//                putExtra("totalAmount", mStash!!.getStringValue(Constants.totalTransaction, ""))

                //service and commission charge
//                putExtra("tdsTax", mStash!!.getStringValue(Constants.tds, "0.00"))
//                putExtra(
//                    "retailerCommissionWithoutTDS",
//                    mStash!!.getStringValue(Constants.retailerCommissionWithoutTDS, "0.00")
//                )
//                putExtra(
//                    "customerCommissionWithoutTDS",
//                    mStash!!.getStringValue(Constants.customerCommissionWithoutTDS, "0.00")
//                )
                putExtra("serviceCharge", mStash!!.getStringValue(Constants.serviceCharge, "0.00"))
                putExtra(
                    "serviceChargeWithGST",
                    mStash!!.getStringValue(Constants.serviceChargeWithGST, "0.00")
                )
                putExtra(
                    "totalTransaction",
                    mStash!!.getStringValue(Constants.totalTransaction, "0.00")
                )
            }
            startActivity(intent)
//            toast("Transaction Successful")
        } else {
            Toast.makeText(this, response.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    private fun getAllWalletBalance() {
        val walletBalanceReq = GetBalanceReq(
            parmUser = mStash!!.getStringValue(Constants.RegistrationId, ""),
            flag = "CreditBalance")

        Log.d("getAllGsonFromAPI", Gson().toJson(walletBalanceReq))

        getAllApiServiceViewModel.getWalletBalance(walletBalanceReq).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
//                        pd.dismiss()
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                getAllWalletBalanceRes(response)
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

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun getAllWalletBalanceRes(response: GetBalanceRes) {
        if (response.isSuccess == true) {
            val mainBalance = (response.data[0].result!!.toDoubleOrNull() ?: 0.0)
            getMerchantBalance(mainBalance)

            Log.d("actualBalance", "main = $mainBalance")

            val totalAmount =
                mStash!!.getStringValue(Constants.totalTransaction, "")?.toDoubleOrNull() ?: 0.0

        } else {
            toast(response.returnMessage.toString())
        }
    }


    private fun getMerchantBalance(mainBalance: Double) {
        val getMerchantBalanceReq = GetMerchantBalanceReq(
            parmUser = mStash!!.getStringValue(Constants.MerchantId, ""),
//            parmUser = "AOP-554",
            flag = "DebitBalance"
        )
        Log.d("getAllGsonFromAPI", Gson().toJson(getMerchantBalanceReq))

        getAllApiServiceViewModel.getAllMerchantBalance(getMerchantBalanceReq)
            .observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
//                        pd.dismiss()
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    getAllMerchantBalanceRes(response, mainBalance)
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

    private fun getAllMerchantBalanceRes(response: GetMerchantBalanceRes, mainBalance: Double) {
        if (response.isSuccess == true) {
            Log.d(TAG, "getAllMerchantBalanceRes: ${response.data[0].debitBalance}")
            mStash!!.setStringValue(Constants.merchantBalance, response.data[0].debitBalance)

            val totalAmount = mStash!!.getStringValue(Constants.totalTransaction, "")?.toDoubleOrNull() ?: 0.0
            val merchantBalance = response.data[0].debitBalance?.toDoubleOrNull() ?: 0.0

            Log.d("balanceCheck", "MainBal = $mainBalance, merchantBal = $merchantBalance,totalAmount = $totalAmount, Status = ${totalAmount <= mainBalance && totalAmount <= merchantBalance}")

            if (totalAmount <= mainBalance && totalAmount <= merchantBalance) {
                getTransferAmountToAgentWithCal(bin.amount.text.toString())
            }
            else {
                pd.dismiss()
                Toast.makeText(this, "Wallet balance is low. VBal = $mainBalance, MBal = $merchantBalance, totalAmt = $totalAmount", Toast.LENGTH_LONG).show()
            }
        }
        else {
            pd.dismiss()
            Toast.makeText(this, response.returnMessage.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getTransferAmountToAgentWithCal(rechargeAmount: String) {
        try {
            val currentDateTime = Utils.getCurrentDateTime()
            val bankAccountNo = bin.bankAccountNo.text.toString().trim()
            val transferAmountToAgentsReq = TransferAmountToAgentsReq(
                transferFrom = mStash!!.getStringValue(Constants.RegistrationId, "") ?: "0",
                transferTo = "Admin",
                transferAmt = mStash!!.getStringValue(Constants.totalTransaction, "") ?: "0",
                remark = "Payout",
                transferFromMsg = "Your Account is debited by ${rechargeAmount ?: "0"} Rs. Due to Pay Out on number ${bankAccountNo ?: ""}.",
                transferToMsg = "Your Account is credited by ${rechargeAmount ?: "0"} Rs. Due to Pay Out on number ${bankAccountNo ?: ""}.",
                amountType = "Payout",
                actualTransactionAmount = rechargeAmount ?: "0",
                transIpAddress = mStash!!.getStringValue(Constants.deviceIPAddress, "") ?: "0.0.0.0",
                parmUserName = mStash!!.getStringValue(Constants.RegistrationId, "") ?: "0",
                merchantCode = mStash!!.getStringValue(Constants.MerchantId, "") ?: "0",
                servicesChargeAmt = mStash!!.getStringValue(Constants.serviceCharge, "") ?: "0.00",
                servicesChargeGSTAmt = mStash!!.getStringValue(Constants.serviceChargeWithGST, "") ?: "0.00",
                servicesChargeWithoutGST = mStash!!.getStringValue(Constants.serviceCharge, "") ?: "0.00",
                customerVirtualAddress = "",
                retailerCommissionAmt = mStash!!.getStringValue(Constants.retailerCommissionWithoutTDS, "").takeIf { it!!.isNotEmpty() } ?: "0.00",
                retailerId = "",
                paymentMode = "IMPS",
                depositBankName = "",
                branchCodeChecqueNo = "",
                apporvedStatus = "Approved",
                registrationId = mStash!!.getStringValue(Constants.RegistrationId, "") ?: "0",
                benfiid = "",
                accountHolder = "",
                flag = "Y"
            )

            Log.d("getAllGsonFromAPI", Gson().toJson(transferAmountToAgentsReq))
            getAllApiServiceViewModel.getTransferAmountToAgents(transferAmountToAgentsReq)
                .observe(this) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                it.data?.let { users ->
                                    users.body()?.let { response ->
                                        getTransferAmountToAgentWithCalRes(response)
                                    }
                                }
                            }

                            ApiStatus.ERROR -> {
                                pd.dismiss()
                                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            ApiStatus.LOADING -> {
                                pd.show()
                            }
                        }
                    }
                }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            pd.dismiss()
            Toast.makeText(
                this,
                e.message.toString() + " " + e.localizedMessage?.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getTransferAmountToAgentWithCalRes(response: TransferAmountToAgentsRes) {
        if (response.isSuccess == true) {
            sendAllPayoutAmount()
        }
        else {
            pd.dismiss()
            toast(response.returnMessage.toString())
        }
    }

}