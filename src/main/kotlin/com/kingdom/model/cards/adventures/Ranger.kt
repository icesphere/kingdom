package com.kingdom.model.cards.adventures

import com.kingdom.model.Game
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.players.Player

class Ranger : AdventuresCard(NAME, CardType.Action, 4), GameSetupModifier {

    init {
        addBuys = 1
        special = "Turn your Journey token over (it starts face up). Then if it’s face up, +5 Cards."
    }

    override fun modifyGameSetup(game: Game) {
        game.isShowJourneyToken = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.isJourneyTokenFaceUp = !player.isJourneyTokenFaceUp
        if (player.isJourneyTokenFaceUp) {
            player.addInfoLogWithUsername("'s Journey token was flipped to face up")
            player.drawCards(5)
        } else {
            player.addInfoLogWithUsername("'s Journey token was flipped to face down")
        }

        player.refreshPlayerHandArea()
    }

    companion object {
        const val NAME: String = "Ranger"
    }
}

