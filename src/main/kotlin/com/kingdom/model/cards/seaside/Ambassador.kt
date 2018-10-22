package com.kingdom.model.cards.seaside

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Ambassador : SeasideCard(NAME, CardType.Action, 3), AttackCard, ChooseCardActionCard, ChoiceActionCard {

    var revealedCard: Card? = null

    init {
        special = "Reveal a card from your hand. Return up to 2 copies of it from your hand to the Supply. Then each other player gains a copy of it."
        fontSize = 11
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromHand(special, this)
    }

    override fun onCardChosen(player: Player, card: Card) {
        player.revealCardFromHand(card)

        revealedCard = card

        val choices = mutableListOf(
                Choice(0, "0"),
                Choice(1, "1")
        )

        if (player.cardCountByName(card.name) > 1) {
            choices.add(Choice(2, "2"))
        }

        player.makeChoiceFromList(this, "How many copies of ${card.cardNameWithBackgroundColor} do you want to return from your hand to the supply?", choices)
    }

    override fun actionChoiceMade(player: Player, choice: Int) {
        val card = revealedCard ?: return

        if (choice > 0) {
            repeat(choice) {
                val cardByName = player.hand.first { it.name == card.name }
                player.removeCardFromHand(cardByName)
                player.game.returnCardToSupply(cardByName)
            }
        }

        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        val card = revealedCard ?: return

        affectedOpponents.forEach { opponent ->
            opponent.acquireFreeCardFromSupply(card, true)
        }
    }

    companion object {
        const val NAME: String = "Ambassador"
    }
}

