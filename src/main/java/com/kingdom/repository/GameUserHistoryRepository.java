package com.kingdom.repository;

import com.kingdom.model.GameUserHistory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameUserHistoryRepository extends CrudRepository<GameUserHistory, Integer> {

    List<GameUserHistory> findByGameId(int gameId);
}
