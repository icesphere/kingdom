package com.kingdom.model.cards.renaissance

import com.kingdom.model.Choice
import com.kingdom.model.cards.Artifact
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ArtifactAction
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.renaissance.artifacts.Horn
import com.kingdom.model.cards.renaissance.artifacts.Lantern
import com.kingdom.model.players.Player

class BorderGuard : RenaissanceCard(NAME, CardType.Action, 2), ArtifactAction, ChoiceActionCard {

    init {
        disabled = true
        special = "Reveal the top 2 cards of your deck. Put one into your hand and discard the other. If both were Actions, take the Lantern or Horn."
    }

    override val artifacts: List<Artifact>
        get() = listOf(Lantern(), Horn())

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this, Choice(1, "Take Lantern"), Choice(2, "Take Horn"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.takeLantern()
        } else {
            player.takeHorn()
        }
    }

    companion object {
        const val NAME: String = "Border Guard"
    }
}