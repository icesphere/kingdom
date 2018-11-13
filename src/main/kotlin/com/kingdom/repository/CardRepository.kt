package com.kingdom.repository

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.cornucopia.*
import com.kingdom.model.cards.darkages.Beggar
import com.kingdom.model.cards.darkages.PoorHouse
import com.kingdom.model.cards.darkages.Squire
import com.kingdom.model.cards.hinterlands.*
import com.kingdom.model.cards.intrigue.*
import com.kingdom.model.cards.kingdom.*
import com.kingdom.model.cards.prosperity.*
import com.kingdom.model.cards.seaside.*
import org.springframework.stereotype.Service

@Service
class CardRepository {

    val baseCards: List<Card>
        get() = listOf(
                Artisan(),
                Bandit(),
                Bureaucrat(),
                Cellar(),
                Chapel(),
                CouncilRoom(),
                Festival(),
                Gardens(),
                Harbinger(),
                Laboratory(),
                Library(),
                Market(),
                Merchant(),
                Militia(),
                Mine(),
                Moat(),
                Moneylender(),
                Poacher(),
                Remodel(),
                Sentry(),
                Smithy(),
                ThroneRoom(),
                Vassal(),
                Village(),
                Witch(),
                Workshop()
        )

    val intrigueCards: List<Card>
        get() = listOf(
                Baron(),
                Bridge(),
                Conspirator(),
                Courtier(),
                Courtyard(),
                Diplomat(),
                Duke(),
                Ironworks(),
                Lurker(),
                Masquerade(),
                Mill(),
                MiningVillage(),
                Minion(),
                Nobles(),
                Patrol(),
                Pawn(),
                Replace(),
                ShantyTown(),
                Steward(),
                Swindler(),
                Torturer(),
                TradingPost(),
                TreasureRoom(),
                Upgrade(),
                WishingWell()
        )

    val seasideCards: List<Card>
        get() = listOf(
                Ambassador(),
                Bazaar(),
                Caravan(),
                Cutpurse(),
                Embargo(),
                Explorer(),
                FishingVillage(),
                GhostShip(),
                Haven(),
                Island(),
                Lighthouse(),
                Lookout(),
                MerchantShip(),
                NativeVillage(),
                PearlDiver(),
                PirateShip(),
                Salvager(),
                SeaHag(),
                Smugglers(),
                Tactician(),
                TreasureMap(),
                Treasury(),
                Warehouse(),
                Wharf()
        )

    val prosperityCards: List<Card>
        get() = listOf(
                Bank(),
                Bishop(),
                City(),
                Contraband(),
                CountingHouse(),
                Expand(),
                Forge(),
                Goons(),
                GrandMarket(),
                Hoard(),
                KingsCourt(),
                Loan(),
                Mint(),
                Mountebank(),
                Monument(),
                Peddler(),
                Quarry(),
                Rabble(),
                RoyalSeal(),
                TradeRoute(),
                Vault(),
                Venture(),
                Watchtower(),
                WorkersVillage()
        )

    val cornucopiaCards: List<Card>
        get() = listOf(
                Fairgrounds(),
                FarmingVillage(),
                FortuneTeller(),
                Hamlet(),
                Harvest(),
                HornOfPlenty(),
                HorseTraders(),
                HuntingParty(),
                Jester(),
                Menagerie(),
                Remake()
        )

    val hinterlandsCards: List<Card>
        get() = listOf(
                BorderVillage(),
                Cache(),
                Crossroads(),
                Embassy(),
                Farmland(),
                Haggler(),
                Highway(),
                IllGottenGains(),
                Inn(),
                JackOfAllTrades(),
                Mandarin(),
                Margrave(),
                NobleBrigand(),
                NomadCamp(),
                Oasis(),
                SilkRoad(),
                SpiceMerchant(),
                Stables(),
                Trader()
        )

    val darkAgesCards: List<Card>
        get() = listOf(
                Beggar(),
                PoorHouse(),
                Squire()
        )

    val allCards: List<Card>
        get() = baseCards + intrigueCards + seasideCards + prosperityCards + cornucopiaCards +
                hinterlandsCards + darkAgesCards

    fun getCardsByDeck(deck: Deck): List<Card> {
        return when (deck) {
            Deck.Base -> baseCards
            Deck.Intrigue -> intrigueCards
            Deck.Seaside -> seasideCards
            Deck.Prosperity -> prosperityCards
            Deck.Cornucopia -> cornucopiaCards
            Deck.Hinterlands -> hinterlandsCards
            Deck.DarkAges -> darkAgesCards
            else -> emptyList()
        }
    }


}
