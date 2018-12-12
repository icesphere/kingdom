package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCardOptional
import com.kingdom.model.players.Player

class KingsCourt : ProsperityCard(NAME, CardType.Action, 7), ChooseCardActionCardOptional {

    var copiedCard: Card? = null

    init {
        special = "You may play an Action card from your hand three times."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromHandOptional("Choose an Action card from your hand to play three times", this, { c -> c.isAction })
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        copiedCard = card

        if (card != null) {
            player.addActions(1)
            player.playCard(card)
            player.addRepeatCardAction(card)
            player.addRepeatCardAction(card)
        }
    }

    override fun removedFromPlay(player: Player) {
        copiedCard = null
    }

    companion object {
        const val NAME: String = "Kings Court"
    }
}

