package com.kingdom.model.cards.renaissance

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Scepter : RenaissanceCard(NAME, CardType.Treasure, 5), ChoiceActionCard, ChooseCardActionCard {

    init {
        special = "When you play this, chose one: +\$2; or replay an Action card you played this turn that’s still in play."
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val choices = mutableListOf<Choice>()

        choices.add(Choice(1, "+\$2"))

        val actionsPlayedThatAreStillInPlay = player.inPlay.filter { it.isAction }.intersect(player.cardsPlayed).toList()

        if (actionsPlayedThatAreStillInPlay.isNotEmpty()) {
            choices.add(Choice(2, "Replay Action"))

            player.makeChoiceFromList(this, "Action cards played that are still in play: ${actionsPlayedThatAreStillInPlay.groupedString}", choices)
        } else {
            player.addCoins(2)
            player.showInfoMessage("Gained +\$2. You had no Action cards still in play.")
        }

    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addCoins(2)
        } else {
            val actionsPlayedThatAreStillInPlay = player.inPlay.filter { it.isAction }.intersect(player.cardsPlayed).map { it.copy(false) }
            player.chooseCardAction("Chose an Action card to replay", this, actionsPlayedThatAreStillInPlay, false)
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.addActions(1)
        player.playCard(card, repeatedAction = true)
    }

    companion object {
        const val NAME: String = "Scepter"
    }
}