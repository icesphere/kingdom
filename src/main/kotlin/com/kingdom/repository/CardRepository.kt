package com.kingdom.repository

import com.kingdom.model.cards.*
import com.kingdom.model.cards.adventures.*
import com.kingdom.model.cards.adventures.events.*
import com.kingdom.model.cards.allies.*
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
import com.kingdom.model.cards.menagerie.ways.*
import com.kingdom.model.cards.plunder.*
import com.kingdom.model.cards.plunder.events.*
import com.kingdom.model.cards.plunder.traits.*
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
                Farm(),
                Upgrade(),
                WishingWell()
        )

    val seasideCards: List<Card>
        get() = listOf(
                Astrolabe(),
                Bazaar(),
                Blockade(),
                Caravan(),
                Corsair(),
                Cutpurse(),
                FishingVillage(),
                Haven(),
                Island(),
                Lighthouse(),
                Lookout(),
                MerchantShip(),
                Monkey(),
                NativeVillage(),
                Outpost(),
                Pirate(),
                Sailor(),
                Salvager(),
                SeaChart(),
                SeaWitch(),
                Smugglers(),
                Tactician(),
                TidePools(),
                TreasureMap(),
                Treasury(),
                Warehouse(),
                Wharf()
        )

    val prosperityCards: List<Card>
        get() = listOf(
                Anvil(),
                Bank(),
                Bishop(),
                Charlatan(),
                City(),
                Clerk(),
                Collection(),
                CrystalBall(),
                Expand(),
                Forge(),
                GrandMarket(),
                Hoard(),
                Investment(),
                KingsCourt(),
                Magnate(),
                Mint(),
                Monument(),
                Peddler(),
                Quarry(),
                Rabble(),
                Tiara(),
                Vault(),
                WarChest(),
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
                Berserker(),
                BorderVillage(),
                Cartographer(),
                Cauldron(),
                Crossroads(),
                Develop(),
                FoolsGold(),
                GuardDog(),
                Haggler(),
                Highway(),
                Inn(),
                JackOfAllTrades(),
                Margrave(),
                Nomads(),
                Oasis(),
                Scheme(),
                Souk(),
                SpiceMerchant(),
                Stables(),
                Trader(),
                Trail(),
                Weaver(),
                Wheelwright(),
                WitchsHut(),
                Farmland(),
                Tunnel()
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

    val menagerieCards: List<Card>
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
                Stockpile(),
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

    val menagerieWays: List<Way>
        get() = listOf(
                WayOfTheButterfly(),
                WayOfTheChameleon(),
                WayOfTheHorse(),
                WayOfTheMole(),
                WayOfTheMouse(),
                WayOfTheOwl(),
                WayOfTheRat(),
                WayOfTheSeal(),
                WayOfTheSquirrel(),
                WayOfTheTurtle()
        )

    val alliesAllies: List<Ally>
        get() = listOf(
                ArchitectsGuild(),
                BandOfNomads(),
                CaveDwellers(),
                CircleOfWitches(),
                CityState(),
                CoastalHaven(),
                CraftersGuild(),
                DesertGuides(),
                FamilyOfInventors(),
                FellowshipOfScribes(),
                ForestDwellers(),
                GangOfPickpockets(),
                IslandFolk(),
                LeagueOfBankers(),
                LeagueOfShopkeepers(),
                MarketTowns(),
                MountainFolk(),
                OrderOfAstrologers(),
                OrderOfMasons(),
                PeacefulCult(),
                PlateauShepherds(),
                TrappersLodge(),
                WoodworkersGuild()
        )

    val alliesCards: List<Card>
        get() = listOf(
                Augurs(),
                Barbarian(),
                Bauble(),
                Broker(),
                CapitalCity(),
                Carpenter(),
                Clashes(),
                Contract(),
                Courier(),
                Emissary(),
                Forts(),
                Galleria(),
                Guildmaster(),
                Highwayman(),
                Hunter(),
                Importer(),
                Innkeeper(),
                Marquis(),
                MerchantCamp(),
                Modify(),
                Odysseys(),
                RoyalGalley(),
                Sentinel(),
                Skirmisher(),
                Specialist(),
                Swap(),
                Sycophant(),
                Town(),
                Townsfolk(),
                Underling(),
                Wizards()
        )

    val plunderCards: List<Card>
        get() = listOf(
                Abundance(),
                BuriedTreasure(),
                CabinBoy(),
                Cage(),
                Crew(),
                Crucible(),
                Cutthroat(),
                Enlarge(),
                Figurine(),
                FirstMate(),
                Flagship(),
                FortuneHunter(),
                Frigate(),
                Gondola(),
                Grotto(),
                HarborVillage(),
                JewelledEgg(),
                KingsCache(),
                LandingParty(),
                Longship(),
                Mapmaker(),
                Maroon(),
                MiningRoad(),
                Pendant(),
                Pickaxe(),
                Pilgrim(),
                Quartermaster(),
                Rope(),
                SackOfLoot(),
                Search(),
                SecludedShrine(),
                Shaman(),
                SilverMine(),
                Siren(),
                Stowaway(),
                SwampShacks(),
                Taskmaster(),
                Tools(),
                Trickster(),
                WealthyVillage()
        )

    val plunderEvents: List<Event>
        get() = listOf(
                Avoid(),
                Bury(),
                Deliver(),
                Foray(),
                Invasion(),
                Journey(),
                Launch(),
                Looting(),
                Maelstrom(),
                Mirror(),
                Peril(),
                Prepare(),
                Prosper(),
                Rush(),
                Scrounge()
        )

    val plunderTraits: List<Trait>
        get() = listOf(
                Cheap(),
                Cursed(),
                Fated(),
                Fawning(),
                Friendly(),
                Hasty(),
                Inherited(),
                Inspiring(),
                Nearby(),
                Patient(),
                Pious(),
                Reckless(),
                Rich(),
                Shy(),
                Tireless()
        )

    val allCards: List<Card>
        get() = baseCards + intrigueCards + seasideCards + prosperityCards + cornucopiaCards +
                hinterlandsCards + darkAgesCards + guildsCards + adventuresCards + empiresCards +
                renaissanceCards + menagerieCards + alliesCards + plunderCards

    val allEvents: List<Event>
        get() = adventuresEvents + empiresEvents + menagerieEvents + plunderEvents

    val allLandmarks: List<Landmark>
        get() = empiresLandmarks

    val allProjects: List<Project>
        get() = renaissanceProjects

    val allWays: List<Way>
        get() = menagerieWays

    val allAllies: List<Ally>
        get() = alliesAllies

    val allTraits: List<Trait>
        get() = plunderTraits

    val allEventsAndLandmarksAndProjectsAndWays: List<Card>
        get() = allEvents + allLandmarks + allProjects + allWays + allTraits

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
            Deck.Allies -> alliesCards
            Deck.Plunder -> plunderCards
            else -> emptyList()
        }
    }


}
