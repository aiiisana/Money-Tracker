import androidx.room.Entity
import com.fpis.money.models.Transfer
import org.junit.Test
import org.junit.Assert.*
import java.util.*

class TransferTest {

    private val testDate = System.currentTimeMillis()

    @Test
    fun `should create transfer with all required fields`() {
        val transfer = Transfer(
            fromAccount = "acc_123",
            toAccount = "acc_456",
            amount = 500.75f,
            date = testDate,
            notes = "Monthly transfer"
        )

        assertEquals(0, transfer.id) // auto-generated
        assertEquals("acc_123", transfer.fromAccount)
        assertEquals("acc_456", transfer.toAccount)
        assertEquals(500.75f, transfer.amount, 0.001f)
        assertEquals("Monthly transfer", transfer.notes)
    }

    @Test
    fun `should auto-generate id`() {
        val transfer1 = Transfer(
            fromAccount = "acc_1",
            toAccount = "acc_2",
            amount = 100f,
            date = testDate
        )

        val transfer2 = Transfer(
            id = 10,
            fromAccount = "acc_3",
            toAccount = "acc_4",
            amount = 200f,
            date = testDate
        )

        assertEquals(0, transfer1.id)
        assertEquals(10, transfer2.id)
    }

    @Test
    fun `should handle null notes`() {
        val transfer = Transfer(
            fromAccount = "acc_123",
            toAccount = "acc_456",
            amount = 300f,
            date = testDate,
            notes = null
        )

        assertNull(transfer.notes)
    }

    @Test
    fun `equals and hashCode should work correctly`() {
        val fixedDate = System.currentTimeMillis()
        val transfer1 = Transfer(
            id = 1,
            fromAccount = "acc_123",
            toAccount = "acc_456",
            amount = 500f,
            date = fixedDate,
            notes = "Test"
        )

        val transfer2 = Transfer(
            id = 1,
            fromAccount = "acc_123",
            toAccount = "acc_456",
            amount = 500f,
            date = fixedDate,
            notes = "Test"
        )

        assertEquals(transfer1, transfer2)
        assertEquals(transfer1.hashCode(), transfer2.hashCode())
    }


    @Test
    fun `copy should create modified transfer`() {
        val original = Transfer(
            fromAccount = "acc_1",
            toAccount = "acc_2",
            amount = 100f,
            date = testDate
        )

        val modified = original.copy(
            amount = 150f,
            notes = "Modified"
        )

        assertEquals(original.fromAccount, modified.fromAccount)
        assertEquals(150f, modified.amount, 0.001f)
        assertEquals("Modified", modified.notes)
    }

    @Test
    fun `should allow same accounts for from and to`() {
        val transfer = Transfer(
            fromAccount = "acc_123",
            toAccount = "acc_123",
            amount = 100f,
            date = testDate
        )

        assertEquals("acc_123", transfer.fromAccount)
        assertEquals("acc_123", transfer.toAccount)
    }

    @Test
    fun `should allow negative amounts`() {
        val transfer = Transfer(
            fromAccount = "acc_123",
            toAccount = "acc_456",
            amount = -100f,
            date = testDate
        )

        assertEquals(-100f, transfer.amount, 0.001f)
    }

    @Test
    fun `should allow empty notes string`() {
        val transfer = Transfer(
            fromAccount = "acc_123",
            toAccount = "acc_456",
            amount = 100f,
            date = testDate,
            notes = ""
        )

        assertEquals("", transfer.notes)
    }
}