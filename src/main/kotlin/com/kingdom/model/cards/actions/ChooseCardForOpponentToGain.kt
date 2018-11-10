package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

class ChooseCardForOpponentToGain(private val cost: Int?, text: String, private val destination: CardLocation = CardLocation.Discard, private val opponent: Player) : Action(text) {

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return ((cardLocation == CardLocation.Supply)
                && (cost == null || player.getCardCostWithModifiers(card) == cost))
    }

    override fun processAction(player: Player): Boolean {
        return player.game.availableCards.any { cost == null || player.getCardCostWithModifiers(it) == cost }
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val card = result.selectedCard!!

        player.game.removeCardFromSupply(card)

        when (destination) {
            CardLocation.Hand -> {
                player.addGameLog("${player.username} put ${card.cardNameWithBackgroundColor} into ${opponent.username}'s hand")
                opponent.gainCardToHand(card)
            }
            CardLocation.Deck -> {
                player.addGameLog("${player.username} put ${card.cardNameWithBackgroundColor} on top of ${opponent.username}'s deck")
                opponent.gainCardToTopOfDeck(card)
            }
            else -> {
                player.addGameLog("${player.username} put ${card.cardNameWithBackgroundColor} into ${opponent.username}'s discard")
                opponent.cardGained(card)
            }
        }

        return true
    }
}