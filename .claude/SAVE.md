# whisty-backend 프로젝트 분석 보고서
> 작성일: 2026-05-26 | 최종 수정: 2026-05-26 | 버전: v1.1

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
│   ├── WhiskeyBackendApplication.java   # 진입점
│   ├── ServletInitializer.java          # WAR 배포용
│   ├── common/
│   │   ├── config/
│   │   │   ├── CorsConfig.java          # CORS + 정적 이미지 리소스 설정
│   │   │   ├── DataInitializer.java
│   │   │   └── JpaConfig.java           # JPA Auditing 활성화
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java   # @RestControllerAdvice
│   │       ├── ErrorResponse.java
│   │       └── ResourceNotFoundException.java
│   ├── controller/
│   │   ├── WhiskeyController.java       # /api/whiskeys CRUD + 이미지
│   │   └── HealthController.java
│   ├── domain/
│   │   ├── entity/
│   │   │   ├── Whiskey.java             # 메인 엔티티
│   │   │   └── Pairing.java             # @Embeddable
│   │   ├── dto/
│   │   │   ├── WhiskeyRequestDto.java
│   │   │   ├── WhiskeyResponseDto.java
│   │   │   └── PairingDto.java
│   │   ├── enums/
│   │   │   ├── WhiskeyCategory.java
│   │   │   └── WhiskeyCharacteristic.java
│   │   └── specification/
│   │       └── WhiskeySpecification.java  # JPA Specification 필터
│   ├── repository/
│   │   └── WhiskeyRepository.java         # JpaRepository + JpaSpecificationExecutor
│   └── service/
│       └── WhiskeyService.java
├── src/main/resources/
│   ├── application.properties           # 개발 프로파일 (H2)
│   ├── application-prod.properties      # 운영 프로파일 (MySQL, 환경변수)
│   ├── schema-mysql.sql                 # MySQL 스키마 DDL
│   ├── data.sql                         # 샘플 데이터 (10건)
│   └── uploads/images/                  # 이미지 저장 디렉토리
├── docs/
│   └── api-frontend-guide.md            # 프론트엔드 연동 가이드
├── BACKEND_API_SPEC.md                  # 초기 API 설계 문서
├── Dockerfile                           # Multi-stage (maven → jre-alpine)
├── docker-compose.yml
└── .claude/
    ├── CLAUDE.md
    └── SAVE.md                          # (이 파일)
```

---

## 3. 아키텍처

### 레이어 구조

```
┌──────────────────────────────────────────────────┐
│  Next.js 프론트엔드 (:10007)                      │
│  GET/POST/PUT/DELETE /api/whiskeys               │
└──────────────────────┬───────────────────────────┘
                       │ HTTP (CORS 허용)
┌──────────────────────▼───────────────────────────┐
│  WhiskeyController  (/api/whiskeys)              │
│  HealthController   (/api/health)                │
└──────────────────────┬───────────────────────────┘
                       │
┌──────────────────────▼───────────────────────────┐
│  WhiskeyService                                  │
│  - JPA Specification 조합 → findAll()            │
│  - UUID 기반 이미지 파일 저장/삭제               │
└───────────────┬──────────────────────────────────┘
                │
┌───────────────▼──────────────────────────────────┐
│  WhiskeyRepository (JpaSpecificationExecutor)    │
│  DB: H2 (dev) / MySQL (prod)                     │
│  테이블: whiskey, whiskey_characteristics,       │
│          whiskey_pairings, whiskey_flavor_tags   │
└──────────────────────────────────────────────────┘
```

### 요청 처리 흐름 (목록 조회)

```
GET /api/whiskeys?style=SINGLE_MALT&cask=SHERRY&nation=스코틀랜드
  └─ WhiskeyController.getAllWhiskeys()
        └─ WhiskeyService.findAll(style, cask, nation, search, pageable)
              └─ WhiskeySpecification.hasStyle() + hasCask() + hasNation() + hasSearch()
                    └─ whiskeyRepository.findAll(spec, pageable)
                          └─ Page<Whiskey> → convertToResponseDto() → Page<WhiskeyResponseDto>
```

---

## 4. 주요 모듈 상세

### 4-1. `domain/entity/` — 데이터 레이어

**Whiskey** (Entity, `@Table(name = "whiskey")`)

| 필드 | 타입 | 제약 | 설명 |
|------|------|------|------|
| `id` | Long | PK, auto | 고유 ID |
| `name` | String | NOT NULL | 위스키명 |
| `englishName` | String | UNIQUE | 영문명 |
| `brand` | String | NOT NULL | 브랜드 |
| `category` | WhiskeyCategory | NOT NULL | 카테고리 Enum |
| `characteristics` | List\<WhiskeyCharacteristic\> | @ElementCollection | 캐스크 특성 (복수) |
| `abv` | Double | - | 알코올 도수 (%) |
| `volume` | Double | - | 용량 (ml) |
| `nation` | String | - | 국가 |
| `region` | String | - | 생산지역 |
| `imageDataUrl` | String | length=1000 | 이미지 경로 (`/images/xxx.jpg`) |
| `notes` | String | TEXT | 테이스팅 노트 |
| `nose` | String | length=1000 | 노즈 |
| `palate` | String | length=1000 | 팔레트 |
| `finish` | String | length=1000 | 피니시 |
| `personalNote` | String | TEXT | 개인 소감 |
| `starPoint` | Double | - | 별점 (기본 0.0) |
| `pairings` | List\<Pairing\> | @ElementCollection | 페어링 목록 |
| `flavorTags` | List\<String\> | @ElementCollection | 플레이버 태그 |
| `createdAt` | Long | @CreatedDate | Unix timestamp (ms) |
| `updatedAt` | Long | @LastModifiedDate | Unix timestamp (ms) |

**Pairing** (`@Embeddable`, 테이블 `whiskey_pairings`)

| 필드 | 타입 | 설명 |
|------|------|------|
| `icon` | String | 이모지 아이콘 |
| `name` | String | 페어링명 |

### 4-2. `domain/enums/` — Enum 정의

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
| `WINE_PORT` | 와인/포트 캐스크 |

### 4-3. `domain/specification/` — 필터링

**WhiskeySpecification** — JPA Criteria API 기반 동적 필터

| 메서드 | 파라미터 값 | 동작 |
|--------|-------------|------|
| `hasStyle(style)` | `SINGLE_MALT\|BLENDED\|GRAIN_BOURBON_RYE` | category 일치 |
| `hasStyle(style)` | `OTHER` | MAIN_STYLES 외 나머지 |
| `hasCask(cask)` | `SHERRY\|BOURBON\|WINE_PORT\|PEAT` | characteristics JOIN EXISTS |
| `hasCask(cask)` | `OTHER` | 메인 캐스크 없는 위스키 (Subquery NOT EXISTS) |
| `hasNation(nation)` | `스코틀랜드\|미국\|일본\|한국` | nation 일치 |
| `hasNation(nation)` | `OTHER` | MAIN_NATIONS 외 + NULL |
| `hasSearch(search)` | 검색어 | name OR brand LIKE (소문자 변환) |

- 모든 Specification은 `null` 반환 시 조건 없음 (전체)
- 복수 조건은 `Specification.where().and()` 체이닝으로 AND 조합

### 4-4. `service/WhiskeyService`

- `UPLOAD_DIR = "src/main/resources/uploads/images"` — 이미지 로컬 저장
- 이미지 파일명: `UUID.randomUUID() + 확장자`
- `update()` 시 기존 이미지 파일 삭제 후 새 이미지 저장
- `delete()` 시 이미지 파일도 함께 삭제
- `starPoint` null 수신 시 0.0으로 저장

### 4-5. `controller/WhiskeyController`

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| GET | `/api/whiskeys` | 목록 조회 (페이지, 필터) |
| GET | `/api/whiskeys/{id}` | 상세 조회 |
| POST | `/api/whiskeys` (multipart) | 위스키 생성 |
| PUT | `/api/whiskeys/{id}` (multipart) | 위스키 수정 |
| DELETE | `/api/whiskeys/{id}` | 위스키 삭제 |
| POST | `/api/whiskeys/{id}/image` | 이미지 업로드 |
| DELETE | `/api/whiskeys/{id}/image` | 이미지 삭제 |

**목록 조회 Query Parameters**

| 파라미터 | 설명 | 기본값 |
|---------|------|--------|
| `style` | 스타일 필터 | - |
| `cask` | 캐스크/풍미 필터 | - |
| `nation` | 국가 필터 | - |
| `search` | 이름/브랜드 검색 | - |
| `page` | 페이지 번호 | 0 |
| `size` | 페이지 크기 | 20 |
| `sort` | 정렬 | `createdAt,desc` |

### 4-6. `common/` — 공통 레이어

**CorsConfig**
- CORS 허용 오리진: `http://localhost:10007`, `http://192.168.0.206:10007`
- 정적 이미지 서빙: `/images/**` → `file:src/main/resources/uploads/images/`

**GlobalExceptionHandler** (`@RestControllerAdvice`)

| 예외 | HTTP 상태 |
|------|-----------|
| `ResourceNotFoundException` | 404 Not Found |
| `MethodArgumentNotValidException` | 400 Bad Request (Validation) |
| `Exception` | 500 Internal Server Error |

**에러 응답 형식**
```json
{
  "timestamp": "2026-05-26T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "위스키를 찾을 수 없습니다. ID: 999",
  "path": "/api/whiskeys/999"
}
```

---

## 5. 설정 & 프로파일

| 항목 | 개발 (default) | 운영 (prod) |
|------|----------------|-------------|
| DB | H2 in-memory (`jdbc:h2:mem:whiskeydb`) | MySQL (환경변수 주입) |
| DDL | `none` (schema-h2.sql 수동 실행) | `none` |
| SQL Init | `always` | `never` |
| SQL Logging | `show-sql=true` | `show-sql=false` |
| 포트 | `10006` | `10006` |

**운영 환경변수** (docker-compose.yml)

| 환경변수 | 값 |
|---------|-----|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DB_HOST` | `host.docker.internal` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `whiskeydb` |
| `DB_USERNAME` | `rusty` |
| `DB_PASSWORD` | (docker-compose.yml 참조) |

**H2 Console** (개발): `http://localhost:10006/h2-console`

---

## 6. DB 스키마 (MySQL)

```
whiskey                      (id, name, english_name, brand, category, abv, volume,
                               nation, region, image_data_url, notes, nose, palate,
                               finish, personal_note, star_point, created_at, updated_at)
whiskey_characteristics      (whiskey_id FK, characteristic ENUM)
whiskey_pairings             (whiskey_id FK, icon, name)
whiskey_flavor_tags          (whiskey_id FK, flavor_tag)
```

**주의**: `schema-mysql.sql`의 `category` ENUM에 `WORLD_WHISKEY`가 있으나 Java Enum에는 없음.  
실제 코드 기준 카테고리는 `WhiskeyCategory.java` 참고. MySQL ENUM 수정 필요 시 ALTER TABLE 필요.

---

## 7. 의존성

```
spring-boot-starter-web               # REST API
spring-boot-starter-data-jpa          # ORM
spring-boot-starter-validation        # @Valid, @NotBlank 등
spring-boot-starter-hateoas           # HATEOAS (현재 미활용)
spring-boot-starter-actuator          # /actuator 헬스체크
springdoc-openapi-starter-webmvc-ui:2.8.14  # Swagger UI
h2:runtime                            # 개발 DB
mysql-connector-j:runtime             # 운영 DB
lombok                                # 보일러플레이트 제거
spring-boot-devtools:runtime          # 개발용 자동 재시작
```

---

## 8. 배포

### Docker 빌드 & 실행

```powershell
# 이미지 빌드
docker build -t whisty-backend:latest .

# docker-compose 실행 (운영)
docker-compose up -d
```

### docker-compose 볼륨 마운트

| 컨테이너 경로 | 호스트 경로 | 목적 |
|--------------|------------|------|
| `/app/src/main/resources/uploads/images` | `./src/main/resources/uploads/images` | 이미지 영속 보관 |
| `/app/keyfile.md` | `/home/rusty/whiskey/whisty-backend/keyfile.md` | 키파일 |

### 접속 정보

| 환경 | URL |
|------|-----|
| 로컬 개발 | `http://localhost:10006` |
| 내부망 운영 | `http://192.168.0.206:10006` |
| Swagger UI | `http://localhost:10006/swagger-ui/index.html` |
| H2 Console | `http://localhost:10006/h2-console` (dev 프로파일만) |

---

## 9. 샘플 데이터 (data.sql — 10건)

| 이름 | 카테고리 | 특성 | 국가 |
|------|---------|------|------|
| 더 글렌리벳 12년 | SINGLE_MALT | SHERRY | 스코틀랜드 |
| 라프로익 10년 | SINGLE_MALT | PEAT | 스코틀랜드 |
| 맥캘란 12년 셰리오크 | SINGLE_MALT | - | 스코틀랜드 |
| 조니워커 블랙 라벨 | BLENDED | - | 스코틀랜드 |
| 야마자키 12년 | WORLD_WHISKEY* | - | 일본 |
| 버팔로 트레이스 | WORLD_WHISKEY* | - | 미국 |
| 탱커레이 런던 드라이 진 | GIN_VODKA | - | 영국 |
| 샤또 마고 2015 | WINE_LIQUEUR | - | 프랑스 |
| 다이긴조 준마이 | SAKE_TRADITIONAL | - | 일본 |
| 기네스 드래프트 | BEER_SOJU | - | 영국 |

> *data.sql의 야마자키/버팔로는 `WORLD_WHISKEY`로 삽입되어 있음 (현재 Java Enum에 없는 값). 실제 운영 DB는 `api-frontend-guide.md` 기준 수정 반영됨.

---

## 10. 확장 포인트

### 새 필터 조건 추가

1. `WhiskeySpecification`에 `Specification<Whiskey>` 메서드 추가
2. `WhiskeyService.findAll()` 파라미터 및 체이닝 추가
3. `WhiskeyController.getAllWhiskeys()` `@RequestParam` 추가

### DB 스키마 변경

1. `Whiskey.java`에 필드 추가
2. `schema-mysql.sql` ALTER TABLE 작성 (운영 수동 실행)
3. 개발 H2: `schema-h2.sql` 수정

### 인증 추가 (현재 미구현)

현재 단일 사용자 가정, 인증 없음. JWT 추가 시 Spring Security 의존성 필요.

---

## 11. 현재 이슈 (2026-05-26 조사 중)

### 증상
`https://whisty.rusty.it.kr` 접속 시 위스키 리스트 미표시.  
`http://192.168.0.206:10007` 내부 IP 직접 접속 시에는 정상 표시.

### 브라우저 콘솔 에러
```
Mixed Content: The page at 'https://whisty.rusty.it.kr/' was loaded over HTTPS,
but requested an insecure resource 'http://192.168.0.206:10006/api/whiskeys?size=500'.

Access to fetch at 'http://192.168.0.206:10006/api/whiskeys?size=500' from origin
'https://whisty.rusty.it.kr' has been blocked by CORS policy.
```

### 원인 분석

| 원인 | 설명 |
|------|------|
| **Mixed Content** | 프론트엔드가 HTTPS로 서빙되지만 API URL이 `http://192.168.0.206:10006` (HTTP) |
| **CORS 오리진 누락** | `CorsConfig.java`에 `https://whisty.rusty.it.kr`가 허용 오리진에 없음 |
| **API URL 미변경** | 프론트엔드 API Base URL이 내부 IP 그대로 남아있음 |

- 이전에는 프론트엔드가 `http://192.168.0.206:10007` (HTTP)로 접속해서 정상 동작했음
- 도메인(`whisty.rusty.it.kr`) 배포 이후 nginx가 HTTP → HTTPS 리다이렉트하면서 문제 발생
- `CorsConfig.java`에는 `https://whisty.rusty.it.kr`가 한 번도 추가된 적 없음 (git 이력 확인)

### 해결 방향 (미완료)

**필요한 작업:**
1. **nginx 설정**: 기존 `whisty.rusty.it.kr` nginx 설정에 `/api` → `localhost:10006` 프록시 추가
2. **CorsConfig.java 수정**: `https://whisty.rusty.it.kr` 추가
3. **프론트엔드 API URL 변경**: `http://192.168.0.206:10006` → `https://whisty.rusty.it.kr`

**현재 서버 상황 파악 중:**
- 서버 OS: Linux (`root@rusty-linux`)
- nginx sites-enabled: `openmediavault-webgui` (OMV, 포트 8088)만 존재
- `whisty.rusty.it.kr` 도메인의 HTTPS 처리 주체 미파악 (Cloudflare Tunnel 또는 별도 nginx 가능성)
- 다음 확인 필요: `ss -tlnp | grep -E ':80|:443'` 및 `ps aux | grep -E 'nginx|caddy|cloudflared'`