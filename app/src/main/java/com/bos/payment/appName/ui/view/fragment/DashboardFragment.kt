package com.bos.payment.appName.ui.view.fragment

import NotificationPagerAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.bos.payment.appName.R
import com.bos.payment.appName.adapter.AutoScrollViewPagerAdapter
import com.bos.payment.appName.adapter.NotificationDiscountAdapter
import com.bos.payment.appName.data.model.merchant.activeInActiveStatus.GetAPIActiveInactiveStatusReq
import com.bos.payment.appName.data.model.merchant.activeInActiveStatus.GetAPIActiveInactiveStatusRes
import com.bos.payment.appName.data.model.merchant.merchantList.GetApiListMarchentWiseReq
import com.bos.payment.appName.data.model.merchant.merchantList.GetApiListMarchentWiseRes
import com.bos.payment.appName.data.model.notification.GetNotificationReq
import com.bos.payment.appName.data.model.notification.GetNotificationRes
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.repository.MobileRechargeRepository
import com.bos.payment.appName.data.repository.MoneyTransferRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.data.viewModelFactory.MobileRechargeViewModelFactory
import com.bos.payment.appName.data.viewModelFactory.MoneyTransferViewModelFactory
import com.bos.payment.appName.databinding.FragmentDashboardBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.CreditCardDetailsFragment
import com.bos.payment.appName.ui.view.Dashboard.rechargefragment.RechargeFragment
import com.bos.payment.appName.ui.view.Dashboard.activity.RechargeHistory
import com.bos.payment.appName.ui.view.Dashboard.dmt.DMTMobileActivity
import com.bos.payment.appName.ui.view.idfcPayout.Payout
import com.bos.payment.appName.ui.view.moneyTransfer.ScannerFragment
import com.bos.payment.appName.ui.view.travel.busactivity.BookingTravel
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.ui.viewmodel.GetAllMobileRechargeViewModel
import com.bos.payment.appName.ui.viewmodel.MoneyTransferViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.PD
import com.bos.payment.appName.utils.Utils.isLocationEnabled
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.bos.payment.appName.utils.Utils.toast
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var context: Context
    private lateinit var autoScrollViewPagerAdapter: AutoScrollViewPagerAdapter
    private lateinit var notificationDiscountAdapter: NotificationDiscountAdapter
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var mStash: MStash
    private lateinit var pd: AlertDialog
    private lateinit var viewPager: ViewPager
    private var notificationListData = ArrayList<GetNotificationRes>()
    private lateinit var viewModel: MoneyTransferViewModel
    private var requestOption: RequestOptions? = null
    private var doubleBackToExitPressedOnce: Boolean = false
    private var list = mutableListOf<GetNotificationRes>()
    private lateinit var viewPagerAdapter: NotificationPagerAdapter
    private lateinit var indicatorLayout: LinearLayout
    private lateinit var dots: Array<ImageView?>
    private lateinit var viewpager2: ViewPager2
    private var availCredit: String = ""
    private lateinit var pageChangeListener: ViewPager2.OnPageChangeCallback
    private var coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    private lateinit var MobileRechargeViewModel: GetAllMobileRechargeViewModel

    private val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(8, 0, 8, 0) }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

//        setViewPager()
        checkPermissions()
        if (!checkLocationPermission()) {
            requestLocationPermission()
        } else {
            // Permission already granted
            // You can now access the location
        }

        initView()
        startMerchantListPolling(mStash.getStringValue(Constants.MerchantId, "").toString())
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
        getAllNotification()
        btnListener()

        // Enqueue the Worker to periodically check the API status
//        enqueueStatusCheckWorker()
        return binding.root
    }

    private fun refreshData() {
        stopPolling()
        // Refresh the merchant list and stop the swipe animation
        startMerchantListPolling(mStash.getStringValue(Constants.MerchantId, "").orEmpty())
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun startMerchantListPolling(merchantId: String) {
        stopPolling() // Ensure no duplicate polling occurs
        // Cancel any previous coroutine to prevent multiple polling loops
//        coroutineScope.cancel()
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
        requireContext().runIfConnected {
            val getAllMerchantList = GetApiListMarchentWiseReq(MarchentID = merchantId)
            Log.d("ApiStatus", Gson().toJson(getAllMerchantList))
            viewModel.getAllMerchantList(getAllMerchantList)
                .observe(viewLifecycleOwner) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                pd.dismiss()
                                it.data?.let { users ->
                                    users.body()?.let { it1 ->
                                        getAllMerchantListRes(it1, merchantId)
                                    }
                                }
                                // Stop the swipe-to-refresh animation after the data is loaded
                            }

                            ApiStatus.ERROR -> {
                                pd.dismiss()
                                // Stop the swipe-to-refresh animation if an error occurs
//                        binding.swipeRefreshLayout.isRefreshing = false

                            }

                            ApiStatus.LOADING -> {
                                pd.dismiss()
                            }
                        }
                    }
                }
        }
    }

    private fun getAllMerchantListRes(response: GetApiListMarchentWiseRes, merchantId: String) {
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
                    mStash.setStringValue(Constants.APIName, featureName)
                    mStash.setStringValue(Constants.MerchantList, Constants.merchantIdList.toString())

                    Log.d("APINameList_Dash", mStash.getStringValue(Constants.MerchantList, "").toString())
                } else {
                    Log.w("MerchantListWarning", "Empty or null featureCode at index ")
                }
            }

        } else {
            toast(response.returnMessage.orEmpty())
        }
    }

    private fun getAllAPIRetailerWiseActiveInActiveStatus() {
        val getAPIActiveInactiveStatusReq = GetAPIActiveInactiveStatusReq(
            RegistrationId = mStash.getStringValue(Constants.RegistrationId, ""),
            CompanyCode = mStash.getStringValue(Constants.CompanyCode, "")
        )
        Log.d("getAPIActiveInactive", Gson().toJson(getAPIActiveInactiveStatusReq))
        viewModel.getAllAPIRetailerWiseActiveInActive(getAPIActiveInactiveStatusReq)
            .observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            pd.dismiss()
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    getAllAPIRetailerWiseActiveInActiveStatusRes(response)
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

    private fun getAllAPIRetailerWiseActiveInActiveStatusRes(response: GetAPIActiveInactiveStatusRes) {
        if (response.Status == true) {
            // Automatically update statuses based on API response
            with(mStash) {
                setStringValue(Constants.RechargeAPI_Status, response.RechargeAPIStatus.toString())
                setStringValue(
                    Constants.RechargeAPI_2_Status,
                    response.RechargeAPI2Status.toString()
                )
                setStringValue(
                    Constants.MoneyTransferAPI_Status,
                    response.MoneyTransferAPIStatus.toString()
                )
                setStringValue(
                    Constants.MoneyTransferAPI_2_Status,
                    response.MoneyTransferAPI2Status.toString()
                )
                setStringValue(Constants.Payout_API_Status, response.PayoutAPIStatus.toString())
                setStringValue(Constants.Payout_API_2_Status, response.PayoutAPI2Status.toString())
                setStringValue(Constants.Payin_API_Status, response.PayinAPIStatus.toString())
                setStringValue(Constants.Payin_API_2_Status, response.PayinAPI2Status.toString())
                setStringValue(Constants.Fastag_API_Status, response.FastagAPIStatus.toString())
                setStringValue(Constants.PANCardAPI_Status, response.PANCardAPIStatus.toString())
                setStringValue(Constants.AEPS_API_Status, response.AEPSAPIStatus.toString())
                setStringValue(
                    Constants.CreditCardAPI_Status,
                    response.CreditCardAPIStatus.toString()
                )
            }
        } else {
            toast(response.message.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopPolling() // Stop polling
    }

    private fun setDynamicLogo(companyCode: String) {
        val packageManager = requireActivity().packageManager
        val companyALauncher = ComponentName(requireActivity(), "com.bos.bos.app.UserType1Alias")
        val companyBLauncher = ComponentName(requireActivity(), "com.bos.bos.app.UserType1Alias")

        try {
            when (companyCode) {
                "CMP1347" -> {
                    try {
                        packageManager.setComponentEnabledSetting(
                            companyALauncher,
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP
                        )
                        packageManager.setComponentEnabledSetting(
                            companyBLauncher,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP
                        )

                    } catch (e: Exception) {
                        e.printStackTrace()
                        toast(e.message.toString())
                    }
                }

                "CMP1047" -> {
                    try {
                        packageManager.setComponentEnabledSetting(
                            companyALauncher,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP
                        )
                        packageManager.setComponentEnabledSetting(
                            companyBLauncher,
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        toast(e.message.toString())
                    }
                }

                else -> {
                    try {
                        // Disable both and show the default logo if needed
                        toast("Default")
                        packageManager.setComponentEnabledSetting(
                            companyALauncher,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP
                        )
                        packageManager.setComponentEnabledSetting(
                            companyBLauncher,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        toast(e.message.toString())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toast(e.message)
        }
    }

    //    private fun setupViewPager() {
//        viewPagerAdapter = ViewPagerAdapter(images)
//        viewPager.adapter = viewPagerAdapter
//
//
//        setupIndicators(images.size)
//        setCurrentIndicator(0)
//
//        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrolled(
//                position: Int,
//                positionOffset: Float,
//                positionOffsetPixels: Int
//            ) {
//            }
//
//            override fun onPageSelected(position: Int) {
//                setCurrentIndicator(position)
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {}
//        })
//    }
//
//    private fun setupIndicators(count: Int) {
//        dots = arrayOfNulls(count)
//        val params = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.WRAP_CONTENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        ).apply {
//            marginStart = 8
//            marginEnd = 8
//        }
//
//        for (i in 0 until count) {
//            dots[i] = ImageView(this).apply {
//                setImageResource(R.drawable.indicator_inactive)
//                layoutParams = params
//            }
//            indicatorLayout.addView(dots[i])
//        }
//    }
//
//    private fun setCurrentIndicator(index: Int) {
//        for (i in dots.indices) {
//            dots[i]?.setImageResource(
//                if (i == index) R.drawable.indicator_active else R.drawable.indicator_inactive
//            )
//        }
//    }
    private fun getAllNotification() {
        requireContext().runIfConnected {
            val getNotificationReq = GetNotificationReq(
                CompanyCode = mStash!!.getStringValue(Constants.CompanyCode, ""),
                AgentType = mStash!!.getStringValue(Constants.AgentType, "")
            )
            Log.d("getNotificationReq", Gson().toJson(getNotificationReq))

            viewModel.getNotification(getNotificationReq).observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            pd.dismiss()
                            it.data?.body()?.let { response ->
                                getAllNotificationRes(response)
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
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllNotificationRes(response: List<GetNotificationRes>?) {
        if (!response.isNullOrEmpty() && response[0].Status == true) {
            // Pass the entire response (list of images) to the adapter
            val imageUrls = response.map { it.NotificationPicUrl.toString() }

            try {
                Picasso.get()
                    .load(response[0].NotificationPicUrl.toString())
                    .error(R.drawable.noti_image)
                    .into(binding.image)
            } catch (e: Exception) {
                e.printStackTrace()
            }

//
//            // Set up the ViewPager2 adapter
//            val imageAdapter = ImageAdapter()
//            viewpager2.adapter = imageAdapter
//
//            // Submit the list of image URLs to the adapter
//            imageAdapter.submitList(imageUrls)
//
//            // Set up indicator dots for ViewPager2
//            setupViewPagerIndicator(imageUrls.size)

//            Toast.makeText(requireContext(), response[0].message, Toast.LENGTH_SHORT).show()


        } else {
            try {
                Picasso.get()
                    .load(R.drawable.noti_image)
                    .error(R.drawable.no_image)
                    .into(binding.image)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // Handle the case where there is no data or Status is false
            Toast.makeText(requireContext(), "Data not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupViewPagerIndicator(size: Int) {
        // Set up indicator dots for ViewPager2
        val dotsImage = Array(size) { ImageView(requireContext()) }
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = 8
            marginEnd = 8
        }

        dotsImage.forEach {
            it.setImageResource(R.drawable.indicator_inactive)
//            binding.slideDotLL.addView(it, params)
        }

        // Set the first dot to active
        dotsImage[0].setImageResource(R.drawable.indicator_active)

        // ViewPager2 page change listener to update the indicator
//        binding.viewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                dotsImage.forEachIndexed { index, imageView ->
//                    imageView.setImageResource(
//                        if (index == position) R.drawable.indicator_active
//                        else R.drawable.indicator_inactive
//                    )
//                }
//            }
//        })
    }


//private fun getAllNotification() {
//    val getNotificationReq = GetNotificationReq(
//        CompanyCode = mStash!!.getStringValue(Constants.CompanyCode, ""),
//        AgentType = mStash!!.getStringValue(Constants.AgentType, "")
//    )
//
//    // Logging the request for debugging purposes
//    Log.d("getNotificationReq", Gson().toJson(getNotificationReq))
//
//    // Observing LiveData from ViewModel
//    viewModel.getNotification(getNotificationReq).observe(viewLifecycleOwner) { resource ->
//        resource?.let {
//            when (it.apiStatus) {
//                ApiStatus.SUCCESS -> {
//                    // Dismiss loading dialog
//                    pd.dismiss()
//
//                    // Check if the response is not null and handle it
//                    it.data?.let { response ->
//                        Log.d("getNotificationReq", response.body().toString())
//                        getAllNotificationRes(response.body())
//                    }
//                }
//
//                ApiStatus.ERROR -> {
//                    pd.dismiss()
//                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
//                }
//
//                ApiStatus.LOADING -> {
//                    pd.show()
//                }
//            }
//        }
//    }
//}
//
//    @SuppressLint("NotifyDataSetChanged")
//    private fun getAllNotificationRes(response: List<GetNotificationRes>?) {
//        response?.let {
//            if (it.isNotEmpty() && it[0].Status == true) {
//                // Create ViewPager adapter using the list of images from the response
//                val imageUrls = it.map { notification -> notification.NotificationPicUrl }
//                val viewPagerAdapter = NotificationPagerAdapter(requireContext(), imageUrls)
//                Log.d("getNotificationReq",response[0].NotificationPicUrl.toString())
//                viewPager.adapter = viewPagerAdapter
//
//                // Set up the ViewPager indicators
//                setupIndicators(it.size)
//                setCurrentIndicator(0)
//
//                viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
//
//                    override fun onPageSelected(position: Int) {
//                        setCurrentIndicator(position)
//                    }
//
//                    override fun onPageScrollStateChanged(state: Int) {}
//                })
//
//                // Show a message with the first response message
//                Toast.makeText(requireContext(), it[0].message, Toast.LENGTH_SHORT).show()
//
//            } else {
//                // Handle no data or invalid response
//                Picasso.get()
//                    .load(R.drawable.noti_image)
//                    .error(R.drawable.no_image)
//                    .into(binding.image)
//
//                Log.d("getNotificationReq", Constants.imageUrl)
//                Toast.makeText(requireContext(), "Data not found", Toast.LENGTH_SHORT).show()
//            }
//        } ?: run {
//            // Handle null response case
//            Toast.makeText(requireContext(), "Response is null", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun setupIndicators(count: Int) {
        dots = arrayOfNulls(count)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = 8
            marginEnd = 8
        }

        for (i in 0 until count) {
            dots[i] = ImageView(requireContext()).apply {
                setImageResource(R.drawable.indicator_inactive)
                layoutParams = params
            }
            indicatorLayout.addView(dots[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        for (i in dots.indices) {
            dots[i]?.setImageResource(
                if (i == index) R.drawable.indicator_active else R.drawable.indicator_inactive
            )
        }
    }


    private fun btnListener() {

        if (mStash!!.getStringValue(Constants.MerchantId, "") != null) {
            val merchantList = mStash!!.getStringValue(Constants.MerchantList, "")

            if (merchantList?.contains("F0118") == true) {
//                val fastTagAPIStatus =
//                mStash!!.getStringValue(Constants.Fastag_API_Status, "") ?: "Inactive"
//                if (fastTagAPIStatus.equals("Active", ignoreCase = true)) {
                binding.llFastTag.setOnClickListener {
                    callFragment(RechargeFragment(), "FastTag")
                }
//                } else {
//                    binding.llFastTag.setOnClickListener {
//                        toast("This service is not available for you")
//                    }
//                }
            } else {
                binding.llFastTag.setOnClickListener {
                    toast("This service is not available for you")
                }
            }

            if (merchantList?.contains("F0117") == true) {
//                val rechargeApiStatus =
//                    mStash!!.getStringValue(Constants.RechargeAPI_Status, "") ?: "Inactive"
//                val rechargeApi2Status =
//                    mStash!!.getStringValue(Constants.RechargeAPI_2_Status, "") ?: "Inactive"
//                if (rechargeApiStatus.equals(
//                        "Active",
//                        ignoreCase = true
//                    ) || rechargeApi2Status.equals("Active", ignoreCase = true)
//                ) {

                binding.llRecharge.setOnClickListener {
                    callFragment(RechargeFragment(), "mobile")
                }

                binding.llDTH.setOnClickListener {
                    callFragment(RechargeFragment(), "dth")
                }
//                } else {
//                    binding.llRecharge.setOnClickListener {
//                        toast("This service is not available for you")
//                    }
//                    binding.llDTH.setOnClickListener {
//                        toast("This service is not available for you")
//                    }
//                }
            }
            else {
                binding.llRecharge.setOnClickListener {
                   // toast("This service is not available for you")
                    if (isLocationEnabled(requireContext())) {
                        callFragment(RechargeFragment(), "mobile")
                    } else {
                        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, // accuracy mode
                            1000L                           // interval in ms
                        ).build()

                        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

                        val client = LocationServices.getSettingsClient(requireActivity())
                        val task = client.checkLocationSettings(builder.build())

                        task.addOnSuccessListener {
                            // Location is enabled → proceed
                        }.addOnFailureListener { exception ->
                            if (exception is ResolvableApiException) {
                                // Show dialog to enable location
                                exception.startResolutionForResult(requireActivity(), 1001)
                            }
                        }
                    }
                }

                binding.llDTH.setOnClickListener {
                    //toast("This service is not available for you")
                    callFragment(RechargeFragment(), "dth")
                }

            }

            if (merchantList?.contains("F0116") == true) {
//                val rechargeApiStatus =
//                    mStash!!.getStringValue(Constants.RechargeAPI_Status, "") ?: "Inactive"
//                val rechargeApi2Status =
//                    mStash!!.getStringValue(Constants.RechargeAPI_2_Status, "") ?: "Inactive"
//                if (rechargeApiStatus.equals(
//                        "Active",
//                        ignoreCase = true
//                    ) || rechargeApi2Status.equals("Active", ignoreCase = true)
//                ) {

                pressedAllBillBtn()

//                } else {
//                    errorHandleWithMerchantId()
//                }
            } else {
                errorHandleWithMerchantId()
            }
            if (merchantList?.contains("F0112") == true) {
//                val payoutApiStatus =
//                    mStash!!.getStringValue(Constants.Payout_API_Status, "") ?: "Inactive"
//                val payoutApi2Status =
//                    mStash!!.getStringValue(Constants.Payout_API_2_Status, "") ?: "Inactive"
//
//                if (payoutApiStatus.equals(
//                        "Active",
//                        ignoreCase = true
//                    ) || payoutApi2Status.equals("Active", ignoreCase = true)
//                ) {
                binding.scanAndPay.setOnClickListener {
                    startActivity(Intent(context, ScannerFragment::class.java))
                }

                binding.payoutLayout.setOnClickListener {
                    startActivity(Intent(requireContext(), Payout::class.java))
                }
//                } else {
//                    binding.scanAndPay.setOnClickListener {
//                        toast("This service is not available for you")
//                    }
//                }
            } else {
                binding.scanAndPay.setOnClickListener {
                    toast("This service is not available for you")
                }
                binding.payoutLayout.setOnClickListener {
                    toast("This service is not available for you")
                }
            }
           /* if (merchantList!!.contains("F0133")){*/
               // Log.d("merchantlist",merchantList)
                binding.flightAndBusLayout.setOnClickListener {
                    startActivity(Intent(requireContext(), BookingTravel::class.java))
                }
           /* }else {
                binding.flightAndBusLayout.setOnClickListener {
                    toast("This service is not available for you")
                }
            }*/
            // Payout means that money transfer
            if (merchantList!!.contains("F0115")) {
//                val moneyTransferApiStatus =
//                    mStash!!.getStringValue(Constants.MoneyTransferAPI_Status, "") ?: "Inactive"
//                val moneyTransferApi2Status =
//                    mStash!!.getStringValue(Constants.MoneyTransferAPI_2_Status, "") ?: "Inactive"
//                if (moneyTransferApiStatus.equals(
//                        "Active",
//                        ignoreCase = true
//                    ) || moneyTransferApi2Status.equals("Active", ignoreCase = true)
//                ) {
                binding.llDMT.setOnClickListener {

//                        startActivity(Intent(context, DMTMobileActivity::class.java))
                    startActivity(Intent(context, DMTMobileActivity::class.java))
                }
//                } else {
//                    binding.llDMT.setOnClickListener {
//                        toast("This service is not available for you")
//                    }
//                }

            } else {
                binding.llDMT.setOnClickListener {
                    toast("This service is not available for you")
                }
            }
            // Payout means that money receiving

            if (merchantList.contains("F0111")) {
//                val payInApiStatus =
//                    mStash!!.getStringValue(Constants.Payin_API_Status, "") ?: "Inactive"
//                val payInApi2Status =
//                    mStash!!.getStringValue(Constants.Payin_API_2_Status, "") ?: "Inactive"
//
//                if (payInApiStatus.equals(
//                        "Active",
//                        ignoreCase = true
//                    ) || payInApi2Status.equals("Active", ignoreCase = true)
//                ) {
//                    binding.myAccountLayout.setOnClickListener {
//                        startActivity(Intent(requireContext(), com.bos.payment.app.ui.view.Dashboard.GenerateQRCodeActivity::class.java))
//                    }
//                } else {
//                    binding.myAccountLayout.setOnClickListener {
//                        toast("This service is not available for you")
//                    }
//                }
            } else {
//                binding.myAccountLayout.setOnClickListener {
//                    toast("This service is not available for you")
//                }
            }
            if (merchantList.contains("F0125")) {
//                val creditCardAPIStatus = mStash!!.getStringValue(Constants.CreditCardAPI_Status, "")?: "Inactive"

//                if (mStash!!.getStringValue(Constants.CreditCardAPI_Status, "").equals("Active")) {
                binding.creditCardPaymentLayout.setOnClickListener {
                    callFragment(
                        CreditCardDetailsFragment(),
                        "CreditCard"
                    )
                }
//                } else {
//                    binding.creditCardPaymentLayout.setOnClickListener {
//                        toast("This service is not available for you")
//                    }
//                }
            } else {
                binding.creditCardPaymentLayout.setOnClickListener {
                    toast("This service is not available for you")
                }
            }
        } else {
            Toast.makeText(requireContext(), "Merchant Id is bull", Toast.LENGTH_SHORT).show()
        }

        binding.bankTransfer.setOnClickListener {
            startActivity(Intent(requireContext(), RechargeHistory::class.java))
        }
//        binding.myAccountLayout.setOnClickListener {
//            startActivity(Intent(requireContext(), com.bos.payment.app.ui.view.Dashboard.GenerateQRCodeActivity::class.java))
//        }

//        binding.scanAndPay.setOnClickListener {
//            startActivity(Intent(context, ScannerFragment::class.java))
//        }
        binding.adharPayLayout.setOnClickListener {
            toast("In process")
        }

//        binding.llWalletPay.setOnClickListener {
//            toast("In Process")
////            startActivity(
////                Intent(
////                    context,
////                    WalletPayActivity::class.java
////                )
////            )
//        }
//        binding.llLoadAmount.setOnClickListener {
//            toast("In Process")
////            startActivity(
////                Intent(
////                    context,
////                    LoadWalletActivity::class.java
////                )
////            )
//        }
//        binding.llAddMoney.setOnClickListener {
//            startActivity(
//                Intent(
//                    context,
//                    AddAmountActivity::class.java
//                )
//            )
//        }
//        binding.rlMyWallet.setOnClickListener {
//            startActivity(
//                Intent(
//                    context,
//                    MyWalletActivity::class.java
//                )
//            )
//        }
        binding.selfAccount.setOnClickListener {
            toast("In Process")//            callFragment(RechargeFragment(), "postpaid")
        }
//        binding.myAccountLayout.setOnClickListener { callFragment(RechargeFragment(), "postpaid") }
        binding.aepsLayout.setOnClickListener {
            toast("In Process")
        }

//        binding.financeInsuranceLayout.setOnClickListener {
//            callFragment(
//                RechargeFragment(),
//                "Insurance"
//            )
//        }
//        binding.muncipalTaxLayout.setOnClickListener {
//            callFragment(
//                RechargeFragment(),
//                "Municipality"
//            )
//        }
        binding.panCardLayout.setOnClickListener {
            toast("In Process")//
            //        callFragment(
//                RechargeFragment(),
//                "MunicipalTax"
//            )
        }


        binding.llSeeAll.setOnClickListener {
            binding.llviewAll.visibility = View.GONE
            binding.llviewAll2.visibility = View.VISIBLE
            binding.llviewAll3.visibility = View.VISIBLE
            binding.llviewAll4.visibility = View.VISIBLE
            val lastChild = binding.nestedScroll.getChildAt(binding.nestedScroll.childCount - 1)
            binding.nestedScroll.post { binding.nestedScroll.smoothScrollTo(0, lastChild.bottom) }
        }

    }

    private fun pressedAllBillBtn() {

        binding.llPostPaid.setOnClickListener {
            callFragment(RechargeFragment(), "postpaid")
        }
        binding.llBroadband.setOnClickListener {
            callFragment(
                RechargeFragment(),
                "Broadband"
            )
        }
        binding.llElectricity.setOnClickListener {
            callFragment(
                RechargeFragment(),
                "Electricity"
            )
        }
        binding.llElectricity2.setOnClickListener {
            callFragment(
                RechargeFragment(),
                "Electricity"
            )
        }
        binding.llLandline.setOnClickListener {
            callFragment(
                RechargeFragment(),
                "Landline"
            )
        }
        binding.llWaterBill.setOnClickListener {
            callFragment(
                RechargeFragment(),
                "Water"
            )
        }
        binding.llWaterBill2.setOnClickListener {
            callFragment(
                RechargeFragment(),
                "Water"
            )
        }
        binding.llGas.setOnClickListener { callFragment(RechargeFragment(), "Gas") }
        binding.llGas2.setOnClickListener { callFragment(RechargeFragment(), "Gas") }
        binding.llEMI.setOnClickListener { callFragment(RechargeFragment(), "EMI") }
        binding.llCable.setOnClickListener { callFragment(RechargeFragment(), "Cable") }
        binding.llInsurance.setOnClickListener {
            callFragment(
                RechargeFragment(),
                "Insurance"
            )
        }
        binding.muncipalTaxLayout.setOnClickListener {
            callFragment(
                RechargeFragment(),
                "Municipality"
            )
        }
        binding.financeInsuranceLayout.setOnClickListener {
            callFragment(
                RechargeFragment(),
                "Insurance"
            )
        }

    }

    private fun errorHandleWithMerchantId() {
        binding.llPostPaid.setOnClickListener {
            toast("This service is not available for you")
        }
        binding.llBroadband.setOnClickListener {
            toast("This service is not available for you")
        }
        binding.llElectricity.setOnClickListener {
            toast("This service is not available for you")
        }
        binding.llElectricity2.setOnClickListener {
            toast("This service is not available for you")
        }
        binding.llLandline.setOnClickListener {
            toast("This service is not available for you")
        }
        binding.llWaterBill.setOnClickListener {
            toast("This service is not available for you")
        }
        binding.llWaterBill2.setOnClickListener {
            toast("This service is not available for you")
        }
        binding.llGas.setOnClickListener {
            toast("This service is not available for you")
        }
        binding.llGas2.setOnClickListener {
            toast("This service is not available for you")
        }
        binding.llEMI.setOnClickListener {
            toast("This service is not available for you")
        }
        binding.llCable.setOnClickListener {
            toast("This service is not available for you")
        }
        binding.llInsurance.setOnClickListener {
            toast("This service is not available for you")
        }
        binding.muncipalTaxLayout.setOnClickListener {
            toast("This service is not available for you")
        }
        binding.financeInsuranceLayout.setOnClickListener {
            toast("This service is not available for you")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        mStash = MStash.getInstance(requireContext())!!
        pd = PD(requireActivity())
        context = requireContext()
        requestOption = RequestOptions()

//        binding.tvWalletAmount.text = "₹" + mStash!!.getStringValue(Constants.WalletBalance, "")
        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]

        viewModel = ViewModelProvider(requireActivity(), MoneyTransferViewModelFactory(MoneyTransferRepository(RetrofitClient.apiAllInterface)))[MoneyTransferViewModel::class.java]

        MobileRechargeViewModel = ViewModelProvider(this, MobileRechargeViewModelFactory (
            MobileRechargeRepository(RetrofitClient.apiRechargeInterface)
        )
        )[GetAllMobileRechargeViewModel::class.java]

//        val companyCode = mStash!!.getStringValue(Constants.CompanyCode, "")
//        Log.d("companyCode", companyCode.toString())
//        changeAppIcon(companyCode!!)
//        val registrationId = mStash?.getStringValue(Constants.RegistrationId, "") ?: ""
//        getWalletBalance(registrationId)
//        getAllMerchantList(mStash!!.getStringValue(Constants.MerchantId, "").toString())

    }

    private fun callFragment(fragment: Fragment, rechargeType: String) {
        val bundle = Bundle()
        bundle.putString("RechargeType", rechargeType)
        fragment.arguments = bundle
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

//    private fun setViewPager() {
//        val images = listOf(
//            R.drawable.ic_dashboard,
//            R.drawable.ic_dashboard,
//            R.drawable.ic_dashboard
//        )
//        autoScrollViewPagerAdapter = AutoScrollViewPagerAdapter(requireContext(), images)
//        binding.viewPager.adapter = autoScrollViewPagerAdapter
//
//        autoScrollViewPagerAdapter.setupDotsLayout(binding.dotsLayout)
//    }

    @RequiresApi(Build.VERSION_CODES.M)
//    override fun onResume() {
//        super.onResume()
//        autoScrollViewPagerAdapter.startAutoScroll(binding!!.viewPager)
//        login(
//            MySharedPreference.getUserModelData(context).LoginID,
//            MySharedPreference.getUserModelData(context).Password,
//            StrCompanycode = mStash!!.getStringValue(Constants.MerchantId, "")
//        )
//    }

//    override fun onPause() {
//        super.onPause()
//        autoScrollViewPagerAdapter.stopAutoScroll()
//    }

//    override fun onDestroyView() {
//        super.onDestroyView()
////        binding = null
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        startActivity(Intent(requireContext(), IdentifyClient::class.java))
//    }

    @SuppressLint("SetTextI18n")
//    private fun login(StrUserName: String, StrPassword: String, StrCompanycode: String?) {
//        var progressDialog = ProgressDialog(context)
//        progressDialog.setMessage("Please wait!")
//        progressDialog.show()
//        ViewModelProvider(this).get(AttendanceViewModel2::class.java)
//            .login(StrUserName, StrPassword, StrCompanycode)
//            .observe(this) {
//                progressDialog.dismiss()
//                if (it!!.Status == ConstantClass.Success) {
//                    binding.tvWalletAmount.text = "₹" + MySharedPreference.getUserModelData(
//                        requireContext()
//                    ).WalletBalance
//                }
//            }
//    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissions() {
        Dexter.withActivity(requireActivity()).withPermissions(
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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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


}