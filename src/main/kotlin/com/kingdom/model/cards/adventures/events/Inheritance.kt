package com.kingdom.model.cards.adventures.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Inheritance : AdventuresEvent(NAME, 7), ChooseCardActionCard {

    init {
        special = "Once per game: Set aside a non-Victory Action card from the Supply costing up to \$4. Move your Estate token to it. (Your Estates gain the abilities and types of that card.)"
        textSize = 133
        isPlayTreasureCardsRequired = true
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.inheritanceActionCard == null
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromSupply("Choose which non-Victory Action supply pile card to set aside with your Estate token", this, { c -> c.isAction && !c.isVictory && player.getCardCostWithModifiers(c) <= 4 })
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.game.removeCardFromSupply(card)
        player.inheritanceActionCard = card
        player.replaceAllEstatesWithInheritanceEstates()
    }

    companion object {
        const val NAME: String = "Inheritance"
    }
}