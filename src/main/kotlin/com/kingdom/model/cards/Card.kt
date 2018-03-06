package com.kingdom.model.cards

import com.kingdom.model.OldCardAction
import com.kingdom.model.cards.supply.*
import com.kingdom.util.KingdomUtil
import java.util.*

import javax.persistence.*

open class Card(val name: String, val deck: Deck, val type: CardType, val cost: Int) {

    var special: String = ""

    var addActions: Int = 0

    var addCoins: Int = 0

    var addCards: Int = 0

    var addBuys: Int = 0

    var victoryPoints: Int = 0

    var testing: Boolean = false

    var costIncludesPotion: Boolean = false

    var addVictoryCoins: Int = 0

    var playTreasureCards: Boolean = false

    var disabled: Boolean = false

    var fanExpansionCard: Boolean = false

    var sins: Int = 0

    var prizeCard: Boolean = false

    var fontSize: Int = 0

    var nameLines = 1

    var textSize: Int = 0

    var fruitTokens: Int = 0

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
    var gainOldCardActions: MutableMap<String, OldCardAction> = HashMap(0)

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
                CardType.Action -> "Action"
                CardType.ActionAttack -> "Action - Attack"
                CardType.ActionReaction -> "Action - Reaction"
                CardType.Victory -> "Victory"
                CardType.Curse -> "Curse"
                CardType.Treasure -> "Treasure"
                CardType.ActionVictory -> "Action - Victory"
                CardType.TreasureVictory -> "Treasure - Victory"
                CardType.ActionDuration -> "Action - Duration"
                CardType.VictoryReaction -> "Victory - Reaction"
                CardType.TreasureCurse -> "Treasure - Curse"
                CardType.DurationVictory -> "Duration - Victory"
                CardType.Leader -> "Leader"
                CardType.TreasureReaction -> "Treasure - Reaction"
                CardType.ActionSummon -> "Action - Summon"
            }
        }

    val isSpecialCard: Boolean
        get() = special != ""

    val truncatedSpecial: String?
        get() = if (textSize > 0) {
            special.substring(0, textSize) + "..."
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
        get() = type == CardType.Victory

    val isVictory: Boolean
        get() = type == CardType.Victory || type == CardType.ActionVictory || type == CardType.TreasureVictory || type == CardType.VictoryReaction || type == CardType.DurationVictory

    val isVictoryReaction: Boolean
        get() = type == CardType.VictoryReaction

    val isAction: Boolean
        get() = type == CardType.Action || type == CardType.ActionAttack || type == CardType.ActionReaction || type == CardType.ActionVictory || type == CardType.ActionDuration || type == CardType.DurationVictory || type == CardType.ActionSummon

    val isTerminalAction: Boolean
        get() = isAction && addActions == 0 && name != "Nobles" && name != "Pawn" && name != "Trusty Steed"

    val isDuration: Boolean
        get() = type == CardType.ActionDuration || type == CardType.DurationVictory

    val isTreasure: Boolean
        get() = type == CardType.Treasure || type == CardType.TreasureVictory || type == CardType.TreasureCurse || type == CardType.TreasureReaction

    val isReaction: Boolean
        get() = type == CardType.ActionReaction || type == CardType.VictoryReaction || type == CardType.TreasureReaction

    val isCurse: Boolean
        get() = type == CardType.Curse || type == CardType.TreasureCurse

    val isCurseOnly: Boolean
        get() = name == Curse.NAME

    val isCopper: Boolean
        get() = name == Copper.NAME

    val isSilver: Boolean
        get() = name == Silver.NAME

    val isGold: Boolean
        get() = name == Gold.NAME

    val isPlatinum: Boolean
        get() = name == Platinum.NAME

    val isEstate: Boolean
        get() = name == Estate.NAME

    val isDuchy: Boolean
        get() = name == Duchy.NAME

    val isProvince: Boolean
        get() = name == Province.NAME

    val isColony: Boolean
        get() = name == Colony.NAME

    val isPotion: Boolean
        get() = name == Potion.NAME

    val isAttack: Boolean
        get() = type == CardType.ActionAttack

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
            type == CardType.ActionVictory -> ACTION_AND_VICTORY_IMAGE
            type == CardType.TreasureVictory -> TREASURE_AND_VICTORY_IMAGE
            type == CardType.TreasureCurse -> TREASURE_AND_CURSE_IMAGE
            type == CardType.VictoryReaction -> VICTORY_AND_REACTION_IMAGE
            type == CardType.DurationVictory -> DURATION_AND_VICTORY_IMAGE
            type == CardType.TreasureReaction -> TREASURE_REACTION_IMAGE
            isTreasure -> TREASURE_COLOR
            isVictory -> VICTORY_COLOR
            type == CardType.ActionReaction -> ACTION_REACTION_COLOR
            type == CardType.Curse -> CURSE_COLOR
            type == CardType.ActionDuration -> ACTION_DURATION_COLOR
            type == CardType.Leader -> LEADER_COLOR
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

    constructor(card: Card) : this(card.name, card.deck, card.type, card.cost) {
        this.special = card.special
        this.addActions = card.addActions
        this.addCoins = card.addCoins
        this.addCards = card.addCards
        this.addBuys = card.addBuys
        this.addVictoryCoins = card.addVictoryCoins
        this.victoryPoints = card.victoryPoints
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
        return special != ""
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Card) return false
        val card = other
        return name == card.name
    }

    override fun hashCode(): Int {
        return Objects.hash(name)
    }

    companion object {

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
            get() = Estate()

        val duchyCard: Card
            get() = Duchy()

        val provinceCard: Card
            get() = Province()

        val colonyCard: Card
            get() = Colony()

        val copperCard: Card
            get() = Copper()

        val silverCard: Card
            get() = Silver()

        val goldCard: Card
            get() = Gold()

        val platinumCard: Card
            get() = Platinum()

        val curseCard: Card
            get() = Curse()

        val potionCard: Card
            get() = Potion()
    }
}
