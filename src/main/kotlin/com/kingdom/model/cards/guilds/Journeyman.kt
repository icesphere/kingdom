package com.kingdom.model.cards.guilds

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Journeyman : GuildsCard(NAME, CardType.Action, 5), ChooseCardActionCard {

    init {
        special = "Name a card. Reveal cards from your deck until you reveal 3 cards without that name. Put those cards into your hand and discard the rest."
        fontSize = 11
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cardsToSelectFrom = player.game.allCardsCopy
        player.chooseCardAction(special, this, cardsToSelectFrom, false)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.addEventLogWithUsername("named ${card.cardNameWithBackgroundColor}")

        val cards = player.revealFromDeckUntilCardsFoundAndDiscardOthers({ c -> c.name != card.name }, 3)

        if (cards.isNotEmpty()) {
            player.addCardsToHand(cards, true)
        } else {
            val message = "No cards found without that name"
            player.addEventLogWithUsername(message)
            player.showInfoMessage(message)
        }
    }

    companion object {
        const val NAME: String = "Journeyman"
    }
}

