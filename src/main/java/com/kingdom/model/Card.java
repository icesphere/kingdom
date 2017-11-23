package com.kingdom.model;

import com.kingdom.util.KingdomUtil;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Card {
    public static final int TYPE_ACTION = 1;
    public static final int TYPE_ACTION_ATTACK = 2;
    public static final int TYPE_VICTORY = 3;
    public static final int TYPE_ACTION_REACTION = 4;
    public static final int TYPE_TREASURE = 5;
    public static final int TYPE_CURSE = 6;
    public static final int TYPE_ACTION_AND_VICTORY = 7;
    public static final int TYPE_TREASURE_AND_VICTORY = 8;
    public static final int TYPE_ACTION_DURATION = 9;
    public static final int TYPE_TREASURE_AND_CURSE = 10;
    public static final int TYPE_VICTORY_AND_REACTION = 11;
    public static final int TYPE_DURATION_AND_VICTORY = 12;
    public static final int TYPE_LEADER = 13;
    public static final int TYPE_TREASURE_REACTION = 14;
    public static final int TYPE_ACTION_SUMMON = 15;

    public static final int COPPER_ID = -1;
    public static final int SILVER_ID = -2;
    public static final int GOLD_ID = -3;
    public static final int PLATINUM_ID = -9;

    public static final int ESTATE_ID = -4;
    public static final int DUCHY_ID = -5;
    public static final int PROVINCE_ID = -6;
    public static final int COLONY_ID = -10;

    public static final int CURSE_ID = -7;

    public static final int POTION_ID = -8;

    public static final String TREASURE_COLOR = "#F6DC51";
    public static final String CURSE_COLOR = "#A17FBC";
    public static final String VICTORY_COLOR = "#80B75A";
    public static final String ACTION_REACTION_COLOR = "#7FAED8";
    public static final String ACTION_DURATION_COLOR = "#F09954";
    public static final String ACTION_COLOR = "#CBC6B3";
    public static final String LEADER_COLOR = "#BB0000";

    public static final String ACTION_AND_VICTORY_IMAGE = "grey_green.gif";
    public static final String TREASURE_AND_VICTORY_IMAGE = "gold_green.gif";
    public static final String VICTORY_AND_REACTION_IMAGE = "green_blue.gif";
    public static final String TREASURE_AND_CURSE_IMAGE = "gold_purple.gif";
    public static final String DURATION_AND_VICTORY_IMAGE = "orange_green.gif";
    public static final String TREASURE_REACTION_IMAGE = "gold_blue.gif";

    public static final String DECK_KINGDOM = "Kingdom";
    public static final String DECK_INTRIGUE = "Intrigue";
    public static final String DECK_SEASIDE = "Seaside";
    public static final String DECK_PROMO = "Promo";
    public static final String DECK_ALCHEMY = "Alchemy";
    public static final String DECK_PROSPERITY = "Prosperity";
    public static final String DECK_SALVATION = "Salvation";
    public static final String DECK_CORNUCOPIA = "Cornucopia";
    public static final String DECK_HINTERLANDS = "Hinterlands";
    public static final String DECK_FAIRYTALE = "FairyTale";
    public static final String DECK_LEADERS = "Leaders";
    public static final String DECK_FAN = "Fan";
    public static final String DECK_PROLETARIAT = "Proletariat";

    public static final String DECK_REACTION= "Reaction";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cardid")
    private int cardId;

    private String name = "";

    private int cost;

    private int type;

    private String special = "";

    @Column(name = "add_actions")
    private int addActions;

    @Column(name = "add_coins")
    private int addCoins;

    @Column(name = "add_cards")
    private int addCards;

    @Column(name = "add_buys")
    private int addBuys;

    @Column(name = "victory_points")
    private int victoryPoints;

    private String deck = "";

    private boolean testing;

    @Column(name = "cost_includes_potion")
    private boolean costIncludesPotion;

    @Column(name = "add_victory_coins")
    private int addVictoryCoins;

    @Column(name = "play_treasure_cards")
    private boolean playTreasureCards;

    private boolean disabled;

    @Column(name = "fan_expansion_card")
    private boolean fanExpansionCard;

    private int sins;

    @Column(name = "prize_card")
    private boolean prizeCard;

    @Column(name = "font_size")
    private int fontSize;

    @Column(name = "name_lines")
    private int nameLines = 1;

    @Column(name = "text_size")
    private int textSize;

    @Column(name = "fruit_tokens")
    private int fruitTokens;

    @Column(name = "cattle_tokens")
    private int cattleTokens;

    private transient List<Card> associatedCards = new ArrayList<Card>(0);

    private transient boolean disableSelect;
    private transient boolean autoSelect;

    private transient boolean activated;

    private transient boolean copied;

    private transient Map<String, CardAction> gainCardActions = new HashMap<String, CardAction>(0);

    private transient String destination = "";

    private transient boolean traderProcessed;

    private transient boolean cardNotGained;

    private transient boolean gainedFromBuy;

    public Card() {
    }
    
    public Card(Card card){
        this.cardId = card.getCardId();
        this.name = card.getName();
        this.cost = card.getCost();
        this.type = card.getType();
        this.special = card.getSpecial();
        this.addActions = card.getAddActions();
        this.addCoins = card.getAddCoins();
        this.addCards = card.getAddCards();
        this.addBuys = card.getAddBuys();
        this.addVictoryCoins = card.getAddVictoryCoins();
        this.victoryPoints = card.getVictoryPoints();
        this.deck = card.getDeck();
        this.testing = card.isTesting();
        this.costIncludesPotion = card.isCostIncludesPotion();
        this.sins = card.getSins();
        this.fontSize = card.getFontSize();
        this.nameLines = card.getNameLines();
        this.textSize = card.getTextSize();
        this.copied = true;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getType() {
        return type;
    }

    public String getTypeAsString() {
        if (type == TYPE_ACTION) {
            return "Action";
        } else if (type == TYPE_ACTION_ATTACK) {
            return "Action - Attack";
        } else if (type == TYPE_ACTION_REACTION) {
            return "Action - Reaction";
        } else if (type == TYPE_VICTORY) {
            return "Victory";
        } else if (type == TYPE_CURSE) {
            return "Curse";
        } else if (type == TYPE_TREASURE) {
            return "Treasure";
        } else if (type == TYPE_ACTION_AND_VICTORY) {
            return "Action - Victory";
        } else if (type == TYPE_TREASURE_AND_VICTORY) {
            return "Treasure - Victory";
        } else if (type == TYPE_ACTION_DURATION) {
            return "Action - Duration";
        } else if (type == TYPE_VICTORY_AND_REACTION) {
            return "Victory - Reaction";
        } else if (type == TYPE_TREASURE_AND_CURSE) {
            return "Treasure - Curse";
        } else if (type == TYPE_DURATION_AND_VICTORY) {
            return "Duration - Victory";
        } else if (type == TYPE_LEADER) {
            return "Leader";
        } else if (type == TYPE_TREASURE_REACTION) {
            return "Treasure - Reaction";
        } else if (type == TYPE_ACTION_SUMMON) {
            return "Action - Summon";
        }
        return "";
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSpecial() {
        return special;
    }

    public boolean isSpecialCard() {
        return !special.equals("");
    }

    public String getTruncatedSpecial() {
        if (textSize > 0) {
            return special.substring(0, textSize) + "...";
        }
        else {
            return special;
        }
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public String getFullCardText() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTypeAsString());
        sb.append(" (cost ").append(cost);
        if (costIncludesPotion) {
            sb.append(" and a potion");
        }
        sb.append("): ");
        if (victoryPoints != 0) {
            sb.append(victoryPoints).append(" VP. ");
        }
        if (addCoins != 0) {
            sb.append(getAmountSymbol(addCoins)).append(KingdomUtil.getPlural(addCoins, "coin")).append(". ");
        }
        if (addCards != 0) {
            sb.append(getAmountSymbol(addCards)).append(KingdomUtil.getPlural(addCards, "card")).append(". ");
        }
        if (addActions != 0) {
            sb.append(getAmountSymbol(addActions)).append(KingdomUtil.getPlural(addActions, "action")).append(". ");
        }
        if (addBuys != 0) {
            sb.append(getAmountSymbol(addBuys)).append(KingdomUtil.getPlural(addBuys, "buy")).append(". ");
        }
        if (sins != 0) {
            sb.append(getAmountSymbol(sins)).append(KingdomUtil.getPlural(sins, "sin")).append(". ");
        }
        if (addVictoryCoins != 0) {
            sb.append(getAmountSymbol(addVictoryCoins)).append(KingdomUtil.getPlural(addVictoryCoins, "victory coin")).append(". ");
        }
        sb.append(special);
        return sb.toString();
    }

    private String getAmountSymbol(int amount) {
        if (amount < 0) {
            return "";
        }
        else {
            return "+";
        }
    }

    public int getAddActions() {
        return addActions;
    }

    public void setAddActions(int addActions) {
        this.addActions = addActions;
    }

    public int getAddCoins() {
        return addCoins;
    }

    public void setAddCoins(int addCoins) {
        this.addCoins = addCoins;
    }

    public int getAddCards() {
        return addCards;
    }

    public void setAddCards(int addCards) {
        this.addCards = addCards;
    }

    public int getAddBuys() {
        return addBuys;
    }

    public void setAddBuys(int addBuys) {
        this.addBuys = addBuys;
    }

    public int getAddVictoryCoins() {
        return addVictoryCoins;
    }

    public void setAddVictoryCoins(int addVictoryCoins) {
        this.addVictoryCoins = addVictoryCoins;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public String getDeck(){
        return deck;
    }

    public void setDeck(String deck) {
        this.deck = deck;
    }

    public static Card getEstateCard(){
        Card card = new Card();
        card.setCardId(ESTATE_ID);
        card.setType(TYPE_VICTORY);
        card.setName("Estate");
        card.setCost(2);
        card.setVictoryPoints(1);
        return card;
    }

    public static Card getDuchyCard(){
        Card card = new Card();
        card.setCardId(DUCHY_ID);
        card.setType(TYPE_VICTORY);
        card.setName("Duchy");
        card.setCost(5);
        card.setVictoryPoints(3);
        return card;
    }

    public static Card getProvinceCard(){
        Card card = new Card();
        card.setCardId(PROVINCE_ID);
        card.setType(TYPE_VICTORY);
        card.setName("Province");
        card.setCost(8);
        card.setVictoryPoints(6);
        return card;
    }

    public static Card getColonyCard() {
        Card card = new Card();
        card.setCardId(COLONY_ID);
        card.setType(TYPE_VICTORY);
        card.setName("Colony");
        card.setCost(11);
        card.setVictoryPoints(10);
        return card;
    }

    public static Card getCopperCard(){
        Card card = new Card();
        card.setCardId(COPPER_ID);
        card.setType(TYPE_TREASURE);
        card.setName("Copper");
        card.setCost(0);
        card.setAddCoins(1);
        return card;
    }

    public static Card getSilverCard(){
        Card card = new Card();
        card.setCardId(SILVER_ID);
        card.setType(TYPE_TREASURE);
        card.setName("Silver");
        card.setCost(3);
        card.setAddCoins(2);
        return card;
    }

    public static Card getGoldCard(){
        Card card = new Card();
        card.setCardId(GOLD_ID);
        card.setType(TYPE_TREASURE);
        card.setName("Gold");
        card.setCost(6);
        card.setAddCoins(3);
        return card;
    }

    public static Card getPlatinumCard() {
        Card card = new Card();
        card.setCardId(PLATINUM_ID);
        card.setType(TYPE_TREASURE);
        card.setName("Platinum");
        card.setCost(9);
        card.setAddCoins(5);
        return card;
    }

    public static Card getCurseCard(){
        Card card = new Card();
        card.setCardId(CURSE_ID);
        card.setType(TYPE_CURSE);
        card.setName("Curse");
        card.setCost(0);
        card.setVictoryPoints(-1);
        return card;
    }

    public static Card getPotionCard() {
        Card card = new Card();
        card.setCardId(POTION_ID);
        card.setType(TYPE_TREASURE);
        card.setName("Potion");
        card.setCost(4);
        return card;
    }

    public boolean isVictoryOnly() {
        return type == TYPE_VICTORY;
    }

    public boolean isVictory(){
        return type == TYPE_VICTORY || type == TYPE_ACTION_AND_VICTORY || type == TYPE_TREASURE_AND_VICTORY || type == TYPE_VICTORY_AND_REACTION || type == TYPE_DURATION_AND_VICTORY;
    }

    public boolean isVictoryReaction() {
        return type == TYPE_VICTORY_AND_REACTION;
    }

    public boolean isAction() {
        return type == TYPE_ACTION || type == TYPE_ACTION_ATTACK || type == TYPE_ACTION_REACTION || type == TYPE_ACTION_AND_VICTORY || type == TYPE_ACTION_DURATION || type == TYPE_DURATION_AND_VICTORY || type == TYPE_ACTION_SUMMON;
    }

    public boolean isTerminalAction() {
        return isAction() && addActions == 0 && !name.equals("Nobles") && !name.equals("Pawn") && !name.equals("Trusty Steed");
    }
    
    public boolean isDuration(){
        return type == TYPE_ACTION_DURATION || type == TYPE_DURATION_AND_VICTORY;
    }

    public boolean isTreasure() {
        return type == TYPE_TREASURE || type == TYPE_TREASURE_AND_VICTORY || type == TYPE_TREASURE_AND_CURSE || type == TYPE_TREASURE_REACTION;
    }

    public boolean isReaction() {
        return type == TYPE_ACTION_REACTION || type == TYPE_VICTORY_AND_REACTION || type == TYPE_TREASURE_REACTION;
    }

    public boolean isCurse() {
        return type == TYPE_CURSE || type == TYPE_TREASURE_AND_CURSE;
    }

    public boolean isCurseOnly(){
        return cardId == CURSE_ID;
    }

    public boolean isCopper() {
        return cardId == COPPER_ID;
    }

    public boolean isSilver() {
        return cardId == SILVER_ID;
    }

    public boolean isGold() {
        return cardId == GOLD_ID;
    }

    public boolean isPlatinum() {
        return cardId == PLATINUM_ID;
    }

    public boolean isEstate() {
        return cardId == ESTATE_ID;
    }

    public boolean isDuchy() {
        return cardId == DUCHY_ID;
    }

    public boolean isProvince() {
        return cardId == PROVINCE_ID;
    }

    public boolean isColony() {
        return cardId == COLONY_ID;
    }

    public boolean isPotion() {
        return cardId == POTION_ID;
    }

    public boolean isAttack(){
        return type == TYPE_ACTION_ATTACK;
    }

    public boolean isDefense() {
        return name.equals("Moat") || name.equals("Lighthouse") || name.equals("Watchtower") || name.equals("Bell Tower") || name.equals("Enchanted Palace");
    }

    public boolean hasSpecial() {
        return special != null && !special.equals("");
    }

    public boolean isDisableSelect() {
        return disableSelect;
    }

    public void setDisableSelect(boolean disableSelect) {
        this.disableSelect = disableSelect;
    }

    public boolean isTesting() {
        return testing;
    }

    public void setTesting(boolean testing) {
        this.testing = testing;
    }

    public boolean isCostIncludesPotion() {
        return costIncludesPotion;
    }

    public void setCostIncludesPotion(boolean costIncludesPotion) {
        this.costIncludesPotion = costIncludesPotion;
    }

    public boolean isPlayTreasureCards() {
        return playTreasureCards;
    }

    public void setPlayTreasureCards(boolean playTreasureCards) {
        this.playTreasureCards = playTreasureCards;
    }

    public boolean isPrizeCard() {
        return prizeCard;
    }

    public void setPrizeCard(boolean prizeCard) {
        this.prizeCard = prizeCard;
    }

    public boolean isKingdom() {
        return deck.equals(DECK_KINGDOM);
    }

    public boolean isIntrigue() {
        return deck.equals(DECK_INTRIGUE);
    }

    public boolean isSeaside() {
        return deck.equals(DECK_SEASIDE);
    }

    public boolean isAlchemy() {
        return deck.equals(DECK_ALCHEMY);
    }

    public boolean isPromo() {
        return deck.equals(DECK_PROMO);
    }

    public boolean isProsperity() {
        return deck.equals(DECK_PROSPERITY);
    }

    public boolean isSalvation() {
        return deck.equals(DECK_SALVATION);
    }

    public boolean isFairyTale() {
        return deck.equals(DECK_FAIRYTALE);
    }

    public boolean isCornucopia() {
        return deck.equals(DECK_CORNUCOPIA);
    }

    public boolean isHinterlands() {
        return deck.equals(DECK_HINTERLANDS);
    }

    public boolean isLeader() {
        return deck.equals(DECK_LEADERS);
    }

    public boolean isProletariat() {
        return deck.equals(DECK_PROLETARIAT);
    }

    public boolean isFan() {
        return deck.equals(DECK_FAN);
    }

    public String getBackgroundColor() {
        if (type == TYPE_ACTION_AND_VICTORY) {
            return ACTION_AND_VICTORY_IMAGE;
        }
        else if (type == TYPE_TREASURE_AND_VICTORY) {
            return TREASURE_AND_VICTORY_IMAGE;
        }
        else if (type == TYPE_TREASURE_AND_CURSE) {
            return TREASURE_AND_CURSE_IMAGE;
        }
        else if (type == TYPE_VICTORY_AND_REACTION) {
            return VICTORY_AND_REACTION_IMAGE;
        }
        else if (type == TYPE_DURATION_AND_VICTORY) {
            return DURATION_AND_VICTORY_IMAGE;
        }
        else if (type == TYPE_TREASURE_REACTION) {
            return TREASURE_REACTION_IMAGE;
        }
        else if (isTreasure()) {
            return TREASURE_COLOR;
        }
        else if (isVictory()) {
            return VICTORY_COLOR;
        }
        else if (type == TYPE_ACTION_REACTION) {
            return ACTION_REACTION_COLOR;
        }
        else if (type == TYPE_CURSE) {
            return CURSE_COLOR;
        }
        else if (type == TYPE_ACTION_DURATION) {
            return ACTION_DURATION_COLOR;
        }
        else if (type == TYPE_LEADER) {
            return LEADER_COLOR;
        }
        else {
            return ACTION_COLOR;
        }
    }

    public boolean isAutoPlayTreasure() {
        return !name.equals("Bank") && !name.equals("Venture") && !name.equals("Contraband") && !name.equals("Loan") && !name.equals("Horn of Plenty") && !name.equals("Talisman") && !name.equals("Diadem") && !name.equals("Storybook") && !name.equals("Ill-Gotten Gains") && !name.equals("Fool's Gold");
    }

    public List<Card> getAssociatedCards() {
        return associatedCards;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isAutoSelect() {
        return autoSelect;
    }

    public void setAutoSelect(boolean autoSelect) {
        this.autoSelect = autoSelect;
    }

    public boolean isFanExpansionCard() {
        return fanExpansionCard;
    }

    public void setFanExpansionCard(boolean fanExpansionCard) {
        this.fanExpansionCard = fanExpansionCard;
    }

    public int getSins() {
        return sins;
    }

    public void setSins(int sins) {
        this.sins = sins;
    }

    public boolean isTrashingCard() {
        return name.equals("Chapel") || name.equals("Mine") || name.equals("Moneylender") || name.equals("Remodel")
                || name.equals("Masquerade") || name.equals("Steward") || name.equals("Trading Post")
                || name.equals("Upgrade") || name.equals("Ambassador") || name.equals("Island")
                || name.equals("Lookout") || name.equals("Salvager") || name.equals("Apprentice")
                || name.equals("Transmute") || name.equals("Bishop") || name.equals("Expand") || name.equals("Forge")
                || name.equals("Loan") || name.equals("Trade Route") || name.equals("Remake") || name.equals("Develop")
                || name.equals("Jack of all Trades") || name.equals("Spice Merchant") || name.equals("Trader");
    }

    public boolean isExtraActionsCard() {
        return addActions >= 2 || name.equals("Throne Room") || name.equals("King's Court");
    }

    public boolean isVictoryCoinsCard() {
        return addVictoryCoins > 0 || name.equals("Goons");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return cardId == card.cardId;
    }

    @Override
    public int hashCode() {
        int result = cardId;
        result = 31 * result + type;
        return result;
    }

    public int getActionValue() {
        if (isAction()) {
            return 1;
        }
        else if (isTreasure()) {
            return 2;
        }
        else if (isVictory()) {
            return 3;
        }
        else if (isCurseOnly()) {
            return 4;
        }
        return 9;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getNameLines() {
        return nameLines;
    }

    public void setNameLines(int nameLines) {
        this.nameLines = nameLines;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isCopied() {
        return copied;
    }

    public void setCopied(boolean copied) {
        this.copied = copied;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public Map<String, CardAction> getGainCardActions() {
        return gainCardActions;
    }

    public void setGainCardActions(Map<String, CardAction> gainCardActions) {
        this.gainCardActions = gainCardActions;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isTraderProcessed() {
        return traderProcessed;
    }

    public void setTraderProcessed(boolean traderProcessed) {
        this.traderProcessed = traderProcessed;
    }

    public boolean isCardNotGained() {
        return cardNotGained;
    }

    public void setCardNotGained(boolean cardNotGained) {
        this.cardNotGained = cardNotGained;
    }

    public int getFruitTokens() {
        return fruitTokens;
    }

    public void setFruitTokens(int fruitTokens) {
        this.fruitTokens = fruitTokens;
    }

    public int getCattleTokens() {
        return cattleTokens;
    }

    public void setCattleTokens(int cattleTokens) {
        this.cattleTokens = cattleTokens;
    }

    public boolean isGainedFromBuy() {
        return gainedFromBuy;
    }

    public void setGainedFromBuy(boolean gainedFromBuy) {
        this.gainedFromBuy = gainedFromBuy;
    }
}
