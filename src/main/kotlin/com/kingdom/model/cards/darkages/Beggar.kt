package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.HandBeforeAttackListener
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Beggar : DarkAgesCard(NAME, CardType.ActionReaction, 2), HandBeforeAttackListener, ChoiceActionCard {

    init {
        special = "Gain 3 Coppers to your hand. When another player plays an Attack card, you may first discard this to gain 2 Silvers, putting one onto your deck."
        isDefense = true
        textSize = 115
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val coppers = listOf(Copper(), Copper(), Copper())
        player.addEventLogWithUsername("gained ${coppers.groupedString} to their hand")
        for (copper in coppers) {
            player.gainSupplyCardToHand(copper, false)
        }
    }

    override fun onBeforeAttack(attackCard: Card, player: Player, attacker: Player) {
        player.yesNoChoice(this, "Reveal $cardNameWithBackgroundColor and discard $cardNameWithBackgroundColor to gain 2 Silvers, putting one onto your deck?")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addEventLogWithUsername("revealed $cardNameWithBackgroundColor to discard $cardNameWithBackgroundColor and gained 2 Silvers, putting one onto their deck")
            player.discardCardFromHand(this, true)
            player.gainSupplyCard(Silver())
            player.gainSupplyCardToTopOfDeck(Silver(), false)
        }
    }

    companion object {
        const val NAME: String = "Beggar"
    }
}

