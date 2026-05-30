package com.kingdom.model.cards

import com.kingdom.model.players.Player

abstract class Trait(name: String, deck: Deck) : Card(name, deck, CardType.Trait, 0) {

    var traitPileName: String? = null
        private set

    private var traitRulesText: String = ""

    protected fun setTraitRulesText(text: String) {
        traitRulesText = text
        refreshSpecialText()
    }

    fun assignToPile(card: Card) {
        traitPileName = card.pileName
        refreshSpecialText()
    }

    fun appliesTo(card: Card): Boolean {
        return traitPileName != null && traitPileName == card.pileName
    }

    open fun onCardGained(card: Card, player: Player): Boolean = false

    open fun afterCardGained(card: Card, player: Player) {
    }

    open fun afterCardPlayed(card: Card, player: Player) {
    }

    open fun onStartOfTurn(player: Player) {
    }

    open fun onStartOfCleanup(player: Player) {
    }

    open fun afterShuffle(player: Player) {
    }

    open fun handleDiscardFromPlay(card: Card, player: Player): Boolean = false

    private fun refreshSpecialText() {
        special = traitPileName?.let { "On $it: $traitRulesText" } ?: traitRulesText
    }
}
