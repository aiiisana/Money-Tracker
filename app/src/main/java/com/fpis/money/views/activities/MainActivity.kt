package com.fpis.money.views.activities

import CardFragment
import com.fpis.money.databinding.ActivityMainBinding
import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fpis.money.R
import com.fpis.money.utils.broadcast.WifiStateReceiver
import com.fpis.money.utils.database.AppDatabase
import com.fpis.money.views.fragments.add.AddFragment
import com.fpis.money.views.fragments.home.HomeFragment
import com.fpis.money.views.fragments.menu.MenuFragment
import com.fpis.money.views.fragments.records.RecordFragment
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val fragmentOne = MenuFragment()
    private val fragmentTwo = RecordFragment()
    private val fragmentThree = AddFragment()
    private val fragmentFour = CardFragment()
    private val fragmentFive = HomeFragment()

    private val fragmentManager = supportFragmentManager
    private var activeFragment: Fragment = fragmentFive

    private lateinit var db: AppDatabase

    private val wifiReceiver: WifiStateReceiver by inject()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
        registerReceiver(wifiReceiver, filter)

        db = AppDatabase.getDatabase(this)

        setupFragments()
        setupBottomNavigation()
    }

    private fun setupFragments() {
        fragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, fragmentFive, "5")
            add(R.id.fragment_container, fragmentFour, "4").hide(fragmentFour)
            add(R.id.fragment_container, fragmentThree, "3").hide(fragmentThree)
            add(R.id.fragment_container, fragmentTwo, "2").hide(fragmentTwo)
            add(R.id.fragment_container, fragmentOne, "1").hide(fragmentOne)
        }.commit()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    showFragment(fragmentFive)
                    true
                }
                R.id.nav_records -> {
                    showFragment(fragmentTwo)
                    true
                }
                R.id.nav_add -> {
                    showFragment(fragmentThree)
                    true
                }
                R.id.nav_cards -> {
                    showFragment(fragmentFour)
                    true
                }
                R.id.nav_menu -> {
                    showFragment(fragmentOne)
                    true
                }
                else -> false
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().apply {
            hide(activeFragment)
            show(fragment)
            commit()
        }
        activeFragment = fragment
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiReceiver)
    }
}