package com.fpis.money.views.fragments.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.fpis.money.R
import com.fpis.money.utils.ToastType
import com.fpis.money.utils.showCustomToast

class PremiumFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_premium, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.close_button).setOnClickListener {
            dismiss()
        }

        view.findViewById<TextView>(R.id.restore_button).setOnClickListener {
            showCustomToast(requireContext(), "Restoration logic coming soon with Play Store launch!", ToastType.INFO)
        }

        view.findViewById<Button>(R.id.subscribe_button).setOnClickListener {
            showCustomToast(requireContext(), "Yep! We are working on this, wait for us!", ToastType.INFO)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}