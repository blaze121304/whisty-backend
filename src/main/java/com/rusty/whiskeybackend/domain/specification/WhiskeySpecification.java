package com.rusty.whiskeybackend.domain.specification;

import com.rusty.whiskeybackend.domain.entity.Whiskey;
import com.rusty.whiskeybackend.domain.enums.WhiskeyCategory;
import com.rusty.whiskeybackend.domain.enums.WhiskeyCharacteristic;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class WhiskeySpecification {

    private static final List<WhiskeyCategory> MAIN_STYLES = List.of(
            WhiskeyCategory.SINGLE_MALT, WhiskeyCategory.BLENDED, WhiskeyCategory.GRAIN_BOURBON_RYE
    );

    private static final List<WhiskeyCharacteristic> MAIN_CASKS = List.of(
            WhiskeyCharacteristic.SHERRY, WhiskeyCharacteristic.BOURBON,
            WhiskeyCharacteristic.WINE_PORT, WhiskeyCharacteristic.PEAT
    );

    private static final List<String> MAIN_NATIONS = List.of("스코틀랜드", "미국", "일본", "한국");

    // 스타일 필터 (SINGLE_MALT | BLENDED | GRAIN_BOURBON_RYE | OTHER)
    public static Specification<Whiskey> hasStyle(String style) {
        if (style == null) return null;
        if ("OTHER".equals(style)) {
            return (root, query, cb) -> cb.not(root.get("category").in(MAIN_STYLES));
        }
        WhiskeyCategory category = WhiskeyCategory.valueOf(style);
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    // 캐스크 필터 (SHERRY | BOURBON | WINE_PORT | PEAT | OTHER)
    public static Specification<Whiskey> hasCask(String cask) {
        if (cask == null) return null;
        if ("OTHER".equals(cask)) {
            // 메인 캐스크 특성이 하나도 없는 위스키
            return (root, query, cb) -> {
                Subquery<Long> sub = query.subquery(Long.class);
                Root<Whiskey> subRoot = sub.correlate(root);
                Join<Object, Object> join = subRoot.join("characteristics");
                sub.select(subRoot.get("id")).where(join.in(MAIN_CASKS));
                return cb.not(cb.exists(sub));
            };
        }
        WhiskeyCharacteristic characteristic = WhiskeyCharacteristic.valueOf(cask);
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<Whiskey> subRoot = sub.correlate(root);
            Join<Object, Object> join = subRoot.join("characteristics");
            sub.select(subRoot.get("id")).where(cb.equal(join, characteristic));
            return cb.exists(sub);
        };
    }

    // 국가 필터 (스코틀랜드 | 미국 | 일본 | 한국 | OTHER)
    public static Specification<Whiskey> hasNation(String nation) {
        if (nation == null) return null;
        if ("OTHER".equals(nation)) {
            return (root, query, cb) -> cb.or(
                    cb.not(root.get("nation").in(MAIN_NATIONS)),
                    root.get("nation").isNull()
            );
        }
        return (root, query, cb) -> cb.equal(root.get("nation"), nation);
    }

    // 검색어 필터
    public static Specification<Whiskey> hasSearch(String search) {
        if (search == null || search.isBlank()) return null;
        String pattern = "%" + search.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("brand")), pattern)
        );
    }
}
