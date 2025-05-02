import com.fpis.money.models.RecordType
import org.junit.Test
import org.junit.Assert.*

class RecordTypeTest {

    @Test
    fun `should have correct enum values`() {
        val values = RecordType.values()
        assertEquals(3, values.size)
        assertEquals(RecordType.INCOME, values[0])
        assertEquals(RecordType.EXPENSE, values[1])
        assertEquals(RecordType.TRANSFER, values[2])
    }

    @Test
    fun `should convert from string correctly`() {
        assertEquals(RecordType.INCOME, RecordType.valueOf("INCOME"))
        assertEquals(RecordType.EXPENSE, RecordType.valueOf("EXPENSE"))
        assertEquals(RecordType.TRANSFER, RecordType.valueOf("TRANSFER"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw on invalid value`() {
        RecordType.valueOf("INVALID_TYPE")
    }

    @Test
    fun `should maintain correct order`() {
        assertEquals(0, RecordType.INCOME.ordinal)
        assertEquals(1, RecordType.EXPENSE.ordinal)
        assertEquals(2, RecordType.TRANSFER.ordinal)
    }

    @Test
    fun `should return correct string representation`() {
        assertEquals("INCOME", RecordType.INCOME.toString())
        assertEquals("EXPENSE", RecordType.EXPENSE.toString())
        assertEquals("TRANSFER", RecordType.TRANSFER.toString())
    }

    // Дополнительные тесты для возможных методов
    @Test
    fun `isIncome should return true only for INCOME`() {
        assertTrue(RecordType.INCOME.isIncome())
        assertFalse(RecordType.EXPENSE.isIncome())
        assertFalse(RecordType.TRANSFER.isIncome())
    }
}

// Добавьте эти методы-расширения в основной код, если нужно
fun RecordType.isIncome() = this == RecordType.INCOME