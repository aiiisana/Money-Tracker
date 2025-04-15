package com.fpis.money.views.activities.splash

import CardFragment
import com.fpis.money.databinding.ActivityMainBinding
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.fpis.money.R
import com.fpis.money.utils.database.AppDatabase
import com.fpis.money.views.activities.MainActivity
import com.fpis.money.views.fragments.add.AddFragment
import com.fpis.money.views.fragments.home.HomeFragment
import com.fpis.money.views.fragments.menu.MenuFragment
import com.fpis.money.views.fragments.records.RecordFragment


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 500)
    }
}