import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ConstVersusValTest {
    /*
        const: 컴파일 타임에 할당된다.
        val: 런타임에 할당 된다.

        컴파일 타임 상ㄱ수는 반드시 객체나 동반 객체 선언의 최상위 속성 또는 멤버여야 한다.
        컴파일 타임 상수는 문자열 또는 기본 타입의 래퍼 클래스이며, 사용자 정의 획득자(getter)를 가질 수 없다.
        컴파일 타임 상수는 컴파일 시점에 값을 사용할 수 있도록 main 함수를 포함한 모든 함수의 바깥쪽에서 할당돼야 한다.

        val 은 키워드지만 const 는 private, inline 등과 같은 변경자임에 유의해야 한다.
        따라서 const 와 val 을 같이 사용해야 한다.
     */
    class Task(val name: String, _property: Int = DEFAULT_PRIORITY) {

        companion object {
            // 컴파일 타임 상수
            const val MIN_PRIORITY = 1
            const val MAX_PRIORITY = 5
            const val DEFAULT_PRIORITY = 3
        }

        // 사용자 정의 설정자(setter)를 사용하는 속성
        var priority = validaPriority(_property)
            set(value) {
                field = validaPriority(value)
            }

        // private 검증 함수
        private fun validaPriority(p: Int) = p.coerceIn(MIN_PRIORITY..MAX_PRIORITY)
    }
}