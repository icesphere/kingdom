package com.kingdom.util

import com.kingdom.model.cards.Card
import com.kingdom.model.OldGame
import com.kingdom.model.OldPlayer

object DurationHandler {
    fun applyDurationCards(game: OldGame, player: OldPlayer) {
        var numTimesCardCopied = 0
        for (card in player.durationCards) {
            var action: DurationAction? = null

            when(card.name) {
                "Caravan" -> action = CaravanDurationAction(game, player)
                "Fishing Village" -> action = FishingVillageDurationAction(game, player)
                "Haven" -> action = HavenDurationAction(game, player)
                "Hedge Wizard" -> action = HedgeWizardDurationAction(game, player)
                "Lighthouse" -> action = LighthouseDurationAction(game, player)
                "Merchant Ship" -> action = MerchantShipDurationAction(game, player)
                "Quest" -> action = QuestDurationAction(game, player, card)
                "Tactician" -> if (player.hasTacticianBonus()) {
                    player.setTacticianBonus(false)
                    player.drawCards(5)
                    player.addBuys(1)
                    player.addActions(1)
                    game.addHistory(player.username, " gained +5 Cards, +1 Buy, +1 Action from ", KingdomUtil.getWordWithBackgroundColor("Tactician", Card.ACTION_DURATION_COLOR))
                }
                "Wharf" -> action = WharfDurationAction(game, player)
            }

            if (action != null) {
                action.apply(0)
                var numTimesCardApplied = 1
                while (numTimesCardCopied > 0) {
                    numTimesCardCopied--
                    action.apply(numTimesCardApplied)
                    numTimesCardApplied++
                }
            }

            numTimesCardCopied = when(card.name) {
                "Throne Room" -> 1
                "King's Court" -> 2
                else -> 0
            }
        }
    }

    internal interface DurationAction {
        fun apply(numTimesApplied: Int)
    }

    internal class CaravanDurationAction(private val game: OldGame, private val player: OldPlayer) : DurationAction {

        override fun apply(numTimesApplied: Int) {
            player.drawCards(1)
            game.addHistory(player.username, " gained +1 Card from ", KingdomUtil.getWordWithBackgroundColor("Caravan", Card.ACTION_DURATION_COLOR))
        }
    }

    internal class FishingVillageDurationAction(private val game: OldGame, private val player: OldPlayer) : DurationAction {

        override fun apply(numTimesApplied: Int) {
            player.addActions(1)
            player.addCoins(1)
            game.addHistory(player.username, " gained +1 Action, +1 Coin from ", KingdomUtil.getWordWithBackgroundColor("Fishing Village", Card.ACTION_DURATION_COLOR))
        }
    }

    internal class HavenDurationAction(private val game: OldGame, private val player: OldPlayer) : DurationAction {

        override fun apply(numTimesApplied: Int) {
            for (c in player.havenCards) {
                player.addCardToHand(c)
            }
            player.havenCards.clear()
            game.addHistory(player.username, " added ", KingdomUtil.getWordWithBackgroundColor("Haven", Card.ACTION_DURATION_COLOR), " cards to hand")
        }
    }

    internal class HedgeWizardDurationAction(private val game: OldGame, private val player: OldPlayer) : DurationAction {

        override fun apply(numTimesApplied: Int) {
            player.drawCards(1)
            game.addHistory(player.username, " gained +1 Card from ", KingdomUtil.getWordWithBackgroundColor("Hedge Wizard", Card.DURATION_AND_VICTORY_IMAGE))
        }
    }

    internal class LighthouseDurationAction(private val game: OldGame, private val player: OldPlayer) : DurationAction {

        override fun apply(numTimesApplied: Int) {
            player.addCoins(1)
            game.addHistory(player.username, " gained +1 Coin from ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR))
        }
    }

    internal class MerchantShipDurationAction(private val game: OldGame, private val player: OldPlayer) : DurationAction {

        override fun apply(numTimesApplied: Int) {
            player.addCoins(2)
            game.addHistory(player.username, " gained +2 Coins from ", KingdomUtil.getWordWithBackgroundColor("Merchant Ship", Card.ACTION_DURATION_COLOR))
        }
    }

    internal class QuestDurationAction(private val game: OldGame, private val player: OldPlayer, private val card: Card) : DurationAction {

        override fun apply(numTimesApplied: Int) {
            val questCard = card.associatedCards[numTimesApplied]
            if (numTimesApplied == 0) {
                game.addHistory(player.username, "'s hand contains ", KingdomUtil.groupCards(player.hand, true))
            }
            game.addHistory(player.username, " was questing for ", KingdomUtil.getCardWithBackgroundColor(questCard))
            if (player.hand.contains(questCard)) {
                player.drawCards(1)
                player.addActions(1)
                player.addCoins(1)
                game.addHistory(player.username, " gained +1 Card, +1 Action, +1 Coin for successfully completing the Quest")
            }
        }
    }

    internal class WharfDurationAction(private val game: OldGame, private val player: OldPlayer) : DurationAction {

        override fun apply(numTimesApplied: Int) {
            player.drawCards(2)
            player.addBuys(1)
            game.addHistory(player.username, " gained +2 Cards, +1 Buy from ", KingdomUtil.getWordWithBackgroundColor("Wharf", Card.ACTION_DURATION_COLOR))
        }
    }
}
