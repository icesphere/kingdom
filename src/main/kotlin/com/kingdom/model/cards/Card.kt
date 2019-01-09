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
        var addCoffers: Int = 0,
        val associatedCards: MutableList<Card> = ArrayList(0),
        var isDisableSelect: Boolean = false,
        var isAutoSelect: Boolean = false,
        var isActivated: Boolean = false,
        var isDefense: Boolean = false,
        var isTrashingCard: Boolean = false,
        var isTrashingFromHandRequiredCard: Boolean = false,
        var isTrashingFromHandToUpgradeCard: Boolean = false,
        var isOverpayForCardAllowed: Boolean = false,
        var playersExcludedFromCardEffects: MutableSet<Player> = mutableSetOf()) {

    var isHighlighted: Boolean = false

    var isSelected: Boolean = false

    val id: String = UUID.randomUUID().toString()

    @Suppress("MemberVisibilityCanBePrivate")
    val typeAsString: String
        get() = when (type) {
            CardType.Action -> "Action"
            CardType.ActionAttack -> "Action - Attack"
            CardType.ActionAttackVictory -> "Action - Attack - Victory"
            CardType.ActionAttackDuration -> "Action - Attack - Duration"
            CardType.ActionAttackDurationVictory -> "Action - Attack - Duration - Victory"
            CardType.ActionAttackLooter -> "Action - Attack - Looter"
            CardType.ActionAttackLooterVictory -> "Action - Attack - Looter - Victory"
            CardType.ActionReaction -> "Action - Reaction"
            CardType.ActionReactionVictory -> "Action - Reaction - Victory"
            CardType.Victory -> "Victory"
            CardType.Curse -> "Curse"
            CardType.Treasure -> "Treasure"
            CardType.TreasureAttack -> "Treasure - Attack"
            CardType.ActionVictory -> "Action - Victory"
            CardType.TreasureVictory -> "Treasure - Victory"
            CardType.ActionDuration -> "Action - Duration"
            CardType.ActionDurationVictory -> "Action - Duration - Victory"
            CardType.VictoryReaction -> "Victory - Reaction"
            CardType.TreasureCurse -> "Treasure - Curse"
            CardType.DurationVictory -> "Duration - Victory"
            CardType.TreasureReaction -> "Treasure - Reaction"
            CardType.ActionRuins -> "Action - Ruins"
            CardType.ActionRuinsVictory -> "Action - Ruins - Victory"
            CardType.ActionShelter -> "Action - Shelter"
            CardType.ActionShelterVictory -> "Action - Shelter - Victory"
            CardType.ReactionShelter -> "Reaction - Shelter"
            CardType.ReactionShelterVictory -> "Reaction - Shelter - Victory"
            CardType.VictoryShelter -> "Victory - Shelter"
            CardType.ActionLooter -> "Action - Looter"
            CardType.ActionLooterVictory -> "Action - Looter - Victory"
            CardType.TreasureReserve -> "Treasure - Reserve"
            CardType.ActionReserve -> "Action - Reserve"
            CardType.ActionReserveVictory -> "Action - Reserve - Victory"
            CardType.ActionDurationReaction -> "Action - Duration - Reaction"
            CardType.ActionDurationReactionVictory -> "Action - Duration - Reaction - Victory"
            CardType.ActionTraveller -> "Action - Traveller"
            CardType.ActionTravellerVictory -> "Action - Traveller - Victory"
            CardType.ActionAttackTraveller -> "Action - Attack - Traveller"
            CardType.ActionAttackTravellerVictory -> "Action - Attack - Traveller - Victory"
            CardType.Event -> "Event"
        }

    val numTypes: Int
        get() = when (type) {
            CardType.Action -> 1
            CardType.ActionAttack -> 2
            CardType.ActionAttackVictory -> 3
            CardType.ActionAttackLooter -> 3
            CardType.ActionAttackLooterVictory -> 4
            CardType.ActionReaction -> 2
            CardType.ActionReactionVictory -> 3
            CardType.Victory -> 1
            CardType.Curse -> 1
            CardType.Treasure -> 1
            CardType.TreasureAttack -> 2
            CardType.ActionVictory -> 2
            CardType.TreasureVictory -> 2
            CardType.ActionDuration -> 2
            CardType.ActionDurationVictory -> 3
            CardType.VictoryReaction -> 2
            CardType.TreasureCurse -> 2
            CardType.DurationVictory -> 2
            CardType.TreasureReaction -> 2
            CardType.ActionRuins -> 2
            CardType.ActionRuinsVictory -> 3
            CardType.ActionShelter -> 2
            CardType.ActionShelterVictory -> 3
            CardType.ReactionShelter -> 2
            CardType.ReactionShelterVictory -> 3
            CardType.VictoryShelter -> 2
            CardType.ActionLooter -> 2
            CardType.ActionLooterVictory -> 3
            CardType.TreasureReserve -> 2
            CardType.ActionReserve -> 2
            CardType.ActionReserveVictory -> 3
            CardType.ActionDurationReaction -> 3
            CardType.ActionDurationReactionVictory -> 4
            CardType.ActionAttackDuration -> 3
            CardType.ActionAttackDurationVictory -> 4
            CardType.ActionTraveller -> 2
            CardType.ActionTravellerVictory -> 3
            CardType.ActionAttackTraveller -> 3
            CardType.ActionAttackTravellerVictory -> 4
            CardType.Event -> 1
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
            if (addBuys != 0) {
                sb.append(getAmountSymbol(addCoffers)).append("coffer".plural(addCoffers)).append(". ")
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
        get() = type == CardType.Victory || type == CardType.ActionVictory || type == CardType.TreasureVictory || type == CardType.VictoryReaction || type == CardType.DurationVictory || type == CardType.VictoryShelter || type == CardType.ActionReserveVictory || type == CardType.ActionAttackVictory || type == CardType.ActionAttackDurationVictory || type == CardType.ActionAttackLooterVictory || type == CardType.ActionReactionVictory || type == CardType.ActionDurationVictory || type == CardType.ActionRuinsVictory || type == CardType.ActionShelterVictory || type == CardType.ReactionShelterVictory || type == CardType.ActionLooterVictory || type == CardType.ActionDurationReactionVictory || type == CardType.ActionTravellerVictory || type == CardType.ActionAttackTravellerVictory

    val isVictoryReaction: Boolean
        get() = type == CardType.VictoryReaction

    val isAction: Boolean
        get() = type == CardType.Action || type == CardType.ActionAttack || type == CardType.ActionAttackVictory || type == CardType.ActionAttackDuration || type == CardType.ActionAttackDurationVictory || type == CardType.ActionReaction || type == CardType.ActionReactionVictory || type == CardType.ActionVictory || type == CardType.ActionDuration || type == CardType.ActionDurationVictory || type == CardType.DurationVictory || type == CardType.ActionRuins || type == CardType.ActionRuinsVictory || type == CardType.ActionShelter || type == CardType.ActionShelterVictory || type == CardType.ActionLooter || type == CardType.ActionLooterVictory || type == CardType.ActionAttackLooter || type == CardType.ActionAttackLooterVictory || type == CardType.ActionReserve || type == CardType.ActionReserveVictory || type == CardType.ActionDurationReaction || type == CardType.ActionDurationReactionVictory || type == CardType.ActionTraveller || type == CardType.ActionTravellerVictory || type == CardType.ActionAttackTraveller || type == CardType.ActionAttackTravellerVictory

    open val isTerminalAction: Boolean
        get() = isAction && addActions == 0

    val isDuration: Boolean
        get() = type == CardType.ActionDuration || type == CardType.ActionDurationVictory || type == CardType.DurationVictory || type == CardType.ActionDurationReaction || type == CardType.ActionDurationReactionVictory || type == CardType.ActionAttackDuration || type == CardType.ActionAttackDurationVictory

    val isTreasure: Boolean
        get() = type == CardType.Treasure || type == CardType.TreasureAttack || type == CardType.TreasureVictory || type == CardType.TreasureCurse || type == CardType.TreasureReaction || type == CardType.TreasureReserve

    val isReaction: Boolean
        get() = type == CardType.ActionReaction || type == CardType.ActionReactionVictory || type == CardType.VictoryReaction || type == CardType.TreasureReaction || type == CardType.ReactionShelter || type == CardType.ReactionShelterVictory || type == CardType.ActionDurationReaction || type == CardType.ActionDurationReactionVictory

    val isCurse: Boolean
        get() = type == CardType.Curse || type == CardType.TreasureCurse

    val isRuins: Boolean
        get() = type == CardType.ActionRuins || type == CardType.ActionRuinsVictory

    val isShelter: Boolean
        get() = type == CardType.ActionShelter || type == CardType.ActionShelterVictory || type == CardType.ReactionShelter || type == CardType.ReactionShelterVictory || type == CardType.VictoryShelter

    val isLooter: Boolean
        get() = type == CardType.ActionLooter || type == CardType.ActionLooterVictory || type == CardType.ActionAttackLooter || type == CardType.ActionAttackLooterVictory

    val isTraveller: Boolean
        get() = type == CardType.ActionTraveller || type == CardType.ActionAttackTraveller || type == CardType.ActionTravellerVictory || type == CardType.ActionAttackTravellerVictory

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
        get() = type == CardType.ActionAttack || type == CardType.ActionAttackVictory || type == CardType.ActionAttackDuration || type == CardType.ActionAttackDurationVictory || type == CardType.ActionAttackLooter || type == CardType.ActionAttackLooterVictory || type == CardType.TreasureAttack || type == CardType.ActionAttackTraveller || type == CardType.ActionAttackTravellerVictory

    val isEvent: Boolean
        get() = type == CardType.Event

    val backgroundColor: CardColor
        get() = when {
            type == CardType.ActionVictory -> CardColor.ActionVictory
            type == CardType.TreasureVictory -> CardColor.TreasureVictory
            type == CardType.TreasureCurse -> CardColor.TreasureCurse
            type == CardType.VictoryReaction -> CardColor.VictoryReaction
            type == CardType.DurationVictory -> CardColor.DurationVictory
            type == CardType.TreasureReaction -> CardColor.TreasureReaction
            type == CardType.ActionReaction -> CardColor.ActionReaction
            type == CardType.ActionReactionVictory -> CardColor.ActionReaction
            type == CardType.Curse -> CardColor.Curse
            type == CardType.ActionDuration -> CardColor.ActionDuration
            type == CardType.ActionDurationVictory -> CardColor.ActionDuration
            type == CardType.ActionAttackDuration -> CardColor.ActionDuration
            type == CardType.ActionAttackDurationVictory -> CardColor.ActionDuration
            type == CardType.ActionReserve -> CardColor.ActionReserve
            type == CardType.ActionRuins -> CardColor.Ruins
            type == CardType.ActionRuinsVictory -> CardColor.Ruins
            type == CardType.ActionShelter -> CardColor.ActionShelter
            type == CardType.ActionShelterVictory -> CardColor.ActionShelter
            type == CardType.ReactionShelter -> CardColor.ReactionShelter
            type == CardType.ReactionShelterVictory -> CardColor.ReactionShelter
            type == CardType.VictoryShelter -> CardColor.VictoryShelter
            type == CardType.ActionDurationReaction -> CardColor.ActionDurationReaction
            type == CardType.ActionDurationReactionVictory -> CardColor.ActionDurationReaction
            type == CardType.ActionReserveVictory -> CardColor.ActionReserveVictory
            type == CardType.TreasureReserve -> CardColor.TreasureReserve
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
        player.addCoffers(addCoffers)

        if (addCards > 0) {
            player.drawCards(addCards)
        }

        if (special.isNotBlank()) {
            if (this !is Event) {
                player.opponentsInOrder.forEach { opponent ->
                    opponent.hand.filter { it is BeforeOpponentCardPlayedListener }
                            .forEach { (it as BeforeOpponentCardPlayedListener).onBeforeOpponentCardPlayed(this, opponent, player) }
                }
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
            CardLocation.Event -> (this as Event).isEventActionable(player)
            else -> false
        }
    }

    private fun isHandCardActionable(player: Player): Boolean {
        return when {
            isAction -> player.actions > 0 && !player.isTreasureCardsPlayedInBuyPhase && !player.isCardsBought
            isTreasure -> player.isPlayTreasureCards && !player.isCardsBought
            else -> false
        }
    }

    private fun isSupplyCardActionable(player: Player): Boolean {
        return player.buys > 0 && player.canBuyCard(this)
    }
}
