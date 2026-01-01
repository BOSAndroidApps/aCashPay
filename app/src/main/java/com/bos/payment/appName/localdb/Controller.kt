package com.bos.payment.appName.localdb

import android.util.Log
import com.bos.payment.appName.localdb.AppLog.initialize
import com.bos.payment.appName.utils.Constants
import com.google.firebase.FirebaseApp
import com.mikepenz.iconics.Iconics
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome



class Controller : android.app.Application() {

    init {
        instance = this
    }


    companion object {
        private var instance: Controller? = null

        fun applicationContext() : Controller {
            return instance as Controller
        }
    }


    override fun onCreate() {
        super.onCreate()
        // Initialize global resources here (e.g., logging, DI, shared prefs)
        Log.d("MyApplication", "App Started")

        FirebaseApp.initializeApp(this)
        Iconics.init(this)
        Iconics.registerFont(FontAwesome)
        this.initialize()

    }

}