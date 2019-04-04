package com.kingdom.model.cards.renaissance

import com.kingdom.model.Choice
import com.kingdom.model.cards.Artifact
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ArtifactAction
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.cards.listeners.CardDiscardedFromPlayListener
import com.kingdom.model.cards.renaissance.artifacts.Horn
import com.kingdom.model.cards.renaissance.artifacts.Lantern
import com.kingdom.model.players.Player

class BorderGuard : RenaissanceCard(NAME, CardType.Action, 2), ArtifactAction, ChoiceActionCard, ChooseCardActionCard, CardDiscardedFromPlayListener {

    init {
        special = "Reveal the top 2 cards of your deck. Put one into your hand and discard the other. If both were Actions, take the Lantern or Horn."
        fontSize = 10
    }

    override val artifacts: List<Artifact>
        get() = listOf(Lantern(), Horn())

    override fun cardPlayedSpecialAction(player: Player) {
        val cardsToReveal = if (player.hasArtifact(Lantern.NAME)) 3 else 2

        val cards = player.removeTopCardsOfDeck(cardsToReveal, true)

        when {
            cards.size > 1 -> {
                player.chooseCardAction("Choose a card to put in your hand and discard the others", this, cards.map { it.copy(false) }, false, cards)
            }
            cards.size == 1 -> {
                player.addCardToHand(cards.first(), true)
                player.showInfoMessage("Added ${cards.first().cardNameWithBackgroundColor} to your hand since it was the only card in your deck")
            }
            else -> player.showInfoMessage("Your deck was empty")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        val cards = (info as List<Card>)

        player.addCardToHand(card, true)

        val cardsToDiscard = cards.toMutableList()
        cardsToDiscard.remove(cards.first { it.name == card.name })

        player.addCardsToDiscard(cardsToDiscard, true)

        val cardsRevealed = if (player.hasArtifact(Lantern.NAME)) 3 else 2

        val allowTakingArtifact = cards.size == cardsRevealed && cards.all { it.isAction }

        if (allowTakingArtifact) {
            val noLantern = !player.hasArtifact(Lantern.NAME)
            val noHorn = !player.hasArtifact(Horn.NAME)
            if (noLantern && noHorn) {
                player.makeChoice(this, Choice(3, "Take Lantern"), Choice(4, "Take Horn"))
            } else if (noLantern) {
                player.takeLantern()
                player.showInfoMessage("You took the ${Lantern().cardNameWithBackgroundColor}")
            } else if (noHorn) {
                player.takeHorn()
                player.showInfoMessage("You took the ${Horn().cardNameWithBackgroundColor}")
            }
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        when (choice) {
            1 -> {
                player.removeCardFromDiscard(this)
                player.addCardToTopOfDeck(this, false)
                player.addEventLogWithUsername("Used ${Horn().cardNameWithBackgroundColor} to add $cardNameWithBackgroundColor onto their deck")
            }
            3 -> player.takeLantern()
            4 -> player.takeHorn()
        }
    }

    override fun onCardDiscarded(player: Player) {
        if (player.hasArtifact(Horn.NAME) && !player.isUsedHornThisTurn) {
            //todo figure out how to not ask twice when discarding cards so that this only gets set to true if they pick yes
            player.isUsedHornThisTurn = true
            player.yesNoChoice(this, "Put $cardNameWithBackgroundColor onto your deck?")
        }
    }

    companion object {
        const val NAME: String = "Border Guard"
    }
}