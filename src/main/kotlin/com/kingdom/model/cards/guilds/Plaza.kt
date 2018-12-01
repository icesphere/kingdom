package com.kingdom.model.cards.guilds

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Plaza : GuildsCard(NAME, CardType.Action, 4), GameSetupModifier, ChoiceActionCard, DiscardCardsForBenefitActionCard {

    init {
        addCards = 1
        addActions = 2
        special = "You may discard a Treasure for +1 Coffers."
    }

    override fun modifyGameSetup(game: Game) {
        game.isShowCoffers = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it.isTreasure }) {
            player.yesNoChoice(this, "Discard a Treasure for +1 Coffers?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.discardCardsForBenefit(this, 1, "Discard a Treasure for +1 Coffers", { c -> c.isTreasure })
        }
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        player.addCoffers(1)
    }

    override fun onChoseDoNotUse(player: Player, info: Any?) {
        //do nothing
    }

    companion object {
        const val NAME: String = "Plaza"
    }
}

