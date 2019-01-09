package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class InheritanceEstate(private val associatedActionCard: Card, cardType: CardType) : AdventuresCard(NAME, cardType, 2) {

    init {
        victoryPoints = 1
    }

    override fun cardPlayedSpecialAction(player: Player) {
        associatedActionCard.cardPlayedSpecialAction(player)
    }

    companion object {
        const val NAME: String = "Estate"

        fun calculateInheritanceEstateCardType(card: Card): CardType {
            return when (card.type) {
                CardType.Action -> CardType.ActionVictory
                CardType.ActionAttack -> CardType.ActionAttackVictory
                CardType.ActionAttackDuration -> CardType.ActionAttackDurationVictory
                CardType.ActionAttackLooter -> CardType.ActionAttackLooterVictory
                CardType.ActionReaction -> CardType.ActionReactionVictory
                CardType.ActionDuration -> CardType.ActionDurationVictory
                CardType.ActionRuins -> CardType.ActionRuinsVictory
                CardType.ActionShelter -> CardType.ActionShelterVictory
                CardType.ReactionShelter -> CardType.ReactionShelterVictory
                CardType.ActionLooter -> CardType.ActionLooterVictory
                CardType.ActionReserve -> CardType.ActionReserveVictory
                CardType.ActionDurationReaction -> CardType.ActionDurationReactionVictory
                CardType.ActionTraveller -> CardType.ActionTravellerVictory
                CardType.ActionAttackTraveller -> CardType.ActionAttackTravellerVictory
                else -> card.type
            }
        }
    }
}

