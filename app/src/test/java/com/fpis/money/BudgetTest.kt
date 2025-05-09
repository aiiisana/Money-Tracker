import com.fpis.money.models.Budget
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class BudgetTest {

    private lateinit var budget: Budget

    @Before
    fun setUp() {
        budget = Budget(
            id = 1L,
            category = "Food",
            amount = 1000.0,
            spent = 300.0,
            color = "#FF5733",
            iconRes = 1,
            colorRes = 1
        )
    }

    // 1. Тестирование базовых свойств
    @Test
    fun `should store correct properties`() {
        assertEquals(1L, budget.id)
        assertEquals("Food", budget.category)
        assertEquals(1000.0, budget.amount, 0.001)
        assertEquals("#FF5733", budget.color)
    }

    // 2. Тестирование вычисляемых свойств
    @Test
    fun `remaining should be amount minus spent`() {
        assertEquals(700.0, budget.remaining, 0.001) // 1000 - 300 = 700
    }

    @Test
    fun `isOverspending should be true when spent exceeds amount`() {
        val overspendingBudget = Budget(
            category = "Shopping",
            amount = 500.0,
            spent = 600.0,
            color = "#3366FF",
            iconRes = 1,
            colorRes = 1
        )
        assertTrue(overspendingBudget.isOverspending)
        assertFalse(budget.isOverspending) // Исходный бюджет без перерасхода
    }

    @Test
    fun `percentage should calculate correct spent ratio`() {
        assertEquals(30, budget.percentage) // 300/1000 = 30%

        val zeroBudget = Budget(
            category = "Transport",
            amount = 0.0,
            spent = 100.0,
            color = "#00FF00",
            iconRes = 1,
            colorRes = 1
        )
        assertEquals(0, zeroBudget.percentage) // При amount=0 процент всегда 0
    }

    // 3. Тестирование граничных случаев
    @Test
    fun `should allow empty category name`() {
        val emptyCategoryBudget = Budget(
            category = "",
            amount = 100.0,
            spent = 0.0,
            color = "#000000",
            iconRes = 1,
            colorRes = 1
        )
        assertEquals("", emptyCategoryBudget.category)
    }

    @Test
    fun `should allow negative values`() {
        val negativeBudget = Budget(
            category = "Debt",
            amount = 100.0,
            spent = -50.0,
            color = "#FFFFFF",
            iconRes = 1,
            colorRes = 1
        )
        assertEquals(-50.0, negativeBudget.spent, 0.001)
        assertEquals(150.0, negativeBudget.remaining, 0.001) // 100 - (-50) = 150
    }

    @Test
    fun `percentage should round down`() {
        val unevenBudget = Budget(
            category = "Utilities",
            amount = 300.0,
            spent = 157.0,
            color = "#FFFF00",
            iconRes = 1,
            colorRes = 1
        )
        assertEquals(52, unevenBudget.percentage) // 157/300 ≈ 52.333 → 52
    }
}