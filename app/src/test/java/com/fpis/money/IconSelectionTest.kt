import com.fpis.money.models.IconSelection
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class IconSelectionTest {

    private lateinit var iconSelection: IconSelection

    @Before
    fun setUp() {
        iconSelection = IconSelection(
            selectedIconRes = 123,
            selectedColorRes = 456
        )
    }

    // 1. Тестирование базовых свойств
    @Test
    fun `should store selected icon and color resources`() {
        assertEquals(123, iconSelection.selectedIconRes)
        assertEquals(456, iconSelection.selectedColorRes)
    }

    // 2. Тестирование дефолтных значений
    @Test
    fun `should initialize with null values by default`() {
        val defaultSelection = IconSelection()
        assertNull(defaultSelection.selectedIconRes)
        assertNull(defaultSelection.selectedColorRes)
    }

    // 3. Тестирование изменения состояния
    @Test
    fun `should allow modifying icon resource`() {
        iconSelection.selectedIconRes = 789
        assertEquals(789, iconSelection.selectedIconRes)
    }

    @Test
    fun `should allow modifying color resource`() {
        iconSelection.selectedColorRes = 999
        assertEquals(999, iconSelection.selectedColorRes)
    }

    @Test
    fun `should allow resetting to null`() {
        iconSelection.selectedIconRes = null
        iconSelection.selectedColorRes = null
        assertNull(iconSelection.selectedIconRes)
        assertNull(iconSelection.selectedColorRes)
    }

    // 4. Тестирование equals и hashCode
    @Test
    fun `equals should compare icon and color resources`() {
        val sameSelection = IconSelection(
            selectedIconRes = 123,
            selectedColorRes = 456
        )

        val differentSelection = IconSelection(
            selectedIconRes = 999,
            selectedColorRes = 456
        )

        assertEquals(iconSelection, sameSelection)
        assertNotEquals(iconSelection, differentSelection)
    }

    @Test
    fun `null values should be equal`() {
        val selection1 = IconSelection(null, null)
        val selection2 = IconSelection(null, null)
        assertEquals(selection1, selection2)
    }

    // 5. Тестирование copy()
    @Test
    fun `copy should create identical but independent object`() {
        val copied = iconSelection.copy()
        assertEquals(iconSelection, copied)
        assertNotSame(iconSelection, copied)
    }

    @Test
    fun `copy with modification should work`() {
        val modified = iconSelection.copy(
            selectedIconRes = null,
            selectedColorRes = 777
        )
        assertNull(modified.selectedIconRes)
        assertEquals(777, modified.selectedColorRes)
    }

    // 6. Edge cases
    @Test
    fun `should handle partial selection`() {
        val partialSelection = IconSelection(
            selectedIconRes = 111,
            selectedColorRes = null
        )
        assertEquals(111, partialSelection.selectedIconRes)
        assertNull(partialSelection.selectedColorRes)
    }

    @Test
    fun `should handle zero resource values`() {
        val zeroSelection = IconSelection(
            selectedIconRes = 0,
            selectedColorRes = 0
        )
        assertEquals(0, zeroSelection.selectedIconRes)
        assertEquals(0, zeroSelection.selectedColorRes)
    }
}