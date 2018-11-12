package com.kingdom.model.cards.darkages.shelters

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.darkages.DarkAgesCard
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForCardsInHand
import com.kingdom.model.players.Player

class Hovel : DarkAgesCard(NAME, CardType.ReactionShelter, 1), ChoiceActionCard, AfterCardBoughtListenerForCardsInHand {

    init {
        special = "When you buy a Victory card, you may trash this from your hand."
    }

    override fun afterCardBought(card: Card, player: Player) {
        if (card.isVictory) {
            player.yesNoChoice(this, "Trash ${this.cardNameWithBackgroundColor} from your hand?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.trashCardFromHand(this)
        }
    }

    companion object {
        const val NAME: String = "Hovel"
    }
}

