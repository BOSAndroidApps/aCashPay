package com.bos.payment.appName.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.transactionreportsmodel.DataItem
import com.bos.payment.appName.data.model.transactionreportsmodel.VPADataItem
import com.bos.payment.appName.databinding.TransactionReportItemLayoutBinding
import com.bos.payment.appName.databinding.VpaItemlayoutBinding
import com.bos.payment.appName.ui.adapter.TransactionReportAdapter.ViewHolder
import java.text.SimpleDateFormat
import java.util.Locale

class VPATransactionReportAdapter (var context: Context, var vpatransactionList: List<VPADataItem?>) : RecyclerView.Adapter<VPATransactionReportAdapter.ViewHolder>() {

    class ViewHolder(var binding: VpaItemlayoutBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VPATransactionReportAdapter.ViewHolder {
        val binding = VpaItemlayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return vpatransactionList!!.size
    }

    override fun onBindViewHolder(holder: VPATransactionReportAdapter.ViewHolder, position: Int) {
        val item = vpatransactionList[position] ?: return

        with(holder.binding) {

            if(item.status.equals("1")){
                txtStatus.text = "Success"
                txtStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
                txtAmount.setTextColor(ContextCompat.getColor(context, R.color.green))
            }
            else{
                txtStatus.text = "Failed"
                txtStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
                txtAmount.setTextColor(ContextCompat.getColor(context, R.color.red))
            }

            txtAmount.text = item.amount

            txtPayerVPA.text = item.payervpa
            txtPayeeVPA.text = item.payeevpa
            txtRefId.text = item.txnReferance
            txtName.text = item.payerName
            txtMessage.text = item.gatewayResponseMessage

            val outputFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            val date = inputFormat.parse(item.transactiondate)
            val output = outputFormat.format(date)
            txtDate.text = output

        }

    }


}