package com.bos.payment.appName.ui.view.supportmanagement

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.data.model.supportmanagement.AddCommentReq
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.databinding.ActivityChatLayoutBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.adapter.ChatCommentAdapter
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.google.gson.Gson
import kotlin.properties.Delegates

class ChatToAdminActivity : AppCompatActivity() {
    lateinit var binding : ActivityChatLayoutBinding
    private var mStash: MStash? = null
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    lateinit var adapter : ChatCommentAdapter
    lateinit  var userCode : String
    lateinit  var adminCode : String

    companion object {
       var commentId by Delegates.notNull<Int>()
       var checkForSendChat : String =""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         binding= ActivityChatLayoutBinding.inflate(layoutInflater)
         setContentView(binding.root)

        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]

        mStash = MStash.getInstance(this@ChatToAdminActivity)


        userCode = mStash!!.getStringValue(Constants.RegistrationId, "").toString()
        adminCode = mStash!!.getStringValue(Constants.AdminCode, "").toString()


        if(checkForSendChat.equals("closed" , ignoreCase = true))
        {
            binding.sendmsglayout.visibility= View.GONE
        }
        else{
            binding.sendmsglayout.visibility= View.VISIBLE
        }

        setclicklistner()
        hitApiForRaiseTicket(commentId)

    }

    fun setclicklistner(){

        binding.back.setOnClickListener {
            finish()
        }

        binding.resettxt.setOnClickListener {
            if(binding.subjecttxt.text.toString().isNullOrBlank()){

            }else{
                hitApiForSendComment()
            }
        }


    }


    fun hitApiForRaiseTicket(commentId: Int) {

            getAllApiServiceViewModel.sendTicketCommentListReq(commentId).observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    Log.d("commentrespo", Gson().toJson(response))
                                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                                        Constants.dialog.dismiss()
                                    }
                                    if (response.isSuccess!!) {
                                        var commentList = response.data!!.comments
                                        if(commentList!!.isNotEmpty()&& !commentList.isNullOrEmpty()){
                                            binding.nocommenttxt.visibility= View.GONE
                                            binding.chatlist.visibility= View.VISIBLE

                                            var filterCommentList = commentList.filter { !it!!.commentBy!!.contains("EMP",ignoreCase = true) }
                                            adapter = ChatCommentAdapter(this,filterCommentList,userCode)
                                            binding.chatlist.adapter= adapter
                                            adapter.notifyDataSetChanged()
                                        }
                                        else{
                                            binding.nocommenttxt.visibility= View.VISIBLE
                                            binding.chatlist.visibility= View.GONE
                                        }
                                    }
                                    else {
                                        Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
                                        binding.nocommenttxt.visibility= View.VISIBLE
                                        binding.chatlist.visibility= View.GONE
                                    }
                                }
                            }
                        }

                        ApiStatus.ERROR -> {
                            if(Constants.dialog!=null && Constants.dialog.isShowing){
                                Constants.dialog.dismiss()
                            }
                        }

                        ApiStatus.LOADING -> {
                            Constants.OpenPopUpForVeryfyOTP(this)
                        }

                    }
                }
            }

        }

    fun hitApiForSendComment(){

        var req = AddCommentReq(
            complaintID=commentId,
            commentBy = userCode,
            commentTo = adminCode,
            comment = binding.subjecttxt.text.toString().trim()
        )

        getAllApiServiceViewModel.sendTicketCommentListReq(req).observe(this) { resource ->
            resource?.let {
                when (it.apiStatus) {
                    ApiStatus.SUCCESS -> {
                        it.data?.let { users ->
                            users.body()?.let { response ->
                                Log.d("commentrespo", Gson().toJson(response))
                                if(response.isSuccess!!){
                                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                                        Constants.dialog.dismiss()
                                    }
                                    binding.subjecttxt.text.clear()
                                    hitApiForRaiseTicket(commentId)
                                } else{
                                    Toast.makeText(this,response.returnMessage,Toast.LENGTH_SHORT).show()
                                }

                            }
                        }
                    }

                    ApiStatus.ERROR -> {
                        if(Constants.dialog!=null && Constants.dialog.isShowing){
                            Constants.dialog.dismiss()
                        }
                    }

                    ApiStatus.LOADING -> {
                        Constants.OpenPopUpForVeryfyOTP(this)
                    }

                }
            }
        }
    }


    }


