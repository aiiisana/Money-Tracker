package com.fpis.money.views.fragments.home.budgets

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fpis.money.R
import com.fpis.money.databinding.FragmentEditBudgetBinding
import com.fpis.money.models.Budget
import com.fpis.money.utils.ToastType
import com.fpis.money.utils.showCustomToast
import com.larswerkman.holocolorpicker.ColorPicker
import com.larswerkman.holocolorpicker.SaturationBar

class EditBudgetFragment : Fragment() {

    private var _binding: FragmentEditBudgetBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BudgetViewModel

    private var budgetId: String? = null
    private var budget: Budget? = null

    // track the selected color
    private var selectedColorHex = "#66FFA3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        budgetId = arguments?.getString(ARG_BUDGET_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(BudgetViewModel::class.java)

        // Load existing budget
        budgetId?.let { id ->
            viewModel.getBudgetById(id).observe(viewLifecycleOwner, Observer { loaded ->
                budget = loaded
                loaded?.let {
                    selectedColorHex = it.color
                    binding.etBudgetName.setText(it.category)
                    binding.etBudgetAmount.setText(String.format("%,.2f", it.amount))
                    updateColorCircle()
                }
            })
        }

        // Close & save
        binding.btnClose.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnSaveBudget.setOnClickListener { saveBudget() }

        // Color picker
        binding.colorCircle.setOnClickListener { showColorPickerDialog() }
    }

    private fun showColorPickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.card_color_picker, null)
        val picker  = dialogView.findViewById<ColorPicker>(R.id.color_picker)
        val sat     = dialogView.findViewById<SaturationBar>(R.id.saturation_bar)
        val hexTv   = dialogView.findViewById<TextView>(R.id.tv_hex_color)
        val cancel  = dialogView.findViewById<Button>(R.id.btn_cancel)
        val select  = dialogView.findViewById<Button>(R.id.btn_select)

        picker.addSaturationBar(sat)
        try { picker.color = Color.parseColor(selectedColorHex) } catch (_:Exception){}

        picker.onColorChangedListener = ColorPicker.OnColorChangedListener { c ->
            val h = String.format("#%06X", 0xFFFFFF and c)
            hexTv.text = h
            hexTv.setBackgroundColor(c)
        }

        val dlg = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        cancel.setOnClickListener { dlg.dismiss() }
        select.setOnClickListener {
            selectedColorHex = hexTv.text.toString()
            updateColorCircle()
            dlg.dismiss()
        }

        dlg.show()
    }

    private fun updateColorCircle() {
        binding.colorCircle.background.setTint(Color.parseColor(selectedColorHex))
    }

    private fun saveBudget() {
        val name   = binding.etBudgetName.text.toString().trim().ifEmpty { budget?.category ?: "" }
        val amount = binding.etBudgetAmount.text.toString()
            .replace("[^0-9.]".toRegex(), "")
            .toDoubleOrNull() ?: budget?.amount ?: 0.0

        budget?.let {
            val updated = it.copy(
                category = name,
                amount   = amount,
                color    = selectedColorHex
            )
            viewModel.updateBudget(updated)
            showCustomToast(requireContext(), "Budget updated", ToastType.SUCCESS)
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_BUDGET_ID = "budget_id"
        @JvmStatic fun newInstance(budgetId: String) =
            EditBudgetFragment().apply {
                arguments = Bundle().apply { putString(ARG_BUDGET_ID, budgetId) }
            }
    }
}
