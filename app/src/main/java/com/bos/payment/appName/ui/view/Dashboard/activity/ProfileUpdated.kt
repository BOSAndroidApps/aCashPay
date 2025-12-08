package com.bos.payment.appName.ui.view.Dashboard.activity

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bos.payment.appName.R
import com.bos.payment.appName.constant.ConstantClass
import com.bos.payment.appName.constant.ConstantClass.saveImageToCache
import com.bos.payment.appName.data.model.justpedashboard.ProfileReq
import com.bos.payment.appName.data.model.transactionreportsmodel.RaiseTicketReq
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.databinding.ActivityProfileUpdatedBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.view.Dashboard.transactionreports.RaiseTicketActivity.ImageAdapter
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson

class ProfileUpdated : AppCompatActivity() {

    private var mStash: MStash? = null
    lateinit var binding : ActivityProfileUpdatedBinding
    private var selectedImageUri: Uri? = null
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel


    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pickImages()
            } else {
                Toast.makeText(this, "Permission denied. Can't open gallery.", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileUpdatedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mStash = MStash.getInstance(this)
        getAllApiServiceViewModel = ViewModelProvider(this, GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface)))[GetAllApiServiceViewModel::class.java]

        setdataonui()
        setOnClickListner()
        GetProfile()

    }

    fun setOnClickListner(){
        binding.back.setOnClickListener {
            finish()
        }

        binding.cameraicon.setOnClickListener {
            checkPermissionAndPick()
        }

        binding.updateBtn.setOnClickListener{
            if(selectedImageUri!=null){
                UpdateProfile()
            } else{
            }
        }

    }

    private fun checkPermissionAndPick() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                pickImages()
            }

            shouldShowRequestPermissionRationale(permission) -> {
                Toast.makeText(this, "Storage permission is needed to pick images.", Toast.LENGTH_SHORT).show()
                permissionLauncher.launch(permission)
            }

            else -> {
                permissionLauncher.launch(permission)
            }
        }
    }

    private fun pickImages() {
        pickSingleImageLauncher.launch("image/*")
    }

    private val pickSingleImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                binding.updateBtn.setAlpha(1f)
                binding.updateBtn.isEnabled = true
                selectedImageUri = uri
                binding.imgProfile.setImageURI(uri)  // show selected image in ImageView
            } else {
                binding.updateBtn.setAlpha(0.4f)
                binding.updateBtn.isEnabled = false
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
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


    fun UpdateProfile(){
        var userCode = mStash!!.getStringValue(Constants.RegistrationId, "").toString()
        val imageFile1 = saveImageToCache(this, selectedImageUri!!, "image1.jpg")

        runIfConnected {
            val request = ProfileReq(
                UserId = userCode,
                TaskType = "UPDATE",
                profileImage = imageFile1,
            )

            Log.d("UpdateProfileReq", Gson().toJson(request))

            getAllApiServiceViewModel.profileReq(request).observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    Log.d("UpdateProfileResp", Gson().toJson(response))
                                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                                        Constants.dialog.dismiss()
                                    }
                                    if (response.isSuccess!!) {
                                        Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
                                        GetProfile()
                                    } else {
                                        Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
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

    fun GetProfile(){
        var userCode = mStash!!.getStringValue(Constants.RegistrationId, "").toString()

        runIfConnected {
            val request = ProfileReq(
                UserId = userCode,
                TaskType = "GET",
                profileImage = null,
            )

            Log.d("GetProfileReq", Gson().toJson(request))

            getAllApiServiceViewModel.profileReq(request).observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    Log.d("GetProfileResponse", Gson().toJson(response))
                                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                                        Constants.dialog.dismiss()
                                    }
                                    if (response.isSuccess!!) {
                                        binding.updateBtn.setAlpha(0.4f)
                                        binding.updateBtn.isEnabled= false
                                        selectedImageUri=null
                                        binding.imgProfile.setImageURI(null)
                                        var uri =  "${RetrofitClient.IMAGE_BASE_URL}${response.data!!.profileImage}".trim()
                                        Log.d("ProfileImage", uri)
                                        Glide.get(this).clearMemory()
                                        Thread {
                                            Glide.get(this).clearDiskCache()
                                        }.start()

                                        Glide.with(this).load(uri)  .placeholder(R.drawable.profile_image)  // shown while loading
                                            .error(R.drawable.profile_image)        // shown if loading failed
                                            .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(binding.imgProfile)

                                    }
                                    else {
                                        Toast.makeText(this, response.returnMessage, Toast.LENGTH_SHORT).show()
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


}