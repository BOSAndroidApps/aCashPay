package com.bos.payment.appName.ui.view.Dashboard.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bos.payment.appName.R
import com.bos.payment.appName.databinding.ActivityProfileUpdatedBinding
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash

class ProfileUpdated : AppCompatActivity() {

    private var mStash: MStash? = null
    lateinit var binding : ActivityProfileUpdatedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileUpdatedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mStash = MStash.getInstance(this)
        setdataonui()
        setOnClickListner()

    }

    fun setOnClickListner(){
        binding.back.setOnClickListener {
            finish()
        }
    }


    fun setdataonui(){

        binding.apply {
            imgProfile.setImageResource(R.drawable.ic_d_profile)
            tvFullName.text =  mStash!!.getStringValue(Constants.retailerName, "")
            tvMobileNo.text =  mStash!!.getStringValue(Constants.MobileNumber, "")
            profilerowlayout.apply {
                mailid.text = mStash!!.getStringValue(Constants.mailid, "")
                agencyname.text = mStash!!.getStringValue(Constants.AgentName, "")
                merchantcode.text = mStash!!.getStringValue(Constants.MerchantId, "")
                admincode.text = mStash!!.getStringValue(Constants.AdminCode, "")
                agenttype.text = mStash!!.getStringValue(Constants.AgentType, "")
                applicationtype.text = mStash!!.getStringValue(Constants.applicationtype, "")
                userid.text = mStash!!.getStringValue(Constants.RegistrationId, "")
            }
        }
    }


}