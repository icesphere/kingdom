package com.kingdom.repository;

import com.kingdom.model.AnnotatedGame;
import org.springframework.data.repository.CrudRepository;

public interface AnnotatedGameRepository extends CrudRepository<AnnotatedGame, Integer> {
}
