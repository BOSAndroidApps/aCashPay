package com.bos.payment.appName.notification

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.data.model.servicesbasednotification.DataItem
import com.bos.payment.appName.data.model.servicesbasednotification.NotificationReq
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.databinding.ActivityNotificationPageBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.adapter.NotificationAdapter
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.google.gson.Gson

class NotificationPage : AppCompatActivity() {
    lateinit var binding : ActivityNotificationPageBinding
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    private var mStash: MStash? = null
    var notificationList: List<DataItem?>? = arrayListOf()
    lateinit var adapter : NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mStash = MStash.getInstance(this@NotificationPage)
        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]

        hitApiForServicesNotification()

        binding.back.setOnClickListener {
            finish()
        }

    }

    fun hitApiForServicesNotification() {
        var userCode = mStash!!.getStringValue(Constants.RegistrationId, "").toString()
        var merchantId = mStash!!.getStringValue(Constants.MerchantId, "").toString()

        runIfConnected {
            val request = NotificationReq(
                companyCode = userCode,
                merchantCode = merchantId,
            )

            Log.d("notificationreq", Gson().toJson(request))

            getAllApiServiceViewModel.GetNotificationReq(request).observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    Log.d("notificationresp", Gson().toJson(response))
                                    Constants.dialog.dismiss()
                                    if (response.isSuccess!!) {
                                        notificationList = response!!.data!!
                                        adapter = NotificationAdapter(this@NotificationPage, notificationList!!)
                                        binding.notilist.adapter = adapter
                                        binding.notfoundimage.visibility= View.GONE
                                        binding.notilist.visibility=View.VISIBLE

                                    }
                                    else {
                                        binding.notfoundimage.visibility= View.VISIBLE
                                        binding.notilist.visibility=View.GONE
                                        Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            Constants.dialog.dismiss()
                            binding.notfoundimage.visibility= View.VISIBLE
                            binding.notilist.visibility=View.GONE
                        }

                        ApiStatus.LOADING -> {
                          Constants.OpenPopUpForVeryfyOTP(this)
                        }

                    }
                }
            }
        }
    }

}