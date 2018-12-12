package com.kingdom.model.cards.adventures

import com.kingdom.model.Game
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.CardDiscardedFromPlayListener
import com.kingdom.model.players.Player

class Peasant : AdventuresCard(NAME, CardType.ActionTraveller, 2), GameSetupModifier, CardDiscardedFromPlayListener, ChoiceActionCard {

    init {
        addBuys = 1
        addCoins = 1
        isPlayTreasureCardsRequired = true
        special = "When you discard this from play, you may exchange it for a Soldier."
    }

    override fun modifyGameSetup(game: Game) {
        game.cardsNotInSupply.add(Soldier())
        game.setupAmountForPile(Soldier.NAME, 5)

        game.cardsNotInSupply.add(Fugitive())
        game.setupAmountForPile(Fugitive.NAME, 5)

        game.cardsNotInSupply.add(Disciple())
        game.setupAmountForPile(Disciple.NAME, 5)

        game.cardsNotInSupply.add(Teacher())
        game.setupAmountForPile(Teacher.NAME, 5)

        game.isShowTavern = true
    }

    override fun onCardDiscarded(player: Player) {
        val soldier = Soldier()

        if (player.game.isCardAvailableInSupply(soldier)) {
            player.yesNoChoice(this, "Exchange ${this.cardNameWithBackgroundColor} for a ${soldier.cardNameWithBackgroundColor}?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.exchangeDiscardedCard(this, Soldier())
        }
    }

    companion object {
        const val NAME: String = "Peasant"
    }
}

