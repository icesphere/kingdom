package com.kingdom.model.players.bots

import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Event
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.adventures.events.*
import com.kingdom.model.cards.base.*
import com.kingdom.model.cards.cornucopia.Fairgrounds
import com.kingdom.model.cards.cornucopia.HornOfPlenty
import com.kingdom.model.cards.darkages.Feodum
import com.kingdom.model.cards.empires.events.*
import com.kingdom.model.cards.empires.landmarks.*
import com.kingdom.model.cards.hinterlands.Trader
import com.kingdom.model.cards.hinterlands.SilkRoad
import com.kingdom.model.cards.intrigue.Duke
import com.kingdom.model.cards.intrigue.Minion
import com.kingdom.model.cards.intrigue.WishingWell
import com.kingdom.model.cards.menagerie.events.*
import com.kingdom.model.cards.prosperity.Expand
import com.kingdom.model.cards.prosperity.Goons
import com.kingdom.model.cards.prosperity.Monument
import com.kingdom.model.cards.prosperity.Mountebank
import com.kingdom.model.cards.prosperity.Quarry
import com.kingdom.model.cards.seaside.Bazaar
import com.kingdom.model.cards.seaside.Cutpurse
import com.kingdom.model.cards.supply.*

open class HardBotPlayer(user: User, game: Game) : MediumBotPlayer(user, game) {

    override val difficulty: Int = 3

    private val scorePerCoin = 4

    private enum class BuildPlan {
        Money,
        PayloadMoney,
        Engine,
        AlternativeScoring
    }

    private val preferredMaxCopies = mapOf(
            Witch.NAME to 2,
            Mountebank.NAME to 2,
            Goons.NAME to 3,
            Monument.NAME to 2,
            Militia.NAME to 2,
            Cutpurse.NAME to 2,
            Smithy.NAME to 1,
            CouncilRoom.NAME to 1,
            Bureaucrat.NAME to 1,
            Moneylender.NAME to 1,
            Minion.NAME to 6,
            Market.NAME to 4,
            Laboratory.NAME to 4,
            Bazaar.NAME to 3,
            Festival.NAME to 3
    )

    private val namedCardTieBreakers = mapOf(
            Witch.NAME to 3,
            Mountebank.NAME to 3,
            Goons.NAME to 3,
            Monument.NAME to 2,
            Militia.NAME to 2,
            Smithy.NAME to 2,
            CouncilRoom.NAME to 2,
            Cutpurse.NAME to 2,
            Minion.NAME to 2,
            Bureaucrat.NAME to 1,
            Market.NAME to 1,
            Laboratory.NAME to 1,
            Bazaar.NAME to 1,
            Festival.NAME to 1,
            Moneylender.NAME to 1
    )

    private val provinceCardsLeft: Int
        get() = game.numInPileMap[Province.NAME] ?: 0

    private val colonyCardsLeft: Int
        get() = game.numInPileMap[Colony.NAME] ?: Int.MAX_VALUE

    private val hasChapelInSupply: Boolean
        get() = game.allCards.any { it.name == Chapel.NAME }

    private val hasLaboratoryInSupply: Boolean
        get() = game.allCards.any { it.name == Laboratory.NAME }

    private val hasGoonsInSupply: Boolean
        get() = game.allCards.any { it.name == Goons.NAME }

    private val goonsInPlay: Int
        get() = inPlay.count { it.name == Goons.NAME }

    private val hasAlternativeScoringAvailable: Boolean
        get() = game.allCards.any { isAlternativeScoringCard(it) || it.isVictoryCoinsCard }

    override fun getBuyEventScore(event: Event): Int {
        if (!event.isEventActionable(this)) {
            return 0
        }

        val eventValue = hardEventValue(event)
        if (eventValue <= 0) {
            return 0
        }

        val bestCardWithoutEvent = bestCardBuyScore(availableCoins)
        val remainingBuys = projectedBuysAfterEvent(event)
        val remainingCoins = projectedCoinsAfterEvent(event)
        val followUpCardScore = if (remainingBuys > 0 && remainingCoins >= 0) bestCardBuyScore(remainingCoins) else 0
        val totalTurnValue = eventValue + followUpCardScore
        val margin = if (event.isMostlyEnablingEvent()) 2 else 0

        return if (bestCardWithoutEvent == 0 || totalTurnValue >= bestCardWithoutEvent + margin) {
            totalTurnValue
        } else {
            0
        }
    }

    override fun getCardToBuy(): String? {
        if (onlyBuyVictoryCards) {
            return bestVictoryCardToBuy()?.name
        }

        val candidates = game.allCards.filter { canBuyCard(it) && !excludeCard(it) }
        if (candidates.isEmpty()) {
            return null
        }

        return candidates.maxWithOrNull(
                compareBy<Card> { getBuyCardScore(it) }
                        .thenBy { getCardCostWithModifiers(it) }
                        .thenBy { -cardCountByName(it.name) }
        )?.takeIf { getBuyCardScore(it) > 0 }?.name
    }

    override fun getBuyCardScore(card: Card): Int {
        if (excludeCard(card)) {
            return 0
        }

        when (card.name) {
            Colony.NAME -> return scoreWithInheritedAdjustments(44, card)
            Platinum.NAME -> return scoreWithInheritedAdjustments(38, card)
            Province.NAME -> return scoreWithInheritedAdjustments(34, card)
            Duchy.NAME -> return scoreWithInheritedAdjustments(if (shouldBuyDuchy() || shouldBuyVictoryForLandmark(card)) 30 else 0, card)
            Estate.NAME -> return scoreWithInheritedAdjustments(if (shouldBuyEstate() || shouldBuyVictoryForLandmark(card)) 27 else 0, card)
            Gold.NAME -> return scoreWithInheritedAdjustments(when {
                shouldPreferPayloadOverTreasure() -> 18
                cardCountByName(Gold.NAME) == 0 -> 26
                else -> 24
            }, card)
            Silver.NAME -> return scoreWithInheritedAdjustments(if (cardCountByName(Silver.NAME) < 2 || turns < 5) 10 else 7, card)
            Copper.NAME -> return maxOf(scoreWithInheritedAdjustments(if (goonsInPlay > 0) 3 + goonsInPlay else 0, card), inheritedScore(card))
            Chapel.NAME -> return if (shouldBuyChapel()) 28 else 0
        }

        val cap = preferredMaxCopies[card.name]
        if (cap != null && cardCountByName(card.name) >= cap) {
            return 0
        }

        var score = maxOf(super.getBuyCardScore(card), kingdomPayloadScoreFloor(card))

        if (card.isVictoryPointsCalculator && card.isVictory) {
            val victoryPoints = victoryPointsFromGain(card)
            score = maxOf(score, victoryPoints * 4)
        }

        if (card.isAlternativeScoringCardForBot()) {
            score = maxOf(score, victoryPointsFromGain(card) * 6 + if (isPilePressureLikely()) 3 else 0)
        }

        if (card.isAttack && turns < 7) {
            score += 3
        }

        if (card.addCards > 1 && !card.isTerminalAction) {
            score += 5
        } else if (card.addCards > 1) {
            score += 2
        }

        if (card.addActions > 1 && terminalActionCount() > extraActionCount()) {
            score += 4
        }

        if (card.addBuys > 0 && (goonsInPlay > 0 || availableCoins >= 6)) {
            score += 2
        }

        return score +
                generalizedKingdomAnalysisAdjustment(card) +
                strategicPlanAdjustment(card) +
                landmarkStrategicAdjustment(card) +
                namedCardTieBreaker(card)
    }

    override fun excludeCard(card: Card): Boolean {
        if (card.isCurseOnly || buyingCardWouldEndGameWhileBehind(card)) {
            return true
        }

        if (onlyBuyVictoryCards && !card.isVictory) {
            return true
        }

        when (card.name) {
            Chapel.NAME -> return !shouldBuyChapel()
            Duchy.NAME -> return !shouldBuyDuchy() && !shouldBuyVictoryForLandmark(card)
            Estate.NAME -> return !shouldBuyEstate() && !shouldBuyVictoryForLandmark(card)
            Copper.NAME -> return goonsInPlay == 0 && super.excludeCard(card)
        }

        if (card.isAlternativeScoringCardForBot() && shouldPursueAlternativeScoring(card)) {
            return false
        }

        if (isPreferredCard(card)) {
            return excludePreferredCard(card)
        }

        if (super.excludeCard(card)) {
            return true
        }

        return when {
            //todo game.buyingCardWillEndGame(card.name) && !game.currentlyWinning(userId) -> return true
            card.name == Mine.NAME
                    || card.name == WishingWell.NAME || card.name == Workshop.NAME
                    || card.name == HornOfPlenty.NAME || card.name == Quarry.NAME
                    || card.name == Trader.NAME -> true
            turns < 2 && card.name == Village.NAME -> true
            card.isTerminalAction && !hasTerminalRoomFor(card) -> true
            card.isAction && !card.isVictory && cardCountByExpression { it.isAction } >= maxActionCardsToOwn() -> true
            card.name == Expand.NAME && cardCountByName(card.name) > 0 -> true
            else -> false
        }

    }

    override fun getChoice(choiceActionCard: ChoiceActionCard, choices: Array<Choice>, info: Any?): Int {
        if (choiceActionCard.name == Minion.NAME) {
            return chooseMinionMode()
        }

        return super.getChoice(choiceActionCard, choices, info)
    }

    override fun optionallyTrashCardsFromHand(numCardsToTrash: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)?) {
        chooseOptionalTrashCards(numCardsToTrash, cardActionableExpression)
                .forEach { trashCardFromHand(it) }
    }

    override fun optionallyTrashCardsFromHandForBenefit(
            card: TrashCardsForBenefitActionCard,
            numCardsToTrash: Int,
            text: String,
            cardActionableExpression: ((card: Card) -> Boolean)?,
            info: Any?
    ) {
        val cards = chooseOptionalTrashCards(numCardsToTrash, cardActionableExpression)

        cards.forEach { trashCardFromHand(it) }

        card.cardsTrashed(this, cards, info)
    }

    private fun bestVictoryCardToBuy(): Card? {
        return game.allCards
                .filter { canBuyCard(it) && it.isVictory && !buyingCardWouldEndGameWhileBehind(it) }
                .maxByOrNull {
                    if (it is VictoryPointsCalculator) {
                        victoryPointsFromGain(it)
                    } else {
                        it.victoryPoints
                    }
                }
    }

    private fun scoreWithInheritedAdjustments(baseScore: Int, card: Card): Int {
        if (baseScore <= 0) {
            return 0
        }

        return baseScore + inheritedScoreAdjustment(card) + landmarkStrategicAdjustment(card)
    }

    private fun inheritedScore(card: Card): Int {
        return super.getBuyCardScore(card).coerceAtLeast(0)
    }

    private fun inheritedScoreAdjustment(card: Card): Int {
        val inheritedScore = inheritedScore(card)
        if (inheritedScore <= 0) {
            return 0
        }

        return inheritedScore - getCardCostWithModifiers(card)
    }

    private fun namedCardTieBreaker(card: Card): Int {
        return namedCardTieBreakers[card.name] ?: 0
    }

    private fun kingdomPayloadScoreFloor(card: Card): Int {
        if (!card.isKingdomPayloadCardForBot() || card.isVictoryOnly) {
            return 0
        }

        val cost = getCardCostWithModifiers(card)
        if (cost < 4) {
            return 0
        }

        return cost * scorePerCoin
    }

    private fun hardEventValue(event: Event): Int {
        val inheritedValue = super.getBuyEventScore(event).coerceIn(0, 18)
        val eventValue = when (event.name) {
            Advance.NAME -> advanceEventValue()
            Alliance.NAME -> allianceEventValue()
            Alms.NAME -> bestGainScore(4)
            Annex.NAME -> if (shouldBuyDuchy() && game.isCardAvailableInSupply(Duchy())) getBuyCardScore(Duchy()) + minOf(cardsInDiscard.size, 5) else 0
            Ball.NAME -> (twoBestGainScores(4).sum() - if (needsThinning()) 4 else 8).coerceAtLeast(0)
            Banquet.NAME -> (bestGainScore(5) { !it.isVictory } - if (needsThinning()) 8 else 4).coerceAtLeast(0)
            Bargain.NAME -> (bestGainScore(5) { !it.isVictory } - 4).coerceAtLeast(0)
            Bonfire.NAME -> inPlay.count { getTrashCardScore(it) >= 90 } * 9
            Borrow.NAME -> borrowEventValue()
            Commerce.NAME -> currentTurnSummary.cardsGained.distinctBy { it.name }.size * getBuyCardScore(Gold())
            Conquest.NAME -> 14 + currentTurnSummary.cardsGained.count { it.isSilver } * 4
            Delay.NAME -> hand.filter { it.isAction }.maxOfOrNull { getPlayCardScore(it).coerceAtLeast(0) } ?: 0
            Delve.NAME -> silverGainValue()
            Demand.NAME -> bestGainScore(4) + 8
            Desperation.NAME -> desperationEventValue()
            Dominate.NAME -> if (game.isCardAvailableInSupply(Province())) getBuyCardScore(Province()) + 9 * scorePerCoin else 0
            Donate.NAME -> donateEventValue()
            Enhance.NAME -> enhanceEventValue()
            Expedition.NAME -> if (currentBuildPlan() == BuildPlan.Engine || deckControlCardCount() > 0) 12 else 8
            Ferry.NAME -> actionTokenEventValue(minusTwoCostTokenSupplyPile) { card ->
                if (getCardCostWithModifiers(card) >= 4) 18 else 8
            }
            Gamble.NAME -> gambleEventValue()
            Inheritance.NAME -> inheritanceEventValue()
            LostArts.NAME -> actionTokenEventValue(plusActionTokenSupplyPile) { card ->
                when {
                    card.isTerminalAction -> 20
                    terminalActionCount() > extraActionCount() -> 14
                    else -> 6
                }
            }
            March.NAME -> cardsInDiscard.filter { it.isAction }.maxOfOrNull {
                getPlayCardScore(it).coerceAtLeast(getBuyCardScore(it) / 2)
            } ?: 0
            Pathfinding.NAME -> actionTokenEventValue(plusCardTokenSupplyPile) { card ->
                card.drawSupportStrength() * 5 + if (card.isTerminalAction) 4 else 0
            }
            Pilgrimage.NAME -> pilgrimageEventValue()
            Plan.NAME -> if (trashingTokenSupplyPile == null && needsThinning()) actionTokenEventValue(trashingTokenSupplyPile) { 16 } else 0
            Populate.NAME -> populateEventValue()
            Pursue.NAME -> if (deck.any { cardCountByName(it.name) >= 3 }) 8 else 4
            Quest.NAME -> questEventValue()
            Raid.NAME -> inPlay.count { it.isSilver } * silverGainValue() + 6
            Ride.NAME -> 8
            SaltTheEarth.NAME -> if (shouldBuyDuchy() || shouldBuyEstate() || isPilePressureLikely()) 12 else 0
            Save.NAME -> if (hand.any { getBuyCardScore(it) >= 20 }) 7 else 4
            ScoutingParty.NAME -> if (deck.size + cardsInDiscard.size >= 5) 8 else 4
            Seaway.NAME -> bestGainScore(4) { it.isAction } + 8
            Stampede.NAME -> 30
            TaintedVictory.NAME -> taintedVictoryEventValue()
            Toil.NAME -> hand.filter { it.isAction }.maxOfOrNull { getPlayCardScore(it).coerceAtLeast(0) } ?: 0
            Trade.NAME -> tradeEventValue()
            Training.NAME -> actionTokenEventValue(plusCoinTokenSupplyPile) { card ->
                if (card.isStrongPayloadCard()) 16 else 10
            }
            TravellingFair.NAME -> if (buys > 1 || currentTurnSummary.cardsGained.isNotEmpty() || isPilePressureLikely()) 10 else 6
            Triumph.NAME -> if (currentTurnSummary.cardsGained.isNotEmpty()) 4 + currentTurnSummary.cardsGained.size * scorePerCoin else 0
            Wedding.NAME -> if (game.isCardAvailableInSupply(Gold())) getBuyCardScore(Gold()) + scorePerCoin - 8 else 0
            Windfall.NAME -> if (game.isCardAvailableInSupply(Gold())) 42 else 0
            else -> 0
        }

        return maxOf(inheritedValue, eventValue)
    }

    private fun bestCardBuyScore(coins: Int): Int {
        if (coins < 0) {
            return 0
        }

        return game.allCards
                .filter { canBuyCardWithCoins(it, coins) && !excludeCard(it) }
                .maxOfOrNull { getBuyCardScore(it) }
                ?: 0
    }

    private fun canBuyCardWithCoins(card: Card, coins: Int): Boolean {
        return debt == 0 &&
                game.isCardAvailableInSupply(card) &&
                !game.isCardNotInSupply(card) &&
                getCardCostWithModifiers(card) <= coins
    }

    private fun bestGainScore(maxCost: Int, predicate: (Card) -> Boolean = { true }): Int {
        return gainCandidates(maxCost, predicate).maxOfOrNull { getBuyCardScore(it) } ?: 0
    }

    private fun twoBestGainScores(maxCost: Int, predicate: (Card) -> Boolean = { true }): List<Int> {
        return gainCandidates(maxCost, predicate)
                .map { getBuyCardScore(it) }
                .sortedDescending()
                .take(2)
    }

    private fun gainCandidates(maxCost: Int, predicate: (Card) -> Boolean): List<Card> {
        return game.availableCards
                .filter { it.debtCost == 0 && getCardCostWithModifiers(it) <= maxCost && predicate(it) && !excludeCard(it) }
    }

    private fun projectedBuysAfterEvent(event: Event): Int {
        return buys - 1 + event.addBuys + when (event.name) {
            Borrow.NAME, Desperation.NAME -> 1
            Save.NAME -> 1
            else -> 0
        }
    }

    private fun projectedCoinsAfterEvent(event: Event): Int {
        return availableCoins - event.cost - event.debtCost + event.addCoins + when (event.name) {
            Borrow.NAME -> 1
            Desperation.NAME -> 2
            else -> 0
        }
    }

    private fun Event.isMostlyEnablingEvent(): Boolean {
        return addBuys > 0 || name in setOf(
                Borrow.NAME,
                Desperation.NAME,
                Ferry.NAME,
                Gamble.NAME,
                LostArts.NAME,
                Pathfinding.NAME,
                Save.NAME,
                ScoutingParty.NAME,
                Plan.NAME,
                Training.NAME,
                TravellingFair.NAME
        )
    }

    private fun advanceEventValue(): Int {
        val trashedActionScore = hand
                .filter { it.isAction }
                .minOfOrNull { getBuyCardScore(it) }
                ?: return 0
        val gainedActionScore = bestGainScore(6) { it.isAction }

        return (gainedActionScore - trashedActionScore + 6).coerceAtLeast(0)
    }

    private fun allianceEventValue(): Int {
        if (!game.isCardAvailableInSupply(Province())) {
            return 0
        }

        val duchyScore = if (shouldBuyDuchy()) getBuyCardScore(Duchy()) else 12
        val estateScore = if (shouldBuyEstate()) getBuyCardScore(Estate()) else 4
        val victoryScore = getBuyCardScore(Province()) + duchyScore + estateScore
        val treasureScore = getBuyCardScore(Gold()) / 2 + getBuyCardScore(Silver()) / 2

        return victoryScore + treasureScore - if (needsThinning()) 8 else 4
    }

    private fun borrowEventValue(): Int {
        val bestBefore = bestCardBuyScore(availableCoins)
        val bestAfter = bestCardBuyScore(availableCoins + 1)

        return if (bestAfter >= bestBefore + 6) 4 else 0
    }

    private fun silverGainValue(): Int {
        if (!game.isCardAvailableInSupply(Silver())) {
            return 0
        }

        return getBuyCardScore(Silver())
    }

    private fun desperationEventValue(): Int {
        if ((game.numInPileMap[Curse.NAME] ?: 0) <= 3) {
            return 0
        }

        val bestBefore = bestCardBuyScore(availableCoins)
        val bestAfter = bestCardBuyScore(availableCoins + 2)

        return if (bestAfter >= bestBefore + 8) 4 else 0
    }

    private fun donateEventValue(): Int {
        val junkCards = allCards.count { it.isCurseOnly || it.isRuins || it.isEstate || it.isCopper }

        return when {
            junkCards >= 8 -> 42
            junkCards >= 6 -> 36
            junkCards >= 4 && turns < 5 -> 30
            cardCountByName(Curse.NAME) >= 2 -> 28
            else -> 0
        }
    }

    private fun enhanceEventValue(): Int {
        return hand
                .filter { !it.isVictory }
                .maxOfOrNull { trashedCard ->
                    bestGainScore(getCardCostWithModifiers(trashedCard) + 2) - getBuyCardScore(trashedCard)
                }
                ?.coerceAtLeast(0)
                ?: 0
    }

    private fun actionTokenEventValue(currentPile: String?, tokenValue: (Card) -> Int): Int {
        val bestTarget = game.availableCards
                .filter { it.isAction && !excludeCard(it) }
                .maxByOrNull { tokenValue(it) + getBuyCardScore(it) / 4 }
                ?: return 0
        val bestTargetValue = tokenValue(bestTarget) + getBuyCardScore(bestTarget) / 4
        val currentTargetValue = currentPile?.let { pile ->
            game.availableCards
                    .firstOrNull { it.pileName == pile }
                    ?.let { tokenValue(it) + getBuyCardScore(it) / 4 }
        } ?: 0

        return if (bestTargetValue >= currentTargetValue + 4) bestTargetValue else 0
    }

    private fun gambleEventValue(): Int {
        val deckCards = deck + cardsInDiscard
        if (deckCards.isEmpty()) {
            return 0
        }

        val playableDensity = deckCards.count { it.isAction || it.isTreasure }.toDouble() / deckCards.size
        return when {
            playableDensity >= 0.75 -> 12
            playableDensity >= 0.55 -> 8
            else -> 0
        }
    }

    private fun inheritanceEventValue(): Int {
        if (cardCountByName(Estate.NAME) == 0) {
            return 0
        }

        val estateTargetScore = bestGainScore(4) { it.isAction && !it.isVictory }
        return if (estateTargetScore >= 18) estateTargetScore + 8 else 0
    }

    private fun populateEventValue(): Int {
        val actionScores = game.availableCards
                .filter { it.isAction && !excludeCard(it) }
                .map { getBuyCardScore(it).coerceAtMost(28) }

        if (actionScores.size < 4) {
            return 0
        }

        return actionScores.sum() / 2
    }

    private fun pilgrimageEventValue(): Int {
        if (isJourneyTokenFaceUp) {
            return 0
        }

        return inPlay
                .distinctBy { it.name }
                .filter { game.isCardAvailableInSupply(it) }
                .map { getBuyCardScore(it) }
                .sortedDescending()
                .take(3)
                .sum() / 2
    }

    private fun questEventValue(): Int {
        return when {
            hand.count { it.isCurse } >= 2 -> getBuyCardScore(Gold()) + 4
            hand.any { it.isAttack && getBuyCardScore(it) <= 18 } -> getBuyCardScore(Gold())
            hand.size >= 6 && hand.count { getBuyCardScore(it) <= 10 } >= 3 -> 18
            else -> 0
        }
    }

    private fun taintedVictoryEventValue(): Int {
        val bestTrashForPoints = hand
                .filter { getBuyCardScore(it) <= 12 }
                .maxOfOrNull { getCardCostWithModifiers(it) }
                ?: return 0

        return if (bestTrashForPoints >= 3) bestTrashForPoints * scorePerCoin - 8 else 0
    }

    private fun tradeEventValue(): Int {
        val trashableCards = hand
                .filter { getTrashCardScore(it) >= 90 }
                .take(2)
                .size

        return trashableCards * (silverGainValue() + 6)
    }

    private fun landmarkStrategicAdjustment(card: Card): Int {
        if (game.landmarks.isEmpty()) {
            return 0
        }

        var adjustment = 0

        if (game.landmarks.any { it is Orchard } && card.isAction) {
            adjustment += when (cardCountByName(card.name)) {
                2 -> 4 * scorePerCoin
                1 -> 6
                0 -> if (currentBuildPlan() == BuildPlan.Engine) 2 else 0
                else -> 0
            }
        }

        if (game.landmarks.any { it is TriumphalArch } && card.isAction) {
            adjustment += triumphalArchPointDelta(card) * scorePerCoin
        }

        if (game.landmarks.any { it is Keep } && card.isTreasure) {
            adjustment += keepTreasureAdjustment(card)
        }

        if (game.landmarks.any { it is Palace } && card.isTreasure) {
            adjustment += palaceTreasureAdjustment(card)
        }

        if (game.landmarks.any { it is Museum } && allCards.none { it.name == card.name }) {
            adjustment += 2 * scorePerCoin
        }

        if (game.landmarks.any { it is Obelisk } && card.name == (game.landmarks.first { it is Obelisk } as Obelisk).chosenPile) {
            adjustment += 2 * scorePerCoin
        }

        if (game.landmarks.any { it is WolfDen }) {
            adjustment += when (cardCountByName(card.name)) {
                0 -> -3
                1 -> 10
                else -> 0
            }
        }

        if (game.landmarks.any { it is Tower } && !card.isVictory && !card.isCurseOnly) {
            val cardsLeft = game.numInPileMap[card.pileName] ?: Int.MAX_VALUE
            adjustment += when {
                cardsLeft <= 1 -> 2 * scorePerCoin
                cardsLeft <= 2 && cardCountByName(card.name) > 0 -> scorePerCoin
                else -> 0
            }
        }

        if (game.landmarks.any { it is Battlefield } && card.isVictory && game.getVictoryPointsOnSupplyPile(Battlefield.NAME) > 0) {
            adjustment += 2 * scorePerCoin
        }

        if (game.landmarks.any { it is Basilica } && availableCoins - getCardCostWithModifiers(card) >= 2 && game.getVictoryPointsOnSupplyPile(Basilica.NAME) > 0) {
            adjustment += 2 * scorePerCoin
        }

        if (game.landmarks.any { it is Colonnade } && card.isAction && inPlayWithDuration.any { it.name == card.name } && game.getVictoryPointsOnSupplyPile(Colonnade.NAME) > 0) {
            adjustment += 2 * scorePerCoin
        }

        if (game.landmarks.any { it is Labyrinth } && currentTurnSummary.cardsGained.size == 1 && game.getVictoryPointsOnSupplyPile(Labyrinth.NAME) > 0) {
            adjustment += 2 * scorePerCoin
        }

        if (game.landmarks.any { it is Aqueduct }) {
            if (card.isVictory) {
                adjustment += minOf(game.getVictoryPointsOnSupplyPile(Aqueduct.NAME), 8) * scorePerCoin
            } else if (card.isTreasure && game.getVictoryPointsOnSupplyPile(card.pileName) > 0) {
                adjustment += 2
            }
        }

        if (game.landmarks.any { it is BanditFort } && (card.isSilver || card.isGold)) {
            adjustment -= 6
        }

        return adjustment
    }

    private fun triumphalArchPointDelta(card: Card): Int {
        val before = triumphalArchPoints(allCards)
        val after = triumphalArchPoints(allCards + card)

        return after - before
    }

    private fun triumphalArchPoints(cards: List<Card>): Int {
        val actionCounts = cards
                .filter { it.isAction }
                .groupBy { it.name }
                .mapValues { it.value.size }

        if (actionCounts.size < 2) {
            return 0
        }

        return actionCounts.values.sortedDescending()[1] * 3
    }

    private fun keepTreasureAdjustment(card: Card): Int {
        val ownCount = cardCountByName(card.name)
        val opponentMax = opponents.maxOfOrNull { opponent -> opponent.allCards.count { it.name == card.name } } ?: 0

        return when {
            card.isCopper && ownCount >= opponentMax -> 0
            ownCount == 0 && opponentMax == 0 -> 12
            ownCount < opponentMax && ownCount + 1 >= opponentMax -> 12
            ownCount < opponentMax -> 4
            else -> 0
        }
    }

    private fun palaceTreasureAdjustment(card: Card): Int {
        val coppers = cardCountByName(Copper.NAME)
        val silvers = cardCountByName(Silver.NAME)
        val golds = cardCountByName(Gold.NAME)

        return when (card.name) {
            Copper.NAME -> if (coppers < minOf(silvers, golds)) 3 * scorePerCoin else 0
            Silver.NAME -> if (silvers < minOf(coppers, golds)) 3 * scorePerCoin else 0
            Gold.NAME -> if (golds < minOf(coppers, silvers)) 3 * scorePerCoin else 0
            else -> 0
        }
    }

    private fun generalizedKingdomAnalysisAdjustment(card: Card): Int {
        var adjustment = 0

        if (turns < 6) {
            if (card.isTrashingCard && needsThinning()) {
                adjustment += 9
            }

            if (card.isDeckControlCard()) {
                adjustment += 4
            }

            if (card.isReliabilityCard()) {
                adjustment += 2
            }

            if (card.addActions > 1 && terminalActionCount() > extraActionCount()) {
                adjustment += 4
            }
        }

        if (card.isCurseGiver) {
            adjustment += when {
                (game.numInPileMap[Curse.NAME] ?: 0) <= 3 -> -4
                turns < 6 -> 12
                turns < 9 -> 6
                else -> 0
            }
        }

        if (card.isVictoryCoinsCard) {
            adjustment += if (deckControlCardCount() > 0 || hasDeckControlAvailable()) 12 else 9
        }

        if (card.addBuys > 0 && hasAlternativeScoringAvailable) {
            adjustment += 3
        }

        if (deckControlCardCount() > 0 && (card.addCards > 1 || card.addBuys > 0 || card.isVictoryCoinsCard)) {
            adjustment += 3
        }

        if (card.drawSupportStrength() >= 3) {
            adjustment += 8
        } else if (card.drawSupportStrength() > 1) {
            adjustment += 5
        }

        if (card.addCards > 0 && card.addActions > 0 || card.isPlusBuySupportCard()) {
            adjustment += 5
        }

        if (isPilePressureLikely() && card.isVictory) {
            adjustment += 3
        }

        return adjustment
    }

    private fun strategicPlanAdjustment(card: Card): Int {
        val plan = currentBuildPlan()
        var adjustment = 0

        when (plan) {
            BuildPlan.AlternativeScoring -> {
                if (card.isAlternativeScoringCardForBot()) adjustment += 8
                if (card.isPlusBuySupportCard()) adjustment += 5
                if (card.isGainerCard()) adjustment += 4
                if (card.isDrawSupportCard()) adjustment += 3
                if (card.isTreasure && cardCountByExpression { it.isTreasure } >= 7) adjustment -= 2
            }
            BuildPlan.Engine -> {
                if (turns < 6 && card.isDeckControlCard()) adjustment += 6
                if (card.isDrawSupportCard()) adjustment += 5
                if (card.isVillageCard()) adjustment += 4
                if (card.isPlusBuySupportCard()) adjustment += 4
                if (card.isReliabilityCard()) adjustment += 3
                if (card.isTreasure && card.isGold && cardCountByName(Gold.NAME) > 0) adjustment -= 2
                if (card.isTerminalAction && !card.isStrongPayloadCard()) adjustment -= 4
            }
            BuildPlan.PayloadMoney -> {
                if (card.isStrongPayloadCard()) adjustment += 5
                if (card.isTreasure && card.isGold) adjustment += 2
                if (card.isAction && !card.isStrongPayloadCard() && !card.isDeckControlCard()) adjustment -= 4
            }
            BuildPlan.Money -> {
                if (card.isTreasure && card.isGold) adjustment += 4
                if (card.isTreasure && card.isSilver && cardCountByName(Silver.NAME) < 2) adjustment += 2
                if (card.isAction && !card.isStrongPayloadCard() && !card.isDeckControlCard()) adjustment -= 6
                if (card.isStrongPayloadCard()) adjustment += 3
            }
        }

        if (turns < 6 && card.isSilver && hasBetterOpeningBuildCardAvailable()) {
            adjustment -= 3
        }

        return adjustment
    }

    private fun currentBuildPlan(): BuildPlan {
        return when {
            hasUsableAlternativeScoringAvailable() -> BuildPlan.AlternativeScoring
            hasEngineSupportAvailable() -> BuildPlan.Engine
            hasStrongPayloadAvailable() -> BuildPlan.PayloadMoney
            else -> BuildPlan.Money
        }
    }

    private fun isPreferredCard(card: Card): Boolean {
        return preferredMaxCopies.containsKey(card.name)
    }

    private fun excludePreferredCard(card: Card): Boolean {
        val maxCopies = preferredMaxCopies.getValue(card.name)
        if (cardCountByName(card.name) >= maxCopies) {
            return true
        }

        if (card.isCurseGiver && (turns >= 9 || game.numInPileMap.getValue(Curse.NAME) <= 3)) {
            return true
        }

        if (card.isTerminalAction && !hasTerminalRoomFor(card)) {
            return true
        }

        return false
    }

    private fun shouldBuyChapel(): Boolean {
        if (!hasChapelInSupply || cardCountByName(Chapel.NAME) > 0 || availableCoins >= 5 || turns > 5) {
            return false
        }

        return allCards.count { it.isCurseOnly || it.isEstate || it.isCopper } >= 4
    }

    private fun shouldBuyDuchy(): Boolean {
        return provinceCardsLeft in 1..5 || game.isIncludeColonyCards && colonyCardsLeft <= 4
    }

    private fun shouldBuyEstate(): Boolean {
        return provinceCardsLeft in 1..2 || game.isIncludeColonyCards && colonyCardsLeft <= 2
    }

    private fun shouldBuyVictoryForLandmark(card: Card): Boolean {
        if (!card.isVictory) {
            return false
        }

        if (game.landmarks.any { it is Aqueduct } && game.getVictoryPointsOnSupplyPile(Aqueduct.NAME) >= 4) {
            return true
        }

        return game.landmarks.any { it is Battlefield } &&
                game.getVictoryPointsOnSupplyPile(Battlefield.NAME) >= 4 &&
                (turns > 6 || card.name == Duchy.NAME || card.name == Province.NAME)
    }

    private fun shouldPreferPayloadOverTreasure(): Boolean {
        return game.allCards.any { it.isCurseGiver || it.isVictoryCoinsCard || it.addBuys > 0 && hasAlternativeScoringAvailable }
    }

    private fun shouldPursueAlternativeScoring(card: Card): Boolean {
        val points = victoryPointsFromGain(card)

        return points >= 3 || points > 0 && isPilePressureLikely()
    }

    private fun hasTerminalRoomFor(card: Card): Boolean {
        if (!card.isTerminalAction) {
            return true
        }

        val preferredLimit = preferredMaxCopies[card.name]
        if (preferredLimit != null) {
            return terminalActionCount() < preferredLimit + extraActionCount() * 2
        }

        return terminalActionCount() < 1 + extraActionCount() * 2
    }

    private fun terminalActionCount(): Int {
        return cardCountByExpression { it.isTerminalAction }
    }

    private fun extraActionCount(): Int {
        return cardCountByExpression { it.isExtraActionsCard }
    }

    private fun maxActionCardsToOwn(): Int {
        return when {
            currentBuildPlan() == BuildPlan.Engine -> 10
            hasGoonsInSupply || hasLaboratoryInSupply -> 8
            extraActionCount() > 1 -> 7
            else -> 5
        }
    }

    private fun needsThinning(): Boolean {
        return allCards.count { it.isCurseOnly || it.isEstate || it.isCopper || it.isRuins } >= 4
    }

    private fun deckControlCardCount(): Int {
        return cardCountByExpression { it.isDeckControlCard() }
    }

    private fun hasDeckControlAvailable(): Boolean {
        return game.allCards.any { it.isDeckControlCard() }
    }

    private fun hasUsableAlternativeScoringAvailable(): Boolean {
        return game.allCards.any {
            it.isAlternativeScoringCardForBot() && potentialVictoryPointsFromGain(it) >= 2
        } || hasGoonsInSupply
    }

    private fun hasEngineSupportAvailable(): Boolean {
        val kingdomCards = game.kingdomCards
        return kingdomCards.any { it.isDrawSupportCard() } &&
                (kingdomCards.any { it.isVillageCard() } || kingdomCards.any { it.isPlusBuySupportCard() }) &&
                (kingdomCards.any { it.isDeckControlCard() } || kingdomCards.any { it.isReliabilityCard() })
    }

    private fun hasStrongPayloadAvailable(): Boolean {
        return game.kingdomCards.any { it.isStrongPayloadCard() }
    }

    private fun hasBetterOpeningBuildCardAvailable(): Boolean {
        return game.allCards.any {
            it.name != Silver.NAME &&
                    canBuyCard(it) &&
                    it.isKingdomPayloadCardForBot() &&
                    !it.isVictoryOnly &&
                    (it.isDeckControlCard() || it.isReliabilityCard() || it.isStrongPayloadCard()) &&
                    !super.excludeCard(it)
        }
    }

    private fun Card.isDeckControlCard(): Boolean {
        return isTrashingCard || addCards > 0 && addActions > 0 || addVillagers > 0
    }

    private fun Card.isDrawSupportCard(): Boolean {
        return drawSupportStrength() > 1
    }

    private fun Card.drawSupportStrength(): Int {
        val text = strategyText()
        return maxOf(
                addCards,
                when {
                    text.contains("+4 card") || text.contains("draw until") -> 4
                    text.contains("+3 card") -> 3
                    text.contains("+2 card") || text.contains("draw 2") -> 2
                    text.contains("draw") -> 2
                    else -> 0
                }
        )
    }

    private fun Card.isPlusBuySupportCard(): Boolean {
        val text = strategyText()
        return addBuys > 0 || text.contains("+1 buy") || text.contains("+2 buy")
    }

    private fun Card.isVillageCard(): Boolean {
        return addActions > 1 || addVillagers > 0
    }

    private fun Card.isReliabilityCard(): Boolean {
        val text = strategyText()
        return isDeckControlCard() ||
                addCards > 0 && addActions > 0 ||
                text.contains("look at") ||
                text.contains("top of your deck") ||
                text.contains("set aside")
    }

    private fun Card.isGainerCard(): Boolean {
        val text = strategyText()
        return text.contains("gain a card") ||
                text.contains("gain 2") ||
                text.contains("gain two")
    }

    private fun Card.isStrongPayloadCard(): Boolean {
        return isCurseGiver ||
                isVictoryCoinsCard ||
                drawSupportStrength() >= 3 ||
                isDrawSupportCard() && isPlusBuySupportCard() ||
                isGainerCard()
    }

    private fun Card.isKingdomPayloadCardForBot(): Boolean {
        return !isCopper && !isSilver && !isGold && !isPlatinum &&
                !isEstate && !isDuchy && !isProvince && !isColony && !isCurseOnly
    }

    private fun Card.isAlternativeScoringCardForBot(): Boolean {
        return isAlternativeScoringCard(this)
    }

    private fun isAlternativeScoringCard(card: Card): Boolean {
        return card.isVictory && card is VictoryPointsCalculator && !card.isProvince && !card.isColony && !card.isDuchy && !card.isEstate
    }

    private fun Card.strategyText(): String {
        return special.lowercase()
    }

    private fun victoryPointsFromGain(card: Card): Int {
        if (card !is VictoryPointsCalculator) {
            return if (card.isVictory || card.isCurse) card.victoryPoints else 0
        }

        return when (card.name) {
            Gardens.NAME -> (numCards + 1) / 10
            SilkRoad.NAME -> (cardCountByExpression { it.isVictory } + 1) / 4
            Duke.NAME -> cardCountByName(Duchy.NAME)
            Feodum.NAME -> cardCountByName(Silver.NAME) / 3
            Fairgrounds.NAME -> ((allCards.map { it.name }.toSet() + card.name).size / 5) * 2
            else -> card.calculatePoints(this)
        }
    }

    private fun potentialVictoryPointsFromGain(card: Card): Int {
        if (card !is VictoryPointsCalculator) {
            return if (card.isVictory || card.isCurse) card.victoryPoints else 0
        }

        return when (card.name) {
            Gardens.NAME -> (numCards + 11) / 10
            SilkRoad.NAME -> (cardCountByExpression { it.isVictory } + 3) / 4
            Duke.NAME -> maxOf(1, cardCountByName(Duchy.NAME))
            Feodum.NAME -> maxOf(1, (cardCountByName(Silver.NAME) + 2) / 3)
            Fairgrounds.NAME -> ((allCards.map { it.name }.toSet() + card.name).size + 2) / 5 * 2
            else -> victoryPointsFromGain(card)
        }
    }

    private fun isPilePressureLikely(): Boolean {
        val supplyPileNames = (game.cardsInSupply + game.kingdomCards).map { it.pileName }.toSet()
        val almostEmptyPiles = game.numInPileMap
                .filterKeys { it in supplyPileNames }
                .values
                .count { it in 1..2 }
        val numEmptyPilesForGameEnd = if (game.numPlayers > 4) 4 else 3

        return game.numEmptyPiles + almostEmptyPiles >= numEmptyPilesForGameEnd ||
                (game.numInPileMap[Curse.NAME] ?: Int.MAX_VALUE) <= 5
    }

    private fun chooseMinionMode(): Int {
        if (hand.count { it.name == Minion.NAME } > 0) {
            return 1
        }

        val handValueAfterMoneyChoice = availableCoins + hand.filter { it.isTreasure }.sumOf { it.addCoins } + 2
        val target = if (cardCountByName(Minion.NAME) >= 3) 8 else 5

        return if (handValueAfterMoneyChoice < target) 2 else 1
    }

    private fun chooseOptionalTrashCards(numCardsToTrash: Int, cardActionableExpression: ((card: Card) -> Boolean)?): List<Card> {
        val cardsToTrash = mutableListOf<Card>()
        var remainingTreasureValue = totalTreasureValue()

        while (cardsToTrash.size < numCardsToTrash) {
            val card = hand
                    .filter { it !in cardsToTrash }
                    .filter { cardActionableExpression == null || cardActionableExpression(it) }
                    .maxByOrNull { trashScore(it, remainingTreasureValue) } ?: break

            if (trashScore(card, remainingTreasureValue) < 20) {
                break
            }

            cardsToTrash.add(card)

            if (card.isTreasure) {
                remainingTreasureValue -= card.addCoins
            }
        }

        return cardsToTrash
    }

    private fun trashScore(card: Card, remainingTreasureValue: Int): Int {
        return when {
            card.isCurseOnly -> 200
            card.isEstate -> if (shouldTrashEstate()) 120 else 5
            card.isCopper -> if (remainingTreasureValue > minimumTreasureValueToKeep()) 90 else 5
            card.isRuins -> 80
            card.isVictory && !card.isProvince && !card.isColony -> if (shouldTrashEstate()) 60 else 5
            else -> 20 - card.cost
        }
    }

    private fun shouldTrashEstate(): Boolean {
        return provinceCardsLeft > 4 || game.isIncludeColonyCards && colonyCardsLeft > 4
    }

    private fun minimumTreasureValueToKeep(): Int {
        return if (hasLaboratoryInSupply && cardCountByName(Laboratory.NAME) > 0) 6 else 3
    }

    private fun totalTreasureValue(): Int {
        return allCards.filter { it.isTreasure }.sumOf { it.addCoins }
    }

    private fun buyingCardWouldEndGameWhileBehind(card: Card): Boolean {
        if (!buyingCardWillEndGame(card)) {
            return false
        }

        return victoryPointsAfterBuying(card) < (opponents.maxOfOrNull { it.victoryPoints } ?: Int.MIN_VALUE)
    }

    private fun buyingCardWillEndGame(card: Card): Boolean {
        if (card.name == Province.NAME && game.numInPileMap[Province.NAME] == 1) {
            return true
        }

        if (card.name == Colony.NAME && game.isIncludeColonyCards && game.numInPileMap[Colony.NAME] == 1) {
            return true
        }

        val numEmptyPilesForGameEnd = if (game.numPlayers > 4) 4 else 3
        val pileWillEmpty = game.numInPileMap[card.pileName] == 1

        return game.numEmptyPiles + (if (pileWillEmpty) 1 else 0) >= numEmptyPilesForGameEnd
    }

    private fun victoryPointsAfterBuying(card: Card): Int {
        val gainedVictoryPoints = when {
            card is VictoryPointsCalculator -> victoryPointsFromGain(card)
            card.isVictory || card.isCurse -> card.victoryPoints
            else -> 0
        }

        return victoryPoints + gainedVictoryPoints + goonsInPlay
    }
}
