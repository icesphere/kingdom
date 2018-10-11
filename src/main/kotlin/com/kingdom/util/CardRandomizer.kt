package com.kingdom.util

import com.kingdom.model.Game
import com.kingdom.model.RandomizingOptions
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
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
                && (!changingBaneCard || card.cost == 2 || card.cost == 3)
                && (!replacingCardWithSpecificType || cardMatchesType(card, options!!.cardTypeToReplaceWith!!)))
    }

    private fun cardMatchesType(card: Card, type: String): Boolean {
        return when (type) {
            "extraBuy" -> card.addBuys > 0
            "extraActions" -> card.addActions >= 2
            "treasure" -> card.isTreasure
            "reaction" -> card.isReaction
            "attack" -> card.isAttack
            "trashingCard" -> card.isTrashingCard
            else -> false
        }
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
        swapCard(game, cardName, null)
    }

    fun swapCard(game: Game, cardName: String, cardType: String?) {
        var cardToReplaceIndex = 0
        var cardToReplace: Card? = null
        val cards = game.kingdomCards
        for (i in cards.indices) {
            if (cards[i].name == cardName) {
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
