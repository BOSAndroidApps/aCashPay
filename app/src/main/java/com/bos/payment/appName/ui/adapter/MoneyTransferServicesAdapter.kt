package com.bos.payment.appName.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.justpaymodel.MoneyTransferServicesModel
import com.bos.payment.appName.databinding.MoneytransferServicesLayoutBinding
import com.bos.payment.appName.ui.view.CreditCardDetailsFragment
import com.bos.payment.appName.ui.view.Dashboard.rechargefragment.RechargeFragment
import com.bos.payment.appName.ui.view.Dashboard.activity.AllServicesSelectionActivity
import com.bos.payment.appName.ui.view.travel.busfragment.BusBookingMainFragment
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.FlightMainFragment
import com.bos.payment.appName.ui.view.travel.train.Trainpage

class MoneyTransferServicesAdapter( var servicesList:List<MoneyTransferServicesModel>, var context: Context, private val activity: AppCompatActivity) : RecyclerView.Adapter<MoneyTransferServicesAdapter.ViewHolder>(){
    var selectionPosition = -1
    var default = 0
    class ViewHolder (var binding : MoneytransferServicesLayoutBinding) : RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bin = MoneytransferServicesLayoutBinding.inflate(LayoutInflater.from(context), parent,false)
        return ViewHolder(bin)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val service = servicesList[position]

       holder.binding.servicesName.text= servicesList[position].name
       holder.binding.servicesImage.setImageResource(servicesList[position].image)

        if (selectionPosition == position) {
            holder.binding.backcard.visibility = View.VISIBLE
            holder.binding.servicesName.setTextColor(context.getColor(R.color.colorPrimary))
        } else {
            holder.binding.backcard.visibility = View.GONE
            holder.binding.servicesName.setTextColor(context.getColor(R.color.black))
        }



        holder.itemView.setOnClickListener {

            selectionPosition = position
            notifyDataSetChanged()


           if(servicesList[position].name.equals(context.getString(R.string.flight)))
           {
             // context.startActivity(Intent(context, FlightMainActivity::class.java))
               if (activity is AllServicesSelectionActivity) {
                   activity.callFragment(FlightMainFragment(), "fight",servicesList[position].featurecode,"FlightMainFragment")
               }
           }


            if(servicesList[position].name.equals(context.getString(R.string.train)))
            {
                // context.startActivity(Intent(context, FlightMainActivity::class.java))
                if (activity is AllServicesSelectionActivity) {
                    activity.callFragment(Trainpage(), "fight",servicesList[position].featurecode,"FlightMainFragment")
                }
            }


           if(servicesList[position].name.equals(context.getString(R.string.bus))){
               //context.startActivity(Intent(context, BookingTravel::class.java))

               if (activity is AllServicesSelectionActivity) {
                   activity.callFragment(BusBookingMainFragment(), "bus",servicesList[position].featurecode,"BusMainFragment")
               }
           }


           if(servicesList[position].name.equals(context.getString(R.string.recharge))){
               if (activity is AllServicesSelectionActivity) {
                   activity.callFragment(RechargeFragment(), "mobile",servicesList[position].featurecode,"")
               }
           }


           if(servicesList[position].name.equals(context.getString(R.string.postpaid))){
               if (activity is AllServicesSelectionActivity) {
                   activity.callFragment(RechargeFragment(), "postpaid",servicesList[position].featurecode,"")
               }
           }


           if(servicesList[position].name.equals(context.getString(R.string.dth))){
               if (activity is AllServicesSelectionActivity) {
                   activity.callFragment(RechargeFragment(), "dth",servicesList[position].featurecode,"")
               }
           }


           if(servicesList[position].name.equals(context.getString(R.string.electricity))){
               if (activity is AllServicesSelectionActivity) {
                   activity.callFragment(RechargeFragment(), "Electricity",servicesList[position].featurecode,"")
               }
           }


           if(servicesList[position].name.equals(context.getString(R.string.gas))){
               if (activity is AllServicesSelectionActivity) {
                   activity.callFragment(RechargeFragment(), "Gas",servicesList[position].featurecode,"")
               }
           }


           if(servicesList[position].name.equals(context.getString(R.string.waterbill))){
               if (activity is AllServicesSelectionActivity) {
                   activity. callFragment(RechargeFragment(), "Water",servicesList[position].featurecode,"")
               }
           }

           if(servicesList[position].name.equals(context.getString(R.string.broadband))){
               if (activity is AllServicesSelectionActivity) {
                   activity.callFragment(RechargeFragment(), "Broadband",servicesList[position].featurecode,"")
               }
           }

           if(servicesList[position].name.equals(context.getString(R.string.emi))){
               if (activity is AllServicesSelectionActivity) {
                   activity.callFragment(RechargeFragment(), "EMI",servicesList[position].featurecode,"")
               }
           }

           if(servicesList[position].name.equals(context.getString(R.string.creditcard))){
               if (activity is AllServicesSelectionActivity) {
                   activity. callFragment(CreditCardDetailsFragment(), "CreditCard",servicesList[position].featurecode,"")
               }
           }

           if(servicesList[position].name.equals(context.getString(R.string.muncipal))){
               if (activity is AllServicesSelectionActivity) {
                   activity.callFragment(RechargeFragment(), "Municipality",servicesList[position].featurecode,"")
               }
           }


           val position = holder.adapterPosition
           if (position == RecyclerView.NO_POSITION) return@setOnClickListener
           val recyclerView = (holder.itemView.parent as RecyclerView)
           val layoutManager = recyclerView.layoutManager as LinearLayoutManager

           val lastVisiblePos = layoutManager.findLastVisibleItemPosition()-1
           if (position == lastVisiblePos) {
               val nextPos = position + 1
               if (nextPos < itemCount) {
                   recyclerView.smoothScrollToPosition(nextPos)
               }
           }

       }





    }



    override fun getItemCount(): Int {
      return  servicesList.size
    }





}