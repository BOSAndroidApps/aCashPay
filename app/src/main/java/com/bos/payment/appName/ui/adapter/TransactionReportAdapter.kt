package com.bos.payment.appName.ui.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.justpaymodel.RetailerDataItem
import com.bos.payment.appName.data.model.transactionreportsmodel.DataItem
import com.bos.payment.appName.data.model.transactionreportsmodel.TransactionReportsReq
import com.bos.payment.appName.databinding.ContactlistItemBinding
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

class TransactionReportAdapter(var context: Context, var transactionList: List<DataItem?>) :
    RecyclerView.Adapter<TransactionReportAdapter.ViewHolder>() {


    class ViewHolder(var binding: TransactionReportItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionReportAdapter.ViewHolder {
        val binding = TransactionReportItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: TransactionReportAdapter.ViewHolder, position: Int) {
         if(transactionList[position]!!.status.isNullOrBlank()){
             // for commission report
             holder.binding.withoutCommissionRequestcard.visibility=View.GONE
             holder.binding.CommissionRequestcard.visibility=View.VISIBLE

             if (transactionList[position]!!.cr!!.isNotEmpty()) {
                 holder.binding.tranAmt.text="+ ₹${String.format("%.2f", transactionList[position]!!.tranAmt!!.toDouble())}"
                 holder.binding.credamt.text="₹${String.format("%.2f", transactionList[position]!!.cr!!.toDouble())}"
                 holder.binding.tranAmt.setTextColor(ContextCompat.getColor(context, R.color.green))
             }

             holder.binding.comtransactionno.text = transactionList[position]!!.transactionno

             if(transactionList[position]?.tdsAmt.isNullOrBlank()){
              // for deposit case..............................................
              holder.binding.tdsamtlayout.visibility=View.GONE
              holder.binding.commissionlayout.visibility=View.GONE
             }
             else{
                 holder.binding.tdsamtlayout.visibility=View.VISIBLE
                 holder.binding.commissionlayout.visibility=View.VISIBLE
                 holder.binding.tdsamt.text = "₹${String.format("%.2f", transactionList[position]?.tdsAmt!!.toDouble())}"
             }

             holder.binding.comupiRefID.text = transactionList[position]!!.upiRefID
             holder.binding.commisservicetype.text = transactionList[position]!!.servicETYPE
             holder.binding.commissiondatetime.text = "${transactionList[position]!!.date} , ${transactionList[position]!!.time}"

             holder.binding.commissionremarks.text = transactionList[position]!!.remarks
             holder.binding.commissionremarkflag.text = transactionList[position]!!.flagRemarks

         }

         else{
             holder.binding.withoutCommissionRequestcard.visibility=View.VISIBLE
             holder.binding.CommissionRequestcard.visibility=View.GONE

             if (transactionList[position]!!.status!!.toLowerCase().equals("pending")) {
                 holder.binding.statustxt.setTextColor(ContextCompat.getColor(context, R.color.orange))
             }

             if (transactionList[position]!!.status!!.toLowerCase().equals("approved")) {
                 holder.binding.statustxt.setTextColor(ContextCompat.getColor(context, R.color.green))
             }

             if (transactionList[position]!!.status!!.toLowerCase().equals("rejected") || transactionList[position]!!.status!!.toLowerCase().equals("failed")) {
                 holder.binding.statustxt.setTextColor(ContextCompat.getColor(context, R.color.red))
                 holder.binding.raiseticketcard.visibility= View.INVISIBLE
             }
             else {
                 holder.binding.raiseticketcard.visibility= View.VISIBLE
             }

             holder.binding.statustxt.text = transactionList[position]!!.status

             holder.binding.transactionno.text = transactionList[position]!!.transactionno
             holder.binding.upiRefID.text = transactionList[position]!!.upiRefID
             holder.binding.servicetype.text = transactionList[position]!!.servicETYPE
             holder.binding.datetime.text = "${transactionList[position]!!.date} , ${transactionList[position]!!.time}"

             if(transactionList[position]!!.serviceChGst!!.isNotEmpty() && !transactionList[position]!!.serviceChGst.equals("0.00")){
                 holder.binding.servicechargelayout.visibility=View.VISIBLE
                 holder.binding.servicecharge.text= "₹ ${String.format("%.2f", transactionList[position]!!.serviceChGst!!.toDouble())}"
             }
             else{
                 holder.binding.servicechargelayout.visibility=View.GONE
             }

             if (transactionList[position]!!.cr!!.isNotEmpty()) {
                 holder.binding.debitorcredit.text = "Credited Amt"
                 holder.binding.amount.text="+ ₹${String.format("%.2f", transactionList[position]!!.tranAmt!!.toDouble())}"
                 holder.binding.debitamt.text="₹${String.format("%.2f", transactionList[position]!!.cr!!.toDouble())}"
                 holder.binding.amount.setTextColor(ContextCompat.getColor(context, R.color.green))
             }

             if (transactionList[position]!!.dr!!.isNotEmpty()) {
                 holder.binding.debitorcredit.text = "Debited Amt"
                 holder.binding.amount.text=" - ₹${String.format("%.2f", transactionList[position]!!.tranAmt!!.toDouble())}"
                 holder.binding.debitamt.text="₹${String.format("%.2f", transactionList[position]!!.dr!!.toDouble())}"
                 holder.binding.amount.setTextColor(ContextCompat.getColor(context, R.color.black))
             }

             holder.binding.servicechargelayout.setOnClickListener {
                 var commission = transactionList[position]!!.retailerComm
                 var servicecharge = transactionList[position]!!.serviceCh
                 var gst = transactionList[position]!!.gst
                 openDialogFor(commission!!.toDouble(),servicecharge!!.toDouble(),gst!!.toDouble())
             }

             holder.binding.remarks.text = transactionList[position]!!.remarks

             holder.binding.raiseticketcard.setOnClickListener {
                 servicetype = transactionList[position]!!.servicETYPE!!
                 transactionNo = transactionList[position]!!.transactionno!!
                 RefID = transactionList[position]!!.upiRefID!!
                 date = transactionList[position]!!.date!!
                 time = transactionList[position]!!.time!!
                 transferfrom = transactionList[position]!!.transferfrom!!
                 transferto = transactionList[position]!!.transferto!!
                 creditamount = transactionList[position]!!.cr!!
                 debitamount = transactionList[position]!!.dr!!
                 remarks = transactionList[position]!!.remarks!!
                 var activity = context as TransactionReportsActivity
                 if(activity!=null){
                     activity.hitApiForCheckRaiseTicket(transactionList[position]!!.transactionno!!)
                 }

             }

         }

    }


    override fun getItemCount(): Int {
        return transactionList!!.size
    }



    @SuppressLint("SetTextI18n")
    fun openDialogFor(commission: Double, servicecharge: Double ,gst: Double) {
        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.commisionlayout)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        dialog.setCanceledOnTouchOutside(false)

        val transferamttxt = dialog.findViewById<TextView>(R.id.commission)
        val serviceChargetxt = dialog.findViewById<TextView>(R.id.servicecharge)
        val gsttxt = dialog.findViewById<TextView>(R.id.gst)

        val cancel = dialog.findViewById<ImageView>(R.id.cancel)

        transferamttxt.text = "${String.format("%.2f", commission)}"
        serviceChargetxt.text = "${String.format("%.2f", servicecharge)}"
        gsttxt.text = "${String.format("%.2f", gst)}"


        cancel.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show() // ✅ REQUIRED
    }




}