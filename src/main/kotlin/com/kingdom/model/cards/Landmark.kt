package com.kingdom.model.cards

import com.kingdom.model.players.Player

abstract class Landmark(name: String,
                        deck: Deck) : Card(name, deck, CardType.Landmark, 0) {

    open fun isLandmarkActionable(player: Player): Boolean = false
}