package com.fpis.money.views.fragments.add.category

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import com.fpis.money.models.Category
import com.fpis.money.views.fragments.add.category.icon.IconPickerBottomSheet
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CategoryBottomSheet(
    private val isIncome: Boolean,
    private val onCategorySelected: (Category) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var viewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.bottom_sheet_category, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_categories)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = CategoryAdapter(
            emptyList(),
            onClick = { category ->
                onCategorySelected(category)
                dismiss()
            },
            dialog = this,
            onLongClick = { category ->
                showDeleteCategoryDialog(category)
            }
        )

        recyclerView.adapter = adapter

        view.findViewById<Button>(R.id.add_category_button).setOnClickListener {
            showAddCategoryDialog()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        viewModel.loadCategories(isIncome)
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.updateCategories(categories)
        }
    }

    private fun showAddCategoryDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_add_category)

        val etName = dialog.findViewById<EditText>(R.id.etCategoryName)
        val btnSave = dialog.findViewById<Button>(R.id.btnSave)
        val iconSelector = dialog.findViewById<ImageView>(R.id.ivIconSelector)

        var selectedIconRes = R.drawable.ic_food
        var selectedColorRes = R.color.green

        iconSelector.setOnClickListener {
            val iconPicker = IconPickerBottomSheet(
                onSelectionComplete = { iconRes, colorRes ->
                    selectedIconRes = iconRes
                    selectedColorRes = colorRes
                    iconSelector.setImageResource(iconRes)
                    iconSelector.setColorFilter(ContextCompat.getColor(requireContext(), colorRes))
                },
                initialIconRes = selectedIconRes,
                initialColorRes = selectedColorRes
            )
            iconPicker.show(parentFragmentManager, "icon_picker")
        }

        iconSelector.setColorFilter(ContextCompat.getColor(requireContext(), selectedColorRes))

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            if (name.isNotBlank()) {
                val newCategory = Category(
                    name = name,
                    iconRes = selectedIconRes,
                    colorRes = selectedColorRes,
                    isIncomeCategory = isIncome
                )
                viewModel.addCustomCategory(newCategory)
                dialog.dismiss()
            } else {
                etName.error = "Please enter category name"
            }
        }

        dialog.show()
    }

    private fun showDeleteCategoryDialog(category: Category) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete category")
            .setMessage("Are you sure you want to delete \"${category.name}\"?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteCategory(category.id, isIncome)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}