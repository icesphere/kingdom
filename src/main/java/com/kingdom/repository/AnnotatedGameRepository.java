package com.kingdom.repository;

import com.kingdom.model.AnnotatedGame;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AnnotatedGameRepository extends CrudRepository<AnnotatedGame, Integer> {

    List<AnnotatedGame> findAllByOrderByGameIdDesc();
}
