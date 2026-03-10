-- =============================================
-- Whiskey Backend - MySQL Schema
-- charset: utf8mb4 / collation: utf8mb4_unicode_ci
-- =============================================

CREATE DATABASE IF NOT EXISTS whiskeydb
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE whiskeydb;

-- 위스키 메인 테이블
CREATE TABLE IF NOT EXISTS whiskey (
    id             BIGINT          NOT NULL AUTO_INCREMENT,
    name           VARCHAR(200)    NOT NULL COMMENT '위스키명',
    english_name   VARCHAR(200)    UNIQUE   COMMENT '영문명',
    brand          VARCHAR(200)    NOT NULL COMMENT '브랜드',
    category       ENUM(
                       'SINGLE_MALT',
                       'BLENDED',
                       'WORLD_WHISKEY',
                       'GIN_VODKA',
                       'WINE_LIQUEUR',
                       'SAKE_TRADITIONAL',
                       'BEER_SOJU'
                   )               NOT NULL COMMENT '카테고리',
    abv            DECIMAL(5,2)    COMMENT '알코올 도수 (%)',
    volume         DECIMAL(7,2)    COMMENT '용량 (ml)',
    nation         VARCHAR(100)    COMMENT '국가',
    region         VARCHAR(100)    COMMENT '생산지역',
    image_data_url VARCHAR(500)    COMMENT '이미지 URL',
    notes          TEXT            COMMENT '테이스팅 노트',
    nose           VARCHAR(1000)   COMMENT '노즈',
    palate         VARCHAR(1000)   COMMENT '팔레트',
    finish         VARCHAR(1000)   COMMENT '피니시',
    personal_note  TEXT            COMMENT '개인 소감',
    star_point     DECIMAL(3,1)    COMMENT '별점',
    created_at     BIGINT          NOT NULL COMMENT '생성일시 (timestamp)',
    updated_at     BIGINT          NOT NULL COMMENT '수정일시 (timestamp)',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 특성 테이블 (셰리, 피트, 버번 - 싱글몰트 서브필터)
CREATE TABLE IF NOT EXISTS whiskey_characteristics (
    whiskey_id     BIGINT          NOT NULL,
    characteristic ENUM('SHERRY','PEAT','BOURBON') NOT NULL COMMENT '특성',
    CONSTRAINT fk_characteristics_whiskey FOREIGN KEY (whiskey_id) REFERENCES whiskey(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 페어링 테이블
CREATE TABLE IF NOT EXISTS whiskey_pairings (
    whiskey_id     BIGINT          NOT NULL,
    icon           VARCHAR(10)     COMMENT '아이콘 (이모지)',
    name           VARCHAR(100)    COMMENT '페어링명',
    CONSTRAINT fk_pairings_whiskey FOREIGN KEY (whiskey_id) REFERENCES whiskey(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 플레이버 태그 테이블
CREATE TABLE IF NOT EXISTS whiskey_flavor_tags (
    whiskey_id     BIGINT          NOT NULL,
    flavor_tag     VARCHAR(100)    NOT NULL COMMENT '플레이버 태그',
    CONSTRAINT fk_flavor_tags_whiskey FOREIGN KEY (whiskey_id) REFERENCES whiskey(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스
CREATE INDEX idx_whiskey_category    ON whiskey (category);
CREATE INDEX idx_whiskey_nation      ON whiskey (nation);
CREATE INDEX idx_whiskey_name        ON whiskey (name);
CREATE INDEX idx_whiskey_brand       ON whiskey (brand);
CREATE INDEX idx_whiskey_star_point  ON whiskey (star_point);
