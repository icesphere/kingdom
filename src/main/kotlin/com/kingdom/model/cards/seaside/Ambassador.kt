package com.kingdom.model.cards.seaside

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Ambassador : SeasideCard(NAME, CardType.ActionAttack, 3), AttackCard, ChooseCardActionCard, ChoiceActionCard {

    var revealedCard: Card? = null

    init {
        special = "Reveal a card from your hand. Return up to 2 copies of it from your hand to the Supply. Then each other player gains a copy of it."
        fontSize = 11
        isTrashingCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromHand(special, this)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.revealCardFromHand(card)

        if (card.isShelter || player.game.cardsNotInSupply.any { it.name == card.name }) {
            player.showInfoMessage("Revealed card is not in the supply")
            player.addEventLog("Revealed card is not in the supply")
            return
        }

        val choices = mutableListOf(
                Choice(0, "0"),
                Choice(1, "1")
        )

        if (player.cardCountByName(card.name) > 1) {
            choices.add(Choice(2, "2"))
        }

        player.makeChoiceFromListWithInfo(this, "How many copies of ${card.cardNameWithBackgroundColor} do you want to return from your hand to the supply?", card, choices)
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val card = info as Card

        if (choice > 0) {
            revealedCard = card

            repeat(choice) {
                val cardByName = player.hand.first { it.name == card.name }
                player.removeCardFromHand(cardByName)
                player.game.returnCardToSupply(cardByName)
            }

            player.triggerAttack(this)
        }
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        val card = revealedCard ?: return

        affectedOpponents.forEach { opponent ->
            opponent.gainSupplyCard(card, true)
            opponent.showInfoMessage("You gained ${card.cardNameWithBackgroundColor} from ${player.username}'s $cardNameWithBackgroundColor")
        }
    }

    companion object {
        const val NAME: String = "Ambassador"
    }
}

