package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.ConditionalDuration
import com.kingdom.model.cards.MultipleTurnDuration
import com.kingdom.model.cards.NextTurnRepeater
import com.kingdom.model.players.Player

interface CardRepeater {

    var cardBeingRepeated: Card?

    val cardsBeingRepeated: MutableList<Card>

    val timesRepeated: Int

}

fun CardRepeater.handleCardToRepeatChosen(card: Card?, player: Player) {
    cardBeingRepeated = card

    if (card != null) {
        cardsBeingRepeated.add(card)

        player.addActions(1)

        player.playCard(card)

        repeat(timesRepeated) {
            player.addRepeatCardAction(card)
        }
    }
}

fun CardRepeater.clearRepeatedCards() {
    cardBeingRepeated = null
    cardsBeingRepeated.clear()
}

fun CardRepeater.beforeCardRepeaterRepeated() {
    val card = this as Card
    card.playersExcludedFromCardEffects.clear()
    card.isSelected = false
    card.isHighlighted = false
}

fun CardRepeater.hasRepeatedDurationForCleanup(): Boolean {
    return cardsBeingRepeated.any { it.isKeptDurationForCleanup() }
}

fun CardRepeater.hasRepeatedMultipleTurnDurationToKeep(player: Player): Boolean {
    return cardsBeingRepeated.any { it.keepsDurationAtEndOfTurn(player) }
}

fun Card.isKeptDurationForCleanup(): Boolean {
    return (isDuration && (this !is ConditionalDuration || isKeepAtEndOfTurn))
            || (this is CardRepeater && hasRepeatedDurationForCleanup())
}

fun Card.keepsDurationAtEndOfTurn(player: Player): Boolean {
    return (this is MultipleTurnDuration && keepAtEndOfTurn(player))
            || (this is NextTurnRepeater && keepAtEndOfTurn(player))
            || (this is CardRepeater && hasRepeatedMultipleTurnDurationToKeep(player))
}
