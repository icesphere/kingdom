package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player
import java.util.*

abstract class SelectCardsAction(text: String,
                             private val cards: List<Card>,
                             protected var numCardsToSelect: Int = 1,
                             protected val optional: Boolean = false) : Action(text) {

    protected var selectedCards: MutableList<Card> = ArrayList()

    override val isShowDone: Boolean
        get() =
            optional || selectedCards.size == numCardsToSelect

    override fun isCardActionable(card: Card,
                                  cardLocation: CardLocation,
                                  player: Player): Boolean = cardLocation == CardLocation.CardAction

    override fun processAction(player: Player): Boolean = cards.isNotEmpty()

    override fun isCardSelected(card: Card): Boolean {
        return selectedCards.contains(card)
    }
}