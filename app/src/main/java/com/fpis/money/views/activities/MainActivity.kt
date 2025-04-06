package com.fpis.money.views.activities

import com.fpis.money.databinding.ActivityMainBinding
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fpis.money.R
import com.fpis.money.utils.database.AppDatabase
import com.fpis.money.views.fragments.add.AddFragment
import com.fpis.money.views.fragments.cards.CardFragment
import com.fpis.money.views.fragments.home.HomeFragment
import com.fpis.money.views.fragments.menu.MenuFragment
import com.fpis.money.views.fragments.records.RecordFragment

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


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
}