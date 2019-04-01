package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.Artifact
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ArtifactAction
import com.kingdom.model.cards.listeners.BeforeBuyPhaseListenerForCardsInSupply
import com.kingdom.model.cards.renaissance.artifacts.TreasureChest
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class Swashbuckler : RenaissanceCard(NAME, CardType.Action, 5), BeforeBuyPhaseListenerForCardsInSupply, ArtifactAction {

    init {
        addCards = 3
        special = "If your discard pile has any cards in it: +1 Coffers, then if you have at least 4 Coffers tokens, take the Treasure Chest."
        fontSize = 9
    }

    override val artifacts: List<Artifact>
        get() = listOf(TreasureChest())

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.cardsInDiscard.isNotEmpty()) {
            player.addCoffers(1)
            player.addEventLogWithUsername("gained +1 Coffers from $cardNameWithBackgroundColor")

            if (player.coffers >= 4) {
                player.takeTreasureChest()
            }
        }
    }

    override fun beforeBuyPhase(player: Player) {
        if (player.hasArtifact(TreasureChest.NAME)) {
            player.gainSupplyCard(Gold())
            player.addEventLogWithUsername("gained ${Gold().cardNameWithBackgroundColor} from ${TreasureChest().cardNameWithBackgroundColor}")
        }
    }

    companion object {
        const val NAME: String = "Swashbuckler"
    }
}