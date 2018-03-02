package com.kingdom.repository

import com.kingdom.model.RecommendedSet
import org.springframework.data.repository.CrudRepository

interface RecommendedSetRepository : CrudRepository<RecommendedSet, Int> {

    fun findAllByOrderByIdAsc(): List<RecommendedSet>
}
