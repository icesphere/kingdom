package com.kingdom.service;

import com.kingdom.model.Game;
import com.kingdom.model.GameRoom;
import com.kingdom.util.GameRoomComparator;

import java.util.*;

public class GameRoomManager {

    private static final int MAX_GAME_ROOMS = 20;

    private Map<Integer, Game> games = new HashMap<Integer, Game>();

    private boolean updatingWebsite;
    private String updatingMessage;

    private boolean showNews;
    private String news = "";

    private static GameRoomManager ourInstance = new GameRoomManager();

    public static GameRoomManager getInstance() {
        return ourInstance;
    }

    private GameRoomManager() {
    }

    public Game getGame(int gameId) {
        return games.get(gameId);
    }

    public Game getNextAvailableGame() {
        if (games.size() >= MAX_GAME_ROOMS) {
            return null;
        }
        int i = 1;
        while (games.get(i) != null) {
            i++;
        }
        Game game = new Game(i);
        game.setStatus(Game.STATUS_GAME_BEING_CONFIGURED);
        games.put(i, game);
        return game;
    }

    public List<GameRoom> getLobbyGameRooms() {
        return getGameRooms(true);
    }

    public List<GameRoom> getGamesInProgress() {
        return getGameRooms(false);
    }

    private List<GameRoom> getGameRooms(boolean lobbyGameRooms) {
        List<GameRoom> gameRooms = new ArrayList<GameRoom>();
        List<Game> currentGames = new ArrayList<Game>(games.values());
        for (Game game : currentGames) {
            checkLastActivity(game);
            boolean addGame = false;
            if (lobbyGameRooms && (game.getStatus() == Game.STATUS_GAME_BEING_CONFIGURED || game.getStatus() == Game.STATUS_GAME_WAITING_FOR_PLAYERS)) {
                addGame = true;
            } else if (!lobbyGameRooms && game.getStatus() == Game.STATUS_GAME_IN_PROGRESS) {
                addGame = true;
            }
            if (addGame) {
                GameRoom gameRoom = new GameRoom();
                gameRoom.setName("Game Room " + game.getGameId());
                gameRoom.setGameId(game.getGameId());
                gameRoom.setGame(game);
                gameRooms.add(gameRoom);
            }
            if (game.getStatus() == Game.STATUS_NO_GAMES) {
                games.remove(game.getGameId());
            }
        }
        GameRoomComparator grc = new GameRoomComparator();
        Collections.sort(gameRooms, grc);
        return gameRooms;
    }

    private void checkLastActivity(Game game) {
        int minute = 60000;
        long now = System.currentTimeMillis();
        boolean resetGame = false;
        if (game.getStatus() == Game.STATUS_GAME_BEING_CONFIGURED) {
            if (now - (15 * minute) > game.getLastActivity().getTime()) {
                resetGame = true;
            }
        } else if (game.getStatus() == Game.STATUS_GAME_WAITING_FOR_PLAYERS) {
            if (now - (15 * minute) > game.getLastActivity().getTime()) {
                game.addGameChat("This game was reset due to inactivity.");
                resetGame = true;
            }
        } else if (game.getStatus() == Game.STATUS_GAME_IN_PROGRESS) {
            if (now - (30 * minute) > game.getLastActivity().getTime()) {
                game.addGameChat("This game was reset due to inactivity.");
                resetGame = true;
            }
        } else if (game.getStatus() == Game.STATUS_GAME_FINISHED) {
            if (now - (2 * minute) > game.getLastActivity().getTime()) {
                game.addGameChat("This game was reset due to inactivity.");
                resetGame = true;
            }
        }

        if (resetGame) {
            if (game.getStatus() == Game.STATUS_GAME_IN_PROGRESS) {
                game.setGameEndReason("Game Abandoned");
                game.setAbandonedGame(true);
                game.saveGameHistory();
            }
            game.reset();
        }
    }

    public boolean maxGameRoomLimitReached() {
        return games.size() >= MAX_GAME_ROOMS;
    }

    public boolean isUpdatingWebsite() {
        return updatingWebsite;
    }

    public void setUpdatingWebsite(boolean updatingWebsite) {
        this.updatingWebsite = updatingWebsite;
    }

    public String getUpdatingMessage() {
        return updatingMessage;
    }

    public void setUpdatingMessage(String updatingMessage) {
        this.updatingMessage = updatingMessage;
    }

    public boolean isShowNews() {
        return showNews;
    }

    public void setShowNews(boolean showNews) {
        this.showNews = showNews;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }
}
