package com.fpis.money.views.activities.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fpis.money.R
import com.fpis.money.databinding.ActivityAdminBinding
import com.fpis.money.views.fragments.admin.AdminStatsFragment
import com.fpis.money.views.fragments.admin.AdminUsersFragment

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding

    private val usersFragment = AdminUsersFragment()
    private val statsFragment = AdminStatsFragment()
    private var activeFragment: Fragment = usersFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupFragments()
        setupNavigation()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Admin Panel"
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupFragments() {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, statsFragment, "stats").hide(statsFragment)
            add(R.id.fragment_container, usersFragment, "users")
        }.commit()
    }

    private fun setupNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.nav_users -> {
                    switchFragment(usersFragment)
                    true
                }
                R.id.nav_stats -> {
                    switchFragment(statsFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit()
        activeFragment = fragment
    }
}