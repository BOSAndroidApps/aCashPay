package com.bos.payment.appName.network

import com.bos.payment.appName.constant.ConstantClass
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

     private const val BASE_URL: String = "https://payout.boscenter.in/" //  with UPI live url

    // private const val BASE_GETURL_RECHARGE: String = "https://api.boscenter.in/" // for recharge all functionality test & live url

    /*Live Api*/
    private const val BASE_GETURL_RECHARGE : String = "https://api.aopay.in/" // Annu just commented 7/08/2025 for guru demo

     /*Demo Api*/
   // private const val BASE_URLAPI: String = "https://api.boscenter.in/"
     private const val BASE_URLAPI: String = "http://192.168.1.107/"

    /* UAT Api Fr Login*/
    //private const val BASE_GETURL: String = "https://bosapi.bos.center"
    private const val BASE_GETURL: String = "http://192.168.1.107/"
    const val IMAGE_BASE_URL: String = "https://bosapi.bos.center"

    /* Live Api Fr Login*/
   /* private const val BASE_GETURL: String = "https://bosapi.businessonlinesolution.in"
    const val IMAGE_BASE_URL: String = "https://bosapi.businessonlinesolution.in"*/

    /*Demo Api*/
   private const val TRAVEL_URL: String = "https://travel.bospay.co.in/"

    /*Live Api*/
   // private const val TRAVEL_URL: String = "https://travel.bospay.in/"

    private const val PAYOUT_URL: String = "https://payout.aopay.in/"

    const val PAN_BASE_URL = "https://api.aopay.in/"


    private var retrofit: Retrofit? = null
    private var retrofit2: Retrofit? = null


    fun getService(): ApiInterface? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(ConstantClass.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(ApiInterface::class.java)
    }


    fun getService2(): ApiInterface? {
        if (retrofit2 == null) {
            retrofit2 = Retrofit.Builder()
                .baseUrl(ConstantClass.BASE_URL_LOGIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit2!!.create(ApiInterface::class.java)
    }


    private fun getInstance(): Retrofit {
        // Create OkHttpClient with 1-minute timeout settings
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // Connection timeout
            .readTimeout(60, TimeUnit.SECONDS)    // Read timeout
            .writeTimeout(60, TimeUnit.SECONDS)   // Write timeout
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }


    private fun getInstanceData(): Retrofit {
        // Create OkHttpClient with 1-minute timeout settings
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // Connection timeout
            .readTimeout(60, TimeUnit.SECONDS)    // Read timeout
            .writeTimeout(60, TimeUnit.SECONDS)   // Write timeout
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URLAPI)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }


    private fun getAllInstance(): Retrofit {
        // Create OkHttpClient with 1-minute timeout settings
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // Connection timeout
            .readTimeout(60, TimeUnit.SECONDS)    // Read timeout
            .writeTimeout(60, TimeUnit.SECONDS)   // Write timeout
            .build()

        // Build Retrofit instance with the custom OkHttpClient
        return Retrofit.Builder()
            .baseUrl(BASE_GETURL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }


    private fun getAllTravelInstance(): Retrofit {
        // Create OkHttpClient with 1-minute timeout settings
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(RetryOn204Interceptor(maxRetry = 2))
            .connectTimeout(60, TimeUnit.SECONDS) // Connection timeout
            .readTimeout(60, TimeUnit.SECONDS)    // Read timeout
            .writeTimeout(60, TimeUnit.SECONDS)   // Write timeout
            .build()

        // Build Retrofit instance with the custom OkHttpClient
        return Retrofit.Builder()
            .baseUrl(TRAVEL_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }


    private fun getRechargeInstance(): Retrofit {
        // Create OkHttpClient with 1-minute timeout settings
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // Connection timeout
            .readTimeout(60, TimeUnit.SECONDS)    // Read timeout
            .writeTimeout(60, TimeUnit.SECONDS)   // Write timeout
            .build()

        // Build Retrofit instance with the custom OkHttpClient
        return Retrofit.Builder()
            .baseUrl(BASE_GETURL_RECHARGE)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }


    private fun getAllPayoutInstance(): Retrofit {
        // Create OkHttpClient with 1-minute timeout settings
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // Connection timeout
            .readTimeout(60, TimeUnit.SECONDS)    // Read timeout
            .writeTimeout(60, TimeUnit.SECONDS)   // Write timeout
            .build()

        // Build Retrofit instance with the custom OkHttpClient
        return Retrofit.Builder()
            .baseUrl(PAYOUT_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }


    private fun getAllInstancePAN(): Retrofit{
        // Create OkHttpClient with 1-minute timeout settings
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // Connection timeout
            .readTimeout(60, TimeUnit.SECONDS)    // Read timeout
            .writeTimeout(60, TimeUnit.SECONDS)   // Write timeout
            .build()

        // Build Retrofit instance with the custom OkHttpClient
        return Retrofit.Builder()
            .baseUrl(PAN_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }


    val apiInterface: ApiInterface = getInstance().create(ApiInterface::class.java)
    val apiRechargeInterface: ApiInterface = getRechargeInstance().create(ApiInterface::class.java)
    val apiAllInterface: ApiInterface = getAllInstance().create(ApiInterface::class.java)
    val apiBusAddRequestlAPI: ApiInterface = getAllInstance().create(ApiInterface::class.java)
    val apiAllAPIService: ApiInterface = getInstanceData().create(ApiInterface::class.java)
    val apiAllTravelAPI: TravelInterface = getAllTravelInstance().create(TravelInterface::class.java)
    val apiAllPayoutAPI: ApiInterface = getAllPayoutInstance().create(ApiInterface::class.java)
    val apiInterfacePAN: TravelInterface = getAllInstancePAN().create(TravelInterface::class.java)

}