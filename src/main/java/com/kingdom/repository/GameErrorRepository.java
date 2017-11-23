package com.kingdom.repository;

import com.kingdom.model.GameError;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameErrorRepository extends CrudRepository<GameError, Integer> {

    List<GameError> findTop50ByOrderByErrorIdDesc();
}
