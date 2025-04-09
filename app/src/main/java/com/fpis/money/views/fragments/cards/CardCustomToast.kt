package com.fpis.money.views.fragments.cards


import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.fpis.money.R

enum class ToastType {
    SUCCESS, ERROR, INFO
}

fun showCustomToast(context: Context, message: String, type: ToastType = ToastType.SUCCESS) {
    val inflater = LayoutInflater.from(context)
    val layout = inflater.inflate(R.layout.card_custom_toast, null)

    // Set the message text
    val text = layout.findViewById<TextView>(R.id.toast_text)
    text.text = message

    // Set the icon based on toast type
    val icon = layout.findViewById<ImageView>(R.id.toast_icon)
    when (type) {
        ToastType.SUCCESS -> {
            layout.background = context.getDrawable(R.drawable.toast_bg_success)
            icon.setImageResource(R.drawable.ic_check_circle)
        }
        ToastType.ERROR -> {
            layout.background = context.getDrawable(R.drawable.toast_bg_error)
            icon.setImageResource(R.drawable.ic_error)
        }
        ToastType.INFO -> {
            layout.background = context.getDrawable(R.drawable.toast_bg_info)
            icon.setImageResource(R.drawable.ic_info)
        }
    }

    // Create and show the Toast
    val toast = Toast(context)
    toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 64)
    toast.duration = Toast.LENGTH_SHORT
    toast.view = layout
    toast.show()
}
