package com.kingdom.model.cards.menagerie.ways

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.players.Player
import com.kingdom.repository.CardRepository

class WayOfTheMouse : MenagerieWay(NAME), GameSetupModifier {

    init {
        special = "Play the set-aside card, leaving it there. Setup: Set aside an unused Action costing \$2 or \$3."
    }

    lateinit var setAsideCard: Card

    override fun modifyGameSetup(game: Game) {
        val allCards = CardRepository().allCards
        val kingdomCardPileNames = game.kingdomCards.map { it.pileName }
        val availableCards = allCards
                .filter { !kingdomCardPileNames.contains(it.pileName) }
                .filter { it.isAction && it.cost >= 2 && it.cost <= 3 }
        setAsideCard = availableCards.shuffled().first()
        game.cardsNotInSupply.add(setAsideCard)
        special = "Play the set-aside card, leaving it there. (Set-aside card is ${setAsideCard.name})"
    }

    override fun waySpecialAction(player: Player, card: Card) {
        player.addActions(1)
        setAsideCard.cardPlayed(player, ignoreWays = true)
    }

    companion object {
        const val NAME: String = "Way of the Mouse"
    }

}