package com.fpis.money.views.fragments.add

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CategoryBottomSheet(
    private val categories: List<String>,
    private val onCategorySelected: (String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_category, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_categories)
        recyclerView.layoutManager = LinearLayoutManager(context)

        Log.d("CategoryBottomSheet", "Categories size: ${categories.size}") // Add this line

        recyclerView.adapter = CategoryAdapter(categories, onCategorySelected, this)
        Log.d("CategoryBottomSheet", "RecyclerView initialized")
        return view
    }
}