package com.kingdom.model.cards.empires

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.cards.*
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class Encampment : EmpiresCard(NAME, CardType.Action, 2), MultiTypePile, ChoiceActionCard {

    init {
        addCards = 2
        addActions = 2
        special = "You may reveal a Gold or Plunder from your hand. If you don’t, set this aside, and return it to the Supply at the start of Clean-up. (Encampment is the top half of the Plunder pile.)"
        fontSize = 10
    }

    override val otherCardsInPile: List<Card>
        get() = listOf(Plunder())

    override fun createMultiTypePile(game: Game): List<Card> {
        return listOf(
                Encampment(),
                Encampment(),
                Encampment(),
                Encampment(),
                Encampment(),
                Plunder(),
                Plunder(),
                Plunder(),
                Plunder(),
                Plunder()
        )
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it.isGold || it.name == Plunder.NAME }) {
            val choices = mutableListOf<Choice>()

            if (player.hand.any { it.isGold }) {
                choices.add(Choice(1, "Reveal Gold"))
            }
            if (player.hand.any { it.name == Plunder.NAME }) {
                choices.add(Choice(2, "Reveal Plunder"))
            }

            choices.add(Choice(3, "Do not reveal"))

            val text = "If you don’t reveal, set $cardNameWithBackgroundColor aside, and return it to the Supply at the start of Clean-up"

            player.makeChoiceFromList(this, text, choices)
        } else {
            setAsideEncampment(player)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        when(choice) {
            1 -> player.addEventLogWithUsername("revealed ${Gold().cardNameWithBackgroundColor} from hand")
            2 -> player.addEventLogWithUsername("revealed ${Plunder().cardNameWithBackgroundColor} from hand")
            3 -> setAsideEncampment(player)
        }
    }

    private fun setAsideEncampment(player: Player) {
        player.removeCardInPlay(this, CardLocation.Supply)
        player.cardsSetAsideToReturnToSupplyAtStartOfCleanup.add(this)
        player.addEventLogWithUsername("set aside $cardNameWithBackgroundColor")
        player.showInfoMessage("$cardNameWithBackgroundColor was set aside and will be returned to the supply at the start of Clean-up")
    }

    companion object {
        const val NAME: String = "Encampment"
    }
}

