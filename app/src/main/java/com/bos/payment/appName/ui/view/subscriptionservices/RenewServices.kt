package com.bos.payment.appName.ui.view.subscriptionservices

import android.annotation.SuppressLint
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
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.justpaymodel.SendMoneyToMobileReqModel
import com.bos.payment.appName.data.model.managekyc.CountryStateDistrictReq
import com.bos.payment.appName.data.model.managekyc.StateDistDataItem
import com.bos.payment.appName.data.model.managekyc.UpdateKycReq
import com.bos.payment.appName.data.model.subscription.BillingCostReq
import com.bos.payment.appName.data.model.subscription.FeatureDataItem
import com.bos.payment.appName.data.model.subscription.FeatureLinkReq
import com.bos.payment.appName.data.model.subscription.FeatureListReq
import com.bos.payment.appName.data.model.subscription.SubscriptionUserDeatilsReq
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.GetBalanceReq
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.GetBalanceRes
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.databinding.ActivityRenewServicesBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.adapter.FeatureListAdapter
import com.bos.payment.appName.ui.view.Dashboard.activity.ManageKYC.Companion.Aadhardist
import com.bos.payment.appName.ui.view.LoginActivity
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Constants.BillRechargeCard
import com.bos.payment.appName.utils.Constants.FinanceCard
import com.bos.payment.appName.utils.Constants.TravelCard
import com.bos.payment.appName.utils.Constants.uploadDataOnFirebaseConsole
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.bos.payment.appName.utils.Utils.toast
import com.google.gson.Gson
import java.util.Locale

class RenewServices : AppCompatActivity(), FeatureListAdapter.ClickOnItem {
    lateinit var binding: ActivityRenewServicesBinding
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    private var mStash: MStash? = null
    private var featureDataList :  MutableList<FeatureDataItem?>? = mutableListOf()
    lateinit var featureAdapter : FeatureListAdapter

    var durationList: List<StateDistDataItem?>? = arrayListOf()
    var durationDisplayName: ArrayList<String?>? = arrayListOf()
    var durationCodeName: ArrayList<String?>? = arrayListOf()
    var durationCode : String =""

    var featureList: List<StateDistDataItem?>? = arrayListOf()
    var featureDisplayName: ArrayList<String?>? = arrayListOf()
    var featureCodeName: ArrayList<String?>? = arrayListOf()
    var featureCode : String = ""
    var companyCode : String =""
    var companyTypeCode : String =""
    var FeatureLinkCode : String =""
    var walletBalance : String =""
    lateinit var dialog: Dialog


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRenewServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setonClickListner()
        setStatus()
        getWalletBalance()
        hitApiForRetailerDetails()

    }

    fun init(){
        mStash = MStash.getInstance(this@RenewServices)
        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(
            GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]



    }

    fun setStatus(){
        val arraySpinner = resources.getStringArray(R.array.status)
        val genderadapters = ArrayAdapter(this@RenewServices, R.layout.spinner_right_aligned, arraySpinner)
        genderadapters.setDropDownViewResource(R.layout.spinner_right_aligned)
        binding.status.adapter = genderadapters
    }

    private fun hitApiForRetailerDetails(){
        var userCode = mStash!!.getStringValue(Constants.RegistrationId, "").toString()

        val request = SubscriptionUserDeatilsReq(
            userId = userCode
        )

        Log.d("SubscriptionReq", Gson().toJson(request))

        getAllApiServiceViewModel.subscriptionDetailsReq(request).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("SubscriptionResponse", Gson().toJson(response))

                                if (response.isSuccess!!) {
                                    companyTypeCode = response.data!![0]!!.companyTypeCode!!
                                   binding.companyname.text = response.data!![0]!!.companyNameText.toString()
                                   binding.username.text = response.data!![0]!!.userFullName.toString()
                                   binding.checkkyc.text = response.data!![0]!!.kycUpdate.toString()
                                   binding.subscriptionname.text = response.data!![0]!!.companyTypeName.toString()
                                   if(response.data!![0]!!.kycUpdate.toString().equals("Verified"))  {
                                   binding.verifiedkyc.setImageResource(R.drawable.doneicon)
                                   }
                                   else{
                                       binding.verifiedkyc.setImageResource(R.drawable.pending)
                                   }

                                }
                                else {
                                    Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    ApiStatus.ERROR -> {

                    }

                    ApiStatus.LOADING -> {
                    }

                }
            }
        }

    }

    private fun hitApiForFeatureList(status:String){

        var userCode = mStash!!.getStringValue(Constants.RegistrationId, "").toString()
        var merchantCode = mStash!!.getStringValue(Constants.MerchantId,"")

        val request = FeatureListReq(
            companyCode = userCode,
            flag = "SHOW",
            merchantcode = merchantCode,
            companyFeatureCode = "",
            featureName = null,
            status = status

        )

        Log.d("FeatureReq", Gson().toJson(request))

        getAllApiServiceViewModel.featureListReq(request).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("featureResponse", Gson().toJson(response))
                                if(Constants.dialog!=null && Constants.dialog.isShowing){
                                    Constants.dialog.dismiss()
                                }
                                if (response.isSuccess!!) {
                                    featureDataList = response.data
                                    showDataOnView(featureDataList)

                                }
                                else {
                                    binding.notfoundimage.visibility=View.VISIBLE
                                    binding.showfeatures.visibility=View.GONE
                                    Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                            Constants.dialog.dismiss()
                        }
                        binding.notfoundimage.visibility=View.VISIBLE
                        binding.showfeatures.visibility=View.GONE
                    }

                    ApiStatus.LOADING -> {
                        Constants.OpenPopUpForVeryfyOTP(this)
                    }

                }
            }
        }
    }

    private fun showDataOnView(datalist:MutableList<FeatureDataItem?>?){
        if(datalist!!.isNotEmpty()){
          binding.notfoundimage.visibility=View.GONE
          binding.showfeatures.visibility=View.VISIBLE
            featureAdapter = FeatureListAdapter(this@RenewServices,datalist,this)
            binding.showfeatures.adapter = featureAdapter
            featureAdapter.notifyDataSetChanged()
        }
        else {
            binding.notfoundimage.visibility=View.VISIBLE
            binding.showfeatures.visibility=View.GONE
        }

    }

    private fun getWalletBalance() {
        this.runIfConnected {
            val walletBalanceReq = GetBalanceReq(
                parmUser = mStash!!.getStringValue(Constants.RegistrationId, ""),
                flag = "CreditBalance"
            )
            Log.d("checkWallet", Gson().toJson(walletBalanceReq))
            getAllApiServiceViewModel.getWalletBalance(walletBalanceReq).observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            //Constants.dialog.dismiss()
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    Log.d("checkwalletresp", Gson().toJson(response))
                                    getAllWalletBalanceRes(response)
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            Constants.dialog.dismiss()
                        }

                        ApiStatus.LOADING -> {

                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun getAllWalletBalanceRes(response: GetBalanceRes) {
        val data = Gson().toJson(response)

        uploadDataOnFirebaseConsole(data,"DashboardWalletBalance",this@RenewServices)

        if (response.isSuccess == true) {
            binding.walletBalance.text = "₹" + response.data[0].result.toString()
            walletBalance = response.data[0].result.toString()
            Log.d("actualBalance", response.data[0].result.toString())
        }
        else {
            toast(response.returnMessage.toString())
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setonClickListner(){

        binding.back.setOnClickListener {
            finish()
        }

        binding.status.onItemSelectedListener = object :OnItemSelectedListener{


            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
               var selectedItem = binding.status.selectedItem.toString()
                if(selectedItem.equals(Constants.Active)){
                    hitApiForFeatureList("Y")
                }else{
                    hitApiForFeatureList("N")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        binding.featurename.addTextChangedListener(object :TextWatcher{


            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()

                val filteredList = if (query.isEmpty()) {
                    featureDataList
                }
                else {
                    featureDataList?.filter { it?.featureName
                        ?.lowercase()
                        ?.contains(query) == true
                        }?.toMutableList()
                }

                showDataOnView(filteredList)
            }


            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.cancelbutton.setOnClickListener {
            binding.renweLayout.visibility=View.GONE
            binding.featureListLayout.visibility=View.VISIBLE
        }

        binding.duration.onItemSelectedListener = object : OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                durationCode =  durationCodeName!![position]!!
                hitApiForBilling()
                Log.d("durationcode",durationCode)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        binding.renewbutton.setOnClickListener {
            var amount = binding.billingamount.text.toString().toDouble()?:0.0
            var walletamount = walletBalance.toDouble()?:0.0
            if(amount>0.0){
                if(amount<=walletamount){
                    OpenPopUpForVAlert()
                }
                else{
                    Toast.makeText(this,"Your wallet balance is insufficient to renew your subscription",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this,"Proceeding is not allowed as the billing amount must be greater than zero.",Toast.LENGTH_LONG).show()
            }

        }

    }

    override fun itemClick(item: FeatureDataItem) {
        binding.renweLayout.visibility=View.VISIBLE
        binding.featureListLayout.visibility=View.GONE
        companyCode= item.companyCode!!
        FeatureLinkCode = item.cFeatureLinkCode!!
        Log.d("companycode",companyCode)
        hitApiForDuration("BLP")
        hitApiForFeature(item.featureName!!)
    }

    fun hitApiForDuration(flag:String){
        var request = CountryStateDistrictReq(
            pParmFlag = "MCODE",
            pParmFlag1 =flag,
            pParmFlag2 = ""
        )

        getAllApiServiceViewModel.countryStateDistrictListReq(request).observe(this){
                resources-> resources.let {
            when(it.apiStatus){

                ApiStatus.SUCCESS -> {
                    it.data?.let { users ->
                        users.body()?.let { response ->
                            Log.d("durationresp", Gson().toJson(response))
                            if (response.isSuccess!!) {
                                durationList= response.data
                                setDurationForView(durationList)
                            }
                            else {
                                 Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }

                ApiStatus.ERROR -> {

                }

                ApiStatus.LOADING -> {

                }
            }
         }
        }
    }


    fun setDurationForView( durationList : List<StateDistDataItem?>?){
        durationDisplayName!!.clear()
        durationCodeName!!.clear()


        durationList!!.forEach { it->
           durationDisplayName!!.add(it!!.displayText)
           durationCodeName!!.add(it!!.displayValue)
        }

        val genderadapters = ArrayAdapter(this@RenewServices, R.layout.spinner_right_aligned, durationDisplayName!!)
        genderadapters.setDropDownViewResource(R.layout.spinner_right_aligned)
        binding.duration.adapter = genderadapters
    }


    fun hitApiForFeature(featureName: String){
        var request = CountryStateDistrictReq(
            pParmFlag = "SFLIST",
            pParmFlag1 =companyTypeCode,
            pParmFlag2 = ""
        )

        getAllApiServiceViewModel.countryStateDistrictListReq(request).observe(this){
                resources-> resources.let {
            when(it.apiStatus){

                ApiStatus.SUCCESS -> {
                    it.data?.let { users ->
                        users.body()?.let { response ->
                            Log.d("featurerespresp", Gson().toJson(response))
                            if (response.isSuccess!!) {
                                featureList= response.data
                                setFeatureForView(featureList,featureName)
                            }
                            else {
                                Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }

                ApiStatus.ERROR -> {

                }

                ApiStatus.LOADING -> {

                }
            }
          }
        }
    }


    fun setFeatureForView( durationList : List<StateDistDataItem?>?,featureName: String){
        featureDisplayName!!.clear()
        featureCodeName!!.clear()

        durationList!!.forEach { it->
            featureDisplayName!!.add(it!!.displayText)
            featureCodeName!!.add(it!!.displayValue)
        }

        val genderadapters = ArrayAdapter(this@RenewServices, R.layout.spinner_right_aligned, featureDisplayName!!)
        genderadapters.setDropDownViewResource(R.layout.spinner_right_aligned)
        binding.featureName.adapter = genderadapters

        if(featureName.isNotEmpty()){
            featureDisplayName!!.forEachIndexed { index, item ->
                if (item.equals(featureName, ignoreCase = true)) {
                    binding.featureName.setSelection(index)
                    featureCode = featureCodeName!![index]!!
                    Log.d("featurecode", featureCode)
                    return@forEachIndexed
                }
            }
        }

        binding.featureName.isEnabled = false

    }


    fun hitApiForBilling(){

        var request = BillingCostReq(
            companyCode = companyCode,
            companyFeatureCode = FeatureLinkCode,
            paramFlag = durationCode
        )

        getAllApiServiceViewModel.billingCostReq(request).observe(this){
                resources-> resources.let {
            when(it.apiStatus){

                ApiStatus.SUCCESS -> {
                    it.data?.let { users ->
                        users.body()?.let { response ->
                            Log.d("billingpresp", Gson().toJson(response))
                            if (response.isSuccess!!) {
                               var billingamount = response.data!![0]!!.billingCost
                               binding.billingamount.text = billingamount

                            }
                            else {
                                Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }

                ApiStatus.ERROR -> {

                }

                ApiStatus.LOADING -> {

                }
            }
        }
        }
    }


    fun hitApiForWalletTransfer(){
        var request = SendMoneyToMobileReqModel(
            actualTransactionAmount = binding.billingamount.text.toString().toDouble()?:0.0,
            flag = "MINUS",
            amountType = "WithDraw",
            transferFromMsg = "Your wallet has been debited for the renewal of the ${binding.duration.selectedItem.toString()} ${binding.featureName.selectedItem.toString()} subscription.",
            transIpAddress = mStash!!.getStringValue(Constants.deviceIPAddress,""),
            remark = "Amount deducted towards subscription charge",
            transferTo = mStash!!.getStringValue(Constants.RegistrationId,""),
            transferToMsg = "Your wallet has been credited for the renewal of the ${binding.duration.selectedItem.toString()} ${binding.featureName.selectedItem.toString()} subscription.",
            transferAmt = binding.billingamount.text.toString().toDouble()?:0.0,
            parmUserName = mStash!!.getStringValue(Constants.RegistrationId,""),
            merchantCode = mStash!!.getStringValue(Constants.MerchantId,"")
        )
        Log.d("walletTransferreq", Gson().toJson(request))

        getAllApiServiceViewModel.sendMoneyToMobileReqModel(request).observe(this){
                resources-> resources.let {
            when(it.apiStatus){
                ApiStatus.SUCCESS -> {
                    it.data?.let { users ->
                        users.body()?.let { response ->
                            Log.d("walletTransferresp", Gson().toJson(response))
                            if (response.isSuccess!!) {
                                hitApiForFeatureLink()
                            }
                            else {
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

        cancel.text="No"
        done.text="Yes"
        txt.text = "Do you want to renew your subscription?"

        done.setOnClickListener {
            hitApiForWalletTransfer()
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }


    fun hitApiForFeatureLink(){
        var featureCodee = featureCode.split("|"," ")[0]

        var request = FeatureLinkReq(
            companyCode = companyCode,
            limitValue = 0,
            flag = "RENEW",
            featureCode = featureCodee,
            featureDuration  = durationCode,
            loginId = mStash!!.getStringValue(Constants.RegistrationId,""),
            companyFeatureCode = FeatureLinkCode,
            programCode = "",
            smsServiceType = "",
            smsDirection ="",
            remarks  = ""
        )

        Log.d("walletTransferreq", Gson().toJson(request))

        getAllApiServiceViewModel.featureLinkReq(request).observe(this){
                resources-> resources.let {
            when(it.apiStatus){
                ApiStatus.SUCCESS -> {
                    it.data?.let { users ->
                        users.body()?.let { response ->
                            Log.d("walletTransferresp", Gson().toJson(response))
                            if (response.isSuccess!!) {
                                if(Constants.dialog!=null && Constants.dialog.isShowing){
                                    Constants.dialog.dismiss()
                                }
                                dialog.dismiss()
                                if(binding.status.selectedItem.toString().trim().equals(Constants.Active)){
                                    hitApiForFeatureList("Y")
                                }else{
                                    hitApiForFeatureList("N")
                                }
                                getWalletBalance()
                                binding.renweLayout.visibility=View.GONE
                                binding.featureListLayout.visibility=View.VISIBLE
                                Toast.makeText(this,response.returnMessage,Toast.LENGTH_SHORT).show()

                            }
                            else {
                                Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }

                ApiStatus.ERROR -> {
                    Constants.dialog.dismiss()
                }

                ApiStatus.LOADING -> {

                }
            }

        }

        }

    }




}