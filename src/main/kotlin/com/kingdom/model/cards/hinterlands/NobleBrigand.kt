package com.kingdom.model.cards.hinterlands

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForSelf
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class NobleBrigand : HinterlandsCard(NAME, CardType.ActionAttack, 4), AfterCardBoughtListenerForSelf, AttackCard, ChoiceActionCard {

    init {
        addCoins = 1
        special = "When you buy or play this, each other player reveals the top 2 cards of their deck, trashes a revealed Silver or Gold you choose, discards the rest, and gains a Copper if they didn’t reveal a Treasure. You gain the trashed cards."
        fontSize = 10
        textSize = 112
    }

    override fun cardPlayedSpecialAction(player: Player) {
        handleAction(player)
    }

    override fun afterCardBought(player: Player) {
        handleAction(player)
    }

    private fun handleAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        for (opponent in affectedOpponents) {
            val cards = opponent.removeTopCardsOfDeck(2, true)
            val silverAndGoldCards = cards.filter { it.isSilver || it.isGold }

            val otherCards = cards - silverAndGoldCards
            opponent.addCardsToDiscard(otherCards)
            opponent.addEventLogWithUsername("discarded ${otherCards.groupedString}")

            if (silverAndGoldCards.isNotEmpty()) {
                if (silverAndGoldCards.size > 1) {
                    val firstCard = silverAndGoldCards[0]
                    val secondCard = silverAndGoldCards[1]
                    if (firstCard.name == secondCard.name) {
                        opponent.addCardToDiscard(firstCard, showLog = true)
                        gainTrashedCard(player, opponent, secondCard)
                    } else {
                        player.makeChoiceWithInfo(this, "${opponent.username} revealed a ${Silver().cardNameWithBackgroundColor} and a ${Gold().cardNameWithBackgroundColor}.  Which one do you want to trash and gain?", opponent, Choice(1, "Silver"), Choice(2, "Gold"))
                    }
                } else {
                    val card = silverAndGoldCards.first()
                    gainTrashedCard(player, opponent, card)
                }
            } else {
                opponent.gainSupplyCard(Copper(), true)
            }
        }
    }

    private fun gainTrashedCard(player: Player, opponent: Player, card: Card) {
        player.cardGained(card)
        player.addEventLogWithUsername("trashed and gained ${opponent.username}'s ${card.cardNameWithBackgroundColor}")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val opponent = info as Player

        if (choice == 1) {
            opponent.addCardToDiscard(Gold(), showLog = true)
            gainTrashedCard(player, opponent, Silver())
        } else {
            opponent.addCardToDiscard(Silver(), showLog = true)
            gainTrashedCard(player, opponent, Gold())
        }
    }

    companion object {
        const val NAME: String = "Noble Brigand"
    }
}

