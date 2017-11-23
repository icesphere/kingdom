package com.kingdom.repository;

import com.kingdom.model.RecommendedSet;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecommendedSetRepository extends CrudRepository<RecommendedSet, Integer> {

    List<RecommendedSet> findAllByOrderByIdAsc();
}
