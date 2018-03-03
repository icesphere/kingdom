package com.kingdom.service

import com.kingdom.model.cards.Card
import com.kingdom.model.Deck
import com.kingdom.model.Game
import com.kingdom.repository.CardRepository
import com.kingdom.util.CardRandomizer
import org.springframework.stereotype.Service

import java.util.Collections

@Service
class CardManager(private val cardRepository: CardRepository,
                  private val cardRandomizer: CardRandomizer) {

    val prizeCards: MutableList<Card>
        get() = cardRepository.findByPrizeCardOrderByNameAsc(true).toMutableList()

    val availableLeaderCards: MutableList<Card>
        get() {
            val cards = getCardsByDeck(Deck.Leaders, false)
            Collections.shuffle(cards)
            return cards.subList(0, 7).toMutableList()
        }

    fun getAllCards(includeFanExpansionCards: Boolean): List<Card> {
        return cardRepository.findByFanExpansionCardAndDisabledAndPrizeCardOrderByNameAsc(includeFanExpansionCards, false, false)
    }

    fun getCards(deck: Deck, includeTesting: Boolean): List<Card> {
        return getCardsByDeck(deck, includeTesting)
    }

    fun getCard(cardId: Int): Card {
        return cardRepository.findById(cardId).get()
    }

    fun getCard(cardName: String): Card {
        return cardRepository.findByName(cardName)
    }

    fun saveCard(card: Card) {
        cardRepository.save(card)
    }

    fun setRandomKingdomCards(game: Game) {
        cardRandomizer.setRandomKingdomCards(game, game.randomizingOptions!!)
    }

    fun swapRandomCard(game: Game, cardId: Int) {
        cardRandomizer.swapRandomCard(game, cardId)
    }

    fun swapForTypeOfCard(game: Game, cardId: Int, cardType: String) {
        cardRandomizer.swapCard(game, cardId, cardType)
    }

    private fun getCardsByDeck(deck: Deck, includeTesting: Boolean): List<Card> {
        return if (!includeTesting) {
            cardRepository.findByDeckAndTestingAndDisabledAndPrizeCardOrderByNameAsc(deck, false, false, false)
        } else {
            cardRepository.findByDeckAndPrizeCardOrderByNameAsc(deck, false)
        }
    }
}
