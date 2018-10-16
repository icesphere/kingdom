package com.kingdom.repository

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.intrigue.*
import com.kingdom.model.cards.kingdom.*
import com.kingdom.model.cards.seaside.*
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
                Baron(),
                Bridge(),
                Conspirator(),
                Courtier(),
                Courtyard(),
                Diplomat(),
                Duke(),
                Ironworks(),
                Lurker(),
                Masquerade(),
                Mill(),
                MiningVillage(),
                Minion(),
                Nobles(),
                Patrol(),
                Pawn(),
                Replace(),
                ShantyTown(),
                Steward(),
                Swindler(),
                Torturer(),
                TradingPost(),
                TreasureRoom(),
                Upgrade(),
                WishingWell()
        )

    val seasideCards: List<Card>
        get() = listOf(
                Embargo(),
                Haven(),
                Lighthouse(),
                NativeVillage(),
                PearlDiver()
        )

    val allCards: List<Card>
        get() = kingdomCards + intrigueCards

    fun getCardsByDeck(deck: Deck): List<Card> {
        return when (deck) {
            Deck.Kingdom -> kingdomCards
            Deck.Intrigue -> intrigueCards
            Deck.Seaside -> seasideCards
            else -> emptyList()
        }
    }


}
