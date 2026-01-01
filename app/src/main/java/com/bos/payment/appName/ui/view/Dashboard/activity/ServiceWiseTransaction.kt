package com.bos.payment.appName.ui.view.Dashboard.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bos.payment.appName.data.repository.ServiceChangeRepository
import com.bos.payment.appName.data.viewModelFactory.ServiceChangeViewModelFactory
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.adapter.TransactionReportsAdapter
import com.bos.payment.appName.ui.viewmodel.ServiceChangeViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils
import com.bos.payment.appName.utils.Utils.PD
import com.bos.payment.appName.utils.Utils.toast
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.serviceWiseTrans.Data
import com.bos.payment.appName.data.model.serviceWiseTrans.TransactionReportReq
import com.bos.payment.appName.data.model.serviceWiseTrans.TransactionReportRes
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.databinding.ActivityServiceWiseTransactionBinding
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import java.io.File
import java.io.FileOutputStream
import java.io.InterruptedIOException
import java.util.Calendar

class ServiceWiseTransaction : AppCompatActivity() {
    private lateinit var bin: ActivityServiceWiseTransactionBinding
    private var mStash: MStash? = null
    private val myCalender = Calendar.getInstance()
    private val myCalender1 = Calendar.getInstance()
    private lateinit var transactionReportsAdapter: TransactionReportsAdapter
    private var transactionList = ArrayList<Data>()
    private var transactionText: String? = ""
    private lateinit var pd: AlertDialog
    private lateinit var viewModel: ServiceChangeViewModel
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel

    private val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        myCalender.set(Calendar.YEAR, year)
        myCalender.set(Calendar.MONTH, monthOfYear)
        myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        bin.fromDate.let { Utils.updateLabel(it, myCalender, "Update From Date") }
    }

    private val date1 = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        myCalender1.set(Calendar.YEAR, year)
        myCalender1.set(Calendar.MONTH, monthOfYear)
        myCalender1.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        bin.toDate.let { Utils.updateLabel(it, myCalender1, "Update To Date") }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bin = ActivityServiceWiseTransactionBinding.inflate(layoutInflater)
        setContentView(bin.root)

        initView()
        setDropDown()
        btnListener()
    }

    private fun setDropDown() {

        val arrayListSpinner = resources.getStringArray(R.array.transaction_array)
        val adapters = ArrayAdapter(
            this@ServiceWiseTransaction, R.layout.spinner_right_aligned, arrayListSpinner
        )
        adapters.setDropDownViewResource(R.layout.spinner_right_aligned)
        bin.allTransaction.adapter = adapters
        bin.allTransaction.setSelection(0)
        bin.allTransaction.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                transactionText = if (position > 0) {
                    parent!!.getItemAtPosition(position).toString()
                } else {
                    null
                }
                Log.e("TAG", "onItemSelected: " + transactionText)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun buttonCreateExcelFile() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                checkAndRequestStoragePermissions()
            }

            else -> {
                val PERMISSION_REQUEST_CODE_NOTIFICATION = 0
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE_NOTIFICATION
                )
            }
        }
    }

    private fun checkAndRequestStoragePermissions() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // No need to request storage permissions on Android 11 and above, just check if access to manage external storage is allowed
                if (Environment.isExternalStorageManager()) {
                    generateExcelFile(transactionList)
                }
            }

            ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                generateExcelFile(transactionList)
            }

            else -> {
                val PERMISSION_REQUEST_CODE_STORAGE = 0
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), PERMISSION_REQUEST_CODE_STORAGE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val PERMISSION_REQUEST_CODE_STORAGE = null
        val PERMISSION_REQUEST_CODE_NOTIFICATION = null
        when (requestCode) {
            PERMISSION_REQUEST_CODE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    generateExcelFile(transactionList)
                } else {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_LONG).show()
                }
            }

            PERMISSION_REQUEST_CODE_NOTIFICATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkAndRequestStoragePermissions()
                } else {
                    Toast.makeText(this, "Notification permission denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showDownloadNotification(filePath: String) {
        val channelId = "download_channel"
        val channelName = "File Download"

        // Create the notification channel (required for Android O and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val fileUri = FileProvider.getUriForFile(
            this, "${applicationContext.packageName}.fileprovider", File(filePath)
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, "application/vnd.ms-excel")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Build the notification
        val builder =
            NotificationCompat.Builder(this, channelId).setSmallIcon(R.drawable.ic_splash_logo)
                .setContentTitle("Download Complete")
                .setContentText("Excel file created at: $filePath")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent)
                .setAutoCancel(true)

        // Show the notification
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@ServiceWiseTransaction, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(1, builder.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun btnListener() {
        bin.serviceWiseToolbar.dashboardImage.setOnClickListener {
            startActivity(Intent(this@ServiceWiseTransaction, DashboardActivity::class.java))
        }

        bin.fromDate.setOnClickListener {
            Utils.hideKeyboard(this)
            DatePickerDialog(
                this,
                date,
                myCalender[Calendar.YEAR],
                myCalender[Calendar.MONTH],
                myCalender[Calendar.DAY_OF_MONTH]
            ).show()
        }
        bin.toDate.setOnClickListener {
            Utils.hideKeyboard(this)
            DatePickerDialog(
                this,
                date1,
                myCalender1[Calendar.YEAR],
                myCalender1[Calendar.MONTH],
                myCalender1[Calendar.DAY_OF_MONTH]
            ).show()
        }
        bin.checkBoxDate.setOnCheckedChangeListener { _, isChecked ->
            bin.fromToDateLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        bin.proceedBtn.setOnClickListener {
            validationTransactionReports()
        }
        bin.cancelBtn.setOnClickListener {
            onBackPressed()
        }
        bin.downloadExcel.setOnClickListener {
            buttonCreateExcelFile()
        }
    }

    private fun validationTransactionReports() {
        if (transactionText.isNullOrEmpty()) {
            toast("Select transaction type")
        } else {
            transactionAPICalling()
        }
    }

    private fun transactionAPICalling() {
        this.runIfConnected {
            val currentDateAndTime = Utils.getCurrentDate()
            val transactionReportReq = TransactionReportReq(
                parmFromDate = "2000-12-20",
                parmToDate = currentDateAndTime,
                parmFlag = "Payout Report",
                parmFla2 = "All",
                parmUser = mStash!!.getStringValue(Constants.RegistrationId, ""),
                paramMerchantCode = mStash!!.getStringValue(Constants.MerchantId, "")
            )
            Log.d("transactionReportReq", Gson().toJson(transactionReportReq))
            getAllApiServiceViewModel.getAllTransactionReport(transactionReportReq)
                .observe(this) { resource ->
                    resource?.let {
                        when (it.apiStatus) {
                            ApiStatus.SUCCESS -> {
                                pd.dismiss()
                                it.data?.let { users ->
                                    users.body()?.let { response ->
                                        transactionAPICallingRes(response)
                                    }
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
    private fun transactionAPICallingRes(response: TransactionReportRes) {
            try {
            if (response.isSuccess == true) {
                response.data.let { reportsList ->
                    bin.recLayout.visibility = View.VISIBLE
                    transactionList.clear()
                    transactionList.addAll(reportsList)
                    transactionReportsAdapter.notifyDataSetChanged()
                    bin.downloadExcel.visibility = View.GONE
                    toast(response.returnMessage.toString())
                }
            } else {
                pd.dismiss()
                bin.recLayout.visibility = View.GONE
                toast(response.returnMessage.toString())
            }
        } catch (e: InterruptedIOException) {
            e.printStackTrace()
            pd.dismiss()
            toast(e.message.toString())
        }
    }

//    private fun transactionAPICalling() {
//        val serviceWiseTransactionReq = ServiceWiseTransactionReq(
//            RegistrationID = mStash!!.getStringValue(Constants.RegistrationId, ""),
//            ReportCategory = transactionText,
//            CompanyCode = mStash!!.getStringValue(Constants.CompanyCode,"")
//        )
//        Log.d("transactionAPICalling", Gson().toJson(serviceWiseTransactionReq))
//
//        viewModel.serviceWise(serviceWiseTransactionReq).observe(this) { resource ->
//            resource?.let {
//                when (it.apiStatus) {
//                    ApiStatus.SUCCESS -> {
//                        pd.dismiss()
//                        it.data?.let { users ->
//                            users.body()?.let { response ->
//                                transactionReportsRes(response)
//                            }
//                        }
//                    }
//
//                    ApiStatus.ERROR -> {
//                        pd.dismiss()
////                        toast(it.message.toString())
//                        toast("No Data Found")
//                    }
//
//                    ApiStatus.LOADING -> {
//                        pd.show()
//                    }
//                }
//            }
//        }
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    private fun transactionReportsRes(response: List<ServiceWiseTransactionRes>) {
//        try {
//            if (response[0].Status == true) {
//                bin.recLayout.visibility = View.VISIBLE
//                transactionList.clear()
//                pd.dismiss()
//                transactionList.addAll(response)
//                transactionReportsAdapter.notifyDataSetChanged()
//                bin.downloadExcel.visibility = View.GONE
//                toast(response[0].message.toString())
//            } else {
//                pd.dismiss()
//                bin.recLayout.visibility = View.GONE
//                toast("Data not found or Status is false")
//            }
//        } catch (e: InterruptedIOException) {
//            e.printStackTrace()
//            pd.dismiss()
//            toast(e.message.toString())
//        }
//    }

    private fun generateExcelFile(transactionList: List<Data>) {
        val workbook: Workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("ServiceWiseTransReports_$transactionText")

        val headerRow: Row = sheet.createRow(0)
        val headers = resources.getStringArray(R.array.transaction_array)

        for (i in headers.indices) {
            val cell: Cell = headerRow.createCell(i)
            cell.setCellValue(headers[i])
        }

        for (i in transactionList.indices) {
            val row: Row = sheet.createRow(i + 1)
            row.createCell(0).setCellValue(transactionList[i].sNo.toString())
            row.createCell(1).setCellValue(transactionList[i].transactionno.toString())
            row.createCell(2).setCellValue(transactionList[i].upiRefID)
            row.createCell(3).setCellValue(transactionList[i].date)
            row.createCell(4).setCellValue(transactionList[i].time)
            row.createCell(5).setCellValue(transactionList[i].transferfrom)
            row.createCell(6).setCellValue(transactionList[i].transferto)
            row.createCell(7).setCellValue(transactionList[i].servicETYPE)
            row.createCell(8).setCellValue(transactionList[i].cr)
            row.createCell(9).setCellValue(transactionList[i].dr)
            row.createCell(10).setCellValue(transactionList[i].tranAmt)
            row.createCell(11).setCellValue(transactionList[i].remarks)

        }

        val downloadDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadDir, "ServiceWiseTransReports_$transactionText.xls")
        try {
            val fileOut = FileOutputStream(file)
            workbook.write(fileOut)
            fileOut.close()
            workbook.close()
            Toast.makeText(this, "Excel file created: ${file.absolutePath}", Toast.LENGTH_LONG)
                .show()

            // Show notification
            showDownloadNotification(file.absolutePath)
        } catch (e: Exception) {
            Toast.makeText(this, "File creation failed", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initView() {
        mStash = MStash.getInstance(this)!!
        pd = PD(this)
        bin.fromToDateLayout.visibility = View.GONE
        bin.downloadExcel.visibility = View.GONE
        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(
            GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)
        ))[GetAllApiServiceViewModel::class.java]
        viewModel = ViewModelProvider(
            this,
            ServiceChangeViewModelFactory(ServiceChangeRepository(RetrofitClient.apiAllAPIService))
        )[ServiceChangeViewModel::class.java]

        bin.agentTypeService.setText(mStash!!.getStringValue(Constants.AgentType, ""))
        bin.registrationIdService.setText(mStash!!.getStringValue(Constants.RegistrationId, ""))

        bin.recycle1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        transactionReportsAdapter = TransactionReportsAdapter(this, transactionList)
        bin.recycle1.adapter = transactionReportsAdapter

        try {

            val imageUrl = mStash!!.getStringValue(Constants.CompanyLogo, "")
            Picasso.get().load(imageUrl)
//            .placeholder(R.drawable.placeholder)  // Optional: placeholder while loading
                .error(R.drawable.ic_error)        // Optional: error image if load fails
                .into(bin.serviceWiseToolbar.dashboardImage)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
}
