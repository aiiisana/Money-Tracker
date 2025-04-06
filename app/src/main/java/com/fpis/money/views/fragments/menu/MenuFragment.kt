package com.fpis.money.views.fragments.menu

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

import com.fpis.money.R
import com.fpis.money.views.activities.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MenuFragment : Fragment() {

    private lateinit var privacyPolicyLayout: LinearLayout
    private lateinit var eulaLayout: LinearLayout
    private lateinit var rateUsLayout: LinearLayout
    private lateinit var supportLayout: LinearLayout
    private lateinit var restorePurchasesLayout: LinearLayout
    private lateinit var logOutLayout: LinearLayout

    // Currently selected menu item
    private var selectedMenuItem: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        val logOutLayout: LinearLayout = view.findViewById(R.id.log_out_layout)
        logOutLayout.setOnClickListener {
            logOut()
        }

        return view
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize menu item layouts
        privacyPolicyLayout = view.findViewById(R.id.privacy_policy_layout)
        eulaLayout = view.findViewById(R.id.eula_layout)
        rateUsLayout = view.findViewById(R.id.rate_us_layout)
        supportLayout = view.findViewById(R.id.support_layout)
        restorePurchasesLayout = view.findViewById(R.id.restore_purchases_layout)
        logOutLayout = view.findViewById(R.id.log_out_layout)

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
            logOutLayout
        )

        menuItems.forEach { menuItem ->
            menuItem.setOnClickListener {
                selectMenuItem(menuItem)
                handleMenuItemAction(menuItem)
            }
        }
    }

    private fun resetAllMenuItems() {
        // Reset all menu items to default state
        val menuItems = listOf(
            privacyPolicyLayout,
            eulaLayout,
            rateUsLayout,
            supportLayout,
            restorePurchasesLayout,
            logOutLayout
        )

        menuItems.forEach { menuItem ->
            resetMenuItemColors(menuItem)
        }

        // Clear selected menu item
        selectedMenuItem = null
    }

    private fun selectMenuItem(menuItem: LinearLayout) {
        // Reset previous selection if any
        selectedMenuItem?.let { resetMenuItemColors(it) }

        // Set new selection
        setMenuItemColors(menuItem, true)
        selectedMenuItem = menuItem
    }

    private fun resetMenuItemColors(menuItem: LinearLayout) {
        // Get references to the views inside the menu item
        val iconContainer = menuItem.findViewById<LinearLayout>(getIconContainerId(menuItem))
        val icon = menuItem.findViewById<ImageView>(getIconId(menuItem))
        val text = menuItem.findViewById<TextView>(getTextId(menuItem))
        val arrow = menuItem.findViewById<ImageView>(getArrowId(menuItem))

        // Reset background of icon container - keep it transparent
        iconContainer.setBackgroundResource(R.drawable.cr9bffffff0d)

        // Reset text color
        text.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))

        // Reset icon tint
        icon.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.white))

        // Reset arrow tint
        arrow.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.white))
    }

    private fun setMenuItemColors(menuItem: LinearLayout, isSelected: Boolean) {
        // Get references to the views inside the menu item
        val iconContainer = menuItem.findViewById<LinearLayout>(getIconContainerId(menuItem))
        val icon = menuItem.findViewById<ImageView>(getIconId(menuItem))
        val text = menuItem.findViewById<TextView>(getTextId(menuItem))
        val arrow = menuItem.findViewById<ImageView>(getArrowId(menuItem))

        if (isSelected) {
            // Set purple color for all elements without changing the background
            // The background should remain dark/transparent as in the image

            // Set purple text color - using exact #6A66FF color
            text.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_6a66ff))

            // Set purple icon tint
            icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_6a66ff))

            // Set purple arrow tint
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
            R.id.log_out_layout -> R.id.log_out_icon_container
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
            R.id.log_out_layout -> R.id.log_out_icon
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
            R.id.log_out_layout -> R.id.log_out_text
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
            R.id.log_out_layout -> R.id.log_out_arrow
            else -> 0
        }
    }

    private fun handleMenuItemAction(menuItem: LinearLayout) {
        // Implement actions for each menu item
        when (menuItem.id) {
            R.id.privacy_policy_layout -> {
                // Navigate to Privacy Policy screen or show dialog
            }
            R.id.eula_layout -> {
                // Navigate to EULA screen or show dialog
            }
            R.id.rate_us_layout -> {
                // Open app store for rating
            }
            R.id.support_layout -> {
                // Navigate to Support screen or open email client
            }
            R.id.restore_purchases_layout -> {
                // Restore purchases logic
            }
            R.id.log_out_layout -> {
                // Log out logic
            }
        }
    }
}