package com.fpis.money.views.fragments.home.budgets

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fpis.money.R
import com.fpis.money.databinding.FragmentAddBudgetBinding
import com.fpis.money.models.Budget
import com.fpis.money.utils.ToastType
import com.fpis.money.utils.showCustomToast
import com.larswerkman.holocolorpicker.ColorPicker
import com.larswerkman.holocolorpicker.SaturationBar

class AddBudgetFragment : Fragment() {
    private var _binding: FragmentAddBudgetBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BudgetViewModel

    // track the selected color
    private var selectedColorHex = "#66FFA3" // default green

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(BudgetViewModel::class.java)

        // close & create
        binding.btnClose.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnCreateBudget.setOnClickListener { createBudget() }

        // ◀︎ Color circle
        updateColorCircle()
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

    private fun createBudget() {
        val name = binding.etBudgetName.text.toString().ifBlank { "Unnamed" }
        val amt  = binding.etBudgetAmount.text.toString()
            .replace("[^0-9.]".toRegex(), "")
            .toDoubleOrNull() ?: 0.0

        viewModel.addBudget(
            Budget(category = name, amount = amt, spent = 0.0, color = selectedColorHex)
        )
        showCustomToast(requireContext(), "Budget created", ToastType.SUCCESS)
        requireActivity().onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic fun newInstance() = AddBudgetFragment()
    }
}
