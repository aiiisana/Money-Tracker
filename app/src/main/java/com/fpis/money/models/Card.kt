package com.fpis.money.models


data class Card(
    var id: String = "",
    var cardHolder: String = "",
    var cardNumber: String = "",
    var expiryDate: String = "",
    var cvv: String = "",
    var cardColor: String = "#4285F4", // Default blue color
    var cardType: String = "Credit",
    var bankName: String = "Kaspi Bank"
)