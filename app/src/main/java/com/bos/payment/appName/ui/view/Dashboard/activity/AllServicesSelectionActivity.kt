package com.bos.payment.appName.ui.view.Dashboard.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.justpaymodel.MoneyTransferServicesModel
import com.bos.payment.appName.databinding.ActivityAllServicesSelectionBinding
import com.bos.payment.appName.ui.adapter.MoneyTransferServicesAdapter
import com.bos.payment.appName.ui.view.Dashboard.rechargefragment.RechargeFragment
import com.bos.payment.appName.ui.view.travel.flightBooking.fragment.FlightMainFragment
import com.bos.payment.appName.utils.Constants.BILLRECHARGE
import com.bos.payment.appName.utils.Constants.FINANCESERVICES
import com.bos.payment.appName.utils.Constants.RETAILERALLSERVICES
import com.bos.payment.appName.utils.Constants.TRAVEL

class AllServicesSelectionActivity : AppCompatActivity() {
    lateinit var binding: ActivityAllServicesSelectionBinding
    var serviceslist : MutableList<MoneyTransferServicesModel> = mutableListOf()
    lateinit var moneyTransferServicesadapter : MoneyTransferServicesAdapter


    companion object{
        var checkType: String=""
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllServicesSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)



        if(checkType.equals(FINANCESERVICES)){
            setFinanceServices()
        }

        if(checkType.equals(BILLRECHARGE)){
            setBillMobileRecharge()
        }

        if(checkType.equals(TRAVEL)){
            setTravel()
        }

        setclicklistner()

    }


    fun setclicklistner(){

        binding.back.setOnClickListener {
            onBackPressed()
        }


    }



    fun addIfActive(icon: Int, name: String, featureCode: String, extra: String = "") {
      var  activeFeatureCodes = RETAILERALLSERVICES?.filter { it!!.activeYN.equals("Y", ignoreCase = true) }
            ?.mapNotNull { it!!.featureCode } ?: emptyList()

        if (activeFeatureCodes.contains(featureCode)) {
            serviceslist.add(MoneyTransferServicesModel(icon, name, featureCode, extra))
        }
    }



    fun  setFinanceServices(){
        serviceslist.clear()

        addIfActive(R.drawable.eminew, getString(R.string.emi), "F0116")
        addIfActive(R.drawable.creditcardpayment, getString(R.string.creditcard), "F0125")
        addIfActive(R.drawable.tax, getString(R.string.muncipal), "F0116")
        addIfActive(R.drawable.cashwithdraw, getString(R.string.cashwithdraw), "F0141")
        addIfActive(R.drawable.balanceenquirey, getString(R.string.balanceenquiry), "F0141")
        addIfActive(R.drawable.ministatement, getString(R.string.ministatement), "F0141")


       /*serviceslist.add(MoneyTransferServicesModel(R.drawable.eminew,getString(R.string.emi),"F0116",""))
        serviceslist.add(MoneyTransferServicesModel(R.drawable.creditcardpayment,getString(R.string.creditcard),"F0125",""))
        serviceslist.add(MoneyTransferServicesModel(R.drawable.tax,getString(R.string.muncipal),"F0116",""))
        serviceslist.add(MoneyTransferServicesModel(R.drawable.cashwithdraw,getString(R.string.cashwithdraw),"F0141",""))
        serviceslist.add(MoneyTransferServicesModel(R.drawable.balanceenquirey,getString(R.string.balanceenquiry),"F0141",""))
        serviceslist.add(MoneyTransferServicesModel(R.drawable.ministatement,getString(R.string.ministatement),"","F0141"))*/


       // binding.financialserviceslist.layoutManager = GridLayoutManager(this, 3)
        moneyTransferServicesadapter= MoneyTransferServicesAdapter(serviceslist,this@AllServicesSelectionActivity,this@AllServicesSelectionActivity)
        moneyTransferServicesadapter= MoneyTransferServicesAdapter(serviceslist,this@AllServicesSelectionActivity,this@AllServicesSelectionActivity)
        binding.financialserviceslist.adapter = moneyTransferServicesadapter
        moneyTransferServicesadapter.notifyDataSetChanged()

        callFragment(RechargeFragment(), "EMI","F0116","")

    }


    fun  setBillMobileRecharge(){
        serviceslist.clear()

        addIfActive(R.drawable.rechargenew,getString(R.string.recharge),"F0140")
        addIfActive(R.drawable.dthnew,getString(R.string.dth),"F0140")
        addIfActive(R.drawable.electricitynew,getString(R.string.electricity),"F0116")
        addIfActive(R.drawable.gasnew,getString(R.string.gas),"F0116")
        addIfActive(R.drawable.waterbill,getString(R.string.waterbill),"F0116")
        addIfActive(R.drawable.internet,getString(R.string.broadband),"F0116")
        addIfActive(R.drawable.postpaidnew,getString(R.string.postpaid),"F0116")


       /*serviceslist.add(MoneyTransferServicesModel(R.drawable.rechargenew,getString(R.string.recharge),"F0140",""))
        serviceslist.add(MoneyTransferServicesModel(R.drawable.postpaidnew,getString(R.string.postpaid),"F0116",""))
        serviceslist.add(MoneyTransferServicesModel(R.drawable.dthnew,getString(R.string.dth),"F0140",""))
        serviceslist.add(MoneyTransferServicesModel(R.drawable.electricitynew,getString(R.string.electricity),"F0116",""))
        serviceslist.add(MoneyTransferServicesModel(R.drawable.gasnew,getString(R.string.gas),"F0116",""))
        serviceslist.add(MoneyTransferServicesModel(R.drawable.waterbill,getString(R.string.waterbill),"F0116",""))
        serviceslist.add(MoneyTransferServicesModel(R.drawable.internet,getString(R.string.broadband),"F0116",""))*/

       // binding.financialserviceslist.layoutManager = GridLayoutManager(this, 3)
        moneyTransferServicesadapter= MoneyTransferServicesAdapter(serviceslist,this@AllServicesSelectionActivity,this@AllServicesSelectionActivity)

        moneyTransferServicesadapter= MoneyTransferServicesAdapter(serviceslist,this@AllServicesSelectionActivity,this@AllServicesSelectionActivity)
        binding.financialserviceslist.adapter = moneyTransferServicesadapter
        moneyTransferServicesadapter.notifyDataSetChanged()

        moneyTransferServicesadapter.selectionPosition  = 0
        callFragment(RechargeFragment(), "mobile","F0140","")

    }



    fun  setTravel(){
        serviceslist.clear()

        addIfActive(R.drawable.planenew,getString(R.string.flight),"F0134")
        addIfActive(R.drawable.busnew,getString(R.string.bus),"F0133")
        addIfActive(R.drawable.trainnew,getString(R.string.train),"F0140")

       /* serviceslist.add(MoneyTransferServicesModel(R.drawable.planenew,getString(R.string.flight),"F0134",""))
        serviceslist.add(MoneyTransferServicesModel(R.drawable.busnew,getString(R.string.bus),"F0133",""))
        serviceslist.add(MoneyTransferServicesModel(R.drawable.trainnew,getString(R.string.train),"",""))*/
       // binding.financialserviceslist.layoutManager = GridLayoutManager(this, 3)

        moneyTransferServicesadapter= MoneyTransferServicesAdapter(serviceslist,this@AllServicesSelectionActivity,this@AllServicesSelectionActivity)
        binding.financialserviceslist.adapter = moneyTransferServicesadapter
        moneyTransferServicesadapter.notifyDataSetChanged()

        moneyTransferServicesadapter.selectionPosition  = 0
        callFragment(FlightMainFragment(), "flight", "F0134","FlightMainFragment")

    }



     fun callFragment(fragment: Fragment, rechargeType: String,featureCode:String,tag: String) {
        val bundle = Bundle()
        bundle.putString("RechargeType", rechargeType)
        bundle.putString("FeatureCode", featureCode)
        fragment.arguments = bundle

        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment, fragment,tag)
        transaction.addToBackStack(tag)
        transaction.commit()
       // binding.financialserviceslist.visibility= View.GONE
        binding.fragment.visibility=View.VISIBLE
    }


    override fun onBackPressed() {
        finish()
        super.onBackPressed()

     /*   val fragmentManager = supportFragmentManager
        // Check if any fragment is in the back stack
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack() // Go back to the previous fragment
        }
        else {
            super.onBackPressed()
        }

        // Update visibility after back press
        fragmentManager.addOnBackStackChangedListener {
            if (fragmentManager.backStackEntryCount == 0) {
                binding.financialserviceslist.visibility = View.VISIBLE
                binding.fragment.visibility = View.GONE
            }
        }*/


    }



}