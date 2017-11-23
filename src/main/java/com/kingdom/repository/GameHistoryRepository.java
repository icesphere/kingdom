package com.kingdom.repository;

import com.kingdom.model.GameHistory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameHistoryRepository extends CrudRepository<GameHistory, Integer> {

    List<GameHistory> findTop80ByOrderByGameIdDesc();
}
