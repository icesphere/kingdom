package com.kingdom.repository;

import com.kingdom.model.GameUserHistory;
import org.springframework.data.repository.CrudRepository;

public interface GameUserHistoryRepository extends CrudRepository<GameUserHistory, Integer> {
}
