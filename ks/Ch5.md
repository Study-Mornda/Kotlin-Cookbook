## 5.1 배열 다루기
- 자바에서 배열을 다루는 방법과 코틀린에서 배열을 다루는 방법은 서로 약간 다름
- 자바는 `new` 키워드를 사용해서 배열의 크기를 지정하고 인스턴스화 한다면 코틀린은 배열을 생성하는 `arrayOf` 라는 이름의 간단한 팩토리 메소드를 제공함

```kotlin
val strings = arrayOf("this", "is", "an", "of", "strings")

// 널로만 채워진 배열도 가능(배열에 널만 있어도 특정 타입을 선택해야함)
val nullStringArray = arrayOfNulls<String>(5)
```

- `Array` 클래스는 생성자가 하나만 있는데 두 인 자를 받음 `Int` 타입의 `size` 와 `init` 즉, `(Int) -> T` 타입의 람다
- 이 람다는 배열을 생성할 때 인덱스마다 호출됨

```kotlin
val squares = Array(5) { i -> (i * i).toString() }
```

- 코틀린에는 오토방식과 언방식 비용을 방지할 수 있는 기본 타입을 나타내는 클래스가 있음
- `booleanArrayOf` , `byteArrayOf` , `shortArrayOf` , `charArrayOf` , `intArrayOf` , `longArrayOf` , `floatArrayOf`, `doubleArrayOf`
- 배열의 확장 메소드는 대부분 컬렉션에 있는 이름이 같은 확장 메소드와 동일하게 동작함
- 이 중 두어 개의 고유한 확장함수가 존재함
- `indices` 속성을 활용해 주어진 배열의 적법한 인덱스 값을 알 수 있음

```kotlin
@Test
fun `valid indices`() {
		val strings = arrayOf("this", "is", "an", "array", "of", "strings")
		val indices = strings.indices
		assertThat(indices, contains(0, 1, 2, 3, 4, 5))
}
```

- 배열 순회시 배열의 인덱스 값도 같이 사용하고 싶다면 `withIndex` 함수 사용함
- `IndexedValue` 클래스는 `index` 와 `value` 속성을 가진 데이터 클래스임

```kotlin
@Test
fun `withIndex returns IndexValues`() {
		val strings = arrayOf("this", "is", "an", "array", "of", "strings")
		for ((index, value) in strings.withIndex()) {
				println("Index $index maps to $value")
				assertThat(index in 0..5)
		}
}
```

## 5.2 컬렉션 생성하기
- `listOf` , `setOf` , `mapOf` 처럼 변경 불가능한 컬렉션을 생성하기 위해 만들어진 함수나 `mutableListOf` , `mutableSetOf` , `mutableMapOf` 처럼 변경 가능한 컬렉션을 생성하기 위해 고안된 함수를 통해 컬렉션을 사용함
- 기본적으로 코틀린 컬렉션은 불변임, 그런 의미에서 컬렉션은 원소를 추가하거나 제거하는 메소드를 지원하지 않음, 컬렉션 스스로는 오직 읽기 전용 연산만을 지원함

```kotlin
var numList = listOf(3, 1, 4, 1, 5, 9)
var numSet = setOf(3, 1, 4, 1, 5, 9)
var map = mapOf(1 to "one", 2 to "two", 3 to "three")
```

- 컬렉션을 변경하는 메소드는 아래와 같은 팩토리 메소드에서 제공하는 가변 인터페이스에 들어 있음

```kotlin
var numList = mutableListOf(3, 1, 4, 1, 5, 9)
var numSet = mutableSetOf(3, 1, 4, 1, 5, 9)
var map = mutableMapOf(1 to "one", 2 to "two", 3 to "three")
```

- `mapOf` 함수의 인자는 `Pair` 인스턴스 타입의 가변 인자 리스트임, 따라서 `to` 중위 연산자 함수는 `map` 항목을 생성하는 데 사용됨
- `List` , `Set` , `Map` 인터페이스를 직접 구현한 클래스의 인스턴스도 생성할 수 있음

## 5.3 컬렉션에서 읽기 전용 뷰 생성하기
- `toList` , `toSet` , `toMap` 메소드를 사용해 새로운 읽기 전용 컬렉션을 생성할 수 있음
- 기존 컬렉션을 바탕으로 읽기 전용 뷰를 만들려면 `List` , `Set` , `Map` 타입의 변수에 기존 컬렉션을 할당함
- 아래와 같이 먼저 `List` 타입의 레퍼런스를 리턴하는 `toList` 를 호출해서 변경 가능한 읽기 전용 리스트로 생성 가능

```kotlin
@Test
fun `toList on mutableList makes a readOnly new list`() {
		val readOnlyNumList: List<Int> = mutableNums.toList()
		assertEquals(mutableNums, readOnlyNumList)
		assertNotSame(mutableNums, readOnlyNumList)
}
```

- 내용이 같은 읽기 전용 뷰를 생성하고 싶다면 아래와 같이 `List` 타입의 레퍼런스에 가변 리스트를 할당하면 됨

```kotlin
@Test
internal fun `read-only view of a mutable list`() {
		val readOnlySameList: List<Int> = mutableNums
		assertEquals(mutableNums, readOnlySameList)
		assertSame(mutableNums, readOnlySameList)

		mutableNums.add(2)
		assertEquals(mutableNums, readOnlySameList)
		assertSame(mutableNums, readOnlySameList)
}
```

## 5.4 컬렉션에서 맵 만들기
- 키 리스트가 있을 때 각각의 키와 생성한 값을 연관시켜서 맵을 만드려고 할 때 `associateWith` 함수를 통해 각 키에 대해 실행되는 람다를 제공해 사용할 수 있음

```kotlin
val keys = 'a'..'f'
val map = keys.associate { it to it.toString().repeat(5).capitalize() }
println(map)
```

- 이를 더 간소화한 `associateWith` 을 쓸 수 있음

```kotlin
val keys = 'a'..'f'
val map = keys.associateWith { it.toString().repeat(5).capitalize() }
println(map)
```

## 5.5 컬렉션이 빈 경우 기본값 리턴하기
- 컬렉션이나 문자열이 비어 있는 경우에는 `ifEmpty`와 `ifBlank` 함수를 사용해 기본값을 리턴함
- 상품 데이터 클래스가 있을 때 만약 이 상품이 없는 경우 실행 결과가 비었을 경우 특정 문자열을 리턴하려면 `ifEmpty` 라는 이름의 함수를 컬렉션과 문자열 모두에 사용함

```kotlin
fun onSaleProducts_ifEmptyCollection(products: List<Product>) =
				products.filter { it.onSale }
						.map { it.name }
						.ifEmpty { listOf("none") }
						.joinToString(separator = ", ")

fun onSaleProducts_ifEmptyString(products: List<Product>) =
				products.filter { it.onSale }
						.map { it.name }
						.joinToString(separator = ", ")
						.ifEmpty { "none" }
```

## 5.6 주어진 범위로 값 제한하기
- 값이 주어졌을 때, 주어진 값이 특정 범위 안에 들면 해당 값을 리턴하고 그렇지 않다면 범위의 최솟값 또는 최댓값을 리턴하려고 할 때 `coerceIn` 함수를 범위 인자 또는 구체적인 최솟값, 최댓값과 함께 사용함
- 2개의 중복이 존재하는데 하나는 닫힌 범위를 인자로 받고 다른 하나는 최솟값과 최댓값을 인자로 받음

```kotlin
@Test
fun `coerceIn given a range`() {
		val range = 3..8

		assertThat(5, `is`(5.coerceIn(range)))
		assertThat(range.start, `is`(1.coerceIn(range)))
		assertThat(range.endInclusive, `is`(9.coerceIn(range)))
}
```

- 원하는 최솟값과 최댓값이 있다면 아래와 같이 `coerceIn` 함수를 사용하기 위해 범위를 생성하지 않아도 됨

```kotlin
@Test
fun `coerceIn given min and max`() {
		val min = 2
		val max = 6
		assertThat(5, `is`(5.coerceIn(min, max)))
		assertThat(min, `is`(1.coerceIn(min, max)))
		assertThat(max, `is`(9.coerceIn(min, max)))
}
```

## 5.7 컬렉션을 윈도우로 처리하기
- 정해진 간격으로 컬렉션을 따라 움직이는 블록을 위해서는 `windowed` 함수를 사용함
- 이 함수는 3개의 인자를 받고 그 중 2개의 인자는 선택사항임, `size` 는 각 윈도우에 포함될 원소의 개수, `step` 은 각 단계마다 전진할 원소의 개수(기본 1개), `partialWindows` 는 나뉘어 있는 마짐가 부분이 원도우에 필요한 만큼의 원소 개수를 갖지 못한 경우, 해당 부분을 그대로 유지할지 여부를 알려주는 불리언 값임
- `windowed` 는 정확하게 명시한 크기만큼 매번 진행됨

```kotlin
@Test
fun windowed() {
		val range = 0..10

		assertThat(range.windowed(3, 3),
				contains(listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8)))

		assertThat(range.windowed(3, 3) { it.average() },
				contains(1.0, 4.0, 7.0))

		assertThat(range.windowed(3, 1),
				contains(
						listOf(0, 1, 2), listOf(1, 2, 3), listOf(2, 3, 4),
						listOf(3, 4, 5), listOf(4, 5, 6), listOf(5, 6, 7),
						listOf(6, 7, 8), listOf(7, 8, 9), listOf(8, 9, 10)))

		assertThat(range.windowed(3, 1) { it.average() },
				contains(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)
}
```

- 시계열 데이터를 단계별로 처리하는데 유용하

## 5.8 리스트 구조 분해하기
- 리스트의 원소에 접근할 수 있게 구조 분해를 할 수 있음, 구조 분해는 변수 묶음에 추출한 값을 할당해 객체에서 값을 추출하는 과정임

```kotlin
val list = listOf("a", "b", "c,", "d", "e", "f", "g")
val (a, b, c, d, e) = list
println("$a $b $c $d $e")
```

- 구조 분해는 `componentN` 함수에 의존함, `List` 클래스에 `component1`, `component2`, `component3`, `component4`, `component5` 의 구현이 들어 있기 때문에 위의 예제 코드가 동작한 것임
- 데이터 클래스는 정의된 모든 속성 관련 `component` 메소드를 자동으로 추가함, 필요한 것은 직접 정의할 수 있음

## 5.9 다수의 속성으로 정렬하기
- 클래스를 어떤 속성으로 정렬한 다음, 동일한 값을 다른 속성으로 정렬하는 등, 이처럼 계속해서 클래스를 다수의 속성으로 정렬하려고 할 때 `sortedWith` 와 `comparedBy` 함수를 사용해서 처리할 수 있음

```kotlin
val sorted = golfers.sortedWith(
		compareBy({ it.score }, { it.last }, { it.first })
)

sorted.forEach { println(it) } 
```

- 위와 같이 원하는 속성으로 정렬을 처리할 수 있음
- 여기서 위의 함수가 아닌 `thenBy` 함수를 사용해 직접 `Comparator` 를 생성해서 정렬할 수 있음

```kotlin
val comparator = compareBy<Golfer>(Golfer::score)
		.thenBy(Golfer::last)
		.thenBy(Golfer::first)

golfers.sortedWith(comparator)
		.forEach(::println)
```

## 5.10 사용자 정의 이터레이터 정의하기
- 컬렉션을 감싼 클래스를 손쉽게 순회하기 위해서 `next` 와 `hasNext` 함수를 모두 구현한 이터레이터를 리턴하는 연산자 함수를 정의할 수 있음
- 이를 자바에서 `for-each` 루프를 사용해 `Iterable` 을 구현한 모든 클래스를 순회할 수 있듯이, 코틀린에서  `for-in` 루프를 활용해서 쓸 수 있음

```kotlin
// 팀의 선수 목록 순회
val team = Team("Warriors")
team.addPlayers(Player("Curry"), Player("Thompson"),
		Player("Durant"), Player("Green"), Player("Cousins"))

for (player in team.players) {
		println(player)
}
```

- `iterator` 라는 이름의 연산자 함수를 정의하면 더 간단하게 아래와 같이 쓸 수 있음

```kotlin
operator fun Team.iterator() : Iterator<Player> = players.iterator()

for (player in team) {
		println(player)
}
```

- 여기서 `Team` 클래스를 수정해 다른 방식으로 확장 함수로 쓸 수도 있음

## 5.11 타입으로 컬렉션을 필터링하기
- 여러 타입이 섞여 있는 컬렉션에서 특정 타입의 원소로만 구성된 새 컬렉션을 생성하고 싶을 때 `filterIsInstance` 또는 `filterIsInstanceTo` 확장함수를 사용할 수 있음
- 여기서 원소를 필터링하기 위해서 `filter` 라는 확장 함수를 우선 있음
- 여기서 필터링 연산 동작 외에 좀 더 영리한 타입 변환을 위해서 `is` 확인을 추가하거나 간단하게 `filterIsInstance` 함수를 사용할 수 있음

```kotlin
val list = listOf("a", LocalDate.now(), 3, 1, 4, "b")

val all = list.filterIsInstance<Any>()
val strings = list.filterIsInstance<String>()
val ints = list.filterIsInstance<Int>()
val dates = list.filterIsInstance(LocalDate::class.java)

assertThat(all, `is`(list))
assertThat(strings, containsInAnyOrder("a", "b"))
assertThat(ints, containsInAnyOrder(1, 3, 4))
assertThat(dates, contains(LocalDate.now()))
```

- `filterIsInstanceTo` 함수는 특정 타입의 컬렉션 인자를 받고 그곳에 원본 컬렉션에 존재하는 해당 타입의 원소를 채움, 이 때 함수의 인자는 `MutableCollection<in R>` 이므로 원하는 컬렉션의 타입을 명시해 해당 타입의 인스턴스로 컬렉션을 채울 수 있음

## 5.12 범위를 수열로 만들기
- 범위를 순회하고 싶지만 범위가 간단한 정수 또는 문자로 구성되어 있지 않을 때 사용자 정의 수열을 생성할 수 있음
- 범위가 수열이 아닐 경우가 있음, 수열은 순서 있는 값의 연속임, 이 때 사용자 정의 수열은 `Iterable` 인터페이스를 구현해야함

```kotlin
import java.time.LocalDate

// LocalDate를 위한 수열

class LocalDateProgression(
		override val start: LocalDate,
		override val endInclusive: LocalDate,
		val step: Long = 1
) : Iterable<LocalDate>, ClosedRange<LocalDate> {
		
		override fun iterator(): Iterator<LocalDate> = 
				LocalDateProgressionIterator(start, endInclusive, step)

		infix fun step(days: Long) = LocalDateProgression(start, endInclusive, days)
}
```

```kotlin
import java.time.LocalDate

// LocalDateProgression 클래스를 위한 이터레이터

internal class LocalDateProgressionIterator(
		start: LocalDate,
		val endInclusive: LocalDate,
		val step: Long
) : Iterator<LocalDate> {

		private var current = start

		override fun hasNext() = current <= endInclusive

		override fun next(): LocalDate {
				val next = current
				current = current.plusDays(step)
				return next
		}
}
```

- 위와 같이 `next` 와 `hasNext` 를 재정의해야함 그리고 마지막으로 `LocalDateProgression` 인스턴스를 리턴하도록 확장 함수를 사용해 `rangeTo` 함수를 다시 정의함

```kotlin
operator fun LocalDate.rangeTo(other: LocalDate) =
		LocalDateProgression(this, other)
```

- 위와 같은 작업을 마치면 아래처럼 `LocalDate` 에 대해서 순회 범위를 만드는 데 사용될 수 있음

```kotlin
@Test
fun `use LocalDate as a progression`() {
		val startDate = LocalDate.now()
		val endDate = startDate.plusDays(5)

		val dateRange = startDate..endDate

		dateRange.forEachIndexed { index, localDate ->
				assertEquals(localDate, startDate.plusDays(index.toLong()))
		}

		val dateList = dateRange.map { it.toString() }
		assertEquals(6, dateList.size)
}

@Test
fun `use LocalDate as a progression with a step`() {
		val startDate = LocalDate.now()
		val endDate = startDate.plusDays(5)

		val dateRange = startDate..endDate step 2
		dateRange.forEachIndexed { index, localDate ->
				assertEquals(localDate, startDate.plusDays(index.toLong() * 2))
		}

		val dateList = dateRange.map { it.toString() }
		assertEquals(3, dateList.size)
}
```

- 이 범위의 경우에는 순회를 지원함