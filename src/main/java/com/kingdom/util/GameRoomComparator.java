package com.kingdom.util;

import com.kingdom.model.GameRoom;

import java.util.Comparator;

public class GameRoomComparator implements Comparator<GameRoom> {
    public int compare(GameRoom g1, GameRoom g2) {
        if (g1.getGame().getCreationTime() == null) {
            return -1;
        } else if (g2.getGame().getCreationTime() == null) {
            return 1;
        }
        return g2.getGame().getCreationTime().compareTo(g1.getGame().getCreationTime());
    }
}
