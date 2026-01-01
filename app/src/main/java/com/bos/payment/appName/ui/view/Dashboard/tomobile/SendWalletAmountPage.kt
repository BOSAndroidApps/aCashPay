package com.bos.payment.appName.ui.view.Dashboard.tomobile

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.justpaymodel.RetailerContactListRequestModel
import com.bos.payment.appName.data.model.justpaymodel.SendMoneyToMobileReqModel
import com.bos.payment.appName.data.model.recharge.recharge.TransferToAgentReq
import com.bos.payment.appName.data.model.transferAMountToAgent.TransferAmountToAgentsReq
import com.bos.payment.appName.data.model.transferAMountToAgent.TransferAmountToAgentsRes
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.GetBalanceReq
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.GetBalanceRes
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.databinding.ActivitySendWalletAmountPageBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.adapter.ContactListAdapter
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils
import com.bos.payment.appName.utils.Utils.animateTextSize
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.bos.payment.appName.utils.Utils.toast
import com.google.gson.Gson


class SendWalletAmountPage : AppCompatActivity() {
    lateinit var binding: ActivitySendWalletAmountPageBinding
    private var mStash: MStash? = null
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    var totalamoutForWithdraw: Double = 0.0
    var walletamount: Double = 0.0
    lateinit var dialog: Dialog
    companion object{
        var name : String ? = ""
        var agencyName : String ?= ""
        var mobileNumber : String ? = ""
        var userID : String ? = ""
    }

    var checkFirstTime: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySendWalletAmountPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mStash = MStash.getInstance(this)
        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]


        getAllWalletBalance(checkFirstTime)
        setUIData()
        setClickListner()

    }


    fun setUIData(){
        binding.name.text= "$name ( $userID )"
        binding.contactno.text= mobileNumber
        binding.agencynumber.text= agencyName
        binding.firstCharacter.text = name!!.take(2).uppercase()
        binding.etAmount.requestFocus()
        binding.etAmount.postDelayed({
            binding.etAmount.requestFocus()
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.etAmount, InputMethodManager.SHOW_FORCED)
        }, 300)

    }


    fun setClickListner(){

        binding.back.setOnClickListener {
            finish()
        }


        binding.etAmount.filters= arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            val newInput = dest.toString().substring(0, dstart) +
                    source.subSequence(start, end) +
                    dest.toString().substring(dend)

            val regex = Regex("^\\d{0,7}(\\.\\d{0,2})?$") // up to 7 digits before decimal, 2 after
            if (newInput.matches(regex)) {
                null // accept input
            } else {
                ""   // reject invalid characters
            }
        })


        var lastValidAmount = ""
        binding.etAmount.addTextChangedListener(object : TextWatcher {
            private var editing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (editing) return
                editing = true

                val text = s?.toString()?.trim() ?: ""

                // 🧠 Handle empty or just "." input safely
                if (text.isEmpty() || text == ".") {
                    lastValidAmount = ""
                    editing = false
                    return
                }

                val enteredPrice = text.toDoubleOrNull()

                // 🧱 If invalid number typed
                if (enteredPrice == null) {
                    binding.etAmount.setText(lastValidAmount)
                    binding.etAmount.setSelection(binding.etAmount.text.length)
                    editing = false
                    return
                }

                // 🚫 If exceeds wallet amount → revert and show error
                if (enteredPrice > walletamount) {
                    binding.etAmount.error = "Enter amount ≤ ₹$walletamount"
                    binding.etAmount.setText(lastValidAmount)
                    binding.etAmount.setSelection(binding.etAmount.text.length)
                    editing = false
                    return
                }

                // ✅ Valid input → save, show button, and animate
                lastValidAmount = text

                // 💫 Animate text size like Paytm
                val length = text.length
                val targetSize = when {
                    length <= 3 -> 60f
                    length <= 5 -> 45f
                    length <= 7 -> 35f
                    else -> 28f
                }
                animateTextSize(binding.etAmount, targetSize)

                // Keep cursor at end
                binding.etAmount.setSelection(text.length)
                editing = false
            }
        })


        binding.tvBtnProceed.setOnClickListener{
            var amount = binding.etAmount.text.toString()
            var remarks = binding.remarks.text.toString()

            if(amount.isNullOrBlank()){
                Toast.makeText(this,"Please enter amount for send to retailer wallet",Toast.LENGTH_SHORT).show()
            }else {
                getAllWalletBalance(true)
            }

        }


        binding.main.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            binding.main.getWindowVisibleDisplayFrame(r)
            val screenHeight = binding.main.rootView.height
            val keypadHeight = screenHeight - r.bottom

            if (keypadHeight > screenHeight * 0.20) {
                // Keyboard is open
                if (binding.remarks.hasFocus()) {
                    binding.scrollview.post {
                        binding.scrollview.smoothScrollTo(0, binding.scrollview.bottom)
                    }
                }
            }
        }

    }



    private fun getAllWalletBalance(check:Boolean) {
        runIfConnected {
            val walletBalanceReq = GetBalanceReq(
                parmUser = mStash!!.getStringValue(Constants.RegistrationId, ""),
                flag = "CreditBalance"
            )

            getAllApiServiceViewModel.getWalletBalance(walletBalanceReq)
                .observe(this) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                it.data?.let { users ->
                                    users.body()?.let { response ->
                                        getAllWalletBalanceRes(response,check)
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


    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun getAllWalletBalanceRes(response: GetBalanceRes,check:Boolean) {
        if (response.isSuccess == true) {
            walletamount = (response.data[0].result!!.toDoubleOrNull() ?: 0.0)
            totalamoutForWithdraw = binding.etAmount.text.toString()  !!.toDoubleOrNull() ?: 0.0

            if(totalamoutForWithdraw<= walletamount){
                //hitApiForSendMoneyToAdmin(totalamoutForWithdraw)
                if(check){
                    getTransferAmountToAgentWithCal(totalamoutForWithdraw)
                }
                else{
                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                        Constants.dialog.dismiss()
                    }
                }

            }
            else{
                if(Constants.dialog!=null && Constants.dialog.isShowing){
                    Constants.dialog.dismiss()
                }
                Toast.makeText(this, "Yor merchant balance is low, please contact with your admin.", Toast.LENGTH_LONG).show()
            }

            binding.walletamt.text = "You can send money up to ₹$walletamount only"

            Log.d("actualBalance", "main = $walletamount")

        }
        else {
            toast(response.returnMessage.toString())
            if(Constants.dialog!=null && Constants.dialog.isShowing){
                Constants.dialog.dismiss()
            }
        }

    }




    private fun getTransferAmountToAgentWithCal(rechargeAmount: Double) {
        try {
            val rechargeAmt = rechargeAmount ?: 0.0

            val transferAmountToAgentsReq = TransferAmountToAgentsReq(
                transferFrom = mStash!!.getStringValue(Constants.RegistrationId, "") ?: "0",
                transferTo = "Admin",
                transferAmt = (rechargeAmount ?: "0").toString(), //dr
                remark = "To Mobile Transfer" ,
                transferFromMsg = "Your account has been debited by ₹ ${rechargeAmount} for a ‘To Mobile’ transfer to  $name ($userID).Mobile Number: $mobileNumber",
                transferToMsg = "", // for toself commission remarks
                amountType = "Payout",
                actualTransactionAmount = (rechargeAmount ?: "0").toString(),
                transIpAddress = mStash!!.getStringValue(Constants.deviceIPAddress, "") ?: "0.0.0.0",
                parmUserName = mStash!!.getStringValue(Constants.RegistrationId, "") ?: "0",
                merchantCode = mStash!!.getStringValue(Constants.MerchantId, "") ?: "0",
                servicesChargeAmt = "0.00",
                servicesChargeGSTAmt = "0.00",
                servicesChargeWithoutGST =  "0.00",
                customerVirtualAddress = "",
                retailerCommissionAmt ="0.00",
                retailerId = "",
                paymentMode = "",
                depositBankName = "",
                branchCodeChecqueNo = "",
                apporvedStatus = "Approved",
                registrationId = mStash!!.getStringValue(Constants.RegistrationId, "") ?: "0",
                benfiid = "",
                accountHolder =  "",
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
                                        Log.d("AdminTransferResp",Gson().toJson(response))
                                        if(response.isSuccess!!){
                                            getTransferAmountToAgentInCommissionCal(rechargeAmount,response.data!!.refTransID)
                                        }
                                    }
                                }
                            }

                            ApiStatus.ERROR -> {
                                if(Constants.dialog!=null && Constants.dialog.isShowing){
                                    Constants.dialog.dismiss()
                                }
                                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                            }

                            ApiStatus.LOADING -> {

                            }

                        }
                    }
                }

        } catch (e: NumberFormatException) {
            e.printStackTrace()
            if(Constants.dialog!=null && Constants.dialog.isShowing){
                Constants.dialog.dismiss()
            }
            Toast.makeText(this, e.message.toString() + " " + e.localizedMessage?.toString(), Toast.LENGTH_SHORT).show()
        }

    }





    // for payout transaction and commission entry
    private fun getTransferAmountToAgentInCommissionCal(rechargeAmount: Double,refId:String ?) {

        val transferAmountToAgentsReq = TransferToAgentReq(
            merchantCode = mStash!!.getStringValue(Constants.MerchantId, ""),
            transferFrom = "Admin",
            amountType = "Deposit",
            transIpAddress = mStash!!.getStringValue(Constants.deviceIPAddress, ""),
            remark = "To Mobile Deposit",
            transferTo = userID ,
            transferToMsg = "Your account has been credited by ₹${rechargeAmount} from a ‘To Mobile’ transfer by ${mStash!!.getStringValue(Constants.retailerName, "")} (${mStash!!.getStringValue(Constants.RegistrationId, "")}) . Mobile Number: $mobileNumber",
            gstAmt = 0,
            parmUserName = mStash!!.getStringValue(Constants.RegistrationId, ""),
            servicesChargeGSTAmt = 0,
            servicesChargeWithoutGST = 0,
            actualTransactionAmount = rechargeAmount?.toDouble() ?: 0.0,
            actualCommissionAmt = 0,
            commissionWithoutGST = 0,
            transferFromMsg = "Your account has been debited by ₹${rechargeAmount} from a ‘To Mobile’ transfer by ${mStash!!.getStringValue(Constants.retailerName, "")} (${mStash!!.getStringValue(Constants.RegistrationId, "")}) . Mobile Number: $mobileNumber",
            netCommissionAmt = 0,
            tdSAmt = 0.0,
            servicesChargeAmt = 0,
            customerVirtualAddress = "",
            transferAmt = rechargeAmount?.toDouble() ?: 0.0, // cr
        )

        Log.d("transferAmountToAgentcommissionreq", Gson().toJson(transferAmountToAgentsReq))

        getAllApiServiceViewModel.transferToAgentReq(transferAmountToAgentsReq)
            .observe(this) {
                resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { commissionresp ->

                                    var response = commissionresp.isSuccess
                                    Log.d("commissionresponse","$response" )

                                    openDialogForPayout(rechargeAmount,refId)

                                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                                        Constants.dialog.dismiss()
                                    }

                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            if(Constants.dialog!=null && Constants.dialog.isShowing){
                                Constants.dialog.dismiss()
                            }
                            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }

                        ApiStatus.LOADING -> {

                        }

                    }
                }
            }
     }



    @SuppressLint("SetTextI18n")
    fun openDialogForPayout(transferAmount: Double, refid:String?)  {
        dialog = Dialog(this@SendWalletAmountPage, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.successpopup)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        dialog.setCanceledOnTouchOutside(false)

        val tvamount = dialog.findViewById<TextView>(R.id.tvAmount)
        val tvToBeneficiary = dialog.findViewById<TextView>(R.id.tvToBeneficiary)
        val tvUpiRefNumber = dialog.findViewById<TextView>(R.id.tvUpiRefNumber)
        val done = dialog.findViewById<Button>(R.id.btnDone)

        tvamount.text = "₹${transferAmount}"
        tvToBeneficiary.text = "$name ($userID )"
        tvUpiRefNumber.text = refid


        done.setOnClickListener {
           dialog.dismiss()
            finish()
        }

        dialog.show() // ✅ REQUIRED
    }



  /*  fun hitApiForSendMoneyToAdmin(amount : Double){

        val txnId = "TXN" + (1000..9999).random()

        val getRetailerContactList = SendMoneyToMobileReqModel(
            actualTransactionAmount = amount,
            flag = "MINUS",
            amountType = "Payout",
            transferFromMsg = "₹ ${amount} transferred to Admin (ID: ${userID}). Txn ID: $txnId.",
            transIpAddress = "",
            remark = "To Mobile Transfer"*//*binding.remarks.text.toString()*//*,
            transferTo = mStash!!.getStringValue(Constants.RegistrationId, ""),
            transferToMsg = "You have received ₹${amount}from Retailer. User ID: ${userID}.",
            transferAmt = amount,
            parmUserName =  mStash!!.getStringValue(Constants.RegistrationId, "")
        )

        Log.d("sendMoneyToAdminreq", Gson().toJson(getRetailerContactList))

        getAllApiServiceViewModel.sendMoneyToMobileReqModel(getRetailerContactList).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {

                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("sendMoneyToAdminresp", Gson().toJson(response))
                                if(response.isSuccess!!){
                                    hitApiForSendMoneyToRetailer(amount)
                                }
                                else{
                                    pd.dismiss()
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




    fun hitApiForSendMoneyToRetailer(amount : Double){

        val txnId = "TXN" + (1000..9999).random()

        val getRetailerContactList = SendMoneyToMobileReqModel(
            actualTransactionAmount = amount,
            flag = "PLUS",
            amountType = "Deposit",
            transferFromMsg = "₹ ${amount} transferred to Retailer (ID: ${userID}). Txn ID: $txnId.",
            transIpAddress = "",
            remark = "To Mobile Deposit"*//*binding.remarks.text.toString()*//*,
            transferTo = userID,
            transferToMsg = "You have received ₹${amount}from Retailer. User ID: ${userID}.",
            transferAmt = amount,
            parmUserName = userID
        )

        Log.d("sendMoneyToRetailerreq", Gson().toJson(getRetailerContactList))

        getAllApiServiceViewModel.sendMoneyToMobileReqModel(getRetailerContactList).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {

                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("sendMoneyToRetailerresp", Gson().toJson(response))
                                pd.dismiss()
                                if(response.isSuccess!!){
                                    Toast.makeText(this,response.returnMessage,Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    Toast.makeText(this,response.returnMessage,Toast.LENGTH_SHORT).show()
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


    }*/






}