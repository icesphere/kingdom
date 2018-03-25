package com.kingdom.model.cards

import com.kingdom.model.cards.supply.*
import com.kingdom.model.players.Player
import com.kingdom.util.KingdomUtil
import java.util.*

abstract class Card(
        val name: String,
        val deck: Deck,
        val type: CardType,
        val cost: Int,
        var special: String = "",
        var addActions: Int = 0,
        var addCoins: Int = 0,
        var addCards: Int = 0,
        var addBuys: Int = 0,
        var victoryPoints: Int = 0,
        var testing: Boolean = false,
        var addVictoryCoins: Int = 0,
        var playTreasureCards: Boolean = false,
        var disabled: Boolean = false,
        var fontSize: Int = 0,
        var nameLines: Int = 1,
        var textSize: Int = 0,
        var coinsTokens: Int = 0,
        val associatedCards: MutableList<Card> = ArrayList(0),
        var isDisableSelect: Boolean = false,
        var isAutoSelect: Boolean = false,
        var isActivated: Boolean = false,
        var isCopied: Boolean = false,
        var location: CardLocation? = null) {

    val id: String = UUID.randomUUID().toString()

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

    val isPromo: Boolean
        get() = deck === Deck.Promo

    val isProsperity: Boolean
        get() = deck === Deck.Prosperity

    val isCornucopia: Boolean
        get() = deck === Deck.Cornucopia

    val isHinterlands: Boolean
        get() = deck === Deck.Hinterlands

    val backgroundColor: CardColor
        get() = when {
            type == CardType.ActionVictory -> CardColor.ActionVictory
            type == CardType.TreasureVictory -> CardColor.TreasureVictory
            type == CardType.TreasureCurse -> CardColor.TreasureCurse
            type == CardType.VictoryReaction -> CardColor.VictoryReaction
            type == CardType.DurationVictory -> CardColor.DurationVictory
            type == CardType.TreasureReaction -> CardColor.TreasureReaction
            isTreasure -> CardColor.Treasure
            isVictory -> CardColor.Victory
            type == CardType.ActionReaction -> CardColor.ActionReaction
            type == CardType.Curse -> CardColor.Curse
            type == CardType.ActionDuration -> CardColor.ActionDuration
            else -> CardColor.Action
        }

    val backgroundColorColor: String
        get() = backgroundColor.color

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

    open fun removedFromPlay(player: Player) {}

    fun cardPlayed(player: Player) {
        player.actions += addActions
        player.buys += addBuys
        player.coins += addCoins
        if (addCards > 0) {
            player.drawCards(addCards)
        }
        if (special.isNotBlank()) {
            cardPlayedSpecialAction(player)
        }
    }

    open fun cardPlayedSpecialAction(player: Player) {
    }

    open fun isActionable(player: Player, cardLocation: CardLocation): Boolean {
        return player.isYourTurn && cardLocation == CardLocation.Hand
    }
}
