package com.kingdom.model.cards

import com.kingdom.model.cards.kingdom.ThroneRoom
import com.kingdom.model.cards.listeners.BeforeOpponentCardPlayedListener
import com.kingdom.model.cards.prosperity.Goons
import com.kingdom.model.cards.prosperity.KingsCourt
import com.kingdom.model.cards.supply.*
import com.kingdom.model.players.Player
import com.kingdom.util.KingdomUtil
import com.kingdom.util.plural
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
        var isPlayTreasureCardsRequired: Boolean = false,
        var isTreasureExcludedFromAutoPlay: Boolean = false,
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
        var isDefense: Boolean = false,
        var isTrashingCard: Boolean = false,
        var isTrashingFromHandRequiredCard: Boolean = false,
        var isTrashingFromHandToUpgradeCard: Boolean = false,
        var playersExcludedFromCardEffects: MutableSet<Player> = mutableSetOf()) {

    var isHighlighted: Boolean = false

    var isSelected: Boolean = false

    val id: String = UUID.randomUUID().toString()

    @Suppress("MemberVisibilityCanBePrivate")
    val typeAsString: String
        get() = when (type) {
            CardType.Action -> "Action"
            CardType.ActionAttack -> "Action - Attack"
            CardType.ActionAttackLooter -> "Action - Attack - Looter"
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
            CardType.ActionRuins -> "Action - Ruins"
            CardType.ActionShelter -> "Action - Shelter"
            CardType.ReactionShelter -> "Reaction - Shelter"
            CardType.VictoryShelter -> "Victory - Shelter"
            CardType.ActionLooter -> "Action - Looter"
        }

    val numTypes: Int
        get() = when (type) {
            CardType.Action -> 1
            CardType.ActionAttack -> 2
            CardType.ActionAttackLooter -> 3
            CardType.ActionReaction -> 2
            CardType.Victory -> 1
            CardType.Curse -> 1
            CardType.Treasure -> 1
            CardType.ActionVictory -> 2
            CardType.TreasureVictory -> 2
            CardType.ActionDuration -> 2
            CardType.VictoryReaction -> 2
            CardType.TreasureCurse -> 2
            CardType.DurationVictory -> 2
            CardType.TreasureReaction -> 2
            CardType.ActionRuins -> 2
            CardType.ActionShelter -> 2
            CardType.ReactionShelter -> 2
            CardType.VictoryShelter -> 2
            CardType.ActionLooter -> 2
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
                sb.append(getAmountSymbol(addCoins)).append("coin".plural(addCoins)).append(". ")
            }
            if (addCards != 0) {
                sb.append(getAmountSymbol(addCards)).append("card".plural(addCards)).append(". ")
            }
            if (addActions != 0) {
                sb.append(getAmountSymbol(addActions)).append("action".plural(addActions)).append(". ")
            }
            if (addBuys != 0) {
                sb.append(getAmountSymbol(addBuys)).append("buy".plural(addBuys)).append(". ")
            }
            if (addVictoryCoins != 0) {
                sb.append(getAmountSymbol(addVictoryCoins)).append("victory coin".plural(addVictoryCoins)).append(". ")
            }
            sb.append(special)
            return sb.toString()
        }

    val isVictoryOnly: Boolean
        get() = type == CardType.Victory

    val isVictory: Boolean
        get() = type == CardType.Victory || type == CardType.ActionVictory || type == CardType.TreasureVictory || type == CardType.VictoryReaction || type == CardType.DurationVictory || type == CardType.VictoryShelter

    val isVictoryReaction: Boolean
        get() = type == CardType.VictoryReaction

    val isAction: Boolean
        get() = type == CardType.Action || type == CardType.ActionAttack || type == CardType.ActionReaction || type == CardType.ActionVictory || type == CardType.ActionDuration || type == CardType.DurationVictory || type == CardType.ActionShelter || type == CardType.ActionLooter || type == CardType.ActionAttackLooter

    open val isTerminalAction: Boolean
        get() = isAction && addActions == 0

    val isDuration: Boolean
        get() = type == CardType.ActionDuration || type == CardType.DurationVictory

    val isTreasure: Boolean
        get() = type == CardType.Treasure || type == CardType.TreasureVictory || type == CardType.TreasureCurse || type == CardType.TreasureReaction

    val isReaction: Boolean
        get() = type == CardType.ActionReaction || type == CardType.VictoryReaction || type == CardType.TreasureReaction || type == CardType.ReactionShelter

    val isCurse: Boolean
        get() = type == CardType.Curse || type == CardType.TreasureCurse

    val isRuins: Boolean
        get() = type == CardType.ActionRuins

    val isShelter: Boolean
        get() = type == CardType.ActionShelter || type == CardType.ReactionShelter || type == CardType.VictoryShelter

    val isLooter: Boolean
        get() = type == CardType.ActionLooter || type == CardType.ActionAttackLooter

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
        get() = type == CardType.ActionAttack || type == CardType.ActionAttackLooter

    val backgroundColor: CardColor
        get() = when {
            type == CardType.ActionVictory -> CardColor.ActionVictory
            type == CardType.TreasureVictory -> CardColor.TreasureVictory
            type == CardType.TreasureCurse -> CardColor.TreasureCurse
            type == CardType.VictoryReaction -> CardColor.VictoryReaction
            type == CardType.DurationVictory -> CardColor.DurationVictory
            type == CardType.TreasureReaction -> CardColor.TreasureReaction
            type == CardType.ActionReaction -> CardColor.ActionReaction
            type == CardType.Curse -> CardColor.Curse
            type == CardType.ActionDuration -> CardColor.ActionDuration
            type == CardType.ActionRuins -> CardColor.Ruins
            type == CardType.ActionShelter -> CardColor.ActionShelter
            type == CardType.ReactionShelter -> CardColor.ReactionShelter
            type == CardType.VictoryShelter -> CardColor.VictoryShelter
            isTreasure -> CardColor.Treasure
            isVictory -> CardColor.Victory
            else -> CardColor.Action
        }

    val backgroundColorColor: String
        get() = backgroundColor.color

    val backgroundColorMobileColor: String
        get() = backgroundColor.mobileColor

    val isExtraActionsCard: Boolean
        get() = addActions >= 2 || name == ThroneRoom.NAME || name == KingsCourt.NAME

    val isVictoryCoinsCard: Boolean
        get() = addVictoryCoins > 0 || name == Goons.NAME

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

    val cardNameWithBackgroundColor
        get() = KingdomUtil.getWordWithBackgroundColor(name, backgroundColor)

    val cardNameWithArticleAndBackgroundColor
        get() = KingdomUtil.getWordWithBackgroundColor(nameWithArticle(), backgroundColor)

    var isCardActuallyBandOfMisfits: Boolean = false

    var adjustedCost: Int = cost

    fun getNumberPlusNameWithBackgroundColor(num: Int): String {
        return KingdomUtil.getWordWithBackgroundColor(name.plural(num), backgroundColor)
    }

    private fun nameWithArticle(): String {
        if (name == "Goons" || name == "Nobles") {
            return cardNameWithBackgroundColor
        }
        if (name == "University") {
            return "a $cardNameWithBackgroundColor"
        }
        return when (name.toUpperCase().first()) {
            'A', 'E', 'I', 'O', 'U' -> "an $cardNameWithBackgroundColor"
            else -> "a $cardNameWithBackgroundColor"
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
        return id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(name)
    }

    open fun removedFromPlay(player: Player) {
        playersExcludedFromCardEffects.clear()
        isSelected = false
        isHighlighted = false
    }

    open fun beforeCardRepeated(player: Player) {
        removedFromPlay(player)
    }

    fun cardPlayed(player: Player) {
        if (isAction) {
            player.addActions(-1)
        }

        player.addActions(addActions)
        player.addBuys(addBuys)
        player.addCoins(addCoins)
        player.addVictoryCoins(addVictoryCoins)

        if (addCards > 0) {
            player.drawCards(addCards)
        }

        if (special.isNotBlank()) {
            player.opponentsInOrder.forEach { opponent ->
                opponent.hand.filter { it is BeforeOpponentCardPlayedListener }
                        .forEach { (it as BeforeOpponentCardPlayedListener).onBeforeOpponentCardPlayed(this, opponent, player) }
            }

            cardPlayedSpecialAction(player)

            if (player.isOpponentHasAction) {
                player.waitForOtherPlayersToResolveActions()
            }
        }
    }

    open fun cardPlayedSpecialAction(player: Player) {
    }

    open fun isActionable(player: Player, cardLocation: CardLocation): Boolean {
        if (!player.isYourTurn) {
            return false
        }

        return when (cardLocation) {
            CardLocation.Hand -> isHandCardActionable(player)
            CardLocation.Supply -> isSupplyCardActionable(player)
            else -> false
        }
    }

    private fun isHandCardActionable(player: Player): Boolean {
        return when {
            isAction -> player.actions > 0 && !player.isTreasureCardsPlayed && !player.isCardsBought
            isTreasure -> player.isPlayTreasureCards && !player.isCardsBought
            else -> false
        }
    }

    private fun isSupplyCardActionable(player: Player): Boolean {
        return player.buys > 0 && player.canBuyCard(this)
    }
}
