package com.example.reactive.repository;

import com.example.reactive.domain.GoodsLinksError;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface GoodsLinksErrorRepo extends ReactiveCrudRepository<GoodsLinksError, Long> {
}
