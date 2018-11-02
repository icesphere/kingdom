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

class PirateShip : SeasideCard(NAME, CardType.ActionAttack, 4), GameSetupModifier, AttackCard, ChoiceActionCard, ChooseCardActionCard {

    init {
        special = "Choose one: +\$1 per Coin token on your Pirate Ship mat; or each other player reveals the top 2 cards of their deck, trashes one of those Treasures that you choose, and discards the rest, and then if anyone trashed a Treasure you add a Coin token to your Pirate Ship mat."
        fontSize = 11
        textSize = 100
    }

    private var gainedPirateShipCoin = false

    override fun modifyGameSetup(game: Game) {
        game.isShowPirateShipCoins = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this, Choice(1, "+\$${player.pirateShipCoins}"), Choice(2, "Attack"))
    }

    override fun actionChoiceMade(player: Player, choice: Int) {
        if (choice == 1) {
            player.addUsernameGameLog("received +\$${player.pirateShipCoins} from pirate coin tokens")
            player.addCoins(player.pirateShipCoins)
        } else {
            player.addUsernameGameLog("chose to attack with ${this.cardNameWithBackgroundColor}")
            player.triggerAttack(this)
        }
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        affectedOpponents.forEach { opponent ->
            val topCardsOfDeck = opponent.removeTopCardsOfDeck(2, true)

            val treasureCards = topCardsOfDeck.filter { it.isTreasure }

            val nonTreasureCards = topCardsOfDeck.filterNot { it.isTreasure }

            nonTreasureCards.forEach {
                opponent.addCardToDiscard(it, showLog = true)
            }

            if (treasureCards.isNotEmpty()) {
                if (treasureCards.size == 1) {
                    opponent.cardTrashed(treasureCards.first(), true)
                    gainPirateShipCoin(player)
                } else {
                    if (treasureCards[0].name == treasureCards[1].name) {
                        opponent.addCardToDiscard(treasureCards[0], showLog = true)
                        opponent.cardTrashed(treasureCards[1], true)
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
            player.addUsernameGameLog("gained a pirate ship coin")
            player.pirateShipCoins++
        }
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        gainedPirateShipCoin = false
    }

    override fun beforeCardRepeated() {
        super.beforeCardRepeated()
        gainedPirateShipCoin = false
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        val pirateAttackInfo = info as PirateAttackInfo

        pirateAttackInfo.opponent.cardTrashed(card, showLog = true)

        pirateAttackInfo.opponent.addCardToDiscard(pirateAttackInfo.treasureCards.first { it.name != card.name })

        gainPirateShipCoin(player)
    }

    private class PirateAttackInfo(val opponent: Player, val treasureCards: List<Card>)

    companion object {
        const val NAME: String = "Pirate Ship"
    }
}

