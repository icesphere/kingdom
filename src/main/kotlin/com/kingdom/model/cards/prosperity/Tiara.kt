package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCardOptional
import com.kingdom.model.players.Player

class Tiara : ProsperityCard(NAME, CardType.Treasure, 4), ChooseCardActionCardOptional {

    private var topDeckEffects = 0

    init {
        addCoins = 2
        special = "This turn, when you gain a card, you may put it onto your deck. You may play a Treasure from your hand twice."
        isTreasureExcludedFromAutoPlay = true
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.numCardGainedMayPutOnTopOfDeck++
        topDeckEffects++

        if (player.hand.any { it.isTreasure }) {
            player.chooseCardFromHandOptional("Choose a Treasure card from your hand to play twice", this) { it.isTreasure }
        }
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        if (card != null) {
            player.playCard(card)
            player.playCard(card, repeatedAction = true)
        }
    }

    override fun beforeCardRepeated(player: Player) {
    }

    override fun removedFromPlay(player: Player) {
        repeat(topDeckEffects) {
            player.numCardGainedMayPutOnTopOfDeck--
        }
        topDeckEffects = 0
        super.removedFromPlay(player)
    }

    companion object {
        const val NAME: String = "Tiara"
    }
}
