package com.kingdom.service

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.repository.CardRepository
import com.kingdom.util.CardRandomizer
import org.springframework.stereotype.Service

@Service
class CardManager(private val cardRepository: CardRepository,
                  private val cardRandomizer: CardRandomizer) {

    //todo
    val prizeCards: MutableList<Card> = emptyList<Card>().toMutableList()

    val allCards: List<Card>
        get() = cardRepository.allCards

    val shelters: List<Card>
        get() = cardRepository.shelters

    val ruins: List<Card>
        get() = cardRepository.ruins

    fun getCards(deck: Deck, includeTesting: Boolean): List<Card> {
        return getCardsByDeck(deck, includeTesting)
    }

    fun getCard(cardName: String): Card {
        return allCards.first { it.name == cardName }
    }

    fun setRandomKingdomCards(game: Game) {
        cardRandomizer.setRandomKingdomCards(game, game.randomizingOptions!!)
    }

    fun swapRandomCard(game: Game, cardName: String) {
        cardRandomizer.swapRandomCard(game, cardName)
    }

    fun swapForTypeOfCard(game: Game, cardName: String, cardType: String) {
        cardRandomizer.swapCard(game, cardName, cardType)
    }

    private fun getCardsByDeck(deck: Deck, includeTesting: Boolean): List<Card> {
        var cards = cardRepository.getCardsByDeck(deck)
        if (!includeTesting) {
            cards = cards.filterNot { it.testing }
        }
        cards = cards.filterNot { it.disabled }
        return cards
    }
}
