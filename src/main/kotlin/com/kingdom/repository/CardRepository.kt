package com.kingdom.repository

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.kingdom.*
import org.springframework.stereotype.Service

@Service
class CardRepository {

    fun getKingdomCards(): List<Card> {
        return listOf(
                Artisan(),
                Bandit(),
                Bureaucrat(),
                Cellar(),
                Chapel(),
                CouncilRoom(),
                Festival(),
                Gardens(),
                Harbinger(),
                Laboratory(),
                Library(),
                Market(),
                Merchant(),
                Militia(),
                Mine(),
                Moat(),
                Moneylender(),
                Poacher(),
                Remodel(),
                Sentry(),
                Smithy(),
                ThroneRoom(),
                Vassal(),
                Village(),
                Witch(),
                Workshop()
        )
    }

    fun getAllCards(): List<Card> {
        return getKingdomCards()
    }

    fun getCardsByDeck(deck: Deck): List<Card> {
        return when (deck) {
            Deck.Kingdom -> getKingdomCards()
            else -> emptyList()
        }
    }


}
