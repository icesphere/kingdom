package com.kingdom.repository

import com.kingdom.model.GameError
import org.springframework.data.repository.CrudRepository

interface GameErrorRepository : CrudRepository<GameError, Int> {

    fun findTop50ByOrderByErrorIdDesc(): List<GameError>
}
