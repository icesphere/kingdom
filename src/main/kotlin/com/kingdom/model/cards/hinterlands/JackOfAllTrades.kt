package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class JackOfAllTrades : HinterlandsCard(NAME, CardType.Action, 4), ChoiceActionCard {

    init {
        special = "Gain a Silver. Look at the top card of your deck; you may discard it. Draw until you have 5 cards in hand. You may trash a non-Treasure card from your hand."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainSupplyCard(Silver(), showLog = true)
        val cardOnTopOfDeck = player.cardOnTopOfDeck
        if (cardOnTopOfDeck != null) {
            player.yesNoChoice(this, "Discard ${cardOnTopOfDeck.cardNameWithBackgroundColor} from the top of your deck?")
        }
        if (player.hand.size < 5) {
            player.drawCards(5 - player.hand.size)
        }
        player.optionallyTrashCardsFromHand(1, "You may trash a non-Treasure card from your hand", { c -> !c.isTreasure })
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.discardTopCardOfDeck()
        }
    }

    companion object {
        const val NAME: String = "Jack of All Trades"
    }
}

