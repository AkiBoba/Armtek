package com.example.reactive.repository;

import com.example.reactive.domain.ArmatekGoodLink;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArmGoodsLinksRepo extends ReactiveCrudRepository<ArmatekGoodLink, Long> {
}
