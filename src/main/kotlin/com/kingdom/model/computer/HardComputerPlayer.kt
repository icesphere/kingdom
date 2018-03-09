package com.kingdom.model.computer

import com.kingdom.model.cards.Card
import com.kingdom.model.OldGame
import com.kingdom.model.OldPlayer
import java.util.*

open class HardComputerPlayer(player: OldPlayer, game: OldGame) : ComputerPlayer(player, game) {
    init {
        difficulty = 3
    }

    override fun setupStartingStrategies() {
        /*if(!chapelStrategy && !ambassadorStrategy) {
            if (fiveTwoSplit) {
                if (card.getName().equals("Witch")) {
                    witchStrategy = true;
                    witchCard = card;
                }
            }
            else {
                if (difficulty >= 3 && card.getName().equals("Sea Hag")) {
                    seaHagStrategy = true;
                    seaHagCard = card;
                }
                else if (card.getName().equals("Pirate Ship") && game.getNumPlayers() > 2) {
                    pirateShipStrategy = true;
                    pirateShipCard = card;
                }
                else if (difficulty >= 3 && card.getName().equals("Mining Village")) {
                    miningVillageStrategy = true;
                    miningVillageCard = card;
                }
            }
        }

        if (difficulty >= 3 && chapelStrategy && card.getName().equals("Laboratory") && fiveTwoSplit) {
            laboratoryStrategy = true;
            laboratoryCard = card;
        }*/

        //todo victory coin based strategies

        //todo when to buy vineyard, fairgrounds, silk road

        val random = Random()

        if (hasTrashingCard && kingdomCardMap.containsKey("Gardens") && hasExtraBuys) {
            if (random.nextInt(2) == 0) {
                trashingStrategy = true
            } else {
                isGardensStrategy = true
            }
        } else if (hasTrashingCard) {
            if (random.nextInt(2) == 0) {
                trashingStrategy = true
            } else {
                bigMoneyStrategy = true
            }
        } else {
            bigMoneyStrategy = true
        }

        if (trashingStrategy) {
            if (trashingCards.size == 1) {
                trashingCard = trashingCards[0]
            } else {
                if (kingdomCardMap.containsKey("Chapel")) {
                    val i = random.nextInt(3)
                    if (i < 2) {
                        trashingCard = kingdomCardMap["Chapel"]
                    }
                } else if (kingdomCardMap.containsKey("Ambassador")) {
                    val i = random.nextInt(3)
                    if (i < 2) {
                        trashingCard = kingdomCardMap["Ambassador"]
                    }
                }
                if (trashingCard == null) {
                    Collections.shuffle(trashingCards)
                    trashingCard = trashingCards[0]
                }
            }
        }

        //todo attack-based strategies

        //todo starting-action strategies
        if (!trashingStrategy && kingdomCardMap.containsKey("Sea Hag") && !fiveTwoSplit) {
            val i = random.nextInt(4)
            if (i < 3) {
                if (startingHandCoppers == 4) {
                    firstCard = kingdomCardMap["Sea Hag"]
                } else {
                    secondCard = kingdomCardMap["Sea Hag"]
                }
            }
        }

        if (trashingStrategy && trashingCard!!.name == "Chapel") {
            chapelStrategy = true
            if (startingHandCoppers == 2 || startingHandCoppers == 3) {
                firstCard = trashingCard
            } else {
                secondCard = trashingCard
            }
            if (fiveTwoSplit && kingdomCardMap.containsKey("Laboratory")) {
                val i = random.nextInt(3)
                if (i < 2) {
                    if (startingHandCoppers == 5) {
                        firstCard = kingdomCardMap["Laboratory"]
                    } else {
                        secondCard = kingdomCardMap["Laboratory"]
                    }
                }
            }
        } else if (trashingStrategy && trashingCard!!.name == "Ambassador") {
            if (!fiveTwoSplit) {
                if (startingHandCoppers == 3) {
                    firstCard = kingdomCardMap["Ambassador"]
                    if (random.nextInt(2) == 0) {
                        secondCard = kingdomCardMap["Ambassador"]
                    }
                } else {
                    secondCard = kingdomCardMap["Ambassador"]
                    if (random.nextInt(2) == 0) {
                        firstCard = kingdomCardMap["Ambassador"]
                    }
                }
            }
        }

        if (fiveTwoSplit && kingdomCardMap.containsKey("Witch")) {
            val i = random.nextInt(3)
            if (i < 2) {
                if (startingHandCoppers == 5) {
                    firstCard = kingdomCardMap["Witch"]
                } else {
                    secondCard = kingdomCardMap["Witch"]
                }
            }
        }

        //todo action-based strategies


        //todo combo-based strategies
    }

    public override fun buyCard(): Card? {
        val card = buyCardHardDifficulty()
        return card ?: super.buyCard()

    }

    override fun excludeCard(card: Card): Boolean {
        return excludeCardHard(card)
    }
}
