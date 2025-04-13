package com.fpis.money.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.fpis.money.R

object ToastUtils {
    fun showToast(context: Context, message: String) {
        val inflater = LayoutInflater.from(context)
        val layout: View = inflater.inflate(R.layout.custom_toast, null)

        val textView: TextView = layout.findViewById(R.id.toastText)
        textView.text = message

        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}