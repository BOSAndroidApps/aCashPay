package com.bos.payment.appName.ui.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bos.payment.appName.R
import com.bos.payment.appName.adapter.ViewPlanListLatestAdapter.ClickListener
import com.bos.payment.appName.data.model.recharge.mobile.MobileRechargePlanModel
import com.bos.payment.appName.databinding.LayoutForPlanNameBinding
import com.bos.payment.appName.databinding.ServiceWiseTransactionItemBinding


class RechargePlanNameAdapter(var context: android.content.Context, var planName:MutableList<MobileRechargePlanModel>, private val clickListener: ClickListener) : RecyclerView.Adapter<RechargePlanNameAdapter.ViewHolder>() {

    var selectionItem = 0
    private var isDefaultClicked = false

    class ViewHolder (var bin: LayoutForPlanNameBinding) : RecyclerView.ViewHolder(bin.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bin = LayoutForPlanNameBinding.inflate(LayoutInflater.from(context), parent,false)
        return ViewHolder(bin)
    }

    override fun getItemCount(): Int {
       return planName.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bin.topup.text=planName[position].arrayName
        val colorRes = if (selectionItem == position) R.color.blue else R.color.black
        holder.bin.topup.setTextColor(context.getColor(colorRes))


        if (position == 0 && !isDefaultClicked) {
            isDefaultClicked = true
            clickListener.itemClick(planName[position].arrayName)
        }


        holder.itemView.setOnClickListener {
            val previousSelection = selectionItem
            selectionItem = position
            // notify only two items instead of whole list
            notifyItemChanged(previousSelection)
            notifyItemChanged(selectionItem)
            clickListener.itemClick(planName[position].arrayName)
            val nextPos = position + 1
            if (nextPos < planName.size) {
                (holder.itemView.parent as? RecyclerView)?.smoothScrollToPosition(nextPos)
            }
        }


    }

    interface ClickListener {
        fun itemClick(name: String)
    }

}