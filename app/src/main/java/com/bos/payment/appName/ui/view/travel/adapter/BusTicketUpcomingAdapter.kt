package com.bos.payment.appName.ui.view.travel.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.travel.bus.busTicket.DataItem
import com.bos.payment.appName.databinding.UpcomingticketitemlayoutBinding
import com.bos.payment.appName.ui.view.travel.busfragment.UpcomingBus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class BusTicketUpcomingAdapter(
    private var context: Context,
    private var passangerList: MutableList<DataItem>,
    var fragment:UpcomingBus
) :
    RecyclerView.Adapter<BusTicketUpcomingAdapter.ViewHolder>() {

    var headers: Array<String> = arrayOf("Select","Name", "Gender", "Seat No", "Ticket No")
    var checkarrow :Boolean = false
    val selectedPositions : MutableList<Int> = mutableListOf()
    private var expandedPosition = -1

    inner class ViewHolder(val binding: UpcomingticketitemlayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var pnr_No = binding.pnrNumber
        var booking_refNo = binding.bookingRefNo
        var boarding_time = binding.boardingTime
        var dropping_time = binding.droppingTime
        var fromcity = binding.fromcity
        var tocity = binding.tocity
        var busoperatorname = binding.busoperatorname
        var busType = binding.busType
        var mainPassangerName = binding.mainPassangerName
        var travelDate = binding.travelDate
        var duration = binding.duration
        var passangerquantity = binding.passangerquantity
        var detailslayout = binding.detailslayout
        var cancelTicketLayout = binding.cancelTicketLayout
        var passangerListTable = binding.passangerList
        var arrowimage=binding.arrowimage
        var canceltxt= binding.canceltxt
        var view_ticket= binding.viewTicket
        var passangerDetailslayout= binding.passangerDetailslayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bin = UpcomingticketitemlayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(bin)
    }


    override fun getItemCount(): Int {
        return passangerList.size
    }


    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cancelTicketLayout.isEnabled=false
        holder.pnr_No.text=(passangerList.get(position).transportPNR)
        holder.booking_refNo.text=(passangerList.get(position).bookingRefNo)

        if(position == expandedPosition){
            holder.passangerDetailslayout.visibility=View.VISIBLE
            holder.boarding_time.text=(passangerList.get(position).boardingTime)
            holder.dropping_time.text=(passangerList.get(position).droppingTime)
            holder.fromcity.text=(passangerList.get(position).fromCity)
            holder.tocity.text=(passangerList.get(position).toCity)
            holder.busoperatorname.text=(passangerList.get(position).busoperatorname)
            holder.busType.text=(passangerList.get(position).busType)
            holder.mainPassangerName.text=(passangerList.get(position).passangerList?.get(0)!!.paXName)
            holder.travelDate.text=(passangerList.get(position).travelDate)
            holder.passangerquantity.text="${passangerList[position].passangerList!!.size}"
            holder.duration.text= calculateDuration(passangerList.get(position).boardingTime,passangerList.get(position).droppingTime)

                holder.passangerListTable.removeAllViews()
                // Add header row
                val headerRow = TableRow(context)

                for (header in headers) {
                    val textView = TextView(context)
                    textView.text = header
                    textView.setPadding(8, 8, 8, 8)
                    textView.setBackgroundColor(Color.parseColor("#E0E0E0"))
                    textView.setTextColor(Color.BLACK)
                    textView.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.dimen_12sp)
                    )
                    textView.setTypeface(textView.typeface, Typeface.BOLD)
                    textView.gravity = Gravity.START
                    textView.layoutParams =
                        TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    headerRow.addView(textView)
                }

                holder.passangerListTable.addView(headerRow)

                passangerList.get(position).tableData.forEachIndexed { index, row ->
                    val tableRow = TableRow(context)

                    // CheckBox
                    val checkBox = CheckBox(context).apply {
                        setPadding(8, 8, 8, 8)
                        gravity = Gravity.START
                        layoutParams =
                            TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)

                        setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                selectedPositions.add(index) // Use index

                            } else {
                                selectedPositions.remove(index)

                            }

                            // Update the button state based on selectedPositions size
                            if (selectedPositions.isNotEmpty()) {
                                holder.cancelTicketLayout.backgroundTintList =
                                    resources.getColorStateList(R.color.red)
                                holder.canceltxt.setTextColor(resources.getColor(R.color.white))
                                holder.cancelTicketLayout.isEnabled = true
                            } else {
                                holder.cancelTicketLayout.backgroundTintList =
                                    resources.getColorStateList(R.color.button_text_gray)
                                holder.canceltxt.setTextColor(resources.getColor(R.color.black))
                                holder.cancelTicketLayout.isEnabled = false
                            }

                        }
                    }

                    tableRow.addView(checkBox)

                    // Remaining columns
                    for (i in row.indices) {
                        val textView = TextView(context).apply {
                            text = row[i]
                            setPadding(8, 8, 8, 8)
                            setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                context.resources.getDimension(R.dimen.dimen_12sp)
                            )
                            setTextColor(Color.BLACK)
                            gravity = Gravity.START
                            layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                        }
                        tableRow.addView(textView)
                    }
                    holder.passangerListTable.addView(tableRow)
                }

        }
        else{
            if(passangerList[position].dataUpdate){
                holder.passangerDetailslayout.visibility=View.VISIBLE
            } else{
                holder.passangerDetailslayout.visibility=View.GONE
            }
        }



        holder.detailslayout.setOnClickListener {
            if(checkarrow){
                checkarrow =false
                holder.passangerListTable.visibility=View.GONE
                holder.cancelTicketLayout.visibility=View.GONE
                holder.arrowimage.setImageDrawable(context.resources.getDrawable(R.drawable.arrowdown))
            }
            else{
                checkarrow =true
                holder.passangerListTable.visibility=View.VISIBLE
                holder.cancelTicketLayout.visibility=View.VISIBLE
                holder.arrowimage.setImageDrawable(context.resources.getDrawable(R.drawable.arrowup))
            }

        }


        holder.cancelTicketLayout.setOnClickListener {
            if(fragment!=null){
                fragment.hitApiForTicketCancellationCharge(selectedPositions,position)
            }
        }


        holder.view_ticket.setOnClickListener {
            if(fragment!=null) {
                fragment.hitApiForPassangerDetails(position,passangerList.get(position).bookingRefNo)
            }
        }



    }

    public fun updateList(passangerList: MutableList<DataItem>, position: Int){
        this.passangerList= passangerList
        expandedPosition =  position
        notifyDataSetChanged()
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateDuration(departure: String?, arrival: String?): String {

        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)

        val depTime = LocalTime.parse(departure!!.uppercase(), formatter)
        val arrTime = LocalTime.parse(arrival!!.uppercase(), formatter)

        // Use today's date for both times
        val today = LocalDate.now()
        var depDateTime = LocalDateTime.of(today, depTime)
        var arrDateTime = LocalDateTime.of(today, arrTime)

        // If arrival is before departure, it must be on the next day
        if (arrDateTime.isBefore(depDateTime)) {
            arrDateTime = arrDateTime.plusDays(1)
        }

        val duration = java.time.Duration.between(depDateTime, arrDateTime)
        val hours = duration.toHours()
        val minutes = duration.minusHours(hours).toMinutes()

        return String.format("%02d h %02d m", hours, minutes)
    }




}