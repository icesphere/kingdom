package com.kingdom.repository

import com.kingdom.model.Card
import org.springframework.data.repository.CrudRepository

interface CardRepository : CrudRepository<Card, Int> {

    fun findByPrizeCardOrderByNameAsc(prizeCard: Boolean): List<Card>

    fun findByName(name: String): Card

    fun findByDeckStringAndTestingAndDisabledAndPrizeCardOrderByNameAsc(deckString: String, testing: Boolean, disabled: Boolean, prizeCard: Boolean): List<Card>

    fun findByDeckStringAndPrizeCardOrderByNameAsc(deckString: String, prizeCard: Boolean): List<Card>

    fun findByFanExpansionCardAndDisabledAndPrizeCardOrderByNameAsc(fanExpansionCard: Boolean, disabled: Boolean, prizeCard: Boolean): List<Card>

}
