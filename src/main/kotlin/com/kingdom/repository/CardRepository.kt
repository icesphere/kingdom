package com.kingdom.repository

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.intrigue.*
import com.kingdom.model.cards.kingdom.*
import org.springframework.stereotype.Service

@Service
class CardRepository {

    val kingdomCards: List<Card>
        get() = listOf(
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

    val intrigueCards: List<Card>
        get() = listOf(
                Courtyard(),
                Lurker(),
                Pawn(),
                Masquerade(),
                ShantyTown(),
                Steward(),
                Swindler(),
                WishingWell(),
                Baron(),
                Bridge(),
                Conspirator(),
                Diplomat(),
                Ironworks(),
                Mill(),
                MiningVillage(),
                Courtier(),
                Duke(),
                Minion(),
                Patrol(),
                Replace(),
                Torturer(),
                TradingPost(),
                Upgrade(),
                TreasureRoom(),
                Nobles()
        )

    val allCards: List<Card>
        get() = kingdomCards + intrigueCards

    fun getCardsByDeck(deck: Deck): List<Card> {
        return when (deck) {
            Deck.Kingdom -> kingdomCards
            Deck.Intrigue -> intrigueCards
            else -> emptyList()
        }
    }


}
