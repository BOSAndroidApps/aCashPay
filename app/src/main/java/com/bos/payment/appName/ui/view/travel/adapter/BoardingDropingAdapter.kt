package com.bos.payment.appName.ui.view.travel.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bos.payment.appName.data.model.travel.bus.searchBus.BoardingDropingModel
import com.bos.payment.appName.databinding.BusSearchItemBinding
import com.bos.payment.appName.databinding.PickupdropuppointslayoutBinding

class BoardingDropingAdapter(var context: Context , var list:MutableList<BoardingDropingModel>):RecyclerView.Adapter<BoardingDropingAdapter.ViewHolder>(){


     class ViewHolder(var bin:PickupdropuppointslayoutBinding) : RecyclerView.ViewHolder(bin.root)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardingDropingAdapter.ViewHolder {
        val bin = PickupdropuppointslayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(bin)
    }

    override fun onBindViewHolder(holder: BoardingDropingAdapter.ViewHolder, position: Int) {
      holder.bin.timetxt.text=list[position].date
      holder.bin.locationtxt.text=list[position].boarddropname

      if(list[position].date.isNullOrBlank())  {
          holder.bin.icon.setImageResource(list[position].imageicon)
          holder.bin.icon.visibility= View.VISIBLE
          holder.bin.timetxt.visibility= View.GONE
          holder.bin.view.visibility=View.GONE
      }
      else{
          holder.bin.icon.visibility= View.GONE
          holder.bin.timetxt.visibility= View.VISIBLE
          holder.bin.view.visibility=View.VISIBLE
      }
    }

    override fun getItemCount(): Int {
      return   list.size
    }

}