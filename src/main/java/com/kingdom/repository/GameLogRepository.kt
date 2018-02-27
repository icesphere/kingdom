package com.kingdom.repository

import com.kingdom.model.GameLog
import org.springframework.data.repository.CrudRepository

interface GameLogRepository : CrudRepository<GameLog, Int> {

    fun findByGameId(gameId: Int): GameLog
}
