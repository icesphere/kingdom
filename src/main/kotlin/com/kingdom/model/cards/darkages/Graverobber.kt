package com.kingdom.model.cards.darkages

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Graverobber : DarkAgesCard(NAME, CardType.Action, 5), ChoiceActionCard, TrashCardsForBenefitActionCard {

    init {
        testing = true
        special = "Choose one: Gain a card from the trash costing from \$3 to \$6, onto your deck; or trash an Action card from your hand and gain a card costing up to \$3 more than it."
        fontSize = 11
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this, Choice(1, "Gain a card from the trash costing from \$3 to \$6, onto your deck"), Choice(2, "Trash an Action card from your hand and gain a card costing up to \$3 more than it."))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            if (player.game.trashedCards.any { player.getCardCostWithModifiers(it) in 3..6 }) {
                player.isNextCardToTopOfDeck = true
                player.gainCardFromTrash(false, { c -> player.getCardCostWithModifiers(c) in 3..6 })
            } else {
                val message = "There were no cards in the trash costing from \$3 to \$6"
                player.addGameLog(message)
                player.showInfoMessage(message)
            }
        } else {
            if (player.hand.any { it.isAction }) {
                player.trashCardsFromHandForBenefit(this, 1, "Trash an Action card from your hand and gain a card costing up to \$3 more than it.", { c -> c.isAction })
            } else {
                player.addUsernameGameLog("had no actions cards in their hand to trash")
                player.showInfoMessage("There are no actions cards in your hand to trash")
            }
        }
    }

    override fun cardsScrapped(player: Player, scrappedCards: List<Card>) {
        val card = scrappedCards.first()

        player.chooseSupplyCardToGain(player.getCardCostWithModifiers(card) + 3, { c -> c.isAction }, "Choose an action card from the supply costing up to ${player.getCardCostWithModifiers(card) + 3}")
    }

    companion object {
        const val NAME: String = "Graverobber"
    }
}

