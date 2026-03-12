package com.rusty.whiskeybackend.repository;

import com.rusty.whiskeybackend.domain.entity.Whiskey;
import com.rusty.whiskeybackend.domain.enums.WhiskeyCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WhiskeyRepository extends JpaRepository<Whiskey, Long>, JpaSpecificationExecutor<Whiskey> {

    long countByCategory(WhiskeyCategory category);
}
