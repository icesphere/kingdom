package com.kingdom.model.cards.prosperity

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.listeners.CardGainedListenerForCardsInSupply
import com.kingdom.model.players.Player

class TradeRoute : ProsperityCard(NAME, CardType.Action, 3), GameSetupModifier, TrashCardsForBenefitActionCard, CardGainedListenerForCardsInSupply {

    init {
        addBuys = 1
        special = "Trash a card from your hand. +\$1 per Coin token on the Trade Route mat. Setup: Add a Coin token to each Victory Supply pile. When a card is gained from that pile, move the token to the Trade Route mat."
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
        fontSize = 10
        textSize = 89
    }

    override fun modifyGameSetup(game: Game) {
        game.isTrackTradeRouteTokens = true
        (game.allCards).filter { it.isVictory }.forEach {
            game.tradeRouteTokenMap[it.name] = true
        }
    }

    override fun onCardGained(card: Card, player: Player) {
        if (card.isVictory && player.game.tradeRouteTokenMap[card.name] == true) {
            player.game.tradeRouteTokenMap[card.name] = false
            player.game.tradeRouteTokensOnMat++
        }
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.trashCardsFromHandForBenefit(this, 1)
        }
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>) {
        player.addCoins(player.game.tradeRouteTokensOnMat)
    }

    companion object {
        const val NAME: String = "Trade Route"
    }
}

