package com.kingdom.model.cards.adventures

import com.kingdom.model.Game
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.CardDiscardedFromPlayListener
import com.kingdom.model.players.Player

class Page : AdventuresCard(NAME, CardType.ActionTraveller, 2), GameSetupModifier, CardDiscardedFromPlayListener, ChoiceActionCard {

    init {
        addCards = 1
        addActions = 1
        isPlayTreasureCardsRequired = true
        special = "When you discard this from play, you may exchange it for a Treasure Hunter."
    }

    override fun modifyGameSetup(game: Game) {
        game.cardsNotInSupply.add(TreasureHunter())
        game.setupAmountForPile(TreasureHunter.NAME, 5)

        game.cardsNotInSupply.add(Warrior())
        game.setupAmountForPile(Warrior.NAME, 5)

        game.cardsNotInSupply.add(Hero())
        game.setupAmountForPile(Hero.NAME, 5)

        game.cardsNotInSupply.add(Champion())
        game.setupAmountForPile(Champion.NAME, 5)

        game.isShowDuration = true
    }

    override fun onCardDiscarded(player: Player) {
        val treasureHunter = TreasureHunter()

        if (player.game.isCardAvailableInSupply(treasureHunter)) {
            player.yesNoChoice(this, "Exchange ${this.cardNameWithBackgroundColor} for a ${treasureHunter.cardNameWithBackgroundColor}?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.exchangeDiscardedCard(this, TreasureHunter())
        }
    }

    companion object {
        const val NAME: String = "Page"
    }
}

