package com.example.reactive.repository;

import com.example.reactive.domain.ArmatekGoodLink;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ArmGoodsLinksRepo extends ReactiveCrudRepository<ArmatekGoodLink, Long> {
    @Query("delete FROM ArmatekGoodLink where link := url")
    void deleteByLink(String url);
}
