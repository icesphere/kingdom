package com.kingdom.model.cards.empires

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.MultiTypePile
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Gladiator : EmpiresCard(NAME, CardType.Action, 3), MultiTypePile, ChooseCardActionCard {

    init {
        addCoins = 2
        special = "Reveal a card from your hand. The player to your left may reveal a copy from their hand. If they don’t, +\$1 and trash a Gladiator from the Supply. (Gladiator is the top half of the Fortune pile.)"
    }

    override val otherCardsInPile: List<Card>
        get() = listOf(Fortune())

    override fun createMultiTypePile(game: Game): List<Card> {
        return listOf(
                Gladiator(),
                Gladiator(),
                Gladiator(),
                Gladiator(),
                Gladiator(),
                Fortune(),
                Fortune(),
                Fortune(),
                Fortune(),
                Fortune()
        )
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromHand("Reveal a card from your hand. The player to your left may reveal a copy from their hand. If they don’t, +\$1 and trash a Gladiator from the Supply.", this)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.revealCardFromHand(card)

        val playerToLeft = player.playerToLeft
        val copy = playerToLeft.hand.firstOrNull { it.name == card.name }
        if (copy != null) {
            copyRevealed(player, playerToLeft, copy)
        } else {
            noCopyRevealed(player, playerToLeft)
        }
    }

    private fun copyRevealed(player: Player, playerToLeft: Player, card: Card) {
        playerToLeft.revealCardFromHand(card)
        player.showInfoMessage("${playerToLeft.username} revealed ${card.cardNameWithBackgroundColor}")
    }

    private fun noCopyRevealed(player: Player, playerToLeft: Player) {
        player.showInfoMessage("${playerToLeft.username} did not reveal a copy")
        player.addCoins(1)
        player.trashCardFromSupply(Gladiator())
    }

    companion object {
        const val NAME: String = "Gladiator"
    }
}
