package com.kingdom.util

import com.kingdom.model.Game
import com.kingdom.model.RandomizingOptions
import com.kingdom.model.cards.*
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

        addEventsAndLandmarksAndProjectsAndWays(game, options)
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

    private fun addEventsAndLandmarksAndProjectsAndWays(game: Game, options: RandomizingOptions) {

        val eventsAndLandmarksAndProjectsAndWays = cardRepository.allEventsAndLandmarksAndProjectsAndWays.filterNot { it.disabled }.shuffled()

        val numEventsAndLandmarksAndProjectsAndWays = options.numEventsAndLandmarksAndProjectsAndWays

        val selectedEventsAndLandmarksAndProjectsAndWays = LinkedList(options.customEventSelection + options.customLandmarkSelection + options.customProjectSelection + options.customWaySelection)

        if (selectedEventsAndLandmarksAndProjectsAndWays.size < numEventsAndLandmarksAndProjectsAndWays) {
            val selectedNames = selectedEventsAndLandmarksAndProjectsAndWays.map { it.name }
            val available = eventsAndLandmarksAndProjectsAndWays.filterNot { selectedNames.contains(it.name) }
            selectedEventsAndLandmarksAndProjectsAndWays.addAll(available.subList(0, numEventsAndLandmarksAndProjectsAndWays - selectedEventsAndLandmarksAndProjectsAndWays.size))
        }

        game.events = selectedEventsAndLandmarksAndProjectsAndWays.filterIsInstance<Event>().toMutableList()

        game.landmarks = selectedEventsAndLandmarksAndProjectsAndWays.filterIsInstance<Landmark>().toMutableList()

        game.projects = selectedEventsAndLandmarksAndProjectsAndWays.filterIsInstance<Project>().toMutableList()

        game.ways = selectedEventsAndLandmarksAndProjectsAndWays.filterIsInstance<Way>().toMutableList()
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
        swapOptions.customLandmarkSelection = game.landmarks
        swapOptions.customProjectSelection = game.projects
        swapOptions.customWaySelection = game.ways
        setRandomKingdomCardsAndEvents(game, swapOptions)
    }

    fun swapEvent(game: Game, eventName: String) {
        
        val replacement = getEventOrLandmarkOrProjectOrWay(game, eventName)

        when (replacement) {
            is Event -> game.events = game.events.map {
                if (it.name == eventName) {
                    replacement
                } else {
                    it
                }
            }.toMutableList()
            is Landmark -> {
                game.events.removeAll { it.name == eventName }
                game.landmarks.add(replacement)
            }
            is Project -> {
                game.events.removeAll { it.name == eventName }
                game.projects.add(replacement)
            }
        }
    }

    fun swapLandmark(game: Game, landmarkName: String) {

        val replacement = getEventOrLandmarkOrProjectOrWay(game, landmarkName)

        when (replacement) {
            is Landmark -> game.landmarks = game.landmarks.map {
                if (it.name == landmarkName) {
                    replacement
                } else {
                    it
                }
            }.toMutableList()
            is Event -> {
                game.landmarks.removeAll { it.name == landmarkName }
                game.events.add(replacement)
            }
            is Project -> {
                game.landmarks.removeAll { it.name == landmarkName }
                game.projects.add(replacement)
            }
        }
    }

    fun swapProject(game: Game, projectName: String) {

        val replacement = getEventOrLandmarkOrProjectOrWay(game, projectName)

        when (replacement) {
            is Project -> game.projects = game.projects.map {
                if (it.name == projectName) {
                    replacement
                } else {
                    it
                }
            }.toMutableList()
            is Event -> {
                game.projects.removeAll { it.name == projectName }
                game.events.add(replacement)
            }
            is Landmark -> {
                game.projects.removeAll { it.name == projectName }
                game.landmarks.add(replacement)
            }
        }
    }

    fun swapWay(game: Game, wayName: String) {

        val replacement = getEventOrLandmarkOrProjectOrWay(game, wayName)

        when (replacement) {
            is Way -> game.ways = game.ways.map {
                if (it.name == wayName) {
                    replacement
                } else {
                    it
                }
            }.toMutableList()
            is Event -> {
                game.ways.removeAll { it.name == wayName }
                game.events.add(replacement)
            }
            is Landmark -> {
                game.ways.removeAll { it.name == wayName }
                game.landmarks.add(replacement)
            }
            is Project -> {
                game.ways.removeAll { it.name == wayName }
                game.projects.add(replacement)
            }
        }
    }

    fun getEventOrLandmarkOrProjectOrWay(game: Game, excludedName: String): Card {

        val cards = cardRepository.allEventsAndLandmarksAndProjectsAndWays.filterNot { it.disabled }.shuffled()

        val selectedCards = game.events + game.landmarks + game.projects + game.ways

        val selectedNames = selectedCards.map { it.name }

        val availableCards = cards.filterNot { selectedNames.contains(it.name) }

        return availableCards.first()
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
