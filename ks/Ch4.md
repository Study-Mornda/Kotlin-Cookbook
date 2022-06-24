- 함수형 프로그래밍은 불변성을 선호하고, 순수 함수를 사용하는 경우에 동시성을 쉽게 구현할 수 있으며, 반복보다는 변형을 사용하고, 조건문보다는 필터를 사용하는 코딩 스타일을 말함

## 4.1 알고리즘에서 fold 사용하기
- 반복 알고리즘을 함수형 방식으로 구현하고자 할 때 `fold` 함수를 사용해 시퀀스나 컬렉션을 하나의 값으로 축약시킴
- `fold` 함수는 배열 또는 반복 가능한 컬렉션에 적용할 수 있는 축약 연산임
- 명시적 타입 배열에 정의돼 있고 2개의 인자를 받는데 첫 번째는 누적자의 초기값이며 두 번째는 두 개의 인자를 받아 누적자를 위해 새로운 값을 리턴하는 함수임

```kotlin
fun sum(vararg nums: Int) = nums.fold(0) { acc, n -> acc + n }
```

- 위의 예제는 초기값은 0이고 2개의 인자를 받는 람다 함수를 제공함, 람다 함수의 첫 번째 인자는 누적에 사용되는 값이며 두 번째 인자는 `num` 리스트의 각각의 값을 순회하며 첫 번째 인자인 누적 값에 순회 중인 값 n을 더하는 함수임
```kotlin
fun sumWithTrace(vararg nums: Int) =
		nums.fold(0) { acc, n ->
				println("acc = $acc, n = $n")
				acc + n
		}
```

- 이 테스트 호출한 결과는 아래와 같음

```kotlin
acc = 0, n = 3
acc = 3, n = 1
acc = 4, n = 4
acc = 8, n = 1
acc = 9, n = 5
acc = 14, n = 9
```

- `acc` 변수는 `fold` 의 첫 번째 인자 값으로 초기화되고, `n` 변수는 `nums` 컬렉션의 각 원소를 받고, 람다 결과, 즉 `acc + n` 이 매 반복의 새로운 `acc` 값이 됨
- 이 `fold` 를 활용해 팩토리얼 연산의 재귀 연산 역시 아래와 같이 구현할 수 있음

```kotlin
fun factorialFold(n: Long): BigInteger = 
		when(n) {
				0L, 1L -> BigInteger.ONE
				else -> (2..n).fold(BigInteger.ONE) { acc, i ->
						acc * BigInteger.valueOf(i) }
}
```

- `fold` 연산을 적용하여 람다 안에서 누적 값은 이전 누적 값과 순회하는 각 원소 값의 곱으로 처리함
- 이를 피보나치 수 계산에도 적용할 수 있음

```kotlin
fun fibonacciFold(n: Int) =
		(2 until n).fold(1 to 1) { (prev, curr), _ ->
				curr to (prev + curr) }.second
```

- 위의 누적 값의 초기값은 `Pair` 임, 이 `Pair` 의 `first` , `second` 값은 모두 1임
- 그 다음 람다는 계산중인 인덱스를 고려하지 않고 누적 값을 위한 새로운 값을 생성할 수 있음, 그래서 `_` 를 사용함
- 이 람다는 현재 값과 이전 값을 할당해 새로운 `Pair` 를 생성하고 이전 값과 현재 값의 합과 동일한 `curr` 값을 만듬
- 두 번째 인덱스에서부터 계산하려고 하는 인덱스까지 반복됨, 결국 출력 값은 마지막 `Pair` 의 `second` 속성 값임
- 위의 예제는 원소는 `Int` 값인 반면 누적 값 타입은 `Pair` 임

## 4.2 reduce 함수를 사용해 축약하기
- 비어 있지 않는 컬렉션의 값을 축약하고 싶지만 누적자의 초기값을 설정하고 싶지 않을 때 `reduce` 를 사용함
- `reduce` 함수는 `fold` 함수랑 거의 같고 사용 목적도 같음, 다만 누적자의 초기값 인자가 없다는 것이 큰 차이점임
- `reduce` 함수는 누적자를 컬렉션의 첫 번째 값으로 초기화 할 수 있는 경우에만 사용할 수 있음

```kotlin
fun sumReduce(vararg nums: Int) =
		nums.reduce { acc, i -> acc + i }
```

- 여기서 주의할 점은, 컬렉션의 첫 번째 값으로 누적자를 초기화하고 컬렉션의 다른 값에 추가 연산을 필요로 하지 않은 경우에만 사용하는게 좋음

## 4.3 꼬리 재귀 사용하기
- 재귀 프로세스를 실행하는 데 필요한 메모리를 최소화 하기 위해 꼬리 재귀를 사용함
- 재귀 프로세스가 스택 크기 제한에 다다르게 되면 `StackOverflowError` 가 발생함
- 여기서 꼬리 재귀로 알려진 접근법은 콜 스택에 새 스택 프레임을 추가하지 않게 구현하는 특별한 종류의 재귀임
- 먼저 일반적으로 아래와 같이 재귀를 구현하는게 기본임

```kotlin
fun recursiveFactorial(n: Long): BigInteger = 
		when (n) {
				0L, 1L -> BigInteger.ONE
				else -> BigInteger.valueOf(n) * recursiveFactorial(n - 1)
		}
```

- 그리고 이를 꼬리 재귀에 적합하게 바꾸면 아래와 같음

```kotlin
@JvmOverloads
tailrec fun factorial(n: Long, acc: BigInteger = BigInteger.ONE): BigInteger =
		when (n) {
				0L -> BigInteger.ONE
				1L -> acc
				else -> factorial(n - 1, acc * BigInteger.valueOf(n))
		}
```

- 여기서 팩토리얼 연산의 누적자 역할을 하는 두 번째 인자가 필요함, 마지막 평가 식은 더 작은 수와 증가된 누적자를 이용해 자신 스스로를 호출할 수 있음
- 두 번째 인자는 기본값 `[BigInteger.ONE](http://BigInteger.ONE)` 이 할당되므로 두 번째 인자 없이 팩토리얼 함수를 호출할 수 있음, 어노테이션을 추가해도 두 번째 인자 없이 호출되는 경우에도 잘 동작함
- `tailrec` 키워드는 함수는 빠르고 효율적으로 반복함, 컴파일러에게 해당 재귀 호출을 최적화해야 한다고 알려주는 키워드임
- 이 `tailrec` 변경자는 아래와 같은 요건이 필요함
    - 해당 함수는 반드시 수행하는 마지막 연산으로서 자신을 호출해야함
    - `try/catch/finally` 블록 안에서는 `tailrec` 을 사용할 수 없음
    - 오직 JVM 백엔드에서만 꼬리 재귀가 지원됨