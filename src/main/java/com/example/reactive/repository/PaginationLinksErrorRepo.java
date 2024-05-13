package com.example.reactive.repository;

import com.example.reactive.domain.PaginationLinksError;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PaginationLinksErrorRepo extends ReactiveCrudRepository<PaginationLinksError, Long> {
}
