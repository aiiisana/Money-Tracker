import com.fpis.money.models.Converters
import org.junit.Test
import org.junit.Assert.*
import java.sql.Date
import java.util.*

class ConvertersTest {

    private val converters = Converters()

    // 1. Тестирование конвертации Date <-> Timestamp
    @Test
    fun `fromTimestamp should convert Long to Date`() {
        val timestamp = 1672531200000L // 1 января 2023 UTC
        val date = converters.fromTimestamp(timestamp)
        assertEquals(Date(timestamp), date)
    }

    @Test
    fun `fromTimestamp should return null for null input`() {
        assertNull(converters.fromTimestamp(null))
    }

    @Test
    fun `dateToTimestamp should convert Date to Long`() {
        val date = Date(1672531200000L)
        val timestamp = converters.dateToTimestamp(date)
        assertEquals(1672531200000L, timestamp)
    }

    @Test
    fun `dateToTimestamp should return null for null input`() {
        assertNull(converters.dateToTimestamp(null))
    }

    // 2. Тестирование конвертации List <-> String
    @Test
    fun `fromList should convert List to comma-separated String`() {
        val list = listOf("Food", "Transport", "Entertainment")
        val result = converters.fromList(list)
        assertEquals("Food,Transport,Entertainment", result)
    }

    @Test
    fun `fromList should return empty String for empty List`() {
        assertEquals("", converters.fromList(emptyList()))
    }

    @Test
    fun `toList should convert comma-separated String to List`() {
        val str = "A,B,C"
        val result = converters.toList(str)
        assertEquals(listOf("A", "B", "C"), result)
    }

    @Test
    fun `toList should handle empty String`() {
        assertTrue(converters.toList("").isEmpty())
    }

    @Test
    fun `toList should handle blank String`() {
        assertTrue(converters.toList("   ").isEmpty())
    }

    @Test
    fun `toList should handle strings with extra commas`() {
        assertEquals(
            listOf("A", "", "B"),
            converters.toList("A,,B")
        )
    }

    @Test
    fun `toList should preserve spaces around items`() {
        assertEquals(
            listOf(" A ", " B "),
            converters.toList(" A , B ")
        )
    }

    // 3. Интеграционные тесты (туда-обратно)
    @Test
    fun `date conversion should be reversible`() {
        val originalTimestamp = System.currentTimeMillis()
        val date = converters.fromTimestamp(originalTimestamp)
        val convertedTimestamp = converters.dateToTimestamp(date)
        assertEquals(originalTimestamp, convertedTimestamp)
    }

    @Test
    fun `list conversion should be reversible for normal case`() {
        val originalList = listOf("X", "Y", "Z")
        val str = converters.fromList(originalList)
        val convertedList = converters.toList(str)
        assertEquals(originalList, convertedList)
    }
}