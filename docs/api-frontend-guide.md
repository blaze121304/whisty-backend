# Whiskey Backend API 프론트엔드 가이드

> Base URL: `http://192.168.0.206:10006`
> 최종 업데이트: 2026-03-12

---

## 목차
1. [변경사항 요약](#변경사항-요약)
2. [위스키 목록 조회](#위스키-목록-조회)
3. [위스키 상세 조회](#위스키-상세-조회)
4. [위스키 등록](#위스키-등록)
5. [위스키 수정](#위스키-수정)
6. [위스키 삭제](#위스키-삭제)
7. [이미지 업로드](#이미지-업로드)
8. [이미지 삭제](#이미지-삭제)
9. [응답 구조](#응답-구조)
10. [Enum 값 목록](#enum-값-목록)
11. [필터 UI 매핑 가이드](#필터-ui-매핑-가이드)

---

## 변경사항 요약

### ⚠️ Breaking Changes

| 구분 | 이전 | 이후 |
|---|---|---|
| 카테고리 `WORLD_WHISKEY` | 사용 중 | **제거됨** |
| 카테고리 `GRAIN_BOURBON_RYE` | 없음 | **추가됨** |
| 캐스크 `WINE_PORT` | 없음 | **추가됨** |
| 필터 파라미터 `category` | 사용 중 | **`style`로 변경** |
| 필터 파라미터 `characteristic` | 사용 중 | **`cask`로 변경** |
| 필터 파라미터 `nation` | 없음 | **추가됨** |

### 데이터 변경
- 야마자키 12년: `WORLD_WHISKEY` → `SINGLE_MALT` / 국가: `일본`
- 버팔로 트레이스: `WORLD_WHISKEY` → `GRAIN_BOURBON_RYE` / 국가: `미국`

---

## 위스키 목록 조회

```
GET /api/whiskeys
```

### Query Parameters

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `style` | String | 선택 | 스타일 필터 |
| `cask` | String | 선택 | 풍미·캐스크 필터 |
| `nation` | String | 선택 | 국가/지역 필터 |
| `search` | String | 선택 | 이름 또는 브랜드 검색 |
| `page` | Integer | 선택 | 페이지 번호 (기본값: 0) |
| `size` | Integer | 선택 | 페이지 크기 (기본값: 20) |
| `sort` | String | 선택 | 정렬 기준 (기본값: `createdAt,desc`) |

### style 파라미터 값

| 값 | UI 버튼 | 설명 |
|---|---|---|
| _(없음)_ | 전체 보기 | 모든 위스키 |
| `SINGLE_MALT` | 싱글몰트 | 싱글몰트 위스키 |
| `BLENDED` | 블렌디드 | 블렌디드 위스키 |
| `GRAIN_BOURBON_RYE` | 그레인/버번/라이 | 그레인, 버번, 라이 위스키 |
| `OTHER` | 기타 | 위 3가지 외 나머지 (진, 와인, 사케 등) |

### cask 파라미터 값

| 값 | UI 버튼 | 설명 |
|---|---|---|
| _(없음)_ | _(전체)_ | 캐스크 필터 없음 |
| `SHERRY` | 셰리 캐스크 | 셰리 캐스크 숙성 |
| `BOURBON` | 버번 캐스크 | 버번 캐스크 숙성 |
| `WINE_PORT` | 와인/포트 | 와인 또는 포트 캐스크 숙성 |
| `PEAT` | 피트 | 피트 처리 |
| `OTHER` | 기타 | 위 4가지 특성이 없는 것 (혼합 등) |

### nation 파라미터 값

| 값 | UI 버튼 |
|---|---|
| _(없음)_ | _(전체)_ |
| `스코틀랜드` | 스코틀랜드 |
| `미국` | 미국 |
| `일본` | 일본 |
| `한국` | 한국 |
| `OTHER` | 기타 |

### 필터 조합 예시

```js
// 전체 보기
GET /api/whiskeys

// 싱글몰트만
GET /api/whiskeys?style=SINGLE_MALT

// 셰리 캐스크만
GET /api/whiskeys?cask=SHERRY

// 스코틀랜드 싱글몰트
GET /api/whiskeys?style=SINGLE_MALT&nation=스코틀랜드

// 일본 싱글몰트 + 검색
GET /api/whiskeys?style=SINGLE_MALT&nation=일본&search=야마자키

// 피트 + 스코틀랜드
GET /api/whiskeys?cask=PEAT&nation=스코틀랜드

// 그레인/버번/라이 중 미국산
GET /api/whiskeys?style=GRAIN_BOURBON_RYE&nation=미국

// 기타 스타일 (진, 와인 등)
GET /api/whiskeys?style=OTHER

// 국가 기타 (스코틀랜드/미국/일본/한국 외)
GET /api/whiskeys?nation=OTHER
```

### 응답 예시

```json
{
  "content": [
    {
      "id": 1,
      "name": "더 글렌리벳 12년",
      "englishName": "The Glenlivet 12 Year Old",
      "brand": "Glenlivet",
      "category": "SINGLE_MALT",
      "characteristics": ["SHERRY"],
      "abv": 40.0,
      "volume": 700.0,
      "nation": "스코틀랜드",
      "region": "스페이사이드",
      "imageDataUrl": "/images/abc123.jpg",
      "notes": "부드럽고 달콤한 스페이사이드 싱글몰트",
      "nose": "바닐라, 아몬드, 꿀의 향",
      "palate": "부드러운 과일향과 오크의 균형",
      "finish": "중간 길이의 달콤한 여운",
      "personalNote": "입문자에게 추천하는 부드러운 위스키",
      "starPoint": 4.0,
      "pairings": [
        { "icon": "🍫", "name": "다크 초콜릿" },
        { "icon": "🧀", "name": "부드러운 치즈" }
      ],
      "flavorTags": ["부드러움", "달콤함", "과일향", "바닐라"],
      "createdAt": 1700000000000,
      "updatedAt": 1700000000000
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 42,
  "totalPages": 3,
  "last": false,
  "first": true
}
```

---

## 위스키 상세 조회

```
GET /api/whiskeys/{id}
```

### Path Parameters

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Long | 위스키 ID |

### 응답

위 목록 조회의 `content[0]` 구조와 동일한 단일 객체 반환.

---

## 위스키 등록

```
POST /api/whiskeys
Content-Type: multipart/form-data
```

### Request Body (form-data)

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `name` | String | ✅ | 위스키명 |
| `englishName` | String | 선택 | 영문명 (UNIQUE) |
| `brand` | String | ✅ | 브랜드 |
| `category` | String | ✅ | 카테고리 (Enum 값 목록 참고) |
| `characteristics` | String[] | 선택 | 캐스크 특성 (복수 선택 가능) |
| `abv` | Double | 선택 | 알코올 도수 (%) |
| `volume` | Double | 선택 | 용량 (ml) |
| `nation` | String | 선택 | 국가 |
| `region` | String | 선택 | 생산지역 |
| `notes` | String | 선택 | 테이스팅 노트 |
| `nose` | String | 선택 | 노즈 |
| `palate` | String | 선택 | 팔레트 |
| `finish` | String | 선택 | 피니시 |
| `personalNote` | String | 선택 | 개인 소감 |
| `starPoint` | Double | 선택 | 별점 (0.0 ~ 5.0) |
| `pairings[0].icon` | String | 선택 | 페어링 아이콘 (이모지) |
| `pairings[0].name` | String | 선택 | 페어링 이름 |
| `flavorTags` | String[] | 선택 | 플레이버 태그 |
| `image` | File | 선택 | 이미지 파일 |

### 응답

- **201 Created**: 생성된 위스키 객체

---

## 위스키 수정

```
PUT /api/whiskeys/{id}
Content-Type: multipart/form-data
```

Request Body는 등록과 동일. 전체 필드를 다시 전송해야 합니다.

### 응답

- **200 OK**: 수정된 위스키 객체

---

## 위스키 삭제

```
DELETE /api/whiskeys/{id}
```

### 응답

- **204 No Content**

---

## 이미지 업로드

```
POST /api/whiskeys/{id}/image
Content-Type: multipart/form-data
```

| 필드 | 타입 | 필수 |
|---|---|---|
| `image` | File | ✅ |

### 응답

```json
{
  "imageDataUrl": "/images/abc123.jpg"
}
```

이미지 전체 URL: `http://192.168.0.206:10006/images/abc123.jpg`

---

## 이미지 삭제

```
DELETE /api/whiskeys/{id}/image
```

### 응답

- **204 No Content**

---

## 응답 구조

### WhiskeyResponseDto

| 필드 | 타입 | 설명 |
|---|---|---|
| `id` | Long | 고유 ID |
| `name` | String | 위스키명 |
| `englishName` | String | 영문명 |
| `brand` | String | 브랜드 |
| `category` | String | 카테고리 (Enum) |
| `characteristics` | String[] | 캐스크 특성 목록 |
| `abv` | Double | 알코올 도수 |
| `volume` | Double | 용량 (ml) |
| `nation` | String | 국가 |
| `region` | String | 생산지역 |
| `imageDataUrl` | String | 이미지 경로 |
| `notes` | String | 테이스팅 노트 |
| `nose` | String | 노즈 |
| `palate` | String | 팔레트 |
| `finish` | String | 피니시 |
| `personalNote` | String | 개인 소감 |
| `starPoint` | Double | 별점 |
| `pairings` | Pairing[] | 페어링 목록 |
| `flavorTags` | String[] | 플레이버 태그 목록 |
| `createdAt` | Long | 생성일시 (Unix timestamp ms) |
| `updatedAt` | Long | 수정일시 (Unix timestamp ms) |

### Pairing

| 필드 | 타입 | 설명 |
|---|---|---|
| `icon` | String | 이모지 아이콘 |
| `name` | String | 페어링 이름 |

### 에러 응답

```json
{
  "status": 404,
  "message": "위스키를 찾을 수 없습니다. ID: 999",
  "timestamp": "2026-03-12T15:00:00"
}
```

---

## Enum 값 목록

### category (WhiskeyCategory)

| 값 | 설명 |
|---|---|
| `SINGLE_MALT` | 싱글몰트 |
| `BLENDED` | 블렌디드 |
| `GRAIN_BOURBON_RYE` | 그레인/버번/라이 |
| `GIN_VODKA` | 진/보드카 |
| `WINE_LIQUEUR` | 와인/리큐어 |
| `SAKE_TRADITIONAL` | 사케/전통주 |
| `BEER_SOJU` | 맥주/소주 |

### characteristics (WhiskeyCharacteristic)

| 값 | 설명 |
|---|---|
| `SHERRY` | 셰리 캐스크 |
| `BOURBON` | 버번 캐스크 |
| `WINE_PORT` | 와인/포트 캐스크 |
| `PEAT` | 피트 |

---

## 필터 UI 매핑 가이드

```
┌─────────────────────────────────────────────────┐
│ 1. 모든 위스키                                    │
│    [전체 보기]  → 파라미터 없음                    │
├─────────────────────────────────────────────────┤
│ 2. 풍미·캐스크          파라미터: cask=           │
│    [셰리 캐스크] → SHERRY                         │
│    [버번 캐스크] → BOURBON                        │
│    [와인/포트]   → WINE_PORT                      │
│    [피트]        → PEAT                           │
│    [기타]        → OTHER  (혼합 또는 무특성)       │
├─────────────────────────────────────────────────┤
│ 3. 스타일               파라미터: style=          │
│    [싱글몰트]    → SINGLE_MALT                    │
│    [블렌디드]    → BLENDED                        │
│    [그레인/버번/라이] → GRAIN_BOURBON_RYE         │
│    [기타]        → OTHER  (진, 와인, 사케 등)      │
├─────────────────────────────────────────────────┤
│ 4. 국가/지역            파라미터: nation=         │
│    [스코틀랜드]  → 스코틀랜드                      │
│    [미국]        → 미국                           │
│    [일본]        → 일본                           │
│    [한국]        → 한국                           │
│    [기타]        → OTHER                          │
└─────────────────────────────────────────────────┘

※ 그룹 간 AND 조합 가능
   예) 스타일: 싱글몰트 + 국가: 일본
   → GET /api/whiskeys?style=SINGLE_MALT&nation=일본
```
