# AGENTS.md - ToneStore Project Guide

> Agentic coding guide for ToneStore - Guitar/Bass tone settings management Android app

## Project Overview

- **Language**: Kotlin 2.0.21
- **UI**: Jetpack Compose (Material 3) with Compose BOM 2024.09
- **Architecture**: MVI + Clean Architecture (domain/data/presentation layers)
- **DI**: Koin 3.5.0
- **Database**: Room 2.6.1 with KSP
- **Async**: Coroutines + Flow
- **Navigation**: Compose Navigation
- **Min SDK**: 31 (Android 12) | **Target/Compile SDK**: 35

## Build & Run Commands

```bash
# Build
./gradlew assembleDebug                    # Debug APK
./gradlew assembleRelease                  # Release APK
./gradlew build                            # Full build with tests

# Lint & Format
./gradlew ktlintCheck                      # Check code style
./gradlew ktlintFormat                     # Auto-fix code style issues

# Tests
./gradlew test                             # All unit tests
./gradlew testDebugUnitTest                # Debug unit tests only
./gradlew test --tests "*.ExampleUnitTest" # Single test class
./gradlew test --tests "*.ExampleUnitTest.addition_isCorrect"  # Single test method

# Android Instrumentation Tests
./gradlew connectedAndroidTest             # Run on connected device/emulator

# Clean
./gradlew clean                            # Clean build artifacts
```

## Project Structure

```
app/src/main/java/com/haero/tonestore/
├── domain/                     # Domain Layer (pure Kotlin)
│   ├── model/                  # Domain models (ToneSetting, Pedal, Knob, etc.)
│   ├── repository/             # Repository interfaces
│   └── usecase/                # Use cases (single-responsibility)
├── data/                       # Data Layer
│   ├── local/
│   │   ├── database/           # Room Database + migrations
│   │   ├── dao/                # DAO interfaces
│   │   ├── entity/             # Room entities
│   │   └── mapper/             # Entity <-> Domain mappers
│   ├── repository/             # Repository implementations
│   └── preset/                 # Preset data
├── presentation/               # Presentation Layer
│   ├── ui/
│   │   ├── home/               # HomeScreen + HomeIntent + HomeState
│   │   ├── create/             # CreateToneScreen + intent/state
│   │   ├── detail/             # DetailScreen + intent/state
│   │   ├── pedalboard/         # PedalBoard screens + components
│   │   └── components/         # Shared UI components
│   ├── viewmodel/              # MVI ViewModels
│   └── navigation/             # NavGraph.kt
├── di/                         # Koin DI modules
├── ui/theme/                   # Material 3 theme (Color, Type, Theme)
├── MainActivity.kt
└── ToneStoreApp.kt             # Application class (Koin init)
```

## Code Style Guidelines

### Imports
- Group imports: Android/Androidx, third-party (Koin, Room), project imports
- No wildcard imports (enforced by ktlint)
- Use explicit imports for Compose components

### Formatting (ktlint enforced)
- 4-space indentation
- Max line length: 120 characters (soft limit)
- Trailing commas in multi-line parameter lists
- No trailing whitespace

### Naming Conventions
| Type | Convention | Example |
|------|------------|---------|
| Classes/Interfaces | PascalCase | `HomeViewModel`, `ToneSettingRepository` |
| Functions | camelCase | `getAllToneSettings()`, `handleIntent()` |
| Properties/Variables | camelCase | `isLoading`, `toneSettings` |
| Constants | SCREAMING_SNAKE_CASE | `MIGRATION_1_2` |
| Composables | PascalCase | `HomeScreen`, `RotaryKnob` |
| State classes | PascalCase + "State" suffix | `HomeState`, `CreateToneState` |
| Intent classes | PascalCase + "Intent" suffix | `HomeIntent`, `DetailIntent` |
| Use cases | Verb + Noun + "UseCase" | `GetAllToneSettingsUseCase` |

### MVI Pattern
Each screen follows MVI pattern:
```kotlin
// Intent - sealed interface for user actions
sealed interface HomeIntent {
    data object LoadToneSettings : HomeIntent
    data class SelectToneSetting(val id: String) : HomeIntent
}

// State - data class with default values
data class HomeState(
    val isLoading: Boolean = true,
    val toneSettings: List<ToneSetting> = emptyList(),
    val error: String? = null
)

// ViewModel - exposes StateFlow, handles intents
class HomeViewModel(...) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadToneSettings -> loadToneSettings()
            // ...
        }
    }
}
```

### Use Cases
- Single responsibility - one public `invoke` method
- Use `operator fun invoke()` for callable syntax
- Inject repository via constructor

```kotlin
class GetAllToneSettingsUseCase(
    private val repository: ToneSettingRepository
) {
    operator fun invoke(): Flow<List<ToneSetting>> {
        return repository.getAllToneSettings()
    }
}
```

### Repository Pattern
- Interface in `domain/repository/`
- Implementation in `data/repository/`
- Use mappers for Entity <-> Domain conversion

### Room Database
- Entities use JSON serialization for complex nested objects (Gson)
- Migrations defined as constants in Database class
- DAOs return `Flow<T>` for reactive queries

### Compose UI
- Use `collectAsStateWithLifecycle()` for state collection
- Screens receive ViewModel via `koinViewModel()`
- Private composables for internal components
- Use `stringResource()` for all user-visible text
- Preview functions with `@Preview` annotation

```kotlin
@Composable
fun HomeScreen(
    onNavigateToCreate: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    // ...
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    ToneStoreTheme {
        // ...
    }
}
```

### Koin DI
- Modules defined in `di/` package
- ViewModels registered with `viewModel { }` DSL
- Use `get()` for dependency injection in module definitions

```kotlin
val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get()) }
}
```

### Error Handling
- Use `runCatching` for suspend functions that may fail
- Use `catch` operator for Flow error handling
- Update state with error message for UI display

```kotlin
viewModelScope.launch {
    runCatching {
        deleteToneSettingUseCase(id)
    }.onFailure { e ->
        _state.update { it.copy(error = e.message) }
    }
}
```

### Testing
- Unit tests: JUnit 4 + MockK + coroutines-test
- UI tests: Compose UI test + Espresso
- Test file location: `app/src/test/` (unit), `app/src/androidTest/` (instrumented)

```kotlin
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}
```

### Git Commit Guidelines

**커밋 메세지는 한국어로 작성**

형식: `type(scope): 한국어 설명`

| Type | 용도 | 예시 |
|------|------|------|
| `feat` | 새로운 기능 추가 | `feat(home): 홈 화면에 검색 기능 추가` |
| `fix` | 버그 수정 | `fix(pedal): 노브 값 저장 안 되는 문제 수정` |
| `refactor` | 리팩토링 (기능 변화 없음) | `refactor(data): Repository 패턴 적용` |
| `style` | 코드 포맷팅, 세미콜론 등 | `style(ui): ktlint 포맷 적용` |
| `docs` | 문서 수정 | `docs(readme): 설치 방법 업데이트` |
| `test` | 테스트 코드 | `test(usecase): GetAllToneSettingsUseCase 테스트 추가` |
| `chore` | 빌드, 설정 등 | `chore(gradle): 의존성 버전 업데이트` |

**규칙**:
- scope는 영어로 유지 (패키지/모듈명)
- 설명은 한국어로 작성
- 마침표 없이 작성
- 명령형으로 작성 ("추가", "수정", "제거" 등)

## Adding New Features

### New Screen Checklist
1. Create `{Feature}Intent.kt` - sealed interface for intents
2. Create `{Feature}State.kt` - data class for state
3. Create `{Feature}ViewModel.kt` - ViewModel handling intents
4. Create `{Feature}Screen.kt` - Composable UI
5. Register ViewModel in `di/ViewModelModule.kt`
6. Add route in `presentation/navigation/NavGraph.kt`

### New Use Case Checklist
1. Create `{Verb}{Noun}UseCase.kt` in `domain/usecase/`
2. Add repository method if needed (interface + implementation)
3. Register in `di/UseCaseModule.kt`

### Database Migration
1. Increment database version in `ToneStoreDatabase`
2. Add migration constant: `val MIGRATION_X_Y = Migration(x, y) { ... }`
3. Add migration to database builder

## Dependencies (libs.versions.toml)

Dependencies are managed via Gradle Version Catalog. Reference via `libs.*` aliases.

Key dependencies:
- `libs.androidx.compose.bom` - Compose BOM (version alignment)
- `libs.koin.android`, `libs.koin.androidx.compose` - DI
- `libs.room.runtime`, `libs.room.ktx`, `libs.room.compiler` (ksp) - Database
- `libs.navigation.compose` - Navigation
- `libs.gson` - JSON serialization
- `libs.mockk`, `libs.coroutines.test` - Testing
