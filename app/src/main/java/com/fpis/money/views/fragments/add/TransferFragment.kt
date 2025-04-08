package com.fpis.money.views.fragments.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.fpis.money.R
import androidx.navigation.fragment.findNavController

class TransferFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transfer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val expenseOption = view.findViewById<LinearLayout>(R.id.expenseOption)
        val incomeOption = view.findViewById<LinearLayout>(R.id.incomeOption)

        expenseOption.setOnClickListener {
            navigateToAddFragment("expense")
        }

        incomeOption.setOnClickListener {
            navigateToAddFragment("income")
        }
    }


    private fun navigateToAddFragment(type: String) {
        val bundle = Bundle().apply {
            putString("transactionType", type)
        }
        val addFragment = AddFragment().apply {
            arguments = bundle
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, addFragment)
            .commit()  // без addToBackStack
    }
}