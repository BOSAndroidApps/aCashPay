package com.bos.payment.appName.utils

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.bos.payment.appName.R
import com.bos.payment.appName.data.model.justpedashboard.RetailerWiseServicesDataItem
import com.bos.payment.appName.data.model.recharge.operator.Data
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.gson.internal.bind.ArrayTypeAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random

object Constants {

    var agentTypeAdapter: ArrayTypeAdapter<String>? = null

    val FileName="dashboardlogs/aopaytravel.txt"

    var FINANCESERVICES = "FinanceServices"
    var BILLRECHARGE = "BillRecharge"
    var TRAVEL = "Travel"

    var RegistrationId = "RegistrationId"
    var AdminCode = "AdminCode"
    var MerchantId = "MerchantId"
    var mailid = "mailid"
    var applicationtype = "applicationtype"
    var retailerName = "fullname"
    var MerchantList = "MerchantList"
    var Merchant = "Merchant"
    var imageUrl = "https://letsenhance.io/static/73136da51c245e80edc6ccfe44888a99/1015f/MainBefore.jpg"
    var operatorImage = "operatorImage"
    var IS_LOGIN = "IS_LOGIN"
    var loginId = "loginId"
    var loginPassword = "loginPassword"
    var AgentName = "AgentName"
    var AgentType = "AgentType"
    var WalletBalance = "WalletBalance"
    var ActiveStatus = "ActiveStatus"
    var Status = "Status"
    var Password = "Password"
//    var CompanyCode = "cmp1045"
    var ReferenceId = "ReferenceId"
    var ReferenceType = "ReferenceType"
    var PanCardNo = "PanCardNo"
    var FirstName = "FirstName"
    var LastName = "LastName"
    var DOB = "DOB"
    var IS_FIRST_LAUNCH = true
    var fingerPrintAction = false
    var AlternateMobileNumber = "AlternateMobileNumber"
    var PermanentAddress = "PermanentAddress"
    var BusinessType = "BusinessType"
    var OfficeAddress = "OfficeAddress"
    var State = "State"
    var District = "District"
    var City = "City"
    var PinCode = "PinCode"
    var stateRespDMT = "stateRespDMT"
    var PinCodeDMT = "PinCodeDMT"
    var dobOwnerDMT = "dobOwnerDMT"
    var addressOwnerDMT = "addressOwnerDMT"
    var AadharCardNo = "AadharCardNo"
    var GSTNo = "GSTNo"
    var Website = "Website"
    var AccountHolderName = "AccountHolderName"
    var AccountNumber = "AccountNumber"
    var BankName = "BankName"
    var BranchName = "BranchName"
    var AccountType = "AccountType"
    var IFSCCODE = "IFSCCODE"
    var PanCardPic = "PanCardPic"
    var AadharFront = "AadharFront"
    var AadharBack = "AadharBack"
    var Photo = "Photo"
    var EncryptionKey = "EncryptionKey"
    var billAmount = "billAmount"
    var billnetamount = "billnetamount"
    var dueDate = "dueDate"
    var maxBillAmount = "maxBillAmount"
    var cellNumber = "cellNumber"
    var userName = "userName"
    var jio = "jio"
    var bsnl = "bsnl"
    var idea = "idea"
    var airtel = "airtel"
    var vodafone = "vodafone"
    var noImage = "noImage"
    var beneficiaryId = "beneficiaryId"
    var CompanyCode = "companyCode"
    var CompanyLogo = "CompanyLogo"
    var CompanyName = "CompanyName"
    var MobileNumber = "MobileNumber"
    var EmailID = "EmailID"
    var BillAPI = "F0116"
    var FatTagAPI = "F0118"
    var PayoutAPI = "F0112"
    var mobileRechareAPI = "F0117"
    var clientStateResponse = "clientStateResponse"
    var acceptPayment: Boolean = false
    var acceptPartPay: Boolean = false
    var isUpdate:Boolean = false
    var RechargeAPI_Status = "RechargeAPI_Status"
    var RechargeAPI_2_Status = "RechargeAPI_2_Status"
    var MoneyTransferAPI_Status = "MoneyTransferAPI_Status"
    var MoneyTransferAPI_2_Status = "MoneyTransferAPI_2_Status"
    var Payout_API_2_Status = "Payout_API_2_Status"
    var Payout_API_Status = "Payout_API_Status"
    var Payin_API_Status = "Payin_API_Status"
    var Payin_API_2_Status = "Payin_API_2_Status"
    var Fastag_API_Status = "Fastag_API_Status"
    var PANCardAPI_Status = "PANCardAPI_Status"
    var CreditCardAPI_Status = "CreditCardAPI_Status"
    var AEPS_API_Status = "AEPS_API_Status"
    var APIName = "APIName"
    var AllAPIName = "AllAPIName"
    var OperatorCategory = "OperatorCategory"
    var opCategory = "opCategory"
    var circleName = "circleName"
    val OperatorId = 0
    var rechargeAmount = "rechargeAmount"
    var beneId = "beneId"
    var bankAccount = "bankAccount"
    var ifscCode = "ifscCode"
    var bankOwnerName = "bankOwnerName"
    var mobileOperatorName = "mobileOperatorName"
    var mobileCircleName = "mobileCircleName"
    var uploadImage = ""
    var retailerCommissionWithoutTDS = "retailerCommissionWithoutTDS"
    var retailerCommission = "retailerCommission"
    var adminCommissionWithoutTDS = "adminCommissionWithoutTDS"
    var mDistributerCommissionWithoutTDS = "mDistributerCommissionWithoutTDS"
    var customerCommissionWithoutTDS = "customerCommissionWithoutTDS"
    var tds = "tds"
    var gst = "gst"
    var totalTransaction = "totalTransaction"
    var actualRechargeAmount = "rechargeplanamount"
    var actualRetailerCommissionAmount = "retaileractualcommissionamount"
    var serviceCharge = "serviceCharge"
    var serviceChargewithgst = "serviceChargewithgst"
    var actualbusticketamt = "actualbusticketamt"
    var serviceChargeGST = "serviceChargeGst"
    var toBeCreditedAmt = "tobecreditedamt"
    var serviceChargeWithGST = "serviceChargeWithGST"
    var actualAmountServiceChargeWithGST = "actualAmountServiceChargeWithGST"
    var finalCommission = "finalCommission"
    var finalSubDisCommission = "finalSubDisCommission"
    var finalMDisCommission = "finalMDisCommission"
    var imagePath = ""
    var deviceIPAddress = "deviceIPAddress"
    var mDis_CommissionType = "mDis_CommissionType"
    var admin_CommissionType = "admin_CommissionType"
    var retailer_CommissionType = "retailer_CommissionType"
    var customer_CommissionType = "customer_CommissionType"
    var serviceType = "serviceType"
    var dIS_RegistrationId = "dIS_RegistrationId"
    var mD_RegistrationId = "mD_RegistrationId"
    var retailerMainVirtualAmount = "retailerMainVirtualAmount"
    var retailerCreditLimit = "retailerCreditLimit"
    var retailerAvailCreditLimit = "retailerCreditLimit"
    var retailerHoldAmt = "retailerHoldAmt"
    var retailerActualAvailAmount = "retailerActualAvailAmount"
    var availCreditAmount = "availCreditAmount"
    var merchantBalance = "merchantBalance"

    //View Bill
    var posPaidBillAmount = "posPaidBillAmount"
    var posPaidBillNetAmount = "posPaidBillNetAmount"
    var posPaidDueDate = "posPaidDueDate"
    var posPaidBillDate = "posPaidBillDate"
    var posPaidCellNumber = "posPaidCellNumber"
    var posPaidUserName = "posPaidUserName"
    var fromDesignationName = "fromDesignationName"
    var fromDesignationId = "fromDesignationId"
    var toDesignationName = "toDesignationName"
    var toDesignationId = "toDesignationId"
    var dateAndTime = "dateAndTime"
    var searchKey = "searchKey"
    var busKey = "busKey"
    var boarding_Id = "boarding_Id"
    var dropping_Id = "dropping_Id"
    var travelCompanyName = "travelCompanyName"
    var busName = "busName"
    var travelAmount = "travelAmount"
    var arrivalTime = "arrivalTime"
    var travelTime = "travelTime"
    var seatMap_Key = "seatMap_Key"
    var seatNumber = "seatNumber"
    var booking_RefNo = "booking_RefNo"
    var ForPayoutBookingRefId = "Payout_booking_RefNo"
    var requestId = "requestId"
    var boardingPoint = "boardingPoint"
    var droppingPoint = "droppingPoint"
    var FlightSearchKey = "search_Key"
    var FlightKey = "flight_key"
    var AirTotalTicketPrice = "flight_ticket_price"
    var AirTotalOperatorPrice = "flight_ticket_operator"
    var AirTotalBasicPrice = "flight_ticket_Basic"
    var BookingRefNo = "bookingRefNo"

    //.................... Bank deatils data for side navigation QR Process .....................................................
    var SettlementAccountName = "settlementAccountName"
    var SettlementAccountNumber = "settlementAccountNumber"
    var SettlementAccountIfsc = "settlementAccountIfsc"
    var SellerIdentifier = "sellerIdentifier"
    var BankMobileNumber = "mobileNumber"
    var EmailId = "emailId"
    var BankAccountType = "accountType"
    var CreatedBy = "createdBy"
    var ISQRCodeGenerated = "is_QRCodeGenerated"
    var VPAid = "vpaid"
    var ISQRCodeActivate = "is_QRCodeActivate"
    var StaticQR = "staticQR"
   var TicketStatus =  "Ticket Status"
   var AdminBank =  "Adminbank"
   var BranchNamee =  "branchname"
   var CashDeposit =  "Cash Deposit"
   var DateSelectionHint =  "Select Date"
   var Active =  "Active"
   var Inactive =  "Inactive"

    lateinit var  dialog : Dialog

    val KEY_192 = "your-24-byte-key-here!".toByteArray(Charsets.UTF_8) // 24 bytes
    val IV_192 = "8-byte-iv".toByteArray(Charsets.UTF_8) // 8 bytes

    var posPaidAcceptPayment: Boolean = false
    var posPaidAcceptPartPay: Boolean = false

    var FinanceCard: Boolean = false
    var BillRechargeCard: Boolean = false
    var TravelCard: Boolean = false

    var TYPE = "RTE"
    const val PAN_VERIFICATION_REGISTRATION_ID = "AOP-554"
    var AadharTransactionIdNo : String = ""
    var AadharVerified : String = ""

    var compressedBitmap: Bitmap? = null
    const val OWNER_PAN_CARD: Int = 1
    const val OWNER_AADHAR_FRONT: Int = 2
    const val OWNER_AADHAR_BACK: Int = 3
    const val OWNER_PHOTO: Int = 4
    const val OTHER_PHOTO: Int = 5
    const val UPLOAD_PHOTO: Int = 6
    const val UPLOAD_SLIP: Int = 7


    var dropDownValues: ArrayList<Data>? = null
    var genderAdapter: ArrayAdapter<String>? = null
    var state_adapter: ArrayAdapter<String>? = null
    var getAllOperatorAdapter: ArrayAdapter<String>? = null
    var getAllCircleAdapter: ArrayAdapter<String>? = null
    var getAllOperatorAdapterValue: ArrayAdapter<String>? = null
    var getAllGasOperatorAdapter: ArrayAdapter<String>? = null
    var getAllDTHOperatorAdapter: ArrayAdapter<String>? = null
    var getAllInsuranceOperatorAdapter: ArrayAdapter<String>? = null
    var getAllBankListAdapter: ArrayAdapter<String>? = null
    var getAllBusListAdapter: ArrayAdapter<String>? = null
    var getAllBoardingPointAdapter: ArrayAdapter<String>? = null
    var getAllDroppingPointAdapter: ArrayAdapter<String>? = null


    var operatorName: ArrayList<String>? = null
    var operatorNameDth: ArrayList<String>? = null
    var productIdList: ArrayList<String>? = null
    var dthName: ArrayList<String>? = null
    var emiNo: ArrayList<String>? = null
    var gasName: ArrayList<String>? = null
    var insuranceName: ArrayList<String>? = null
    var broadBandName: ArrayList<String>? = null
    var electricityName: ArrayList<String>? = null
    var waterName: ArrayList<String>? = null
    var prepaidName: ArrayList<String>? = null
    var landLineName: ArrayList<String>? = null
    var municipalityName: ArrayList<String>? = null
    var fastTagName: ArrayList<String>? = null
    var stateName: ArrayList<String>? = null
    var bankListName: ArrayList<String>? = arrayListOf()
    var bankListId: ArrayList<String>? = null
    var busListName: ArrayList<String>? = arrayListOf()
    var toLocationName: ArrayList<String>? = arrayListOf()


    var boardingPointName: ArrayList<String>? = null
    var droppingPointName: ArrayList<String>? = null
    var titleName: ArrayList<String>? = null
    var genderName: ArrayList<String>? = null

    var merchantIdList: MutableList<String>? = null


    var stateNameMap: HashMap<String, Int>? = null
    var operatorNameMap: HashMap<String, Int>? = null
    var dthNameMap: HashMap<String, Int>? = null
    var emiNoMap: HashMap<String, Int>? = null
    var gasNameMap: HashMap<String, Int>? = null
    var insuranceNameMap: HashMap<String, Int>? = null
    var broadBandNameMap: HashMap<String, Int>? = null
    var electricityNameMap: HashMap<String, Int>? = null
    var waterNameMap: HashMap<String, Int>? = null
    var prepaidNameMap: HashMap<String, Int>? = null
    var landLineNameMap: HashMap<String, Int>? = null
    var municipalityNameMap: HashMap<String, Int>? = null
    var fastTagNameMap: HashMap<String, Int>? = null
    var bankListNameMap: HashMap<String, Int>? = null
    var busListNameMap : HashMap<String, Int>? = hashMapOf()
    var toLocationNameMap: HashMap<String, Int>? = hashMapOf()
//    var beneficiaryIdMap: HashMap<String, Int>? = null


    var operatorNameMapForGettingOperatorName: HashMap<Int, String>? = null
    var dthNameMapForGettingDthName: HashMap<Int, String>? = null
    var emiNoMapForGettingEmiNo: HashMap<Int, String>? = null
    var gasNameMapForGettingGasName: HashMap<Int, String>? = null
    var insuranceNameMapForGettingInsuranceName: HashMap<Int, String>? = null
    var broadBandNameMapForGettingBroadBandName: HashMap<Int, String>? = null
    var electricityNameMapForGettingElectricityName: HashMap<Int, String>? = null
    var waterNameMapForGettingWaterName: HashMap<Int, String>? = null
    var prepaidNameMapForGettingPrepaidName: HashMap<Int, String>? = null
    var landLineNameMapForGettingLandLineName: HashMap<Int, String>? = null
    var municipalityNameMapForGettingMunicipalityName: HashMap<Int, String>? = null
    var fastTagNameMapForGettingFastTagName: HashMap<Int, String>? = null
    var bankListNameMapForGettingbankListName: HashMap<Int, String>? = null
    var busListNameMapForGettingBusListName: HashMap<Int, String>? = null
    var toLocationNameMapForGettingToLocationName: HashMap<Int, String>? = null


    var RETAILERALLSERVICES: List<RetailerWiseServicesDataItem?>? = listOf()



    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

    }



    fun uploadDataOnFirebaseConsole(data:String, collectionPath:String,context: Context){
        val context = context
        val db = Firebase.firestore

        val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault())
        val currentDateTime = sdf.format(Date())


        // Create a new user with a first and last name
        val logData = hashMapOf(
            "filename" to "aopaytravel.txt",
            "content" to data,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection(collectionPath)
            .document(currentDateTime)
            .set(logData)
            .addOnSuccessListener {
                //Toast.makeText(context, "Log saved in Firestore", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.d("Error", " $e.message")
                Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }



    fun getRetailerAllServices():List<RetailerWiseServicesDataItem>{
        var servicesList : MutableList<RetailerWiseServicesDataItem> = mutableListOf()
        servicesList.add(RetailerWiseServicesDataItem("y","F0115","DMT"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0116","Bill Payment")) // emi,munsipal
        servicesList.add(RetailerWiseServicesDataItem("y","F0118","FastTag Recharge"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0119","Pan Card"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0120","Aadhar"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0121","PG2"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0122","PG3"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0124","PaymentLink"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0125","Credit Card"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0126","StandardPaymentLink"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0127","UPIPaymentLink"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0128","DMTV1"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0129","DMTV2"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0130","WalletMoney"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0131","YesBankVPA"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0111","Payin"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0112","Payout"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0113","CIBIL"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0114","PG"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0132","PayinV1"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0133","BusApi"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0134","AirApi"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0135","CreditAlalytics"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0136","BasicPanDetailsApi"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0137","PanApi"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0138","DGLockerAadhaarValidationApi"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0139","BasicUpiValidation"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0140","RechargeV2"))
        servicesList.add(RetailerWiseServicesDataItem("y","F0141","AEPSV1Api")) // cashwithdraw,ministatmnt,balanaceenquiery

        return servicesList
    }


    fun maskWithEllipsis(account: String ?, visibleDigits: Int = 4): String {
        val digits = account!!.filter { it.isDigit() }
        if (digits.length <= visibleDigits) return digits
        return "xxxxxx " + digits.takeLast(visibleDigits) // or "…${digits.takeLast(visibleDigits)}"
    }


    fun scanForActivity(cont: Context?): FragmentActivity? {
        return when (cont) {
            null -> null
            is FragmentActivity -> cont
            is ContextWrapper -> scanForActivity(cont.baseContext)
            else -> null
        }
    }

    suspend fun downloadImageFromUrl(context: Context, imageUrl: String, fileName: String = "downloaded_image.jpg"): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection()
                connection.connect()

                val inputStream = connection.getInputStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)

                saveImageToDevice(context, bitmap, fileName)
                true // ✅ Success
            } catch (e: Exception) {
                e.printStackTrace()
                false // ❌ Failed
            }
        }
    }

    private fun saveImageToDevice(context: Context, bitmap: Bitmap, fileName: String) {
        val outputStream: OutputStream?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 and above
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyAppImages")
            }

            val imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            outputStream = context.contentResolver.openOutputStream(imageUri!!)
        } else {
            // Older Android versions
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
            val file = File(imagesDir, fileName)
            outputStream = FileOutputStream(file)
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream!!)
        outputStream?.flush()
        outputStream?.close()
    }


    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("MMM d, yyyy, h:mm a", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate).lowercase()  // to get "pm" instead of "PM"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentIsoDate(): String {
        return Instant.now().toString()
    }


    fun OpenPopUpForVeryfyOTP(context: Context){
        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.loader)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }

        dialog.setCanceledOnTouchOutside(false)

        dialog.show()

    }



    fun isValidPAN(pan: String): Boolean {
        val regex = Regex("[A-Z]{5}[0-9]{4}[A-Z]{1}")
        return regex.matches(pan)
    }


     fun generateReportFileName(filename:String): String {
        val sdf = SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss", Locale.getDefault())
        val timestamp = sdf.format(Date())
        return "$filename$timestamp.csv"   // or .xlsx
    }




    @Throws(java.lang.Exception::class)
    fun convertDate(input: String?): String {
        val inputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("dd MMM, EEE", Locale.ENGLISH)

        val date = inputFormat.parse(input)
        return outputFormat.format(date)
    }


    fun getExpiryMillis(endDate: String?): Long? {
        if (endDate.isNullOrEmpty()) return null
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            sdf.timeZone = TimeZone.getDefault()
            sdf.parse(endDate)?.time
        } catch (e: Exception) {
            null
        }
    }


    fun shouldStartTimer(expiryMillis: Long): Boolean {
        val now = System.currentTimeMillis()
        val diff = expiryMillis - now
        val hours48 = 48 * 60 * 60 * 1000L
        return diff in 1..hours48
    }


    fun formatRemainingTime(millis: Long): String {
        val totalSeconds = millis / 1000

        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return "Exp. in ${hours}h ${minutes}m ${seconds}s"
    }



    fun startExpiryTimer(endDate: String, onTick: (String) -> Unit, onExpire: () -> Unit): CountDownTimer? {

        val expiryMillis = getExpiryMillis(endDate) ?: return null
        val remainingMillis = expiryMillis - System.currentTimeMillis()

        if (!shouldStartTimer(expiryMillis)) return null

        return object : CountDownTimer(remainingMillis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                onTick(formatRemainingTime(millisUntilFinished))
            }

            override fun onFinish() {
                onExpire()
            }

        }.start()
    }



    fun formatDateTime(date: String?): String {
        if (date.isNullOrEmpty()) return "-"

        return try {
            val inputFormat = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss",
                Locale.US
            )

            val outputFormat = SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.US
            )

            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate!!)
                .lowercase() // am / pm in lowercase
        } catch (e: Exception) {
            "-"
        }
    }


    fun generateRequestId(): String {
        val sdf = SimpleDateFormat("ddHHmmss", Locale.getDefault())
        val datePart = sdf.format(Date())
        val randomPart = Random.nextInt(100, 999) // 3-digit random
        return "ClientRef$datePart$randomPart"
    }




}
