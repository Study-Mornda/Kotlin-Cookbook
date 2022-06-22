## 2.1 코틀린에서 널 허용 타입 사용하기
- 코틀린은 가능한 모든 널 값을 제거함, 즉 변수가 절대 널을 갖지 못함
- 물음표를 사용하지 않고 널 허용 타입은 안전 호출 연산자 `?.` 나 엘비스 연산자 `?:` 와 결합해서 사용함

```kotlin
class Person(val first: String,
						 val middle: String?,
						 val last: String)

// 널 값이라도 파라미터 값을 제공하긴 해야함
val jkRowling = Person("Joanne", null, "Rowling")
val northWest = Person("North", null, "West")
```

- 여기서 널 허용성 검사를 한 다음 널이 아니라면 `String?` 타입이 아닌 `String` 타입으로 처리한는 영리한 타입 변환을 수행함, 이는 변수 `p` 가 한 번 설정되면 그 값을 바꿀 수 없는 `val` 키워드로 선언되었기 때문이긴함

```kotlin
val p = Person(first = "North", middle = null, last = "West")

if (p.middle != null) {
		val middleNameLength = p.middle.length
}
```

- 만약 `p` 가 `val` 대신 `var` 키워드로 쓰였다면 위와 같은 영리한 타입 변환이 불가능함
- `p` 가 정의된 시점과 `p` 의 `middle` 속성에 접근하는 시점에 중간에 값이 벼경되었을 수도 있다고 가정하기 때문에 수행하지 않음
- 이를 우회하는 방법은 `!!` 사용하는 것 이 연산자는 널이 아닌 값으로 다뤄지도록 강제하고 해당 변수가 널이라면 예외를 던짐(코틀린에서 널포인트 예외가 나타나는 상황일 수 있어서 되도록 지양할 것)

```kotlin
var p = Person(first = "North", middle = null, last = "West")

if (p.middle != null) {
		// val middleNameLength = p.middle.length
		val middleNameLength = p.middle!!.length
}
```

- 이와 같은 상황에선 안전 호출을 위해서 `?.` 연산자를 사용하면 좋음, 여기서 이 결과 추론 타입도 널 허용 타입이기 때문에 이는 엘비스 연산자 `?:` 를 병행하는게 좋음

```kotlin
var p = Person(first = "North", middle = null, last = "West")
val middleNameLength = p.middle?.length ?: 0 // middle이 null 일 경우 엘비스 연산자는 0을 리턴함

```

- 엘비스 연산자는 자신의 왼쪽에 위치한 식의 값을 확인해서 해당 값이 널이 아니면 그 값을 리턴함, 만약 널이면 자신의 오른쪽에 위치한 값을 돌려줌
- 마지막으로 안전 타입 변환 연산자를 제공함 `as?`
- 이 타입 변환이 올바르게 동작하지 않으면 `ClassCastException` 이 발생하는 상황을 방지할 수 있음
- 아래와 같이 `Person` 타입으로 변환 시도시 해당 인스턴스가 널일 수도 있는 상황이라면 아래와 같이 쓸 수 있음, 실패하여 그 결과가 널이면 널 값을 받음

```kotlin
val p1 = p as? Person
```

-------

## 2.2 자바에 널 허용성 지시자 추가하기
- 컴파일 시간에 타입 시스템에 널 허용성을 강제할 수 있음
- 여기서 일반 `String` 타입은 절대 해당 변수는 널이 될 수 없지만 `String?` 타입은 널이 될 수 있음
- 여기서 이 부분에 있어서 자바에 빌드 파일에 추가하여서 호환성을 강제할 수 있음

-------

## 2.3 자바를 위한 메소드 중복
- 기본 파라미터를 가진 코틀린 함수에서 자바는 각 파라미터의 값을 직접적으로 명시하지 않고 코틀린 함수를 호출하고 싶을 때 활용할 수 있음

```kotlin
fun addProduct(name: String, price: Double = 0.0, desc: String? = null) = 
		"Adding product with $name, ${desc ?: "None" }, and " + 
				NumberFormat.getCurrencyInstance().format(price)
```

- 이 코틀린 함수는 아래와 같이 코트린에서는 여러개 인자와 함께 쉽게 호출할 수 있음

```kotlin
@Test
fun `check all overloads`() {
		assertAll("Overloads called from Kotlin", 
				{ println(addProduct("Name", 5.0, "Desc")) },
				{ println(addProduct("Name", 5.0)) },
				{ println(addProduct("Name")) }
		)
}
```

- 여기서 자바는 메소드 기본 인자를 지원하지 않아서 아래와 같이 모든 인자를 제공해야함

```java
@Test
void supplyAllArguments() {
		System.out.println(OverloadsKt.addProduct("Name", 5.0, "Desc"));
}
```

- 여기서 `@JvmOverloads` 애노 테이션을 `addProduct` 함수에 추가하면 컴파일 후 생성된 클래스는 아래와 같이 함수 중복을 지원할 것임

```java
@Test
void checkOverloads() {
		assertAll("Overloads called from Java", 
				() -> System.out.println(OverloadsKt.addProduct("Name", 5.0, "Desc")),
				() -> System.out.println(OverloadsKt.addProduct("Name", 5.0)),
				() -> System.out.println(OverloadsKt.addProduct("Name"))
		);
}
```

- 여기서 코틀린이 생성한 바이트코드를 디컴파일 하게 되면 `@JvmOverloads` 를 추가한 경우 생성된 클래스에는 제공된 기본 인자와 함께 모든 인자를 요구하는 `addProduct` 메소드를 호출하는 메소드가 추가됨
- 생성자 중복 역시 위와 같은 비슷한 원리로 여러가지 생성자를 만들 때 `@JvmOverloads` 를 통해서 적용해서 처리할 수 있음

-------

## 2.4 명시적으로 타입 변환하기
- 코틀린은 자동으로 기본 타입을 더 넓은 타입으로 `Int` 를 `Long` 으로 승격하지 않음
- 더 작은 타입을 명시적으로 변환하려면 `toInt` `toLong` 등 구체적인 변환 함수를 사용함
- 코틀린에서는 기본 타입을 직접적으로 제공하지 않음, 바이트코드에서는 해당되는 기본 타입을 생성하겠지만 개발자가 코드를 직접 작성할 때는 기본 타입이 아닌 클래스를 다룸
- 그래서 `toInt` `toLong` 같은 형태의 변환 메소드를 제공함

```kotlin
val intVar: Int = 3
// val longVar: Long = intVar, 컴파일 되지 않음
val longVar: Long = intVar.toLong() // 명시적 타입 변환
```

- 사용 가능한 타입 변환 메소드는 다음과 같음

```kotlin
toByte(): Byte

toChar(): Char

toShort(): Short

toInt(): Int

toLong(): Long

toFloat(): Float

toDouble(): Double
```

- 여기서 타입 변환을 투명하게 수행하는 연산자 중복 장점이 있어서 아래의 코드는 명시적 타입 변환이 필요하지 않음
- `val longSum = 3L + intVar` : 더하기 연산자는 자동으로 `intVar` 의 값을 `long` 으로 변환하고 `long` 리터럴에 그 값을 더함

---------

## 2.5 다른 기수로 출력하기
- 십진법이 아닌 다른 기수(base)로 출력
- 올바른 기수를 위해 확장 함수 `toString(radix: Int)` 를 사용함
- 코틀린에서는 자바의 정적 메소드를 사용해서 `Byte` 와 `Short` `Int` `Long` 에 확장함수 `toString(radix: Int)` 을 만듬
- 숫자 42를 이진 문자열로 변환한다면 아래와 같이 해야함

```kotlin
42.toString(2) == "101010"
```

----------

## 2.6 숫자를 거듭제곱하기
- 코틀린은 내장된 거듭제곱 연산자가 없음, 기본타입도 없고 자동으로 클래스 인스턴스가 승격되지도 않음
- `Double`과 `Float`에는 `pow` 확장 함수가 있지만 `Int` 나 `Long` 에는 없기 대문에 변환후 원래 타입으로 아래와 같이 돌려줘야함

```kotlin
@Test
fun `raise an Int to a power`() {
		assertThat(256, equalTo(2.toDouble().pow(8).toInt())
} 
```

- 이를 직접 확장함수로 만들어 정의해서 처리 과정을 자동화 할 수 있음

```kotlin
fun Int.pow(x: Int) = toDouble().pow(x).toInt()
```

- 여기서 `infix` 연산자를 통해서 미리 정의된 연산자 심볼이 아닌 백틱으로 감싸 가상의 연산자를 만들어 아래와 같이 거듭제곱 연산을 처리함


```kotlin
import kotlin.math.pow

infix fun Int.`**`(x: Int) = toDouble().pow(x).toInt()
infix fun Long.`**`(x: Int) = toDouble().pow(x).toLong()
infix fun Float.`**`(x: Int) = pow(x)
infix fun Double.`**`(x: Int) = pow(x)

// Float과 Double에 존재하는 함수와 비슷한 패턴
fun Int.pow(x: Int) = `**`(x)
fun Long.pow(x: Int) = `**`(x)
```

- `infix` 키워드를 `**` 함수 정의에 사용하고 `Int` `Long` 확장 함수에는 `infix` 키워드를 쓰지 않고 처리함, 이제 이를 거듭제곱 연산처럼 사용할 수 있음
- 이렇게 백틱으로 쓴 가상 연산자가 마음에 들지 않으면 실질적인 이름으로 함수를 정의해도 됨

---------

## 2.7 비트 시프트 연산자 사용하기
- 코틀린에는 비트 시프트를 위한 `shr` `shl` `ushr` 같은 비트 중위 연산자가 있음
- 코틀린은 시프트 연산을 위해 특정 연산자 심볼을 사용하지는 않지만 대신 시프트 연산을 위한 함수를 정의해놓음
- `shl` : 부호 있는 왼쪽 시프트
- `shr` : 부호 있는 오른쪽 시프트
- `ushr` : 부호 없는 오른쪽 시프트
- 2의 보수 연산, 즉 비트를 왼쪽 또는 오른쪽으로 시프트하는 것은 2를 곱하거나 나눈 것과 같음

```kotlin
@Test
fun `doubling and halving`() {
		assertAll("left shifts doubling from 1", // 0000_0001
				{ assertThat(  2, equalTo(1 shl 1)) }, // 0000_0010
				{ assertThat(  4, equalTo(1 shl 2)) }, // 0000_0100
				{ assertThat(  8, equalTo(1 shl 3)) }, // 0000_1000
				{ assertThat( 16, equalTo(1 shl 4)) }, // 0001_0000
				{ assertThat( 32, equalTo(1 shl 5)) }, // 0010_0000
				{ assertThat( 64, equalTo(1 shl 6)) }, // 0100_0000
				{ assertThat(128, equalTo(1 shl 7)) }  // 1000_0000
	)

		assertAll("right shifts halving from 235", // 1110_1011
				{ assertThat(117, equalTo(235 shr 1)) }, // 0111_0101
				{ assertThat( 58, equalTo(235 shr 2)) }, // 0011_1010
				{ assertThat( 29, equalTo(235 shr 3)) }, // 0001_1101
				{ assertThat( 14, equalTo(235 shr 4)) }, // 0000_1110
				{ assertThat(  7, equalTo(235 shr 5)) }, // 0000_0111
				{ assertThat(  3, equalTo(235 shr 6)) }, // 0000_0011
				{ assertThat(  1, equalTo(235 shr 7)) }, // 0000_0001
	)
}
```

- 부호를 보존하지 않고 값을 시프트하려는 경우 `ushr` 함수를 사용하면 됨
- `shr` 과 `ushr` 함수는 양수인 경우에는 똑같은 동작을 하지만, 음수인 경우에는 `shr` 이 왼쪽에서부터 1을 채우기 때문에 결과는 여전히 음수임

```kotlin
val n1 = 5
val n2 = -5
println(n1.toString(2)) //  0b0101
println(n2.toString(2)) // -0b0101

assertThat(n1  shr 1, equalTo(0b0010)) // 2
assertThat(n1 ushr 1, equalTo(0b0010)) // 2

assertThat(n2  shr 1, equalTo(-0b0011)) // -3
assertThat(n2 ushr 1, equalTo(0x7fff_fffd)) // 2_147_483_645
```

- 이 `ushr` 의 경우 평균 값 계산이 원하는 경계에 위치하게 할 수 있기 때문에 중간 값을 찾는 경우 자주 씀

## 2.8 비트 불리언 연산자 사용하기
- 비트 값에 마스크 적용, 비트 연산자 사용
- 마스킹 연산자 and, or, xor, (not 대신) inv도 정의됨
- `inv` 함수는 숫자의 모든 비트를 뒤집음

```kotlin
// 4 == 0b0000_0100(이진수)
// 주어진 비트 보수(모든 비트를 뒤집는다):
// 0b1111_1011 == 251 (십진수)
assertEquals(-5, 4.inv())
```

- 251에서 -5를 얻은 것은 시스템은 2의 보수연산을 하고 있기 때문임
- 모든 정수 n의 2의 보수는 -(~n + 1)임, ~n의 n의 (모든 비트를 뒤집은) 1의 보수임, 그래서 아래와 같이 계산됨

`0b1111_1011 -> -(0b0000_0100 + 1) -> -0b0000_0101 -> -5`

- `and`, `or`, `xor` 의 경우 아래와 같이 쓸 수 있음

```kotlin
@Test
fun `and, or, xor`() {
	val n1 = 0b0000_1100 // 십진수 12
	val n2 = 0b0001_1001 // 십진수 25

	val n1_and_n2 = n1 and n2
	val n1_or_n2 = n1 or n2
	val n1_xor_n2 = n1 xor n2

	assertThat(n1_and_n2, equalTo(0b0000_1000)) // 8
	assertThat(n1_or_n2, equalTo(0b0001_1101)) // 29
	assertThat(n1_xor_n2, equalTo(0b0001_0101)) // 21
}
```

-------

## 2.9 to로 Pair 인스턴스 생성하기
- 보통 map의 항목으로서 `Pair` 클래스의 인스턴스를 생성하는데 이를 중위 `to` 함수를 사용해서 만듬, 맵은 키와 값이 결합된 항목으로 구성됨
- 코틀린은 `Pair` 인스턴스의 리스트로부터 맵을 생성하는 `mapOf` 와 같은 맵 생성을 위한 최상위 함수를 몇 가지 제공함
- 여기서 `to` 함수 구현은 `Pair` 클래스의 인스턴스를 생성하는 것임, 이러한 기능을 하나로 모아 아래와 같이 `to` 함수로 생성된 `pair` 를 사용해서 `map` 을  생성하는 방법을 보여줌

```kotlin
@Test
fun `create map using infix to function`() {
		val map = mapOf("a" to 1, "b" to 2, "c" to 2)
		assertAll(
				{ assertThat(map, hasKey("a")) },
				{ assertThat(map, hasKey("b")) },
				{ assertThat(map, hasKey("c")) },
				{ assertThat(map, hasValue(1)) },
				{ assertThat(map, hasValue(2)) }
		}
}

@Test
fun `create a Pair from constructor vs to function`() {
		val p1 = Pair("a", 1)
		val p2 = "a" to 1
		
		assertAll(
				{ assertThat(p1.first, `is`("a")) },
				{ assertThat(p1.second, `is`(1)) },
				{ assertThat(p2.first, `is`("a")) },
				{ assertThat(p2.second, `is`(1)) },
				{ assertThat(p1, `is`(equalTo(p2))) }
		}
}
		
```

- 위와 같이 `to` 를 사용해서 `Pair` 를 생성할 수 있고 생성자를 통해서도 `Pair` 를 생성할 수 있음
- 이 `to` 함수는 더 적고 쉬운 코드를 사용해서 맵 리터럴을 생성하는 방법임, 하지만 `Pair` 는 데이터 클래스이므로 아래처럼 구조 분해를 통해서만 개별 원소에 접근할 수 있음

```kotlin
@Test
fun `destructuring a Pair`() {
		val pair = "a" to 1
		val (x,y) = pair

		assertThat(x, `is`("a"))
		assertThat(y, `is`(1))
}
```