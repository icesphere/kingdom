package com.kingdom.model.cards.seaside

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class NativeVillage : SeasideCard(NAME, CardType.Action, 2), GameSetupModifier, ChoiceActionCard {

    init {
        addActions = 2
        special = "Choose one: Put the top card of your deck face down on your Native Village mat (you may look at those cards at any time); or put all the cards from your mat into your hand."
        fontSize = 10
    }

    override fun modifyGameSetup(game: Game) {
        game.isShowNativeVillage = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this,
                "$special Cards on mat: ${player.nativeVillageCards.groupedString}",
                Choice(1, "Add card to mat"),
                Choice(2, "Put cards from mat into hand"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addEventLogWithUsername("chose to add a card to their native village")
            val topCardOfDeck = player.removeTopCardOfDeck()
            if (topCardOfDeck != null) {
                player.nativeVillageCards.add(topCardOfDeck)
                player.showInfoMessage("Added ${topCardOfDeck.cardNameWithBackgroundColor} to your ${this.cardNameWithBackgroundColor}")
            }
        } else {
            player.addEventLogWithUsername("chose to put their native village cards into their hand")
            player.hand.addAll(player.nativeVillageCards)
            player.nativeVillageCards.clear()
        }
    }

    companion object {
        const val NAME: String = "Native Village"
    }
}

