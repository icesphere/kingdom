package com.kingdom.repository

import com.kingdom.model.cards.*
import com.kingdom.model.cards.adventures.*
import com.kingdom.model.cards.adventures.events.*
import com.kingdom.model.cards.base.*
import com.kingdom.model.cards.cornucopia.*
import com.kingdom.model.cards.darkages.*
import com.kingdom.model.cards.darkages.ruins.*
import com.kingdom.model.cards.darkages.shelters.Hovel
import com.kingdom.model.cards.darkages.shelters.Necropolis
import com.kingdom.model.cards.darkages.shelters.OvergrownEstate
import com.kingdom.model.cards.empires.*
import com.kingdom.model.cards.empires.castles.Castles
import com.kingdom.model.cards.empires.events.*
import com.kingdom.model.cards.empires.landmarks.*
import com.kingdom.model.cards.guilds.*
import com.kingdom.model.cards.hinterlands.*
import com.kingdom.model.cards.intrigue.*
import com.kingdom.model.cards.menagerie.*
import com.kingdom.model.cards.menagerie.events.*
import com.kingdom.model.cards.prosperity.*
import com.kingdom.model.cards.renaissance.*
import com.kingdom.model.cards.renaissance.projects.*
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
                Altar(),
                Armory(),
                BandOfMisfits(),
                BanditCamp(),
                Beggar(),
                Catacombs(),
                Count(),
                Counterfeit(),
                Cultist(),
                DeathCart(),
                Feodum(),
                Forager(),
                Fortress(),
                Graverobber(),
                Hermit(),
                Ironmonger(),
                JunkDealer(),
                Marauder(),
                MarketSquare(),
                Mystic(),
                Pillage(),
                PoorHouse(),
                Procession(),
                Rats(),
                Rebuild(),
                Rogue(),
                Sage(),
                Scavenger(),
                Squire(),
                Storeroom(),
                Urchin(),
                Vagrant(),
                WanderingMinstrel()
        )

    val shelters: List<Card>
        get() = listOf(
                Hovel(),
                Necropolis(),
                OvergrownEstate()
        )

    val ruins: List<Card>
        get() = listOf(
                AbandonedMine(),
                RuinedLibrary(),
                RuinedMarket(),
                RuinedVillage(),
                Survivors()
        )

    val guildsCards: List<Card>
        get() = listOf(
                Advisor(),
                Baker(),
                Butcher(),
                CandlestickMaker(),
                Doctor(),
                Herald(),
                Journeyman(),
                Masterpiece(),
                MerchantGuild(),
                Plaza(),
                Soothsayer(),
                Stonemason(),
                Taxman()
        )

    val adventuresCards: List<Card>
        get() = listOf(
                Amulet(),
                Artificer(),
                BridgeTroll(),
                CaravanGuard(),
                CoinOfTheRealm(),
                DistantLands(),
                Dungeon(),
                Duplicate(),
                Gear(),
                Giant(),
                Guide(),
                Hireling(),
                LostCity(),
                Magpie(),
                Messenger(),
                Miser(),
                Page(),
                Peasant(),
                Port(),
                Ranger(),
                Ratcatcher(),
                Raze(),
                Relic(),
                RoyalCarriage(),
                Storyteller(),
                Transmogrify(),
                TreasureTrove()
        )

    val adventuresEvents: List<Event>
        get() = listOf(
                Alms(),
                Ball(),
                Bonfire(),
                Borrow(),
                Expedition(),
                Ferry(),
                Inheritance(),
                LostArts(),
                Pathfinding(),
                Pilgrimage(),
                Plan(),
                Quest(),
                Raid(),
                Save(),
                ScoutingParty(),
                Seaway(),
                Trade(),
                Training(),
                TravellingFair()
        )

    val empiresCards: List<Card>
        get() = listOf(
                Archive(),
                Capital(),
                Castles(),
                Catapult(),
                ChariotRace(),
                Charm(),
                CityQuarter(),
                Crown(),
                Encampment(),
                Enchantress(),
                Engineer(),
                FarmersMarket(),
                Forum(),
                Gladiator(),
                Groundskeeper(),
                Legionary(),
                Overlord(),
                Patrician(),
                RoyalBlacksmith(),
                Sacrifice(),
                Settlers(),
                Temple(),
                Villa(),
                WildHunt()
        )

    val empiresEvents: List<Event>
        get() = listOf(
                Advance(),
                Annex(),
                Banquet(),
                Conquest(),
                Delve(),
                Dominate(),
                Donate(),
                SaltTheEarth(),
                TaintedVictory(),
                Tax(),
                Triumph(),
                Wedding(),
                Windfall()
        )

    val empiresLandmarks: List<Landmark>
        get() = listOf(
                Aqueduct(),
                Arena(),
                BanditFort(),
                Basilica(),
                Baths(),
                Battlefield(),
                Colonnade(),
                CursedMarket(),
                Fountain(),
                Keep(),
                Labyrinth(),
                MountainPass(),
                Museum(),
                Obelisk(),
                Orchard(),
                Palace(),
                Tomb(),
                Tower(),
                TriumphalArch(),
                Wall(),
                WolfDen()
        )

    val renaissanceCards: List<Card>
        get() = listOf(
                ActingTroupe(),
                BorderGuard(),
                CargoShip(),
                Ducat(),
                Experiment(),
                FlagBearer(),
                Hideout(),
                Improve(),
                Inventor(),
                Lackeys(),
                MountainVillage(),
                OldWitch(),
                Patron(),
                Priest(),
                Recruiter(),
                Research(),
                Scepter(),
                Scholar(),
                Sculptor(),
                Seer(),
                SilkMerchant(),
                Spices(),
                Swashbuckler(),
                Treasurer(),
                Villain()
        )

    val renaissanceProjects: List<Project>
        get() = listOf(
                Academy(),
                Barracks(),
                Canal(),
                Capitalism(),
                Cathedral(),
                Citadel(),
                CityGate(),
                CropRotation(),
                Exploration(),
                Fair(),
                Fleet(),
                Guildhall(),
                Innovation(),
                Pageant(),
                Piazza(),
                RoadNetwork(),
                Sewers(),
                Silos(),
                SinisterPlot(),
                StarChart()
        )

    val menagerieCards : List<Card>
        get() = listOf(
                Barge(),
                BountyHunter(),
                CamelTrain(),
                Cardinal(),
                Cavalry(),
                Destrier(),
                Displace(),
                Fisherman(),
                Goatherd(),
                Groom(),
                Hostelry(),
                HuntingLodge(),
                Kiln(),
                Livery(),
                Mastermind(),
                Paddock(),
                Sanctuary(),
                Scrap(),
                SnowyVillage(),
                Supplies(),
                Wayfarer()
        )

    val menagerieEvents: List<Event>
        get() = listOf(
                Alliance(),
                Bargain(),
                Commerce(),
                Delay(),
                Demand(),
                Desperation(),
                Enhance(),
                Gamble(),
                March(),
                Populate(),
                Pursue(),
                Ride(),
                Stampede(),
                Toil()
        )

    val allCards: List<Card>
        get() = baseCards + intrigueCards + seasideCards + prosperityCards + cornucopiaCards +
                hinterlandsCards + darkAgesCards + guildsCards + adventuresCards + empiresCards +
                renaissanceCards + menagerieCards

    val allEvents: List<Event>
        get() = adventuresEvents + empiresEvents + menagerieEvents

    val allLandmarks: List<Landmark>
        get() = empiresLandmarks

    val allProjects: List<Project>
        get() = renaissanceProjects

    val allEventsAndLandmarksAndProjects: List<Card>
        get() = allEvents + allLandmarks + allProjects

    fun getCardsByDeck(deck: Deck): List<Card> {
        return when (deck) {
            Deck.Base -> baseCards
            Deck.Intrigue -> intrigueCards
            Deck.Seaside -> seasideCards
            Deck.Prosperity -> prosperityCards
            Deck.Cornucopia -> cornucopiaCards
            Deck.Hinterlands -> hinterlandsCards
            Deck.DarkAges -> darkAgesCards
            Deck.Guilds -> guildsCards
            Deck.Adventures -> adventuresCards
            Deck.Empires -> empiresCards
            Deck.Renaissance -> renaissanceCards
            Deck.Menagerie -> menagerieCards
            else -> emptyList()
        }
    }


}
