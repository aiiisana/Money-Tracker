package com.fpis.money.views.fragments.cards

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fpis.money.R
import com.fpis.money.databinding.FragmentAddCardBinding
import com.fpis.money.views.fragments.cards.Card
import java.util.UUID

class AddCardFragment : Fragment() {

    private var _binding: FragmentAddCardBinding? = null
    private val binding get() = _binding!!
    private var selectedColor = "#4285F4" // Default blue color

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
        setupColorSelection()
        setupTextChangeListeners()
        updateCardPreview()
    }

    private fun setupUI() {
        // Set up close button
        binding.btnClose.setOnClickListener {
            // Go back to the previous fragment
            parentFragmentManager.popBackStack()
        }

        // Set up add card button
        binding.btnAddCard.setOnClickListener {
            if (validateForm()) {
                saveCard()
            }
        }
    }

    private fun setupColorSelection() {
        // Set up color selection buttons
        binding.colorBlue.setOnClickListener {
            selectedColor = "#4285F4"
            updateColorSelection()
            updateCardPreview()
        }

        binding.colorOrange.setOnClickListener {
            selectedColor = "#FF9966"
            updateColorSelection()
            updateCardPreview()
        }

        binding.colorGreen.setOnClickListener {
            selectedColor = "#4CAF50"
            updateColorSelection()
            updateCardPreview()
        }

        binding.colorPurple.setOnClickListener {
            selectedColor = "#9C27B0"
            updateColorSelection()
            updateCardPreview()
        }

        // Set initial selection
        updateColorSelection()
    }

    private fun setupTextChangeListeners() {
        binding.etCardHolder.addTextChangedListener(createTextWatcher { updateCardPreview() })
        binding.etCardNumber.addTextChangedListener(createTextWatcher { updateCardPreview() })
        binding.etExpiryDate.addTextChangedListener(createTextWatcher { updateCardPreview() })
        binding.etCvv.addTextChangedListener(createTextWatcher { updateCardPreview() })
    }

    private fun createTextWatcher(afterTextChanged: () -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { afterTextChanged() }
        }
    }

    private fun updateColorSelection() {
        // Reset all selections
        binding.colorBlue.alpha = 0.5f
        binding.colorOrange.alpha = 0.5f
        binding.colorGreen.alpha = 0.5f
        binding.colorPurple.alpha = 0.5f

        // Highlight selected color
        when (selectedColor) {
            "#4285F4" -> binding.colorBlue.alpha = 1.0f
            "#FF9966" -> binding.colorOrange.alpha = 1.0f
            "#4CAF50" -> binding.colorGreen.alpha = 1.0f
            "#9C27B0" -> binding.colorPurple.alpha = 1.0f
        }
    }

    private fun updateCardPreview() {
        // Update card background color
        try {
            binding.cardBackground.setBackgroundColor(android.graphics.Color.parseColor(selectedColor))
        } catch (e: Exception) {
            // Fallback to default blue
            binding.cardBackground.setBackgroundResource(R.drawable.card_bg_blue)
        }

        // Update card holder name
        val cardHolder = binding.etCardHolder.text.toString().trim()
        binding.tvCardHolder.text = if (cardHolder.isNotEmpty()) cardHolder else "John Doe"

        // Update card number
        val cardNumber = binding.etCardNumber.text.toString().trim()
        val formattedNumber = formatCardNumber(cardNumber)
        val cardNumberGroups = splitCardNumber(formattedNumber)

        binding.tvCardGroup1.text = if (cardNumberGroups[0].isNotEmpty()) cardNumberGroups[0] else "1234"
        binding.tvCardGroup2.text = if (cardNumberGroups[1].isNotEmpty()) cardNumberGroups[1] else "1234"
        binding.tvCardGroup3.text = if (cardNumberGroups[2].isNotEmpty()) cardNumberGroups[2] else "1234"
        binding.tvCardGroup4.text = if (cardNumberGroups[3].isNotEmpty()) cardNumberGroups[3] else "1234"

        // Update CVV
        val cvv = binding.etCvv.text.toString().trim()
        binding.tvCvv.text = "CVV: ${if (cvv.isNotEmpty()) cvv else "123"}"

        // Update expiry date
        val expiryDate = binding.etExpiryDate.text.toString().trim()
        binding.tvExpireDate.text = if (expiryDate.isNotEmpty()) expiryDate else "MM/YY"
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validate card holder
        if (binding.etCardHolder.text.toString().trim().isEmpty()) {
            binding.etCardHolder.error = "Card holder name is required"
            isValid = false
        }

        // Validate card number
        val cardNumber = binding.etCardNumber.text.toString().trim().replace("\\D".toRegex(), "")
        if (cardNumber.length < 16) {
            binding.etCardNumber.error = "Valid card number is required"
            isValid = false
        }

        // Validate expiry date
        val expiryDate = binding.etExpiryDate.text.toString().trim()
        if (!expiryDate.matches("\\d{2}/\\d{2}".toRegex())) {
            binding.etExpiryDate.error = "Valid expiry date is required (MM/YY)"
            isValid = false
        }

        // Validate CVV
        val cvv = binding.etCvv.text.toString().trim()
        if (cvv.length < 3) {
            binding.etCvv.error = "Valid CVV is required"
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
            cardType = "Credit", // Default type
            bankName = "Kaspi Bank" // Default bank
        )

        // Here you would save the card to your database
        // For now, just show a success message and go back
        Toast.makeText(requireContext(), "Card added successfully", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }

    private fun formatCardNumber(cardNumber: String): String {
        // Remove any non-digit characters
        val digitsOnly = cardNumber.replace("\\D".toRegex(), "")

        // Format as groups of 4 digits
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

        // Ensure we have 4 groups
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
