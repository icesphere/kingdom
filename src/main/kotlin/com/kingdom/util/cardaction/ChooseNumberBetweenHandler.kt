package com.kingdom.util.cardaction

import com.kingdom.model.cards.Card
import com.kingdom.model.CardAction
import com.kingdom.model.Game
import com.kingdom.model.Player
import com.kingdom.util.KingdomUtil

object ChooseNumberBetweenHandler {
    fun handleCardAction(game: Game, player: Player, cardAction: CardAction, numberChosen: Int) {
        when (cardAction.cardName) {
            "Counting House" -> if (numberChosen > 0) {
                game.addHistory(KingdomUtil.getWordWithBackgroundColor("Counting House", Card.ACTION_COLOR), " added ", KingdomUtil.getPlural(numberChosen, "Copper"), " to ", player.username, "'s hand")
                for (i in 0 until numberChosen) {
                    player.discard.remove(game.copperCard)
                    player.addCardToHand(game.copperCard)
                }
            }
            "Use Cattle Tokens" -> if (numberChosen > 0 && player.cattleTokens >= numberChosen) {
                player.addCattleTokens(numberChosen * -1)
                val timesToApplyBonus = numberChosen / 2
                player.drawCards(timesToApplyBonus)
                player.addActions(timesToApplyBonus)
                game.refreshHandArea(player)
                game.refreshAllPlayersCardsPlayed()
                game.addHistory(player.username, " used ", KingdomUtil.getPlural(numberChosen, "Cattle Token"))
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
