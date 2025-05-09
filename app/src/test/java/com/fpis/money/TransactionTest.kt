import androidx.room.Entity
import com.fpis.money.models.Transaction
import org.junit.Test
import org.junit.Assert.*
import java.util.*

class TransactionTest {

    private val testDate = System.currentTimeMillis()

    @Test
    fun `should create transaction with all fields`() {
        val transaction = Transaction(
            type = "EXPENSE",
            paymentMethod = "CARD",
            date = testDate,
            amount = 100.50f,
            category = "Food",
            subCategory = "Restaurants",
            notes = "Business lunch",
            iconRes = 123,
            colorRes = 456
        )

        assertEquals(0L, transaction.id) // auto-generated
        assertEquals("EXPENSE", transaction.type)
        assertEquals(100.50f, transaction.amount)
        assertEquals(123, transaction.iconRes)
        assertEquals("Business lunch", transaction.notes)
    }

    @Test
    fun `should use default null values for optional fields`() {
        val transaction = Transaction(
            type = "INCOME",
            paymentMethod = "CASH",
            date = testDate,
            amount = 200.0f,
            category = "Salary",
            subCategory = "Main",
            notes = ""
        )

        assertNull(transaction.iconRes)
        assertNull(transaction.colorRes)
        assertEquals("", transaction.notes)
    }

    @Test
    fun `should allow negative amounts`() {
        val transaction = Transaction(
            type = "EXPENSE",
            paymentMethod = "CARD",
            date = testDate,
            amount = -50.0f,
            category = "Penalty",
            subCategory = "Late fee",
            notes = "Late payment"
        )

        assertEquals(-50.0f, transaction.amount)
    }

    @Test
    fun `should allow long notes`() {
        val longNote = "Very detailed description ".repeat(10)
        val transaction = Transaction(
            type = "EXPENSE",
            paymentMethod = "TRANSFER",
            date = testDate,
            amount = 75.0f,
            category = "Services",
            subCategory = "Subscription",
            notes = longNote
        )

        assertEquals(longNote, transaction.notes)
    }

    @Test
    fun `equals and hashCode should consider all fields`() {
        val tx1 = Transaction(
            id = 1L,
            type = "INCOME",
            paymentMethod = "CASH",
            date = testDate,
            amount = 100.0f,
            category = "Bonus",
            subCategory = "Annual",
            notes = "Yearly bonus"
        )

        val tx2 = Transaction(
            id = 1L,
            type = "INCOME",
            paymentMethod = "CASH",
            date = testDate,
            amount = 100.0f,
            category = "Bonus",
            subCategory = "Annual",
            notes = "Yearly bonus"
        )

        assertEquals(tx1, tx2)
        assertEquals(tx1.hashCode(), tx2.hashCode())
    }

}