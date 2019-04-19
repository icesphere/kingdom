package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class Ironmonger : DarkAgesCard(NAME, CardType.Action, 4), ChoiceActionCard {

    init {
        addCards = 1
        addActions = 1
        isAddCoinsCard = true
        special = "Reveal the top card of your deck; you may discard it. Either way, if it’s an: Action card, +1 Action; Treasure card, +\$1; Victory card, +1 Card"
        fontSize = 11
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val card = player.revealTopCardOfDeck()
        if (card != null) {
            player.yesNoChoice(this, "Discard ${card.cardNameWithBackgroundColor}?", card)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.discardTopCardOfDeck()
        }

        val card = info as Card

        if (card.isAction) {
            player.addEventLogWithUsername("gained +1 Action from ${this.cardNameWithBackgroundColor}")
            player.addActions(1)
        }
        if (card.isTreasure) {
            player.addEventLogWithUsername("gained +\$1 from ${this.cardNameWithBackgroundColor}")
            player.addCoins(1)
        }
        if (card.isVictory) {
            player.addEventLogWithUsername("gained +1 Card from ${this.cardNameWithBackgroundColor}")
            player.drawCard()
        }
    }

    companion object {
        const val NAME: String = "Ironmonger"
    }
}

