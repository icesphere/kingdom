package com.kingdom.model.cards.adventures.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChooseCardsActionCard
import com.kingdom.model.players.Player

class Bonfire : AdventuresEvent(NAME, 3), ChooseCardsActionCard {

    init {
        special = "Trash up to 2 cards you have in play."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.inPlay.isNotEmpty()
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardsAction(2, special, this, player.inPlayCopy, true)
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        cards.forEach { card -> player.trashCardInPlay(card) }
    }

    companion object {
        const val NAME: String = "Bonfire"
    }
}