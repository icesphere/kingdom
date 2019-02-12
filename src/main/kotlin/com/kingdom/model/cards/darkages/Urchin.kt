package com.kingdom.model.cards.darkages

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.CardPlayedListenerForCardsInPlay
import com.kingdom.model.players.Player

class Urchin : DarkAgesCard(NAME, CardType.ActionAttack, 3), GameSetupModifier, AttackCard, CardPlayedListenerForCardsInPlay, ChoiceActionCard {

    init {
        addCards = 1
        addActions = 1
        special = "Each other player discards down to 4 cards in hand. When you play another Attack card with this in play, you may first trash this, to gain a Mercenary from the Mercenary pile."
    }

    override fun modifyGameSetup(game: Game) {
        game.cardsNotInSupply.add(Mercenary())
        game.setupAmountForPile(Mercenary.NAME, 10)
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        for (opponent in affectedOpponents) {
            if (opponent.hand.size > 4) {
                opponent.discardCardsFromHand(opponent.hand.size - 4, false)
            }
        }
    }

    override fun onCardPlayed(card: Card, player: Player) {
        if (card.isAttack && card.id != this.id) {
            player.yesNoChoice(this, "Trash ${this.cardNameWithBackgroundColor} from in play to gain a ${Mercenary().cardNameWithBackgroundColor} from the Mercenary pile?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.trashCardInPlay(this, true)
            player.gainCardNotInSupply(Mercenary())
        }
    }

    companion object {
        const val NAME: String = "Urchin"
    }
}

