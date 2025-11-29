package com.bos.payment.appName.ui.view.Dashboard.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bos.payment.appName.R
import com.bos.payment.appName.adapter.MenuListAdapter
import com.bos.payment.appName.constant.ConstantClass
import com.bos.payment.appName.constant.CustomFuseLocationActivity
import com.bos.payment.appName.data.model.justpaymodel.CheckBankDetailsModel
import com.bos.payment.appName.data.model.justpaymodel.MoneyTransferServicesModel
import com.bos.payment.appName.data.model.justpedashboard.DashboardBannerListModel
import com.bos.payment.appName.data.model.justpedashboard.RetailerWiseServicesRequest
import com.bos.payment.appName.data.model.menuList.Data
import com.bos.payment.appName.data.model.menuList.GetAllMenuListReq
import com.bos.payment.appName.data.model.menuList.GetAllMenuListRes
import com.bos.payment.appName.data.model.merchant.merchantList.GetApiListMarchentWiseReq
import com.bos.payment.appName.data.model.merchant.merchantList.GetApiListMarchentWiseRes
import com.bos.payment.appName.data.model.supportmanagement.NavParentItem
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.GetBalanceReq
import com.bos.payment.appName.data.model.walletBalance.walletBalanceCal.GetBalanceRes
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.repository.MoneyTransferRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.data.viewModelFactory.MoneyTransferViewModelFactory
import com.bos.payment.appName.databinding.ActivityJustPeDashboardBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.adapter.DashboardServicesAdapter
import com.bos.payment.appName.ui.adapter.ImageSliderAdapter
import com.bos.payment.appName.ui.adapter.NavAdapter
import com.bos.payment.appName.ui.view.Dashboard.ToSelf.ToSelfMoneyTransferPage
import com.bos.payment.appName.ui.view.Dashboard.activity.AllServicesSelectionActivity.Companion.checkType
import com.bos.payment.appName.ui.view.Dashboard.dmt.DMTMobileActivity
import com.bos.payment.appName.ui.view.Dashboard.tomobile.ToMobileSendMoneyActivity
import com.bos.payment.appName.ui.view.LoginActivity
import com.bos.payment.appName.ui.view.fragment.SideNavigationBankDetailsSheet
import com.bos.payment.appName.ui.view.fragment.SideNavigationBankDetailsSheet.Companion.Address
import com.bos.payment.appName.ui.view.fragment.SideNavigationBankDetailsSheet.Companion.cityName
import com.bos.payment.appName.ui.view.fragment.SideNavigationBankDetailsSheet.Companion.district
import com.bos.payment.appName.ui.view.fragment.SideNavigationBankDetailsSheet.Companion.latt
import com.bos.payment.appName.ui.view.fragment.SideNavigationBankDetailsSheet.Companion.long
import com.bos.payment.appName.ui.view.fragment.SideNavigationBankDetailsSheet.Companion.pincode
import com.bos.payment.appName.ui.view.fragment.SideNavigationBankDetailsSheet.Companion.statecode
import com.bos.payment.appName.ui.view.makepayment.AdminBankListActivity
import com.bos.payment.appName.ui.view.makepayment.MakePaymentActivity
import com.bos.payment.appName.ui.view.moneyTransfer.ScannerFragment
import com.bos.payment.appName.ui.view.supportmanagement.TicketStatus
import com.bos.payment.appName.ui.view.travel.flightBooking.activity.FlightFilterActivity.Companion.TAG
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.ui.viewmodel.MoneyTransferViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Constants.BILLRECHARGE
import com.bos.payment.appName.utils.Constants.BillRechargeCard
import com.bos.payment.appName.utils.Constants.FINANCESERVICES
import com.bos.payment.appName.utils.Constants.FinanceCard
import com.bos.payment.appName.utils.Constants.RETAILERALLSERVICES
import com.bos.payment.appName.utils.Constants.TRAVEL
import com.bos.payment.appName.utils.Constants.TravelCard
import com.bos.payment.appName.utils.Constants.getRetailerAllServices
import com.bos.payment.appName.utils.Constants.maskWithEllipsis
import com.bos.payment.appName.utils.Constants.uploadDataOnFirebaseConsole
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.PD
import com.bos.payment.appName.utils.Utils.generateQrBitmap
import com.bos.payment.appName.utils.Utils.getScreenshotFromView
import com.bos.payment.appName.utils.Utils.getStateCode
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.bos.payment.appName.utils.Utils.toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Locale


class JustPeDashboard : AppCompatActivity() {
    lateinit var binding : ActivityJustPeDashboardBinding
    private var mStash: MStash? = null
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    private  var getMenuListData: ArrayList<Data> = arrayListOf()
    private lateinit var menuListAdapter: MenuListAdapter
   // private lateinit var pd: AlertDialog
    private var doubleBackToExitPressedOnce: Boolean = false
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var viewModel: MoneyTransferViewModel
    lateinit var storageRef : StorageReference
    private var fingerPrint: Boolean = false

    // var serviceslist : MutableList<MoneyTransferServicesModel> = mutableListOf()
    private var fullServiceList = mutableListOf<MoneyTransferServicesModel>()
    private var displayedServiceList = mutableListOf<MoneyTransferServicesModel>()
    private var isExpanded = false


    lateinit var moneyTransferServicesadapter : DashboardServicesAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    private var customFuseLocation: CustomFuseLocationActivity? = null


    private val imageList = listOf(R.drawable.image1, R.drawable.image2, R.drawable.image3)

    val items = listOf(NavParentItem("Support Management", listOf("Ticket Status")))
    private lateinit var navAdapter: NavAdapter


    companion object{
         var QRBimap : Bitmap? = null
         var vpa : String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityJustPeDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()

        if (!checkLocationPermission()) {
            requestLocationPermission()
        }
        else {
            // Permission already granted
            // You can now access the location
        }

        init()

        getfirebasetoken()
        hitApiForBannerRetailer("retailer")
        startMerchantListPolling(mStash!!.getStringValue(Constants.MerchantId, "").toString())
        setMoneyTransferServices()
        setclickListner()

    }


    private fun startMerchantListPolling(merchantId: String) {
        stopPolling() // Ensure no duplicate polling occurs
        coroutineScope = CoroutineScope(Dispatchers.Main + Job())

        coroutineScope.launch {
            while (isActive) {
                getAllMerchantList(merchantId)
                delay(3000L) // Poll every 3 seconds
            }
        }
    }


    private fun stopPolling() {
        coroutineScope.cancel() // Cancel existing coroutine scope to stop polling
    }


    private fun getAllMerchantList(merchantId: String) {
        runIfConnected {
            val getAllMerchantList = GetApiListMarchentWiseReq(MarchentID = merchantId)
            Log.d("ApiStatus", Gson().toJson(getAllMerchantList))
            viewModel.getAllMerchantList(getAllMerchantList).observe(this) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                it.data?.let { users ->
                                    users.body()?.let { it1 ->
                                        getAllMerchantListRes(it1, merchantId)
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
    }


    private fun getAllMerchantListRes(response: GetApiListMarchentWiseRes, merchantId: String) {
        if(binding.appBarDashBoard.deskdesign.swipeRefreshLayout.isRefreshing){
            binding.appBarDashBoard.deskdesign.swipeRefreshLayout.isRefreshing = false
        }
        if (response.isSuccess == true) {
            if (response.data.isEmpty()) {
                Log.e("MerchantListError", "response.data is null or empty")
                return
            }

            if (Constants.merchantIdList == null) {
                Constants.merchantIdList = mutableListOf()
            }

            Log.d("MerchantList",Gson().toJson(response))

            response.data.forEach { item ->
                val featureCode = item.featureCode?.trim() ?: ""
                val featureName = item.featureName ?: ""

                if (featureCode.isNotEmpty()) {
                    Constants.merchantIdList?.add(featureCode)
                    mStash!!.setStringValue(Constants.APIName, featureName)
                    mStash!!.setStringValue(Constants.MerchantList, Constants.merchantIdList.toString())
                    Log.d("APINameList_Dash", mStash!!.getStringValue(Constants.MerchantList, "").toString())
                } else {
                    Log.w("MerchantListWarning", "Empty or null featureCode at index ")
                }
            }

        } else {
            toast(response.returnMessage.orEmpty())
        }
    }

    fun init(){
        mStash = MStash.getInstance(this@JustPeDashboard)

        getFuseLocation()

        viewModel = ViewModelProvider(this, MoneyTransferViewModelFactory(MoneyTransferRepository(RetrofitClient.apiAllInterface)))[MoneyTransferViewModel::class.java]
        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]

        binding.nav.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        menuListAdapter = MenuListAdapter(this, getMenuListData, fragmentManager = supportFragmentManager, containerId = R.id.fragment)
        binding.nav.recyclerView.adapter = menuListAdapter

        fingerPrint = mStash!!.getBoolanValue(Constants.fingerPrintAction.toString(), false)
        binding.nav.switchButton.isChecked = fingerPrint

        binding.nav.customerName.text =  "Hello ${mStash!!.getStringValue(Constants.retailerName, "")}"

    }

    fun  setMoneyTransferServices(){
        fullServiceList.clear()
        fullServiceList.add(MoneyTransferServicesModel(R.drawable.dmticon, getString(R.string.dmt), "F0115", ""))
        fullServiceList.add(MoneyTransferServicesModel(R.drawable.paymobileicon, getString(R.string.paytomob), "", ""))
        fullServiceList.add(MoneyTransferServicesModel(R.drawable.transfericon, getString(R.string.selftrans), "", ""))
        fullServiceList.add(MoneyTransferServicesModel(R.drawable.upidicon, getString(R.string.payupi), "", ""))


        moneyTransferServicesadapter = DashboardServicesAdapter(displayedServiceList, this@JustPeDashboard,
            onServiceClick = { item ->
                if (item.name == getString(R.string.dmt)) startActivity(Intent(this, DMTMobileActivity::class.java))
                if(item.name== getString(R.string.paytomob)) startActivity(Intent(this, ToMobileSendMoneyActivity::class.java))
                if(item.name== getString(R.string.selftrans)) startActivity(Intent(this, ToSelfMoneyTransferPage::class.java))
            },
            onSeeMoreClick = {
                //toggleSeeMore()
                isExpanded = !isExpanded
                updateDisplayedList()
            }
        )

        binding.appBarDashBoard.deskdesign.moneytransferlist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.appBarDashBoard.deskdesign.moneytransferlist.adapter = moneyTransferServicesadapter

        updateDisplayedList()

    }


    private fun updateDisplayedList() {
        displayedServiceList.clear()

        if (!isExpanded && fullServiceList.size > 4) {
            // show first 2 and "See More"
            displayedServiceList.addAll(fullServiceList.take(4))
            displayedServiceList.add(MoneyTransferServicesModel(
                    image = 0,
                    name = "See More",
                    featurecode = "SEE_MORE",
                    activeYN = "Y"
                )
            )
        } else if (isExpanded) {
            // show all + "See Less"
            displayedServiceList.addAll(fullServiceList)
            displayedServiceList.add(
                MoneyTransferServicesModel(
                    image = 0,
                    name = "See Less",
                    featurecode = "SEE_MORE",
                    activeYN = "Y"
                )
            )
        } else {
            // if less than 2 items — show all only
            displayedServiceList.addAll(fullServiceList)
        }

        Log.d("updateDisplayedList", "displayedServiceList size: ${displayedServiceList.size}")
        moneyTransferServicesadapter.updateList(displayedServiceList)

    }


    fun setQRCodeWithBankDetailsCodition(){
        if(mStash!!.getStringValue(Constants.ISQRCodeGenerated,"No").equals("No",ignoreCase = true)){
            binding.nav.qrcodetxt.text= "Generate QR Code"
            binding.nav.lockiconlayout.visibility= View.VISIBLE
            binding.nav.sharelayout.visibility=View.GONE
        }
        else{
            binding.nav.lockiconlayout.visibility= View.GONE
            binding.nav.sharelayout.visibility=View.VISIBLE
            binding.nav.qrcodetxt.text= "View Bank Details"
            binding.nav.vpaid.text=vpa
            var accountnumber =  maskWithEllipsis(mStash!!.getStringValue(Constants.SettlementAccountNumber,""))
            binding.nav.accountnumber.text= "Account No : ${accountnumber}"
            binding.nav.QRCode.setImageBitmap(QRBimap)

            binding.nav.copytext.setOnClickListener {
                val textToCopy = "${binding.nav.vpaid.text.toString()}"
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("text", textToCopy)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun setclickListner(){

        binding.appBarDashBoard.deskdesign.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

        binding.nav.shareqrcode.setOnClickListener {
            if(QRBimap!=null)
            shareBitmap(QRBimap!!,this)
        }


        binding.nav.saveqrcode.setOnClickListener {
            if(QRBimap!=null){
              var qrbitmap =  getScreenshotFromView(binding.nav.qrlayout)
                val imageUri =  saveBitmapToGallery(this, qrbitmap!!,"QR Code")
                if (imageUri != null) {
                    Toast.makeText(this, "Image saved to gallery!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save image!", Toast.LENGTH_SHORT).show()
                }
            }

        }

        binding.nav.bankbottomsheetcall.setOnClickListener {
            val bottomfrag = SideNavigationBankDetailsSheet()
            supportFragmentManager.let {
                bottomfrag.show(it, SideNavigationBankDetailsSheet.TAG)
            }
        }

        binding.nav.llLogout.setOnClickListener {
            binding.drawer.closeDrawer(GravityCompat.START)
            mStash!!.clear()
            TravelCard=false
            FinanceCard=false
            BillRechargeCard=false
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.nav.switchButton.setOnClickListener {
            fingerPrint = !fingerPrint // Toggle the value
            mStash!!.setBooleanValue(Constants.fingerPrintAction.toString(), fingerPrint)
        }

        //ProfileUpdated

        binding.nav.ownerPhoto.setOnClickListener {
           startActivity(Intent(this@JustPeDashboard,ProfileUpdated::class.java))
        }

        binding.appBarDashBoard.deskdesign.menuicon.setOnClickListener {
            getAllMenuList()
            binding.drawer.openDrawer(GravityCompat.START)
        }

        binding.appBarDashBoard.deskdesign.financialservicesimageview.setOnClickListener {
            checkType = FINANCESERVICES
            startActivity(Intent(this@JustPeDashboard,AllServicesSelectionActivity::class.java))
        }

        binding.appBarDashBoard.deskdesign.billrechargeimageview.setOnClickListener {
            checkType = BILLRECHARGE
            startActivity(Intent(this@JustPeDashboard,AllServicesSelectionActivity::class.java))
        }

        binding.appBarDashBoard.deskdesign.travelimageview.setOnClickListener {
            checkType = TRAVEL
            startActivity(Intent(this@JustPeDashboard,AllServicesSelectionActivity::class.java))
        }

        binding.appBarDashBoard.deskdesign.scanpaycard.setOnClickListener {
            startActivity(Intent(this, ScannerFragment::class.java))
        }

    }

    fun refreshData(){
        hitApiForBannerRetailer("retailer")
        hitApiForServicesRequest()
        startMerchantListPolling(mStash!!.getStringValue(Constants.MerchantId, "").toString())
    }

    fun setViewPagerData(response:DashboardBannerListModel){
        // Example: taking only 2 banners from API response

        if (response.isSuccess == true && !response.data.isNullOrEmpty()) {

            // Build banner list from API response
            val bannerList = response.data!!.map { item ->
                BannerItem(
                    imagePath = item!!.imagePath ?: "",
                    urlRedirect = item.urlRedirect ?: ""
                )
            } // only take first two banners

            // Set adapter to ViewPager
            binding.appBarDashBoard.deskdesign.viewpager.apply { adapter = ImageSliderAdapter(bannerList)
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
            }

            // Track current page
            binding.appBarDashBoard.deskdesign.viewpager.registerOnPageChangeCallback(
                object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        currentPage = position
                    }
                }
            )

            // Start auto-slide
            startAutoSlide()

        } else {
            Log.e("DashboardBanner", "Banner fetch failed or empty: ${response.returnMessage}")
        }


    }

    private fun startAutoSlide() {
        val runnable = object : Runnable {
            override fun run() {
                if (currentPage == imageList.size) {
                    currentPage = 0
                }
                binding.appBarDashBoard.deskdesign.viewpager.setCurrentItem(currentPage++, true)
                handler.postDelayed(this, 3000) // change every 3 seconds
            }
        }

        handler.postDelayed(runnable, 3000)

    }


    private fun getAllMenuList() {
        val getAllMenuListReq = GetAllMenuListReq(
            loginId = mStash!!.getStringValue(Constants.RegistrationId, ""),
            applicationCode = "B2B"
        )

           Log.d("getAllMenuListReq", Gson().toJson(getAllMenuListReq))

           getAllApiServiceViewModel.getAllMenuList(getAllMenuListReq).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        Constants.dialog.dismiss()
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("MenuList",Gson().toJson(response))
                                getAllMenuListRes(response)
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


    @SuppressLint("NotifyDataSetChanged")
    private fun getAllMenuListRes(response: GetAllMenuListRes) {
        if (response.isSuccess == true) {
            // Clear the current menu list
            getMenuListData.clear()

            // Map to hold child menus by parentMenuCode
            val childMenusMap = mutableMapOf<String, MutableList<Data>>()

            //Separate parent menus and child menus
            val parentMenus = mutableListOf<Data>()
            val childMenus = mutableListOf<Data>()

            // Separate parent menus and child menus
            response.data.forEach { menuItem ->
                if (menuItem.parentMenuCode.isNullOrEmpty()) {
                    parentMenus.add(menuItem)
                } else {
                    childMenusMap.getOrPut(menuItem.parentMenuCode!!)
                    { mutableListOf() }.add(menuItem)
                    childMenus.add(menuItem)
                }
            }

            //Filter parent menus: keep only those that have at least one child menu
            val validParentMenus = parentMenus.filter { parentMenu ->
                childMenus.any { it.parentMenuCode == parentMenu.childMenuCode}
            }

            //Attach child menus to parent menu
            validParentMenus.forEach { parentMenu ->
                parentMenu.childMenus = childMenusMap[parentMenu.childMenuCode]?: mutableListOf()
            }

            //Add filtered child menu to parent menus
            getMenuListData.addAll(validParentMenus)
            // Notify the adapter
            menuListAdapter.notifyDataSetChanged()
        } else {
            // Show an error message
            Toast.makeText(this, response.returnMessage ?: "Error occurred", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getWalletBalance() {
        this.runIfConnected {
            val walletBalanceReq = GetBalanceReq(
                parmUser = mStash!!.getStringValue(Constants.RegistrationId, ""),
                flag = "CreditBalance"
            )
            Log.d("checkWallet",Gson().toJson(walletBalanceReq))
            getAllApiServiceViewModel.getWalletBalance(walletBalanceReq).observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            Constants.dialog.dismiss()
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
                            Constants.OpenPopUpForVeryfyOTP(this)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun getAllWalletBalanceRes(response: GetBalanceRes) {
        val data = Gson().toJson(response)
        uploadDataOnFirebaseConsole(data,"DashboardWalletBalance",this@JustPeDashboard)

        if (response.isSuccess == true) {
            binding.nav.walletBalance.text = "₹" + response.data[0].result.toString()
            Log.d("actualBalance", response.data[0].result.toString())
        } else {
            toast(response.returnMessage.toString())
        }


    }


    fun getfirebasetoken(){
        FirebaseFirestore.setLoggingEnabled(true)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d(TAG, "FCM Token: $token")

            // Send this token to your server if you need to send targeted notifications
            // sendRegistrationToServer(token)
        }
        val storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
    }


    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }


    override fun onResume() {
        super.onResume()
        startAutoSlide()
        getWalletBalance()
        hitApiForServicesRequest()
        getBankDetails(mStash!!.getStringValue(Constants.RegistrationId, "").toString())
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finish()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, R.string.double_back_press_msg, Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }


    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }


    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissions() {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NOTIFICATION_POLICY
        )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // good to go
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                // You can now access the location
            } else {
                // Permission denied
                // Handle accordingly (e.g., show an error message or disable location functionality)
            }
        }
    }


    fun hitApiForBannerRetailer(agentType:String){
        binding.appBarDashBoard.deskdesign.viewpager.visibility= View.VISIBLE
        var retailerCode = mStash!!.getStringValue(Constants.RegistrationId,"")
        var merchantCode = mStash!!.getStringValue(Constants.MerchantId,"")
        var adminCode = mStash!!.getStringValue(Constants.AdminCode,"")
        var task = "GET"
        var rid = 0


        getAllApiServiceViewModel.getDashboardBannerRequest(rid, task, merchantCode!!, adminCode!!,retailerCode!! , agentType).observe(this) { resource ->

            when (resource.apiStatus) {

                ApiStatus.SUCCESS -> {


                    val response = resource.data
                    if (response?.isSuccess == true) {
                        Log.d("BannerList", Gson().toJson(response))
                        setViewPagerData(response)
                    } else {
                        hitApiForBannerAdmin("admin")
                        Log.d("ERRORmessage", response?.returnMessage!!)
                        //Toast.makeText(this, response?.returnMessage ?: "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }


                ApiStatus.ERROR -> {
                    hitApiForBannerAdmin("admin")

                    //Toast.makeText(this, resource.message ?: "Error occurred", Toast.LENGTH_SHORT).show()
                }

                ApiStatus.LOADING -> {

                }
            }
        }


    }


    fun hitApiForBannerAdmin(agentType:String){
        var retailerCode = mStash!!.getStringValue(Constants.AdminCode,"")
        var merchantCode = mStash!!.getStringValue(Constants.MerchantId,"")
        var adminCode = mStash!!.getStringValue(Constants.AdminCode,"")
        var task = "GET"
        var rid = 0

        getAllApiServiceViewModel.getDashboardBannerRequest(rid, task, merchantCode!!, adminCode!!,retailerCode!! , agentType).observe(this) { resource ->

            when (resource.apiStatus) {

                ApiStatus.SUCCESS -> {
                    val response = resource.data

                    if (response?.isSuccess == true) {
                        Log.d("AdminBannerList", Gson().toJson(response))
                        binding.appBarDashBoard.deskdesign.viewpager.visibility= View.VISIBLE
                        setViewPagerData(response)
                    } else {
                        binding.appBarDashBoard.deskdesign.viewpager.visibility= View.GONE
                        Log.d("ERRORmessage", response?.returnMessage!!)
                        Toast.makeText(this, response?.returnMessage ?: "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }

                ApiStatus.ERROR -> {
                    binding.appBarDashBoard.deskdesign.viewpager.visibility= View.GONE
                    Toast.makeText(this, resource.message ?: "Error occurred", Toast.LENGTH_SHORT).show()
                }

                ApiStatus.LOADING -> {
                }
            }
        }


    }


    fun hitApiForServicesRequest(){
        var retailerCode = mStash!!.getStringValue(Constants.RegistrationId,"")
        var merchantCode =  mStash!!.getStringValue(Constants.MerchantId,"")

        val servicesRequest = RetailerWiseServicesRequest(
            retailerCode = retailerCode!!,
            merchantCode = merchantCode!!
        )

        Log.d("RetailerWiseServicereq", Gson().toJson(servicesRequest))
        getAllApiServiceViewModel.getRetailerWiseServicesReq(servicesRequest).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {

                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("RetailerWiseServicesresp",Gson().toJson(response))
                                if(response.isSuccess!!){
                                    var serviceslist = response.data

                                    RETAILERALLSERVICES= serviceslist

                                    val allServices = getRetailerAllServices()
                                    val serviceMap = allServices.associateBy { it.featureCode }


                                    val matchedServices = serviceslist
                                        ?.filter { it!!.activeYN.equals("Y", ignoreCase = true) }   // only active services
                                        ?.mapNotNull { apiItem -> serviceMap[apiItem!!.featureCode] }


                                    // Check for specific card types by featureCode
                                    matchedServices?.forEach { item ->
                                        when (item.featureCode) {
                                            "F0133", "F0134" -> TravelCard = true
                                            "F0116", "F0141","F0125" -> FinanceCard = true
                                            "F0140", "F0116" -> BillRechargeCard = true
                                        }
                                    }


                                    if(TravelCard){
                                        binding.appBarDashBoard.deskdesign.travelimageview.visibility= View.VISIBLE
                                    }else{
                                        binding.appBarDashBoard.deskdesign.travelimageview.visibility= View.GONE
                                    }


                                    if(FinanceCard){
                                        binding.appBarDashBoard.deskdesign.financialservicesimageview.visibility= View.VISIBLE
                                    }else{
                                        binding.appBarDashBoard.deskdesign.financialservicesimageview.visibility= View.GONE
                                    }

                                    if(BillRechargeCard){
                                        binding.appBarDashBoard.deskdesign.billrechargeimageview.visibility= View.VISIBLE
                                    }else{
                                        binding.appBarDashBoard.deskdesign.billrechargeimageview.visibility= View.GONE
                                    }

                                    Log.d("ServicesName", Gson().toJson(matchedServices))

                                }
                                else{
                                    binding.appBarDashBoard.deskdesign.travelimageview.visibility= View.GONE
                                    binding.appBarDashBoard.deskdesign.billrechargeimageview.visibility= View.GONE
                                    binding.appBarDashBoard.deskdesign.financialservicesimageview.visibility= View.GONE
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


    data class BannerItem(
        val imagePath: String,
        val urlRedirect: String
    )


    fun getBankDetails(retailerCode: String){
        val requestForBankDetails = CheckBankDetailsModel(reatilerCode =  retailerCode)
        Log.d("bankdetailereq", Gson().toJson(requestForBankDetails))

        getAllApiServiceViewModel.getBankDetails(requestForBankDetails).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {

                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("BankdetailsResponse",Gson().toJson(response))
                                if(response.isSuccess!!){
                                    var getdata = response.data

                                    if(getdata!=null){
                                        mStash!!.setStringValue(Constants.SettlementAccountName, getdata[0]!!.settlementAccountName)
                                        mStash!!.setStringValue(Constants.SettlementAccountNumber, getdata[0]!!.settlementAccountNumber)
                                        mStash!!.setStringValue(Constants.SettlementAccountIfsc, getdata[0]!!.settlementAccountIfsc)
                                        mStash!!.setStringValue(Constants.SellerIdentifier, getdata[0]!!.sellerIdentifier)
                                        mStash!!.setStringValue(Constants.BankMobileNumber, getdata[0]!!.mobileNumber)
                                        mStash!!.setStringValue(Constants.EmailId, getdata[0]!!.emailId)
                                        mStash!!.setStringValue(Constants.BankAccountType, getdata[0]!!.accountType)
                                        mStash!!.setStringValue(Constants.CreatedBy, getdata[0]!!.createdBy)
                                        mStash!!.setStringValue(Constants.ISQRCodeActivate, getdata[0]!!.isQRCodeActivate)
                                        mStash!!.setStringValue(Constants.ISQRCodeGenerated, getdata[0]!!.isQRCodeGenerated)
                                        mStash!!.setStringValue(Constants.VPAid, getdata[0]!!.vpaid)
                                        QRBimap = generateQrBitmap(getdata[0]!!.staticQR!!, 800)
                                        vpa =  getdata[0]!!.vpaid
                                        setQRCodeWithBankDetailsCodition()
                                    }
                                }
                                else{

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


    private fun getFuseLocation() {

        customFuseLocation = CustomFuseLocationActivity(this, this) { mCurrentLocation ->
            latt = mCurrentLocation.latitude
            long = mCurrentLocation.longitude

            Log.d("Lat Long", "Lat: $latt : Long: $long")
            getAddressFromLatLng(this, latt, long)
        }


    }


    fun getAddressFromLatLng(context: Context, latitude: Double, longitude: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]

                Address = address.getAddressLine(0) ?: ""
                cityName = address.locality ?: ""
                district = address.subAdminArea ?: ""
                val state = address.adminArea ?: ""
                statecode = getStateCode(state ?: "")
                pincode = address.postalCode ?: ""

                // You can return full formatted string
                 Log.d("Address", "Address: $Address\nCity: $cityName\nDistrict: $district\nState: $state\nPincode: $pincode")
                "Address: $Address\nCity: $cityName\nDistrict: $district\nState: $state\nPincode: $pincode"
            } else {
                "Address not found"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.message}"
        }
    }


    private fun shareBitmap(bitmap: Bitmap, context: Context) {
        // Save bitmap to cache directory
        val cachePath = File(context.cacheDir, "shared_images")
        cachePath.mkdirs()
        val file = File(cachePath, "shared_image.png")
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.close()

        // Get content URI using FileProvider
        val imageUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        // Create share intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Start chooser
        context.startActivity(Intent.createChooser(shareIntent, "Share Image Via"))
    }


    @SuppressLint("Recycle")
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, bitmap.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyAppImages") // Folder name in gallery
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val contentResolver = context.contentResolver
        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(uri, contentValues, null, null)
        }

        return imageUri
    }


}