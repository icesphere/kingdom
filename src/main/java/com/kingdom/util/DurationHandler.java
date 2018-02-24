package com.kingdom.util;

import com.kingdom.model.Card;
import com.kingdom.model.Game;
import com.kingdom.model.Player;

public class DurationHandler {
    public static void applyDurationCards(Game game, Player player) {
        int numTimesCardCopied = 0;
        for (Card card : player.getDurationCards()) {
            DurationAction action = null;
            if (card.getName().equals("Caravan")) {
                action = new CaravanDurationAction(game, player);
            } else if (card.getName().equals("Fishing Village")) {
                action = new FishingVillageDurationAction(game, player);
            } else if (card.getName().equals("Haven")) {
                action = new HavenDurationAction(game, player);
            } else if (card.getName().equals("Hedge Wizard")) {
                action = new HedgeWizardDurationAction(game, player);
            } else if (card.getName().equals("Lighthouse")) {
                action = new LighthouseDurationAction(game, player);
            } else if (card.getName().equals("Merchant Ship")) {
                action = new MerchantShipDurationAction(game, player);
            } else if (card.getName().equals("Quest")) {
                action = new QuestDurationAction(game, player, card);
            } else if (card.getName().equals("Tactician") && player.hasTacticianBonus()) {
                player.setTacticianBonus(false);
                player.drawCards(5);
                player.addBuys(1);
                player.addActions(1);
                game.addHistory(player.getUsername(), " gained +5 Cards, +1 Buy, +1 Action from ", KingdomUtil.getWordWithBackgroundColor("Tactician", Card.ACTION_DURATION_COLOR));
            } else if (card.getName().equals("Wharf")) {
                action = new WharfDurationAction(game, player);
            }

            if (action != null) {
                action.apply(0);
                int numTimesCardApplied = 1;
                while (numTimesCardCopied > 0) {
                    numTimesCardCopied--;
                    action.apply(numTimesCardApplied);
                    numTimesCardApplied++;
                }
            }

            if (card.getName().equals("Throne Room")) {
                numTimesCardCopied = 1;
            } else if (card.getName().equals("King's Court")) {
                numTimesCardCopied = 2;
            } else {
                numTimesCardCopied = 0;
            }
        }
    }

    interface DurationAction {
        void apply(int numTimesApplied);
    }

    static class CaravanDurationAction implements DurationAction {
        private Game game;
        private Player player;

        CaravanDurationAction(Game game, Player player) {
            this.game = game;
            this.player = player;
        }

        public void apply(int numTimesApplied) {
            player.drawCards(1);
            game.addHistory(player.getUsername(), " gained +1 Card from ", KingdomUtil.getWordWithBackgroundColor("Caravan", Card.ACTION_DURATION_COLOR));
        }
    }

    static class FishingVillageDurationAction implements DurationAction {
        private Game game;
        private Player player;

        FishingVillageDurationAction(Game game, Player player) {
            this.game = game;
            this.player = player;
        }

        public void apply(int numTimesApplied) {
            player.addActions(1);
            player.addCoins(1);
            game.addHistory(player.getUsername(), " gained +1 Action, +1 Coin from ", KingdomUtil.getWordWithBackgroundColor("Fishing Village", Card.ACTION_DURATION_COLOR));
        }
    }

    static class HavenDurationAction implements DurationAction {
        private Game game;
        private Player player;

        HavenDurationAction(Game game, Player player) {
            this.game = game;
            this.player = player;
        }

        public void apply(int numTimesApplied) {
            for (Card c : player.getHavenCards()) {
                player.addCardToHand(c);
            }
            player.getHavenCards().clear();
            game.addHistory(player.getUsername(), " added ", KingdomUtil.getWordWithBackgroundColor("Haven", Card.ACTION_DURATION_COLOR), " cards to hand");
        }
    }

    static class HedgeWizardDurationAction implements DurationAction {
        private Game game;
        private Player player;

        HedgeWizardDurationAction(Game game, Player player) {
            this.game = game;
            this.player = player;
        }

        public void apply(int numTimesApplied) {
            player.drawCards(1);
            game.addHistory(player.getUsername(), " gained +1 Card from ", KingdomUtil.getWordWithBackgroundColor("Hedge Wizard", Card.DURATION_AND_VICTORY_IMAGE));
        }
    }

    static class LighthouseDurationAction implements DurationAction {
        private Game game;
        private Player player;

        LighthouseDurationAction(Game game, Player player) {
            this.game = game;
            this.player = player;
        }

        public void apply(int numTimesApplied) {
            player.addCoins(1);
            game.addHistory(player.getUsername(), " gained +1 Coin from ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
        }
    }

    static class MerchantShipDurationAction implements DurationAction {
        private Game game;
        private Player player;

        MerchantShipDurationAction(Game game, Player player) {
            this.game = game;
            this.player = player;
        }

        public void apply(int numTimesApplied) {
            player.addCoins(2);
            game.addHistory(player.getUsername(), " gained +2 Coins from ", KingdomUtil.getWordWithBackgroundColor("Merchant Ship", Card.ACTION_DURATION_COLOR));
        }
    }

    static class QuestDurationAction implements DurationAction {
        private Game game;
        private Player player;
        private Card card;

        QuestDurationAction(Game game, Player player, Card card) {
            this.game = game;
            this.player = player;
            this.card = card;
        }

        public void apply(int numTimesApplied) {
            Card questCard = card.getAssociatedCards().get(numTimesApplied);
            if (numTimesApplied == 0) {
                game.addHistory(player.getUsername(), "'s hand contains ", KingdomUtil.groupCards(player.getHand(), true));
            }
            game.addHistory(player.getUsername(), " was questing for ", KingdomUtil.getCardWithBackgroundColor(questCard));
            if (player.getHand().contains(questCard)) {
                player.drawCards(1);
                player.addActions(1);
                player.addCoins(1);
                game.addHistory(player.getUsername(), " gained +1 Card, +1 Action, +1 Coin for successfully completing the Quest");
            }
        }
    }

    static class WharfDurationAction implements DurationAction {
        private Game game;
        private Player player;

        WharfDurationAction(Game game, Player player) {
            this.game = game;
            this.player = player;
        }

        public void apply(int numTimesApplied) {
            player.drawCards(2);
            player.addBuys(1);
            game.addHistory(player.getUsername(), " gained +2 Cards, +1 Buy from ", KingdomUtil.getWordWithBackgroundColor("Wharf", Card.ACTION_DURATION_COLOR));
        }
    }
}
