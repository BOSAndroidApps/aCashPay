package com.bos.payment.appName.ui.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.justpaymodel.RetailerDataItem
import com.bos.payment.appName.data.model.transactionreportsmodel.DataItem
import com.bos.payment.appName.data.model.transactionreportsmodel.TransactionReportsReq
import com.bos.payment.appName.databinding.ContactlistItemBinding
import com.bos.payment.appName.databinding.RenewServicesReportItemlayoutBinding
import com.bos.payment.appName.databinding.TransactionReportItemLayoutBinding
import com.bos.payment.appName.ui.view.Dashboard.transactionreports.RaiseTicketActivity
import com.bos.payment.appName.ui.view.Dashboard.transactionreports.RaiseTicketActivity.Companion.RefID
import com.bos.payment.appName.ui.view.Dashboard.transactionreports.RaiseTicketActivity.Companion.creditamount
import com.bos.payment.appName.ui.view.Dashboard.transactionreports.RaiseTicketActivity.Companion.date
import com.bos.payment.appName.ui.view.Dashboard.transactionreports.RaiseTicketActivity.Companion.debitamount
import com.bos.payment.appName.ui.view.Dashboard.transactionreports.RaiseTicketActivity.Companion.remarks
import com.bos.payment.appName.ui.view.Dashboard.transactionreports.RaiseTicketActivity.Companion.servicetype
import com.bos.payment.appName.ui.view.Dashboard.transactionreports.RaiseTicketActivity.Companion.time
import com.bos.payment.appName.ui.view.Dashboard.transactionreports.RaiseTicketActivity.Companion.transactionNo
import com.bos.payment.appName.ui.view.Dashboard.transactionreports.RaiseTicketActivity.Companion.transferfrom
import com.bos.payment.appName.ui.view.Dashboard.transactionreports.RaiseTicketActivity.Companion.transferto
import com.bos.payment.appName.ui.view.Dashboard.transactionreports.TransactionReportsActivity
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RenewServicesReportAdapter(var context: Context, var transactionList: List<DataItem?>) :
    RecyclerView.Adapter<RenewServicesReportAdapter.ViewHolder>() {


    class ViewHolder(var binding: RenewServicesReportItemlayoutBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RenewServicesReportAdapter.ViewHolder {
        val binding = RenewServicesReportItemlayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.commisservicetype.text= transactionList[position]!!.servicETYPE
        holder.binding.tranAmt.text= "- ${transactionList[position]!!.tranAmt}"
        holder.binding.comtransactionno.text= transactionList[position]!!.transactionno
        holder.binding.comupiRefID.text= transactionList[position]!!.upiRefID
        holder.binding.commissionremarks.text= transactionList[position]!!.remarks
        holder.binding.commissionremarkflag.text= transactionList[position]!!.flagRemarks

        val inputFormat = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")
        val outputFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")

        val dateTime = LocalDateTime.parse("${transactionList[position]!!.date} ${transactionList[position]!!.time}", inputFormat)
        val formatted = dateTime.format(outputFormat)

        holder.binding.datetime.text= formatted


    }


    override fun getItemCount(): Int {
        return transactionList!!.size
    }




}