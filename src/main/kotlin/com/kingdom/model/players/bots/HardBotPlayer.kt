package com.kingdom.model.players.bots

import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.base.*
import com.kingdom.model.cards.cornucopia.Fairgrounds
import com.kingdom.model.cards.cornucopia.HornOfPlenty
import com.kingdom.model.cards.darkages.Feodum
import com.kingdom.model.cards.hinterlands.Trader
import com.kingdom.model.cards.hinterlands.SilkRoad
import com.kingdom.model.cards.intrigue.Duke
import com.kingdom.model.cards.intrigue.Minion
import com.kingdom.model.cards.intrigue.WishingWell
import com.kingdom.model.cards.prosperity.Expand
import com.kingdom.model.cards.prosperity.Goons
import com.kingdom.model.cards.prosperity.Monument
import com.kingdom.model.cards.prosperity.Mountebank
import com.kingdom.model.cards.prosperity.Quarry
import com.kingdom.model.cards.seaside.Ambassador
import com.kingdom.model.cards.seaside.Bazaar
import com.kingdom.model.cards.seaside.Cutpurse
import com.kingdom.model.cards.seaside.SeaHag
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
            SeaHag.NAME to 2,
            Witch.NAME to 2,
            Mountebank.NAME to 2,
            Goons.NAME to 3,
            Monument.NAME to 2,
            Militia.NAME to 2,
            Cutpurse.NAME to 2,
            Smithy.NAME to 1,
            CouncilRoom.NAME to 1,
            Ambassador.NAME to 1,
            Bureaucrat.NAME to 1,
            Moneylender.NAME to 1,
            Minion.NAME to 6,
            Market.NAME to 4,
            Laboratory.NAME to 4,
            Bazaar.NAME to 3,
            Festival.NAME to 3
    )

    private val namedCardTieBreakers = mapOf(
            SeaHag.NAME to 3,
            Witch.NAME to 3,
            Mountebank.NAME to 3,
            Goons.NAME to 3,
            Monument.NAME to 2,
            Militia.NAME to 2,
            Smithy.NAME to 2,
            CouncilRoom.NAME to 2,
            Cutpurse.NAME to 2,
            Ambassador.NAME to 2,
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
            Duchy.NAME -> return scoreWithInheritedAdjustments(if (shouldBuyDuchy()) 30 else 0, card)
            Estate.NAME -> return scoreWithInheritedAdjustments(if (shouldBuyEstate()) 27 else 0, card)
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

        return score + generalizedKingdomAnalysisAdjustment(card) + strategicPlanAdjustment(card) + namedCardTieBreaker(card)
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
            Duchy.NAME -> return !shouldBuyDuchy()
            Estate.NAME -> return !shouldBuyEstate()
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

        return baseScore + inheritedScoreAdjustment(card)
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
