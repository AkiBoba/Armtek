package com.example.reactive.repository;

import com.example.reactive.domain.GoodInfo;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface GoodInfoRepository extends ReactiveCrudRepository<GoodInfo, Long> {

    @Modifying
    @Query(value = "UPDATE alliance_goods SET description = :description, width = :width, height = :height, length = :length, weight = :weight WHERE article = :article AND name = :name")
    int update(@Param("description") String description, @Param("width") String width, @Param("height") String height, @Param("length") String length, @Param("weight") String weight, @Param("article") String article, @Param("name") String name);
}
