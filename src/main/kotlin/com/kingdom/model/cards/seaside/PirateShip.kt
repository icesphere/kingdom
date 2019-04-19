package com.kingdom.model.cards.seaside

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class PirateShip : SeasideCard(NAME, CardType.ActionAttack, 4), GameSetupModifier, AttackCard, ChoiceActionCard, ChooseCardActionCard {

    init {
        special = "Choose one: +\$1 per Coin token on your Pirate Ship mat; or each other player reveals the top 2 cards of their deck, trashes one of those Treasures that you choose, and discards the rest, and then if anyone trashed a Treasure you add a Coin token to your Pirate Ship mat."
        fontSize = 11
        isAddCoinsCard = true
    }

    private var gainedPirateShipCoin = false

    override fun modifyGameSetup(game: Game) {
        game.isShowPirateShipCoins = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        player.makeChoiceWithInfo(this, "", affectedOpponents, Choice(1, "+\$${player.pirateShipCoins}"), Choice(2, "Attack"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addEventLogWithUsername("received +\$${player.pirateShipCoins} from pirate coin tokens")
            player.addCoins(player.pirateShipCoins)
        } else {
            player.addEventLogWithUsername("chose to attack with ${this.cardNameWithBackgroundColor}")

            @Suppress("UNCHECKED_CAST")
            val affectedOpponents = info as List<Player>

            attack(player, affectedOpponents)
        }
    }

    private fun attack(player: Player, affectedOpponents: List<Player>) {

        affectedOpponents.forEach { opponent ->
            val topCardsOfDeck = opponent.removeTopCardsOfDeck(2, true)

            val treasureCards = topCardsOfDeck.filter { it.isTreasure }

            val nonTreasureCards = topCardsOfDeck.filterNot { it.isTreasure }

            nonTreasureCards.forEach {
                opponent.addCardToDiscard(it, showLog = true)
            }

            if (nonTreasureCards.isNotEmpty()) {
                opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor discarded ${nonTreasureCards.groupedString} from your deck")
                player.showInfoMessage("discarded ${opponent.username}'s ${nonTreasureCards.groupedString}")
            }

            if (treasureCards.isNotEmpty()) {
                if (treasureCards.size == 1) {
                    opponent.cardTrashed(treasureCards.first(), true)
                    opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor trashed ${treasureCards.first().cardNameWithBackgroundColor} from your deck")
                    player.showInfoMessage("trashed ${opponent.username}'s ${treasureCards.first().cardNameWithBackgroundColor}")
                    gainPirateShipCoin(player)
                } else {
                    if (treasureCards[0].name == treasureCards[1].name) {
                        opponent.addCardToDiscard(treasureCards[0], showLog = true)
                        opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor discarded ${treasureCards.first().cardNameWithBackgroundColor} from your deck")
                        player.showInfoMessage("discarded ${opponent.username}'s ${treasureCards.first().cardNameWithBackgroundColor}")
                        opponent.cardTrashed(treasureCards[1], true)
                        opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor trashed ${treasureCards[1].cardNameWithBackgroundColor} from your deck")
                        player.showInfoMessage("trashed ${opponent.username}'s ${treasureCards[1].cardNameWithBackgroundColor}")
                        gainPirateShipCoin(player)
                    } else {
                        val pirateAttackInfo = PirateAttackInfo(opponent, treasureCards)
                        player.chooseCardAction("Attacking ${opponent.username}. Choose which treasure to trash (the other will be discarded)", this, treasureCards, false, pirateAttackInfo)
                    }
                }
            }
        }
    }

    @Synchronized
    private fun gainPirateShipCoin(player: Player) {
        if (!gainedPirateShipCoin) {
            gainedPirateShipCoin = true
            player.addEventLogWithUsername("gained a pirate ship coin")
            player.pirateShipCoins++
        }
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        gainedPirateShipCoin = false
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        val pirateAttackInfo = info as PirateAttackInfo

        pirateAttackInfo.opponent.cardTrashed(card, showLog = true)

        pirateAttackInfo.opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor trashed ${card.cardNameWithBackgroundColor} from your deck")

        val cardToDiscard = pirateAttackInfo.treasureCards.first { it.name != card.name }

        pirateAttackInfo.opponent.addCardToDiscard(cardToDiscard)

        pirateAttackInfo.opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor discarded ${cardToDiscard.cardNameWithBackgroundColor} from your deck")

        gainPirateShipCoin(player)
    }

    private class PirateAttackInfo(val opponent: Player, val treasureCards: List<Card>)

    companion object {
        const val NAME: String = "Pirate Ship"
    }
}

