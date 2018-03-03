package com.kingdom.util

import com.kingdom.model.cards.Card
import com.kingdom.model.Deck
import com.kingdom.model.Game
import com.kingdom.model.RandomizingOptions
import com.kingdom.repository.CardRepository
import org.springframework.stereotype.Service

import java.util.ArrayList
import java.util.Collections
import java.util.LinkedList

@Service
class CardRandomizer(private val cardRepository: CardRepository) {

    private var options: RandomizingOptions? = null
    private var rcs: RandomCardsSelected? = null

    private lateinit var selectedCards: LinkedList<Card>

    private var cardSwapped: Boolean = false
    private var changingBaneCard: Boolean = false
    private var replacingCardWithSpecificType: Boolean = false

    fun setRandomKingdomCards(game: Game, options: RandomizingOptions) {
        game.isRandomizerReplacementCardNotFound = false
        this.options = options
        val decks = game.decks
        cardSwapped = false
        changingBaneCard = options.isSwappingCard && options.cardToReplaceIndex == 10
        replacingCardWithSpecificType = options.cardTypeToReplaceWith != null

        selectedCards = LinkedList()
        selectedCards.addAll(options.customSelection)

        rcs = RandomCardsSelected()

        var cards: MutableList<Card> = ArrayList()

        //special case where they only selected alchemy deck
        if (decks.size == 1 && decks[0] == Deck.Alchemy) {
            cards = getCardsByDeck(Deck.Alchemy)
            Collections.shuffle(cards)
            if (options.isSwappingCard) {
                for (card in cards) {
                    if (canAddCard(card)) {
                        selectedCards.add(card)
                        break
                    }
                }
            } else {
                selectedCards.addAll(cards.subList(0, 10 - options.customSelection.size))
            }
            game.kingdomCards = cards.subList(0, 10)
            return
        }

        var includeAlchemy = !options.isThreeToFiveAlchemy
        if (options.isThreeToFiveAlchemy && decks.contains(Deck.Alchemy) && selectedCards.size <= 5) {
            val rand = 1 + (Math.random() * (decks.size - 1 + 1)).toInt()
            if (rand == 1) {
                includeAlchemy = true
            }
        }

        for (deck in decks) {
            if (!options.isThreeToFiveAlchemy || deck != Deck.Alchemy) {
                cards.addAll(getCardsByDeck(deck))
            }
        }
        Collections.shuffle(cards)

        if (includeAlchemy && options.isThreeToFiveAlchemy) {
            var alchemyCardsToInclude = 3
            val alchemyCards = getCardsByDeck(Deck.Alchemy)
            Collections.shuffle(alchemyCards)
            if (alchemyCards[0].cost > 3) {
                alchemyCardsToInclude = 5
            } else if (alchemyCards[0].cost > 2) {
                alchemyCardsToInclude = 4
            }
            for (i in 0 until alchemyCardsToInclude) {
                addSelectedCard(alchemyCards[i])
            }
        }

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
            Collections.shuffle(selectedCards)
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
                    if (canAddCard(card) && card.cardId != baneCard.cardId) {
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
        return (!selectedCards.contains(card) && !options!!.excludedCards.contains(card)
                && (!changingBaneCard || card.cost == 2 || card.cost == 3)
                && (!replacingCardWithSpecificType || cardMatchesType(card, options!!.cardTypeToReplaceWith!!)))
    }

    private fun cardMatchesType(card: Card, type: String): Boolean {
        if (type == "extraBuy") {
            return card.addBuys > 0
        } else if (type == "extraActions") {
            return card.addActions >= 2
        } else if (type == "treasure") {
            return card.isTreasure
        } else if (type == "reaction") {
            return card.isReaction
        } else if (type == "attack") {
            return card.isAttack
        } else if (type == "trashingCard") {
            return card.isTrashingCard
        }
        return false
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
        if (card.cost == 2 && !card.costIncludesPotion) {
            rcs!!.hasTwo = true
        }
        if (card.cost == 3 && !card.costIncludesPotion) {
            rcs!!.hasThree = true
        }
        if (card.cost == 4 && !card.costIncludesPotion) {
            rcs!!.hasFour = true
        }
        if (card.cost == 5 && !card.costIncludesPotion) {
            rcs!!.hasFive = true
        }
    }

    fun swapRandomCard(game: Game, cardId: Int) {
        swapCard(game, cardId, null)
    }

    fun swapCard(game: Game, cardId: Int, cardType: String?) {
        var cardToReplaceIndex = 0
        var cardToReplace: Card? = null
        val cards = game.kingdomCards
        for (i in cards.indices) {
            if (cards[i].cardId == cardId) {
                cardToReplace = cards[i]
                cardToReplaceIndex = i
                break
            }
        }
        val swapOptions = RandomizingOptions()
        swapOptions.cardTypeToReplaceWith = cardType
        swapOptions.isSwappingCard = true
        swapOptions.cardToReplace = cardToReplace
        swapOptions.cardToReplaceIndex = cardToReplaceIndex
        swapOptions.customSelection = cards
        swapOptions.excludedCards.toMutableList().add(cardToReplace!!)
        setRandomKingdomCards(game, swapOptions)
    }

    private fun getCardsByDeck(deck: Deck): MutableList<Card> {
        return cardRepository.findByDeckAndTestingAndDisabledAndPrizeCardOrderByNameAsc(deck, false, false, false).toMutableList()
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