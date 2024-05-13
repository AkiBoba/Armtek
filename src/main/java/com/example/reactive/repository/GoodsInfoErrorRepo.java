package com.example.reactive.repository;

import com.example.reactive.domain.GoodsInfoError;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface GoodsInfoErrorRepo extends ReactiveCrudRepository<GoodsInfoError, Long> {
}
