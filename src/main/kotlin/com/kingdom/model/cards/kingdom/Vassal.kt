package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class Vassal : KingdomCard(NAME, CardType.Action, 3), ChoiceActionCard {

    lateinit var cardDiscarded: Card

    init {
        addCoins = 2
        special = "Discard the top card of your deck. If it’s an Action card, you may play it."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val card = player.discardTopCardOfDeck()
        card?.let {
            if (card.isAction) {
                cardDiscarded = card
                player.yesNoChoice(this, "Play discarded ${card.name}?")
            }
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int) {
        if (choice == 1) {
            player.removeCardFromDiscard(cardDiscarded)
            player.addActions(1)
            player.playCard(cardDiscarded)
        }
    }

    companion object {
        const val NAME: String = "Vassal"
    }
}

