package com.bos.payment.appName.ui.view.makepayment

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Visibility
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.makepaymentnew.RaiseMakePaymentReq
import com.bos.payment.appName.data.model.makepaymentnew.DataItem
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.databinding.ActivityMakePayment2Binding
import com.bos.payment.appName.databinding.ActivityMakePaymentBinding
import com.bos.payment.appName.databinding.ActivityMakepaymentReportsBinding
import com.bos.payment.appName.databinding.ActivityTransactionReportsBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.adapter.MakePaymentReportsAdapter
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class MakepaymentReports : AppCompatActivity() {

    lateinit var binding : ActivityMakepaymentReportsBinding
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    private var mStash: MStash? = null
    private var getMakePaymentReportsList: MutableList<DataItem?>? = arrayListOf()
    var reportModeList: MutableList<String?> = arrayListOf()
    private val myCalender1 = Calendar.getInstance()
    var ToDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMakepaymentReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]

        mStash = MStash.getInstance(this)

        HitApiForTransferAmountToAdminAccount()
        setReportModeInSpinner()
        setOnClickListner()

    }

    private fun setReportModeInSpinner() {
        reportModeList.clear()
        reportModeList.add("All")
        reportModeList.add("Approved")
        reportModeList.add("Pending")
        reportModeList.add("Rejected")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, reportModeList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.reportmode.adapter = adapter
    }


    private fun setOnClickListner() {

        binding.back.setOnClickListener {
            finish()
        }


        binding.reseticon.setOnClickListener {
            binding.toDate.text = Constants.DateSelectionHint
            if(!binding.toDate.text.toString().equals(Constants.DateSelectionHint)){
                binding.reseticon.visibility = View.VISIBLE
            }else {
                binding.reseticon.visibility = View.GONE
            }

            HitApiForTransferAmountToAdminAccount()
            setReportModeInSpinner()
        }


        binding.toDate.setOnClickListener {
            DatePickerDialog(
                this, { _, year, monthOfYear, dayOfMonth ->
                    val actualMonth = monthOfYear + 1
                    myCalender1.set(year, actualMonth - 1, dayOfMonth)

                    if(!binding.toDate.text.toString().equals(Constants.DateSelectionHint)){
                        binding.reseticon.visibility = View.VISIBLE
                    }else {
                        binding.reseticon.visibility = View.GONE
                    }

                    binding.toDate.text = "$dayOfMonth/$actualMonth/$year"

                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
                    calendar.set(Calendar.MILLISECOND, 860)

                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    sdf.timeZone = TimeZone.getTimeZone("UTC")

                    ToDate = sdf.format(calendar.time)

                    val selectedStatus = binding.reportmode.selectedItem.toString().trim()

                    if (selectedStatus.isNotEmpty() && !selectedStatus.equals("All", ignoreCase = true) && ToDate.isNotEmpty()) {
                        val filterList = getMakePaymentReportsList!!.filter { item ->
                            item!!.apporvedStatus.equals(selectedStatus, ignoreCase = true) &&
                                    item.paymentDate!!.startsWith(ToDate.substring(0, 10)) // compare only date part
                        }.toMutableList()

                        setAdapter(filterList)
                    }
                    else {
                        val filterList = getMakePaymentReportsList!!.filter { item ->
                            item!!.paymentDate!!.startsWith(ToDate.substring(0, 10))
                        }.toMutableList()

                        setAdapter(filterList)
                    }



                    Log.d("ToDate", ToDate)
                },
                myCalender1.get(Calendar.YEAR),
                myCalender1.get(Calendar.MONTH),
                myCalender1.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.reportmode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedItem = binding.reportmode.selectedItem.toString().trim()

                val filterList = if (selectedItem.isNotEmpty() &&
                    !selectedItem.equals("All", ignoreCase = true) ||
                    ToDate.isNotEmpty()
                ) {
                    getMakePaymentReportsList!!.filter { item ->
                        item!!.apporvedStatus.equals(selectedItem, ignoreCase = true) &&
                                item.paymentDate!!.startsWith(ToDate)
                    }.toMutableList()
                } else {
                    getMakePaymentReportsList!!.filter { item ->
                        item!!.paymentDate!!.startsWith(ToDate)
                    }.toMutableList()
                }

                setAdapter(filterList)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun HitApiForTransferAmountToAdminAccount() {
        val userCode = mStash!!.getStringValue(Constants.RegistrationId, "").toString()
        val adminCode = mStash!!.getStringValue(Constants.AdminCode, "").toString()

        val request = RaiseMakePaymentReq(
            Mode = "GET",
            RID = "",
            RefrenceID = "",
            PaymentMode = "",
            PaymentDate = "",
            DepositBankName = "",
            BranchCode_ChecqueNo = "",
            Remarks = "",
            TransactionID = "",
            DocumentPath = "",
            RecordDateTime = "",
            UpdatedBy = userCode,
            UpdatedOn = "",
            ApprovedBy = "",
            ApprovedDateTime = "",
            ApporvedStatus = "Pending",
            RegistrationId = userCode,
            ApporveRemakrs = "",
            Amount = "",
            CompanyCode = "",
            BeneId = "",
            AccountHolder = "",
            PaymentType = "",
            Flag = "",
            AdminCode = adminCode,
            imagefile1 = null
        )

        Log.d("GetMakePaymentReq", Gson().toJson(request))

        getAllApiServiceViewModel.RaisMakePaymentReq(request).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.body()?.let { response ->
                            if(Constants.dialog!=null && Constants.dialog.isShowing){
                                Constants.dialog.dismiss()
                            }

                            Log.d("GetMakePaymentResp", Gson().toJson(response))

                            if (response.isSuccess == true) {

                                getMakePaymentReportsList = response.data

                                if (!getMakePaymentReportsList.isNullOrEmpty()) {
                                    setAdapter(getMakePaymentReportsList)
                                } else {
                                    binding.notfoundlayout.visibility = View.VISIBLE
                                    binding.makepaymentreports.visibility = View.GONE
                                }

                            } else {
                                binding.notfoundlayout.visibility = View.VISIBLE
                                binding.makepaymentreports.visibility = View.GONE
                                Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    ApiStatus.ERROR ->  if(Constants.dialog!=null && Constants.dialog.isShowing){
                        Constants.dialog.dismiss()
                    }

                    ApiStatus.LOADING ->  Constants.OpenPopUpForVeryfyOTP(this)
                }
            }
        }
    }

    private fun setAdapter(getMakePaymentReportsList: MutableList<DataItem?>?) {
        if (!getMakePaymentReportsList.isNullOrEmpty()) {
            binding.notfoundlayout.visibility = View.GONE
            binding.makepaymentreports.visibility = View.VISIBLE

            val adapter = MakePaymentReportsAdapter(this, getMakePaymentReportsList)
            binding.makepaymentreports.adapter = adapter
            adapter.notifyDataSetChanged()
        } else {
            binding.notfoundlayout.visibility = View.VISIBLE
            binding.makepaymentreports.visibility = View.GONE
        }
    }
}