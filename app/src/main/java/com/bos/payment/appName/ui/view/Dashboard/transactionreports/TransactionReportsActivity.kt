package com.bos.payment.appName.ui.view.Dashboard.transactionreports

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.transactionreportsmodel.CheckRaiseTicketExistReq
import com.bos.payment.appName.data.model.transactionreportsmodel.ReportListReq
import com.bos.payment.appName.data.model.transactionreportsmodel.TransactionReportsReq
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.databinding.ActivityTransactionReportsBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.adapter.TransactionReportAdapter
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.google.gson.Gson
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


class TransactionReportsActivity : AppCompatActivity() {
    lateinit var binding: ActivityTransactionReportsBinding
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    private var mStash: MStash? = null
    var displayReportList : MutableList<String?> = arrayListOf()
    var rechargeModeList : MutableList<String?> = arrayListOf()
    var payoutModeList : MutableList<String?> = arrayListOf()
    var depositModeList : MutableList<String?> = arrayListOf()
    var transferModeList : MutableList<String?> = arrayListOf()
    var dmtModeList : MutableList<String?> = arrayListOf()
    var displaytxtReportList : MutableList<String?> = arrayListOf()
    private val myCalender = Calendar.getInstance()
    private val myCalender1 = Calendar.getInstance()
    var FromDate: String= ""
    var ToDate: String =""
    lateinit var adapter : TransactionReportAdapter
    lateinit var dialog: Dialog
    var transactionReports:String =""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]

        mStash = MStash.getInstance(this)

        hitapiforReports()
        setClickListner()
        setReportModeInSpinner()

    }

    private val createExcelLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) { uri ->

            uri ?: return@registerForActivityResult

            writeExcelToUri(uri,transactionReports)
        }


    private fun setClickListner(){

       binding.excellayout.setOnClickListener {
           createExcelLauncher.launch("Transaction_Report.xlsx")
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
                    if (binding.reportmode.selectedItem.toString().trim().isNotEmpty()
                        && binding.selectreport.selectedItem.toString().trim().isNotEmpty()
                        && ToDate.isNotEmpty()
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
                    if (binding.reportmode.selectedItem.toString().trim().isNotEmpty()
                        && binding.selectreport.selectedItem.toString().trim().isNotEmpty()
                        && ToDate.isNotEmpty()
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
                myCalender1.get(Calendar.YEAR),
                myCalender1.get(Calendar.MONTH),
                myCalender1.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        binding.reportmode.onItemSelectedListener= object : OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var selectedItem = binding.reportmode.selectedItem.toString().trim()

                if(selectedItem.isNotEmpty() && binding.selectreport.selectedItem.toString().trim().isNotEmpty()&& ToDate.isNotEmpty() && FromDate.isNotEmpty()){
                    hitApiForGettingTransactionReports()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }


        binding.selectreport.onItemSelectedListener=object :OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var selectedItem = binding.selectreport.selectedItem.toString().trim()

                if(selectedItem.contains("Recharge" ,ignoreCase = true)){
                    setDataForModeBasedOnSelectedReport(rechargeModeList)
                }
                if(selectedItem.contains("dmt",ignoreCase = true)){
                    setDataForModeBasedOnSelectedReport(dmtModeList)
                }

                if(selectedItem.contains("payout",ignoreCase = true)){
                    setDataForModeBasedOnSelectedReport(payoutModeList)
                }

                if(selectedItem.contains("deposit",ignoreCase = true)){
                    setDataForModeBasedOnSelectedReport(depositModeList)
                }

                if(selectedItem.trim().contains("transfer report",ignoreCase = true)){
                    setDataForModeBasedOnSelectedReport(transferModeList)
                }

                if(selectedItem.isNotEmpty() && binding.reportmode.selectedItem.toString().trim().isNotEmpty()&& ToDate.isNotEmpty() && FromDate.isNotEmpty()){
                    hitApiForGettingTransactionReports()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

    }


    private fun hitapiforReports() {
        runIfConnected {
            val reportReq = ReportListReq(
                pParmFlag ="Reports" ,
                pParmFlag1 = mStash!!.getStringValue(Constants.RegistrationId, ""),
                pParmFlag2 = ""
            )

            getAllApiServiceViewModel.sendForReportListReq(reportReq)
                .observe(this) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                it.data?.let { users ->
                                    users.body()?.let { response ->
                                        if(response.isSuccess!!){
                                            if(Constants.dialog!=null && Constants.dialog.isShowing){
                                                Constants.dialog.dismiss()
                                            }
                                            response.data!!.forEach { unit->
                                                displayReportList.add(unit!!.displayValue)
                                                displaytxtReportList.add(unit!!.displayText)
                                                setReportInSpinner()
                                            }
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


    private fun writeExcelToUri(uri: Uri, reportsList: String) {
        try {
            val jsonArray = JSONArray(reportsList)
            if (jsonArray.length() == 0) {
                Toast.makeText(this, "No data to write", Toast.LENGTH_SHORT).show()
                return
            }

            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Transactions")

            // Extract headers
            val keys = jsonArray.getJSONObject(0).keys().asSequence().toList()

            // Header row
            val headerRow = sheet.createRow(0)
            keys.forEachIndexed { index, key ->
                headerRow.createCell(index).setCellValue(key)
            }

            // Data rows
            for (i in 0 until jsonArray.length()) {
                val row = sheet.createRow(i + 1)
                val obj = jsonArray.getJSONObject(i)
                keys.forEachIndexed { colIndex, key ->
                    row.createCell(colIndex).setCellValue(obj.optString(key, ""))
                }
            }

            // Auto-size columns
            keys.indices.forEach { sheet.autoSizeColumn(it) }

            // Write to file
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                workbook.write(outputStream)
                outputStream.flush()
            }

            workbook.close()
            Toast.makeText(this, "File saved successfully", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }




    private fun setReportInSpinner(){
        var adapter = ArrayAdapter(this@TransactionReportsActivity, android.R.layout.simple_spinner_dropdown_item, displayReportList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.selectreport.adapter = adapter
    }


    private fun setReportModeInSpinner(){
        rechargeModeList.clear()
        payoutModeList.clear()
        dmtModeList.clear()
        depositModeList.clear()
        transferModeList.clear()

        dmtModeList.add("")

        rechargeModeList.add("Recharge History")
        rechargeModeList.add("Commission History")

        payoutModeList.add("All")
        payoutModeList.add("Approved")
        payoutModeList.add("Pending")
        payoutModeList.add("Rejected")
        payoutModeList.add("Commission History")


        depositModeList.add("All")
        depositModeList.add("Make Payment Deposit")
        depositModeList.add("Wallet Transfer")
        depositModeList.add("Refunds Deposit")
        depositModeList.add("To Mobile Deposit")
        depositModeList.add("Promo cashback")


        transferModeList.add("To Mobile Transfer")
        transferModeList.add("To Self Transfer")

    }


    private fun setDataForModeBasedOnSelectedReport(report :MutableList<String?> ){
        var adapter = ArrayAdapter(this@TransactionReportsActivity, android.R.layout.simple_spinner_dropdown_item, report)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.reportmode.adapter = adapter
    }


    private fun hitApiForGettingTransactionReports(){
        runIfConnected {
            val reportReq = TransactionReportsReq(
                parmFlag = binding.selectreport.selectedItem.toString().trim() ,
                paramMerchantCode = mStash!!.getStringValue(Constants.MerchantId,""),
                parmToDate = ToDate,
                parmUser = mStash!!.getStringValue(Constants.RegistrationId,""),
                parmFla2 = binding.reportmode.selectedItem.toString().trim(),
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
                                                    binding.excellayout.visibility= View.VISIBLE
                                                    binding.reportslayout.visibility = View.VISIBLE
                                                    var datalist = response.data
                                                    transactionReports = Gson().toJson(datalist)
                                                    adapter= TransactionReportAdapter(this,datalist)
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


    fun hitApiForCheckRaiseTicket(transactionID:String){
        runIfConnected {
            val reportReq = CheckRaiseTicketExistReq(
                transactionID = transactionID ,
            )
            Log.d("TransactionReportsReq", Gson().toJson(reportReq))

            getAllApiServiceViewModel.sendTransactionRaiseTicketExitsReq(reportReq)
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
                                          if(response.data!!.isValid!!){
                                            startActivity(Intent(this,RaiseTicketActivity::class.java))
                                          }else{
                                              popupforshowingraiseticketstatus(transactionID)
                                          }
                                        }
                                        else{
                                          Toast.makeText(this,response.returnMessage,Toast.LENGTH_SHORT).show()
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


    fun popupforshowingraiseticketstatus(transactionId : String){
        dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.ticketraisedalert)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        dialog.setCanceledOnTouchOutside(false)

        val done = dialog.findViewById<Button>(R.id.ok)
        val txt = dialog.findViewById<TextView>(R.id.dialog_message)
        val titletxt = dialog.findViewById<TextView>(R.id.title)

        txt.text="A ticket has already been raised by the retailer. Transaction No: $transactionId"

        done.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }


}