package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

class SelectCardsToTrashFromDeck(cards: List<Card>, numCardsToTrash: Int, optional: Boolean = false)
    : SelectCardsAction("", cards, numCardsToTrash, optional) {

    init {
        setTextFromNumberOfCardsToTrash()
    }

    private fun setTextFromNumberOfCardsToTrash() {
        text = "Trash $numCardsToSelect card"
        if (numCardsToSelect != 1) {
            text += "s"
        }
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        if (result.isDoneWithAction) {
            selectedCards.forEach( {
                player.addGameLog("${it.cardNameWithBackgroundColor} trashed from ${player.username}'s deck")
                player.removeCardFromDeck(it)
                player.cardTrashed(it)
            })
            return true
        } else {
            val selectedCard = result.selectedCard!!
            if (selectedCards.contains(selectedCard)) {
                selectedCards.remove(selectedCard)
            } else {
                if (!optional && numCardsToSelect == 1) {
                    selectedCards.clear()
                }
                selectedCards.add(selectedCard)
            }
        }

        return false
    }
}