- `lazy`, `observable` , `vetoable`, `notNull` 을 통해서 클래스 대리자를 통해 상속을 합성으로 대체할 수 있고, 속성 대리자를 통해 어떤 속성의 획득자와 설정자를 다른 클래스에 있는 속성의 획득자와 설정자로 대체할 수 있음

## 8.1 대리자를 사용해서 합성 구현하기
- 다른 클래스의 인스턴스가 포함된 클래스를 만들고 그 클래스에 연산을 위임하려고 할 때 연산을 위임할 메소드가 포함된 인터페이스를 만들고, 클래스에서 해당 인터페이스를 구현한 다음, `by` 키워드를 사용해 바깥쪽에 래퍼 클래스를 만듬
- `by` 키워드는 포함된 객체에 있는 모든 `public` 함수를 이 객체를 담고 있는 컨테이너를 통해 노출할 수 있음
```kotlin
interface Dialable {
		fun dial(number: String): String
}

class Phone : Dialable {
		override fun dial(number: String) =
				"Dialing $number..."
}

interface Snappable {
		fun takePicture(): String
}

class Camera : Snappable {
		override fun takePicture() = 
				"Taking picture..."
}
```

- 이 인터페이스와 클래스를 아래와 같이 위임하도록 정의할 수 있음

```kotlin
class SmartPhone(
		private val phone: Dialable = Phone(),
		private val camera: Snappable = Camera()
) : Dialable by phone, Snappable by camera
```

- 이렇게 하게 된다면 `SmartPhone` 클래스를 인스턴스화해 `Phone` 또는 `Camera` 의 모든 메소드를 호출할 수 있음

```kotlin
class SmartPhoneTest {
		private val smartPhone: SmartPhone = SmartPhone()

		@Test
		fun `Dialing delegates to internal phone`() {
				assertEquals("Dialing 555-1234...",
						smartPhone.dial("555-1234"))
		}

		@Test
		fun `Taking picture delegates to internal camera`() {
				assertEquals("Taking picture...",
						smartPhone.takePicture())
		}
}
```

- 포함된 객체는 `SmartPhone` 을 통해 노출된 것이 아니라 오직 포함된 객체의 `public` 함수만이 노출됨

## 8.2 lazy 대리자 사용하기
- 어떤 속성이 필요할 때까지 해당 속성의 초기화를 지연시키고 싶을 때 `lazy` 대리자 사용
- 해당 변수에 처음 접근하게 될 때까지 계산하지 않음
- 어떤 객체를 `lazy` 의 `lock` 의 인자로 제공하면 값을 계산할 때 이 객체가 대리자를 동기화함
- `lazy` 대리자는 복잡한 객체를 인스턴스화할 때 적합한데, `lazy` 기본 원리는 모든 경우에 다 동일함

## 8.3 값이 널이 될 수 없게 만들기
- 처음 접근이 일어나기 전에 값이 초기화되지 않았다면 예외를 던지려고 할 때 `notNull` 함수를 이용해, 값이 설정되지 않았다면 예외를 던지는 대리자를 제공함
- 속성 초기화를 지연시키는 한가지의 방법임

## 8.4 observable과 vetoable 대리자 사용하기
- 속성의 변경을 가로채서, 필요에 따라 변경을 거부하고 싶을 때 변경 감지에는 `observable` 함수를 사용하고 변경의 적용 여부를 결정할 때는 `vetoable` 함수와 람다를 사용함

## 8.5 대리자로서 Map 제공하기
- 여러 값이 들어 있는 맵을 제공해 객체를 초기화하려고 할 때, 코틀린 맵에서는 대리자가 되는 데 필요한 `getValue` 와 `setValue` 함수 구현이 있음
- 객체 초기화에 필요한 값이 맵 안에 있다면 해당 클래스 속성을 자동으로 맵에 위임할 수 있음

```kotlin
data class Project(val map: MutableMap<String, Any?>) {
		val name: String by map
		var priority: Int by map
		var completed: Boolean by map
}
```

- 여기서 `MutableMap` 을 인자로서 받고 해당 맵의 키에 해당하는 값으로 `Project` 클래스의 모든 속성을 초기화함, 이 타입의 인스턴스를 생성하려면 아래와 같이 맵이 필요함

```kotlin
@Test
fun `use map delegate for Project`() {
		val project = Project(
				mutableMapOf(
						"name" to "Learn Kotlin",
						"priority" to 5,
						"completed" to true))

		assertAll(
				{ assertEquals("Learn Kotlin", project.name) },
				{ assertEquals(5, project.priority) },
				{ assertTrue(project.completed) }
		)
}
```

- 이는 올바른 시그니처의 확장 함수가 있기 때문에 동작이 가능함

## 8.6 사용자 정의 대리자 만들기
- 어떤 클래스의 속성이 다른 클래스의 획득자와 설정자를 사용하게끔 만들고 싶을 때, `ReadOnlyProperty` , `ReadWriteProperty` 를 구현하는 클래스를 생성함으로써 직접 속성 대리자를 작성함
- 대체로 클래스의 속성은 지원 필드와 함께 동작하지만 필수는 아님, 대신 값을 획득하거나 설정하는 동작을 다른 객체에 위임할 수 있음
- 사용자 정의 속성 대리자를 생성하려면 `ReadOnlyProperty` 또는 `ReadWriteProperty` 인터페이스에 존재하는 함수를 제공해야함
- 대리자를 만들려고 이 2개의 인터페이스를 구현할 필요는 없음, 시그니처와 동일한 `getValue` 와 `setValue` 함수만으로도 충분함