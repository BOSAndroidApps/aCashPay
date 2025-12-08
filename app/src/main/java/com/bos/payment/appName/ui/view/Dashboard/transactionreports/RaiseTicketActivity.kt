package com.bos.payment.appName.ui.view.Dashboard.transactionreports

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bos.payment.appName.R

import com.bos.payment.appName.constant.ConstantClass.saveImageToCache

import com.bos.payment.appName.data.model.transactionreportsmodel.CheckRaiseTicketExistReq
import com.bos.payment.appName.data.model.transactionreportsmodel.RaiseTicketReq
import com.bos.payment.appName.data.repository.GetAllAPIServiceRepository
import com.bos.payment.appName.data.viewModelFactory.GetAllApiServiceViewModelFactory
import com.bos.payment.appName.databinding.ActivityRaiseTicketBinding
import com.bos.payment.appName.databinding.ContactlistItemBinding
import com.bos.payment.appName.databinding.ImagelayoutforraiseticketBinding
import com.bos.payment.appName.network.RetrofitClient
import com.bos.payment.appName.ui.viewmodel.GetAllApiServiceViewModel
import com.bos.payment.appName.utils.ApiStatus
import com.bos.payment.appName.utils.Constants
import com.bos.payment.appName.utils.MStash
import com.bos.payment.appName.utils.Utils.runIfConnected
import com.coding.imagesliderwithdotindicatorviewpager2.adapters.ImageAdapter
import com.google.gson.Gson
import java.io.File
import java.util.jar.Manifest

class RaiseTicketActivity : AppCompatActivity() {
    lateinit var binding: ActivityRaiseTicketBinding
    var reportModeList: MutableList<String?> = arrayListOf()
    private val selectedUris = mutableListOf<Uri>()
    private lateinit var adapter: ImageAdapter
    private lateinit var getAllApiServiceViewModel: GetAllApiServiceViewModel
    private var mStash: MStash? = null

    companion object {
        var servicetype: String = ""
        var transactionNo: String = ""
        var summery: String = ""
        var RefID: String = ""
        var date: String = ""
        var time: String = ""
        var transferfrom: String = ""
        var transferto: String = ""
        var creditamount: String = ""
        var debitamount: String = ""
        var remarks: String = ""
    }


    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pickImages()
            } else {
                Toast.makeText(this, "Permission denied. Can't open gallery.", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    // Use GetMultipleContents which returns List<Uri>
    private val pickMultipleLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
            if (uris.isNullOrEmpty()) return@registerForActivityResult

            // Validation: allow only up to 3
            if (uris.size > 3) {
                Toast.makeText(
                    this,
                    "You can select maximum 3 photos. Taking first 3.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Clear previous selection if you want to replace; otherwise you can append and check combined size
            selectedUris.clear()

            // Add only first 3
            val toAdd = uris.take(3)
            selectedUris.addAll(toAdd)

            // update UI
            adapter = ImageAdapter(this, selectedUris)
            binding.imageviewliast.adapter = adapter
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRaiseTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllApiServiceViewModel = ViewModelProvider(
            this,
            GetAllApiServiceViewModelFactory(GetAllAPIServiceRepository(RetrofitClient.apiAllInterface))
        )[GetAllApiServiceViewModel::class.java]

        mStash = MStash.getInstance(this@RaiseTicketActivity)

        bindDataOnUi()
        setclicklistner()

    }


    fun bindDataOnUi() {
        binding.servicetype.text = servicetype
        binding.transactionNo.text = transactionNo
        summery =
            "A $servicetype transaction (Txn No: $transactionNo, Ref ID: $RefID) was made on $date at $time. The transaction was from $transferfrom to $transferto, with a credit of ₹$creditamount and a debit of ₹$debitamount. Remarks: $remarks."

        binding.summery.text = summery
        adapter = ImageAdapter(this, selectedUris)
        binding.imageviewliast.adapter = adapter
        setSpinnerData()
    }


    fun setclicklistner() {

        binding.back.setOnClickListener {
            finish()
        }


        binding.uploadfilecard.setOnClickListener {
            checkPermissionAndPick()
        }


        binding.resetcard.setOnClickListener {
            binding.resetcard.setCardBackgroundColor(resources.getColor(R.color.blue))
            binding.submitcard.setCardBackgroundColor(resources.getColor(R.color.grey))
            binding.submittxt.setTextColor(resources.getColor(R.color.blue))
            binding.resettxt.setTextColor(resources.getColor(R.color.white))
            selectedUris.clear()
            adapter = ImageAdapter(this, selectedUris)
            binding.imageviewliast.adapter = adapter
        }


        binding.submitcard.setOnClickListener {
            binding.resetcard.setCardBackgroundColor(resources.getColor(R.color.grey))
            binding.submitcard.setCardBackgroundColor(resources.getColor(R.color.blue))
            binding.submittxt.setTextColor(resources.getColor(R.color.white))
            binding.resettxt.setTextColor(resources.getColor(R.color.blue))

            val (isValid, errorMessage) = isValidForm(
                subject = binding.subjecttxt.text.toString().trim(),
                description = binding.descriptiontxt.text.toString().trim(),
                selectedUris
            )
            if (!isValid) {
                Toast.makeText(this@RaiseTicketActivity, errorMessage, Toast.LENGTH_SHORT).show()

            } else {
                hitApiForRaiseTicket()
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
        pickMultipleLauncher.launch("image/*")
    }

    fun setSpinnerData() {
        reportModeList.clear()
        reportModeList.add("Low")
        reportModeList.add("Medium")
        reportModeList.add("High")
        var adapter = ArrayAdapter(
            this@RaiseTicketActivity,
            android.R.layout.simple_spinner_dropdown_item,
            reportModeList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.priorityspinner.adapter = adapter
    }



    class ImageAdapter(var context: Context, private val items: List<Uri>) :
        RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ImagelayoutforraiseticketBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ImagelayoutforraiseticketBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.image.setImageURI(items[position])
        }

        override fun getItemCount(): Int = items.size
    }


    fun hitApiForRaiseTicket() {
        var userCode = mStash!!.getStringValue(Constants.RegistrationId, "").toString()
        var adminCode = mStash!!.getStringValue(Constants.AdminCode, "").toString()
        val imageFile1 = saveImageToCache(this, selectedUris[0], "image1.jpg")
        val imageFile2 = saveImageToCache(this, selectedUris[1], "image2.jpg")
        val imageFile3 = saveImageToCache(this, selectedUris[2], "image3.jpg")

        runIfConnected {
            val request = RaiseTicketReq(
                userCode = userCode,
                serviceCode = binding.servicetype.text.toString(),
                subject = binding.subjecttxt.text.toString().trim(),
                description = binding.descriptiontxt.text.toString().trim(),
                priority = binding.priorityspinner.selectedItem.toString().trim(),
                adminCode = adminCode,
                imagePath1 = "image1.jpg",
                imagePath2 = "image2.jpg",
                imagePath3 = "image3.jpg",
                transactionID = binding.transactionNo.text.toString().trim(),
                transactionSummary = binding.summery.text.toString().trim(),
                imageFile1 = imageFile1,
                imageFile2 = imageFile2,
                imageFile3 = imageFile3
            )

            Log.d("RaiseTicketReq", Gson().toJson(request))

            getAllApiServiceViewModel.uploadRaiseTicketReq(request).observe(this) { resource ->
                resource?.let {
                    when (it.apiStatus) {
                        ApiStatus.SUCCESS -> {
                            it.data?.let { users ->
                                users.body()?.let { response ->
                                    Log.d("RaiseTicketResp", Gson().toJson(response))
                                    if(Constants.dialog!=null && Constants.dialog.isShowing){
                                        Constants.dialog.dismiss()
                                    }
                                    if (response.isSuccess!!) {
                                        Toast.makeText(
                                            this,
                                            response.returnMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this,
                                            response.returnMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
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


    fun isValidForm(
        subject: String,
        description: String,
        selectedUris :MutableList<Uri>): Pair<Boolean, String?> {
        if (subject.isBlank()) return Pair(false, "Enter subject")

        if (description.isBlank()) return Pair(false, "Enter description")

        if(selectedUris.isNullOrEmpty())return Pair(false, "Upload files first")

        return Pair(true, null)
    }



}