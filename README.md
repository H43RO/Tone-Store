# 🎸 Tone Store

일렉기타/베이스 기타 연주자를 위한 톤 세팅 관리 앱

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.09-blue.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

## 📋 소개

Tone Store는 곡마다 다른 이펙터, 앰프, 기타 톤 노브 세팅을 체계적으로 저장하고 관리할 수 있는 앱입니다. 기존의 사진 저장 방식을 대체하여, 디지털 방식으로 톤 세팅을 저장하고 빠르게 조회/편집할 수 있습니다.

## ✨ 주요 기능

### 🎛️ 이펙터 페달보드
- **8가지 프리셋 이펙터**: Overdrive, Distortion, Fuzz, Chorus, Delay, Reverb, Compressor, Wah
- **커스텀 이펙터 생성**: 이름과 노브를 직접 설정 (최대 6개 노브)
- **페달 ON/OFF**: 각 페달별 활성화/비활성화
- **회전 노브 UI**: 실제 이펙터처럼 드래그로 노브 조작 (햅틱 피드백 지원)

### 🔊 앰프 세팅
- Gain, Bass, Middle, Treble, Presence, Reverb, Master Volume
- 앰프 모델명 저장

### 🎸 기타 세팅
- 5-way 픽업 셀렉터 (Neck, Neck+Mid, Mid, Mid+Bridge, Bridge)
- Tone / Volume 노브
- 기타 모델명 저장

### 📱 기타 기능
- 스와이프로 삭제
- 다국어 지원 (한국어/영어)
- 다크 모드 지원

## 🛠️ 기술 스택

| 카테고리 | 기술 |
|---------|------|
| **Language** | Kotlin 2.0.21 |
| **UI** | Jetpack Compose (Material 3) |
| **Architecture** | MVI + Clean Architecture |
| **DI** | Koin 3.5.0 |
| **Database** | Room 2.6.1 |
| **Async** | Coroutines + Flow |
| **Navigation** | Compose Navigation |
| **Serialization** | Gson |
| **Min SDK** | 31 (Android 12) |

## 🏗️ 프로젝트 구조

```
app/src/main/java/com/haero/tonestore/
├── domain/                     # Domain Layer
│   ├── model/                  # 도메인 모델
│   │   ├── ToneSetting.kt      # 톤 세팅 (메인 모델)
│   │   ├── PedalBoard.kt       # 페달보드
│   │   ├── Pedal.kt            # 이펙터 페달
│   │   ├── Knob.kt             # 노브
│   │   ├── AmpSetting.kt       # 앰프 세팅
│   │   ├── GuitarSetting.kt    # 기타 세팅
│   │   ├── PedalType.kt        # 페달 타입 (Preset/Custom)
│   │   └── PickupPosition.kt   # 픽업 포지션
│   ├── repository/             # Repository 인터페이스
│   │   └── ToneSettingRepository.kt
│   └── usecase/                # UseCase
│       ├── GetAllToneSettingsUseCase.kt
│       ├── GetToneSettingByIdUseCase.kt
│       ├── SaveToneSettingUseCase.kt
│       ├── DeleteToneSettingUseCase.kt
│       └── GetPresetPedalsUseCase.kt
│
├── data/                       # Data Layer
│   ├── local/
│   │   ├── database/           # Room Database
│   │   ├── dao/                # DAO
│   │   ├── entity/             # Entity
│   │   └── mapper/             # Entity ↔ Domain 변환
│   ├── repository/             # Repository 구현체
│   └── preset/                 # 프리셋 이펙터 데이터
│
├── di/                         # Koin DI Modules
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   ├── RepositoryModule.kt
│   ├── UseCaseModule.kt
│   └── ViewModelModule.kt
│
├── presentation/               # Presentation Layer
│   ├── ui/
│   │   ├── home/               # 홈 화면
│   │   ├── create/             # 생성/편집 화면
│   │   ├── detail/             # 상세 화면
│   │   └── components/         # 공통 UI 컴포넌트
│   │       ├── RotaryKnob.kt   # 회전 노브 (커스텀)
│   │       └── SectionHeader.kt
│   ├── viewmodel/              # MVI ViewModel
│   └── navigation/             # Navigation Graph
│
├── ui/theme/                   # Material 3 Theme
│   ├── Color.kt                # 커스텀 색상 팔레트
│   ├── Theme.kt
│   └── Type.kt
│
├── MainActivity.kt
└── ToneStoreApp.kt             # Application (Koin 초기화)
```

## 🎨 디자인 컨셉

- **아날로그 감성 + 모던 UI**: 실제 기타 장비의 느낌을 살린 노브 UI
- **다크 모드 기본**: 무대/연습실 환경에 적합
- **컬러 팔레트**:
  - Primary: `#FF6B35` (앰프 LED 연상 오렌지-레드)
  - Secondary: `#004E89` (이펙터 페달 케이스 딥 블루)
  - Background: `#1A1A1D` (다크 모드)


## 📝 향후 개발 예정

- [ ] 검색 기능 (곡 이름으로 검색)
- [ ] 정렬 옵션 (최신순, 이름순, 생성일순)
- [ ] 백업/복원 (JSON 파일로 내보내기/가져오기)
- [ ] 이미지 첨부 (실제 장비 사진)
- [ ] 즐겨찾기 기능
- [ ] 태그 시스템 (장르별: Rock, Jazz, Blues 등)
- [ ] Firebase Analytics / Crashlytics 연동

## 📄 License

MIT License

---

Made with 🎸 by [H43RO](https://github.com/H43RO)
