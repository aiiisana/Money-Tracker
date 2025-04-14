package com.fpis.money.views.fragments.add.category.subcategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.fpis.money.R

class AddSubcategoryDialog : DialogFragment() {
    private var onSubcategoryAdded: ((name: String) -> Unit)? = null

    fun setOnSubcategoryAddedListener(listener: (name: String) -> Unit) {
        onSubcategoryAdded = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_add_subcategory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<EditText>(R.id.etSubcategoryName)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            if (name.isNotBlank()) {
                onSubcategoryAdded?.invoke(name)
                dismiss()
            } else {
                etName.error = "Please enter subcategory name"
            }
        }
    }
}