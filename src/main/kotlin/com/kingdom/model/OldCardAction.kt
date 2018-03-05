package com.kingdom.model

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import java.util.*

class OldCardAction(var type: Int) {
    var cards: MutableList<Card> = ArrayList()
    var numCards: Int = 0
    var instructions = ""
    var buttonValue = ""
    var cardName = ""
    var cardId: Int = 0
    var phase: Int = 0
    var playerId: Int = 0
    var width = 0
        get() {
            if (field > 0) {
                return field
            }
            return when (type) {
                TYPE_WAITING_FOR_PLAYERS -> 250
                TYPE_YES_NO -> 500
                else -> 750
            }
        }
    var choices: MutableList<CardActionChoice> = ArrayList()
    var isHideOnSelect: Boolean = false
    var destination: String? = null
    var startNumber: Int = 0
    var endNumber: Int = 0
    var deck: Deck? = null
    var associatedCard: Card? = null
    var action: String? = null
    var isGainCardAction: Boolean = false
    var isGainCardAfterBuyAction: Boolean = false

    val isDiscard: Boolean
        get() = type == TYPE_DISCARD_DOWN_TO_FROM_HAND || type == TYPE_DISCARD_FROM_HAND || type == TYPE_DISCARD_UP_TO_FROM_HAND || type == TYPE_DISCARD_AT_LEAST_FROM_HAND || type == TYPE_DISCARD_UP_TO

    val isWaitingForPlayers: Boolean
        get() = type == TYPE_WAITING_FOR_PLAYERS

    val isSelectExact: Boolean
        get() = (type == TYPE_DISCARD_FROM_HAND || type == TYPE_GAIN_CARDS_FROM_SUPPLY || type == TYPE_GAIN_CARDS_INTO_HAND_FROM_SUPPLY
                || type == TYPE_TRASH_CARDS_FROM_HAND || type == TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK
                || type == TYPE_CHOOSE_CARDS || type == TYPE_CHOOSE_IN_ORDER || type == TYPE_GAIN_CARDS || type == TYPE_SETUP_LEADERS)

    val isSelectUpTo: Boolean
        get() = type == TYPE_DISCARD_UP_TO_FROM_HAND || type == TYPE_TRASH_UP_TO_FROM_HAND || type == TYPE_GAIN_UP_TO_FROM_SUPPLY || type == TYPE_CHOOSE_UP_TO || type == TYPE_GAIN_CARDS_UP_TO

    val isSelectAtLeast: Boolean
        get() = type == TYPE_DISCARD_AT_LEAST_FROM_HAND

    companion object {
        const val TYPE_WAITING_FOR_PLAYERS = 1
        const val TYPE_DISCARD_FROM_HAND = 2
        const val TYPE_DISCARD_DOWN_TO_FROM_HAND = 3
        const val TYPE_DISCARD_UP_TO_FROM_HAND = 4
        const val TYPE_GAIN_CARDS_FROM_SUPPLY = 5
        const val TYPE_GAIN_CARDS_INTO_HAND_FROM_SUPPLY = 6
        const val TYPE_TRASH_CARDS_FROM_HAND = 7
        const val TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK = 8
        const val TYPE_TRASH_UP_TO_FROM_HAND = 9
        const val TYPE_CHOOSE_CARDS = 10
        const val TYPE_YES_NO = 11
        const val TYPE_GAIN_UP_TO_FROM_SUPPLY = 12
        const val TYPE_INFO = 13
        const val TYPE_CHOICES = 14
        const val TYPE_CHOOSE_IN_ORDER = 15
        const val TYPE_CHOOSE_UP_TO = 16
        const val TYPE_GAIN_CARDS = 17
        const val TYPE_GAIN_CARDS_UP_TO = 18
        const val TYPE_CHOOSE_NUMBER_BETWEEN = 19
        const val TYPE_SETUP_LEADERS = 20
        const val TYPE_DISCARD_AT_LEAST_FROM_HAND = 21
        const val TYPE_DISCARD_UP_TO = 22
        const val TYPE_CHOOSE_EVEN_NUMBER_BETWEEN = 23

        val waitingForPlayersOldCardAction: OldCardAction
            get() {
                return OldCardAction(TYPE_WAITING_FOR_PLAYERS).apply { instructions = "Waiting For Players" }
            }

        val waitingForSecretChamberOldCardAction: OldCardAction
            get() {
                return OldCardAction(TYPE_WAITING_FOR_PLAYERS).apply {
                    instructions = "Waiting For Players to use Secret Chambers"
                    width = 300
                }
            }

        val waitingForHorseTradersOldCardAction: OldCardAction
            get() {
                return OldCardAction(TYPE_WAITING_FOR_PLAYERS).apply {
                    instructions = "Waiting For Players to use Horse Traders"
                    width = 300
                }
            }

        val waitingForBellTowerOldCardAction: OldCardAction
            get() {
                return OldCardAction(TYPE_WAITING_FOR_PLAYERS).apply {
                    instructions = "Waiting For Players to use Bell Towers"
                    width = 300
                }
            }
    }
}
