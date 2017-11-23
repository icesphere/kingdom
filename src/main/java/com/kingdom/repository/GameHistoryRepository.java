package com.kingdom.repository;

import com.kingdom.model.GameHistory;
import org.springframework.data.repository.CrudRepository;

public interface GameHistoryRepository extends CrudRepository<GameHistory, Integer> {
}
