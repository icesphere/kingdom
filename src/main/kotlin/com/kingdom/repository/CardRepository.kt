package com.kingdom.repository

import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.Card
import org.springframework.data.repository.CrudRepository

interface CardRepository : CrudRepository<Card, Int> {

    fun findByPrizeCardOrderByNameAsc(prizeCard: Boolean): List<Card>

    fun findByName(name: String): Card

    fun findByDeckAndTestingAndDisabledAndPrizeCardOrderByNameAsc(deck: Deck, testing: Boolean, disabled: Boolean, prizeCard: Boolean): List<Card>

    fun findByDeckAndPrizeCardOrderByNameAsc(deck: Deck, prizeCard: Boolean): List<Card>

    fun findByFanExpansionCardAndDisabledAndPrizeCardOrderByNameAsc(fanExpansionCard: Boolean, disabled: Boolean, prizeCard: Boolean): List<Card>

}
