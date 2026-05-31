package com.kingdom.service

import com.kingdom.model.Game
import com.kingdom.model.cards.*
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

    val allEvents: List<Event>
        get() = cardRepository.allEvents

    val allLandmarks: List<Landmark>
        get() = cardRepository.allLandmarks
    
    val allProjects: List<Project>
        get() = cardRepository.allProjects

    val allWays: List<Way>
        get() = cardRepository.allWays

    val allAllies: List<Ally>
        get() = cardRepository.allAllies

    val allTraits: List<Trait>
        get() = cardRepository.allTraits

    val allProphecies: List<Prophecy>
        get() = cardRepository.allProphecies

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

    fun getEvent(eventName: String): Event {
        return allEvents.first { it.name == eventName }
    }

    fun getLandmark(landmarkName: String): Landmark {
        return allLandmarks.first { it.name == landmarkName }
    }

    fun getProject(projectName: String): Project {
        return allProjects.first { it.name == projectName }
    }

    fun getWay(wayName: String): Way {
        return allWays.first { it.name == wayName }
    }

    fun getAlly(allyName: String): Ally {
        return allAllies.first { it.name == allyName }
    }

    fun getTrait(traitName: String): Trait {
        return allTraits.first { it.name == traitName }
    }

    fun setRandomKingdomCardsAndEvents(game: Game) {
        cardRandomizer.setRandomKingdomCardsAndEvents(game, game.randomizingOptions!!)
    }

    fun swapRandomCard(game: Game, cardName: String) {
        cardRandomizer.swapRandomCard(game, cardName)
    }

    fun swapEvent(game: Game, eventName: String) {
        cardRandomizer.swapEvent(game, eventName)
    }

    fun swapLandmark(game: Game, landmarkName: String) {
        cardRandomizer.swapLandmark(game, landmarkName)
    }

    fun swapProject(game: Game, projectName: String) {
        cardRandomizer.swapProject(game, projectName)
    }

    fun swapWay(game: Game, wayName: String) {
        cardRandomizer.swapWay(game, wayName)
    }

    fun swapAlly(game: Game, allyName: String) {
        cardRandomizer.swapAlly(game, allyName)
    }

    fun swapTrait(game: Game, traitName: String) {
        cardRandomizer.swapTrait(game, traitName)
    }

    fun swapProphecy(game: Game, prophecyName: String) {
        cardRandomizer.swapProphecy(game, prophecyName)
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
