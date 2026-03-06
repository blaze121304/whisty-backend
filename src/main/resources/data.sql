-- =============================================
-- Whiskey Sample Data  (PK: english_name)
-- =============================================

INSERT INTO whiskey (english_name, name, brand, category, abv, volume, nation, region, notes, nose, palate, finish, personal_note, star_point, image_data_url, created_at, updated_at) VALUES
('The Glenlivet 12 Year Old','더 글렌리벳 12년','Glenlivet','SINGLE_MALT',40.0,700.0,'스코틀랜드','스페이사이드','부드럽고 달콤한 스페이사이드 싱글몰트','바닐라, 아몬드, 꿀의 향','부드러운 과일향과 오크의 균형','중간 길이의 달콤한 여운','입문자에게 추천하는 부드러운 위스키',4.0,NULL,1700000000000,1700000000000),
('Laphroaig 10 Year Old','라프로익 10년','Laphroaig','SINGLE_MALT',40.0,700.0,'스코틀랜드','아일라','강렬한 피트와 스모키한 아일라 위스키','강한 피트, 요오드, 바다 향','스모키하고 약간의 단맛','길고 강렬한 피트 여운','피트 애호가를 위한 클래식',4.0,NULL,1700000000000,1700000000000),
('Macallan 12 Year Old Sherry Oak','맥캘란 12년 셰리오크','Macallan','SINGLE_MALT',40.0,700.0,'스코틀랜드','스페이사이드','풍부한 셰리 캐스크 숙성의 깊은 맛','말린 과일, 생강, 오렌지','셰리의 달콤함과 스파이시함','긴 여운의 말린 과일과 스파이스','셰리 캐스크의 정석',4.0,NULL,1700000000000,1700000000000),
('Johnnie Walker Black Label','조니워커 블랙 라벨','Johnnie Walker','BLENDED_MALT',40.0,700.0,'스코틀랜드','스페이사이드','균형잡힌 블렌디드 스카치','스모키, 과일, 바닐라','크리미하고 스모키한 맛','중간 길이의 스모키한 여운','가성비 좋은 블렌디드 위스키',4.0,NULL,1700000000000,1700000000000),
('Yamazaki 12 Year Old','야마자키 12년','Suntory','WORLD_WHISKEY',43.0,700.0,'일본','야마자키','일본 위스키의 대표작','과일, 꿀, 미즈나라 오크','복숭아, 파인애플, 계피','달콤하고 긴 여운','섬세하고 우아한 재패니즈 위스키',4.0,NULL,1700000000000,1700000000000),
('Tanqueray London Dry Gin','탱커레이 런던 드라이 진','Tanqueray','GIN_VODKA',47.3,750.0,'영국','런던','클래식 런던 드라이 진','주니퍼 베리, 시트러스','크리스프하고 드라이한 맛','상쾌한 여운','진토닉의 정석',4.0,NULL,1700000000000,1700000000000),
('Chateau Margaux 2015','샤또 마고 2015','Chateau Margaux','WINE_LIQUEUR',13.5,750.0,'프랑스','보르도','보르도 최고급 와인','블랙베리, 바이올렛, 시더','실키하고 우아한 타닌','매우 긴 여운','특별한 날을 위한 와인',4.0,NULL,1700000000000,1700000000000),
('Daiginjo Junmai Sake','다이긴조 준마이','타테야마','SAKE_TRADITIONAL',16.0,720.0,'일본','기후','프리미엄 일본 사케','과일, 꽃향기, 쌀','깨끗하고 섬세한 맛','부드럽고 상쾌한 여운','차갑게 또는 미지근하게',4.0,NULL,1700000000000,1700000000000),
('Guinness Draught','기네스 드래프트','Guinness','BEER',4.2,440.0,'영국','아이리시','아이리시 스타우트의 대표','로스티드 몰트, 커피','크리미하고 부드러운 질감','약간 쓴 여운','캔으로도 드래프트 느낌',4.0,NULL,1700000000000,1700000000000),
('Buffalo Trace Bourbon','버팔로 트레이스','Buffalo Trace','WORLD_WHISKEY',45.0,750.0,'미국','켄터키','켄터키 버번의 클래식','바닐라, 캐러멜, 오크','달콤한 옥수수와 라이 스파이스','긴 여운의 캐러멜과 오크','가성비 좋은 버번',4.0,NULL,1700000000000,1700000000000);

INSERT INTO whiskey_sub_categories (whiskey_id, sub_category) VALUES
('The Glenlivet 12 Year Old','SHERRY'),
('Laphroaig 10 Year Old','PEAT'),
('Macallan 12 Year Old Sherry Oak','SHERRY'),
('Buffalo Trace Bourbon','BOURBON');

INSERT INTO whiskey_pairings (whiskey_id, icon, name) VALUES
('The Glenlivet 12 Year Old','🍫','다크 초콜릿'),('The Glenlivet 12 Year Old','🧀','부드러운 치즈'),
('Laphroaig 10 Year Old','🥩','훈제 고기'),('Laphroaig 10 Year Old','🦞','해산물'),
('Macallan 12 Year Old Sherry Oak','🍰','과일 케이크'),('Macallan 12 Year Old Sherry Oak','🥜','견과류'),
('Johnnie Walker Black Label','🍔','버거'),('Johnnie Walker Black Label','🥓','베이컨'),
('Yamazaki 12 Year Old','🍣','스시'),('Yamazaki 12 Year Old','🍡','화과자'),
('Tanqueray London Dry Gin','🍋','레몬'),('Tanqueray London Dry Gin','🌿','토닉 워터'),
('Chateau Margaux 2015','🥩','스테이크'),('Chateau Margaux 2015','🧀','숙성 치즈'),
('Daiginjo Junmai Sake','🍣','사시미'),('Daiginjo Junmai Sake','🦐','새우 튀김'),
('Guinness Draught','🥧','미트 파이'),('Guinness Draught','🦪','굴'),
('Buffalo Trace Bourbon','🍖','바비큐'),('Buffalo Trace Bourbon','🥧','피칸 파이');

INSERT INTO whiskey_flavor_tags (whiskey_id, flavor_tag) VALUES
('The Glenlivet 12 Year Old','부드러움'),('The Glenlivet 12 Year Old','달콤함'),('The Glenlivet 12 Year Old','과일향'),('The Glenlivet 12 Year Old','바닐라'),
('Laphroaig 10 Year Old','피트'),('Laphroaig 10 Year Old','스모키'),('Laphroaig 10 Year Old','약품'),('Laphroaig 10 Year Old','바다'),
('Macallan 12 Year Old Sherry Oak','셰리'),('Macallan 12 Year Old Sherry Oak','과일'),('Macallan 12 Year Old Sherry Oak','스파이시'),('Macallan 12 Year Old Sherry Oak','오크'),
('Johnnie Walker Black Label','스모키'),('Johnnie Walker Black Label','균형'),('Johnnie Walker Black Label','부드러움'),
('Yamazaki 12 Year Old','과일'),('Yamazaki 12 Year Old','꿀'),('Yamazaki 12 Year Old','섬세함'),('Yamazaki 12 Year Old','우아함'),
('Tanqueray London Dry Gin','주니퍼'),('Tanqueray London Dry Gin','시트러스'),('Tanqueray London Dry Gin','드라이'),('Tanqueray London Dry Gin','상쾌함'),
('Chateau Margaux 2015','우아함'),('Chateau Margaux 2015','복합성'),('Chateau Margaux 2015','타닌'),('Chateau Margaux 2015','과일'),
('Daiginjo Junmai Sake','섬세함'),('Daiginjo Junmai Sake','과일'),('Daiginjo Junmai Sake','깨끗함'),('Daiginjo Junmai Sake','부드러움'),
('Guinness Draught','로스티드'),('Guinness Draught','커피'),('Guinness Draught','크리미'),('Guinness Draught','부드러움'),
('Buffalo Trace Bourbon','바닐라'),('Buffalo Trace Bourbon','캐러멜'),('Buffalo Trace Bourbon','오크'),('Buffalo Trace Bourbon','스파이시');
