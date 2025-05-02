package com.fpis.money.views.fragments.add.category.icon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import com.fpis.money.utils.GridSpacingItemDecoration
import com.fpis.money.utils.ToastType
import com.fpis.money.utils.showCustomToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class IconPickerBottomSheet(
    private val onSelectionComplete: (iconRes: Int, colorRes: Int) -> Unit,
    private val initialIconRes: Int? = null,
    private val initialColorRes: Int? = null
) : BottomSheetDialogFragment() {

    private lateinit var iconAdapter: IconAdapter
    private lateinit var colorAdapter: ColorAdapter
    private var selectedIconRes: Int? = null
    private var selectedColorRes: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_icon_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.title).text = "Select Icon & Color"

        val iconsRecyclerView = view.findViewById<RecyclerView>(R.id.iconsRecyclerView)
        iconsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

        val colorsRecyclerView = view.findViewById<RecyclerView>(R.id.colorsRecyclerView)
        colorsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 6)

        val icons = listOf(
            R.drawable.ic_food,
            R.drawable.shopping,
            R.drawable.ic_health,
            R.drawable.ic_bus,
            R.drawable.ic_entertainment,
            R.drawable.ic_cash,
            R.drawable.ic_debit_card,
            R.drawable.ic_credit_card,
            R.drawable.ic_calendar,
            R.drawable.ic_car,
            R.drawable.ic_folder,
            R.drawable.ic_home,
            R.drawable.ic_travel,
            R.drawable.ic_wallet
        )

        val colors = listOf(
            R.color.circle_color_1,
            R.color.circle_color_2,
            R.color.circle_color_3,
            R.color.circle_color_4,
            R.color.circle_color_5,
            R.color.circle_color_6,
            R.color.circle_color_7,
            R.color.circle_color_8,
            R.color.circle_color_9,
            R.color.circle_color_10,
            R.color.circle_color_11,
            R.color.circle_color_12
        )

        iconAdapter = IconAdapter(icons) { iconRes ->
            selectedIconRes = iconRes
            updatePreview(view)
        }

        colorAdapter = ColorAdapter(colors) { colorRes ->
            selectedColorRes = colorRes
            iconAdapter.setCurrentColor(colorRes)
            updatePreview(view)
        }
        val spacingPx = (12 * resources.displayMetrics.density).toInt()
        iconsRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 4)
            adapter = iconAdapter
            addItemDecoration(GridSpacingItemDecoration(4, spacingPx, true))
        }
        colorsRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 6)
            adapter = colorAdapter
            addItemDecoration(GridSpacingItemDecoration(6, spacingPx, true))
        }

        iconsRecyclerView.adapter = iconAdapter
        colorsRecyclerView.adapter = colorAdapter

        initialIconRes?.let { iconRes ->
            val iconPos = icons.indexOf(iconRes)
            if (iconPos != -1) {
                iconAdapter.setSelectedPosition(iconPos)
                selectedIconRes = iconRes
            }
        }

        initialColorRes?.let { colorRes ->
            val colorPos = colors.indexOf(colorRes)
            if (colorPos != -1) {
                colorAdapter.setSelectedPosition(colorPos)
                selectedColorRes = colorRes
                iconAdapter.setCurrentColor(colorRes)
            }
        }

        view.findViewById<Button>(R.id.saveButton).setOnClickListener {
            selectedIconRes?.let { iconRes ->
                selectedColorRes?.let { colorRes ->
                    onSelectionComplete(iconRes, colorRes)
                    dismiss()
                } ?: showCustomToast(requireContext(), "Please select a color", ToastType.INFO)
            } ?: showCustomToast(requireContext(), "Please select an icon", ToastType.INFO)
        }

        updatePreview(view)
    }

    private fun updatePreview(view: View) {
        val previewIcon = view.findViewById<ImageView>(R.id.previewIcon)

        selectedIconRes?.let { iconRes ->
            previewIcon.setImageResource(iconRes)

            selectedColorRes?.let { colorRes ->
                previewIcon.setColorFilter(ContextCompat.getColor(requireContext(), colorRes))
            } ?: previewIcon.clearColorFilter()
        } ?: previewIcon.setImageResource(R.drawable.shopping)
    }
}