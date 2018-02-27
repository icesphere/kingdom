package com.kingdom.repository

import com.kingdom.model.GameUserHistory
import org.springframework.data.repository.CrudRepository

interface GameUserHistoryRepository : CrudRepository<GameUserHistory, Int> {

    fun findByGameId(gameId: Int): List<GameUserHistory>
}
