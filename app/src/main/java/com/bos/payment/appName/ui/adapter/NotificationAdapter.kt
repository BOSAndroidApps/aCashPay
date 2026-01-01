package com.bos.payment.appName.ui.adapter

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.servicesbasednotification.DataItem
import com.bos.payment.appName.data.model.subscription.FeatureDataItem
import com.bos.payment.appName.databinding.FeatureItemLayoutBinding
import com.bos.payment.appName.databinding.NotiItemLayoutBinding

class NotificationAdapter (var context: Context, var datalist : List<DataItem?>?) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {


    class ViewHolder(var binding: NotiItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NotiItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(datalist!![position]!!.expiryStatus.equals("EXPIRING",ignoreCase = true)){
            holder.binding.txtStatus.backgroundTintList=context.resources.getColorStateList(R.color.orange)
        }
        else{
            holder.binding.txtStatus.backgroundTintList=context.resources.getColorStateList(R.color.red)
        }

        holder.binding.txtFeatureName.text = datalist!![position]!!.featureName
        holder.binding.txtPlan.text = "Plan : ${datalist!![position]!!.planName}"
        holder.binding.txtStatus.text = datalist!![position]!!.expiryStatus
        holder.binding.txtMessage.text = datalist!![position]!!.notificationMessage!!
        holder.binding.txtDaysRemaining.text = "${datalist!![position]!!.daysRemaining!!} Days Remaining"

    }


    override fun getItemCount(): Int {
        return datalist!!.size
    }


}