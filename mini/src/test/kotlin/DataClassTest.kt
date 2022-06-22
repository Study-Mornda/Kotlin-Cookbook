import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import org.junit.jupiter.api.Test

class DataClassTest {
    @Test
    fun `코틀린이 생성한 equals 와 hashCode 동등성 비교`() {
        val p1 = Product("baseball", 10.0)
        val p2 = Product("baseball", 10.0, false)

        p1 shouldBe p2
        p1.hashCode() shouldBe p2.hashCode()
    }

    @Test
    fun `코틀린이 생성한 equals 와 hashCode 동등성 비교, setOf 함수 이용`() {
        val p1 = Product("baseball", 10.0)
        val p2 = Product("baseball", 10.0, false)

        val product = setOf(p1, p2)

        product.size shouldBe 1
    }

    @Test
    fun `copy 함수 테스트`() {
        val p1 = Product("baseball", 10.0)
        val p2 = p1.copy(price = 12.0)  // 얕은 복사

        "baseball" shouldBe p2.name
        p2.price shouldBe 12.0
        p2.onSale.shouldBeFalse()
    }

    /*
        두 개의 OrderItem 인스턴스가 동등하지만 레퍼런스 동등 연산자 ===가 false 를 리턴하기 때문에 2개의 서로 다른 객체임을 나타낸다.
        하지만 두 OrderItem 인스턴스는 === 연산자가 있는 Product 레퍼런스에 true 를 리턴하므로 같은 내부 Product 인스턴스를 공유하고 있다.
     */
    @Test
    fun `얕은 복사를 검증하는 테스트`() {
        val item1 = OrderItem(Product("baseball", 10.0), 5)
        val item2 = item1.copy()

        item1 shouldBeEqualToComparingFields item2
        item1 shouldNotBeSameInstanceAs item2   // copy(얕은 복사) 함수로 생성한 OrderItem 은 다른 객체다.

        item1.product shouldBeEqualToComparingFields item2.product
        item1.product shouldBeSameInstanceAs item2.product  // 두 OrderItem 인스턴스에 있는 Product 는 같은 객체이다.
    }

    @Test
    fun `Product 인스턴스 구조 분해`() {
        val p = Product("baseball", 10.0)

        val (name, price, sale) = p

        p.name shouldBe name
        p.price shouldBe price
        sale.shouldBeFalse()
    }

    data class Product(val name: String, var price: Double, val onSale: Boolean = false)
    data class OrderItem(val product: Product, val quantity: Int)
}