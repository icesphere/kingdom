package com.kingdom.model.cards.adventures.events

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.ChooseCardsActionCard
import com.kingdom.model.players.Player

class Pilgrimage : AdventuresEvent(NAME, 4, true), GameSetupModifier, ChooseCardsActionCard {

    init {
        isPlayTreasureCardsRequired = true
        special = "Once per turn: Turn your Journey token over (it starts face up); then if it’s face up, choose up to 3 differently named cards you have in play and gain a copy of each."
        fontSize = 11
        textSize = 119
    }

    override fun modifyGameSetup(game: Game) {
        game.isShowJourneyToken = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.isJourneyTokenFaceUp = !player.isJourneyTokenFaceUp
        player.refreshPlayerHandArea()

        if (player.isJourneyTokenFaceUp) {
            player.addInfoLogWithUsername("'s Journey token was flipped to face up")
            val distinctCards = player.inPlayCopy.distinctBy { it.name }.filter { player.game.isCardAvailableInSupply(it) }
            if (distinctCards.isNotEmpty()) {
                val numToSelect = if (distinctCards.size < 3) distinctCards.size else 3
                player.chooseCardsAction(numToSelect, "Choose up to 3 differently named cards you have in play and gain a copy of each", this, distinctCards, true)
            } else {
                player.showInfoMessage("There are no cards in play that can be copied")
            }
        } else {
            player.addInfoLogWithUsername("'s Journey token was flipped to face down")
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        cards.forEach {
            player.gainSupplyCard(it, true)
        }
    }

    companion object {
        const val NAME: String = "Pilgrimage"
    }
}