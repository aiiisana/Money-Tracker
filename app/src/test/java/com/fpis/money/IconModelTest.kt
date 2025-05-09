import com.fpis.money.models.IconModel
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class IconModelTest {

    private lateinit var iconModel: IconModel

    @Before
    fun setUp() {
        iconModel = IconModel(
            iconRes = 123,
            colorRes = 456
        )
    }

    // 1. Тестирование базовых свойств
    @Test
    fun `should store icon and color resources`() {
        assertEquals(123, iconModel.iconRes)
        assertEquals(456, iconModel.colorRes)
        assertFalse(iconModel.isSelected) // default value
    }

    // 2. Тестирование дефолтных значений
    @Test
    fun `should initialize with isSelected false by default`() {
        val defaultIcon = IconModel(
            iconRes = 789,
            colorRes = 101
        )
        assertFalse(defaultIcon.isSelected)
    }

    // 3. Тестирование изменения состояния
    @Test
    fun `should allow changing selection state`() {
        iconModel.isSelected = true
        assertTrue(iconModel.isSelected)

        iconModel.isSelected = false
        assertFalse(iconModel.isSelected)
    }

    @Test
    fun `hashCode should be same for equal icons`() {
        val sameIcon = IconModel(
            iconRes = iconModel.iconRes,
            colorRes = iconModel.colorRes
        )
        assertEquals(iconModel.hashCode(), sameIcon.hashCode())
    }

    // 4. Тестирование copy()
    @Test
    fun `copy should create identical but independent object`() {
        val copied = iconModel.copy()
        assertEquals(iconModel, copied)
        assertNotSame(iconModel, copied)
    }

    @Test
    fun `copy with modification should work`() {
        val modified = iconModel.copy(
            colorRes = 999,
            isSelected = true
        )
        assertEquals(123, modified.iconRes)
        assertEquals(999, modified.colorRes)
        assertTrue(modified.isSelected)
    }

    // 5. Edge cases
    @Test
    fun `should handle zero resource values`() {
        val zeroIcon = IconModel(
            iconRes = 0,
            colorRes = 0
        )
        assertEquals(0, zeroIcon.iconRes)
        assertEquals(0, zeroIcon.colorRes)
    }
}