import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class SupportPropertyTest {

    @Test
    fun `지연 로딩으로 고객 메시지에 접근하기`() {
        val customer = Customer("Fred").apply { message }   // messages 를 처음 로딩
        3 shouldBe customer.message.size    // messages 에 다시 접근, 로딩됨
    }

    class Customer(val name: String) {
        private var _messages: List<String>? = null

        val message: List<String>
            get() {
                if (_messages == null) {
                    _messages = loadMessages()
                }
                return _messages!!
            }

        private fun loadMessages(): MutableList<String> =
            mutableListOf(
                "Initial contact",
                "Convinced them to use Kotlin",
                "Sold training class. Sweet."
            ).also { println("Loaded messages") }
    }

    @Test
    fun `by lazy 지연 로딩으로 고객 메시지에 접근하기`() {
        val customer = CustomerLazyLoading("Fred")
        3 shouldBe customer.messages.size    // messages 에 다시 접근, 로딩됨
    }

    class CustomerLazyLoading(val name: String) {
        val messages: List<String> by lazy { loadMessages() }

        private fun loadMessages(): MutableList<String> =
            mutableListOf(
                "Initial contact",
                "Convinced them to use Kotlin",
                "Sold training class. Sweet."
            ).also { println("Loaded messages") }
    }
}