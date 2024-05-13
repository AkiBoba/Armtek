package com.example.reactive.repository;

import com.example.reactive.domain.ArmGoodInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArmtekGoodInfoRepository extends ReactiveCrudRepository<ArmGoodInfo, Long> {
}
