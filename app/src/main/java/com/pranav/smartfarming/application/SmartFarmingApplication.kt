package com.pranav.smartfarming.application

import android.app.Application
import android.graphics.Typeface
import com.pranav.smartfarming.BuildConfig
import es.dmoral.toasty.Toasty
import timber.log.Timber

class SmartFarmingApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Toasty.Config.getInstance()
            .tintIcon(true)
            .setToastTypeface(Typeface.SANS_SERIF)
            .apply()
    }
}