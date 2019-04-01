package com.kingdom.model.cards.renaissance

import com.kingdom.model.Choice
import com.kingdom.model.cards.Artifact
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ArtifactAction
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.TurnStartedListenerForCardsInSupply
import com.kingdom.model.cards.renaissance.artifacts.Key
import com.kingdom.model.players.Player

class Treasurer : RenaissanceCard(NAME, CardType.Action, 5), ChoiceActionCard, ArtifactAction, TurnStartedListenerForCardsInSupply {

    init {
        addCoins = 3
        special = "Choose one: Trash a Treasure from your hand; or gain a Treasure from the trash to your hand; or take the Key."
    }

    override val artifacts: List<Artifact>
        get() = listOf(Key())

    override fun cardPlayedSpecialAction(player: Player) {
        val choices = mutableListOf<Choice>()

        if (player.hand.any { it.isTreasure }) {
            choices.add(Choice(1, "Trash Treasure"))
        }

        if (player.game.trashedCards.any { it.isTreasure }) {
            choices.add(Choice(2, "Treasure from trash"))
        }

        if (!player.hasArtifact(Key.NAME)) {
            choices.add(Choice(3, "Take Key"))
        }

        when {
            choices.isEmpty() -> player.showInfoMessage("There were no treasures to trash, no treasures to gain, and you already have the Key")
            choices.size == 1 -> when {
                player.hand.any { it.isTreasure } -> trashTreasure(player)
                player.game.trashedCards.any { it.isTreasure } -> gainTrashedTreasure(player)
                else -> player.takeKey()
            }
            else -> player.makeChoiceFromList(this, choices)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        when (choice) {
            1 -> trashTreasure(player)
            2 -> gainTrashedTreasure(player)
            3 -> player.takeKey()
        }
    }

    private fun trashTreasure(player: Player) {
        player.trashCardFromHand(false) { c -> c.isTreasure }
    }

    private fun gainTrashedTreasure(player: Player) {
        player.isNextCardToHand = true
        player.gainCardFromTrash(false) { c -> c.isTreasure }
    }

    override fun turnStarted(player: Player) {
        if (player.hasArtifact(Key.NAME)) {
            player.addCoins(1)
            player.addEventLogWithUsername("gained +\$1 from ${Key().cardNameWithBackgroundColor}")
        }
    }

    companion object {
        const val NAME: String = "Treasurer"
    }
}