package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Masquerade : IntrigueCard(NAME, CardType.Action, 3), ChooseCardActionCard {

    private val cardsPassedMap = mutableMapOf<Player, Card>()

    private val passToPlayerMap = mutableMapOf<Player, Player>()

    private var numPlayersPassingCards = 0

    init {
        addCards = 2
        special = "Each player with any cards in hand passes one to the next such player to their left, at once. Then you may trash a card from your hand."
        textSize = 102
        fontSize = 11
        isTrashingCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val game = player.game

        val passingPlayers = game.players.filter { it.hand.isNotEmpty() }

        if (passingPlayers.size < 2) {
            return
        }

        numPlayersPassingCards = passingPlayers.size

        passingPlayers.forEachIndexed { index, p ->
            val playerOnLeft = if (index == passingPlayers.lastIndex) {
                passingPlayers[0]
            } else {
                passingPlayers[index + 1]
            }

            passToPlayerMap[p] = playerOnLeft

            p.chooseCardFromHand("Choose a card from your hand to pass to ${playerOnLeft.username}", this)
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        cardsPassedMap[player] = card

        player.removeCardFromHand(card)

        if (cardsPassedMap.size == numPlayersPassingCards) {
            for ((p, c) in cardsPassedMap) {
                val playerOnLeft = passToPlayerMap[p]!!
                playerOnLeft.addCardToHand(c)
            }

            player.game.currentPlayer.trashCardFromHand(true)
        }
    }

    override fun removedFromPlay(player: Player) {
        clearCardVariables()
        super.removedFromPlay(player)
    }

    private fun clearCardVariables() {
        cardsPassedMap.clear()
        passToPlayerMap.clear()
        numPlayersPassingCards = 0
    }

    companion object {
        const val NAME: String = "Masquerade"
    }
}

