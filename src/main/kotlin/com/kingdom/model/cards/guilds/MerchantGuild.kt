package com.kingdom.model.cards.guilds

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForCardsInPlay
import com.kingdom.model.players.Player

class MerchantGuild : GuildsCard(NAME, CardType.Action, 5), GameSetupModifier, AfterCardBoughtListenerForCardsInPlay {

    init {
        addBuys = 1
        addCoins = 1
        special = "While this is in play, when you buy a card, +1 Coffers"
        fontSize = 9
    }

    override fun modifyGameSetup(game: Game) {
        game.isShowCoffers = true
    }

    override fun afterCardBought(card: Card, player: Player) {
        player.addCoffers(1)
    }

    companion object {
        const val NAME: String = "Merchant Guild"
    }
}

