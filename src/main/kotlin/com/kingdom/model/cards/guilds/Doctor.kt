package com.kingdom.model.cards.guilds

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForSelf
import com.kingdom.model.players.Player

class Doctor : GuildsCard(NAME, CardType.Action, 3), ChooseCardActionCard, AfterCardBoughtListenerForSelf, ChoiceActionCard {

    init {
        special = "Name a card. Reveal the top 3 cards of your deck. Trash the matches. Put the rest in any order. When you buy this, you may overpay for it. For each \$1 you overpaid, look at the top card of your deck; trash it, discard it, or put it back."
        isOverpayForCardAllowed = true
        textSize = 122
        isTrashingCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cardsToSelectFrom = player.game.allCardsCopy
        player.chooseCardAction(special, this, cardsToSelectFrom, false)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.addEventLogWithUsername("named ${card.cardNameWithBackgroundColor}")

        val cards = player.removeTopCardsOfDeck(3, true)
        val cardsToTrash = cards.filter { it.name == card.name }
        cardsToTrash.forEach { player.cardTrashed(it, true) }
        val cardsToPutBack = cards - cardsToTrash
        if (cardsToPutBack.size == 1) {
            player.addCardToTopOfDeck(cardsToPutBack.first(), true)
        } else if (cardsToPutBack.isNotEmpty()) {
            player.putCardsOnTopOfDeckInAnyOrder(cardsToPutBack)
        }
    }

    override fun afterCardBought(player: Player) {
        if (player.availableCoins > 0) {
            player.yesNoChoice(this, "For each \$1 you overpaid, look at the top card of your deck; trash it, discard it, or put it back. Overpay?")
        } else {
            player.showInfoMessage("No coins available for overpaying")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {

        when (info) {
            is String -> {
                player.addCoins(choice * -1)
                val cards = player.removeTopCardsOfDeck(choice)
                cards.forEachIndexed { index, card ->
                    player.makeChoiceWithInfo(this, "Top card of deck ${index + 1}: ${card.cardNameWithBackgroundColor}", card, Choice(1, "Trash"), Choice(2, "Discard"), Choice(3, "Put back"))
                }
            }
            is Card -> {
                when (choice) {
                    1 -> player.cardTrashed(info, true)
                    2 -> player.addCardToDiscard(info, showLog = true)
                    3 -> player.addCardToTopOfDeck(info, true)
                }
            }
            else -> if (choice == 1) {
                val choices = mutableListOf<Choice>()

                for (i in 1..player.availableCoins) {
                    choices.add(Choice(i, i.toString()))
                }

                player.makeChoiceFromListWithInfo(this, "How much do you want to overpay?", "overpayChoice", choices)
            }
        }
    }

    companion object {
        const val NAME: String = "Doctor"
    }
}

