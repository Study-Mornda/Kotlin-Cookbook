- 객체 컨텍스트 안에서 코드 블록을 실행할 목적으로 만든 다수의 함수가 포함됨

## 7.1 apply로 객체 생성 후에 초기화하기
- 객체를 사용하기 전에 생성자 인자만으로는 할 수 없는 초기화 작업에 `apply` 함수를 사용함
- `apply` 함수는 `this` 를 인자로 전달하고 `this` 를 리턴하는 확장 함수임, 명시된 블록을 수신자인 `this` 와 함께 호출하고 해당 블록이 완료되면 `this` 를 리턴함
- 스프링 프레임워크에서 관계형 데이터베이스에 객체를 저장할 때 `apply` 함수를 사용해 `save` 함수는 단 하나의 구문으로 저장해야 할 인스턴스를 받아 새로운 키로 한꺼번에 갱신할 수 있음
```kotlin
@Repository
class JdbcOfficerDAO(private val jdbcTemplate: JdbcTemplate) {
		private val insertOfficer = SimpleJdbcInsert(jdbcTemplate)
				.withTableName("OFFICERS")
				.usingGeneratedKeyColumns("id")

		fun save(officer: Officer) = 
				officer.apply {
						id = insertOfficer.executeAndReturnKey(
								mapOf("rank" to rank,
											"first_name" to first,
											"last_name" to last))
				}

}
```

- 위의 `Officer` 인스턴스는 `this` 로서 `apply` 블록에 전달되기 때문에 블록 안에서 `rank` , `first` , `last` 속성에 접근할 수 있음
- `Officer` 의 `id` 속성은 `apply` 블록 안에서 갱신된 다음 `Officer` 인스턴스가 리턴됨
- 결과가 컨텍스트 객체가 되어야 한다면 `apply` 블록은 유용함

## 7.2 부수 효과를 위해 also 사용하기
- 코드 흐름을 방해하지 않고 메시지를 출력하거나 다른 부수 효과를 생성하려고 할 때 `also` 함수를 사용해 부수 효과를 생성하는 동작을 수행할 수 있음
- `also` 는 모든 제네릭 타입 `T` 에 추가되고 `block` 인자를 실행시킨 후에 자신을 리턴함
```kotlin
val book = createBook()
		.also { println(it) }
		.also { Logger.getAnonymousLogger().info(it.toString()) }
```

- 블록 안에서 객체를 `it` 이라고 언급함, `also` 는 컨텍스트 객체를 리턴하기 때문에 추가 호출을 함께 연쇄시키기에 용이함

## 7.3 let 함수와 엘비스 연산자 사용하기
- 오직 널이 아닌 레퍼런스의 코드 블록을 실행하고 싶지만 레퍼런스가 널이라면 기본값을 리턴하고 싶을 때 엘비스 연산자를 결합한 안전 호출 연산자와 함께 `let` 영역 함수 사용할 수 있음
- `let` 함수는 컨텍스트 객체가 아닌 블록의 결과를 리턴함, `let` 은 객체를 위한 `map` 처럼 마치 컨텍스트 객체의 변형처럼 동작함
```kotlin
fun processString(str: String) = 
		str.let {
				when {
						it.isEmpty() -> "Empty"
						it.isBlank() -> "Blank"
						else -> it.capitalize()
				}
		}
}
```

- 위의 `let` 함수를 통해서 이 함수 내부 블록에서 `when` 조건을 감싸 필요한 모든 경우를 처리하고 변환된 문자열을 돌려줌
- 여기서 널이 될 수 있는 경우를 아래와 같이 처리할 수 있음

```kotlin
fun processString(str: String) = 
		str?.let {
				when {
						it.isEmpty() -> "Empty"
						it.isBlank() -> "Blank"
						else -> it.capitalize()
				}
		}
} ?: "Null"
```

- 위와 같이 `?.` 와 `let` 함수, `?:` 조합을 통해서 모든 경우를 쉽게 처리할 수 있음
- 널이 될 수 있는 경우와 널이 될 수 없는 경우 모두 쉽게 처리할 수 있음

## 7.4 임시 변수로 let 사용하기
- 연산 결과를 임시 변수에 할당하지 않고 처리하고 싶을 때 `let` 을 사용하여 `let` 에 제공한 람다 또는 함수 레퍼런스 안에서 그 결과를 처리함
```kotlin
// 리팩토링 이전
val numbers = mutableListOf("one", "two", "three", "four", "five")
val resultList = numbers.map { it.length }.filter { it > 3 }
println(resultList)
```
```kotlin
// 리팩토링 이후
val numbers = mutableListOf("one", "two", "three", "four", "five")
numbers.map { it.length }.filter { it > 3 }.let {
		println(it)
}
```

- 결과를 임시 변수에 할당하는 대신 연쇄시킨 `let` 호출을 사용하는 목적은 결과 자체를 컨텍스트 변수로서 사용하는 것이기 때문에 `let` 에 제공한 블록 안에서 결과를 출력할 수 있음