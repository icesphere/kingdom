package com.kingdom.repository

import com.kingdom.model.GameHistory
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface GameHistoryRepository : CrudRepository<GameHistory, Int> {

    fun findTop80ByOrderByGameIdDesc(): List<GameHistory>

}
