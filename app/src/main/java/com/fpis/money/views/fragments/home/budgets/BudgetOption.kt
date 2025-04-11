package com.fpis.money.views.fragments.home.budgets

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.fpis.money.R

class BudgetOption(
    context: Context,
    private val onEditClick: () -> Unit,
    private val onDeleteClick: () -> Unit
) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup_budget)

        // Set dialog window properties
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.BOTTOM)
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }

        // Set click listeners
        findViewById<TextView>(R.id.tv_edit_budget).setOnClickListener {
            onEditClick()
            dismiss()
        }

        findViewById<TextView>(R.id.tv_delete_budget).setOnClickListener {
            onDeleteClick()
            dismiss()
        }
    }
}
