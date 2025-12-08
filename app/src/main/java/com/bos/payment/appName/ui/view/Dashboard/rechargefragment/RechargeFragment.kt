package com.bos.payment.appName.ui.view.Dashboard.rechargefragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bos.payment.appName.R
import com.bos.payment.appName.adapter.ViewPlanListAdapter
import com.bos.payment.appName.adapter.ViewPlanListLatestAdapter
import com.bos.payment.appName.constant.ConstantClass
import com.bos.payment.appName.constant.CustomFuseLocationActivity
import com.bos.payment.appName.data.model.fastTag.billPayment.BillPaymentPaybillReq
import com.bos.payment.appName.data.model.fastTag.customerDetails.BillFetchDetails
import com.bos.payment.appName.data.model.fastTag.customerDetails.FetchConsumerDetailsReq
import com.bos.payment.appName.data.model.fastTag.fastTagOperator.FastTagOperatorsListRes
import com.bos.payment.appName.data.model.fastTag.recharge.BillFetch
import com.bos.payment.appName.data.model.fastTag.recharge.FastTagRechargeReq
import com.bos.payment.appName.data.model.fastTag.viewBillPayment.FetchBilPaymentDetailsReq
import com.bos.payment.appName.data.model.fastTag.viewBillPayment.FetchBilPaymentDetailsRes
import com.bos.payment.appName.data.model.merchant.apiServiceCharge.GetAPIServiceChargeReq
import com.bos.payment.appName.data.model.merchant.apiServiceCharge.mobileCharge.GetCommercialReq
import com.bos.payment.appName.data.model.merchant.apiServiceCharge.mobileCharge.GetCommercialRes
import com.bos.payment.appName.data.model.recharge.BillOperationPaymentReq
import com.bos.payment.appName.data.model.recharge.BillOperationPaymentRes
import com.bos.payment.appName.data.model.recharge.Data
import com.bos.payment.appName.data.model.recharge.mobile.MobileCheckReq
import com.bos.payment.appName.data.model.recharge.mobile.MobileRechargePlanModel
import com.bos.payment.appName.data.model.recharge.mobile.Plan
import com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.DataItemOperator
import com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.MobileRechargeRespo
import com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.MobileWiseRechargeReq
import com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.RechargeCategoryReq
import com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.RechargeOperatorsReq
import com.bos.payment.appName.data.model.recharge.operator.RechargeOperatorsListReq
import com.bos.payment.appName.data.model.recharge.operator.RechargeOperatorsListRes
import com.bos.payment.appName.data.model.recharge.recharge.DthInfoReq
import com.bos.payment.appName.data.model.recharge.recharge.RechargeapiresponseReq
import com.bos.payment.appName.data.model.recharge.recharge.TransferToAgentReq
import com.bos.payment.appName.data.model.recharge.recharge.UploadRechargeMobileRespReq
import com.bos.payment.appName.data.model.recharge.recharge.UploadRechargeMobileRespRespReq
import com.bos.payment.appName.data.model.transferAMountToAgent.TransferAmountToAgentsReq
import com.bos.payment.appName.data.model.transferAMountToAgent.TransferAmountToAgentsRes
import com.bos.payment.appName.data.model.walletBalance.merchantBal.GetMerchantBalanceReq
import com.bos.payment.appName.data.model.walletBalance.merchantBal.GetMerchantBalanceRes
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.GetBalanceReq
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.GetBalanceRes
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.repository.MobileRechargeRepository
import com.bos.payment.appName.data.repository.MoneyTransferRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.data.viewModelFactory.MobileRechargeViewModelFactory
import com.bos.payment.appName.data.viewModelFactory.MoneyTransferViewModelFactory
import com.bos.payment.appName.databinding.FragmentRechargeBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.adapter.DTHViewInfoAdapter
import com.bos.payment.appName.ui.adapter.RechargePlanNameAdapter
import com.bos.payment.appName.ui.view.Dashboard.rechargeactivity.RechargeSuccessfulPageActivity
import com.bos.payment.appName.ui.view.Dashboard.rechargeactivity.RechargeSuccessfulPageActivity.Companion.Datetime
import com.bos.payment.appName.ui.view.Dashboard.rechargeactivity.RechargeSuccessfulPageActivity.Companion.mobileNumber
import com.bos.payment.appName.ui.view.Dashboard.rechargeactivity.RechargeSuccessfulPageActivity.Companion.operatorLogo
import com.bos.payment.appName.ui.view.Dashboard.rechargeactivity.RechargeSuccessfulPageActivity.Companion.orderID
import com.bos.payment.appName.ui.view.Dashboard.rechargeactivity.RechargeSuccessfulPageActivity.Companion.planPrice
import com.bos.payment.appName.ui.view.Dashboard.rechargeactivity.RechargeSuccessfulPageActivity.Companion.rechargeStatus
import com.bos.payment.appName.ui.view.Dashboard.rechargeactivity.RechargeSuccessfulPageActivity.Companion.referenceId
import com.bos.payment.appName.ui.view.Dashboard.rechargeactivity.RechargeSuccessfulPageActivity.Companion.serviceChargeWithGST
import com.bos.payment.appName.ui.view.Dashboard.rechargeactivity.RechargeSuccessfulPageActivity.Companion.totalTransaction
import com.bos.payment.appName.ui.view.Dashboard.rechargeactivity.RechargeSuccessfulPageActivity.Companion.transactionID
import com.bos.payment.appName.ui.view.LoginActivity
import com.bos.payment.appName.ui.viewmodel.AttendanceViewModel
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.ui.viewmodel.GetAllMobileRechargeViewModel
import com.bos.payment.appName.ui.viewmodel.MoneyTransferViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Constants.getCurrentIsoDate
import com.bos.payment.appName.utils.Constants.uploadDataOnFirebaseConsole
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.PD
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.bos.payment.appName.utils.Utils.toast
import com.example.example.FetchConsumerDetailsRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RechargeFragment : Fragment() {
    private lateinit var binding: FragmentRechargeBinding
    private lateinit var context: Context
    private var selectedModel: String = ""
    private var selectedAllOperator: String = ""
    private var selectedOperatorDTHName: String = ""
    private var OperatorID: String = ""
    private lateinit var selectedFastTagModel: com.bos.payment.appName.data.model.fastTag.fastTagOperator.Data
    private lateinit var spinnerType: String
    private lateinit var adapter: ViewPlanListAdapter
    private var oeratorList = ArrayList<Data>()
    private var getAllOperatorList = ArrayList<com.bos.payment.appName.data.model.recharge.operator.Data>()
    private var rechargeType: String = ""
    private var featureCode: String = ""
   // private lateinit var pd: AlertDialog
    private var customFuseLocation: CustomFuseLocationActivity? = null
    private var bill_model = FetchConsumerDetailsRes()
    private var mStash: MStash? = null
    private lateinit var viewModel: MoneyTransferViewModel
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    private lateinit var MobileRechargeViewModel: GetAllMobileRechargeViewModel
    private val PICK_CONTACT_REQUEST = 1
    private val REQUEST_CONTACT_PERMISSION = 2
    private var bill_rec_model = BillPaymentPaybillReq()

    // Latest object for new update......................................................................................
    var mob: String = ""
    var operatorsNameList: MutableList<DataItemOperator> = mutableListOf()
    lateinit var RechargePlanNameAdapter: RechargePlanNameAdapter

    companion object {
        var DisplayName: String? = ""
    }

    var mobileRechargePlanList: MutableList<MobileRechargePlanModel> = mutableListOf()
    var filterMobileRechargePlanList: MutableList<MobileRechargePlanModel> = mutableListOf()
    var mobilePlan: MutableList<Plan> = mutableListOf()

    var operatorNameDTHList: MutableList<Pair<String, Int>> = mutableListOf()
    var operatorNameMobileList: MutableList<Pair<String, Int>> = mutableListOf()

    var apiCalled: Boolean = false

    var DthInfoList: MutableList<com.bos.payment.appName.data.model.recharge.recharge.DataItem> = mutableListOf()

    private var lastTriggeredBy: String? = null

    lateinit var dialog: Dialog

    // for successful Page after Mobile Recharge................................

    lateinit var operatorlogo: Drawable


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRechargeBinding.inflate(inflater, container, false)
        context = requireContext()
        rechargeType = arguments?.getString("RechargeType").toString()
        featureCode = arguments?.getString("FeatureCode").toString()

        getFuseLocation()

        /*if (rechargeType == "FastTag") {
            getFastTagList()
        }
        else {*/
        initView()
        if (rechargeType == "mobile" || rechargeType == "dth") {
            // new changes.............................................
            val apiName = "Recharge Api"
            getAllOperatorList(apiName)
            setOperatorNameForDTH()
            hitApiForRechargeCategory()
        }
        else {
            val apiName = "Bill Payment Api"
            getOperatorList(apiName)
        }
        /* }*/

        if (!isUserLoggedIn()) {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

        btnListener()


        return binding.root

    }


    fun setOperatorNameForDTH(): MutableList<Pair<String, Int>> {
        operatorNameDTHList.clear()
        operatorNameDTHList.add(Pair("Airteldth", 522))
        operatorNameDTHList.add(Pair("TataSky", 525))
        operatorNameDTHList.add(Pair("Videocon", 526))
        operatorNameDTHList.add(Pair("SunDirect", 524))
        operatorNameDTHList.add(Pair("DishTV", 523))
        return operatorNameDTHList
    }


    fun setMobileOperatorNameWithProductID(): MutableList<Pair<String, Int>> {
        operatorNameMobileList.clear()
        operatorNameMobileList.add(Pair("Airtel Prepaid", 518))
        operatorNameMobileList.add(Pair("BSNL Prepaid", 521))
        operatorNameMobileList.add(Pair("Reliance Jio Prepaid", 519))
        operatorNameMobileList.add(Pair("VI Prepaid", 520))

        return operatorNameMobileList
    }


    private fun isUserLoggedIn(): Boolean {
        // Add your logic to check if the user is logged in (e.g., check SharedPreferences)
        return mStash!!.getBoolanValue(Constants.IS_LOGIN.toString(), false)
    }


    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun btnListener() {

        // Set up the SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Filter the adapter when query is submitted
                query?.let {
                    if(mobileRechargePlanList.size>0&&!it.isNullOrBlank()){
                        filterMobileRechargePlanList= filterPlansByRs(mobileRechargePlanList,
                            it.toIntOrNull()!!
                        )
                        if(filterMobileRechargePlanList.size>0){
                            setSearchPlanMobile(filterMobileRechargePlanList)
                        }else{
                            Toast.makeText(context,"No Plan Found",Toast.LENGTH_SHORT).show()
                        }

                    }
                    else {
                        setSearchPlanMobile(mobileRechargePlanList)
                    }


                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the adapter as the user types
                newText?.let {
                    if(mobileRechargePlanList.size>0&&!it.isNullOrBlank()){
                        filterMobileRechargePlanList= filterPlansByRs(mobileRechargePlanList,
                            it.toIntOrNull()!!
                        )
                        if(filterMobileRechargePlanList.size>0){
                            setSearchPlanMobile(filterMobileRechargePlanList)
                        }else{
                            Toast.makeText(context,"No Plan Found",Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        setSearchPlanMobile(mobileRechargePlanList)
                    }
                }
                return false
            }


        })


        binding.etAmount.addTextChangedListener(object :TextWatcher{
           override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

           }

           override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               binding.llViewBill.visibility = View.GONE
               binding.tvAmount2.visibility = View.GONE
               binding.etAmount2.visibility = View.GONE
               binding.userNameText.visibility = View.GONE
               binding.layoutDetails.visibility = View.GONE
               binding.userNameEditText.visibility = View.GONE
               binding.arrowImg.animate().rotation(0f).setDuration(200).start()
           }

           override fun afterTextChanged(s: Editable?) {

           }

       })

        binding.etMobileNumber.addTextChangedListener(object : TextWatcher {

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable) {
                    if (s.length == 10) {
                        binding.llViewPlan.visibility = View.VISIBLE
                    } else {
                        binding.llViewPlan.visibility = View.GONE
                        binding.etAmount.setText("")
                        binding.spOperator.setSelection(0)
                        //binding.etCircle.setText("")
                    }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length == 10) {
                    mob = binding.etMobileNumber.text.toString().trim()
                    hitApiForMobileWiseOperatorName(mob)
                    hideKeyboard(binding.etMobileNumber)
                }
                else {
                    if (s.length == 0) {
                        hitApiForRechargeOperatorNameList(DisplayName!!)
                    }

                }
            }

        })

        binding.tvBtnViewPlan.setOnClickListener {
            viewPlanList()
        }

        binding.etDTHBillNumber.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable) {
                if (binding.etDTHBillNumber.length() > 0) {
                    if (binding.etDTHBillNumber.text.length >= 10) {
                        lastTriggeredBy = "circle"
                        CheckAndHitApi()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        binding.etMobileNumber.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.etMobileNumber.right - binding.etMobileNumber.compoundDrawables[2].bounds.width())) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.READ_CONTACTS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.READ_CONTACTS),
                            REQUEST_CONTACT_PERMISSION
                        )

                    } else {
                        pickContact()
                    }
                    return@setOnTouchListener true
                }
            }
            false
        }

        binding.cancelBtn.setOnClickListener {
            binding.llViewBill.visibility = View.GONE
        }

        // mobile recharge
        binding.tvBtnProceed.setOnClickListener {
            validation()
        }

        binding.tvBtnProceedBill.setOnClickListener {
            //showUpdateKycDetails(context)
            var bbpsrechargeamt =   binding.etAmount2.text.toString().trim()
            if (bbpsrechargeamt > "0.00") {
                //getAllBillServiceCharge(response.amount.toString())
               // getAllServiceChargeRetailer(bbpsrechargeamt)
                Toast.makeText(requireContext(),"Failed to process the payment",Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(requireContext(),"All dues are paid!!",Toast.LENGTH_LONG).show()
            }
        }

    }

    fun filterPlansByRs(list: List<MobileRechargePlanModel>, amount: Int): MutableList<MobileRechargePlanModel> {
        return list.mapNotNull { item ->
            val filteredPlans = item.plans.filter { it.rs == amount }
            if (filteredPlans.isNotEmpty()) {
                item.copy(plans = filteredPlans)    // return arrayName + matched plans
            } else {
                null
            }
        }.toMutableList()
    }


    private fun pickContact() {
        val contactPickerIntent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(contactPickerIntent, PICK_CONTACT_REQUEST)
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            val contactUri: Uri? = data?.data
            val projection: Array<String> = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)

            contactUri?.let { uri ->
                try {
                    // Use `this.contentResolver` in an Activity
                    val cursor: Cursor? =
                        requireContext().contentResolver.query(uri, projection, null, null, null)
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val numberIndex: Int =
                                it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            var number: String = it.getString(numberIndex)

                            // Remove spaces
                            number = number.replace("\\s".toRegex(), "")

                            if (number.startsWith("+91")) {
                                number = number.substring(3)
                            }
                            binding.etMobileNumber.setText(number)
                            binding.etMobileNumber.addTextChangedListener(object : TextWatcher {
                                override fun afterTextChanged(s: Editable) {
                                    if (binding.etMobileNumber.length() == 10) {
                                        getOperatorName(binding.etMobileNumber.text.toString().trim(), "mobile")
                                        hideKeyboard(binding.etMobileNumber)
                                    }
                                }

                                override fun beforeTextChanged(
                                    s: CharSequence,
                                    start: Int,
                                    count: Int,
                                    after: Int
                                ) {
                                }

                                override fun onTextChanged(
                                    s: CharSequence,
                                    start: Int,
                                    before: Int,
                                    count: Int
                                ) {
                                }
                            })
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Failed to load contact", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CONTACT_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickContact()
            } else {
                Toast.makeText(requireContext(), "Permission denied to read contacts", Toast.LENGTH_SHORT).show()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun initView() {
       // pd = PD(requireContext())
        mStash = MStash.getInstance(requireActivity())

        bill_model = FetchConsumerDetailsRes()
        selectedFastTagModel = com.bos.payment.appName.data.model.fastTag.fastTagOperator.Data()

        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]

        MobileRechargeViewModel = ViewModelProvider(this, MobileRechargeViewModelFactory(MobileRechargeRepository(RetrofitClient.apiRechargeInterface)))[GetAllMobileRechargeViewModel::class.java]

        viewModel = ViewModelProvider(this, MoneyTransferViewModelFactory(MoneyTransferRepository(RetrofitClient.apiAllAPIService)))[MoneyTransferViewModel::class.java]

        ShowHideUtils()
    }


    private fun ShowHideUtils(){
        binding.etDTHBillNumber.visibility = View.GONE

        Constants.operatorName = ArrayList()
        Constants.dthName = ArrayList()
        Constants.emiNo = ArrayList()
        Constants.gasName = ArrayList()
        Constants.insuranceName = ArrayList()
        Constants.electricityName = ArrayList()
        Constants.prepaidName = ArrayList()
        Constants.waterName = ArrayList()
        Constants.broadBandName = ArrayList()
        Constants.landLineName = ArrayList()
        Constants.municipalityName = ArrayList()
        Constants.fastTagName = ArrayList()

        Constants.operatorName!!.add("Select Mobile Operator")
        Constants.dthName!!.add("Select DTH Operator")
        Constants.emiNo!!.add("Select Loan Operator")
        Constants.gasName!!.add("Select Gas Operator")
        Constants.insuranceName!!.add("Select Insurance Operator")
        Constants.electricityName!!.add("Select Electricity Operator")
        Constants.prepaidName!!.add("Select Prepaid Mobile Operator")
        Constants.waterName!!.add("Select Water Operator")
        Constants.broadBandName!!.add("Select Broadband Operator")
        Constants.landLineName!!.add("Select LandLine Operator")
        Constants.municipalityName!!.add("Select Municipality Operator")
        Constants.fastTagName!!.add("Select FastTag Operator")

        Constants.operatorNameMap = HashMap()
        Constants.dthNameMap = HashMap()
        Constants.emiNoMap = HashMap()
        Constants.gasNameMap = HashMap()
        Constants.insuranceNameMap = HashMap()
        Constants.electricityNameMap = HashMap()
        Constants.prepaidNameMap = HashMap()
        Constants.waterNameMap = HashMap()
        Constants.broadBandNameMap = HashMap()
        Constants.landLineNameMap = HashMap()
        Constants.municipalityNameMap = HashMap()
        Constants.fastTagNameMap = HashMap()

        Constants.operatorNameMapForGettingOperatorName = HashMap()
        Constants.dthNameMapForGettingDthName = HashMap()
        Constants.emiNoMapForGettingEmiNo = HashMap()
        Constants.gasNameMapForGettingGasName = HashMap()
        Constants.insuranceNameMapForGettingInsuranceName = HashMap()
        Constants.electricityNameMapForGettingElectricityName = HashMap()
        Constants.prepaidNameMapForGettingPrepaidName = HashMap()
        Constants.waterNameMapForGettingWaterName = HashMap()
        Constants.broadBandNameMapForGettingBroadBandName = HashMap()
        Constants.landLineNameMapForGettingLandLineName = HashMap()
        Constants.municipalityNameMapForGettingMunicipalityName = HashMap()
        Constants.fastTagNameMapForGettingFastTagName = HashMap()


        if (rechargeType == "mobile") {
            binding.etAmount.visibility = View.GONE
            binding.rechargeAmount.visibility = View.VISIBLE

        }
        else if (rechargeType == "dth") {
            binding.tvRechargeType.text = "Enter CA No"
            binding.etAmount.visibility = View.VISIBLE
            binding.rechargeAmount.visibility = View.GONE
            binding.tvCircle.visibility = View.VISIBLE
            binding.etDTHBillNumber.visibility = View.VISIBLE
            binding.etMobileNumber2.visibility = View.GONE
            binding.etMobileNumber.visibility = View.GONE
            binding.etCircle.visibility = View.VISIBLE
            binding.tvBtnViewPlan.visibility = View.GONE

        }
        else if (rechargeType == "FastTag") {
            binding.tvRechargeType.text = "Enter Vehicle No"
            binding.etMobileNumber.visibility = View.GONE
            binding.etMobileNumber2.visibility = View.VISIBLE
            binding.tvCircle.visibility = View.GONE
            binding.etCircle.visibility = View.GONE
            binding.circlelayout.visibility = View.GONE
            binding.tvBtnViewPlan.visibility = View.GONE
            capitalizeEditText(binding.etMobileNumber2)
        }
        else
            if (rechargeType == "postpaid" || rechargeType == "Broadband" || rechargeType == "Electricity" || rechargeType == "Landline" || rechargeType == "Water" || rechargeType == "Gas" || rechargeType == "EMI" || rechargeType == "Cable" || rechargeType == "Insurance" || rechargeType == "Municipality") {
            binding.llMode.visibility = View.VISIBLE
            binding.etAmount.visibility = View.VISIBLE
            binding.tvRechargeType.visibility = View.GONE
            binding.etMobileNumber.visibility = View.GONE
            binding.rechargeAmount.visibility = View.GONE
            binding.tvCircle.visibility = View.GONE
            binding.etCircle.visibility = View.GONE
            binding.circlelayout.visibility = View.GONE
            binding.tvBtnViewPlan.visibility = View.GONE

            if(rechargeType == "postpaid"){
                binding.etMobileNumber.visibility = View.VISIBLE
                binding.tvRechargeType.visibility = View.VISIBLE
            }
            else if (rechargeType == "Broadband" || rechargeType == "Landline") {
                binding.tvAmount.text = "No. + STD Code"
            } else if (rechargeType == "Electricity" || rechargeType == "Gas" || rechargeType == "Municipality") {
                binding.tvAmount.text = "Enter CA Number"
            } else if (rechargeType == "Water") {
                binding.tvOperator.text = "Water Board"
                binding.tvAmount.text = "CA / RR No."
            } else if (rechargeType == "EMI") {
                binding.tvOperator.text = "Lender"
                binding.tvAmount.text = "Loan Account No."
            } else if (rechargeType == "Cable") {
                binding.tvAmount.text = "Mobile / Acc No."
            } else if (rechargeType == "Insurance") {
                binding.tvOperator.text = "Insurance"
                binding.tvAmount.text = "Policy Number."
            } else {
                binding.tvAmount.text = "Enter Mobile No"
            }

            binding.tvBtnProceed.text = "Get Bill"

            val arrayList = ArrayList<String>()
            arrayList.add("online")
            arrayList.add("offline")
            binding.spMode.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, arrayList)
            binding.spMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                    try {
                        spinnerType = arrayList[i]
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            }

        }


    }


    fun hideKeyboard(editText: EditText) {
        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }


    private fun viewPlanList() {
        if (rechargeType == "mobile") {
            if (TextUtils.isEmpty(binding.etMobileNumber.text.toString()) || binding.etMobileNumber.length() != 10) {
                binding.llViewPlan.visibility = View.GONE
                binding.llTransactionHistory.visibility = View.GONE
                Toast.makeText(context, "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show()
            }
            else {
                if (binding.spOperator.selectedItem != null && binding.spOperator.selectedItem.toString().isNotEmpty() && binding.etCircle.selectedItem.toString().isNotEmpty()) {
                    Constants.mobileOperatorName = binding.spOperator.selectedItem.toString()
                    Constants.mobileCircleName = binding.etCircle.selectedItem.toString()
                    mStash!!.setStringValue(Constants.mobileOperatorName, Constants.mobileOperatorName)
                    mStash!!.setStringValue(Constants.mobileCircleName, Constants.mobileCircleName)
                    mobileRechargePlanList.clear()

                    getAllPlanList(mStash!!.getStringValue(Constants.mobileOperatorName, "").toString(), mStash!!.getStringValue(Constants.mobileCircleName, "").toString())
                }
                else {
                    Toast.makeText(requireContext(), "Operator not found", Toast.LENGTH_SHORT).show()
                }


            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun validation() {
        if (rechargeType == "dth") {
            if (TextUtils.isEmpty(binding.etDTHBillNumber.text.toString())) {
                Toast.makeText(context, "Enter Valid CA Number", Toast.LENGTH_SHORT).show()
            } else if (mStash!!.getStringValue(Constants.OperatorId.toString(), "").equals("")) {
                Toast.makeText(context, "Please Select Operator", Toast.LENGTH_SHORT).show()
                return
            } else if (TextUtils.isEmpty(binding.etAmount.text.toString())) {
                Toast.makeText(context, "Enter Valid Amount", Toast.LENGTH_SHORT).show()
            } else {
                /* getAllWalletBalance()
                 //recharge()
                 hideKeyboard(binding.etAmount)*/

                // mobile recharge
                val amount = binding.etAmount.text.toString().trim()
                val operatorId = mStash!!.getStringValue(Constants.OperatorId.toString(), "")

                // ✅ dynamic first check slab for commission and service charge
                getAllServiceChargeRetailer(amount)
            }

        } else if (rechargeType == "postpaid") {
            if (selectedModel == "0") {
                Toast.makeText(context, "Please Select Operator", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(binding.etAmount.text.toString()) || binding.etAmount.length() != 10) {
                Toast.makeText(context, "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show()
            } else {
                getBill()
                hideKeyboard(binding.etAmount)
            }
        } else if (rechargeType == "Broadband" || rechargeType == "Landline") {
            if (selectedModel == "0") {
                Toast.makeText(context, "Please Select Operator", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(binding.etAmount.text.toString())) {
                Toast.makeText(context, "Enter Valid STD Code", Toast.LENGTH_SHORT).show()
            } else {
                getBill()
                hideKeyboard(binding.etAmount)
            }
        } else
            if (rechargeType == "Electricity" || rechargeType == "Gas") {
                if (selectedModel == "0") {
                    Toast.makeText(context, "Please Select Operator", Toast.LENGTH_SHORT).show()
                } else if (TextUtils.isEmpty(binding.etAmount.text.toString())) {
                    Toast.makeText(context, "Enter CA Number", Toast.LENGTH_SHORT).show()
                } else {
                    getBill()
                    hideKeyboard(binding.etAmount)
                }
            } else if (rechargeType == "Municipality") {
                if (selectedModel == "0") {
                    Toast.makeText(context, "Please Select Operator", Toast.LENGTH_SHORT).show()
                } else if (TextUtils.isEmpty(binding.etAmount.text.toString())) {
                    Toast.makeText(context, "Enter CA Number", Toast.LENGTH_SHORT).show()
                } else {
                    getBill()
                    hideKeyboard(binding.etAmount)
                }
            } else if (rechargeType == "Water") {
                if (selectedModel == "0") {
                    Toast.makeText(context, "Please Select Water Board", Toast.LENGTH_SHORT).show()
                } else if (TextUtils.isEmpty(binding.etAmount.text.toString())) {
                    Toast.makeText(context, "Enter CA / RR No.", Toast.LENGTH_SHORT).show()
                } else {
                    getBill()
                    hideKeyboard(binding.etAmount)
                }
            } else if (rechargeType == "EMI") {
                if (selectedModel == "0") {
                    Toast.makeText(context, "Please Select Lender", Toast.LENGTH_SHORT)
                        .show()
                } else
                    if (TextUtils.isEmpty(binding.etAmount.text.toString())) {
                        Toast.makeText(
                            context,
                            "Enter Loan Account No.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        getBill()
                        hideKeyboard(binding.etAmount)
                    }
            } else if (rechargeType == "Cable") {
                if (selectedModel == "0") {
                    Toast.makeText(context, "Please Select Operator", Toast.LENGTH_SHORT)
                        .show()
                } else if (TextUtils.isEmpty(binding.etAmount.text.toString())) {
                    Toast.makeText(context, "Enter Mobile / Acc No.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    getBill()
                    hideKeyboard(binding.etAmount)
                }
            } else if (rechargeType == "Insurance") {
                if (selectedModel == "0") {
                    Toast.makeText(
                        context,
                        "Please Select Insurance",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (TextUtils.isEmpty(binding.etAmount.text.toString())) {
                    Toast.makeText(context, "Enter Policy No.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    getBill()
                    hideKeyboard(binding.etAmount)
                }
            } else if (rechargeType == "FastTag") {
                if (TextUtils.isEmpty(binding.etMobileNumber2.text.toString())) {
                    Toast.makeText(
                        context,
                        "Please Select Vehicle Number",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (selectedFastTagModel.id == "0") {
                    Toast.makeText(
                        context,
                        "Please Select Operator",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (TextUtils.isEmpty(binding.etAmount.text.toString())) {
                    Toast.makeText(context, "Enter Amount.", Toast.LENGTH_SHORT).show()
                } else {
                    var data = BillFetchDetails()
                    billFastTagRecharge(data)
                    hideKeyboard(binding.etAmount)
                }
            } else {

                // mobile recharge
                val amount = binding.rechargeAmount.text.toString().trim()
                val mobile = binding.etMobileNumber.text.toString().trim()
                val operatorId = mStash!!.getStringValue(Constants.OperatorId.toString(), "")

                // schedule a new one after delay (e.g., 800ms)

                if (amount.isEmpty()) {
                    Toast.makeText(context, "Enter Valid Amount", Toast.LENGTH_SHORT).show()
                    return
                }
                if (mobile.length != 10) {
                    Toast.makeText(context, "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show()
                    return
                }

                if (operatorId!!.isEmpty()) {
                    Toast.makeText(context, "Please Select Operator", Toast.LENGTH_SHORT).show()
                    return
                }

                // ✅ Trigger popup only after typing stopped
                getAllServiceChargeRetailer(amount)

            }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAllWalletBalance() {
        requireContext().runIfConnected {
            val walletBalanceReq = GetBalanceReq(
                parmUser = mStash!!.getStringValue(Constants.RegistrationId, ""),
                flag = "CreditBalance"
            )
            getAllApiServiceViewModel.getWalletBalance(walletBalanceReq)
                .observe(viewLifecycleOwner) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                it.data?.let { users ->
                                    users.body()?.let { response ->
                                        getAllWalletBalanceRes(response)
                                    }
                                }
                            }

                            ApiStatus.ERROR -> {
                                Constants.dialog.dismiss()
                            }

                            ApiStatus.LOADING -> {
                                Constants.OpenPopUpForVeryfyOTP(requireContext())
                            }
                        }
                    }
                }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun getAllWalletBalanceRes(response: GetBalanceRes) {
        if (response.isSuccess == true) {
            val mainBalance = (response.data[0].result!!.toDoubleOrNull() ?: 0.0)
            getMerchantBalance(mainBalance)

            Log.d("actualBalance", "main = $mainBalance")

            val totalAmount = mStash!!.getStringValue(Constants.totalTransaction, "")?.toDoubleOrNull() ?: 0.0

        } else {
            toast(response.returnMessage.toString())
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMerchantBalance(mainBalance: Double) {
        val getMerchantBalanceReq = GetMerchantBalanceReq(
            parmUser = mStash!!.getStringValue(Constants.MerchantId, "")/*"AOP-554"*/,
            flag = "DebitBalance"
        )
        getAllApiServiceViewModel.getAllMerchantBalance(getMerchantBalanceReq)
            .observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    getAllMerchantBalanceRes(response, mainBalance)
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            Constants.dialog.dismiss()
                        }

                        ApiStatus.LOADING -> {
                           // Constants.OpenPopUpForVeryfyOTP(requireContext())
                        }
                    }
                }
            }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAllMerchantBalanceRes(response: GetMerchantBalanceRes, mainBalance: Double) {
        if (response.isSuccess == true) {
            Log.d(TAG, "getAllMerchantBalanceRes: ${response.data[0].debitBalance}")
            mStash!!.setStringValue(Constants.merchantBalance, response.data[0].debitBalance)

            val totalAmount = mStash!!.getStringValue(Constants.totalTransaction, "")?.toDoubleOrNull() ?: 0.0
            val merchantBalance = response.data[0].debitBalance?.toDoubleOrNull() ?: 0.0

            Log.d(
                "balanceCheck",
                "MainBal = $mainBalance, merchantBal = $merchantBalance,totalAmount = $totalAmount, Status = ${totalAmount <= mainBalance && totalAmount <= merchantBalance}"
            )

            if (totalAmount <= mainBalance && totalAmount <= merchantBalance) {

                if (rechargeType == "mobile") {
                    var mobileNo = binding.etMobileNumber.text.toString()
                    hitApiForMobileRecharge(
                        binding.rechargeAmount.text.toString(),
                        mobileNo,
                        binding.spOperator.selectedItem.toString().trim(),
                        "Mobile Api",
                        "Mobile Recharge"
                    )

                }

                if (rechargeType == "dth") {
                    var CANumber = binding.etDTHBillNumber.text.toString()

                    hitApiForMobileRecharge(
                        binding.etAmount.text.toString(),
                        CANumber,
                        binding.spOperator.selectedItem.toString().trim(),
                        "DTH Api",
                        "DTH Recharge"
                    )
                }


            } else {
                Constants.dialog.dismiss()
                Toast.makeText(requireContext(), "Your merchant balance is low. Please contact the administrator", Toast.LENGTH_LONG).show()
            }
        }
        else {
            Constants.dialog.dismiss()
            Toast.makeText(requireContext(), response.returnMessage.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun billFastTagRecharge(data: BillFetchDetails) {
        var referenceID: String = ConstantClass.generateRandomNumber()
        var model = FastTagRechargeReq(
            operator = selectedFastTagModel.id!!.toInt(),
            canumber = binding.etMobileNumber2.text.toString().trim(),
            amount = binding.etAmount.text.toString().trim(),
            referenceid = referenceID,
            latitude = ConstantClass.latdouble.toString(),
            longitude = ConstantClass.longdouble.toString(),
//            billFetch = bill_model.billFetchDetails,
            billFetch = BillFetch(
                billAmount = mStash!!.getStringValue(Constants.billAmount, ""),
                billnetamount = mStash!!.getStringValue(Constants.billnetamount, ""),
                minBillAmount = mStash!!.getStringValue(Constants.maxBillAmount, "1"),
                acceptPayment = mStash!!.getBoolanValue(Constants.acceptPayment.toString(), false),
                acceptPartPay = mStash!!.getBoolanValue(Constants.acceptPartPay.toString(), false),
                cellNumber = mStash!!.getStringValue(Constants.cellNumber, ""),
                userName = mStash!!.getStringValue(Constants.userName, "")
            ),
            RegistrationID = mStash!!.getStringValue(Constants.MerchantId, "")
        )
        Log.d("RegistrationID", Gson().toJson(model))
        ViewModelProvider(this).get(AttendanceViewModel::class.java).billFastTagRecharge(model)
            .observe(viewLifecycleOwner) {
                if (it!!.status == true) {
                    //rechargeStatusCheck(referenceID)
                } else {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun billRecharge() {
        var referenceID: String = ConstantClass.generateRandomNumber()
        var billPaymentPaybillReq = BillPaymentPaybillReq(
            operator = selectedModel,
            canumber = binding.etAmount.text.toString().trim(),
            amount = binding.etAmount2.text.toString().trim(),
            referenceid = referenceID,
            latitude = ConstantClass.latdouble.toString(),
            longitude = ConstantClass.longdouble.toString(),
            mode = "online",
            RegistrationID = mStash!!.getStringValue(Constants.MerchantId, ""),
            billFetch = com.bos.payment.appName.data.model.fastTag.billPayment.BillFetch(
                billAmount = "10",
                billnetamount = mStash!!.getStringValue(Constants.posPaidBillNetAmount, ""),
                billdate = mStash!!.getStringValue(Constants.posPaidBillDate, ""),
                dueDate = mStash!!.getStringValue(Constants.posPaidDueDate, ""),
                acceptPayment = mStash!!.getBoolanValue(Constants.posPaidAcceptPayment.toString(), false),
                acceptPartPay = mStash!!.getBoolanValue(Constants.posPaidAcceptPartPay.toString(), false),
                cellNumber = mStash!!.getStringValue(Constants.posPaidCellNumber, ""),
                userName = mStash!!.getStringValue(Constants.posPaidUserName, "")
            )
        )

        Log.d("billPaymentPaybillReq", Gson().toJson(billPaymentPaybillReq))
        ViewModelProvider(this).get(AttendanceViewModel::class.java)
            .billRecharge(billPaymentPaybillReq)
            .observe(viewLifecycleOwner) {
                if (it!!.status == true) {
                    Constants.dialog.dismiss()
                    //rechargeStatusCheck(referenceID)
                    toast(it.message.toString())
                } else {
                    Constants.dialog.dismiss()
                    toast(it.message.toString())
                }
            }
    }


    private fun getBill() {
        requireContext().runIfConnected {
            val fetchBilPaymentDetailsReq = FetchBilPaymentDetailsReq(
                operator = selectedModel,
                canumber = binding.etAmount.text.toString(),
                mode = spinnerType,
                RegistrationID = mStash!!.getStringValue(Constants.MerchantId, "")
            )
            Log.d("billreq",Gson().toJson(fetchBilPaymentDetailsReq))
            MobileRechargeViewModel.viewBill(fetchBilPaymentDetailsReq).observe(requireActivity()) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            Constants.dialog.dismiss()
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    Log.d("Bill Response",Gson().toJson(response))
                                    getViewBBPSBillRes(response) }
                            }
                        }

                        ApiStatus.ERROR -> {
                            Constants.dialog.dismiss()
                        }

                        ApiStatus.LOADING -> {
                            Constants.OpenPopUpForVeryfyOTP(requireContext())
                        }
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getViewBBPSBillRes(response: FetchBilPaymentDetailsRes) {
        if (response.status == true) {
            binding.userNameEditText.visibility = View.VISIBLE
            binding.userNameText.visibility = View.VISIBLE
            binding.llViewBill.visibility = View.VISIBLE
            binding.userNameEditText.setText(response.name.toString())
            binding.tvBtnProceedBill.visibility = View.VISIBLE
            binding.tvAmount2.visibility = View.VISIBLE
            binding.etAmount2.visibility = View.VISIBLE

            binding.llViewDetails.setOnClickListener {
                if (binding.layoutDetails.visibility == View.VISIBLE) {
                    binding.layoutDetails.visibility = View.GONE
                    binding.arrowImg.animate().rotation(0f).setDuration(200).start()
                } else {
                    binding.layoutDetails.visibility = View.VISIBLE
                    binding.arrowImg.animate().rotation(180f).setDuration(200).start()
                }
            }

            binding.tvBillCaNumber.text = binding.etAmount.text.toString()
            binding.tvBillName.text = response.name.toString()
            binding.tvBillAmount.text = response.billFetch!!.billAmount
            binding.tvBillBillDate.text = response.billFetch!!.billdate
            binding.tvBillDueDate.text = response.billFetch!!.dueDate
            binding.operatorName.text =   binding.spOperator.selectedItem.toString().trim()
            binding.etAmount2.setText(response.amount.toString())

            mStash!!.setStringValue(Constants.posPaidBillAmount, response.billFetch!!.billAmount)
            mStash!!.setStringValue(Constants.posPaidBillNetAmount, response.billFetch!!.billnetamount)
            mStash!!.setStringValue(Constants.posPaidBillDate, response.billFetch!!.billdate)
            mStash!!.setStringValue(Constants.posPaidDueDate, response.billFetch!!.dueDate)
            mStash!!.setStringValue(Constants.posPaidUserName, response.billFetch!!.userName)
            mStash!!.setStringValue(Constants.posPaidCellNumber, response.billFetch!!.cellNumber)

            response.billFetch!!.acceptPayment?.let { mStash!!.setBooleanValue(Constants.posPaidAcceptPayment.toString(), it) }

            response.billFetch!!.acceptPartPay?.let { mStash!!.setBooleanValue(Constants.posPaidAcceptPartPay.toString(), it) }

            Log.d(TAG, "viewBill: ${mStash!!.setStringValue(Constants.posPaidBillNetAmount, response.billFetch!!.billnetamount.toString())}")

            var referenceID: String = ConstantClass.generateRandomNumber()

            /*   bill_rec_model = BillPaymentPaybillReq(
                operator = selectedModel,
                canumber = binding.etMobileNumber2.text.toString().trim(),
                amount = binding.etAmount.text.toString().trim(),
                referenceid = referenceID,
                latitude = ConstantClass.latdouble.toString(),
                longitude = ConstantClass.longdouble.toString(),
                mode = spinnerType,
                billFetch = bill_rec_model.billFetch,
                RegistrationID = mStash!!.getStringValue(Constants.MerchantId, "")
            )*/

            Toast.makeText(requireActivity(), response.message.toString(), Toast.LENGTH_SHORT).show()

        }
        else {
            Constants.dialog.dismiss()
            binding.llViewBill.visibility = View.GONE
            binding.tvAmount2.visibility = View.GONE
            binding.etAmount2.visibility = View.GONE
            binding.userNameText.visibility = View.GONE
            binding.layoutDetails.visibility = View.GONE
            binding.userNameEditText.visibility = View.GONE
            binding.arrowImg.animate().rotation(0f).setDuration(200).start()
            Toast.makeText(context, response.message.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun getAllBillServiceCharge(rechargeAmount: String) {
        requireContext().runIfConnected {
            val getAPIServiceChargeReq = GetAPIServiceChargeReq(
                APIName = mStash!!.getStringValue(Constants.AllAPIName, ""),
                Category = mStash!!.getStringValue(Constants.opCategory, ""),
                Code = mStash!!.getStringValue(Constants.OperatorId.toString(), ""),
                CompanyCode = mStash!!.getStringValue(Constants.CompanyCode, "")
            )

            Log.d("getAllServiceCharge", Gson().toJson(getAPIServiceChargeReq))

            viewModel.getAllAPIServiceCharge(getAPIServiceChargeReq)
                .observe(viewLifecycleOwner) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                Constants.dialog.dismiss()
                                it.data?.let { users ->
                                    users.body()?.let { response ->
                                        Log.d("billservice",Gson().toJson(response))
                                    }
                                }
                            }

                            ApiStatus.ERROR -> Constants.dialog.dismiss()
                            ApiStatus.LOADING -> Constants.OpenPopUpForVeryfyOTP(requireContext())
                        }
                    }
                }
        }
    }

    /*private fun getFastTagDetails() {
        var model = FetchConsumerDetailsReq(
            operator = selectedFastTagModel.id,
            canumber = binding.etMobileNumber2.text.toString(),
            RegistrationID = mStash!!.getStringValue(Constants.MerchantId, "")
        )
        ViewModelProvider(this)[AttendanceViewModel::class.java].getFastTagDetails(model)
            .observe(viewLifecycleOwner) {
                if (it!!.status == true) {
                    Constants.dialog.dismiss()
                    bill_model = it
                    binding.llViewBill.visibility = View.VISIBLE
                    hideKeyboard(binding.etAmount)
                    it.billFetchDetails?.let { it1 -> viewFastTagBill(it1) }
                } else {
                    Constants.dialog.dismiss()
                    binding.llViewBill.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
    }*/

    /*    @SuppressLint("SetTextI18n")
    private fun viewFastTagBill(model: BillFetchDetails) {
        binding.tvVehicleNumber.text = "Vehicle No.:"
        binding.tvCustomerName.text = "Customer Name:"
        binding.tvDueDate.text = "Due Date:"
        binding.tvBillAmountFastTag.text = "Bill Amount:"
        binding.tv5.visibility = View.GONE
        binding.tv6.visibility = View.GONE


        binding.tvBillCaNumber.text = model.cellNumber
        binding.tvBillName.text = model.userName
        binding.tvBillAmount.text = model.dueDate
        binding.tvBillBillDate.text = model.billnetamount
        binding.tvBillDueDate.visibility = View.GONE
        binding.tvBillCaMessage.visibility = View.GONE

        mStash!!.setStringValue(Constants.billAmount, model.billAmount)
        mStash!!.setStringValue(Constants.billnetamount, model.billnetamount)
        mStash!!.setStringValue(Constants.dueDate, model.dueDate)
        mStash!!.setStringValue(
            Constants.maxBillAmount,
            if (model.maxBillAmount != null) model.maxBillAmount.toString() else "1"
        )
        mStash!!.setStringValue(Constants.userName, model.userName)
        mStash!!.setStringValue(Constants.cellNumber, model.cellNumber)
        model.acceptPayment?.let {
            mStash!!.setBooleanValue(
                Constants.acceptPayment.toString(),
                it
            )
        }
        model.acceptPartPay?.let {
            mStash!!.setBooleanValue(
                Constants.acceptPartPay.toString(),
                it
            )
        }

    }*/


    private fun getOperatorName(number: String, type: String) {
        requireContext().runIfConnected {
            val mobileCheckReq = MobileCheckReq(
                RegisterID = mStash!!.getStringValue(Constants.MerchantId, ""),
                number = number,
                type = type
            )

            Log.d("getOperatorName", Gson().toJson(mobileCheckReq))
            viewModel.getOperatorName(mobileCheckReq).observe(requireActivity()) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
//                        Constants.dialog.dismiss()
                            it.data?.let { users ->
                                users.body()?.let { it1 ->// getAllOperatorRes(it1)
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            Constants.dialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                        ApiStatus.LOADING -> {
                            Constants.OpenPopUpForVeryfyOTP(requireContext())
                        }
                    }
                }
            }
        }
    }



    /************************************* getOperatorList ***********************************/


    @RequiresApi(Build.VERSION_CODES.M)
    private fun getOperatorList(apiName: String) {
        requireContext().runIfConnected {
            val billOperationPaymentReq = BillOperationPaymentReq(
                mode = "Online",
                RegistrationID = mStash!!.getStringValue(Constants.MerchantId, "")
            )
            Log.d("getOperatorList: ", Gson().toJson(billOperationPaymentReq))
            MobileRechargeViewModel.getOperatorList(billOperationPaymentReq).observe(viewLifecycleOwner) { resource ->
                    try {
                        resource?.let {
                            when (it.apiStatus) {
                                ApiStatus.SUCCESS -> {
                                    Constants.dialog.dismiss()
                                    it.data?.let { response ->
                                        getAllOperatorDropDown(response.body()!!.data, response, apiName)

                                    }
                                }

                                ApiStatus.ERROR -> {
                                    Constants.dialog.dismiss()
                                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                                }

                                ApiStatus.LOADING -> {
                                    Constants.OpenPopUpForVeryfyOTP(requireContext())

                                }
                            }
                        }
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    }
                }
        }
    }


    private fun getAllOperatorDropDown(list: java.util.ArrayList<Data>, response: Response<BillOperationPaymentRes>, apiName: String) {
        mStash!!.setStringValue(Constants.AllAPIName, apiName.toString())
        if (response.body()!!.status == true) {
            for (singleObject in list) {
                when (singleObject.category) {
                    "EMI" -> {
                        Constants.emiNo!!.add(singleObject.name.toString())
                        Constants.emiNoMap!![singleObject.name.toString()] = Integer.valueOf(singleObject.id?.toInt() ?: 0)
                        Constants.emiNoMapForGettingEmiNo!![singleObject.id?.toInt() ?: 0] = singleObject.name.toString()
                    }

                    "Gas" -> {
                        Constants.gasName!!.add(singleObject.name.toString())
                        Constants.gasNameMap!![singleObject.name.toString()] = Integer.valueOf(singleObject.id?.toInt() ?: 0)
                        Constants.gasNameMapForGettingGasName!![singleObject.id?.toInt() ?: 0] = singleObject.name.toString()
                    }

                    "Insurance" -> {
                        Constants.insuranceName!!.add(singleObject.name.toString())
                        Constants.insuranceNameMap!![singleObject.name.toString()] = Integer.valueOf(singleObject.id?.toInt() ?: 0)
                        Constants.insuranceNameMapForGettingInsuranceName!![singleObject.id?.toInt() ?: 0] = singleObject.name.toString()
                    }

                    "Broadband" -> {
                        Constants.broadBandName!!.add(singleObject.name.toString())
                        Constants.broadBandNameMap!![singleObject.name.toString()] = Integer.valueOf(singleObject.id?.toInt() ?: 0)
                        Constants.broadBandNameMapForGettingBroadBandName!![singleObject.id?.toInt() ?: 0] = singleObject.name.toString()
                    }

                    "Electricity" -> {
                        Constants.electricityName!!.add(singleObject.name.toString())
                        Constants.electricityNameMap!![singleObject.name.toString()] = Integer.valueOf(singleObject.id?.toInt() ?: 0)
                        Constants.electricityNameMapForGettingElectricityName!![singleObject.id?.toInt() ?: 0] = singleObject.name.toString()
                    }

                    "Water" -> {
                        Constants.waterName!!.add(singleObject.name.toString())
                        Constants.waterNameMap!![singleObject.name.toString()] = Integer.valueOf(singleObject.id?.toInt() ?: 0)
                        Constants.waterNameMapForGettingWaterName!![singleObject.id?.toInt() ?: 0] = singleObject.name.toString()
                    }

                    "Postpaid" -> {
                        Constants.prepaidName!!.add(singleObject.name.toString())
                        Constants.prepaidNameMap!![singleObject.name.toString()] = Integer.valueOf(singleObject.id?.toInt() ?: 0)
                        Constants.prepaidNameMapForGettingPrepaidName!![singleObject.id?.toInt() ?: 0] = singleObject.name.toString()
                    }

                    "Landline" -> {
                        Constants.landLineName!!.add(singleObject.name.toString())
                        Constants.landLineNameMap!![singleObject.name.toString()] = Integer.valueOf(singleObject.id?.toInt() ?: 0)
                        Constants.landLineNameMapForGettingLandLineName!![singleObject.id?.toInt() ?: 0] = singleObject.name.toString()
                    }

                    "Municipality" -> {
                        Constants.municipalityName!!.add(singleObject.name.toString())
                        Constants.municipalityNameMap!![singleObject.name.toString()] = Integer.valueOf(singleObject.id?.toInt() ?: 0)
                        Constants.municipalityNameMapForGettingMunicipalityName!![singleObject.id?.toInt() ?: 0] = singleObject.name.toString()
                    }
                }
                setAllDropDown()
            }
        } else {
            Toast.makeText(requireContext(), response.body()!!.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun getAllOperatorList(apiName: String) {
        requireContext().runIfConnected {
            val rechargeOperatorsListReq = RechargeOperatorsListReq(
                registrationID = mStash!!.getStringValue(Constants.MerchantId, ""),
                RechargeType = "Online"
            )
            Log.d("getAllOperatorList", Gson().toJson(rechargeOperatorsListReq))
            viewModel.getAllOperatorList(rechargeOperatorsListReq)
                .observe(viewLifecycleOwner) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                it.data?.let { response ->
                                    populateDropDowns(response.body()!!.data, response, apiName)
                                }
                            }

                            ApiStatus.ERROR -> {
                                toast("Due to technical problem")
                            }

                            ApiStatus.LOADING -> {

                            }
                        }
                    }
                }
        }
    }


    private fun populateDropDowns(list: ArrayList<com.bos.payment.appName.data.model.recharge.operator.Data>, response: Response<RechargeOperatorsListRes>, apiName: String) {
        mStash!!.setStringValue(Constants.APIName, apiName.toString())
        if (response.body()!!.status == true) {
            for (singleObject in list) {
                when (singleObject.category) {
                    "Prepaid" -> {
                        Constants.operatorName!!.add(singleObject.name.toString())
                        Constants.operatorNameMap!![singleObject.name.toString()] =
                            Integer.valueOf(singleObject.id?.toInt() ?: 0)
                        Constants.operatorNameMapForGettingOperatorName!![singleObject.id?.toInt()
                            ?: 0] = singleObject.name.toString()
                    }

                    "DTH" -> {
                        Constants.dthName!!.add(singleObject.name.toString())
                        Constants.dthNameMap!![singleObject.name.toString()] =
                            Integer.valueOf(singleObject.id?.toInt() ?: 0)
                        Constants.dthNameMapForGettingDthName!![singleObject.id?.toInt() ?: 0] =
                            singleObject.name.toString()
                    }

                }
                setAllDropDownMobile(singleObject)
            }
        } else {
            Toast.makeText(
                requireContext(),
                response.body()!!.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun setAllDropDownMobile(singleObject: com.bos.payment.appName.data.model.recharge.operator.Data) {
        if (rechargeType == "mobile") {
            Constants.getAllOperatorAdapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.spinner_right_aligned,
                Constants.operatorName!!
            )
            Constants.getAllOperatorAdapter!!.setDropDownViewResource(R.layout.spinner_right_aligned)
            binding.spOperator.adapter = Constants.getAllOperatorAdapter
            binding.spOperator.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
                        if (pos > 0) {
                            try {
                                selectedAllOperator = java.lang.String.valueOf(Constants.operatorNameMap!![parent.getItemAtPosition(pos)])

                                mStash!!.setStringValue(Constants.OperatorCategory, singleObject.category.toString())

                                mStash!!.setStringValue(Constants.OperatorId.toString(), selectedAllOperator)

                                Log.d("contenttype", mStash!!.getStringValue(Constants.OperatorCategory, "").toString())

                                // If you need to perform further actions with selectedAllOperator, do it here.
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            selectedAllOperator = ""
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Handle the case where nothing is selected, if needed
                    }
                }

            // Notify the adapter that the data set has changed (if data is dynamically added/removed)
            Constants.getAllOperatorAdapter!!.notifyDataSetChanged()
        } else
            if (rechargeType == "dth") {
                //********************************* DTH Spinner ******************************//

                Constants.getAllDTHOperatorAdapter = ArrayAdapter<String>(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    Constants.dthName!!
                )
                Constants.getAllDTHOperatorAdapter!!.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                binding.spOperator.adapter = Constants.getAllDTHOperatorAdapter
                binding.spOperator.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            pos: Int,
                            id: Long
                        ) {
                            if (pos > 0) {
                                try {
                                    selectedAllOperator = java.lang.String.valueOf(Constants.dthNameMap!!.get(parent.getItemAtPosition(pos)))
                                    mStash!!.setStringValue(Constants.OperatorCategory, "DTH")
                                    mStash!!.setStringValue(Constants.OperatorId.toString(), selectedAllOperator)

                                    Log.d("dthName", mStash!!.getStringValue(Constants.OperatorCategory, "").toString())
                                    Log.d("dthName", mStash!!.getStringValue(Constants.OperatorId.toString(), "").toString())

                                    // If you need to perform further actions with selectedAllOperator, do it here.
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                selectedAllOperator = ""
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            // Handle the case where nothing is selected, if needed
                        }
                    }

                // Notify the adapter that the data set has changed (if data is dynamically added/removed)
                Constants.getAllDTHOperatorAdapter!!.notifyDataSetChanged()
            }
    }


    private fun setAllDropDown() {
        if (rechargeType == "EMI") {
            //********************************* EMI Spinner ******************************//

            Constants.getAllOperatorAdapterValue = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, Constants.emiNo!!)
            Constants.getAllOperatorAdapterValue!!.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            binding.spOperator.adapter = Constants.getAllOperatorAdapterValue
            binding.spOperator.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
                        if (pos > 0) {
                            try {
                                selectedModel = java.lang.String.valueOf(Constants.emiNoMap!!.get(parent.getItemAtPosition(pos)))
                                mStash!!.setStringValue(Constants.opCategory, rechargeType.toString())
                                mStash!!.setStringValue(Constants.OperatorId.toString(), selectedModel)
                                Log.d("dthName", mStash!!.getStringValue(Constants.opCategory, rechargeType).toString())
                                Log.d("dthName", mStash!!.getStringValue(Constants.OperatorId.toString(), "").toString())

                                // If you need to perform further actions with selectedAllOperator, do it here.
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            selectedAllOperator = null.toString()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Handle the case where nothing is selected, if needed
                    }
                }

            // Notify the adapter that the data set has changed (if data is dynamically added/removed)
            Constants.getAllOperatorAdapterValue!!.notifyDataSetChanged()

        }
        else if (rechargeType == "Gas") {
            //********************************* Gas Spinner ******************************//

            Constants.getAllGasOperatorAdapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                Constants.gasName!!
            )
            Constants.getAllGasOperatorAdapter!!.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            binding.spOperator.adapter = Constants.getAllGasOperatorAdapter
            binding.spOperator.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
                        if (pos > 0) {
                            try {
                                selectedModel = java.lang.String.valueOf(
                                    Constants.gasNameMap!!.get(
                                        parent.getItemAtPosition(pos)
                                    )
                                )
                                mStash!!.setStringValue(
                                    Constants.opCategory,
                                    rechargeType.toString()
                                )
                                mStash!!.setStringValue(
                                    Constants.OperatorId.toString(),
                                    selectedModel
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.opCategory, rechargeType)
                                        .toString()
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.OperatorId.toString(), "")
                                        .toString()
                                )
//                                mStash!!.setStringValue(
//                                    Constants.OperatorCategory,
//                                    singleObject.category.toString()
//                                )
//                        Log.d("dthName",mStash!!.getStringValue(Constants.OperatorCategory, "").toString())
//
//                                mStash!!.setStringValue(
//                                    Constants.OperatorId.toString(),
//                                    selectedAllOperator
//                                )
                                // If you need to perform further actions with selectedAllOperator, do it here.
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            selectedAllOperator = ""
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Handle the case where nothing is selected, if needed
                    }
                }

            // Notify the adapter that the data set has changed (if data is dynamically added/removed)
            Constants.getAllGasOperatorAdapter!!.notifyDataSetChanged()
        }
        else if (rechargeType == "Insurance") {
            //********************************* Gas Spinner ******************************//

            Constants.getAllGasOperatorAdapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                Constants.insuranceName!!
            )
            Constants.getAllGasOperatorAdapter!!.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            binding.spOperator.adapter = Constants.getAllGasOperatorAdapter
            binding.spOperator.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
                        if (pos > 0) {
                            try {
                                selectedModel = java.lang.String.valueOf(
                                    Constants.insuranceNameMap!!.get(
                                        parent.getItemAtPosition(pos)
                                    )
                                )
                                mStash!!.setStringValue(
                                    Constants.opCategory,
                                    rechargeType.toString()
                                )
                                mStash!!.setStringValue(
                                    Constants.OperatorId.toString(),
                                    selectedModel
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.opCategory, rechargeType)
                                        .toString()
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.OperatorId.toString(), "")
                                        .toString()
                                )
//                                mStash!!.setStringValue(
//                                    Constants.OperatorCategory,
//                                    singleObject.category.toString()
//                                )
////                        Log.d("dthName",mStash!!.getStringValue(Constants.OperatorCategory, "").toString())
//
//                                mStash!!.setStringValue(
//                                    Constants.OperatorId.toString(),
//                                    selectedAllOperator
//                                )
                                // If you need to perform further actions with selectedAllOperator, do it here.
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            selectedModel = ""
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Handle the case where nothing is selected, if needed
                    }
                }

            // Notify the adapter that the data set has changed (if data is dynamically added/removed)
            Constants.getAllGasOperatorAdapter!!.notifyDataSetChanged()
        }
        else if (rechargeType == "Broadband") {
            //********************************* Gas Spinner ******************************//

            Constants.getAllGasOperatorAdapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                Constants.broadBandName!!
            )
            Constants.getAllGasOperatorAdapter!!.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            binding.spOperator.adapter = Constants.getAllGasOperatorAdapter
            binding.spOperator.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
                        if (pos > 0) {
                            try {
                                selectedModel = java.lang.String.valueOf(
                                    Constants.broadBandNameMap!!.get(
                                        parent.getItemAtPosition(pos)
                                    )
                                )

                                mStash!!.setStringValue(
                                    Constants.opCategory,
                                    rechargeType.toString()
                                )
                                mStash!!.setStringValue(
                                    Constants.OperatorId.toString(),
                                    selectedModel
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.opCategory, rechargeType)
                                        .toString()
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.OperatorId.toString(), "")
                                        .toString()
                                )
//                                mStash!!.setStringValue(
//                                    Constants.OperatorCategory,
//                                    singleObject.category.toString()
//                                )
//                        Log.d("dthName",mStash!!.getStringValue(Constants.OperatorCategory, "").toString())
//
//                                mStash!!.setStringValue(
//                                    Constants.OperatorId.toString(),
//                                    selectedAllOperator
//                                )
                                // If you need to perform further actions with selectedAllOperator, do it here.
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            selectedModel = ""
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Handle the case where nothing is selected, if needed
                    }
                }

            // Notify the adapter that the data set has changed (if data is dynamically added/removed)
            Constants.getAllGasOperatorAdapter!!.notifyDataSetChanged()
        }
        else if (rechargeType == "Electricity") {
            //********************************* Electricity Spinner ******************************//
            Constants.getAllGasOperatorAdapter = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, Constants.electricityName!!)
            Constants.getAllGasOperatorAdapter!!.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            binding.spOperator.adapter = Constants.getAllGasOperatorAdapter
            binding.spOperator.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
                        if (pos > 0) {
                            try {
                                selectedModel = java.lang.String.valueOf(
                                    Constants.electricityNameMap!!.get(
                                        parent.getItemAtPosition(pos)
                                    )
                                )

                                mStash!!.setStringValue(
                                    Constants.opCategory,
                                    rechargeType.toString()
                                )
                                mStash!!.setStringValue(
                                    Constants.OperatorId.toString(),
                                    selectedModel
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.opCategory, rechargeType)
                                        .toString()
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.OperatorId.toString(), "")
                                        .toString()
                                )

                                // If you need to perform further actions with selectedAllOperator, do it here.
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            selectedModel = ""
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Handle the case where nothing is selected, if needed
                    }
                }
            // Notify the adapter that the data set has changed (if data is dynamically added/removed)
            Constants.getAllGasOperatorAdapter!!.notifyDataSetChanged()
        }
        else if (rechargeType == "Water") {
            //********************************* Water Spinner ******************************//

            Constants.getAllGasOperatorAdapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                Constants.waterName!!
            )
            Constants.getAllGasOperatorAdapter!!.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            binding.spOperator.adapter = Constants.getAllGasOperatorAdapter
            binding.spOperator.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
                        if (pos > 0) {
                            try {
                                selectedModel = java.lang.String.valueOf(
                                    Constants.waterNameMap!!.get(
                                        parent.getItemAtPosition(pos)
                                    )
                                )
                                mStash!!.setStringValue(
                                    Constants.opCategory,
                                    rechargeType.toString()
                                )
                                mStash!!.setStringValue(
                                    Constants.OperatorId.toString(),
                                    selectedModel
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.opCategory, rechargeType)
                                        .toString()
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.OperatorId.toString(), "")
                                        .toString()
                                )
                                // If you need to perform further actions with selectedAllOperator, do it here.
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            selectedModel = ""
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Handle the case where nothing is selected, if needed
                    }
                }

            // Notify the adapter that the data set has changed (if data is dynamically added/removed)
            Constants.getAllGasOperatorAdapter!!.notifyDataSetChanged()
        }
        else if (rechargeType == "postpaid") {
            //********************************* Prepaid Spinner ******************************//

            Constants.getAllGasOperatorAdapter = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, Constants.prepaidName!!)
            Constants.getAllGasOperatorAdapter!!.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            binding.spOperator.adapter = Constants.getAllGasOperatorAdapter
            binding.spOperator.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                        if (pos > 0) {
                            try {
                                selectedModel = java.lang.String.valueOf(
                                    Constants.prepaidNameMap!!.get(
                                        parent.getItemAtPosition(pos)
                                    )
                                )
                                mStash!!.setStringValue(
                                    Constants.opCategory,
                                    rechargeType.toString()
                                )
                                mStash!!.setStringValue(
                                    Constants.OperatorId.toString(),
                                    selectedModel
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.opCategory, rechargeType)
                                        .toString()
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.OperatorId.toString(), "")
                                        .toString()
                                )
                                // If you need to perform further actions with selectedAllOperator, do it here.
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            selectedModel = ""
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Handle the case where nothing is selected, if needed
                    }
                }

            // Notify the adapter that the data set has changed (if data is dynamically added/removed)
            Constants.getAllGasOperatorAdapter!!.notifyDataSetChanged()
        } else if (rechargeType == "Landline") {
            //********************************* Gas Spinner ******************************//

            Constants.getAllGasOperatorAdapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                Constants.landLineName!!
            )
            Constants.getAllGasOperatorAdapter!!.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            binding.spOperator.adapter = Constants.getAllGasOperatorAdapter
            binding.spOperator.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                        if (pos > 0) {
                            try {
                                selectedModel = java.lang.String.valueOf(
                                    Constants.landLineNameMap!!.get(
                                        parent.getItemAtPosition(pos)
                                    )
                                )
                                mStash!!.setStringValue(
                                    Constants.opCategory,
                                    rechargeType.toString()
                                )
                                mStash!!.setStringValue(
                                    Constants.OperatorId.toString(),
                                    selectedModel
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.opCategory, rechargeType)
                                        .toString()
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.OperatorId.toString(), "")
                                        .toString()
                                )
                                // If you need to perform further actions with selectedAllOperator, do it here.
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            selectedModel = ""
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Handle the case where nothing is selected, if needed
                    }
                }

            // Notify the adapter that the data set has changed (if data is dynamically added/removed)
            Constants.getAllGasOperatorAdapter!!.notifyDataSetChanged()
        } else if (rechargeType == "Municipality") {
            //********************************* Municipality Spinner ******************************//

            Constants.getAllGasOperatorAdapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                Constants.municipalityName!!
            )
            Constants.getAllGasOperatorAdapter!!.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            binding.spOperator.adapter = Constants.getAllGasOperatorAdapter
            binding.spOperator.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
                        if (pos > 0) {
                            try {
                                selectedModel = java.lang.String.valueOf(
                                    Constants.municipalityNameMap!!.get(
                                        parent.getItemAtPosition(pos)
                                    )
                                )
                                mStash!!.setStringValue(
                                    Constants.opCategory,
                                    rechargeType.toString()
                                )
                                mStash!!.setStringValue(
                                    Constants.OperatorId.toString(),
                                    selectedModel
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.opCategory, rechargeType)
                                        .toString()
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.OperatorId.toString(), "")
                                        .toString()
                                )
                                // If you need to perform further actions with selectedAllOperator, do it here.
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            selectedModel = ""
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Handle the case where nothing is selected, if needed
                    }
                }

            // Notify the adapter that the data set has changed (if data is dynamically added/removed)
            Constants.getAllGasOperatorAdapter!!.notifyDataSetChanged()
        } else if (rechargeType == "FastTag") {
            //********************************* FastTag Spinner ******************************//

            Constants.getAllGasOperatorAdapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                Constants.fastTagName!!
            )
            Constants.getAllGasOperatorAdapter!!.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            binding.spOperator.adapter = Constants.getAllGasOperatorAdapter
            binding.spOperator.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
                        if (pos > 0) {
                            try {
                                selectedModel = java.lang.String.valueOf(
                                    Constants.fastTagNameMap!![parent.getItemAtPosition(pos)]
                                )
                                if (!TextUtils.isEmpty(binding.etMobileNumber2.text)) {
                                    binding.llViewBill.visibility = View.GONE
                                    //getFastTagDetails()
                                }
                                mStash!!.setStringValue(
                                    Constants.opCategory,
                                    rechargeType.toString()
                                )
                                mStash!!.setStringValue(
                                    Constants.OperatorId.toString(),
                                    selectedModel
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.opCategory, rechargeType)
                                        .toString()
                                )
                                Log.d(
                                    "dthName",
                                    mStash!!.getStringValue(Constants.OperatorId.toString(), "")
                                        .toString()
                                )
                                // If you need to perform further actions with selectedAllOperator, do it here.
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            selectedModel = ""
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Handle the case where nothing is selected, if needed
                    }
                }

            // Notify the adapter that the data set has changed (if data is dynamically added/removed)
            Constants.getAllGasOperatorAdapter!!.notifyDataSetChanged()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun getFastTagList() {
        requireContext().runIfConnected {
            mStash!!.getStringValue(Constants.MerchantId, "")?.let {
                viewModel.getFastTagList(registrationID = it)
                    .observe(viewLifecycleOwner) { resource ->
                        resource?.let {
                            when (it.apiStatus) {
                                ApiStatus.SUCCESS -> {
                                    Constants.dialog.dismiss()
                                    it.data?.let { users ->
                                        populateDropDown(users.body()!!.data, users)
//                                getFastTagListRes(users)
                                    }
                                }

                                ApiStatus.ERROR -> {
                                    Constants.dialog.dismiss()
                                    Toast.makeText(
                                        context,
                                        "Something went wrong",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }

                                ApiStatus.LOADING -> {
                                    Constants.dialog.dismiss()
                                }
                            }
                        }
                    }
            }
        }
    }


    private fun populateDropDown(list: java.util.ArrayList<com.bos.payment.appName.data.model.fastTag.fastTagOperator.Data>, response: Response<FastTagOperatorsListRes?>) {
        if (response.body()!!.status == true) {
            for (singleObject in list) {
                when (singleObject.category) {
                    "Fastag" -> {
                        Constants.fastTagName!!.add(singleObject.name.toString())
                        Constants.fastTagNameMap!![singleObject.name.toString()] = Integer.valueOf(singleObject.id?.toInt() ?: 0)
                        Constants.fastTagNameMapForGettingFastTagName!![singleObject.id?.toInt() ?: 0] = singleObject.name.toString()
                    }
                }
                setAllDropDown()
            }
        }
        else {
            Toast.makeText(requireContext(), response.body()!!.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    private fun getAllPlanList(operatorName: String, circleName: String) {
        val apiService = RetrofitClient.apiRechargeInterface

        val jsonObject = JSONObject().apply {
            put("RegistrationID", mStash!!.getStringValue(Constants.MerchantId, "")!! /*"AOP-554"*/)
            put("circle", circleName)
            put("operator", operatorName)
            put("number", binding.etMobileNumber.text.toString())
        }

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
        Constants.OpenPopUpForVeryfyOTP(requireContext())

        val call = apiService.getRechargePlanReq(requestBody)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    if (response.isSuccessful) {
                         if(Constants.dialog!=null && Constants.dialog.isShowing){
                             Constants.dialog.dismiss()
                         }

                        val responseString = response.body()?.string()
                        val rootObject = JSONObject(responseString)

                        try {
                            if (rootObject.has("statusCode")) {
                                val errorCode = rootObject.getInt("statusCode")
                                uploadDataOnFirebaseConsole("Error code $errorCode  $responseString", "RechargeFragmentRechargePlanReq", requireContext())
                            } else {
                                uploadDataOnFirebaseConsole("No statusCode found: $responseString", "RechargeFragmentRechargePlanReq", requireContext())
                            }
                        }
                        catch (e: Exception) {
                            uploadDataOnFirebaseConsole("Parsing Exception: ${e.message}", "RechargeFragmentRechargePlanReq", requireContext())
                        }


                        if (rootObject.has("data") && !rootObject.isNull("data")) {
                            val dataObject = rootObject.getJSONObject("data")

                            val keys = dataObject.keys()

                            while (keys.hasNext()) {
                                val key = keys.next()
                                val value = dataObject.get(key)

                                if (value is JSONArray) {
                                    Log.d("APIArrayName", "Array Name: $key")

                                    val mobilePlan = mutableListOf<Plan>()  // 👈 create new list for each array
                                    val array = value

                                    for (i in 0 until array.length()) {
                                        val item = array.getJSONObject(i)
                                        val rs = item.optInt("rs", 0)
                                        val validity = item.optString("validity", "")
                                        val desc = item.optString("desc", "")

                                        mobilePlan.add(Plan(rs, validity, desc))

                                        Log.d("API", " → Plan: Rs.$rs | Validity: $validity | Desc: $desc")
                                    }

                                    mobileRechargePlanList.add(MobileRechargePlanModel(key, mobilePlan))
                                }

                            }


                            if (mobileRechargePlanList.isNotEmpty()) {
                                Log.d("PlanList", Gson().toJson(mobileRechargePlanList))
                                binding.llViewPlan.visibility = View.VISIBLE
                                RechargePlanNameAdapter = RechargePlanNameAdapter(requireContext(), mobileRechargePlanList,
                                    clickListener = object : RechargePlanNameAdapter.ClickListener {
                                        override fun itemClick(item: String) {
                                            val mobilePlan = mobileRechargePlanList.find { it.arrayName == item }?.plans ?: emptyList()
                                            getAllPlanListRes(mobilePlan)
                                        }
                                    }
                                )

                                binding.plannamerecyclerview.adapter = RechargePlanNameAdapter
                                RechargePlanNameAdapter.notifyDataSetChanged()
                            }

                        }

                        Log.d("SuccessApiRespo", "Success: $responseString")

                    } else {
                        Constants.dialog.dismiss()
                        Log.e("ErrorApiRespo", "Error: ${response.code()} - ${response.errorBody()?.string()}")
                        binding.llViewPlan.visibility = View.GONE
                        uploadDataOnFirebaseConsole("Error: ${response.code()} - ${response.errorBody()?.string()}", "RechargeFragmentRechargePlanReq", requireContext())
                    }
                } catch (e: Exception) {
                    Constants.dialog.dismiss()
                    val rechargeplanview = "TryException ".plus(e.printStackTrace().toString())
                    uploadDataOnFirebaseConsole(rechargeplanview, "RechargeFragmentRechargePlanReq", requireContext())
                }


            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Constants.dialog.dismiss()
                Log.e("API", "Failure: ${t.message}")
            }

        })


    }


    @SuppressLint("NotifyDataSetChanged")
    private fun getAllPlanListRes(popularPlans: List<Plan>) {
        try {
            binding.llViewPlan.visibility = View.VISIBLE
            binding.llTransactionHistory.visibility = View.GONE
            // Set up RecyclerView
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvViewPlan.layoutManager = layoutManager
            val layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.recyclerview_anim_layout)
            binding.rvViewPlan.layoutAnimation = layoutAnimationController
            binding.rvViewPlan.setHasFixedSize(true)

            // Initialize and set adapter
            val adapter = ViewPlanListLatestAdapter(mContext = requireContext(),
                popularPlan = popularPlans ?: emptyList(),
                clickListener = object : ViewPlanListLatestAdapter.ClickListener {
                    override fun itemClick(rechargeprice: Int) {
                        binding.rechargeAmount.setText(rechargeprice.toString())
                        // binding.serviceChargeLayout.visibility = View.VISIBLE
                        binding.nestedview.scrollTo(0, 0)
                        // hold at this time by Naim Sir discussion...................................................
                        // getAllServiceCharge(rechargeprice.toString())
                        //........................................................................
                        /*     when (item) {
                            is PopularPlansItem -> {

                            }

                            is True5GUnlimitedPlansItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }

                            is GamingItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }

                            is TopUpItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }

                            is EntertainmentPlansItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }

                            is AnnualPlansItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }

                            is InFlightPacksItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }

                            is TrueUnlimitedUpgradeItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }

                            is InternationalRoamingItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }

                            is IRWiFiCallingItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }

                            is JioPhoneDataAddOnItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }

                            is JioSaavnProItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }

                            is ISDItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }

                            is JioPhonePrimaItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }

                            is DataPacksItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }

                            is JioBharatPhoneItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }

                            is JioLinkItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }

                            is JioPhoneItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }

                            is ValueItem -> {
                                binding.rechargeAmount.setText(item.rs.toString())
                                binding.serviceChargeLayout.visibility = View.VISIBLE
                                getAllServiceCharge(item.rs.toString())
                            }


                            else -> {
                            }
                            // Add handling for other types if needed
                        }
                        //binding.etAmount.requestFocus()
                        binding.etMobileNumber.requestFocus()*/
                    }
                })
            binding.rvViewPlan.layoutManager = LinearLayoutManager(requireContext())
            binding.rvViewPlan.adapter = adapter
            binding.rvViewPlan.setHasFixedSize(true)
            adapter.notifyDataSetChanged()


        } catch (e: Exception) {
            Constants.dialog.dismiss()
            e.printStackTrace()
        }
    }


    private fun setSearchPlanMobile(filterMobileData: MutableList<MobileRechargePlanModel>){
        if (filterMobileData.isNotEmpty()) {
            Log.d("PlanList", Gson().toJson(filterMobileData))
            RechargePlanNameAdapter = RechargePlanNameAdapter(requireContext(), filterMobileData,
                clickListener = object : RechargePlanNameAdapter.ClickListener {
                    override fun itemClick(item: String) {
                        val mobilePlan = filterMobileData.find { it.arrayName == item }?.plans ?: emptyList()
                        getAllPlanListRes(mobilePlan)
                    }
                }
            )

            binding.plannamerecyclerview.adapter = RechargePlanNameAdapter
            RechargePlanNameAdapter.notifyDataSetChanged()
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAllServiceChargeRetailer(rechargeAmount: String) {
        requireContext().runIfConnected {
            val getCommercialReq = GetCommercialReq(
                txtslabamtfrom = rechargeAmount.toDoubleOrNull(),
                txtslabamtto = rechargeAmount.toDoubleOrNull(),
                merchant = mStash!!.getStringValue(Constants.RegistrationId, ""),
                productId = featureCode,
                cantentType = mStash!!.getStringValue(Constants.OperatorCategory, ""),
                operatorsTypeID = mStash!!.getStringValue(Constants.OperatorId.toString(), "")
            )

            Log.d("getAllServiceChargeRetailer", Gson().toJson(getCommercialReq))

            getAllApiServiceViewModel.getAllRechargeAndBillServiceCharge(getCommercialReq)
                .observe(viewLifecycleOwner) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                Constants.dialog.dismiss()
                                it.data?.let { users ->
                                    users.body()?.let { response ->
                                        Log.d("RetailerResp", Gson().toJson(response))
                                        getAllServiceChargeApiResRetailer(response, rechargeAmount)
                                    }
                                }
                            }

                            ApiStatus.ERROR -> Constants.dialog.dismiss()
                            ApiStatus.LOADING -> Constants.dialog.dismiss()
                        }
                    }
                }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAllServiceChargeAdmin(rechargeAmount: String) {
        requireContext().runIfConnected {
            val getCommercialReq = GetCommercialReq(
                txtslabamtfrom = rechargeAmount.toDoubleOrNull(),
                txtslabamtto = rechargeAmount.toDoubleOrNull(),
                merchant = mStash!!.getStringValue(Constants.AdminCode, ""),
                productId = featureCode,
                cantentType = mStash!!.getStringValue(Constants.OperatorCategory, ""),
                operatorsTypeID = mStash!!.getStringValue(Constants.OperatorId.toString(), "")
            )

            Log.d("getAllServiceChargeAdmin", Gson().toJson(getCommercialReq))

            getAllApiServiceViewModel.getAllRechargeAndBillServiceCharge(getCommercialReq)
                .observe(viewLifecycleOwner) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                Constants.dialog.dismiss()
                                it.data?.let { users ->
                                    users.body()?.let { response ->
                                        Log.d("AdminResp", Gson().toJson(response))
                                        getAllServiceChargeApiResAdmin(response, rechargeAmount)
                                    }
                                }
                            }

                            ApiStatus.ERROR -> Constants.dialog.dismiss()
                            ApiStatus.LOADING -> Constants.dialog.dismiss()
                        }
                    }
                }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale", "SetTextI18n", "SuspiciousIndentation")
    private fun getAllServiceChargeApiResRetailer(response: GetCommercialRes, rechargeAmount: String) {
        response.let {
            if (it.isSuccess == true) {
                // Parse values safely
                val rechargeAmountValue = rechargeAmount.toDoubleOrNull() ?: 0.0
                val retailerCommission =
                    response.data[0].retailerCommission?.toDoubleOrNull() ?: 0.0

                val TDSTax = 5.0 // Fixed TDS rate

                // Function to calculate commission with TDS
                fun calculateCommission(amount: Double, type: String?, tdsRate: Double): Double {
                    return when (type) {
                        "Percentage" -> {
                            val commissionAmount = rechargeAmountValue * (amount / 100)
                            val tdsAmount = commissionAmount * (tdsRate / 100)
                            mStash!!.setStringValue(
                                Constants.retailerCommission,
                                String.format("%.2f", commissionAmount)
                            )
                            mStash!!.setStringValue(Constants.tds, String.format("%.2f", tdsAmount))
                            commissionAmount - (commissionAmount * (tdsRate / 100))
                        }

                        "Amount" -> {
                            val tdsAmount = amount * (tdsRate / 100)
                            mStash!!.setStringValue(
                                Constants.retailerCommission,
                                String.format("%.2f", amount)
                            )
                            mStash!!.setStringValue(Constants.tds, String.format("%.2f", tdsAmount))
                            amount - (amount * (tdsRate / 100))
                        }

                        else -> {
                            mStash!!.setStringValue(Constants.retailerCommission, "0.0")
                            mStash!!.setStringValue(Constants.tds, "0.0")
                            0.0

                        }
                    }
                }

                // Calculate retailer commission
                val finalRetailerCommission = calculateCommission(
                    retailerCommission,
                    response.data[0].retailerCommissionType,
                    TDSTax
                )
                // Log results
                Log.d("FinalRetailerCommission", String.format("%.2f", finalRetailerCommission))


                // Service charge calculation
                val gst = 18.0 // Fixed GST rate of 18%
                val serviceCharge = response.data[0].serviceCharge?.toDoubleOrNull() ?: 0.0

                val totalServiceChargeWithGst =
                    serviceChargeCalculation(serviceCharge, gst, rechargeAmount, response)
                Log.d("servicechargewithgst", String.format("%.2f", totalServiceChargeWithGst))

//              Calculating the total recharge amount
                val totalRechargeAmount =
                    (rechargeAmount.toDoubleOrNull() ?: 0.0) + totalServiceChargeWithGst
                Log.d("rechargeAmount", String.format("%.2f", totalRechargeAmount))

                // Save commission types in shared preferences
                with(mStash!!) {
                    setStringValue(
                        Constants.admin_CommissionType,
                        response.data[0].adminCommissionType.toString()
                    )
                    setStringValue(
                        Constants.retailer_CommissionType,
                        response.data[0].retailerCommissionType.toString()
                    )
                    setStringValue(
                        Constants.customer_CommissionType,
                        response.data[0].customerCommissionType.toString()
                    )
                    setStringValue(Constants.serviceType, response.data[0].serviceType.toString())
                }

                mStash!!.setStringValue(
                    Constants.retailerCommissionWithoutTDS,
                    String.format("%.2f", finalRetailerCommission)
                )

                mStash!!.setStringValue(
                    Constants.totalTransaction,
                    String.format("%.2f", totalRechargeAmount)
                )

                openDialogForPayout(
                    rechargeAmount.toDoubleOrNull() ?: 0.0,
                    totalServiceChargeWithGst,
                    totalRechargeAmount,
                    mStash!!.getStringValue(Constants.retailerCommission, "")!!.toDoubleOrNull()!!,
                    ""
                )

            } else {
                getAllServiceChargeAdmin(rechargeAmount)

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale", "SetTextI18n", "SuspiciousIndentation")
    private fun getAllServiceChargeApiResAdmin(response: GetCommercialRes, rechargeAmount: String) {
        response.let {
            if (it.isSuccess == true) {
                // Parse values safely
                val rechargeAmountValue = rechargeAmount.toDoubleOrNull() ?: 0.0
                val retailerCommission =
                    response.data[0].retailerCommission?.toDoubleOrNull() ?: 0.0

                val TDSTax = 5.0 // Fixed TDS rate


                // Function to calculate commission with TDS
                fun calculateCommission(amount: Double, type: String?, tdsRate: Double): Double {
                    return when (type) {
                        "Percentage" -> {
                            val commissionAmount = rechargeAmountValue * (amount / 100)
                            val tdsAmount = commissionAmount * (tdsRate / 100)
                            mStash!!.setStringValue(Constants.retailerCommission, String.format("%.2f", commissionAmount))
                            mStash!!.setStringValue(Constants.tds, String.format("%.2f", tdsAmount))
                            commissionAmount - (commissionAmount * (tdsRate / 100))
                        }

                        "Amount" -> {
                            val tdsAmount = amount * (tdsRate / 100)
                            mStash!!.setStringValue(Constants.retailerCommission, String.format("%.2f", amount))
                            mStash!!.setStringValue(Constants.tds, String.format("%.2f", tdsAmount))
                            amount - (amount * (tdsRate / 100))
                        }

                        else -> {
                            mStash!!.setStringValue(Constants.retailerCommission, "0.0")
                            mStash!!.setStringValue(Constants.tds, "0.0")
                            0.0

                        }
                    }
                }

                // Calculate retailer commission
                val finalRetailerCommission = calculateCommission(
                    retailerCommission,
                    response.data[0].retailerCommissionType,
                    TDSTax
                )

                // Log results
                Log.d("FinalRetailerCommission", String.format("%.2f", finalRetailerCommission))

                // Service charge calculation
                val gst = 18.0 // Fixed GST rate of 18%
                val serviceCharge = response.data[0].serviceCharge?.toDoubleOrNull() ?: 0.0

                val totalServiceChargeWithGst =
                    serviceChargeCalculation(serviceCharge, gst, rechargeAmount, response)
                Log.d("servicechargewithgst", String.format("%.2f", totalServiceChargeWithGst))

//              Calculating the total recharge amount
                val totalRechargeAmount = (rechargeAmount.toDoubleOrNull() ?: 0.0) + totalServiceChargeWithGst
                Log.d("rechargeAmount", String.format("%.2f", totalRechargeAmount))

                // Save commission types in shared preferences
                with(mStash!!) {
                    setStringValue(
                        Constants.admin_CommissionType,
                        response.data[0].adminCommissionType.toString()
                    )
                    setStringValue(
                        Constants.retailer_CommissionType,
                        response.data[0].retailerCommissionType.toString()
                    )
                    setStringValue(
                        Constants.customer_CommissionType,
                        response.data[0].customerCommissionType.toString()
                    )
                    setStringValue(Constants.serviceType, response.data[0].serviceType.toString())
                }

                mStash!!.setStringValue(
                    Constants.retailerCommissionWithoutTDS,
                    String.format("%.2f", finalRetailerCommission)
                )

                mStash!!.setStringValue(
                    Constants.totalTransaction,
                    String.format("%.2f", totalRechargeAmount)
                )

                openDialogForPayout(
                    rechargeAmount.toDoubleOrNull() ?: 0.0,
                    totalServiceChargeWithGst,
                    totalRechargeAmount,
                    mStash!!.getStringValue(Constants.retailerCommission, "")!!.toDoubleOrNull()!!,
                    ""
                )

            } else {

                // Save commission types in shared preferences
                with(mStash!!) {
                    setStringValue(Constants.admin_CommissionType, "")
                    setStringValue(Constants.retailer_CommissionType, "")
                    setStringValue(Constants.customer_CommissionType, "")
                    setStringValue(Constants.serviceType, "")
                }

                mStash!!.setStringValue(Constants.retailerCommissionWithoutTDS, String.format("%.2f", 0.0))
                mStash!!.setStringValue(Constants.retailerCommission, String.format("%.2f", 0.0))
                mStash!!.setStringValue(Constants.tds, String.format("%.2f", 0.0))

                val totalRechargeAmount = (rechargeAmount.toDoubleOrNull() ?: 0.0) + 0.0
                mStash!!.setStringValue(Constants.totalTransaction, String.format("%.2f", totalRechargeAmount))

                serviceChargeWithGST = mStash!!.getStringValue(Constants.serviceChargeWithGST, "")!!
                mStash!!.setStringValue(Constants.gst, String.format("%.2f", 0.0))
                mStash!!.setStringValue(Constants.serviceCharge, String.format("%.2f", 0.0))
                mStash!!.setStringValue(Constants.retailerCommission, String.format("%.2f", 0.0))
                mStash!!.setStringValue(Constants.tds, String.format("%.2f", 0.0))

                var msg = "Warning : Slab structure not found. This transaction will proceed without any commission being credited."

                openDialogForPayout(rechargeAmount.toDoubleOrNull() ?: 0.0, 0.0, totalRechargeAmount, 0.0, msg)

            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    fun openDialogForPayout(transferAmount: Double, servicechargeGst: Double, totalRechargeAmount: Double, retailerCommission: Double, msg: String) {
        dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.mobilerechargecommissionlayout)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        dialog.setCanceledOnTouchOutside(false)

        val transferamttxt = dialog.findViewById<TextView>(R.id.actualamt)
        val servicechargewithgst = dialog.findViewById<TextView>(R.id.servicechargewithgst)
        val warningmsg = dialog.findViewById<TextView>(R.id.warningmsg)
        val transferamt = dialog.findViewById<TextView>(R.id.transferamt)
        val serviceChargeamount = dialog.findViewById<TextView>(R.id.servicescharge)
        val retailercommission = dialog.findViewById<TextView>(R.id.retailercommission)
        val gstamount = dialog.findViewById<TextView>(R.id.gstamount)
        val cancel = dialog.findViewById<ImageView>(R.id.cancel)
        val done = dialog.findViewById<LinearLayout>(R.id.Proceedbtn)
        val viewBreakLayout = dialog.findViewById<LinearLayout>(R.id.viewbreaklayout)
        val servicechargelayout = dialog.findViewById<LinearLayout>(R.id.servicechargelayout)
        val detailsgstserviceslayout = dialog.findViewById<LinearLayout>(R.id.chargesdetailslayout)
        val retailercommissionlayout =
            dialog.findViewById<LinearLayout>(R.id.retailercommissionlayout)

        if (msg.isNotEmpty()) {
            warningmsg.visibility = View.VISIBLE
            warningmsg.text = msg
        } else {
            warningmsg.visibility = View.GONE
        }

        val gst = mStash!!.getStringValue(Constants.gst, "")
        mStash!!.setStringValue(Constants.serviceChargewithgst, String.format("%.2f", servicechargeGst)).toString()

        transferamttxt.text = String.format("%.2f", transferAmount)
        servicechargewithgst.text = String.format("%.2f", servicechargeGst)
        retailercommission.text = String.format("%.2f", retailerCommission)
        serviceChargeamount.text = mStash!!.getStringValue(Constants.serviceCharge, "").toString()
        gstamount.text = "$gst"
        transferamt.text = String.format("%.2f", totalRechargeAmount)

        if (servicechargeGst == 0.0) {
            servicechargelayout.visibility = View.GONE
        } else {
            servicechargelayout.visibility = View.VISIBLE
        }

        if (retailerCommission == 0.0) {
            retailercommissionlayout.visibility = View.GONE
        } else {
            retailercommissionlayout.visibility = View.VISIBLE
        }

        var checkView: Boolean = false


        viewBreakLayout.setOnClickListener {
            if (checkView) {
                detailsgstserviceslayout.visibility = View.GONE
                checkView = false
            } else {
                detailsgstserviceslayout.visibility = View.VISIBLE
                checkView = true
            }
        }

        done.setOnClickListener {
            if (totalRechargeAmount > 0) {
                mStash!!.setStringValue(
                    Constants.totalTransaction,
                    String.format("%.2f", totalRechargeAmount)
                )
                mStash!!.setStringValue(
                    Constants.serviceChargeWithGST,
                    String.format("%.2f", servicechargeGst)
                )
                mStash!!.setStringValue(
                    Constants.actualRechargeAmount,
                    String.format("%.2f", transferAmount)
                )

                getAllWalletBalance()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Transfer amount must be greater than the service charge.",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        cancel.setOnClickListener {
            if (dialog != null && dialog.isShowing) {
                dialog.dismiss()
            }

        }

        dialog.setOnDismissListener {
            dialog.dismiss()
        }

        dialog.show() // ✅ REQUIRED
    }


    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun serviceChargeCalculation(serviceCharge: Double, gstRate: Double, rechargeAmount: String, response: GetCommercialRes): Double {

        val rechargeAmountValue = rechargeAmount.toDoubleOrNull() ?: 0.0

        val totalAmountWithGst = when (response.data[0].serviceType) {
            "Amount" -> {
                // Service charge is a fixed amount
                //binding.serviceChargeText.text = "Service Charge Rs"
                val serviceChargeWithGst = serviceCharge * (gstRate / 100)
                mStash!!.setStringValue(Constants.gst, String.format("%.2f", serviceChargeWithGst))
                mStash!!.setStringValue(
                    Constants.serviceCharge,
                    String.format("%.2f", serviceCharge)
                )
                serviceCharge + serviceChargeWithGst
            }

            "Percentage" -> {
                // Service charge is a percentage of the recharge amount
                //binding.serviceChargeText.text = "Service Charge %"
                val serviceInAmount = rechargeAmountValue * (serviceCharge / 100)
                val serviceWithGst = serviceInAmount * (gstRate / 100)
                mStash!!.setStringValue(Constants.gst, String.format("%.2f", serviceWithGst))
                // binding.serviceChargeWithGST.text = String.format("%.2f", serviceWithGst)
                mStash!!.setStringValue(Constants.serviceCharge, String.format("%.2f", serviceInAmount))
                serviceInAmount + serviceWithGst

            }

            else -> {
                mStash!!.setStringValue(Constants.gst, String.format("%.2f", 0.0))
                mStash!!.setStringValue(Constants.serviceCharge, String.format("%.2f", 0.0))
                0.0
            }
        }

        mStash!!.setStringValue(Constants.serviceChargeWithGST, String.format("%.2f", totalAmountWithGst))

        Log.d("gstamount", mStash!!.getStringValue(Constants.gst, "").toString())
        Log.d("servicecharge", mStash!!.getStringValue(Constants.serviceCharge, "").toString())
        Log.d("totalAmountWithGst", mStash!!.getStringValue(Constants.serviceChargeWithGST, "").toString())

        // Return the total service charge (with GST) to include in the final transaction
        return totalAmountWithGst
    }


    // Function to capitalize all text in an EditText
    fun capitalizeEditText(editText: EditText) {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                val capitalizedText = s.toString().toUpperCase()
                if (s.toString() != capitalizedText) {
                    editText.removeTextChangedListener(this)
                    editText.setText(capitalizedText)
                    editText.setSelection(capitalizedText.length)
                    editText.addTextChangedListener(this)
                }
            }
        }

        editText.addTextChangedListener(textWatcher)
    }


    private fun getFuseLocation() {

        customFuseLocation =
            CustomFuseLocationActivity(requireActivity(), requireContext()) { mCurrentLocation ->
                ConstantClass.latdouble = mCurrentLocation.latitude
                ConstantClass.longdouble = mCurrentLocation.longitude
                Log.d(
                    "Lat Long",
                    "Lat: ${ConstantClass.latdouble} : Long: ${ConstantClass.longdouble}"
                )
            }
    }


    override fun onResume() {
        super.onResume()
        try {
            customFuseLocation!!.onResume()
            getFuseLocation()
        } catch (e: java.lang.Exception) {
        }
    }


    override fun onStop() {
        try {
            customFuseLocation!!.onStop()
        } catch (e: java.lang.Exception) {
        }
        super.onStop()
    }


    override fun onPause() {
        super.onPause()
        try {
            customFuseLocation!!.onPause()
        } catch (e: java.lang.Exception) {
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun showUpdateKycDetails(context: Context) {
        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle("Need Permissions")
        builder.setMessage("Are you sure want to pay Bill")
        builder.setPositiveButton("Yes") { dialog, which ->
            billRecharge()
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }


    // operators name.......................................................................
    fun hitApiForRechargeOperatorNameList(displayName: String) {

        var operatorsReq = RechargeOperatorsReq(
            registrationID = mStash?.getStringValue(Constants.MerchantId, "") /*"AOP-554"*/,
            displayName = DisplayName!!
        )
        Log.d("operatorReq", Gson().toJson(operatorsReq))

        MobileRechargeViewModel.getRechargeOperatorsRequest(operatorsReq)
            .observe(requireActivity()) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {

                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    Constants.dialog.dismiss()
                                    uploadDataOnFirebaseConsole(
                                        Gson().toJson(response),
                                        "RechargeFragmentOperatorsRequest",
                                        requireContext()
                                    )
                                    Log.d("operatorRespo", Gson().toJson(response))
                                    if (response.data!!.size > 0) {
                                        var getdata = response.data
                                        operatorsNameList.clear()
                                        operatorsNameList = getdata!!

                                        Constants.operatorName!!.clear()
                                        Constants.productIdList!!.clear()

                                        operatorsNameList.forEach { it ->
                                            Constants.operatorName!!.add(it.displayName.toString())
                                            Constants.productIdList!!.add(it.productId.toString())
                                        }
                                        setDropDownMobileOperators()
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


    private fun setDropDownMobileOperators() {
        if (rechargeType == "mobile") {
            Constants.operatorName!!.clear()
            setMobileOperatorNameWithProductID().forEach { it ->
                Constants.operatorName!!.add(it.first)
            }
            Constants.getAllOperatorAdapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.spinner_right_aligned,
                Constants.operatorName!!
            )
        } else {
            Constants.getAllOperatorAdapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.spinner_right_aligned,
                Constants.operatorName!!
            )
        }

        Constants.getAllOperatorAdapter!!.setDropDownViewResource(R.layout.spinner_right_aligned)
        binding.spOperator.adapter = Constants.getAllOperatorAdapter

        binding.spOperator.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (Constants.productIdList!!.isNotEmpty()) {
                    if (rechargeType == "mobile") {
                        var operatorName = binding.spOperator.selectedItem.toString()
                        OperatorID = getOperatorId(operatorName).toString()
                        mStash!!.setStringValue(Constants.OperatorId.toString(), OperatorID)
                        Log.d("operatorID", OperatorID)
                    } else {
                        Log.d("SelectedPos", Constants.productIdList!![position].toString())
                        OperatorID = Constants.productIdList!![position].toString()
                        mStash!!.setStringValue(
                            Constants.OperatorId.toString(),
                            Constants.productIdList!![position].toString()
                        )
                    }

                    binding.llViewPlan.visibility = View.GONE
                    if (binding.etDTHBillNumber.text.length >= 10)
                        lastTriggeredBy = "operator"
                    CheckAndHitApi()
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        // Notify the adapter that the data set has changed (if data is dynamically added/removed)
        Constants.getAllOperatorAdapter!!.notifyDataSetChanged()


        val circleArray = resources.getStringArray(R.array.recharge_circle)
        Constants.getAllCircleAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_right_aligned, circleArray)
        Constants.getAllCircleAdapter!!.setDropDownViewResource(R.layout.spinner_right_aligned)
        binding.etCircle.adapter = Constants.getAllCircleAdapter

        binding.etCircle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (circleArray!![position]!!.isNotEmpty()) {
                    Log.d("circlename", circleArray!![position].toString())

                    mStash!!.setStringValue(Constants.circleName.toString(), circleArray!![position].toString())
                    binding.llViewPlan.visibility = View.GONE

                    if (binding.etDTHBillNumber.text.length >= 10) {
                        lastTriggeredBy = "circle"
                        CheckAndHitApi()
                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        // Notify the adapter that the data set has changed (if data is dynamically added/removed)
        Constants.getAllCircleAdapter!!.notifyDataSetChanged()


    }


    private fun getOperatorId(operatorName: String): Int? {
        val normalized = operatorName.lowercase()

        // Direct match
        val directMatch =
            operatorNameMobileList.find { it.first.equals(operatorName, ignoreCase = true) }
        if (directMatch != null) return directMatch.second

        // Keyword mapping
        return when {
            "airtel" in normalized -> operatorNameMobileList.find {
                it.first.contains(
                    "Airtel",
                    true
                )
            }?.second

            "jio" in normalized || "reliance" in normalized -> operatorNameMobileList.find {
                it.first.contains(
                    "Jio",
                    true
                )
            }?.second

            "vodafone" in normalized || "vi" in normalized -> operatorNameMobileList.find {
                it.first.contains(
                    "VI",
                    true
                )
            }?.second

            "bsnl" in normalized -> operatorNameMobileList.find {
                it.first.contains(
                    "BSNL",
                    true
                )
            }?.second

            else -> null // No match found
        }
    }


    // operators name.......................................................................
    fun hitApiForMobileWiseOperatorName(mob: String) {

        var operatorsReq = MobileWiseRechargeReq(
            registrationID = mStash?.getStringValue(Constants.MerchantId, "") /*"AOP-554"*/,
            number = mob
        )
        Log.d("operatorReq", Gson().toJson(operatorsReq))

        MobileRechargeViewModel.getRechargeMobileWiseRequest(operatorsReq)
            .observe(requireActivity()) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            Constants.dialog.dismiss()
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    Constants.dialog.dismiss()
                                    uploadDataOnFirebaseConsole(
                                        Gson().toJson(response),
                                        "RechargeFragmentMobileWiseRequest",
                                        requireContext()
                                    )
                                    Log.d("mobileNameRespo", Gson().toJson(response))
                                    if (response.statusCode == 200) {
                                        var operatorName = response.data!!.operator
                                        var circleName = response.data!!.circle
                                        setdefaultDropDownList(operatorName!!, circleName!!)
                                    } else {
                                        hitApiForRechargeOperatorNameList(DisplayName!!)
                                    }
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            Constants.dialog.dismiss()
                        }

                        ApiStatus.LOADING -> {
                            Constants.OpenPopUpForVeryfyOTP(requireContext())
                        }
                    }
                }

            }

    }


    fun setdefaultDropDownList(operatorName: String, circleName: String) {
        //..................................Operator Name.........................................................................................

        val position = Constants.operatorName!!.indexOf(operatorName)

        if (position == -1) {
            // Not found → add at top
            Constants.operatorName!!.clear()
            Constants.operatorName!!.add(0, operatorName)
        } else {
            // Found → move it to top (optional)
            if (position != 0) {
                Constants.operatorName!!.removeAt(position)
                Constants.operatorName!!.add(0, operatorName)
            }
        }

        Constants.getAllOperatorAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_right_aligned,
            Constants.operatorName!!
        ).apply {
            setDropDownViewResource(R.layout.spinner_right_aligned)
        }

        // Set to spinner
        binding.spOperator.adapter = Constants.getAllOperatorAdapter
        // Always select default at 0th position
        binding.spOperator.setSelection(0)

        //....................................................................................................................................

        //........................................Circle Name.................................................................................
        val circleArray = resources.getStringArray(R.array.recharge_circle).toMutableList()
        val circleposition = circleArray.indexOf(circleName)
        if (circleposition == -1) {
            // Not found → add at top
            circleArray!!.add(0, circleName)
        } else {
            // Found → move it to top (optional)
            if (circleposition != 0) {
                circleArray!!.removeAt(circleposition)
                circleArray!!.add(0, circleName)
            }
        }

        val circleAdapter = ArrayAdapter(requireContext(), R.layout.spinner_right_aligned, circleArray).apply { setDropDownViewResource(R.layout.spinner_right_aligned) }

        binding.etCircle.adapter = circleAdapter
        binding.etCircle.setSelection(0)  // default always selected

        //....................................................................................................................................

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun hitApiForMobileRecharge(rechargeAmt: String, mobileNo: String, operatorName: String, remarks: String, commissionremark: String) {
        var registrationId = mStash?.getStringValue(Constants.MerchantId, "")
        var productID = mStash!!.getStringValue(Constants.OperatorId.toString(), "")

        var latlong = ConstantClass.latdouble.toString().plus(",").plus(ConstantClass.longdouble)
        Log.d("Lat Long", ConstantClass.latdouble.toString().plus(",").plus(ConstantClass.longdouble))

        var MobileRechargeReq =
            com.bos.payment.appName.data.model.recharge.newapiflowforrecharge.MobileRechargeReq(
                registrationId = registrationId,
                productId = productID!!,
                amount = rechargeAmt,
                geoCoder = latlong,
                customerNumber = mobileNo
            )
        Log.d("rechargeReq", Gson().toJson(MobileRechargeReq).toString())

        MobileRechargeViewModel.getMobileRechargeRequest(MobileRechargeReq)
            .observe(requireActivity()) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    uploadDataOnFirebaseConsole(Gson().toJson(response), "RechargeFragmentRechargeRequest", requireContext())
                                    getFuseLocation()
                                    Constants.dialog.dismiss()
                                    Log.d("rechargeResp", Gson().toJson(response))

                                    if (commissionremark.contains("dth", ignoreCase = true)) {
                                        when (productID) {
                                            "522" -> operatorlogo =
                                                context.resources.getDrawable(R.drawable.airteldigital)

                                            "525" -> operatorlogo =
                                                context.resources.getDrawable(R.drawable.tatasky)

                                            "526" -> operatorlogo =
                                                context.resources.getDrawable(R.drawable.videocon)

                                            "524" -> operatorlogo =
                                                context.resources.getDrawable(R.drawable.sundirect)

                                            "523" -> operatorlogo =
                                                context.resources.getDrawable(R.drawable.dishtv)
                                        }
                                        RechargeSuccessfulPageActivity.operatorName = binding.spOperator.selectedItem.toString().trim()
                                        Log.d("operatorName", RechargeSuccessfulPageActivity.operatorName)
                                        Log.d("productID", productID)
                                        operatorLogo = operatorlogo
                                        planPrice = binding.etAmount.text.toString().trim()

                                    } else {
                                        when (productID) {
                                            "518" -> operatorlogo =
                                                context.resources.getDrawable(R.drawable.airtel)

                                            "521" -> operatorlogo =
                                                context.resources.getDrawable(R.drawable.bsnl)

                                            "519" -> operatorlogo =
                                                context.resources.getDrawable(R.drawable.jio)

                                            "520" -> operatorlogo =
                                                context.resources.getDrawable(R.drawable.vodaphone)
                                        }
                                        RechargeSuccessfulPageActivity.operatorName = binding.spOperator.selectedItem.toString().trim()
                                        Log.d("operatorName", RechargeSuccessfulPageActivity.operatorName)
                                        Log.d("productID", productID)
                                        operatorLogo = operatorlogo
                                        planPrice = binding.rechargeAmount.text.toString().trim()
                                    }

                                    rechargeStatus = response.data!!.status!!
                                    Datetime = Constants.getCurrentDateTime()
                                    transactionID = response.data.txnId!!
                                    mobileNumber = mobileNo
                                    orderID = response.referanceID ?: ""

                                    serviceChargeWithGST = mStash!!.getStringValue(Constants.serviceChargeWithGST, "")!!
                                    totalTransaction = mStash!!.getStringValue(Constants.totalTransaction, "")!!

                                    hitApiForMobileRechargeReqUpload(rechargeAmt, mobileNo, response, Gson().toJson(MobileRechargeReq).toString(), operatorName, remarks, commissionremark)

                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            Constants.dialog.dismiss()
                        }

                        ApiStatus.LOADING -> {
                           // Constants.OpenPopUpForVeryfyOTP(requireContext())
                        }
                    }
                }

            }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun hitApiForMobileRechargeReqUpload(rechargeAmt: String, mobileNo: String, rechargeResponse: MobileRechargeRespo, rechargeresuqst: String, operatorName: String, remarks: String, commissionremark: String) {

        var mearchantId = mStash?.getStringValue(Constants.MerchantId, "")
        var registrationId = mStash?.getStringValue(Constants.RegistrationId, "")
        var productID = mStash!!.getStringValue(Constants.OperatorId.toString(), "")

        var MobileRechargeReq = UploadRechargeMobileRespReq(
            amount = rechargeAmt,
            requestParam = rechargeresuqst,
            operators = productID,
            registrationID = mearchantId,
            ipAddress = "",
            paramuser = registrationId,
            canumber = mobileNo,
            referenceid = rechargeResponse.data!!.txnId,
        )
        Log.d("rechargeReq", Gson().toJson(MobileRechargeReq).toString())

        getAllApiServiceViewModel.putRechargemobileReq(MobileRechargeReq)
            .observe(requireActivity()) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    Log.d("RechargeReqResp", Gson().toJson(response))
                                    hitApiForMobileRechargeRespUpload(
                                        rechargeResponse,
                                        operatorName,
                                        remarks,
                                        commissionremark
                                    )
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            Constants.dialog.dismiss()
                        }

                        ApiStatus.LOADING -> {
                            //Constants.OpenPopUpForVeryfyOTP(requireContext())
                        }
                    }
                }

            }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun hitApiForMobileRechargeRespUpload(rechargeResponse: MobileRechargeRespo, operatorName: String, remarks: String, commissionremark: String) {
        var mearchantId = mStash?.getStringValue(Constants.MerchantId, "")
        var registrationId = mStash?.getStringValue(Constants.RegistrationId, "")
        var productID = mStash!!.getStringValue(Constants.OperatorId.toString(), "")

        var MobileRechargeReq = UploadRechargeMobileRespRespReq(
            status = rechargeResponse.statusBos,
            responsecode = rechargeResponse.statusCode.toString(),
            operators = productID,
            ackno = rechargeResponse.data!!.ackno,
            refid = rechargeResponse.data!!.txnId,
            message = rechargeResponse.data!!.message,
            registrationID = mearchantId,
            ipAddress = "",
            responseParam = Gson().toJson(rechargeResponse.data).toString(),
            paramuser = registrationId,
        )
        Log.d("rechargeresponseReq", Gson().toJson(MobileRechargeReq).toString())

        getAllApiServiceViewModel.putRechargemobileResponseReq(MobileRechargeReq)
            .observe(requireActivity()) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    Log.d("RechargeRespResp", Gson().toJson(response))
                                    hitApiForRechargeApiResponse(rechargeResponse, operatorName, remarks, commissionremark)
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            Constants.dialog.dismiss()
                        }

                        ApiStatus.LOADING -> {
                           // Constants.OpenPopUpForVeryfyOTP(requireContext())
                        }
                    }
                }

            }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun hitApiForRechargeApiResponse(rechargeResponse: MobileRechargeRespo, operatorName: String, remarks: String, commissionremark: String) {
        var registrationId = mStash?.getStringValue(Constants.RegistrationId, "")
        var productID = mStash!!.getStringValue(Constants.OperatorId.toString(), "")
        var totalamount = mStash!!.getStringValue(Constants.totalTransaction, "")

        var rechargeamount = ""
        var rechargenumber = ""


        if (commissionremark.contains("dth", ignoreCase = true)) {
            rechargeamount = binding.etAmount.text.toString().trim()
            rechargenumber = binding.etDTHBillNumber.text.toString()
        } else {
            rechargeamount = binding.rechargeAmount.text.toString().trim()
            rechargenumber = binding.etMobileNumber.text.toString().trim()
        }

        val req = RechargeapiresponseReq(
            rechargePayableAmount = totalamount,
            refundReqStatus = "",
            apIBal = "0",
            apITransId = rechargeResponse.referanceID,
            transId = rechargeResponse.referanceID,
            rechargeAmount = totalamount,
            transIpAddress = "",
            apIErrorCode = rechargeResponse.error,
            rechargeMobileNoCaNo = rechargenumber,
            retailerID = registrationId,
            apIService = "RECH",
            apIResText = rechargeResponse.data!!.message,
            mode = "Online",
            rechargeOperator = binding.spOperator.selectedItem.toString().trim(),
            apIUrid = "registrationId",
            rechargeServiceType = "MOBILE",
            apIAmount = totalamount,
            apICommissionBal = "0",
            companyCode = "",
            apIOperatorId = productID,
            updatedBy = registrationId,
            rechargeDate = getCurrentIsoDate(),
            refundStatus = "",
            refundTransID = "",
            updatedOn = getCurrentIsoDate(),
            apIOrderId = rechargeResponse.referanceID,
            apIStatus = rechargeResponse.statusBos,
            apIBillName = "",
            apIMobile = rechargeamount,
            apIBillAmount = "",
            gateway = ""
        )

        Log.d("rechargeapiresponsereq", Gson().toJson(req).toString())

        getAllApiServiceViewModel.putRechargeapiresponseReq(req)
            .observe(requireActivity()) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    Log.d("rechargeapiresponsersp", Gson().toJson(response))
                                    getTransferAmountToAgentWithCal(
                                        rechargeamount,
                                        rechargenumber,
                                        operatorName,
                                        remarks,
                                        commissionremark
                                    )
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            Constants.dialog.dismiss()
                        }

                        ApiStatus.LOADING -> {
                           // Constants.OpenPopUpForVeryfyOTP(requireContext())
                        }
                    }
                }

            }


    }


    private fun getTransferAmountToAgentWithCal(rechargeAmount: String, rechargenumber: String, operatorName: String, remarks: String, commissionremark: String) {
        var totalamount = mStash!!.getStringValue(Constants.totalTransaction, "")
        var status = ""

        if (rechargeStatus.equals("success", ignoreCase = true)) {
            status = "Approved"
        }
        else if (rechargeStatus.equals("failed", ignoreCase = true)) {
            status = "Failed"
        }
        else {
            status = "Pending"
        }

        if (status.equals("Failed")) {
            val transferAmountToAgentsReq = TransferAmountToAgentsReq(
                transferFrom = mStash!!.getStringValue(Constants.RegistrationId, ""),
                transferTo = "Admin",
                transferAmt = "0.00",
                remark = "Recharge $remarks",
                transferFromMsg = "Your recharge of ₹$rechargeAmount  on $operatorName number ${rechargenumber}  has failed. No amount has been deducted",
                transferToMsg = "Your recharge of ₹$rechargeAmount  on $operatorName number ${rechargenumber}  has failed. No amount has been credited",
                amountType = "Payout",
                actualTransactionAmount = "0.00",
                transIpAddress = mStash!!.getStringValue(Constants.deviceIPAddress, ""),
                parmUserName = mStash!!.getStringValue(Constants.RegistrationId, ""),
                merchantCode = mStash!!.getStringValue(Constants.MerchantId, ""),
                servicesChargeAmt = "0.00",
                servicesChargeGSTAmt = "0.00",
                servicesChargeWithoutGST = "0.00",
                customerVirtualAddress = "",
                retailerCommissionAmt = "0.00",
                retailerId = "",
                paymentMode = "",
                depositBankName = "",
                branchCodeChecqueNo = "",
                apporvedStatus = status,
                registrationId = mStash!!.getStringValue(Constants.RegistrationId, ""),
                benfiid = "",
                accountHolder = "",
                flag = "Y"
            )

            Log.d("transferAmountToAgent", Gson().toJson(transferAmountToAgentsReq))

            getAllApiServiceViewModel.getTransferAmountToAgents(transferAmountToAgentsReq)
                .observe(viewLifecycleOwner) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                it.data?.let { users ->
                                    users.body()?.let { response ->
                                        Log.d("transafertoagent", Gson().toJson(response))

                                        Log.d("checkconditionforcommission", "CommissionMethodfailed")

                                        getTransferAmountToAgentWithCalRes(response)
                                    }
                                }
                            }

                            ApiStatus.ERROR -> {
                                Constants.dialog.dismiss()
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                            }

                            ApiStatus.LOADING -> {
                               // Constants.OpenPopUpForVeryfyOTP(requireContext())
                            }
                        }
                    }
                }

        }
        else {
            val transferAmountToAgentsReq = TransferAmountToAgentsReq(
                transferFrom = mStash!!.getStringValue(Constants.RegistrationId, ""),
                transferTo = "Admin",
                transferAmt = mStash!!.getStringValue(Constants.totalTransaction, "0.00") ?: "0.00",
                remark = "Recharge $remarks",
                transferFromMsg = "Your account has been debited by ₹$totalamount for a recharge on $operatorName number ${
                    rechargenumber
                }.",
                transferToMsg = "Your Account has been credited by ₹$totalamount for a recharge on $operatorName  number  ${
                    rechargenumber
                }.",
                amountType = "Payout",
                actualTransactionAmount = rechargeAmount,
                transIpAddress = mStash!!.getStringValue(Constants.deviceIPAddress, ""),
                parmUserName = mStash!!.getStringValue(Constants.RegistrationId, ""),
                merchantCode = mStash!!.getStringValue(Constants.MerchantId, ""),
                servicesChargeAmt = mStash!!.getStringValue(Constants.serviceChargewithgst, "0.00")
                    ?: "0.00",
                servicesChargeGSTAmt = mStash!!.getStringValue(Constants.gst, "0.00") ?: "0.00",
                servicesChargeWithoutGST = mStash!!.getStringValue(Constants.serviceCharge, "0.00")
                    ?: "0.00",
                customerVirtualAddress = "",
                retailerCommissionAmt = mStash!!.getStringValue(
                    Constants.retailerCommission,
                    "0.00"
                ) ?: "0.00",
                retailerId = "",
                paymentMode = "",
                depositBankName = "",
                branchCodeChecqueNo = "",
                apporvedStatus = status,
                registrationId = mStash!!.getStringValue(Constants.RegistrationId, ""),
                benfiid = "",
                accountHolder = "",
                flag = "Y"
            )

            Log.d("transferAmountToAgent", Gson().toJson(transferAmountToAgentsReq))

            getAllApiServiceViewModel.getTransferAmountToAgents(transferAmountToAgentsReq)
                .observe(viewLifecycleOwner) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                it.data?.let { users ->
                                    users.body()?.let { response ->
                                        Log.d("transafertoagent", Gson().toJson(response))

                                        val commission = mStash!!.getStringValue(
                                            Constants.retailerCommission,
                                            "0.00"
                                        )?.trim()?.toDoubleOrNull() ?: 0.0

                                        Log.d("retailercommissionforpayout", commission.toString())

                                        if (commission > 0.0) {
                                            Log.d("checkconditionforcommission", "Commissionmethod")
                                            getTransferAmountToAgentInCommissionCal(
                                                response,
                                                commissionremark
                                            )
                                        } else {
                                            Log.d(
                                                "checkconditionforcommission",
                                                "CommissionMethodfailed"
                                            )
                                            getTransferAmountToAgentWithCalRes(response)
                                        }

                                    }
                                }
                            }

                            ApiStatus.ERROR -> {
                                Constants.dialog.dismiss()
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            ApiStatus.LOADING -> {
                                //Constants.OpenPopUpForVeryfyOTP(requireContext())
                            }
                        }
                    }
                }
        }

    }


    private fun getTransferAmountToAgentInCommissionCal(response: TransferAmountToAgentsRes, remarks: String) {
        var totalamount = mStash!!.getStringValue(Constants.totalTransaction, "")
        var withouttdscommissionamount =
            mStash!!.getStringValue(Constants.retailerCommissionWithoutTDS, "")
        var tdsamount = mStash!!.getStringValue(Constants.tds, "")
        var actualcommission = mStash!!.getStringValue(Constants.retailerCommission, "")

        Log.d("tdsamount", tdsamount.toString())
        Log.d("commissionamount", withouttdscommissionamount.toString())
        Log.d("actualcommission", actualcommission.toString())

        val transferAmountToAgentsReq = TransferToAgentReq(
            merchantCode = mStash!!.getStringValue(Constants.MerchantId, ""),
            transferFrom = "Admin",
            amountType = "Deposit",
            transIpAddress = mStash!!.getStringValue(Constants.deviceIPAddress, ""),
            remark = "Commission Deposit by $remarks ",
            transferTo = mStash!!.getStringValue(Constants.RegistrationId, ""),
            transferToMsg = "Your account has been credited by ₹ ${withouttdscommissionamount} due to a commission deposit on  ${
                mStash!!.getStringValue(
                    Constants.RegistrationId,
                    ""
                )
            } , with reference number ${response.data!!.refTransID}.",
            gstAmt = 0,
            parmUserName = mStash!!.getStringValue(Constants.RegistrationId, ""),
            servicesChargeGSTAmt = 0,
            servicesChargeWithoutGST = 0,
            actualTransactionAmount = withouttdscommissionamount?.toDouble() ?: 0.0,
            actualCommissionAmt = 0,
            commissionWithoutGST = 0,
            transferFromMsg = "Your account has been debited by ₹ ${withouttdscommissionamount} due to a commission deposit on ${
                mStash!!.getStringValue(
                    Constants.RegistrationId,
                    ""
                )
            },with reference number ${response.data!!.refTransID}.",
            netCommissionAmt = 0,
            tdSAmt = tdsamount?.toDouble() ?: 0.0,
            servicesChargeAmt = 0,
            customerVirtualAddress = "",
            transferAmt = actualcommission?.toDouble() ?: 0.0,
        )

        Log.d("transferAmountToAgentcommissionreq", Gson().toJson(transferAmountToAgentsReq))

        getAllApiServiceViewModel.transferToAgentReq(transferAmountToAgentsReq)
            .observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    when (it.apiStatus) {

                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { commissionresp ->
                                    if (Constants.dialog != null && Constants.dialog.isShowing) {
                                        Constants.dialog.dismiss()
                                    }
                                    referenceId =
                                        response.data!!.refTransID!!  // payout referenceid
                                    Log.d(
                                        "transferAmountToAgentcommissionresp",
                                        Gson().toJson(response)
                                    )
                                    var intent = Intent(
                                        requireContext(),
                                        RechargeSuccessfulPageActivity::class.java
                                    )
                                    startActivity(intent)
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            Constants.dialog.dismiss()
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                                .show()
                        }

                        ApiStatus.LOADING -> {
                            Constants.OpenPopUpForVeryfyOTP(requireContext())
                        }

                    }
                }
            }
    }


    private fun getTransferAmountToAgentWithCalRes(response: TransferAmountToAgentsRes) {
        if (response.isSuccess == true) {
            Constants.dialog.dismiss()
            referenceId = response.data!!.refTransID!!  // payout referenceid
            var intent = Intent(requireContext(), RechargeSuccessfulPageActivity::class.java)
            startActivity(intent)
        } else {
            Constants.dialog.dismiss()
            toast(response.returnMessage.toString())
        }
    }


    fun hitApiForRechargeCategory() {
        // new changes done by Annu

        var categoryreq = RechargeCategoryReq(
            registerationID = mStash?.getStringValue(Constants.MerchantId, "") /*"AOP-554"*/
        )

        MobileRechargeViewModel.getRechargeCategoryRequest(categoryreq)
            .observe(requireActivity()) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    uploadDataOnFirebaseConsole(Gson().toJson(response), "RechargeFragmentCategoryRequest", requireContext())
                                    if (response.status!!) {
                                        Log.d("RechargeCategoryRespo", Gson().toJson(response))
                                        //Toast.makeText(context,response.message.toString(),Toast.LENGTH_SHORT).show()
                                        if (response.data!!.size > 0) {
                                            var getdata = response.data
                                            getdata.forEach { it ->
                                                if (it!!.displayName!!.lowercase().contains("prepaid") && rechargeType == "mobile") {
                                                    DisplayName = it!!.displayName
                                                    mStash!!.setStringValue(Constants.OperatorCategory, DisplayName)

                                                    if (DisplayName!!.isNotEmpty()) {
                                                        Constants.operatorName = ArrayList()
                                                        Constants.productIdList = ArrayList()
                                                        hitApiForRechargeOperatorNameList(DisplayName!!)
                                                    }

                                                }
                                                else {

                                                    if (it!!.displayName!!.lowercase().contains("dth") && rechargeType == "dth") {
                                                        DisplayName = it!!.displayName
                                                    }

                                                    mStash!!.setStringValue(Constants.OperatorCategory, DisplayName)

                                                    if (DisplayName!!.isNotEmpty()) {
                                                        Constants.operatorName = ArrayList()
                                                        Constants.productIdList = ArrayList()
                                                        hitApiForRechargeOperatorNameList(DisplayName!!)
                                                    }
                                                }

                                            }
                                        }
                                    }
                                    else {
                                        Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
                                        Constants.dialog.dismiss()
                                    }
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            Constants.dialog.dismiss()
                        }

                        ApiStatus.LOADING -> {
                            Constants.OpenPopUpForVeryfyOTP(requireContext())
                        }
                    }
                }

            }

    }


    fun CheckAndHitApi() {
        val caNumber = binding.etDTHBillNumber.text.toString().trim()
        val operator = binding.spOperator.selectedItem?.toString()
        val circle = binding.etCircle.selectedItem?.toString()

        if (caNumber.isNotEmpty() && !operator.isNullOrEmpty() && !circle.isNullOrEmpty()) {
            when (lastTriggeredBy) {
                "operator" -> {
                    hitApiForGetDTHInfo()
                }

                "circle" -> {
                    hitApiForGetDTHInfo()
                }

                "ca" -> {
                    hitApiForGetDTHInfo()
                }
            }
        }
    }


    fun hitApiForGetDTHInfo() {

        apiCalled = false
        operatorNameDTHList.forEach { item ->

            if (item.second.toString().equals(OperatorID)) {
                selectedOperatorDTHName = item.first
                Log.d("CheckOperator", "Match found: $selectedOperatorDTHName")
            }
        }

        var dthreq = DthInfoReq(
            registrationId = mStash?.getStringValue(Constants.MerchantId, "") /*"AOP-554"*/,
            circle = binding.etCircle.selectedItem.toString(),
            operator = selectedOperatorDTHName,
            opnumber = binding.etDTHBillNumber.text.toString()
        )

        Log.d("dthreq", Gson().toJson(dthreq))

        MobileRechargeViewModel.getDthInfoRequest(dthreq).observe(requireActivity()) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                uploadDataOnFirebaseConsole(Gson().toJson(response), "RechargeFragmentDthInfoRequest", requireContext())
                                if (response.status!!) {
                                    Log.d("dthRespo", Gson().toJson(response))
                                    if (response.data!!.size > 0) {
                                        if (Constants.dialog != null && Constants.dialog.isShowing) {
                                            Constants.dialog.dismiss()
                                        }
                                        var getdata = response.data
                                        DthInfoList = getdata!!.toMutableList()!!
                                        binding.dthinfo.visibility = View.VISIBLE
                                        var adapter = DTHViewInfoAdapter(requireContext(), DthInfoList,
                                            clickListener = DTHViewInfoAdapter.ClickListener { it ->
                                                binding.etAmount.setText(it.toString())
                                            })
                                        binding.dthinfo.adapter = adapter
                                        adapter.notifyDataSetChanged()
                                    }
                                } else {
                                    if (Constants.dialog != null && Constants.dialog.isShowing) {
                                        Constants.dialog.dismiss()
                                    }
                                    binding.dthinfo.visibility = View.GONE
                                    Toast.makeText(requireContext(), "Must check operator or circle may be incorrect", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        Constants.dialog.dismiss()
                    }

                    ApiStatus.LOADING -> {
                        Constants.OpenPopUpForVeryfyOTP(requireContext())
                    }
                }
            }

        }

    }


}