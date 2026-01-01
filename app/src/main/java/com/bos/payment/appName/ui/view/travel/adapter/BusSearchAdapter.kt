package com.bos.payment.appName.ui.view.travel.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.travel.bus.searchBus.BoardingDropingModel
import com.bos.payment.appName.data.model.travel.bus.searchBus.Buses
import com.bos.payment.appName.databinding.BusSearchItemBinding
import com.bos.payment.appName.ui.view.travel.busactivity.BusSeating
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.Utils

class BusSearchAdapter(private val context: Context, private val busList: MutableList<Buses>) : RecyclerView.Adapter<BusSearchAdapter.CustomViewHolder>() {
    var boardingPoint:MutableList<BoardingDropingModel> = mutableListOf()
    var droppingPoint:MutableList<BoardingDropingModel> = mutableListOf()
    lateinit var dropboardadapter : BoardingDropingAdapter


    inner class CustomViewHolder(val bin: BusSearchItemBinding): RecyclerView.ViewHolder(bin.root) {
        val companyName: TextView = itemView.findViewById(R.id.companyName)
        val busName: TextView = itemView.findViewById(R.id.busName)
        val arrivalTime: TextView = itemView.findViewById(R.id.arrivalTime)
        val availableSeat: TextView = itemView.findViewById(R.id.availableSeat)
        val amount: TextView = itemView.findViewById(R.id.travelAmount)
        val boardingarrowicon1: ImageView = itemView.findViewById(R.id.arrowicon1)
        val droppingarrowicon1: ImageView = itemView.findViewById(R.id.arrowicon2)
        var boardingId: String?= null
        var droppingId: String?= null
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val bin = BusSearchItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return CustomViewHolder(bin)
    }


    override fun getItemCount(): Int {
        return busList.size
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val model = busList[position]
        val calculateTotalTime = Utils.calculateTimeDifference(model.departureTime.toString(), model.arrivalTime.toString())

        holder.companyName.text = model.operatorName.toString()
        holder.busName.text = model.busType.toString()
        holder.availableSeat.text = model.availableSeats.toString() + " Seat Left"
        holder.arrivalTime.text = model.departureTime.toString() + " - ${calculateTotalTime} - " + model.arrivalTime.toString()

        val minAmount = model?.fareMasters
            ?.mapNotNull { it.basicAmount } // Convert to a list of non-null amounts
            ?.minOrNull() // Get the minimum value

        if (minAmount != null) {
            holder.amount.text = "₹$minAmount"
        }

        else {
            holder.amount.text = "₹0"
        }

        model.boardingDetails.forEach { boardingDetails ->
            holder.boardingId = boardingDetails.boardingId.toString()
        }

        model.droppingDetails.forEach { droppingId ->
            holder.droppingId = droppingId.droppingId.toString()
        }

        var click= false
        var amentclick= false

        holder.bin.selecteddropview.visibility=View.GONE
        holder.bin.pickupdroplayout.visibility= View.VISIBLE

        holder.bin.boardingarrowlayout.setOnClickListener {
            val model = busList[position]
            var boardinglist = model.boardingDetails
            var dropinglist = model.droppingDetails
            boardingPoint.clear()
            droppingPoint.clear()

            boardinglist.forEach { it->
                boardingPoint.add(BoardingDropingModel(it.boardingTime!!,it.boardingName!!,-1))
            }


            dropinglist.forEach { it->
                droppingPoint.add(BoardingDropingModel(it.droppingTime!!,it.droppingName!!,-1))
            }


            if(boardingPoint.size>0){
                setRecyclerviewData(holder.bin,boardingPoint)
            }
            holder.bin.boardtxt.setTextColor(context.getColor(R.color.blue))
            holder.bin.droptxt.setTextColor(context.getColor(R.color.text_hint_color))
            holder.bin.selectedboardview.visibility=View.VISIBLE
            holder.bin.selecteddropview.visibility=View.GONE
            holder.bin.pickupdroplayout.visibility= View.VISIBLE

            holder.bin.arrowicon2.setImageResource(R.drawable.arrowdown)
            amentclick= false

            if(click){
                holder.bin.boardinglayout.visibility= View.GONE
                holder.bin.arrowicon1.setImageResource(R.drawable.arrowdown)
                click= false
            }
            else{
                holder.bin.boardinglayout.visibility= View.VISIBLE
                holder.bin.arrowicon1.setImageResource(R.drawable.arrowup)
                click= true
            }

        }

        holder.bin.amentiteslayout.setOnClickListener {
           var dataList = listOfAmentites()
            boardingPoint.clear()
            droppingPoint.clear()
            holder.bin.pickupdroplayout.visibility= View.GONE
            holder.bin.arrowicon1.setImageResource(R.drawable.arrowdown)
            click= false

            if(amentclick){
                holder.bin.boardinglayout.visibility= View.GONE
                holder.bin.arrowicon2.setImageResource(R.drawable.arrowdown)
                amentclick= false
            }
            else{
                holder.bin.boardinglayout.visibility= View.VISIBLE
                holder.bin.arrowicon2.setImageResource(R.drawable.arrowup)
                amentclick= true
            }

            if(dataList.size>0){
                setRecyclerviewData(holder.bin,dataList)
            }

        }

        holder.bin.boardtxt.setOnClickListener {
            if(boardingPoint.size>0){
                setRecyclerviewData(holder.bin,boardingPoint)
            }
            holder.bin.boardtxt.setTextColor(context.getColor(R.color.blue))
            holder.bin.droptxt.setTextColor(context.getColor(R.color.text_hint_color))
            holder.bin.selectedboardview.visibility=View.VISIBLE
            holder.bin.selecteddropview.visibility=View.GONE
        }

        holder.bin.droptxt.setOnClickListener {
            if(droppingPoint.size>0){
                setRecyclerviewData(holder.bin,droppingPoint)
            }

            holder.bin.selectedboardview.visibility=View.GONE
            holder.bin.selecteddropview.visibility=View.VISIBLE
            holder.bin.boardtxt.setTextColor(context.getColor(R.color.text_hint_color))
            holder.bin.droptxt.setTextColor(context.getColor(R.color.blue))
        }

        holder.itemView.setOnClickListener {
            try {
            val intent = Intent(context, BusSeating::class.java).apply {
                putExtra(Constants.busKey, model.busKey.toString())
                putExtra(Constants.boarding_Id, holder.boardingId.toString())
                putExtra(Constants.dropping_Id, holder.droppingId.toString())
                putExtra(Constants.travelCompanyName, model.operatorName.toString())
                putExtra(Constants.travelTime, calculateTotalTime)
                putExtra(Constants.busName, model.busType.toString())
                putExtra(Constants.travelAmount, minAmount.toString())
                putExtra(Constants.arrivalTime, model.departureTime.toString() + " - " +calculateTotalTime+ " - " + model.arrivalTime.toString())
            }
            context.startActivity(intent)
            }
            catch (e: IndexOutOfBoundsException){
                e.printStackTrace()
            }
        }

    }


    fun listOfAmentites():MutableList<BoardingDropingModel>{
        var datalist : MutableList<BoardingDropingModel> = mutableListOf()
        datalist.add(BoardingDropingModel("","Blanket",R.drawable.blancketicon))
        datalist.add(BoardingDropingModel("","Charging Point",R.drawable.chargepoints))
        datalist.add(BoardingDropingModel("","Emergency Exit",R.drawable.emergency_exit))
        datalist.add(BoardingDropingModel("","Fire Extinguisher",R.drawable.fire))
        datalist.add(BoardingDropingModel("","Hammer",R.drawable.hammer))
        datalist.add(BoardingDropingModel("","GPS",R.drawable.gpslocation))
        datalist.add(BoardingDropingModel("","CCTV",R.drawable.cctv))
        datalist.add(BoardingDropingModel("","First Aid Box",R.drawable.firstaidbox))

        return datalist
    }


    fun setRecyclerviewData(bin : BusSearchItemBinding, list:MutableList<BoardingDropingModel>){
        dropboardadapter= BoardingDropingAdapter(context,list)
        bin.showpickupdroppoint.adapter = dropboardadapter
    }


}