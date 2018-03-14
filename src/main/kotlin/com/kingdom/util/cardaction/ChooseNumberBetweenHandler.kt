package com.kingdom.util.cardaction

import com.kingdom.model.cards.Card
import com.kingdom.model.OldCardAction
import com.kingdom.model.OldGame
import com.kingdom.model.OldPlayer
import com.kingdom.util.KingdomUtil

object ChooseNumberBetweenHandler {
    fun handleCardAction(game: OldGame, player: OldPlayer, oldCardAction: OldCardAction, numberChosen: Int) {
        when (oldCardAction.cardName) {
            "Counting House" -> if (numberChosen > 0) {
                game.addHistory(KingdomUtil.getWordWithBackgroundColor("Counting House", Card.ACTION_COLOR), " added ", KingdomUtil.getPlural(numberChosen, "Copper"), " to ", player.username, "'s hand")
                for (i in 0 until numberChosen) {
                    player.discard.remove(game.copperCard)
                    player.addCardToHand(game.copperCard)
                }
            }
            "Use Fruit Tokens" -> if (numberChosen > 0 && player.fruitTokens >= numberChosen) {
                player.addFruitTokens(numberChosen * -1)
                player.addCoins(numberChosen)
                game.addFruitTokensPlayed(numberChosen)
                game.refreshAllPlayersCardsBought()
                game.addHistory(player.username, " used ", KingdomUtil.getPlural(numberChosen, "Fruit Token"))
            }
        }
    }
}
