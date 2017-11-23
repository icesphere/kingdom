package com.kingdom.repository;

import com.kingdom.model.GameLog;
import org.springframework.data.repository.CrudRepository;

public interface GameLogRepository extends CrudRepository<GameLog, Integer> {

    GameLog findByGameId(int gameId);
}
