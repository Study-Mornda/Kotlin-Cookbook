import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import org.junit.jupiter.api.Test

class NullTypeTest {

    /*
        name = null 이라면 컴파일 에러가 발생한다.
     */
    @Test
    fun `코틀린에서 널 할당이 불가능한 변수 선언하기`() {
        var name : String = "Dolly"
        name shouldBe "Dolly"
    }

    @Test
    fun `코틀린에서 널 할당이 가능한 변수 선언하기`() {
        var name : String? = null
        name shouldBe null
    }

    /*
        val 의 경우 초기에 값을 할당되면 나중에 값을 변경할 수 없다

        아래의 코드에서 if 문은 middle 속성이 널이 아닌 값을 가지고 있는지 확인하고,
        middle 값이 널이 아니라면 마치 p.middle 의 타입을 String? 타입 대신 String 타입으로 처리하는 영리한 타입 변환을 수행한다.
     */
    @Test
    fun `val 변수의 널 허용성 검사하기`() {
        val p = Person(first = "North", middle = null, last = "West")

        if (p.middle != null) {
            val middleNameLength = p.middle.length // 영리한 타입 변환을 통해 String?타입이 아닌 String 타입으로 변환
        }

        p.middle shouldBe null // 단순히 테스트를 통과하기 위한 assert 구문
    }

    /*
        var 의 경우 초기화 후 값의 변경이 가능하다.

        아래의 코드에서는 val 을 사용했을때와는 다르게 변수 p가 정의된 시점과 p의 middle 속성에 접근하는 시점 중간에
        값이 변경되었을 수도 있다고 가정하고. 영리한 타입 변환을 수행하지 않는다.

        따라서 아래의 코드는 컴파일 타임에 오류가 발생한다.
        Smart cast to 'String' is impossible, because 'p.middle' is a complex expression

        if (p.middle != null) {
            // 이 시점에서 값이 변경되었을 수도 있다고 가정하기 때문에 영리한 타입 변환을 수행하지 않는다.
            val middleNameLength = p.middle.length  // 컴파일 에러 발생
        }
     */
    @Test
    fun `var 변수가 널 값이 아님을 단언하기`() {
        var p = Person(first = "North", middle = null, last = "West")

        if (p.middle != null) {
            /*
                !!(단언 연산자)를 통해 middle 이 널 아님을 보장하는 방식으로 우회할 수 있다.
                하지만 null 일 경우 NullPointException 을 발생시키기 때문에 사용하지 않는것이 좋다.
             */
            val middleNameLength = p.middle!!.length
        }

        p.middle shouldBe null  // 단순히 테스트를 통과하기 위한 assert 구문
    }

    @Test
    fun `안전 호출 연산자 사용하기`() {
        var p = Person(first = "North", middle = null, last = "West")
        val middleNameLength = p.middle?.length // 추론타입이 null 을 허용한다.

        middleNameLength.shouldBeNull()
    }

    @Test
    fun `안전 호출 연산자와 엘비스 연산자`() {
        var p = Person(first = "North", middle = null, last = "West")
        val middleNameLength = p.middle?.length ?: 0 // 추론타입이 null 을 허용하지 않는다.

        middleNameLength shouldBe 0
        middleNameLength.shouldNotBeNull()
    }

    @Test
    fun `안전 타입 변환 연산자`() {
        var p = Person(first = "North", middle = null, last = "West")

        val success = p as? Person   // p1 값이 Person 타입이 된다.
        success should beInstanceOf<Person>()
        success.shouldNotBeNull()

        val fail = p as? Int  // p2 값이 널 값을 받는다.
        fail.shouldBeNull()
    }

    class Person (val first: String, val middle: String?, val last: String)
}