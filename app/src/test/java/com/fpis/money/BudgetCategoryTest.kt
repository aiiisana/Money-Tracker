import com.fpis.money.models.BudgetCategory
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class BudgetCategoryTest {

    private lateinit var category: BudgetCategory

    @Before
    fun setUp() {
        category = BudgetCategory(
            id = 1L,
            name = "Groceries",
            amount = 500.0,
            spent = 150.0,
            color = "#4CAF50"
        )
    }

    // 1. Тестирование базовых свойств
    @Test
    fun `should store correct properties`() {
        assertEquals(1L, category.id)
        assertEquals("Groceries", category.name)
        assertEquals(500.0, category.amount, 0.001)
        assertEquals("#4CAF50", category.color)
    }

    // 2. Тестирование вычисляемого progress
    @Test
    fun `progress should be 30 when spent is 150 of 500`() {
        assertEquals(30, category.progress) // 150/500 = 30%
    }

    @Test
    fun `progress should be 0 if amount is zero`() {
        val zeroBudget = BudgetCategory(
            name = "Empty",
            amount = 0.0,
            spent = 100.0,
            color = "#000000"
        )
        assertEquals(0, zeroBudget.progress)
    }

    @Test
    fun `progress should be 100 when spent equals amount`() {
        val fullBudget = BudgetCategory(
            name = "Full",
            amount = 200.0,
            spent = 200.0,
            color = "#FF0000"
        )
        assertEquals(100, fullBudget.progress)
    }

    @Test
    fun `progress should round to nearest integer`() {
        val testCases = listOf(
            Triple(300.0, 157.0, 52), // 52.33 → 52
            Triple(200.0, 105.0, 53), // 52.5 → 53
            Triple(100.0, 99.9, 100)  // 99.9 → 100
        )

        testCases.forEach { (amount, spent, expected) ->
            val budget = BudgetCategory(
                name = "Test",
                amount = amount,
                spent = spent,
                color = "#FFFFFF"
            )
            assertEquals(expected, budget.progress)
        }
    }

    // 3. Тестирование граничных случаев
    @Test
    fun `should allow empty name`() {
        val emptyNameCategory = BudgetCategory(
            name = "",
            amount = 100.0,
            spent = 0.0,
            color = "#FFFFFF"
        )
        assertEquals("", emptyNameCategory.name)
    }

    @Test
    fun `should allow negative values`() {
        val negativeBudget = BudgetCategory(
            name = "Debt",
            amount = 100.0,
            spent = -20.0,
            color = "#0000FF"
        )
        assertEquals(-20.0, negativeBudget.spent, 0.001)
        assertEquals(120.0, negativeBudget.amount - negativeBudget.spent, 0.001)
    }

    @Test
    fun `should allow invalid color formats`() {
        val invalidColorBudget = BudgetCategory(
            name = "Test",
            amount = 100.0,
            spent = 50.0,
            color = "INVALID_COLOR"
        )
        assertEquals("INVALID_COLOR", invalidColorBudget.color)
    }
}