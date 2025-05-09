package com.fpis.money.views.fragments.cards


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.fpis.money.R

class CardOption(
    context: Context,
    private val onEditClick: () -> Unit,
    private val onDeleteClick: () -> Unit
) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup_card)

        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.BOTTOM)
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }

        findViewById<TextView>(R.id.tv_edit_card).setOnClickListener {
            onEditClick()
            dismiss()
        }

        findViewById<TextView>(R.id.tv_delete_card).setOnClickListener {
            onDeleteClick()
            dismiss()
        }
    }
}
