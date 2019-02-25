package com.kingdom.util

import com.kingdom.model.Game
import com.kingdom.model.RandomizingOptions
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.Event
import com.kingdom.model.cards.Landmark
import com.kingdom.repository.CardRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class CardRandomizer(private val cardRepository: CardRepository) {

    private var options: RandomizingOptions? = null
    private var rcs: RandomCardsSelected? = null

    private lateinit var selectedCards: LinkedList<Card>

    private var cardSwapped: Boolean = false
    private var changingBaneCard: Boolean = false

    fun setRandomKingdomCardsAndEvents(game: Game, options: RandomizingOptions) {
        this.options = options

        addCards(game, options)

        addEventsAndLandmarks(game, options)
    }

    private fun addCards(game: Game, options: RandomizingOptions) {
        game.isRandomizerReplacementCardNotFound = false

        val decks = game.decks

        cardSwapped = false
        changingBaneCard = options.isSwappingCard && options.cardToReplaceIndex == 10

        selectedCards = LinkedList(options.customCardSelection)

        rcs = RandomCardsSelected()

        val cards: MutableList<Card> = ArrayList()

        for (deck in decks) {
            cards.addAll(getCardsByDeck(deck))
        }

        cards.shuffle()

        if (options.isOneWithBuy && !rcs!!.hasAdditionalBuys && needMoreCards()) {
            for (card in cards) {
                if (card.addBuys > 0 && canAddCard(card)) {
                    addSelectedCard(card)
                    break
                }
            }
        }

        if (options.isOneWithActions && !rcs!!.hasAdditionalActions && needMoreCards()) {
            for (card in cards) {
                if (card.addActions >= 2 && canAddCard(card)) {
                    addSelectedCard(card)
                    break
                }
            }
        }

        if (options.isOneOfEachCost && needMoreCards()) {
            for (card in cards) {
                if (!rcs!!.hasTwo && card.cost == 2) {
                    addSelectedCard(card)
                } else if (!rcs!!.hasThree && card.cost == 3) {
                    addSelectedCard(card)
                } else if (!rcs!!.hasFour && card.cost == 4) {
                    addSelectedCard(card)
                } else if (!rcs!!.hasFive && card.cost == 5) {
                    addSelectedCard(card)
                }

                if (!needMoreCards()) {
                    break
                }
            }
        }

        if (needMoreCards()) {
            for (card in cards) {
                if (canAddCard(card)) {
                    addSelectedCard(card)
                }
                if (!needMoreCards()) {
                    break
                }
            }
        }

        if (options.isDefenseForAttack && rcs!!.hasAttackCard && !rcs!!.hasDefenseCard) {
            for (card in cards) {
                if (card.isDefense && canAddCard(card)) {
                    selectedCards.removeLast()
                    addSelectedCard(card)
                    break
                }
            }
        }

        if (!options.isSwappingCard) {
            selectedCards.shuffle()
        }

        if (options.isSwappingCard && !cardSwapped) {
            game.isRandomizerReplacementCardNotFound = true
        }

        if (addBaneCard()) {
            for (card in cards) {
                if ((card.cost == 2 || card.cost == 3) && canAddCard(card)) {
                    selectedCards.add(card)
                    break
                }
            }
            if (selectedCards.size < 11) {
                val baneCard: Card = selectedCards.first { it.cost == 2 || it.cost == 3 }
                selectedCards.remove(baneCard)
                for (card in cards) {
                    if (canAddCard(card) && card.name != baneCard.name) {
                        selectedCards.add(card)
                    }
                    if (selectedCards.size == 10) {
                        break
                    }
                }
                selectedCards.add(baneCard)
            }
        }

        game.kingdomCards = selectedCards
    }

    private fun addEventsAndLandmarks(game: Game, options: RandomizingOptions) {

        val eventsAndLandmarks = cardRepository.allEventsAndLandmarks.filterNot { it.disabled }.shuffled()

        val numEventsAndLandmarks = options.numEventsAndLandmarks

        val selectedEventsAndLandmarks = LinkedList(options.customEventSelection + options.customLandmarkSelection)

        if (selectedEventsAndLandmarks.size < numEventsAndLandmarks) {
            val selectedEventAndLandmarkNames = selectedEventsAndLandmarks.map { it.name }
            val availableEventsAndLandmarks = eventsAndLandmarks.filterNot { selectedEventAndLandmarkNames.contains(it.name) }
            selectedEventsAndLandmarks.addAll(availableEventsAndLandmarks.subList(0, numEventsAndLandmarks - selectedEventsAndLandmarks.size))
        }

        @Suppress("UNCHECKED_CAST")
        game.events = (selectedEventsAndLandmarks.filter { it.isEvent } as List<Event>).toMutableList()

        @Suppress("UNCHECKED_CAST")
        game.landmarks = (selectedEventsAndLandmarks.filter { it.isLandmark } as List<Landmark>).toMutableList()
    }

    private fun addBaneCard(): Boolean {
        if (selectedCards.size == 11) {
            return false
        }
        for (card in selectedCards) {
            if (card.name == "Young Witch") {
                return true
            }
        }
        return false
    }

    private fun needMoreCards(): Boolean {
        return if (options!!.isSwappingCard) {
            !cardSwapped
        } else {
            selectedCards.size < 10
        }
    }

    private fun addSelectedCard(card: Card): Boolean {
        if (canAddCard(card)) {
            if (options!!.isSwappingCard) {
                selectedCards[options!!.cardToReplaceIndex] = card
                cardSwapped = true
                if (options!!.cardToReplace!!.name == "Young Witch" && selectedCards.size > 10) {
                    selectedCards.removeAt(10)
                }
            } else {
                selectedCards.add(card)
            }
            randomCardSelected(card)
            return true
        }
        return false
    }

    private fun canAddCard(card: Card): Boolean {
        return (!selectedCards.map { it.name }.contains(card.name) && !options!!.excludedCards.contains(card)
                && (!changingBaneCard || card.cost == 2 || card.cost == 3))
    }

    private fun randomCardSelected(card: Card) {
        if (card.addBuys > 0) {
            rcs!!.hasAdditionalBuys = true
        }
        if (card.addActions >= 2) {
            rcs!!.hasAdditionalActions = true
        }
        if (card.isAttack) {
            rcs!!.hasAttackCard = true
        }
        if (card.isDefense) {
            rcs!!.hasDefenseCard = true
        }
        if (card.cost == 2) {
            rcs!!.hasTwo = true
        }
        if (card.cost == 3) {
            rcs!!.hasThree = true
        }
        if (card.cost == 4) {
            rcs!!.hasFour = true
        }
        if (card.cost == 5) {
            rcs!!.hasFive = true
        }
    }

    fun swapRandomCard(game: Game, cardName: String) {
        swapCard(game, cardName)
    }

    fun swapCard(game: Game, cardName: String) {
        var cardToReplaceIndex = 0
        var cardToReplace: Card? = null
        val cards = game.topKingdomCards
        for (i in cards.indices) {
            if (cards[i].name == cardName) {
                cardToReplace = cards[i]
                cardToReplaceIndex = i
                break
            }
        }
        val swapOptions = RandomizingOptions()
        swapOptions.isSwappingCard = true
        swapOptions.cardToReplace = cardToReplace
        swapOptions.cardToReplaceIndex = cardToReplaceIndex
        swapOptions.customCardSelection = cards
        swapOptions.excludedCards.toMutableList().add(cardToReplace!!)
        swapOptions.customEventSelection = game.events
        setRandomKingdomCardsAndEvents(game, swapOptions)
    }

    fun swapEventOrLandmark(game: Game, name: String) {

        val eventsAndLandmarks = cardRepository.allEventsAndLandmarks.filterNot { it.disabled }.shuffled()

        val selectedEventsAndLandmarks = LinkedList(game.events + game.landmarks)

        val selectedNames = selectedEventsAndLandmarks.map { it.name }

        val availableEventsAndLandmarks = eventsAndLandmarks.filterNot { selectedNames.contains(it.name) }

        val replacement = availableEventsAndLandmarks.first()

        if (replacement.isEvent) {
            game.events = game.events.map {
                if (it.name == name) {
                    replacement as Event
                } else {
                    it
                }
            }.toMutableList()
        } else if (replacement.isLandmark) {
            game.landmarks = game.landmarks.map {
                if (it.name == name) {
                    replacement as Landmark
                } else {
                    it
                }
            }.toMutableList()
        }

    }

    private fun getCardsByDeck(deck: Deck): MutableList<Card> {
        return cardRepository.getCardsByDeck(deck)
                .filterNot { it.disabled || it.testing }
                .toMutableList()
    }

    private inner class RandomCardsSelected {
        var hasTwo = false
        var hasThree = false
        var hasFour = false
        var hasFive = false
        var hasAdditionalBuys = false
        var hasAdditionalActions = false
        var hasDefenseCard = false
        var hasAttackCard = false
    }
}
