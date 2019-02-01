package com.kingdom.model.cards.darkages

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Pillage : DarkAgesCard(NAME, CardType.ActionAttack, 5), GameSetupModifier, AttackCard, ChooseCardActionCard {

    init {
        special = "Trash this. Each other player with 5 or more cards in hand reveals their hand and discards a card that you choose. Gain 2 Spoils from the Spoils pile."
        textSize = 119
    }

    override fun modifyGameSetup(game: Game) {
        game.isIncludeSpoils = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardInPlay(this, true)
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        for (opponent in affectedOpponents) {
            if (opponent.hand.size >= 5) {
                opponent.revealHand()
                player.chooseCardAction("Choose a card from ${opponent.username}'s hand to discard", this, opponent.handCopy, false, opponent)
            }
        }

        player.gainSpoils()
        player.gainSpoils()
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        val opponent = info as Player
        opponent.discardCardFromHand(opponent.hand.first { it.name == card.name }, true)
        opponent.showInfoMessage("${player.username}'s ${this.cardNameWithBackgroundColor} discarded ${card.cardNameWithBackgroundColor} from your hand")
    }

    companion object {
        const val NAME: String = "Pillage"
    }
}

