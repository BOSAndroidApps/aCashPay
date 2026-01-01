package com.bos.payment.appName.ui.view.promocode

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.promocode.GetPromotionListReq
import com.bos.payment.appName.data.model.promocode.PromoDataItem
import com.bos.payment.appName.data.model.servicesbasednotification.DataItem
import com.bos.payment.appName.data.model.servicesbasednotification.NotificationReq
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.databinding.ActivityPromocodeListBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.adapter.NotificationAdapter
import com.bos.payment.appName.ui.adapter.PromocodeListAdapter
import com.bos.payment.appName.ui.view.promocode.PromocodeDetailsPage.Companion.promoDataItem
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class PromocodeListActivity : AppCompatActivity() {
    lateinit var binding: ActivityPromocodeListBinding
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    private var mStash: MStash? = null
    var promodataList: List<PromoDataItem?>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityPromocodeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        binding.back.setOnClickListener {
            finish()
        }

    }


    fun init(){
        mStash = MStash.getInstance(this@PromocodeListActivity)
        getAllApiServiceViewModel = ViewModelProvider(this@PromocodeListActivity, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]
    }


    fun hitApiForPromotionList() {
        var userCode = mStash!!.getStringValue(Constants.RegistrationId, "").toString()
        var adminCode = mStash!!.getStringValue(Constants.AdminCode, "").toString()
        var currentDate = getCurrentDateUtc() // 2025-12-29T07:15:24.611Z

        runIfConnected {
            val request = GetPromotionListReq(
                country = "India",
                applicationType = "Mobile App",
               /* toDate = currentDate,
                fromDate = currentDate,*/
                applicableSubServices = "",
                applicationMode = "B2B",
                adminCode = adminCode,
                applicableOperators = "",
                promoCode = "",
                retailerCode = userCode,
                state = "DELHI",
                applicableServices = "",
                status = ""
            )

            Log.d("promolistreq", Gson().toJson(request))

            getAllApiServiceViewModel.GetPromotionListReq(request).observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    Constants.dialog.dismiss()
                                    if (response.isSuccess!!) {
                                        promodataList = response!!.data!!
                                        Log.d("promolistresp", Gson().toJson(promodataList))
                                        val adapter = PromocodeListAdapter(this,getAllApiServiceViewModel,this,promodataList,
                                            onDetailsClick = { item->
                                                promoDataItem= item
                                                startActivity(Intent(this@PromocodeListActivity,PromocodeDetailsPage::class.java))
                                            },
                                            onApplyClick = {
                                                // Apply promo logic
                                            }

                                        )
                                        binding.showpromocode.adapter = adapter
                                        binding.notfoundimage.visibility= View.GONE
                                        binding.showpromocode.visibility= View.VISIBLE
                                    }
                                    else {
                                        binding.notfoundimage.visibility= View.VISIBLE
                                        binding.showpromocode.visibility= View.GONE
                                        Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            Constants.dialog.dismiss()
                            binding.notfoundimage.visibility= View.VISIBLE
                            binding.showpromocode.visibility= View.GONE
                        }

                        ApiStatus.LOADING -> {
                            Constants.OpenPopUpForVeryfyOTP(this)
                        }

                    }
                }
            }
        }
    }


    fun getCurrentDateUtc(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

    override fun onResume() {
        super.onResume()
        hitApiForPromotionList()
    }
}