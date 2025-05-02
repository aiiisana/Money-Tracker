import com.fpis.money.models.Transaction
import com.fpis.money.models.TransactionItem
import com.fpis.money.models.Transfer
import org.junit.Test
import org.junit.Assert.*
import java.util.*

class TransactionItemTest {

    private val testDate = System.currentTimeMillis()

    // 1. Тестирование RecordItem
    @Test
    fun `RecordItem should wrap Transaction correctly`() {
        val transaction = Transaction(
            type = "EXPENSE",
            paymentMethod = "CARD",
            date = testDate,
            amount = 100.0f,
            category = "Food",
            subCategory = "Restaurant",
            notes = "Dinner"
        )

        val recordItem = TransactionItem.RecordItem(transaction)

        assertTrue(recordItem is TransactionItem)
        assertEquals(transaction, recordItem.record)
        assertEquals("Food", recordItem.record.category)
    }

    // 2. Тестирование TransferItem
    @Test
    fun `TransferItem should wrap Transfer correctly`() {
        val transfer = Transfer(
            fromAccount = "acc_123",
            toAccount = "acc_456",
            amount = 500.0f,
            date = testDate,
            notes = "Monthly transfer"
        )

        val transferItem = TransactionItem.TransferItem(transfer)

        assertTrue(transferItem is TransactionItem)
        assertEquals(transfer, transferItem.transfer)
        assertEquals("acc_123", transferItem.transfer.fromAccount)
    }

    // 3. Тестирование различий между типами
    @Test
    fun `should distinguish between RecordItem and TransferItem`() {
        val transaction = Transaction(
            type = "INCOME",
            paymentMethod = "CASH",
            date = testDate,
            amount = 200.0f,
            category = "Salary",
            subCategory = "Main",
            notes = "Monthly salary"
        )

        val transfer = Transfer(
            fromAccount = "acc_1",
            toAccount = "acc_2",
            amount = 300.0f,
            date = testDate,
            notes = "Between accounts"
        )

        val recordItem = TransactionItem.RecordItem(transaction)
        val transferItem = TransactionItem.TransferItem(transfer)

        assertNotEquals(recordItem::class, transferItem::class)
    }

    // 4. Тестирование when-выражений
    @Test
    fun `when should handle all sealed subtypes`() {
        val items = listOf(
            TransactionItem.RecordItem(
                Transaction(
                    type = "EXPENSE",
                    paymentMethod = "CARD",
                    date = testDate,
                    amount = 50.0f,
                    category = "Entertainment",
                    subCategory = "Movies",
                    notes = "Cinema"
                )
            ),
            TransactionItem.TransferItem(
                Transfer(
                    fromAccount = "acc_1",
                    toAccount = "acc_2",
                    amount = 200.0f,
                    date = testDate,
                    notes = "Savings"
                )
            )
        )

        val results = items.map { item ->
            when (item) {
                is TransactionItem.RecordItem -> "Record: ${item.record.amount}"
                is TransactionItem.TransferItem -> "Transfer: ${item.transfer.amount}"
            }
        }

        assertEquals("Record: 50.0", results[0])
        assertEquals("Transfer: 200.0", results[1])
    }

    // 5. Тестирование equals/hashCode
    @Test
    fun `equal RecordItems should be equal`() {
        val tx = Transaction(
            type = "EXPENSE",
            paymentMethod = "CARD",
            date = testDate,
            amount = 100.0f,
            category = "Food",
            subCategory = "Groceries",
            notes = "Test"
        )

        val item1 = TransactionItem.RecordItem(tx)
        val item2 = TransactionItem.RecordItem(tx.copy())

        assertEquals(item1, item2)
        assertEquals(item1.hashCode(), item2.hashCode())
    }

    // 6. Тестирование null-значений в полях
    @Test
    fun `should handle null notes in TransferItem`() {
        val transfer = Transfer(
            fromAccount = "acc_1",
            toAccount = "acc_2",
            amount = 100.0f,
            date = testDate,
            notes = null
        )

        val item = TransactionItem.TransferItem(transfer)
        assertNull(item.transfer.notes)
    }
}