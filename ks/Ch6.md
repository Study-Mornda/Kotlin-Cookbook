- 코틀린 시퀀스는 자바 스트림과 비슷함
- 컬렉션에서 처리는 즉시 발생함, 즉 컬렉션의 `map` 이나 `filter` 가 호출될 때 컬렉션의 모든 원소는 즉시 처리됨
- 반면에 시퀀스는 지연 처리됨, 데이터를 처리하기 위해 시퀀스를 사용하면 각각의 원소는 자신의 다음 원소가 처리되기 전에 전체 파이프라인을 완료함
- 지연 처리 방식은 데이터가 많거나 `first` 같은 쇼트 서킷 연산의 경우에 도움이 되고 원하는 값을 찾았을 때 시퀀스를 종료할 수 있게 도와줌

## 6.1 지연 시퀀스 사용하기
- 특정 조건을 만족시키는 데 필요한 최소량의 데이터만 처리하고 싶을 때 코틀린 시퀀스를 쇼트 서킷 함수와 함께 사용하여 처리할 수 있음
- 만약 3으로 나누어지는 첫 번째 배수를 찾는다고 할 때 1차원적으로 아래와 같이 짤 수 있음
- 하지만 아래와 같은 방식은 모든 숫자를 2배로 만들고 100개의 숫자에 나머지 연산을 수행한 후 그저 첫 번째 원소를 선택함

```kotlin
(100 until 200).map { it * 2 }
		.filter { it % 3 == 0 }
		.first()
```

- 위 방식을 말고 서술 조건자 즉 컬렉션의 원소를 인자로 받고 불리언을 리턴하는 람다를 활용해 `first` 함수 중복을 쓸 수 있음
- 이렇게 하면 루프를 사용해도 첫번째 원소를 발견하는 순간 진행을 멈춤
- 특정 조건에 다다를 때까지 오직 필요한 데이터만을 처리하는 방식을 쇼트 서킷이라고함

```kotlin
(100 until 200).map { it * 2 }
		.first { it % 3 == 0 }
```

- 코틀린 시퀀스는 데이터를 다른 방식으로 처리함

```kotlin
(100 until 2_000_000).asSequence()
		.map { println("doubling $it"); it * 2 }
		.filter { println("filtering $it"); it % 3 == 0 }
		.first()

/**
doubling 100
filtering 200
doubling 101
filtering 202
doubling 102
filtering 204
*/
```

- 위와 같은 방식은 오직 6개의 연산만을 수행함, 어떤 방법을 쓰더라도 시퀀스의 각 원소는 다음 원소로 진행하기 전에 완전한 파이프라인에서 처리되기 떄문에 오직 6개의 연산만이 수행됨
- 시퀀스 API는 컬렉션에 들어 있는 함수와 똑같은 함수를 가지고 있지만 시퀀스에 대한 연산은 중간 연산과 최종 연산이라는 범주로 나뉨
- 중요한 점은 최종 연산 없이는 시퀀스가 데이터를 처리하지 않음

## 6.2 시퀀스 생성하기
- 값으로 이뤄진 시퀀스를 생성하려고 할 때, 이미 원소가 있다면 `sequenceOf` 를 사용하고 `Iterable` 이 있다면 `asSequence` 를 사용함, 그 외의 경우에는 시퀀스 생성기를 사용함

```kotlin
val numSequence1 = sequenceOf(3, 1, 4, 1, 5, 9)
val numSequence2 = listOf(3, 1, 4, 1, 5, 9).asSequence()
```

- 만약 어떤 정수가 있고 해당 정수의 다음에 나오는 소수를 알고 싶을 때 다음 소수를 찾기 위해서 얼마나 많은 수를 확인해야하는지 알 수가 없는데 이때, 시퀀스를 사용할 수 있음
- 먼저 아래와 같이 주어진 수가 2인지 확인하고 2가 아니면 2부터 해당 수의 제곱근 값을 반올림한 수까지를 범위로 생성하는 확장함수가 있고 이를 시퀀스를 활용하여 위에서 설명한 바와 같이 소수를 찾는 것을 할 수 있음

```kotlin
import kotlin.math.ceil
import kotlin.math.sqrt

fun Int.isPrime() = 
		this == 2 || (2..ceil(sqrt(this.toDouble())).toInt())
		.none { divisor -> this % divisor == 0 }
```

```kotlin
fun nextPrime(num: Int) =
		generateSequence(num + 1) { it + 1 }
				.first(Int::isPrime)
```

- 위 예제에선 `nextPrime` 함수는 시퀀스 안에서 무한대의 정수를 생성하고 첫 번째 소수를 찾을 때까지 생성한 정수를 하나씩 평가함, `first` 함수는 시퀀스 대신 하나의 값을 리턴하기 때문에 이는 최종 연산임, 그래서 이 시퀀스는 서술 조건자를 만족할 때까지 계속해서 정수를 생성함

## 6.3 무한 시퀀스 다루기
- 무한대의 원소를 갖는 시퀀스의 일부분이 필요할 때, 널을 리턴하는 시퀀스 생성기를 사용하거나 시퀀스 확장 함수 중에서 `takeWhile` 같은 함수를 사용함
- 앞서 본 N개의 소수찾기에 대해서 무한대의 원소를 갖게 되는데 이 때 무한대의 원소를 갖는 시퀀스를 잘라낼 때 마지막에 널을 리턴하는 함수를 아래와 같이 사용할 수 있음
- 그렇게 된다면 처음 N개의 소수를 찾는 작업 대신 특정 한계보다 작은 모든 소수를 찾을 수 있음
- 다음 소수가 한계 값보다 큰 값인지 여부를 미리 알 수 있는 방법은 없지만, 이 함수는 실제로 한계 값을 넘어가는 소수를 하나 포함하는 리스트를 생성함, 그런 다음 `dropLast` 함수는 생성된 결과 리스트에서 한계 값을 넘는 소수를 리턴하기 전에 잘라냄

```kotlin
fun primesLessThan(max: Int): List<Int> = 
		generateSequence(2) { n -> if (n < max) nextPrime(n) else null }
				.toList()
				.dropLast(1)
```

- 혹은 `takeWhile` 을 사용하여 처리할 수 있음, 이 함수는 시퀀스에서 제공된 술어가 `true` 를 리턴하는 동안 시퀀스에서 값을 추출함

```kotlin
fun primesLessThan(max: Int): List<Int> = 
		generateSequence(2, ::nextPrime)
				.takeWhile { it < max }
				.toList()
				
```

- 위 2가지 방법 중 어떤 방법을 사용할지는 개인의 선호도 문제임

## 6.4 시퀀스에서 yield하기
- 구간을 지정해 시퀀스에서 값을 생성하고 싶을 때 `yield` 중단 함수와 함께 `sequence` 함수를 사용함
- `yield` 함수는 이터레이터에 값을 제공하고 다음 값을 요청할 때까지 값 생성을 중단함, 따라서 `yield` 는 `suspend` 함수가 생성한 시퀀스 안에서 각각의 값을 출력하는데 사용됨

```kotlin
fun fibonacciSequence() = sequence {
		var terms = Pair(0, 1)

		while (true) {
					yield(terms.first)
					terms = terms.second to terms.first + terms.second
		}
}
```

- `yieldAll` 의 경우 다수의 값을 이터레이터에 넘겨줌

```kotlin
val sequence = sequence {
		val start = 0
		yield(start)
		yieldAll(1..5 step 2)
		yieldAll(generateSequence(8) { it * 3 })
}
```

- 중단 함수 안의 `yield` 와 `yieldAll` 의 조합을 사용하면 시퀀스에 생성된 값을 원하는 조합으로 쉽게 바꿀 수 있음