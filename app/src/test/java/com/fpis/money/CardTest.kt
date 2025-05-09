import com.fpis.money.models.Card
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class CardTest {

    private lateinit var card: Card

    @Before
    fun setUp() {
        card = Card(
            id = "card_123",
            cardHolder = "John Doe",
            cardNumber = "1234567812345678",
            expiryDate = "12/25",
            cvv = "123",
            cardColor = "#FF0000",
            cardType = "Debit",
            bankName = "National Bank"
        )
    }

    // 1. Тестирование базовых свойств
    @Test
    fun `should store all properties correctly`() {
        assertEquals("card_123", card.id)
        assertEquals("John Doe", card.cardHolder)
        assertEquals("1234567812345678", card.cardNumber)
        assertEquals("12/25", card.expiryDate)
        assertEquals("#FF0000", card.cardColor)
        assertEquals("Debit", card.cardType)
        assertEquals("National Bank", card.bankName)
    }

    // 2. Тестирование дефолтных значений
    @Test
    fun `should use default values if not provided`() {
        val defaultCard = Card()
        assertEquals("", defaultCard.id)
        assertEquals("#4285F4", defaultCard.cardColor)
        assertEquals("Credit", defaultCard.cardType)
        assertEquals("Kaspi Bank", defaultCard.bankName)
    }

    // 3. Тестирование изменяемости полей (var)
    @Test
    fun `should allow modifying properties`() {
        card.cardHolder = "Jane Smith"
        card.cardNumber = "9876543210987654"
        card.cardType = "Credit"

        assertEquals("Jane Smith", card.cardHolder)
        assertEquals("9876543210987654", card.cardNumber)
        assertEquals("Credit", card.cardType)
    }

    // 4. Тестирование граничных случаев
    @Test
    fun `should allow empty strings for all fields`() {
        val emptyCard = Card(
            id = "",
            cardHolder = "",
            cardNumber = "",
            expiryDate = "",
            cvv = "",
            cardColor = "",
            cardType = "",
            bankName = ""
        )

        assertEquals("", emptyCard.cardHolder)
        assertEquals("", emptyCard.cardNumber)
        assertEquals("", emptyCard.cardColor)
    }

    @Test
    fun `should allow partial updates`() {
        val partialCard = Card(
            cardHolder = "Alex Brown",
            cardNumber = "1111222233334444"
        )

        assertEquals("Alex Brown", partialCard.cardHolder)
        assertEquals("#4285F4", partialCard.cardColor) // Default
        assertEquals("Kaspi Bank", partialCard.bankName) // Default
    }

    // 5. Дополнительные проверки форматов (без валидации)
    @Test
    fun `should accept any expiry date format`() {
        val testCard = Card(expiryDate = "1/30")
        assertEquals("1/30", testCard.expiryDate)
    }

    @Test
    fun `should accept any CVV length`() {
        val testCard = Card(cvv = "1234")
        assertEquals("1234", testCard.cvv)
    }
}