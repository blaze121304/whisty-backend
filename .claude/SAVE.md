# whisty-backend 프로젝트 분석 보고서
> 작성일: 2026-05-26 | 최종 수정: 2026-05-27 | 버전: v1.2

---

## 1. 프로젝트 개요

**whisty-backend**는 위스키 컬렉션을 관리하는 **Spring Boot REST API 서버**다.  
Next.js 프론트엔드(`:10007`)와 연동되며, 위스키 CRUD·이미지 업로드·다중 필터 검색을 제공한다.

- 언어: Java 21
- 프레임워크: Spring Boot 3.5.7
- 빌드: Maven (WAR 패키징)
- DB: H2 (개발) / MySQL (운영)
- ORM: Spring Data JPA + Hibernate (JPA Auditing)
- 포트: `10006`
- API 문서: Springdoc OpenAPI (Swagger UI `/swagger-ui/index.html`)
- 헬스체크: Spring Actuator

---

## 2. 디렉토리 구조

```
whisty-backend/
├── src/main/java/com/rusty/whiskeybackend/
│   ├── WhiskeyBackendApplication.java
│   ├── ServletInitializer.java
│   ├── common/
│   │   ├── config/
│   │   │   ├── CorsConfig.java
│   │   │   ├── DataInitializer.java
│   │   │   └── JpaConfig.java
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java
│   │       ├── ErrorResponse.java
│   │       └── ResourceNotFoundException.java
│   ├── controller/
│   │   ├── WhiskeyController.java
│   │   └── HealthController.java
│   ├── domain/
│   │   ├── entity/
│   │   │   ├── Whiskey.java
│   │   │   └── Pairing.java
│   │   ├── dto/
│   │   │   ├── WhiskeyRequestDto.java
│   │   │   ├── WhiskeyResponseDto.java
│   │   │   └── PairingDto.java
│   │   ├── enums/
│   │   │   ├── WhiskeyCategory.java
│   │   │   └── WhiskeyCharacteristic.java
│   │   └── specification/
│   │       └── WhiskeySpecification.java
│   ├── repository/
│   │   └── WhiskeyRepository.java
│   └── service/
│       └── WhiskeyService.java
├── src/main/resources/
│   ├── application.properties
│   ├── application-prod.properties
│   ├── schema-mysql.sql
│   ├── data.sql
│   └── uploads/images/
├── Dockerfile
├── docker-compose.yml
└── .claude/
    ├── CLAUDE.md
    └── SAVE.md
```

---

## 3. 아키텍처

### 실제 운영 구조 (2026-05-27 기준)

```
브라우저
  │
  ├─ 페이지 로드: HTTPS → NPM(443) → whisty-react:10007
  │
  └─ API/이미지: HTTPS → NPM(443)
                          ├─ /api/*   → whisty-backend:10006
                          └─ /images/* → whisty-backend:10006
```

### NPM (Nginx Proxy Manager) 설정 — whisty.rusty.it.kr

```nginx
location /api/ {
    proxy_pass http://192.168.0.206:10006;
    proxy_set_header Host $host;
    proxy_set_header X-Forwarded-Proto $scheme;
}

location ^~ /images/ {
    proxy_pass http://192.168.0.206:10006;
    proxy_set_header Host $host;
    proxy_set_header X-Forwarded-Proto $scheme;
}
```

> `^~` 수정자 필수 — NPM의 `assets.conf`가 `.jpg/.png` 등을 정규식으로 가로채므로
> prefix 매칭이 정규식보다 우선순위를 갖도록 설정

---

## 4. 주요 모듈 상세

### 4-1. `domain/enums/` — Enum 정의

**WhiskeyCategory**

| 값 | 설명 |
|----|------|
| `SINGLE_MALT` | 싱글몰트 |
| `BLENDED` | 블렌디드 |
| `GRAIN_BOURBON_RYE` | 그레인/버번/라이 |
| `GIN_VODKA` | 진/보드카 |
| `WINE_LIQUEUR` | 와인/리큐어 |
| `SAKE_TRADITIONAL` | 사케/전통주 |
| `BEER_SOJU` | 맥주/소주 |

**WhiskeyCharacteristic**

| 값 | 설명 |
|----|------|
| `SHERRY` | 셰리 캐스크 |
| `PEAT` | 피트 |
| `BOURBON` | 버번 캐스크 |

> ~~`WINE_PORT`~~ — 2026-05-27 제거. DB `whiskey_characteristics` 테이블에서도 삭제됨.

### 4-2. `domain/specification/WhiskeySpecification`

| 메서드 | 파라미터 | 동작 |
|--------|----------|------|
| `hasStyle(null)` | (전체) | **MAIN_STYLES만 반환** (위스키 전용 탭 기본값) |
| `hasStyle("SINGLE_MALT")` 등 | 특정 카테고리 | 해당 카테고리만 |
| `hasStyle("OTHER")` | 스피릿/기타 | MAIN_STYLES 외 나머지 (진/와인/사케 등) |
| `hasCask(cask)` | `SHERRY\|BOURBON\|PEAT\|OTHER` | 캐스크 필터 |
| `hasNation(nation)` | 국가명 또는 `OTHER` | 국가 필터 |
| `hasSearch(search)` | 검색어 | name OR brand LIKE |

> **핵심 변경**: `style=null`(전체)이 이전에는 스피릿 포함 전체를 반환했으나,
> 위스키 탭과 스피릿 탭 분리를 위해 MAIN_STYLES만 반환하도록 수정됨.
> 스피릿/기타 탭은 반드시 `style=OTHER`로 호출해야 함.

**MAIN_STYLES**: `SINGLE_MALT`, `BLENDED`, `GRAIN_BOURBON_RYE`  
**MAIN_CASKS**: `SHERRY`, `BOURBON`, `PEAT`

### 4-3. `common/config/CorsConfig`

```java
.allowedOrigins(
    "http://localhost:10007",
    "http://192.168.0.206:10007",
    "https://whisty.rusty.it.kr"   // 2026-05-27 추가
)
```

정적 이미지 서빙: `/images/**` → `file:src/main/resources/uploads/images/`

### 4-4. `common/exception/GlobalExceptionHandler`

`@Slf4j` 추가 (2026-05-27). 500 에러 시 스택트레이스 로깅:
```java
log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
```

### 4-5. `service/WhiskeyService` — 주요 수정사항

**불변 컬렉션 버그 수정 (2026-05-27)**  
`updateEntityFromDto`에서 `List.of()` 대신 `new ArrayList<>()`를 사용.  
Hibernate merge 시 `.clear()` 호출로 `UnsupportedOperationException` 발생했던 문제 해결.

```java
// 수정 전 (버그)
whiskey.setCharacteristics(dto.getCharacteristics() != null ? dto.getCharacteristics() : List.of());

// 수정 후
whiskey.setCharacteristics(dto.getCharacteristics() != null ? new ArrayList<>(dto.getCharacteristics()) : new ArrayList<>());
```

---

## 5. 설정 & 프로파일

| 항목 | 개발 (default) | 운영 (prod) |
|------|----------------|-------------|
| DB | H2 in-memory | MySQL (환경변수 주입) |
| 포트 | `10006` | `10006` |

**운영 환경변수** (docker-compose.yml)

| 환경변수 | 값 |
|---------|-----|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DB_HOST` | `host.docker.internal` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `whiskeydb` |
| `DB_USERNAME` | `rusty` |

---

## 6. DB 스키마 (MySQL)

```
whiskey                  (id, name, english_name, brand, category, abv, volume,
                           nation, region, image_data_url, notes, nose, palate,
                           finish, personal_note, star_point, created_at, updated_at)
whiskey_characteristics  (whiskey_id FK, characteristic ENUM('SHERRY','PEAT','BOURBON'))
whiskey_pairings         (whiskey_id FK, icon, name)
whiskey_flavor_tags      (whiskey_id FK, flavor_tag)
```

> `whiskey_characteristics`의 characteristic 컬럼 ENUM: `WINE_PORT` 제거됨.
> MySQL ALTER 필요 시: `ALTER TABLE whiskey_characteristics MODIFY characteristic ENUM('SHERRY','PEAT','BOURBON');`

---

## 7. 배포

### 서버 구조

| 컨테이너 | 포트 | 역할 |
|---------|------|------|
| `npm` | 80, 443 | Nginx Proxy Manager (SSL + 라우팅) |
| `whisty-react-whisty-react-1` | 10007 | Next.js 프론트엔드 |
| `whisty-backend` | 10006 | Spring Boot 백엔드 |
| `infrastructure-mysql` | 3306 | MySQL DB |

### 배포 스크립트

- 백엔드: `/home/rusty/bin/whiskey-backend.deploy.sh`
  - `git fetch && git reset --hard origin/master` → Docker 빌드 → 재시작
- 프론트엔드: `/home/rusty/bin/whisty-react.deploy.sh`
  - `git fetch && git reset --hard origin/main` → Docker 빌드(`--build-arg NEXT_PUBLIC_API_BASE_URL=https://whisty.rusty.it.kr/api`) → Portainer에서 Stack 업데이트

### 이미지 저장 경로

| 위치 | 경로 |
|------|------|
| 호스트 | `/home/rusty/whiskey/whisty-backend/src/main/resources/uploads/images/` |
| 컨테이너 | `/app/src/main/resources/uploads/images/` |
| API 서빙 | `/images/{uuid}.{ext}` |
| 볼륨 마운트 | `docker-compose.yml` 참조 |

### 접속 정보

| 환경 | URL |
|------|-----|
| 외부 도메인 | `https://whisty.rusty.it.kr` |
| 내부망 프론트 | `http://192.168.0.206:10007` |
| 내부망 백엔드 API | `http://192.168.0.206:10006` |
| NPM 관리 UI | `http://192.168.0.206:81` |

---

## 8. 프론트엔드 연동 (whisty-react)

- 소스 경로 (서버): `/home/rusty/whiskey/whisty-react/`
- API Base URL: `process.env.NEXT_PUBLIC_API_BASE_URL || 'http://192.168.0.206:10006/api'`
- 빌드 시 `--build-arg NEXT_PUBLIC_API_BASE_URL=https://whisty.rusty.it.kr/api` 주입
- `NEXT_PUBLIC_*` 변수는 **빌드 타임**에 바인딩됨 → 변경 시 이미지 재빌드 필수

**프론트엔드 주의사항:**
- 캐스크 필터: `SHERRY` / `BOURBON` / `PEAT` / `OTHER` (WINE_PORT 제거됨)
- 위스키 탭 "전체": `style` 파라미터 생략 또는 null → 위스키만 반환
- 스피릿/기타 탭 "전체": 반드시 `style=OTHER`로 호출
- 이미지 URL: DB의 `/images/{파일명}` → `https://whisty.rusty.it.kr/images/{파일명}`으로 조합

---

## 9. 알려진 이슈 및 해결 이력

| 날짜 | 이슈 | 해결 |
|------|------|------|
| 2026-05-27 | HTTPS에서 위스키 리스트 미표시 (Mixed Content + CORS) | NPM `/api`, `/images` 프록시 추가 + CorsConfig 오리진 추가 + 프론트 재빌드 |
| 2026-05-27 | `/images/` HTTPS 접근 시 Next.js 404 반환 | NPM location에 `^~` 수정자 추가 (assets.conf 정규식 우선순위 우회) |
| 2026-05-27 | 위스키 수정(PUT) 시 500 에러 | `updateEntityFromDto`에서 `List.of()` → `new ArrayList<>()` 수정 |
| 2026-05-27 | `style=null` 전체 조회 시 스피릿 포함 | `hasStyle(null)` → MAIN_STYLES 필터 적용 |
| 2026-05-27 | WINE_PORT 캐스크 필터 제거 | enum 삭제 + DB 레코드 삭제 |
