package com.kingdom.model.cards.listeners

import com.kingdom.model.players.Player

interface CardBoughtListenerForSelf {

    //returns true if this handles buying the card
    fun onCardBought(player: Player): Boolean

}