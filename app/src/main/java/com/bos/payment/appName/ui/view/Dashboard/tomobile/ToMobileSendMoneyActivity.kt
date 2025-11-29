package com.bos.payment.appName.ui.view.Dashboard.tomobile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.data.model.justpaymodel.RetailerContactListRequestModel
import com.bos.payment.appName.data.model.justpaymodel.RetailerDataItem
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.databinding.ActivityToMobileSendMoneyBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.adapter.ContactListAdapter
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.PD
import com.google.gson.Gson

class ToMobileSendMoneyActivity : AppCompatActivity() {
    lateinit var binding : ActivityToMobileSendMoneyBinding
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    private var mStash: MStash? = null
    private var RetailerContactList : List<RetailerDataItem?>? = arrayListOf()
    private var FilterRetailerContactList : MutableList<RetailerDataItem?>? = arrayListOf()
    private lateinit var contactListAdapter : ContactListAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityToMobileSendMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mStash = MStash.getInstance(this)
        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]

        hitApiForGettingRetailersList()
        setclicklistner()

    }



    fun setclicklistner(){
        binding.back.setOnClickListener {
            finish()
        }


        binding.serachmobileorname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })




    }



    private fun filterList(query: String) {
        val searchText = query.trim().lowercase()
        val result = if (searchText.isEmpty()) RetailerContactList else RetailerContactList!!.filter {
            it!!.agentType!!.lowercase().contains(searchText) ||
                    it.mobileNo!!.lowercase().contains(searchText) ||
                    it.name!!.lowercase().contains(searchText)||
                    it.userID!!.lowercase().contains(searchText)
        }
        contactListAdapter.updateList(result)
    }



    fun hitApiForGettingRetailersList(){

        var adminCode = mStash!!.getStringValue(Constants.AdminCode, "")
        var retailerloginId = mStash!!.getStringValue(Constants.RegistrationId, "")

        val getRetailerContactList = RetailerContactListRequestModel(
            adminCode = adminCode
        )

        Log.d("retailerContactListReq", Gson().toJson(getRetailerContactList))

        getAllApiServiceViewModel.getRetailerContactList(getRetailerContactList).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                            Constants.dialog.dismiss()
                        }
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("retailerContactListResp", Gson().toJson(response))
                                if(response.isSuccess!!){
                                    var  AllRetailerContactList= response.data

                                     RetailerContactList =  AllRetailerContactList?.filter { item ->
                                         !(item?.userID?.contains(retailerloginId!!, ignoreCase = true) == true ||
                                                 item?.userID?.contains(adminCode!!, ignoreCase = true) == true)
                                     } ?: emptyList()

                                    Log.d("filterdata", Gson().toJson(response))
                                    contactListAdapter = ContactListAdapter(this@ToMobileSendMoneyActivity,RetailerContactList)
                                    binding.contactList.adapter = contactListAdapter
                                    contactListAdapter.notifyDataSetChanged()

                                }
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