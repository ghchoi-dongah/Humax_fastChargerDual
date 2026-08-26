# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

Humax 듀얼채널 EV 급속충전기의 Android HMI(Human Machine Interface) 앱. 화면 좌우에 2개의 충전 채널을 독립적으로 표시하며, OCPP 1.6 프로토콜로 충전소 관리 서버와 통신한다.

## 빌드 명령어

```bat
# 디버그 APK 빌드
gradlew.bat assembleDebug

# 릴리즈 APK 빌드 (ProGuard 난독화 포함)
gradlew.bat assembleRelease

# 로컬 단위 테스트
gradlew.bat test

# 기기 연결 테스트 (에뮬레이터/실제 기기 필요)
gradlew.bat connectedAndroidTest
```

- NDK 26.1.10909125 설치 필요 (JNI 시리얼포트 라이브러리)
- 서명 키스토어: `D:\AndroidDongah\JKS_hola\platform.jks`
- JVM 메모리: `-Xmx2048m` (gradle.properties에 설정됨)

## 아키텍처

### 단일 액티비티 + 다중 프래그먼트 구조

`MainActivity`가 진입점이며 모든 UI는 프래그먼트로 구성된다. 화면은 3개의 FrameLayout으로 나뉜다:
- `ch0Frame` (512×652dp): 채널 0 전용
- `ch1Frame` (512×652dp): 채널 1 전용  
- `chFullFrame` (1024×652dp): 전체화면 모드 (충전 중 사용)

### 핵심 상태 흐름

`UiSeq` enum(29개 상태)이 각 채널의 화면 상태를 정의한다. `FragmentChange`가 `UiSeq` 값을 받아 적절한 프래그먼트로 전환한다.

```
INIT → AUTH_SELECT → MEMBER_CARD / CREDIT_CARD / QR_CODE
                   → PLUG_WAIT → CHARGING → FINISH
                   → FAULT
```

### 채널별 독립 상태머신

채널마다 `ClassUiProcess` 인스턴스 하나씩 존재한다. 각 인스턴스는 독립적인 `ChargingCurrentData`(트랜잭션 데이터)와 `UiSeq` 상태를 관리한다. `MainActivity`에서 배열로 관리한다:
```java
ClassUiProcess[] uiProcess = new ClassUiProcess[MAX_CHANNEL];  // MAX_CHANNEL=2
ChargingCurrentData[] currentData = new ChargingCurrentData[MAX_CHANNEL];
```

### 메시지 처리

`ProcessHandler`(Handler 상속)가 100개 이상의 메시지 타입을 디스패치한다. 메시지 ID는 `GlobalVariables`에 상수로 정의되어 있다(100~131번대). OCPP 수신 메시지는 `SocketReceiveMessage`에서 파싱하여 `ProcessHandler`로 전달한다.

### 하드웨어 통신

- **충전기 제어보드**: `ControlBoard` (시리얼 38400 baud, CRC-16 검증, 46 워드 패킷)
- **RF 카드 리더**: `TLS3800` (Humax 전용 프로토콜)
- **모뎀**: `ClientSocket` (TCP → `192.168.39.1:9999`, AT 명령어)

### OCPP 1.6 WebSocket

`websocket/` 패키지에 OCPP 1.6 구현체 존재:
- 기본 URL: `wss://ocpp-stg.turucharger.com/ocpp/[clientId]`
- 인증: HTTP POST `/getOcppAuthInfo/` + TripleDES 암호화
- Humax 확장: `CustomUnitPrice`, `CustomStatusNotification` 커스텀 DataTransfer 메시지
- 재시도: 3초 간격, 최대 5회

### 설정 파일 (기기 내 외부 저장소)

앱은 다음 파일들을 런타임에 읽는다:
- `~/Download/config`: 충전기 전체 설정 (JSON, `ChargerConfiguration`로 파싱)
- `~/Download/unitPrice.dongah`: 단가 정보 (JSON)
- `~/Download/ChargerOperate`: 플러그별 운영 상태 (텍스트, 줄 단위)

## 주요 클래스 위치

| 역할 | 클래스 |
|------|--------|
| 앱 진입점 | `MainActivity.java` |
| UI 상태 열거 | `basefunction/UiSeq.java` |
| 프래그먼트 전환 | `basefunction/FragmentChange.java` |
| 채널 상태머신 | `basefunction/ClassUiProcess.java` |
| 메시지 디스패처 | `handler/ProcessHandler.java` |
| OCPP 수신 처리 | `websocket/socket/SocketReceiveMessage.java` |
| 트랜잭션 데이터 | `basefunction/ChargingCurrentData.java` |
| 충전기 설정 | `basefunction/ChargerConfiguration.java` |
| 전역 상수 | `basefunction/GlobalVariables.java` |
| 제어보드 통신 | `controlboard/ControlBoard.java` |
| RF 카드 리더 | `TECH3800/TLS3800.java` |

## 프래그먼트 개발 패턴

신규 프래그먼트 추가 시:

1. `pages/` 디렉토리에 Fragment 클래스 작성
2. `UiSeq.java`에 새 상태 추가 (필요한 경우)
3. `FragmentChange.java`의 `changeFragment()` 메서드에 분기 추가
4. `res/layout/fragment_*.xml` 레이아웃 파일 작성

채널 번호는 `Bundle`로 프래그먼트에 전달한다:
```java
Bundle args = new Bundle();
args.putInt("channel", channelIndex);
fragment.setArguments(args);
```

채널 간 데이터 공유는 `SharedModel`(ViewModel) 사용.

## 주요 의존성

- **RxJava 3**: FTP/SFTP/HTTP 비동기 처리
- **OkHttp 3**: OCPP 인증 HTTP 요청 및 WebSocket
- **GSON**: JSON 직렬화 (ChargerConfiguration, OCPP 메시지)
- **ZXing**: QR코드 생성
- **Bouncy Castle**: 암호화 (OCPP 보안)
- **JSch + Commons-Net**: SFTP/FTP (로그, 펌웨어 전송)

## 개발 시 주의사항

- `GlobalVariables.VERSION`과 `FW_VERSION` 상수는 빌드마다 수동 확인 필요 (현재 "DEVS 1.0.3" / "1.0.1")
- 시리얼포트 라이브러리는 JNI(NDK)로 구현되어 있어 `app/src/main/jni/Android.mk` 변경 시 NDK 재빌드 필요
- 릴리즈 빌드 ProGuard 규칙은 `app/proguard-rules.pro` 참조
- `ChargerConfiguration`의 설정 값 변경은 기기 파일 시스템의 `config` 파일을 직접 수정해야 적용됨
