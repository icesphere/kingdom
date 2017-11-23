package com.kingdom.model.computer;

import com.kingdom.model.Card;
import com.kingdom.model.Game;
import com.kingdom.model.Player;

import java.util.Collections;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * Date: 5/21/11
 * Time: 10:34 AM
 */
public class HardComputerPlayer extends ComputerPlayer
{
    public HardComputerPlayer(Player player, Game game) {
        super(player, game);
        difficulty = 3;
    }

    @Override
    protected void setupStartingStrategies() {
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

        Random random = new Random();

        if(hasTrashingCard && (kingdomCardMap.containsKey("Gardens") && hasExtraBuys)) {
            if (random.nextInt(2) == 0) {
                trashingStrategy = true;
            }
            else {
                gardensStrategy = true;
            }
        }
        else if (hasTrashingCard) {
            if (random.nextInt(2) == 0) {
                trashingStrategy = true;
            }
            else {
                bigMoneyStrategy = true;
            }
        }
        else {
            bigMoneyStrategy = true;
        }

        if (trashingStrategy) {
            if (trashingCards.size() == 1) {
                trashingCard = trashingCards.get(0);
            }
            else {
                if (kingdomCardMap.containsKey("Chapel")) {
                    int i = random.nextInt(3);
                    if (i < 2) {
                        trashingCard = kingdomCardMap.get("Chapel");
                    }
                }
                else if (kingdomCardMap.containsKey("Ambassador")) {
                    int i = random.nextInt(3);
                    if (i < 2) {
                        trashingCard = kingdomCardMap.get("Ambassador");
                    }
                }
                if(trashingCard == null) {
                    Collections.shuffle(trashingCards);
                    trashingCard = trashingCards.get(0);
                }
            }
        }

        //todo attack-based strategies

        //todo starting-action strategies
        if (!trashingStrategy && kingdomCardMap.containsKey("Sea Hag") && !fiveTwoSplit) {
            int i = random.nextInt(4);
            if (i < 3) {
                if (startingHandCoppers == 4) {
                    firstCard = kingdomCardMap.get("Sea Hag");
                }
                else {
                    secondCard = kingdomCardMap.get("Sea Hag");
                }
            }
        }

        if (trashingStrategy && trashingCard.getName().equals("Chapel")) {
            chapelStrategy = true;
            if (startingHandCoppers == 2 || startingHandCoppers == 3) {
                firstCard = trashingCard;
            }
            else {
                secondCard = trashingCard;
            }
            if (fiveTwoSplit && kingdomCardMap.containsKey("Laboratory")) {
                int i = random.nextInt(3);
                if (i < 2) {
                    if (startingHandCoppers == 5) {
                        firstCard = kingdomCardMap.get("Laboratory");
                    }
                    else {
                        secondCard = kingdomCardMap.get("Laboratory");
                    }
                }
            }
        }
        else if (trashingStrategy && trashingCard.getName().equals("Ambassador")) {
            if (!fiveTwoSplit) {
                if (startingHandCoppers == 3) {
                    firstCard = kingdomCardMap.get("Ambassador");
                    if (random.nextInt(2) == 0) {
                        secondCard = kingdomCardMap.get("Ambassador");
                    }
                }
                else {
                    secondCard = kingdomCardMap.get("Ambassador");
                    if (random.nextInt(2) == 0) {
                        firstCard = kingdomCardMap.get("Ambassador");
                    }
                }
            }
        }

        if (fiveTwoSplit && kingdomCardMap.containsKey("Witch")) {
            int i = random.nextInt(3);
            if (i < 2) {
                if (startingHandCoppers == 5) {
                    firstCard = kingdomCardMap.get("Witch");
                }
                else {
                    secondCard = kingdomCardMap.get("Witch");
                }
            }
        }

        //todo action-based strategies


        //todo combo-based strategies
    }

    @Override
    public Card buyCard() {
        Card card = buyCardHardDifficulty();
        if (card != null) {
            return card;
        }

        return super.buyCard();
    }

    @Override
    protected boolean excludeCard(Card card) {
        return excludeCardHard(card);
    }
}
