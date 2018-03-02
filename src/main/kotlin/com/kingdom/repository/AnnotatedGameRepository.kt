package com.kingdom.repository

import com.kingdom.model.AnnotatedGame
import org.springframework.data.repository.CrudRepository

interface AnnotatedGameRepository : CrudRepository<AnnotatedGame, Int> {

    fun findAllByOrderByGameIdDesc(): List<AnnotatedGame>
}
