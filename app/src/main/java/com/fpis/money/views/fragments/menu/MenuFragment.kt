package com.fpis.money.views.fragments.menu

import advanced.lab.chatlibrary.ChatLauncher
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.newscompose.NewsActivity
import com.fpis.money.R

class MenuFragment : Fragment() {

    private lateinit var privacyPolicyLayout: LinearLayout
    private lateinit var eulaLayout: LinearLayout
    private lateinit var rateUsLayout: LinearLayout
    private lateinit var supportLayout: LinearLayout
    private lateinit var restorePurchasesLayout: LinearLayout
    private lateinit var profileLayout: LinearLayout

    private var selectedMenuItem: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonOpenNews = view.findViewById<LinearLayout>(R.id.news_layout)

        buttonOpenNews.setOnClickListener {
            val intent = Intent(activity, NewsActivity::class.java)
            startActivity(intent)
        }

        // Initialize menu item layouts
        privacyPolicyLayout = view.findViewById(R.id.privacy_policy_layout)
        eulaLayout = view.findViewById(R.id.eula_layout)
        rateUsLayout = view.findViewById(R.id.rate_us_layout)
        supportLayout = view.findViewById(R.id.support_layout)
        restorePurchasesLayout = view.findViewById(R.id.restore_purchases_layout)
        profileLayout = view.findViewById(R.id.profile_layout)

        // Set click listeners for all menu items
        setupMenuItemClickListeners()

        // Reset all menu items to default state
        resetAllMenuItems()
    }

    private fun setupMenuItemClickListeners() {
        val menuItems = listOf(
            privacyPolicyLayout,
            eulaLayout,
            rateUsLayout,
            supportLayout,
            restorePurchasesLayout,
            profileLayout
        )

        menuItems.forEach { menuItem ->
            menuItem.setOnClickListener {
                selectMenuItem(menuItem)
                handleMenuItemAction(menuItem)
            }
        }
    }

    private fun resetAllMenuItems() {
        val menuItems = listOf(
            privacyPolicyLayout,
            eulaLayout,
            rateUsLayout,
            supportLayout,
            restorePurchasesLayout,
            profileLayout
        )

        menuItems.forEach { resetMenuItemColors(it) }
        selectedMenuItem = null
    }

    private fun selectMenuItem(menuItem: LinearLayout) {
        selectedMenuItem?.let { resetMenuItemColors(it) }
        setMenuItemColors(menuItem, true)
        selectedMenuItem = menuItem
    }

    private fun resetMenuItemColors(menuItem: LinearLayout) {
        val iconContainer = menuItem.findViewById<LinearLayout>(getIconContainerId(menuItem))
        val icon = menuItem.findViewById<ImageView>(getIconId(menuItem))
        val text = menuItem.findViewById<TextView>(getTextId(menuItem))
        val arrow = menuItem.findViewById<ImageView>(getArrowId(menuItem))

        iconContainer.setBackgroundResource(R.drawable.cr9bffffff0d)
        text.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        icon.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.white))
        arrow.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.white))
    }

    private fun setMenuItemColors(menuItem: LinearLayout, isSelected: Boolean) {
        val iconContainer = menuItem.findViewById<LinearLayout>(getIconContainerId(menuItem))
        val icon = menuItem.findViewById<ImageView>(getIconId(menuItem))
        val text = menuItem.findViewById<TextView>(getTextId(menuItem))
        val arrow = menuItem.findViewById<ImageView>(getArrowId(menuItem))

        if (isSelected) {
            text.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_6a66ff))
            icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_6a66ff))
            arrow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_6a66ff))
        } else {
            resetMenuItemColors(menuItem)
        }
    }

    private fun getIconContainerId(menuItem: LinearLayout): Int {
        return when (menuItem.id) {
            R.id.privacy_policy_layout -> R.id.privacy_policy_icon_container
            R.id.eula_layout -> R.id.eula_icon_container
            R.id.rate_us_layout -> R.id.rate_us_icon_container
            R.id.support_layout -> R.id.support_icon_container
            R.id.restore_purchases_layout -> R.id.restore_purchases_icon_container
            R.id.profile_layout -> R.id.profile_icon_container
            else -> 0
        }
    }

    private fun getIconId(menuItem: LinearLayout): Int {
        return when (menuItem.id) {
            R.id.privacy_policy_layout -> R.id.privacy_policy_icon
            R.id.eula_layout -> R.id.eula_icon
            R.id.rate_us_layout -> R.id.rate_us_icon
            R.id.support_layout -> R.id.support_icon
            R.id.restore_purchases_layout -> R.id.restore_purchases_icon
            R.id.profile_layout -> R.id.log_out_icon
            else -> 0
        }
    }

    private fun getTextId(menuItem: LinearLayout): Int {
        return when (menuItem.id) {
            R.id.privacy_policy_layout -> R.id.privacy_policy_text
            R.id.eula_layout -> R.id.eula_text
            R.id.rate_us_layout -> R.id.rate_us_text
            R.id.support_layout -> R.id.support_text
            R.id.restore_purchases_layout -> R.id.restore_purchases_text
            R.id.profile_layout -> R.id.profile_text
            else -> 0
        }
    }

    private fun getArrowId(menuItem: LinearLayout): Int {
        return when (menuItem.id) {
            R.id.privacy_policy_layout -> R.id.privacy_policy_arrow
            R.id.eula_layout -> R.id.eula_arrow
            R.id.rate_us_layout -> R.id.rate_us_arrow
            R.id.support_layout -> R.id.support_arrow
            R.id.restore_purchases_layout -> R.id.restore_purchases_arrow
            R.id.profile_layout -> R.id.profile_arrow
            else -> 0
        }
    }

    private fun handleMenuItemAction(menuItem: LinearLayout) {
        when (menuItem.id) {
            R.id.privacy_policy_layout -> {
                // Навигация к Privacy Policy
            }
            R.id.eula_layout -> {
                // Навигация к EULA
            }
            R.id.rate_us_layout -> {
                // Навигация на Play Market
            }
            R.id.support_layout -> {
                ChatLauncher.start(requireContext())
            }
            R.id.restore_purchases_layout -> {
                // Логика восстановления покупок
            }
            R.id.profile_layout -> {
                seeProfile()
            }
        }
    }

    private fun seeProfile() {
        val intent = Intent(requireContext(), com.fpis.money.views.activities.profile.ProfileActivity::class.java)
        startActivity(intent)
    }
}