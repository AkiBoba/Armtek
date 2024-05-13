package com.example.reactive.repository;

import com.example.reactive.domain.CatLinksErrors;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CatLinksErrorsRepo extends ReactiveCrudRepository<CatLinksErrors, Long> {
}
