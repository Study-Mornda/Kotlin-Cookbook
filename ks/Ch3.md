## 3.1 const와 val의 차이 이해하기
- 컴파일 타임 상수에 `const` 변경자를 사용함, `val` 키워드는 변수에 한 번 할당되면 변경이 불가능함을 나타내지만 이러한 할당은 실행 시간에 일어남
- 코틀린 키워드 `val` 은 값이 변경 불가능한 변수임을 나타냄, 자바에서 `final` 키워드 같은 목적으로 사용됨
- 그럼에도 `const` 를 지원하는 이유는
    - 컴파일 타임 상수는 반드시 객체나 동반 객체(companion object)선언의 최상위 속성 또는 멤버여야 함
    - 컴파일 타임 상수는 문자열 또는 기본 타입의 래퍼 클래스이며, 사용자 정의 getter를 가질 수 없음
    - 컴파일 타임 상수는 컴파일 시점에 값을 사용할 수 있도록 `main` 함수를 포함한 모든 함수의 바깥쪽에서 할당돼야함

```kotlin
class Task(val name: String, _priority: Int = DEFAULT_PRIORITY) {

		companion object {
				const val MIN_PRIORITY = 1
				const val MAX_PRIORITY = 5
				const val DEFAULT_PRIORITY = 3
		}

		var priority = validPriority(_priority)
				set(value) { 
						field = validPriority(value)
				}

		// private 검증 함수
		private fun validPriority(p: Int) = p.coerceIn(MIN_PRIORITY, MAX_PRIORITY)
}
```

- 코틀린에서 `val` 은 키워드지만 `const` 는 `private` , `inline` 등과 같은 변경자임, 그런 이유로 `const` 가 `val` 키워드를 대체하는 것이 아니라 반드시 같이 써야함

## 3.2 사용자 정의 획득자와 설정자 생성하기
- 코틀린도 데이터와 보통 캡슐화로 알려진 해당 데이터를 조작하는 함수로 이루어짐
- 하지만 특이하게 모든 것이 기본적으로 `public` 임
- 따라서 정보와 연관된 데이터 구조의 세부 구현이 필요하다고 추정되며 이는 데이터 은닉 원칙을 침해하는 것처럼 보임, 코틀린 클래스에서 필드는 직접 선언할 수 없음
```kotlin
class Task(val name: String) {
		var priority = 3
		// ...
}
```

- `name` 은 주 생성자 안에 선언된 반면에 `priority` 는 클래스의 최상위 멤버로 선언됨
- 여기서 `priority` 는 `apply` 블록을 활용하여 값을 할당할 수 있지만 클래스를 인스턴스화 할 때는 `priority` 값을 할당할 수 없음
- 여기서 위와 같은 값을 사용자 정의 획득자와 설정자를 추가해서 처리할 수 있는데 아래와 같음

```kotlin
var <propertyName>[: <PropertyName] [= <property_initializer]
	[<getter>]
	[<setter>]
```

- 여기서 속성 초기화 블록, 획득자, 설정자는 선택사항임, 속성 타입이 초기값 또는 획득자의 리턴 타입에서 추론 가능하다면 속성 타입 또한 선택사항임
- 하지만 생성자에서 선언한 속성에서는 타입 선언이 필수임
```kotlin
// getter 사용
// get 함수의 리턴 타입으로 추론되어 boolean 타입으로 추론됨
val isLowPriority
		get() = priority < 3

// setter 사용
var priority = 3
		set(value) {
				// 코틀린이 생성한 지원 필드 참조
				field = value.coerceIn(1..5)
		}

```

- 앞서 3-1의 코드를 보면 `_priority` 는 생성자의 인자일 뿐 실제 `priority` 속성을 초기화하는 데 사용되고 setter의 우선순위 값을 원하는 범위로 강제하기 위해서 그 값이 변경될 대마다 실행되게 함
```kotlin
class Task(val name: String, _priority: Int = DEFAULT_PRIORITY) {

		companion object {
				const val MIN_PRIORITY = 1
				const val MAX_PRIORITY = 5
				const val DEFAULT_PRIORITY = 3
		}

		var priority = validPriority(_priority)
				set(value) { 
						field = validPriority(value)
				}

		// private 검증 함수
		private fun validPriority(p: Int) = p.coerceIn(MIN_PRIORITY, MAX_PRIORITY)
}
```

## 3.3 데이터 클래스 정의하기
- 데이터를 담는 특정 클래스의 용도를 나타내기 위해서 `data` 키워드를 제공함
- 클래스 정의에 `data` 를 추가하면 코틀린 컴파일러는 일관된 `equals` 와 `hashCode` 함수, 클래스와 속성 값을 보여주는 `toString` 함수, `copy` 함수와 구조 분해를 위한 `component` 함수 등 일련의 함수를 생성함

```kotlin
data class Product(
		val name: String,
		val price: Double,
		val onSale: Boolean = false
)
```

- 코틀린 컴파일러는 주 생성자에 선언된 속성을 바탕으로 `equals` 와 `hashCode` 함수를 생성함
- 여기서 `copy` 의 경우 깊은 복사가 아니라 얕은 복사를 수행함, `copy` 함수로 생성한 객체는 기존 객체와는 다른 객체임
- 속성 값을 리턴하는 함수 `component1` `component2` 등이 있음, 이는 구조 분해에 사용됨

## 3.4 지원 속성 기법
- 클래스의 속성을 클라이언트에 노출하고 싶지만 해당 속성을 초기화하거나 읽는 방법을 제어해야함, 이 때 같은 타입의 속성 하나 더 정의하고 사용자 정의 획득자와 설정자를 이용해 원하는 속성에 접근함

```kotlin
class Customer(val name: String) {
		private var _message: List<String>? = null

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
```

- 위와 같이 생성 즉시 초기화되지 않게 `messages` 속성과 같은 타입의 널 허용 `_messages` 속성을 추가함
- 사용자 정의 획득자는 `messages` 의 로딩 여부를 검사하며 아직 로딩되지 않았다면 메시지를 불러옴
- `_messages` `private` 이기 때문에 생성자 속성을 사용해 `messages` 를 불러올 수 없음
- 여기서 `messages`  호출에 대해서는 `getter` 메소드를 호출해서 씀
- 위와 같은 예제는 지연 로딩을 어렵게 구현함, 아래와 같이 코틀린 내장 `lazy` 대리자 함수를 사용해서 더 쉽게 할 수 있음

```kotlin
class Customer(val name: String) {

		val messages: List<String> by lazy { loadMessages() }

		private fun loadMessages(): MutableList<String> =
				mutableListOf(
						"Initial contact",
						"Convinced them to use Kotlin",
						"Sold training class. Sweet."
				).also { println("Loaded messages") }
}
```

- 속성 초기화를 강제하기 위한 `private` 지원 필드의 사용은 유용한 방법임
- 아래의 앞서 봤던 3-1에서의 예제 코드는 속성에 제약 조건을 강제함
- `_priority` 속성은 실제 클래스 속성이 아니라 오직 생성자 인자임을 나타내는 `val` 로 명시되지 않음
- 제어하고 싶은 `priority` 속성은 생성자 인자를 바탕으로 해당 속성 값을 할당하는 사용자 정의 설정자를 가짐, 이 같은 방식을 지원 속성 기법이라고 함

```kotlin
class Task(val name: String, _priority: Int = DEFAULT_PRIORITY) {

		companion object {
				const val MIN_PRIORITY = 1
				const val MAX_PRIORITY = 5
				const val DEFAULT_PRIORITY = 3
		}

		var priority = validPriority(_priority)
				set(value) { 
						field = validPriority(value)
				}

		// private 검증 함수
		private fun validPriority(p: Int) = p.coerceIn(MIN_PRIORITY, MAX_PRIORITY)
}
```

## 3.5 연산자 중복
- 코틀린의 연산자 중복 매커니즘을 사용해서 `+` 와 `*` 등의 연산자와 연관된 함수를 구현함
- 여러 연산자가 코틀린에서 함수로 구현되어 있고 기호를 사용하면 해당 연산자와 연관된 함수에 처리를 위임함
- 이는 연관 함수를 제공해 클라이언트가 연산자를 사용할 수 있게 한다는 의미임
- 아래와 같이 연산자 재정의를 할 수 있음

```kotlin
data class Point(val x: Int, val y: Int)

operator fun Point.unaryMinus() = Point(-x, -y)

val point = Point(10, 20)

fun main() {
	println(-point) // "Point(x=-10, y=-20)"을 출력
}
```

- 자신이 작성하지 않은 클래스에도 연산자와 연관된 함수를 추가할 수 있음, 예를 들어 아래와 같이 복소수를 표현하는 클래스에 연산까지 재정의 할 수 있음
- 이와 같은 기존 함수에 연산을 위임하는 확장 함수를 추가하면 연산자를 대신 사용할 수 있음

```kotlin
import org.apache.commons.math3.complex.Complex

operator fun Complex.plus(c: Complex) = this.add(c)
operator fun Complex.plus(d: double) = this.add(d)
operator fun Complex.minus(c: Complex) = this.subtract(c)
operator fun Complex.minus(d: Double) = this.subtract(d)
operator fun Complex.div(c: Complex) = this.divide(c)
operator fun Complex.div(d: Double) = this.divide(d)
operator fun Complex.times(c: Complex) = this.mutiply(c)
operator fun Complex.times(d: Double) = this.mutiply(d)
operator fun Complex.times(i: Int) = this.mutiply(i)
operator fun Double.times(c: Complex) = c.mutiply(this)
operator fun Complex.unaryMinus() = this.negate()
```
```kotlin
import org.apache.commons.math3.complex.Complex
import org.apache.commons.math3.complex.Complex.*

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.Math.*

internal class ComplexOverloadOperatorsKtTest {
	private val first = Complex(1.0, 3.0)
	private val second = Complex(2.0, 5.0)

	@Test
	internal fun plus() {
		val sum = first + second
		assertThat(sum, `is`(Complex(3.0, 8.0)))
	}

	@Test
	internal fun minus() {
		val diff = second - first
		assertThat(diff, `is`(Complex(1.0, 2.0)))
	}

	@Test
	internal fun negate() {
		val minus1 = -ONE
		assertThat(minus1.real, closeTo(-1.0, 0.000001))
		assertThat(minus1.imaginary, closeTo(0.0, 0.000001))
	}

	@Test
	internal fun `Euler's formula`() {
		val iPI = I * PI
		assertTrue(Complex.eqauls(iPI.exp(), -ONE, 0.000001))
	}
}
```

- 이처럼 확장 함수에 있는 몇 개의 연산자 중복이 일반 숫자에서 사용하던 연산자를 복소수에서도 사용할 수 있게 해줌

## 3.6 나중 초기화를 위해 lateinit 사용하기
- 생성자에 속성 초기화를 위한 정보가 충분하지 않으면 해당 속성을 널 비허용 속성으로 만들려고 할 때 `lateinit` 변경자를 사용함
- 즉, 모든 객체가 생성될 때까지 의존성 주입이 일어나지 않는 의존성 주입 프레임워크에서 발생하거나 유닛 테스트의 설정 메소드 안에서 발생함
- `lateinit` 변경자는 클래스 몸체에서만 선언되고 사용자 정의 획득자와 설정자가 없는 `var` 속성에서만 사용할 수 있음
- 최상위 속성과 지역 변수에서도 사용이 가능하고 널 할당이 불가능한 타입이어야 하며 기본 타입에는 사용할 수 없음
- `lateinit` 을 추가하면 해당 변수가 처음 사용되기 전에 초기화 할 수 있음
- 만약 사용 전 초기화를 실패하면 예외를 던짐
- 여기서 `isInitialized` 를 사용해서 해당 속성의 초기화 여부를 확인할 수 있음
- `lateinit` 과 `lazy` 의 차이
    - `lateinit` 변경자는 제약사항과 함께 `var` 속성에 사용됨, `lazy` 대리자는 속성에 처음 접근할 때 평가되는 람다를 받음
    - `lazy` 는 또한 `val` 속성에 사용할 수 있고 `lateinit`은 `var` 속성에만 적용함
    - 그리고 `lateinit` 의 경우 객체 바깥쪽에서도 초기화할 수 있음

## 3.7 equals 재정의를 위해 안전 타입 변환, 레퍼런스 동등, 엘비스 사용하기
- 코틀린에서는 `==` 연산자는 자동으로 `equals` 함수를 호출함
- 자바에서 봤듯이 두 객체를 동등하고 판단하면 `equals` 구현 뿐 아니라 `hashCode` 도 잘 구현해야함
- 코틀린은 아래와 같이 `equals` 를 정의함

```kotlin
override fun equals(other: Any?): Boolean {
		if (this === other) return true
		val otherVersion = (other as? KotlinVersion) ?: return false
		return this.version == otherVersion.version
}
```

- 먼저 `===` 으로 레퍼런스 동등성을 확인하고 그 다음 인자를 원하는 타입으로 변환하거나 널을 리턴하는 안전 타입 변환 연산자 `as?` 를 사용함
- 안전 타입 변환 연산자가 널을 리턴하면 같은 클래스의 인스턴스가 아니므로 동등일 수 없기 때문에 엘비스 연산자 `?:` 는 `false` 를 리턴
- 마지막으로 `equals` 함수의 마지막 줄은 현재 인스턴스의 `version` 속성이 `other` 객체의 `version` 속성과의 동등 여부를 검사해 결과를 리턴함
- 이 3줄이 필요한 모든 경우를 다룸
- `hashCode` 구현은 다음과 같이 하면 됨 `override fun hashCode(): Int = version`
- 위와 같이 재정의를 하여서 활용할 수 있음
- 데이터 클래스에는 자동으로 생성된 자신만의 `equals` 와 `hashCode` 구현이 있음

## 3.8 싱글톤 생성하기
- `class` 대신 `object` 키워드를 사용해 특정 클래스의 인스턴스를 오직 하나만 존재하도록 할 수 있음
- 자바에서는 인스턴스와 `private` 설정 등 정적 팩토리 메소드화 하여서 처리를 하지만 코틀린에서는 `object` 키워드만 사용하면 됨 이를 객체 선언이라고 함

```kotlin
object MySingleton {
		val myProperty = 3

		fun myFunction() = "Hello"
}
```

- 코틀린의 `object` 의 멤버 함수와 속성은 필요한 모든 획득자 메소드와 함께 디컴파일된 자바 클래스의 `static final` 메소드와 속성으로 변환되고 이 속성들은 자신의 클래스와 함께 `static` 블록에서 초기화됨
- 코틀린 코드에서는 아래와 같이 해당 멤버를 접근할 수 있음

```kotlin
MySingleton.myFunctio()
MySingleton.myProperty
```

## 3.9 Nothing에 관한 야단법석
- 절대 리턴하지 않는 함수에 `Nothing` 을 사용함
- 결코 존재할 수 없는 값을 나타내기 위해 `Nothing` 을 사용함
- 이 클래스 사용은 2가지 상황에서 발생함, 먼저 함수 몸체가 전적으로 예외를 던지는 코드로 구성된 상황임, 아래 메소드는 결코 리턴하지 않으므로 리턴 타입이 `Nothing` 임

```kotlin
fun doNothing(): Nothing = throw Exception("Nothing at all")
```

- 자바에서는 예외 처리를 위해 메소드의 리턴 타입을 변경할 필요는 없지만 코틀린 타입 시스템은 다름
- 변수에 널을 할당할 때 구체적인 타입을 명시하지 않은 경우 `Nothing` 을 사용할 수 있음

```kotlin
val x = null
```

- 위와 같이 `x` 는 분명히 널 할당이 가능한 타입이고 컴파일러는 `x` 에 대한 다른 정보가 없기 떄문에 추론된 `x` 의 타입은 `Nothing?` 임
- 코틀린에서 `Nothing` 클래스는 실제로 다른 모든 타입의 하위 타입임