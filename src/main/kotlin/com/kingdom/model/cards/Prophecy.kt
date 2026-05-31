package com.kingdom.model.cards

import com.kingdom.model.players.Player

abstract class Prophecy(name: String,
                        deck: Deck,
                        cost: Int = 0) : Card(name, deck, CardType.Prophecy, cost) {

    var isFulfilled: Boolean = false

    open fun onFulfilled(game: com.kingdom.model.Game) {}
    open fun applyFulfilledEffect(player: Player) {}

    fun isActive(): Boolean = isFulfilled

    open fun onStartOfTurn(player: Player) {}
    open fun onStartOfCleanup(player: Player) {}
    open fun beforeCardPlayed(card: Card, player: Player) {}
    open fun replaceCardPlayed(card: Card, player: Player): Boolean = false
    open fun afterCardPlayed(card: Card, player: Player) {}
    open fun onCardGained(card: Card, player: Player): Boolean = false
    open fun afterCardGained(card: Card, player: Player) {}
    open fun handleDiscardFromPlay(card: Card, player: Player): Boolean = false
}
