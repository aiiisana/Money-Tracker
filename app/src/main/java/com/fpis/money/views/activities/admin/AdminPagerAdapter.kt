package com.fpis.money.views.activities.admin

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fpis.money.views.fragments.admin.AdminStatsFragment
import com.fpis.money.views.fragments.admin.AdminUsersFragment

class AdminPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> AdminUsersFragment()
            1 -> AdminStatsFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}