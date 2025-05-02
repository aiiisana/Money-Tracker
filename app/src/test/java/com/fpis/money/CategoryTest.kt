import com.fpis.money.models.Category
import org.junit.Test
import org.junit.Assert.*

class CategoryTest {
    @Test
    fun `should create category with required fields`() {
        val category = Category(
            name = "Food",
            iconRes = 123,
            colorRes = 456
        )

        assertEquals("Food", category.name)
        assertEquals(123, category.iconRes)
        assertEquals(456, category.colorRes)
        assertFalse(category.isIncomeCategory)
    }

    @Test
    fun `should use default values correctly`() {
        val category = Category(
            name = "Transport",
            iconRes = 789,
            colorRes = 101
        )

        assertEquals(0, category.id) // autoGenerate = true
        assertTrue(category.subcategories.isEmpty())
        assertFalse(category.isDefault)
    }

    @Test
    fun `should handle income categories`() {
        val category = Category(
            name = "Salary",
            iconRes = 111,
            colorRes = 222,
            isIncomeCategory = true
        )

        assertTrue(category.isIncomeCategory)
    }

    @Test
    fun `should store subcategories list`() {
        val category = Category(
            name = "Entertainment",
            iconRes = 333,
            colorRes = 444,
            subcategories = listOf("Movies", "Concerts")
        )

        assertEquals(2, category.subcategories.size)
        assertEquals("Movies", category.subcategories[0])
    }

    @Test
    fun `should allow empty name`() {
        val category = Category(
            name = "",
            iconRes = 555,
            colorRes = 666
        )

        assertEquals("", category.name)
    }
}