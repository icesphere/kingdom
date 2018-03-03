package com.kingdom.model.cards

import com.kingdom.model.CardAction
import com.kingdom.model.Deck
import com.kingdom.util.KingdomUtil

import javax.persistence.*
import java.util.ArrayList
import java.util.HashMap

@Table(name = "cards")
@Entity
class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cardid")
    var cardId: Int = 0

    var name = ""

    var cost: Int = 0

    var type: Int = 0

    var special: String? = ""

    @Column(name = "add_actions")
    var addActions: Int = 0

    @Column(name = "add_coins")
    var addCoins: Int = 0

    @Column(name = "add_cards")
    var addCards: Int = 0

    @Column(name = "add_buys")
    var addBuys: Int = 0

    @Column(name = "victory_points")
    var victoryPoints: Int = 0

    @Column(name = "deck")
    var deckString: String? = null

    @Transient
    var deck: Deck? = null
        get() = deckString?.let { Deck.valueOf(deckString!!) }

    var testing: Boolean = false

    @Column(name = "cost_includes_potion")
    var costIncludesPotion: Boolean = false

    @Column(name = "add_victory_coins")
    var addVictoryCoins: Int = 0

    @Column(name = "play_treasure_cards")
    var playTreasureCards: Boolean = false

    var disabled: Boolean = false

    @Column(name = "fan_expansion_card")
    var fanExpansionCard: Boolean = false

    var sins: Int = 0

    @Column(name = "prize_card")
    var prizeCard: Boolean = false

    @Column(name = "font_size")
    var fontSize: Int = 0

    @Column(name = "name_lines")
    var nameLines = 1

    @Column(name = "text_size")
    var textSize: Int = 0

    @Column(name = "fruit_tokens")
    var fruitTokens: Int = 0

    @Column(name = "cattle_tokens")
    var cattleTokens: Int = 0

    @Transient
    val associatedCards: MutableList<Card> = ArrayList(0)

    @Transient
    var isDisableSelect: Boolean = false
    
    @Transient
    var isAutoSelect: Boolean = false

    @Transient
    var isActivated: Boolean = false

    @Transient
    var isCopied: Boolean = false

    @Transient
    var gainCardActions: MutableMap<String, CardAction> = HashMap(0)

    @Transient
    var destination = ""

    @Transient
    var isTraderProcessed: Boolean = false

    @Transient
    var isCardNotGained: Boolean = false

    @Transient
    var isGainedFromBuy: Boolean = false

    val typeAsString: String
        get() {
            return when (type) {
                TYPE_ACTION -> "Action"
                TYPE_ACTION_ATTACK -> "Action - Attack"
                TYPE_ACTION_REACTION -> "Action - Reaction"
                TYPE_VICTORY -> "Victory"
                TYPE_CURSE -> "Curse"
                TYPE_TREASURE -> "Treasure"
                TYPE_ACTION_AND_VICTORY -> "Action - Victory"
                TYPE_TREASURE_AND_VICTORY -> "Treasure - Victory"
                TYPE_ACTION_DURATION -> "Action - Duration"
                TYPE_VICTORY_AND_REACTION -> "Victory - Reaction"
                TYPE_TREASURE_AND_CURSE -> "Treasure - Curse"
                TYPE_DURATION_AND_VICTORY -> "Duration - Victory"
                TYPE_LEADER -> "Leader"
                TYPE_TREASURE_REACTION -> "Treasure - Reaction"
                TYPE_ACTION_SUMMON -> "Action - Summon"
                else -> ""
            }
        }

    val isSpecialCard: Boolean
        get() = special != ""

    val truncatedSpecial: String?
        get() = if (textSize > 0) {
            special!!.substring(0, textSize) + "..."
        } else {
            special
        }

    val fullCardText: String
        get() {
            val sb = StringBuilder()
            sb.append(typeAsString)
            sb.append(" (cost ").append(cost)
            if (costIncludesPotion) {
                sb.append(" and a potion")
            }
            sb.append("): ")
            if (victoryPoints != 0) {
                sb.append(victoryPoints).append(" VP. ")
            }
            if (addCoins != 0) {
                sb.append(getAmountSymbol(addCoins)).append(KingdomUtil.getPlural(addCoins, "coin")).append(". ")
            }
            if (addCards != 0) {
                sb.append(getAmountSymbol(addCards)).append(KingdomUtil.getPlural(addCards, "card")).append(". ")
            }
            if (addActions != 0) {
                sb.append(getAmountSymbol(addActions)).append(KingdomUtil.getPlural(addActions, "action")).append(". ")
            }
            if (addBuys != 0) {
                sb.append(getAmountSymbol(addBuys)).append(KingdomUtil.getPlural(addBuys, "buy")).append(". ")
            }
            if (sins != 0) {
                sb.append(getAmountSymbol(sins)).append(KingdomUtil.getPlural(sins, "sin")).append(". ")
            }
            if (addVictoryCoins != 0) {
                sb.append(getAmountSymbol(addVictoryCoins)).append(KingdomUtil.getPlural(addVictoryCoins, "victory coin")).append(". ")
            }
            sb.append(special)
            return sb.toString()
        }

    val isVictoryOnly: Boolean
        get() = type == TYPE_VICTORY

    val isVictory: Boolean
        get() = type == TYPE_VICTORY || type == TYPE_ACTION_AND_VICTORY || type == TYPE_TREASURE_AND_VICTORY || type == TYPE_VICTORY_AND_REACTION || type == TYPE_DURATION_AND_VICTORY

    val isVictoryReaction: Boolean
        get() = type == TYPE_VICTORY_AND_REACTION

    val isAction: Boolean
        get() = type == TYPE_ACTION || type == TYPE_ACTION_ATTACK || type == TYPE_ACTION_REACTION || type == TYPE_ACTION_AND_VICTORY || type == TYPE_ACTION_DURATION || type == TYPE_DURATION_AND_VICTORY || type == TYPE_ACTION_SUMMON

    val isTerminalAction: Boolean
        get() = isAction && addActions == 0 && name != "Nobles" && name != "Pawn" && name != "Trusty Steed"

    val isDuration: Boolean
        get() = type == TYPE_ACTION_DURATION || type == TYPE_DURATION_AND_VICTORY

    val isTreasure: Boolean
        get() = type == TYPE_TREASURE || type == TYPE_TREASURE_AND_VICTORY || type == TYPE_TREASURE_AND_CURSE || type == TYPE_TREASURE_REACTION

    val isReaction: Boolean
        get() = type == TYPE_ACTION_REACTION || type == TYPE_VICTORY_AND_REACTION || type == TYPE_TREASURE_REACTION

    val isCurse: Boolean
        get() = type == TYPE_CURSE || type == TYPE_TREASURE_AND_CURSE

    val isCurseOnly: Boolean
        get() = cardId == CURSE_ID

    val isCopper: Boolean
        get() = cardId == COPPER_ID

    val isSilver: Boolean
        get() = cardId == SILVER_ID

    val isGold: Boolean
        get() = cardId == GOLD_ID

    val isPlatinum: Boolean
        get() = cardId == PLATINUM_ID

    val isEstate: Boolean
        get() = cardId == ESTATE_ID

    val isDuchy: Boolean
        get() = cardId == DUCHY_ID

    val isProvince: Boolean
        get() = cardId == PROVINCE_ID

    val isColony: Boolean
        get() = cardId == COLONY_ID

    val isPotion: Boolean
        get() = cardId == POTION_ID

    val isAttack: Boolean
        get() = type == TYPE_ACTION_ATTACK

    val isDefense: Boolean
        get() = name == "Moat" || name == "Lighthouse" || name == "Watchtower" || name == "Bell Tower" || name == "Enchanted Palace"

    val isKingdom: Boolean
        get() = deck === Deck.Kingdom

    val isIntrigue: Boolean
        get() = deck === Deck.Intrigue

    val isSeaside: Boolean
        get() = deck === Deck.Seaside

    val isAlchemy: Boolean
        get() = deck === Deck.Alchemy

    val isPromo: Boolean
        get() = deck === Deck.Promo

    val isProsperity: Boolean
        get() = deck === Deck.Prosperity

    val isSalvation: Boolean
        get() = deck === Deck.Salvation

    val isFairyTale: Boolean
        get() = deck === Deck.FairyTale

    val isCornucopia: Boolean
        get() = deck === Deck.Cornucopia

    val isHinterlands: Boolean
        get() = deck === Deck.Hinterlands

    val isLeader: Boolean
        get() = deck === Deck.Leaders

    val isProletariat: Boolean
        get() = deck === Deck.Proletariat

    val isFan: Boolean
        get() = deck === Deck.Fan

    val backgroundColor: String
        get() = when {
            type == TYPE_ACTION_AND_VICTORY -> ACTION_AND_VICTORY_IMAGE
            type == TYPE_TREASURE_AND_VICTORY -> TREASURE_AND_VICTORY_IMAGE
            type == TYPE_TREASURE_AND_CURSE -> TREASURE_AND_CURSE_IMAGE
            type == TYPE_VICTORY_AND_REACTION -> VICTORY_AND_REACTION_IMAGE
            type == TYPE_DURATION_AND_VICTORY -> DURATION_AND_VICTORY_IMAGE
            type == TYPE_TREASURE_REACTION -> TREASURE_REACTION_IMAGE
            isTreasure -> TREASURE_COLOR
            isVictory -> VICTORY_COLOR
            type == TYPE_ACTION_REACTION -> ACTION_REACTION_COLOR
            type == TYPE_CURSE -> CURSE_COLOR
            type == TYPE_ACTION_DURATION -> ACTION_DURATION_COLOR
            type == TYPE_LEADER -> LEADER_COLOR
            else -> ACTION_COLOR
        }

    val isAutoPlayTreasure: Boolean
        get() = name != "Bank" && name != "Venture" && name != "Contraband" && name != "Loan" && name != "Horn of Plenty" && name != "Talisman" && name != "Diadem" && name != "Storybook" && name != "Ill-Gotten Gains" && name != "Fool's Gold"

    val isTrashingCard: Boolean
        get() = (name == "Chapel" || name == "Mine" || name == "Moneylender" || name == "Remodel"
                || name == "Masquerade" || name == "Steward" || name == "Trading Post"
                || name == "Upgrade" || name == "Ambassador" || name == "Island"
                || name == "Lookout" || name == "Salvager" || name == "Apprentice"
                || name == "Transmute" || name == "Bishop" || name == "Expand" || name == "Forge"
                || name == "Loan" || name == "Trade Route" || name == "Remake" || name == "Develop"
                || name == "Jack of all Trades" || name == "Spice Merchant" || name == "Trader")

    val isExtraActionsCard: Boolean
        get() = addActions >= 2 || name == "Throne Room" || name == "King's Court"

    val isVictoryCoinsCard: Boolean
        get() = addVictoryCoins > 0 || name == "Goons"

    val actionValue: Int
        get() {
            return when {
                isAction -> 1
                isTreasure -> 2
                isVictory -> 3
                isCurseOnly -> 4
                else -> 9
            }
        }

    constructor()

    constructor(cardId: Int, type: Int, name: String, cost: Int) {
        this.cardId = cardId
        this.type = type
        this.name = name
        this.cost = cost
    }

    constructor(card: Card) {
        this.cardId = card.cardId
        this.name = card.name
        this.cost = card.cost
        this.type = card.type
        this.special = card.special
        this.addActions = card.addActions
        this.addCoins = card.addCoins
        this.addCards = card.addCards
        this.addBuys = card.addBuys
        this.addVictoryCoins = card.addVictoryCoins
        this.victoryPoints = card.victoryPoints
        this.deck = card.deck
        this.testing = card.testing
        this.costIncludesPotion = card.costIncludesPotion
        this.sins = card.sins
        this.fontSize = card.fontSize
        this.nameLines = card.nameLines
        this.textSize = card.textSize
        this.isCopied = true
    }

    private fun getAmountSymbol(amount: Int): String {
        return if (amount < 0) {
            ""
        } else {
            "+"
        }
    }

    fun hasSpecial(): Boolean {
        return special != null && special != ""
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Card) return false
        val card = other
        return cardId == card.cardId
    }

    override fun hashCode(): Int {
        var result = cardId
        result = 31 * result + type
        return result
    }

    companion object {
        const val TYPE_ACTION = 1
        const val TYPE_ACTION_ATTACK = 2
        const val TYPE_VICTORY = 3
        const val TYPE_ACTION_REACTION = 4
        const val TYPE_TREASURE = 5
        const val TYPE_CURSE = 6
        const val TYPE_ACTION_AND_VICTORY = 7
        const val TYPE_TREASURE_AND_VICTORY = 8
        const val TYPE_ACTION_DURATION = 9
        const val TYPE_TREASURE_AND_CURSE = 10
        const val TYPE_VICTORY_AND_REACTION = 11
        const val TYPE_DURATION_AND_VICTORY = 12
        const val TYPE_LEADER = 13
        const val TYPE_TREASURE_REACTION = 14
        const val TYPE_ACTION_SUMMON = 15

        const val COPPER_ID = -1
        const val SILVER_ID = -2
        const val GOLD_ID = -3
        const val PLATINUM_ID = -9

        const val ESTATE_ID = -4
        const val DUCHY_ID = -5
        const val PROVINCE_ID = -6
        const val COLONY_ID = -10

        const val CURSE_ID = -7

        const val POTION_ID = -8

        const val TREASURE_COLOR = "#F6DC51"
        const val CURSE_COLOR = "#A17FBC"
        const val VICTORY_COLOR = "#80B75A"
        const val ACTION_REACTION_COLOR = "#7FAED8"
        const val ACTION_DURATION_COLOR = "#F09954"
        const val ACTION_COLOR = "#CBC6B3"
        const val LEADER_COLOR = "#BB0000"

        const val ACTION_AND_VICTORY_IMAGE = "grey_green.gif"
        const val TREASURE_AND_VICTORY_IMAGE = "gold_green.gif"
        const val VICTORY_AND_REACTION_IMAGE = "green_blue.gif"
        const val TREASURE_AND_CURSE_IMAGE = "gold_purple.gif"
        const val DURATION_AND_VICTORY_IMAGE = "orange_green.gif"
        const val TREASURE_REACTION_IMAGE = "gold_blue.gif"

        val estateCard: Card
            get() {
                return Card(ESTATE_ID, TYPE_VICTORY, "Estate", 2).apply { victoryPoints = 1 }
            }

        val duchyCard: Card
            get() {
                return Card(DUCHY_ID, TYPE_VICTORY, "Duchy", 5).apply { victoryPoints = 3 }
            }

        val provinceCard: Card
            get() {
                return Card(PROVINCE_ID, TYPE_VICTORY, "Province", 8).apply { victoryPoints = 6 }
            }

        val colonyCard: Card
            get() {
                return Card(COLONY_ID, TYPE_VICTORY, "Colony", 11).apply { victoryPoints = 11 }
            }

        val copperCard: Card
            get() {
                return Card(COPPER_ID, TYPE_TREASURE, "Copper", 0).apply { addCoins = 1 }
            }

        val silverCard: Card
            get() {
                return Card(SILVER_ID, TYPE_TREASURE, "Silver", 3).apply { addCoins = 2 }
            }

        val goldCard: Card
            get() {
                return Card(GOLD_ID, TYPE_TREASURE, "Gold", 6).apply { addCoins = 3 }
            }

        val platinumCard: Card
            get() {
                return Card(PLATINUM_ID, TYPE_TREASURE, "Platinum", 9).apply { addCoins = 5 }
            }

        val curseCard: Card
            get() {
                return Card(CURSE_ID, TYPE_CURSE, "Curse", 0).apply { victoryPoints = -1 }
            }

        val potionCard: Card
            get() {
                return Card(POTION_ID, TYPE_TREASURE, "Potion", 4)
            }
    }
}
