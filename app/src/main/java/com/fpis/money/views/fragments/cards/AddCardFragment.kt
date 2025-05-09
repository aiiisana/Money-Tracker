package com.fpis.money.views.fragments.cards

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.fpis.money.R
import com.fpis.money.databinding.FragmentAddCardBinding
import com.fpis.money.models.Card
import com.fpis.money.utils.ToastType
import com.fpis.money.utils.showCustomToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.larswerkman.holocolorpicker.ColorPicker
import com.larswerkman.holocolorpicker.SaturationBar
import java.io.FileNotFoundException
import java.util.Calendar
import java.util.UUID

class AddCardFragment : Fragment() {

    private var _binding: FragmentAddCardBinding? = null
    private val binding get() = _binding!!
    private var selectedColor = "#4285F4"
    private var customImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupTextChangeListeners()
        setupExpiryDatePicker()
        setupCardNumberAndCVVFormatting()
        updateCardPreview()
        updateColorCircle()
    }

    private fun setupUI() {
        binding.btnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnAddCard.setOnClickListener {
            if (validateForm()) {
                saveCard()
            }
        }

        binding.btnCustomImage.setOnClickListener {
            openImagePicker()
        }

        binding.btnColorPicker.setOnClickListener {
            showColorPickerDialog()
        }
    }

    private fun setupTextChangeListeners() {
        binding.etCardHolder.addTextChangedListener(createTextWatcher { updateCardPreview() })
        binding.etCardNumber.addTextChangedListener(createTextWatcher { updateCardPreview() })
        binding.etExpiryDate.addTextChangedListener(createTextWatcher { updateCardPreview() })
        binding.etCvv.addTextChangedListener(createTextWatcher { updateCardPreview() })
    }

    private fun setupExpiryDatePicker() {
        binding.etExpiryDate.setOnClickListener {
            showDatePicker()
        }

        binding.etExpiryDate.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            private val calendar = Calendar.getInstance()
            private val currentYear = calendar.get(Calendar.YEAR) % 100
            private val currentMonth = calendar.get(Calendar.MONTH) + 1

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val str = s.toString().replace("/", "")
                if (str.length > 0) {
                    val month = str.substring(0, minOf(2, str.length)).toIntOrNull() ?: 0

                    if (str.length == 1 && month > 1) {
                        s?.replace(0, s.length, "0$month")
                    } else if (str.length >= 2) {
                        val validMonth = when {
                            month < 1 -> "01"
                            month > 12 -> "12"
                            else -> month.toString().padStart(2, '0')
                        }

                        if (str.length > 2) {
                            val year = str.substring(2, minOf(4, str.length)).toIntOrNull() ?: 0

                            val validYear = when {
                                year < currentYear -> currentYear.toString().padStart(2, '0')
                                else -> year.toString().padStart(minOf(2, str.length - 2), '0')
                            }

                            if (year == currentYear && month < currentMonth) {
                                s?.replace(0, s.length, "$validMonth/$validYear")
                            } else {
                                s?.replace(0, s.length, "$validMonth/$validYear")
                            }
                        } else {
                            s?.replace(0, s.length, validMonth)
                        }
                    }
                }

                if (s?.length == 2) {
                    s.append("/")
                }

                isFormatting = false
            }
        })
    }

    private fun setupCardNumberAndCVVFormatting() {
        // Limit card number to 12 digits
        binding.etCardNumber.filters = arrayOf(android.text.InputFilter.LengthFilter(19))

        // Format card number with spaces
        binding.etCardNumber.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val digits = s.toString().replace("\\D".toRegex(), "")
                val formatted = StringBuilder()

                for (i in digits.indices) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(" ")
                    }
                    formatted.append(digits[i])
                }

                if (formatted.toString() != s.toString()) {
                    s?.replace(0, s.length, formatted.toString())
                }

                isFormatting = false
            }
        })

        binding.etCvv.filters = arrayOf(android.text.InputFilter.LengthFilter(3))
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        // Parse existing date if available
        val existingDate = binding.etExpiryDate.text.toString()
        if (existingDate.matches("\\d{2}/\\d{2}".toRegex())) {
            val parts = existingDate.split("/")
            val month = parts[0].toInt() - 1 // Calendar months are 0-based
            val year = 2000 + parts[1].toInt() // Assuming 20xx years
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.YEAR, year)
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.DatePickerTheme,
            { _, year, monthOfYear, _ ->
                val selectedMonth = monthOfYear + 1 // Calendar months are 0-based
                val yearShort = year % 100
                val formattedDate = String.format("%02d/%02d", selectedMonth, yearShort)
                binding.etExpiryDate.setText(formattedDate)
                updateCardPreview()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            1
        )

        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        calendar.add(Calendar.YEAR, 10)
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        try {
            val datePickerField = datePickerDialog.datePicker.javaClass.getDeclaredField("mDaySpinner")
            datePickerField.isAccessible = true
            val dayPicker = datePickerField.get(datePickerDialog.datePicker) as View
            dayPicker.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }

        datePickerDialog.show()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            customImageUri = data.data
            updateCardPreview()
        }
    }

    private fun showColorPickerDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.card_color_picker, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setView(dialogView)
            .create()

        val colorPicker = dialogView.findViewById<ColorPicker>(R.id.color_picker)
        val saturationBar = dialogView.findViewById<SaturationBar>(R.id.saturation_bar)
        val hexColorText = dialogView.findViewById<TextView>(R.id.tv_hex_color)
        val cancelButton = dialogView.findViewById<Button>(R.id.btn_cancel)
        val selectButton = dialogView.findViewById<Button>(R.id.btn_select)

        colorPicker.addSaturationBar(saturationBar)

        try {
            colorPicker.color = Color.parseColor(selectedColor)
        } catch (e: Exception) {
            colorPicker.color = Color.parseColor("#4285F4")
        }

        colorPicker.onColorChangedListener = object : ColorPicker.OnColorChangedListener {
            override fun onColorChanged(color: Int) {
                val hexColor = String.format("#%06X", 0xFFFFFF and color)
                hexColorText.text = hexColor
                hexColorText.setBackgroundColor(color)
            }
        }

        val initialHexColor = String.format("#%06X", 0xFFFFFF and colorPicker.color)
        hexColorText.text = initialHexColor
        hexColorText.setBackgroundColor(colorPicker.color)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        selectButton.setOnClickListener {
            selectedColor = String.format("#%06X", 0xFFFFFF and colorPicker.color)
            customImageUri = null
            updateColorCircle()
            updateCardPreview()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun createTextWatcher(afterTextChanged: () -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { afterTextChanged() }
        }
    }

    private fun updateColorCircle() {
        try {
            val drawable = binding.selectedColorCircle.background.mutate()
            drawable.setTint(Color.parseColor(selectedColor))
            binding.selectedColorCircle.background = drawable
        } catch (e: Exception) {
            binding.selectedColorCircle.setBackgroundResource(R.drawable.circle_blue)
        }
    }

    private fun updateCardPreview() {
        try {
            if (customImageUri != null) {
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(customImageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.cardBackground.background = null
                    binding.cardBackground.setBackgroundColor(Color.TRANSPARENT)
                    binding.cardBackgroundImage.setImageBitmap(bitmap)
                    binding.cardBackgroundImage.visibility = View.VISIBLE
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    binding.cardBackgroundImage.visibility = View.GONE
                    setCardBackgroundColor()
                }
            } else {
                binding.cardBackgroundImage.visibility = View.GONE
                setCardBackgroundColor()
            }
        } catch (e: Exception) {
            binding.cardBackgroundImage.visibility = View.GONE
            binding.cardBackground.setBackgroundResource(R.drawable.card_bg_blue)
        }

        val cardHolder = binding.etCardHolder.text.toString().trim()
        binding.tvCardHolder.text = if (cardHolder.isNotEmpty()) cardHolder else "John Doe"

        val cardNumber = binding.etCardNumber.text.toString().trim()
        val formattedNumber = formatCardNumber(cardNumber)
        val cardNumberGroups = splitCardNumber(formattedNumber)

        binding.tvCardGroup1.text = if (cardNumberGroups[0].isNotEmpty()) cardNumberGroups[0] else "1234"
        binding.tvCardGroup2.text = if (cardNumberGroups[1].isNotEmpty()) cardNumberGroups[1] else "1234"
        binding.tvCardGroup3.text = if (cardNumberGroups[2].isNotEmpty()) cardNumberGroups[2] else "1234"
        binding.tvCardGroup4.text = if (cardNumberGroups[3].isNotEmpty()) cardNumberGroups[3] else "1234"

        val cvv = binding.etCvv.text.toString().trim()
        binding.tvCvv.text = "CVV: ${if (cvv.isNotEmpty()) cvv else "123"}"

        val expiryDate = binding.etExpiryDate.text.toString().trim()
        binding.tvExpireDate.text = if (expiryDate.isNotEmpty()) expiryDate else "MM/YY"
    }

    private fun setCardBackgroundColor() {
        try {
            binding.cardBackground.setBackgroundColor(Color.parseColor(selectedColor))
        } catch (e: Exception) {
            binding.cardBackground.setBackgroundResource(R.drawable.card_bg_blue)
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (binding.etCardHolder.text.toString().trim().isEmpty()) {
            binding.etCardHolder.error = "Card holder name is required"
            isValid = false
        }

        val cardNumber = binding.etCardNumber.text.toString().trim().replace("\\D".toRegex(), "")
        if (cardNumber.length < 16) {
            binding.etCardNumber.error = "Valid card number is required (16 digits)"
            isValid = false
        }

        val expiryDate = binding.etExpiryDate.text.toString().trim()
        if (!expiryDate.matches("\\d{2}/\\d{2}".toRegex())) {
            binding.etExpiryDate.error = "Valid expiry date is required (MM/YY)"
            isValid = false
        }

        val cvv = binding.etCvv.text.toString().trim()
        if (cvv.length < 3) {
            binding.etCvv.error = "Valid CVV is required (3 digits)"
            isValid = false
        }

        return isValid
    }

    private fun saveCard() {
        val card = Card(
            id = UUID.randomUUID().toString(),
            cardHolder = binding.etCardHolder.text.toString().trim(),
            cardNumber = binding.etCardNumber.text.toString().trim().replace("\\D".toRegex(), ""),
            expiryDate = binding.etExpiryDate.text.toString().trim(),
            cvv = binding.etCvv.text.toString().trim(),
            cardColor = selectedColor,
            cardType = "Credit",
            bankName = "Kaspi Bank"
        )

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .document(currentUser.uid)
                .collection("cards")
                .document(card.id)
                .set(card)
                .addOnSuccessListener {
                    showCustomToast(requireContext(), "Card added successfully", ToastType.SUCCESS)
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    showCustomToast(requireContext(), "Error saving card: ${e.message}", ToastType.ERROR)
                }
        } else {
            showCustomToast(requireContext(), "User not authenticated", ToastType.ERROR)
        }
        parentFragmentManager.popBackStack()
    }

    private fun formatCardNumber(cardNumber: String): String {
        val digitsOnly = cardNumber.replace("\\D".toRegex(), "")

        val formatted = StringBuilder()
        for (i in digitsOnly.indices) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ")
            }
            formatted.append(digitsOnly[i])
        }

        return formatted.toString()
    }

    private fun splitCardNumber(cardNumber: String): List<String> {
        val groups = mutableListOf<String>()
        val parts = cardNumber.split(" ")

        for (i in 0 until 4) {
            if (i < parts.size) {
                groups.add(parts[i])
            } else {
                groups.add("")
            }
        }

        return groups
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddCardFragment()
    }
}
