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
import com.fpis.money.models.IconSelection
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class IconPickerBottomSheet(
    private val onSelectionComplete: (iconRes: Int, colorRes: Int) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var iconAdapter: IconAdapter
    private lateinit var colorAdapter: ColorAdapter
    private var currentSelection = IconSelection()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_icon_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Заголовок
        view.findViewById<TextView>(R.id.title).text = "Select Icon & Color"

        // Инициализация RecyclerView для иконок
        val iconsRecyclerView = view.findViewById<RecyclerView>(R.id.iconsRecyclerView)
        iconsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

        // Инициализация RecyclerView для цветов
        val colorsRecyclerView = view.findViewById<RecyclerView>(R.id.colorsRecyclerView)
        colorsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 6)

        // Список доступных иконок
        val icons = listOf(
            R.drawable.ic_food,
            R.drawable.ic_shopping,
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

        // Список доступных цветов
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

        // Адаптер для иконок
        iconAdapter = IconAdapter(icons) { iconRes ->
            currentSelection.selectedIconRes = iconRes
            updatePreview(view)
        }

        // Адаптер для цветов
        colorAdapter = ColorAdapter(colors) { colorRes ->
            currentSelection.selectedColorRes = colorRes
            updatePreview(view)
        }

        iconsRecyclerView.adapter = iconAdapter
        colorsRecyclerView.adapter = colorAdapter

        // Кнопка сохранения
        view.findViewById<Button>(R.id.saveButton).setOnClickListener {
            currentSelection.selectedIconRes?.let { iconRes ->
                currentSelection.selectedColorRes?.let { colorRes ->
                    onSelectionComplete(iconRes, colorRes)
                    dismiss()
                }
            }
        }

        // Предпросмотр выбранной комбинации
        view.findViewById<View>(R.id.previewContainer).visibility = View.VISIBLE
    }

    private fun updatePreview(view: View) {
        val previewIcon = view.findViewById<ImageView>(R.id.previewIcon)

        currentSelection.selectedIconRes?.let { iconRes ->
            previewIcon.setImageResource(iconRes)

            currentSelection.selectedColorRes?.let { colorRes ->
                previewIcon.setColorFilter(ContextCompat.getColor(requireContext(), colorRes))
            }
        }
    }
}