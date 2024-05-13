package com.example.reactive.repository;

import com.example.reactive.domain.ArmatekLink;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArmLinksRepo extends ReactiveCrudRepository<ArmatekLink, Long> {
}
