package com.example.reactive.repository;

import com.example.reactive.domain.AlfavitCatLinksError;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AlfavitCatLinksErrorRepo extends ReactiveCrudRepository<AlfavitCatLinksError, Long> {
}
