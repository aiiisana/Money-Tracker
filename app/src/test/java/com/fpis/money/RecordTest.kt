import org.junit.Test
import org.junit.Assert.*
import java.util.*
import com.fpis.money.models.Record
import com.fpis.money.models.RecordType


class RecordTest {

    // 1. Тест для записи о доходе
    @Test
    fun `income record should have accountId and null transfer fields`() {
        val incomeRecord = Record(
            amount = 100.0,
            date = System.currentTimeMillis(),
            type = RecordType.INCOME,
            accountId = "acc_123",
            sourceAccountId = null,
            destinationAccountId = null,
            description = "Salary"
        )

        assertEquals("acc_123", incomeRecord.accountId)
        assertNull(incomeRecord.sourceAccountId)
        assertNull(incomeRecord.destinationAccountId)
        assertEquals(RecordType.INCOME, incomeRecord.type)
    }

    // 2. Тест для записи о расходе
    @Test
    fun `expense record should have accountId and null transfer fields`() {
        val expenseRecord = Record(
            amount = 50.0,
            date = System.currentTimeMillis(),
            type = RecordType.EXPENSE,
            accountId = "acc_456",
            sourceAccountId = null,
            destinationAccountId = null,
            description = "Coffee"
        )

        assertEquals("acc_456", expenseRecord.accountId)
        assertNull(expenseRecord.sourceAccountId)
        assertNull(expenseRecord.destinationAccountId)
        assertEquals(RecordType.EXPENSE, expenseRecord.type)
    }

    // 3. Тест для перевода между счетами
    @Test
    fun `transfer record should have both accounts and null accountId`() {
        val transferRecord = Record(
            amount = 200.0,
            date = System.currentTimeMillis(),
            type = RecordType.TRANSFER,
            accountId = null,
            sourceAccountId = "acc_123",
            destinationAccountId = "acc_456",
            description = "Monthly transfer"
        )

        assertNull(transferRecord.accountId)
        assertEquals("acc_123", transferRecord.sourceAccountId)
        assertEquals("acc_456", transferRecord.destinationAccountId)
        assertEquals(RecordType.TRANSFER, transferRecord.type)
    }

    // 4. Проверка дефолтных значений
    @Test
    fun `should initialize with default values`() {
        val defaultRecord = Record(
            amount = 0.0,
            date = 0L,
            type = RecordType.EXPENSE,
            accountId = null,
            sourceAccountId = null,
            destinationAccountId = null,
            description = null
        )

        assertEquals(0, defaultRecord.id)
        assertNull(defaultRecord.description)
    }
}