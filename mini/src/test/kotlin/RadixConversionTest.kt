import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RadixConversionTest {

    /*
        public actual inline fun Int.toString(radix: Int): String = java.lang.Integer.toString(this, checkRadix(radix))
        radix 는 2~36의 범위를 가진다.
     */
    @Test
    fun `42를 이진법으로 출력하기`() {
        val actual: String = 42.toString(2)
        val expected: String = "101010"

         actual shouldBe expected
    }

    @Test
    fun `42를 적법한 모든 기수로 출력하기`() {
        val array = (Character.MIN_RADIX..Character.MAX_RADIX)
        array.forEach { radix ->
            println("$radix: ${42.toString(radix)}")
        }

        array.count() shouldBe 35
    }
}