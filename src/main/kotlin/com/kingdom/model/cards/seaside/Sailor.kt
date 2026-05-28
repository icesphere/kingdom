package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCardOptional
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForCardsInPlay
import com.kingdom.model.cards.listeners.TurnEndedListenerForCardsPlayedThisTurn
import com.kingdom.model.players.Player

class Sailor : SeasideCard(NAME, CardType.ActionDuration, 4), StartOfTurnDurationAction,
        AfterCardGainedListenerForCardsInPlay, TurnEndedListenerForCardsPlayedThisTurn, ChoiceActionCard,
        ChooseCardActionCardOptional {

    private var canPlayGainedDuration = false
    private var playedGainedDuration = false

    init {
        addCoins = 2
        special = "Once this turn, when you gain a Duration card, you may play it. At the start of your next turn, +\$2, and you may trash a card from your hand."
        fontSize = 10
        isAddCoinsCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        canPlayGainedDuration = true
        playedGainedDuration = false
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (canPlayGainedDuration && !playedGainedDuration && card.isDuration && player.allCardsWithoutInPlay.contains(card)) {
            player.yesNoChoice(this, "Play gained ${card.cardNameWithBackgroundColor}?", card)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            playedGainedDuration = true
            val card = info as Card
            player.removeCard(card)
            player.addCardToHand(card)
            if (card.isAction) {
                player.addActions(1)
            }
            player.playCard(card)
        }
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.addCoins(2)
        player.chooseCardFromHandOptional("You may trash a card from your hand", this)
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        if (card != null) {
            player.trashCardFromHand(card)
        }
    }

    override fun onTurnEnded(player: Player) {
        canPlayGainedDuration = false
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        canPlayGainedDuration = false
        playedGainedDuration = false
    }

    companion object {
        const val NAME: String = "Sailor"
    }
}
