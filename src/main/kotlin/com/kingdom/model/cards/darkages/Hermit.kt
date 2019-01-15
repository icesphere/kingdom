package com.kingdom.model.cards.darkages

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.cards.listeners.CardDiscardedFromPlayListener
import com.kingdom.model.players.Player

class Hermit : DarkAgesCard(NAME, CardType.Action, 3), GameSetupModifier, ChooseCardActionCard, CardDiscardedFromPlayListener {

    init {
        special = "Look through your discard pile. You may trash a non-Treasure card from your discard pile or hand. Gain a card costing up to \$3. When you discard this from play, if you didn’t buy any cards this turn, trash this and gain a Madman from the Madman pile."
        textSize = 117
    }

    override fun modifyGameSetup(game: Game) {
        game.cardsNotInSupply.add(Madman())
        game.setupAmountForPile(Madman.NAME, 10)
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cardsToSelectFrom = player.cardsInDiscardCopy + player.handCopy
        player.chooseCardAction("You may trash a non-Treasure card from your discard pile or hand (cards from discard are shown first)", this, cardsToSelectFrom, true)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        if (player.cardsInDiscard.any { it.name == card.name }) {
            player.removeCardFromDiscard(player.cardsInDiscard.first { it.name == card.name })
            player.cardTrashed(card)
            player.addEventLogWithUsername("trashed ${card.cardNameWithBackgroundColor} from their discard")
        } else {
            player.removeCardFromHand(player.hand.first { it.name == card.name })
            player.cardTrashed(card)
            player.addEventLogWithUsername("trashed ${card.cardNameWithBackgroundColor} from their hand")
        }

        player.chooseSupplyCardToGain(3)
    }

    override fun onCardDiscarded(player: Player) {
        if (player.lastTurnSummary!!.cardsBought.isEmpty()) {
            player.trashCardFromDiscard(this)
            val madman = Madman()
            player.showInfoMessage("${this.cardNameWithBackgroundColor} was trashed and you gained a ${madman.cardNameWithBackgroundColor}")
            player.gainCardNotInSupply(madman)
        }
    }

    companion object {
        const val NAME: String = "Hermit"
    }
}

