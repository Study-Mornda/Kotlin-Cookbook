import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf
import org.junit.jupiter.api.Test;

class ExplicitTypeConversionTest {

    /*
        자바에서는 더 짧은 기본 타입을 더 긴 기본 타입으로 승격시킨다.
        int myInt =3; long myLong = myInt // int 가 long 으로 자동 승격

        래퍼 타입의 경우 한 래퍼 타입에서 다른 레퍼 타입으로 변환하려면 아래와 같이 추가 코드가 필요하다.
        Integer myInteger = 3;
        Long myWrappedLong = myInteger.longValue();
        myWrappedLong = Long.valueOf(myInteger);

        래퍼 타입을 직접 다루는 것은 언박싱을 개발자 스스로 해야 한다는 의미이다.
        먼저 포장된 값을 추출하는 작업 없이는 간단하게 Integer 인스턴스를 Long 에 할당할 수 없다.

        코틀린에서는 기본 타입을 직접적으로 제공하지 않는다.
        개발자가 코드를 직접 작성할 때는 기본 타입이 아닌 클래스를 다룬다는 것에 명심해야 한다.
     */
    @Test
    fun `코틀린에서 int 를 Long 으로 승격시키기`() {
        val intVar: Int = 3
        val longVar: Long = intVar.toLong()

        intVar should beInstanceOf<Int>()
        longVar should beInstanceOf<Long>()
    }

    /*
        코틀린은 타입 변환을 투명하게 수행하는 연산자 중복 장점이 있기 때문에 아래의 코드는 명시적 타입 변환이 필요하지 않다.
        더하기(+) 연산자는 자동으로 intVar 의 값을 long 으로 변환하고 long 리터럴에 그 값을 더한다.
     */
    @Test
    fun `타입 변환을 투명하게 수행하는 연산자 중복 장점`() {
        val intVar: Int = 3
        val longVar: Long = 3L
        val longSum = longVar + intVar

        longSum should beInstanceOf<Long>()
    }
}
