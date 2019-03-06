package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

interface CardRepeater {

    var cardBeingRepeated: Card?

    val timesRepeated: Int

}

fun CardRepeater.handleCardToRepeatChosen(card: Card?, player: Player) {
    cardBeingRepeated = card

    if (card != null) {
        player.addActions(1)

        player.playCard(card)

        repeat(timesRepeated) {
            player.addRepeatCardAction(card)
        }
    }
}