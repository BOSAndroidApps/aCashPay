package com.bos.payment.appName.ui.view.travel.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bos.payment.appName.databinding.AirlineslistitemlayotBinding
import com.bos.payment.appName.ui.view.travel.adapter.AirPortListAdapter.Companion
import com.bos.payment.appName.ui.view.travel.adapter.AirPortListAdapter.setClickListner
import com.bos.payment.appName.ui.view.travel.busactivity.BusFilterActivity


class BusAdapter(
    private val context: Context,
    private val flightList: MutableList<Pair<String, Boolean>>,
    private val filterType: BusFilterActivity.BusFilterType,
    private val listener: OnClickListener
) : RecyclerView.Adapter<BusAdapter.CustomViewHolder>() {


    inner class CustomViewHolder(val binding: AirlineslistitemlayotBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = AirlineslistitemlayotBinding.inflate(LayoutInflater.from(context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun getItemCount(): Int = flightList.size


    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val (name, isChecked) = flightList[position]

        holder.binding.airlinecheck.apply {
            text = name

            // Prevent unwanted callbacks
            setOnCheckedChangeListener(null)
            this.isChecked = isChecked

            setOnCheckedChangeListener { _, checked ->
                flightList[position] = name to checked

                // Send selected list only on user action
                listener.setonclicklistner(
                    flightList.filter { it.second }.map { it.first },
                    filterType
                )
            }
        }
    }

    interface OnClickListener {
        fun setonclicklistner(selectedNames: List<String>,filterType: BusFilterActivity.BusFilterType)
    }
}
