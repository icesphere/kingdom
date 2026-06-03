package com.kingdom.model

import java.io.Serializable

class RefreshLobby : Serializable {
    var isRefreshPlayers: Boolean = false
    var isRefreshGameRooms: Boolean = false
    var isRefreshChat: Boolean = false
    var isStartGame: Boolean = false
    var isRedirectToLogin: Boolean = false
}
