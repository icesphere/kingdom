package com.kingdom.model.cards.empires

import com.kingdom.model.Game
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.players.Player

class ChariotRace : EmpiresCard(NAME, CardType.Action, 3), GameSetupModifier {

    init {
        addActions = 1
        special = "Reveal the top card of your deck and put it into your hand. The player to your left reveals the top card of their deck. If your card costs more, +\$1 and +1 VP."
        fontSize = 10
    }

    override fun modifyGameSetup(game: Game) {
        game.isShowVictoryCoins = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val topCard = player.removeTopCardOfDeck()

        if (topCard != null) {
            player.addCardToHand(topCard)
            player.addEventLogWithUsername("revealed ${topCard.cardNameWithBackgroundColor} from the top of their deck and put it into their hand")

            val playerToLeft = player.playerToLeft

            val playerToLeftTopCard = playerToLeft.revealTopCardOfDeck()
            if (playerToLeftTopCard != null) {
                if (player.getCardCostWithModifiers(topCard) > player.getCardCostWithModifiers(playerToLeftTopCard)) {
                    player.addCoins(1)
                    player.addVictoryCoins(1)
                    player.showInfoMessage("${topCard.cardNameWithBackgroundColor} costs more than ${playerToLeftTopCard.cardNameWithBackgroundColor} so you received +\$1 and +1 VP")
                } else {
                    player.showInfoMessage("${topCard.cardNameWithBackgroundColor} did not cost more than ${playerToLeftTopCard.cardNameWithBackgroundColor}")
                }
            } else {
                player.showInfoMessage("${playerToLeft.username} did not have a top card to reveal")
            }
        }
    }

    companion object {
        const val NAME: String = "Chariot Race"
    }
}

