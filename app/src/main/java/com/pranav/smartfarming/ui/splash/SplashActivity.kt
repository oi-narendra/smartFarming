package com.pranav.smartfarming.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.pranav.smartfarming.R
import com.pranav.smartfarming.ui.login.LoginActivity
import com.pranav.smartfarming.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null) {
            Handler(Looper.myLooper()!!).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, SPLASH_DELAY)
        } else {
            Handler(Looper.myLooper()!!).postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }, SPLASH_DELAY)
        }
    }

    companion object {
        const val SPLASH_DELAY = 2000L
    }
}