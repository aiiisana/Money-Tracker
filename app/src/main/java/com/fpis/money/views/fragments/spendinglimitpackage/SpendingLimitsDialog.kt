package com.fpis.money.views.fragments.spendinglimitpackage

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import com.fpis.money.models.Budget
import com.fpis.money.utils.ToastType
import com.fpis.money.utils.showCustomToast
import com.fpis.money.views.fragments.add.category.CategoryBottomSheet

class SpendingLimitsDialog : DialogFragment() {
    private lateinit var viewModel: SpendingLimitsViewModel
    private lateinit var adapter: SpendingLimitAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_spending_limits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(SpendingLimitsViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_spending_limits)
        adapter = SpendingLimitAdapter(
            onEditClick = { limit -> showEditLimitDialog(limit) },
            onDeleteClick = { limit -> showDeleteConfirmation(limit) }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.allSpendingLimits.observe(viewLifecycleOwner) { limits ->
            adapter.submitList(limits)
        }

        view.findViewById<View>(R.id.btn_close_limits).setOnClickListener { dismiss() }
        view.findViewById<View>(R.id.btn_add_limit).setOnClickListener {
            showCategorySelectionForNewLimit()
        }
        view.findViewById<Button>(R.id.btn_save_limits).setOnClickListener {
            viewModel.checkAndNotifyLimits()
            dismiss()
        }
    }

    private fun showCategorySelectionForNewLimit() {
        val bottomSheet = CategoryBottomSheet(false) { category ->
            showAddLimitDialog(category.name, category.iconRes, category.colorRes)
        }
        bottomSheet.show(parentFragmentManager, "CategoryBottomSheet")
    }

    private fun showAddLimitDialog(category: String, iconRes: Int, colorRes: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_limit, null)
        val limitAmountInput = dialogView.findViewById<EditText>(R.id.et_limit_amount)
        val thresholdInput = dialogView.findViewById<EditText>(R.id.et_notification_threshold)

        AlertDialog.Builder(requireContext())
            .setTitle("Set Limit for $category")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val limitAmount = limitAmountInput.text.toString().toDoubleOrNull() ?: 0.0
                val threshold = thresholdInput.text.toString().toIntOrNull() ?: 80

                if (limitAmount <= 0) {
                    showCustomToast(requireContext(), "Please enter a valid amount", ToastType.ERROR)
                    return@setPositiveButton
                }

                viewModel.addSpendingLimit(category, limitAmount, iconRes, colorRes, threshold)
                showCustomToast(requireContext(), "Limit added successfully", ToastType.SUCCESS)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditLimitDialog(limit: Budget) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_limit, null)
        val limitAmountInput = dialogView.findViewById<EditText>(R.id.et_limit_amount)
        val thresholdInput = dialogView.findViewById<EditText>(R.id.et_notification_threshold)

        limitAmountInput.setText(limit.amount.toString())
        thresholdInput.setText(limit.notificationThreshold.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Limit for ${limit.category}")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val limitAmount = limitAmountInput.text.toString().toDoubleOrNull() ?: 0.0
                val threshold = thresholdInput.text.toString().toIntOrNull() ?: 80

                if (limitAmount <= 0) {
                    showCustomToast(requireContext(), "Please enter a valid amount", ToastType.ERROR)
                    return@setPositiveButton
                }

                val updatedLimit = limit.copy(
                    amount = limitAmount,
                    notificationThreshold = threshold,
                    notificationSent = false
                )

                viewModel.updateSpendingLimit(updatedLimit)
                showCustomToast(requireContext(), "Limit updated successfully", ToastType.SUCCESS)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmation(limit: Budget) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Limit")
            .setMessage("Are you sure you want to delete the spending limit for ${limit.category}?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteSpendingLimit(limit)
                showCustomToast(requireContext(), "Limit deleted", ToastType.SUCCESS)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}