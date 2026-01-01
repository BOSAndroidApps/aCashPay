package com.bos.payment.appName.ui.adapter

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.promocode.GetEligibleReq
import com.bos.payment.appName.data.model.promocode.PromoDataItem
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.databinding.PromocodeItemLayoutBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.promocode.PromocodeDetailsPage.Companion.promoDataItem
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Constants.startExpiryTimer
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale


class PromocodeListAdapter(var context: Context, var getAllApiServiceViewModel: GetAllApiServiceViewModel, val lifecycleOwner: LifecycleOwner, private var promoDataList: List<PromoDataItem?>?, private val onDetailsClick: (PromoDataItem) -> Unit, private val onApplyClick: (PromoDataItem) -> Unit) : RecyclerView.Adapter<PromocodeListAdapter.PromoViewHolder>() {
    var countDownTimer: CountDownTimer? = null
    private var mStash: MStash? = null

    inner class PromoViewHolder(private val binding: PromocodeItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PromoDataItem) {

            // Promo Code & Name
            binding.promocode.text = item.promoCode ?: "-"
            binding.promoname.text = item.promoName ?: "-"

            // Offer Amount
            val offerText = when (item.discountType?.lowercase()) {
                "flat" -> "₹ ${item.discountValue} Cashback"
                "percentage" -> "${item.discountValue}% Cashback"
                else -> "-"
            }

            binding.offeramount.text = "Upto $offerText"

            // Min Transaction
            binding.offerdatelayout.findViewById<TextView>(R.id.maxtxnamount)?.text = "₹ ${String.format("%.2f",item.minTransactionAmount)}"

            // Valid Till
            binding.offerdatelayout.findViewById<TextView>(R.id.tilldate)?.text = formatDate(item.endDate)

            // Services
            binding.offerdatelayout.findViewById<TextView>(R.id.servicesname)?.text = item.applicableServices?.joinToString(", ") ?: "-"

            // Status
            binding.status.text = item.status ?: "Inactive"

            binding.statuscard.setCardBackgroundColor(
                if (item.status.equals("active", true))
                    ContextCompat.getColor(binding.root.context, R.color.green)
                else
                    ContextCompat.getColor(binding.root.context, R.color.grey)
            )

            binding.expriedate.visibility= View.INVISIBLE

            countDownTimer = startExpiryTimer(
                endDate =item.endDate!!,
                onTick = { timeText ->
                    binding.expriedate.text = timeText
                    binding.expriedate.visibility=View.VISIBLE
                },
                onExpire = {
                    binding.expriedate.text = "Expired"
                }
            )

            hitApiForGetEligible(item,binding)

            // Buttons
            binding.details.setOnClickListener {
                onDetailsClick(item)
            }

            binding.applybutton.setOnClickListener {
                onApplyClick(item)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoViewHolder {
        val binding = PromocodeItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PromoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PromoViewHolder, position: Int) {
        mStash = MStash.getInstance(context)
        promoDataList!![position]?.let { holder.bind(it) }
    }


    override fun getItemCount(): Int = promoDataList!!.size


    fun updateList(newList: List<PromoDataItem?>) {
        promoDataList = newList
        notifyDataSetChanged()
    }


    fun formatDate(date: String?): String {
        if (date.isNullOrEmpty()) return "-"

        return try {
            val inputFormat = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss",
                Locale.US
            )
            val outputFormat = SimpleDateFormat(
                "dd MMM yyyy",
                Locale.US
            )

            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate!!)
        } catch (e: Exception) {
            "-"
        }
    }



    fun hitApiForGetEligible(item: PromoDataItem,binding:PromocodeItemLayoutBinding) {
           var userCode = mStash!!.getStringValue(Constants.RegistrationId, "").toString()

            val request = GetEligibleReq(
                fromDate = item.startDate,
                toDate = item.endDate,
                serviceCode = item.applicableServices?.joinToString(",") ?: "-",
                retailerId = userCode,
                operatorCode = "",
                subserviceCode = ""
            )

            Log.d("Eligiblereq", Gson().toJson(request))

            getAllApiServiceViewModel.GetEligibleReq(request).observe(lifecycleOwner) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->

                                    if (response.isSuccess!!) {
                                        Log.d("eligibleresp", String.format("%.2f",response.totalTransactionAmount))

                                        if(response.totalTransactionAmount!! >= item.minTransactionAmount!!){
                                            binding.redeemlayout.visibility= View.VISIBLE
                                        }
                                        else{
                                            binding.redeemlayout.visibility= View.GONE
                                        }

                                    }
                                    else {

                                       // Toast.makeText(context, response.returnMessage, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }

                        ApiStatus.ERROR -> {


                        }

                        ApiStatus.LOADING -> {

                        }

                    }
                }
            }
        }


}
