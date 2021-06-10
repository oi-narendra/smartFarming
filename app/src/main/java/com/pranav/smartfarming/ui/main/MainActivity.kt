package com.pranav.smartfarming.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.pranav.smartfarming.R
import com.pranav.smartfarming.databinding.ActivityMainBinding
import com.pranav.smartfarming.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    lateinit var activityMainBinding: ActivityMainBinding

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(activityMainBinding.root)

        val navView: NavigationView = findViewById(R.id.nav_view)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        drawerLayout = findViewById(R.id.main_drawer_layout)

        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.soilMonitorFragment,
                R.id.samplesFragment,
                R.id.cropsFragment,
                R.id.diseasePredictionFragment
            ), drawerLayout
        )


        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.getHeaderView(0).findViewById<TextView>(R.id.header_profile_name).text =
            mAuth.currentUser?.email?.substringBefore("@") ?: "-"
        navView.getHeaderView(0).findViewById<TextView>(R.id.header_profile_email).text =
            mAuth.currentUser?.email ?: "-"

        navView.setNavigationItemSelectedListener { menuItem ->
            Handler().postDelayed({
                when (menuItem.itemId) {
                    R.id.soilMonitorFragment -> {
                        if (navController.currentDestination?.id != R.id.soilMonitorFragment) navController.navigate(
                            R.id.soilMonitorFragment
                        )
                    }

                    R.id.logout -> {
                        mAuth.signOut()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()

                    }

                    R.id.samplesFragment -> {
                        if (navController.currentDestination?.id != R.id.samplesFragment) navController.navigate(
                            R.id.samplesFragment
                        )
                    }

                    R.id.weatherFragment -> {
                        if (navController.currentDestination?.id != R.id.weatherFragment) navController.navigate(
                            R.id.weatherFragment
                        )
                    }

                    R.id.cropsFragment -> {
                        if (navController.currentDestination?.id != R.id.cropsFragment) navController.navigate(
                            R.id.cropsFragment
                        )
                    }

                    R.id.diseasePredictionFragment -> {
                        if (navController.currentDestination?.id != R.id.diseasePredictionFragment) navController.navigate(
                            R.id.diseasePredictionFragment
                        )
                    }
                }
            }, 250)

            drawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}