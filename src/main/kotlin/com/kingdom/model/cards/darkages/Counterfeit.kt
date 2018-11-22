package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Counterfeit : DarkAgesCard(NAME, CardType.Treasure, 5), ChoiceActionCard, ChooseCardActionCard {

    init {
        testing = true
        addCoins = 1
        addBuys = 1
        special = "When you play this, you may play a treasure from your hand twice. If you do, trash that treasure."
        isPlayTreasureCardsRequired = true
        isTreasureExcludedFromAutoPlay = true
        textSize = 80
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it.isTreasure }) {
            player.yesNoChoice(this, "Play a treasure from your hand twice? (If you do, trash that treasure)")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.chooseCardFromHand("Choose a treasure card from your hand to play twice", this, { c -> c.isTreasure })
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.playCard(card)
        player.playCard(card, false, true)
        player.trashCardInPlay(card)
    }

    companion object {
        const val NAME: String = "Counterfeit"
    }
}

