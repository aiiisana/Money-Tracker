import com.fpis.money.models.User
import org.junit.Test
import org.junit.Assert.*

class UserTest {

    @Test
    fun `should create user with all fields`() {
        val user = User(
            username = "john_doe",
            email = "john@example.com",
            password = "secure123"
        )

        assertEquals("john_doe", user.username)
        assertEquals("john@example.com", user.email)
        assertEquals("secure123", user.password)
    }

    @Test
    fun `should allow null values for all fields`() {
        val user = User(
            username = null,
            email = null,
            password = null
        )

        assertNull(user.username)
        assertNull(user.email)
        assertNull(user.password)
    }

    @Test
    fun `should allow partial null values`() {
        val user = User(
            username = "test_user",
            email = null,
            password = "testpass"
        )

        assertEquals("test_user", user.username)
        assertNull(user.email)
        assertEquals("testpass", user.password)
    }

    @Test
    fun `equals and hashCode should work correctly`() {
        val user1 = User(
            username = "alice",
            email = "alice@example.com",
            password = "pass123"
        )
        val user2 = User(
            username = "alice",
            email = "alice@example.com",
            password = "pass123"
        )

        assertEquals(user1, user2)
        assertEquals(user1.hashCode(), user2.hashCode())
    }

    @Test
    fun `copy should create modified user`() {
        val original = User(
            username = "bob",
            email = "bob@example.com",
            password = "bobpass"
        )

        val modified = original.copy(
            email = "newbob@example.com",
            password = "newpass"
        )

        assertEquals("bob", modified.username)
        assertEquals("newbob@example.com", modified.email)
        assertEquals("newpass", modified.password)
    }

    @Test
    fun `should handle empty strings`() {
        val user = User(
            username = "",
            email = "",
            password = ""
        )

        assertEquals("", user.username)
        assertEquals("", user.email)
        assertEquals("", user.password)
    }

    @Test
    fun `should distinguish different users`() {
        val user1 = User(
            username = "user1",
            email = "user1@test.com",
            password = "pass1"
        )
        val user2 = User(
            username = "user2",
            email = "user2@test.com",
            password = "pass2"
        )

        assertNotEquals(user1, user2)
    }

}