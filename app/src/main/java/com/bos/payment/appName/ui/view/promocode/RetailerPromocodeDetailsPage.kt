package com.bos.payment.appName.ui.view.promocode

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.promocode.GetEligibleReq
import com.bos.payment.appName.data.model.promocode.GetPromotionListReq
import com.bos.payment.appName.data.model.promocode.ManagePromoUsageReq
import com.bos.payment.appName.data.model.promocode.PromoDataItem
import com.bos.payment.appName.data.model.recharge.recharge.TransferToAgentReq
import com.bos.payment.appName.data.model.transferAMountToAgent.TransferAmountToAgentsRes
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.databinding.ActivityPromocodeDetailsPageBinding
import com.bos.payment.appName.databinding.ActivityPromocodeListBinding
import com.bos.payment.appName.databinding.ActivityRetailerPromocodeDetailsPageBinding
import com.bos.payment.appName.databinding.ActivityRetailerWalletPromocodeListBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.adapter.PromocodeListAdapter
import com.bos.payment.appName.ui.view.Dashboard.tomobile.SendWalletAmountPage.Companion.name
import com.bos.payment.appName.ui.view.Dashboard.tomobile.SendWalletAmountPage.Companion.userID
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Constants.formatDateTime
import com.bos.payment.appName.utils.Constants.startExpiryTimer
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.google.gson.Gson

class RetailerPromocodeDetailsPage : AppCompatActivity() {
    lateinit var binding: ActivityRetailerPromocodeDetailsPageBinding
    var countDownTimer: CountDownTimer? = null
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    private var mStash: MStash? = null
    lateinit var dialog: Dialog
    var redeemAmount : Double =0.0
    var totalTransactionAmount : Double =0.0
    var minTransactionAmount : Double =0.0

    companion object{
       lateinit var retailerWalletPromoDataItem: PromoDataItem
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRetailerPromocodeDetailsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mStash = MStash.getInstance(this@RetailerPromocodeDetailsPage)

        getAllApiServiceViewModel = ViewModelProvider(this@RetailerPromocodeDetailsPage, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)
        ))[GetAllApiServiceViewModel::class.java]


        hitApiForGetEligible()
        setDataOnView()
        setonClickListner()

    }

    fun setDataOnView(){
        binding.promocode.text= retailerWalletPromoDataItem.promoCode

        binding.statuscard.setCardBackgroundColor(
            if (retailerWalletPromoDataItem.status.equals("active", true))
                ContextCompat.getColor(binding.root.context, R.color.green)
            else
                ContextCompat.getColor(binding.root.context, R.color.grey))

        binding.status.text= retailerWalletPromoDataItem.status
        binding.promotionTypeApplicationMode.text= "${retailerWalletPromoDataItem.promotionType} Cashback"


        // Offer Amount
        val offerText = when (retailerWalletPromoDataItem.discountType?.lowercase()) {
            "flat" -> " ${retailerWalletPromoDataItem.discountValue}"
            "percentage" -> "${retailerWalletPromoDataItem.discountValue}% "
            else -> "-"
        }
        binding.offeramount.text = "$offerText"
        binding.maxdiscount.text = "Cashback Up to ₹${String.format("%.2f",retailerWalletPromoDataItem.maxDiscountAmount)}"
        binding.maxlimited.text = "${retailerWalletPromoDataItem.usageLimitPerUser} "
        binding.fromdate.text= formatDateTime(retailerWalletPromoDataItem.startDate)
        binding.todate.text= formatDateTime(retailerWalletPromoDataItem.endDate)
        binding.targetamount.text = "₹ (${String.format("%.2f",retailerWalletPromoDataItem.minTransactionAmount)})"
        binding.maxamount.text ="₹ ${String.format("%.2f",retailerWalletPromoDataItem.minTransactionAmount)}"
        binding.promocodeforachieved.text = retailerWalletPromoDataItem.promoCode
        minTransactionAmount =   retailerWalletPromoDataItem.minTransactionAmount!!

        var discountValue = retailerWalletPromoDataItem.discountValue
        var maxdicamount = retailerWalletPromoDataItem.maxDiscountAmount
        var mintxnamt= retailerWalletPromoDataItem.minTransactionAmount
         if(retailerWalletPromoDataItem.discountType.equals("Percentage")){
             val finalAmount = calculateDiscount(
                 amount = mintxnamt!!,
                 discountValue = discountValue!!,
                 isPercentage = true // false if flat
             )
             Log.d("redeemamt","$finalAmount")
              if(finalAmount<=maxdicamount!!){
                  binding.earnedamountforwithdraw.text = "₹ ${String.format("%.2f",finalAmount)}"
                  redeemAmount=finalAmount
              }else{
                  binding.earnedamountforwithdraw.text = "₹ ${String.format("%.2f",maxdicamount)}"
                  redeemAmount=maxdicamount
              }
         }
         else{
             val finalAmount = calculateDiscount(
                 amount = mintxnamt!!,
                 discountValue = discountValue!!,
                 isPercentage = false // false if flat
             )
             Log.d("redeemamt","$finalAmount")
             if(finalAmount<=maxdicamount!!){
                 binding.earnedamountforwithdraw.text = "₹ ${String.format("%.2f",finalAmount)}"
                 redeemAmount=finalAmount
             }else{
                 binding.earnedamountforwithdraw.text = "₹ ${String.format("%.2f",maxdicamount)}"
                 redeemAmount=maxdicamount
             }
         }


        binding.timealertlayout.visibility=View.GONE

        countDownTimer = startExpiryTimer(
            endDate =retailerWalletPromoDataItem.endDate!!,
            onTick = { timeText ->
                binding.expriedate.text = timeText
                binding.timealertlayout.visibility=View.VISIBLE

                if(totalTransactionAmount >= minTransactionAmount!!){
                    binding.redeemlayout.visibility= View.VISIBLE
                    binding.expiringlayout.visibility= View.GONE
                }
                else {
                    binding.expiringlayout.visibility= View.VISIBLE
                    binding.redeemlayout.visibility= View.GONE
                }
            },
            onExpire = {
                binding.expriedate.text = "Expired"
            }
        )

    }


    fun calculateDiscount(amount: Double, discountValue: Double, isPercentage: Boolean): Double {
        val discount = if (isPercentage) {
            amount * discountValue / 100
        } else {
            discountValue
        }
        return discount
    }


    fun hitApiForGetEligible() {
        var userCode = mStash!!.getStringValue(Constants.RegistrationId, "").toString()

        runIfConnected {
            val request = GetEligibleReq(
                fromDate = retailerWalletPromoDataItem.startDate,
                toDate = retailerWalletPromoDataItem.endDate,
                serviceCode = "",
                retailerId = userCode,
                operatorCode = "",
                subserviceCode = ""
            )

            Log.d("Eligiblereq", Gson().toJson(request))

            getAllApiServiceViewModel.GetRetailerWalletEligibleReq(request).observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    Constants.dialog.dismiss()
                                    if (response.isSuccess!!) {
                                        Log.d("eligibleresp", String.format("%.2f",response.totalTransactionAmount))
                                        binding.achievedamount.text = "₹ ${String.format("%.2f",response.totalTransactionAmount)}"
                                        setDataForProgress(response.totalTransactionAmount!!,retailerWalletPromoDataItem.minTransactionAmount!!)
                                        totalTransactionAmount = response.totalTransactionAmount
                                        minTransactionAmount =   retailerWalletPromoDataItem.minTransactionAmount!!

                                        if(response.totalTransactionAmount >= retailerWalletPromoDataItem.minTransactionAmount!!){
                                            binding.redeemlayout.visibility= View.VISIBLE
                                            binding.completetarget.visibility= View.VISIBLE
                                            binding.expiringlayout.visibility= View.GONE
                                            binding.showingprogresstarget.visibility= View.GONE

                                        }
                                        else{
                                            binding.redeemlayout.visibility= View.GONE
                                            binding.completetarget.visibility= View.GONE
                                            binding.expiringlayout.visibility= View.GONE
                                            binding.showingprogresstarget.visibility= View.VISIBLE
                                        }

                                    }
                                    else {
                                        binding.redeemlayout.visibility= View.GONE
                                        binding.completetarget.visibility= View.GONE
                                        binding.expiringlayout.visibility= View.GONE
                                        binding.showingprogresstarget.visibility= View.VISIBLE
                                        Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
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


    fun setDataForProgress(achievedamount:Double, minTransactionAmount:Double){
        val totalAmount =  minTransactionAmount
        val progressPercent = (achievedamount * 100) / totalAmount
        binding.progressBar.max = 100
        binding.progressBar.progress = if (achievedamount > 0 && progressPercent < 1) 1 else progressPercent.toInt()

    }


    fun setonClickListner(){
        binding.back.setOnClickListener {
            finish()
        }

        binding.redeemcard.setOnClickListener {
            openDialogForShowingCashbackData()

        }
    }


    private fun getTransferAmountToAgentInCommissionCal() {

        val transferAmountToAgentsReq = TransferToAgentReq(
            merchantCode = mStash!!.getStringValue(Constants.MerchantId, ""),
            transferFrom = "Admin",
            amountType = "Deposit",
            transIpAddress = mStash!!.getStringValue(Constants.deviceIPAddress, ""),
            remark = "Promo cashback redeemed deposit",
            transferTo = mStash!!.getStringValue(Constants.RegistrationId, ""),
            transferToMsg = "Congratulations! ₹ ${String.format("%.2f",redeemAmount)} has been credited to your wallet for Promo Code ${retailerWalletPromoDataItem.promoCode}",
            gstAmt = 0,
            parmUserName = mStash!!.getStringValue(Constants.RegistrationId, ""),
            servicesChargeGSTAmt = 0,
            servicesChargeWithoutGST = 0,
            actualTransactionAmount = redeemAmount ?: 0.0,
            actualCommissionAmt = 0,
            commissionWithoutGST = 0,
            transferFromMsg = "₹ ${String.format("%.2f",redeemAmount)} debited for promo cashback redemption. Promo Code ${retailerWalletPromoDataItem.promoCode} Beneficiary: ${mStash!!.getStringValue(Constants.RegistrationId, "")}",
            netCommissionAmt = 0,
            tdSAmt = 0.0,
            servicesChargeAmt = 0,
            customerVirtualAddress = "",
            transferAmt = redeemAmount ?: 0.0,
        )

        Log.d("transferAmountToAgentcommissionreq", Gson().toJson(transferAmountToAgentsReq))

        getAllApiServiceViewModel.transferToAgentReq(transferAmountToAgentsReq)
            .observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {

                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { commissionresp ->
                                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                                        Constants.dialog.dismiss()
                                    }

                                    HitApiForManageUsagePromoCode(commissionresp.data!!.refTransID!!)

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
                            Constants.OpenPopUpForVeryfyOTP(this)
                        }

                    }
                }
            }

         }


    @SuppressLint("SetTextI18n")
    fun openDialogForShowingCashbackData()  {
        dialog = Dialog(this@RetailerPromocodeDetailsPage, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.promowalletamounttransferalert)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        dialog.setCanceledOnTouchOutside(false)

        val promovalue = dialog.findViewById<TextView>(R.id.promocodevalue)
        val cashbackamount = dialog.findViewById<TextView>(R.id.cashbackamount)
        val cashbackDataCard = dialog.findViewById<CardView>(R.id.cashbackdata)
        val confirmationPayoutCard = dialog.findViewById<CardView>(R.id.confirmationPayout)

        val cancel = dialog.findViewById<Button>(R.id.btncancel)
        val done = dialog.findViewById<Button>(R.id.btnDone)

        cashbackDataCard.visibility=View.VISIBLE
        confirmationPayoutCard.visibility=View.GONE

         promovalue.text = retailerWalletPromoDataItem.promoCode
         cashbackamount.text = "₹ ${String.format("%.2f",redeemAmount)}"

        done.setOnClickListener {
            openDialogForPayoutConfirmation()

        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show() // ✅ REQUIRED
    }


    @SuppressLint("SetTextI18n")
    fun openDialogForPayoutConfirmation()  {

        val cashbackDataCard = dialog.findViewById<CardView>(R.id.cashbackdata)
        val confirmationPayoutCard = dialog.findViewById<CardView>(R.id.confirmationPayout)
        val cancel = dialog.findViewById<Button>(R.id.cancel)
        val done = dialog.findViewById<Button>(R.id.transfer)


        cashbackDataCard.visibility=View.GONE
        confirmationPayoutCard.visibility=View.VISIBLE

        done.setOnClickListener {
            getTransferAmountToAgentInCommissionCal()
            dialog.dismiss()
        }

        cancel.setOnClickListener {
            cashbackDataCard.visibility=View.VISIBLE
            confirmationPayoutCard.visibility=View.GONE
        }

        dialog.show() // ✅ REQUIRED
    }


    fun finalSuccessDialog(){
        var dialog = Dialog(this@RetailerPromocodeDetailsPage, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.successalertpromocode)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        dialog.setCanceledOnTouchOutside(false)

        val msgtxt = dialog.findViewById<TextView>(R.id.msgtxt)
        val done = dialog.findViewById<Button>(R.id.transfer)

        msgtxt.text = "₹ ${String.format("%.2f",redeemAmount)} successfully transferred to your wallet."


        done.setOnClickListener {
            finish()
            dialog.dismiss()
        }

        dialog.show() // ✅ REQUIRED
    }


    private fun HitApiForManageUsagePromoCode(requestId:String) {

        val managePromoUsageReq = ManagePromoUsageReq(
            serviceType = "PROMO_CASHBACK",
            fromDate = null,
            taskType = "INS_USAGE",
            transactionAmount = redeemAmount ?: 0.0,
            toDate = null,
            promoCode = retailerWalletPromoDataItem.promoCode,
            retailerCode = mStash!!.getStringValue(Constants.RegistrationId, ""),
            discountApplied = redeemAmount ?: 0.0,
            transactionID = requestId,
            remarks = "Promo cashback redeemed and credited",
            status = "Applied",
        )

        Log.d("transferAmountToAgentcommissionreq", Gson().toJson(managePromoUsageReq))

        getAllApiServiceViewModel.GetManagePromoUsageReq(managePromoUsageReq)
            .observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {

                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { commissionresp ->
                                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                                        Constants.dialog.dismiss()
                                    }
                                    finalSuccessDialog()
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
                            Constants.OpenPopUpForVeryfyOTP(this)
                        }

                    }
                }
            }
    }


}