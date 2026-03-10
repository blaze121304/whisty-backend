-- =============================================
-- Whiskey Sample Data
-- =============================================

INSERT INTO whiskey (name, english_name, brand, category, abv, volume, nation, region, notes, nose, palate, finish, personal_note, star_point, image_data_url, created_at, updated_at) VALUES
('더 글렌리벳 12년','The Glenlivet 12 Year Old','Glenlivet','SINGLE_MALT',40.0,700.0,'스코틀랜드','스페이사이드','부드럽고 달콤한 스페이사이드 싱글몰트','바닐라, 아몬드, 꿀의 향','부드러운 과일향과 오크의 균형','중간 길이의 달콤한 여운','입문자에게 추천하는 부드러운 위스키',4.0,NULL,1700000000000,1700000000000),
('라프로익 10년','Laphroaig 10 Year Old','Laphroaig','SINGLE_MALT',40.0,700.0,'스코틀랜드','아일라','강렬한 피트와 스모키한 아일라 위스키','강한 피트, 요오드, 바다 향','스모키하고 약간의 단맛','길고 강렬한 피트 여운','피트 애호가를 위한 클래식',4.0,NULL,1700000000000,1700000000000),
('맥캘란 12년 셰리오크','Macallan 12 Year Old Sherry Oak','Macallan','SINGLE_MALT',40.0,700.0,'스코틀랜드','스페이사이드','풍부한 셰리 캐스크 숙성의 깊은 맛','말린 과일, 생강, 오렌지','셰리의 달콤함과 스파이시함','긴 여운의 말린 과일과 스파이스','셰리 캐스크의 정석',4.0,NULL,1700000000000,1700000000000),
('조니워커 블랙 라벨','Johnnie Walker Black Label','Johnnie Walker','BLENDED',40.0,700.0,'스코틀랜드','스페이사이드','균형잡힌 블렌디드 스카치','스모키, 과일, 바닐라','크리미하고 스모키한 맛','중간 길이의 스모키한 여운','가성비 좋은 블렌디드 위스키',4.0,NULL,1700000000000,1700000000000),
('야마자키 12년','Yamazaki 12 Year Old','Suntory','WORLD_WHISKEY',43.0,700.0,'일본','야마자키','일본 위스키의 대표작','과일, 꿀, 미즈나라 오크','복숭아, 파인애플, 계피','달콤하고 긴 여운','섬세하고 우아한 재패니즈 위스키',4.0,NULL,1700000000000,1700000000000),
('버팔로 트레이스','Buffalo Trace Bourbon','Buffalo Trace','WORLD_WHISKEY',45.0,750.0,'미국','켄터키','켄터키 버번의 클래식','바닐라, 캐러멜, 오크','달콤한 옥수수와 라이 스파이스','긴 여운의 캐러멜과 오크','가성비 좋은 버번',4.0,NULL,1700000000000,1700000000000),
('탱커레이 런던 드라이 진','Tanqueray London Dry Gin','Tanqueray','GIN_VODKA',47.3,750.0,'영국','런던','클래식 런던 드라이 진','주니퍼 베리, 시트러스','크리스프하고 드라이한 맛','상쾌한 여운','진토닉의 정석',4.0,NULL,1700000000000,1700000000000),
('샤또 마고 2015','Chateau Margaux 2015','Chateau Margaux','WINE_LIQUEUR',13.5,750.0,'프랑스','보르도','보르도 최고급 와인','블랙베리, 바이올렛, 시더','실키하고 우아한 타닌','매우 긴 여운','특별한 날을 위한 와인',4.0,NULL,1700000000000,1700000000000),
('다이긴조 준마이','Daiginjo Junmai Sake','타테야마','SAKE_TRADITIONAL',16.0,720.0,'일본','기후','프리미엄 일본 사케','과일, 꽃향기, 쌀','깨끗하고 섬세한 맛','부드럽고 상쾌한 여운','차갑게 또는 미지근하게',4.0,NULL,1700000000000,1700000000000),
('기네스 드래프트','Guinness Draught','Guinness','BEER_SOJU',4.2,440.0,'영국','아이리시','아이리시 스타우트의 대표','로스티드 몰트, 커피','크리미하고 부드러운 질감','약간 쓴 여운','캔으로도 드래프트 느낌',4.0,NULL,1700000000000,1700000000000);

INSERT INTO whiskey_characteristics (whiskey_id, characteristic)
SELECT w.id, 'SHERRY' FROM whiskey w WHERE w.english_name = 'The Glenlivet 12 Year Old';
INSERT INTO whiskey_characteristics (whiskey_id, characteristic)
SELECT w.id, 'PEAT'   FROM whiskey w WHERE w.english_name = 'Laphroaig 10 Year Old';
INSERT INTO whiskey_characteristics (whiskey_id, characteristic)
SELECT w.id, 'SHERRY' FROM whiskey w WHERE w.english_name = 'Macallan 12 Year Old Sherry Oak';
INSERT INTO whiskey_characteristics (whiskey_id, characteristic)
SELECT w.id, 'BOURBON' FROM whiskey w WHERE w.english_name = 'Buffalo Trace Bourbon';

INSERT INTO whiskey_pairings (whiskey_id, icon, name)
SELECT w.id, '🍫', '다크 초콜릿' FROM whiskey w WHERE w.english_name = 'The Glenlivet 12 Year Old' UNION ALL
SELECT w.id, '🧀', '부드러운 치즈' FROM whiskey w WHERE w.english_name = 'The Glenlivet 12 Year Old' UNION ALL
SELECT w.id, '🥩', '훈제 고기'    FROM whiskey w WHERE w.english_name = 'Laphroaig 10 Year Old' UNION ALL
SELECT w.id, '🦞', '해산물'        FROM whiskey w WHERE w.english_name = 'Laphroaig 10 Year Old' UNION ALL
SELECT w.id, '🍰', '과일 케이크'  FROM whiskey w WHERE w.english_name = 'Macallan 12 Year Old Sherry Oak' UNION ALL
SELECT w.id, '🥜', '견과류'        FROM whiskey w WHERE w.english_name = 'Macallan 12 Year Old Sherry Oak' UNION ALL
SELECT w.id, '🍔', '버거'          FROM whiskey w WHERE w.english_name = 'Johnnie Walker Black Label' UNION ALL
SELECT w.id, '🥓', '베이컨'        FROM whiskey w WHERE w.english_name = 'Johnnie Walker Black Label' UNION ALL
SELECT w.id, '🍣', '스시'          FROM whiskey w WHERE w.english_name = 'Yamazaki 12 Year Old' UNION ALL
SELECT w.id, '🍡', '화과자'        FROM whiskey w WHERE w.english_name = 'Yamazaki 12 Year Old' UNION ALL
SELECT w.id, '🍖', '바비큐'        FROM whiskey w WHERE w.english_name = 'Buffalo Trace Bourbon' UNION ALL
SELECT w.id, '🥧', '피칸 파이'    FROM whiskey w WHERE w.english_name = 'Buffalo Trace Bourbon' UNION ALL
SELECT w.id, '🍋', '레몬'          FROM whiskey w WHERE w.english_name = 'Tanqueray London Dry Gin' UNION ALL
SELECT w.id, '🌿', '토닉 워터'    FROM whiskey w WHERE w.english_name = 'Tanqueray London Dry Gin' UNION ALL
SELECT w.id, '🥩', '스테이크'     FROM whiskey w WHERE w.english_name = 'Chateau Margaux 2015' UNION ALL
SELECT w.id, '🧀', '숙성 치즈'    FROM whiskey w WHERE w.english_name = 'Chateau Margaux 2015' UNION ALL
SELECT w.id, '🍣', '사시미'        FROM whiskey w WHERE w.english_name = 'Daiginjo Junmai Sake' UNION ALL
SELECT w.id, '🦐', '새우 튀김'    FROM whiskey w WHERE w.english_name = 'Daiginjo Junmai Sake' UNION ALL
SELECT w.id, '🥧', '미트 파이'    FROM whiskey w WHERE w.english_name = 'Guinness Draught' UNION ALL
SELECT w.id, '🦪', '굴'            FROM whiskey w WHERE w.english_name = 'Guinness Draught';

INSERT INTO whiskey_flavor_tags (whiskey_id, flavor_tag)
SELECT w.id, t.tag FROM whiskey w JOIN (VALUES
  ('The Glenlivet 12 Year Old','부드러움'),('The Glenlivet 12 Year Old','달콤함'),('The Glenlivet 12 Year Old','과일향'),('The Glenlivet 12 Year Old','바닐라'),
  ('Laphroaig 10 Year Old','피트'),('Laphroaig 10 Year Old','스모키'),('Laphroaig 10 Year Old','약품'),('Laphroaig 10 Year Old','바다'),
  ('Macallan 12 Year Old Sherry Oak','셰리'),('Macallan 12 Year Old Sherry Oak','과일'),('Macallan 12 Year Old Sherry Oak','스파이시'),('Macallan 12 Year Old Sherry Oak','오크'),
  ('Johnnie Walker Black Label','스모키'),('Johnnie Walker Black Label','균형'),('Johnnie Walker Black Label','부드러움'),
  ('Yamazaki 12 Year Old','과일'),('Yamazaki 12 Year Old','꿀'),('Yamazaki 12 Year Old','섬세함'),('Yamazaki 12 Year Old','우아함'),
  ('Buffalo Trace Bourbon','바닐라'),('Buffalo Trace Bourbon','캐러멜'),('Buffalo Trace Bourbon','오크'),('Buffalo Trace Bourbon','스파이시'),
  ('Tanqueray London Dry Gin','주니퍼'),('Tanqueray London Dry Gin','시트러스'),('Tanqueray London Dry Gin','드라이'),('Tanqueray London Dry Gin','상쾌함'),
  ('Chateau Margaux 2015','우아함'),('Chateau Margaux 2015','복합성'),('Chateau Margaux 2015','타닌'),('Chateau Margaux 2015','과일'),
  ('Daiginjo Junmai Sake','섬세함'),('Daiginjo Junmai Sake','과일'),('Daiginjo Junmai Sake','깨끗함'),('Daiginjo Junmai Sake','부드러움'),
  ('Guinness Draught','로스티드'),('Guinness Draught','커피'),('Guinness Draught','크리미'),('Guinness Draught','부드러움')
) AS t(english_name, tag) ON w.english_name = t.english_name;
