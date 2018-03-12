package com.kingdom.service

import com.kingdom.model.OldGame
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

    fun getAllCards(): List<Card> {
        return cardRepository.getAllCards()
    }

    fun getCards(deck: Deck, includeTesting: Boolean): List<Card> {
        return getCardsByDeck(deck, includeTesting)
    }

    fun getCard(cardName: String): Card {
        return getAllCards().first { it.name == cardName }
    }

    fun setRandomKingdomCards(game: OldGame) {
        cardRandomizer.setRandomKingdomCards(game, game.randomizingOptions!!)
    }

    fun swapRandomCard(game: OldGame, cardName: String) {
        cardRandomizer.swapRandomCard(game, cardName)
    }

    fun swapForTypeOfCard(game: OldGame, cardName: String, cardType: String) {
        cardRandomizer.swapCard(game, cardName, cardType)
    }

    private fun getCardsByDeck(deck: Deck, includeTesting: Boolean): List<Card> {
        var cards = cardRepository.getCardsByDeck(deck)
        if (!includeTesting) {
            cards = cards.filterNot { it.testing }
        }
        return cards
    }
}
