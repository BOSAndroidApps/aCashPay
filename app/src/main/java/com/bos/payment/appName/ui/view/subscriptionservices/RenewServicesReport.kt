package com.bos.payment.appName.ui.view.subscriptionservices

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.data.model.transactionreportsmodel.TransactionReportsReq
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.databinding.ContentRenweServicesReportBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.adapter.RenewServicesReportAdapter
import com.bos.payment.appName.ui.adapter.TransactionReportAdapter
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.google.gson.Gson
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


class RenewServicesReport : AppCompatActivity() {
    private lateinit var binding: ContentRenweServicesReportBinding
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    private var mStash: MStash? = null
    private val myCalender = Calendar.getInstance()
    private val myCalender1 = Calendar.getInstance()
    var FromDate: String= ""
    var ToDate: String =""
    lateinit var adapter: RenewServicesReportAdapter
    var RenewServicesReport:String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ContentRenweServicesReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllApiServiceViewModel = ViewModelProvider(this,
            GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]

        mStash = MStash.getInstance(this)

        setClickListner()

    }

    private val createExcelLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) { uri ->

        uri ?: return@registerForActivityResult

        writeCsvToUri(uri,RenewServicesReport)
    }

    private fun writeCsvToUri(uri: Uri, reportsList: String) {
        try {
            val jsonArray = JSONArray(reportsList)

            if (jsonArray.length() == 0) {
                Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show()
                return
            }

            // 🔹 Extract headers from first object
            val firstObj = jsonArray.getJSONObject(0)
            val headers = firstObj.keys().asSequence().toList()

            contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->

                // ✅ WRITE HEADER
                writer.append(headers.joinToString(","))
                writer.newLine()

                // ✅ WRITE DATA ROWS
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)

                    val row = headers.joinToString(",") { key ->
                        "\"${obj.optString(key).replace("\"", "\"\"")}\""
                    }

                    writer.append(row)
                    writer.newLine()
                }
            }

            Toast.makeText(this, "CSV file saved successfully", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setClickListner(){

        binding.excellayout.setOnClickListener {
            createExcelLauncher.launch(Constants.generateReportFileName("Subscription_Charge_Report_"))
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.fromDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, monthOfYear, dayOfMonth ->
                    val actualMonth = monthOfYear + 1 // Fix zero-based month
                    myCalender.set(year, monthOfYear, dayOfMonth)
                    binding.fromDate.text = "$dayOfMonth/$actualMonth/$year"

                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
                    calendar.set(Calendar.MILLISECOND, 0)

                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    sdf.timeZone = TimeZone.getTimeZone("UTC")

                    FromDate = sdf.format(calendar.time)
                    Log.d("FromDate", FromDate)

                    // If both dates are selected, validate and call API
                    if ( ToDate.isNotEmpty()
                        && FromDate.isNotEmpty()
                    ) {
                        val from = sdf.parse(FromDate)
                        val to = sdf.parse(ToDate)

                        when {
                            to.before(from) -> {
                                Toast.makeText(this, "To date cannot be before From date", Toast.LENGTH_SHORT).show()
                            }
                            to.equals(from) -> {
                                Toast.makeText(this, "From and To dates cannot be the same", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Constants.OpenPopUpForVeryfyOTP(this)
                                hitApiForGettingTransactionReports()
                            }
                        }
                    }
                },
                myCalender.get(Calendar.YEAR),
                myCalender.get(Calendar.MONTH),
                myCalender.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.toDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, monthOfYear, dayOfMonth ->
                    val actualMonth = monthOfYear + 1 // Fix zero-based month
                    myCalender1.set(year, monthOfYear, dayOfMonth)
                    binding.toDate.text = "$dayOfMonth/$actualMonth/$year"

                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
                    calendar.set(Calendar.MILLISECOND, 0)

                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    sdf.timeZone = TimeZone.getTimeZone("UTC")

                    ToDate = sdf.format(calendar.time)
                    Log.d("ToDate", ToDate)

                    // If both dates are selected, validate and call API
                    if ( ToDate.isNotEmpty() && FromDate.isNotEmpty()) {
                        val from = sdf.parse(FromDate)
                        val to = sdf.parse(ToDate)

                        when {
                            to.before(from) -> {
                                Toast.makeText(this, "To date cannot be before From date", Toast.LENGTH_SHORT).show()
                            }
                            to.equals(from) -> {
                                Toast.makeText(this, "From and To dates cannot be the same", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Constants.OpenPopUpForVeryfyOTP(this)
                                hitApiForGettingTransactionReports()
                            }
                        }
                    }
                },
                myCalender1.get(Calendar.YEAR),
                myCalender1.get(Calendar.MONTH),
                myCalender1.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

    }

    private fun hitApiForGettingTransactionReports(){
        runIfConnected {
            val reportReq = TransactionReportsReq(
                parmFlag = "Subscription Report" ,
                paramMerchantCode = mStash!!.getStringValue(Constants.MerchantId,""),
                parmToDate = ToDate,
                parmUser = mStash!!.getStringValue(Constants.RegistrationId,""),
                parmFla2 = "All",
                parmFromDate = FromDate
            )
            Log.d("TransactionReportsReq", Gson().toJson(reportReq))

            getAllApiServiceViewModel.sendTransactionReportsReq(reportReq)
                .observe(this) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                it.data?.let { users ->
                                    users.body()?.let { response ->
                                        Log.d("TransactionReportsResp", Gson().toJson(response))
                                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                                            Constants.dialog.dismiss()
                                        }
                                        if(response.isSuccess!!){
                                            response.data!!.forEach { unit->
                                                if(response.data.size>0){
                                                    binding.notfoundlayout.visibility= View.GONE
                                                    binding.reportslayout.visibility = View.VISIBLE
                                                    binding.excellayout.visibility= View.VISIBLE
                                                    var datalist = response.data
                                                    RenewServicesReport= Gson().toJson(datalist)
                                                    adapter= RenewServicesReportAdapter(this,datalist)
                                                    binding.reportslayout.adapter=adapter
                                                    adapter.notifyDataSetChanged()
                                                }
                                                else{
                                                    binding.notfoundlayout.visibility= View.VISIBLE
                                                    binding.reportslayout.visibility = View.GONE
                                                    binding.excellayout.visibility= View.GONE
                                                }

                                            }
                                        }
                                        else{
                                            binding.notfoundlayout.visibility= View.VISIBLE
                                            binding.reportslayout.visibility = View.GONE
                                            binding.excellayout.visibility= View.GONE
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

                            }

                        }
                    }
                }
        }
    }


}