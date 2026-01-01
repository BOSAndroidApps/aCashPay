package com.bos.payment.appName.ui.adapter

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.makepaymentnew.BankDataItem
import com.bos.payment.appName.data.model.subscription.FeatureDataItem
import com.bos.payment.appName.data.model.supportmanagement.TicketsItem
import com.bos.payment.appName.databinding.BankdetailsitemlayoutBinding
import com.bos.payment.appName.databinding.FeatureItemLayoutBinding
import com.bos.payment.appName.databinding.ImagelayoutforraiseticketBinding
import com.bos.payment.appName.databinding.TicketstatusLayoutBinding
import com.bos.payment.appName.ui.view.Dashboard.activity.JustPeDashboard.Companion.QRBimap
import com.bos.payment.appName.ui.view.makepayment.MakePaymentActivity
import com.bos.payment.appName.ui.view.makepayment.MakePaymentActivity.Companion.BankAccountNumber
import com.bos.payment.appName.ui.view.makepayment.MakePaymentActivity.Companion.BankName
import com.bos.payment.appName.ui.view.makepayment.MakePaymentActivity.Companion.checkQR
import com.bos.payment.appName.ui.view.supportmanagement.ChatToAdminActivity
import com.bos.payment.appName.ui.view.supportmanagement.ChatToAdminActivity.Companion.checkForSendChat
import com.bos.payment.appName.ui.view.supportmanagement.ChatToAdminActivity.Companion.commentId
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Constants.downloadImageFromUrl
import com.bos.payment.appName.utils.Utils.generateQrBitmap
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeatureListAdapter(var context: Context, var datalist:MutableList<FeatureDataItem?>?,var clickOnItem: ClickOnItem) : RecyclerView.Adapter<FeatureListAdapter.ViewHolder>() {

    lateinit var dialog: Dialog
    lateinit var pd: ProgressDialog

    class ViewHolder(var binding: FeatureItemLayoutBinding ) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureListAdapter.ViewHolder {
        val binding = FeatureItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: FeatureListAdapter.ViewHolder, position: Int) {

        holder.binding.tvFeature.text = datalist!![position]!!.featureName
        holder.binding.tvDuration.text = datalist!![position]!!.validityDuration
        holder.binding.tvPrice.text = datalist!![position]!!.billingCost
        holder.binding.tvExpiry.text = extractDate(datalist!![position]!!.expiryDate!!)/*.replace(Regex("<.*?>"), "")*/
        holder.binding.tvRenew.setOnClickListener {
            clickOnItem.itemClick(datalist!![position]!!)
        }



    }


    fun extractDate(value: String?): String {
        if (value.isNullOrEmpty()) return ""

        return android.text.Html.fromHtml(value, android.text.Html.FROM_HTML_MODE_LEGACY).toString().trim()
    }


    override fun getItemCount(): Int {
        return datalist!!.size
    }


   fun interface ClickOnItem{
        fun itemClick(item : FeatureDataItem )
    }





}