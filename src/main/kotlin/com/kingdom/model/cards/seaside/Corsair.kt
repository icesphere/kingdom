package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.cards.listeners.AfterOtherPlayerCardPlayedListenerForCardsInPlay
import com.kingdom.model.players.Player

class Corsair : SeasideCard(NAME, CardType.ActionAttackDuration, 5), AttackCard, StartOfTurnDurationAction,
        AfterOtherPlayerCardPlayedListenerForCardsInPlay {

    init {
        addCoins = 2
        special = "At the start of your next turn, +\$2. Until then, when another player plays a Silver or Gold, they trash it."
        isAddCoinsCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.addCoins(2)
        player.showInfoMessage("Gained +\$2 from ${cardNameWithBackgroundColor}")
    }

    override fun afterCardPlayedByOtherPlayer(card: Card, player: Player, otherPlayer: Player) {
        if ((card.isSilver || card.isGold) && !playersExcludedFromCardEffects.contains(otherPlayer) && otherPlayer.inPlay.contains(card)) {
            otherPlayer.trashCardInPlay(card)
            otherPlayer.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor trashed your ${card.cardNameWithBackgroundColor}")
            player.showInfoMessage("$cardNameWithBackgroundColor trashed ${otherPlayer.username}'s ${card.cardNameWithBackgroundColor}")
        }
    }

    companion object {
        const val NAME: String = "Corsair"
    }
}
