package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterOtherPlayerCardGainedListenerForCardsInHand
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class FoolsGold : HinterlandsCard(NAME, CardType.TreasureReaction, 2), AfterOtherPlayerCardGainedListenerForCardsInHand,
        ChoiceActionCard {

    init {
        special = "If this is the first time you played a Fool's Gold this turn, +\$4, otherwise +\$1. When another player gains a Province, you may trash this from your hand to gain a Gold onto your deck."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val coins = if (player.cardsPlayed.count { it.name == NAME } == 1) 4 else 1
        player.addCoins(coins)
    }

    override fun afterCardGainedByOtherPlayer(card: Card, player: Player, otherPlayer: Player) {
        if (card.isProvince && player.hand.contains(this)) {
            player.yesNoChoice(this, "Trash ${cardNameWithBackgroundColor} to gain a ${Gold().cardNameWithBackgroundColor} onto your deck?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1 && player.hand.contains(this)) {
            player.trashCardFromHand(this)
            player.gainSupplyCard(Gold(), true, CardLocation.Deck)
        }
    }

    companion object {
        const val NAME: String = "Fool's Gold"
    }
}
