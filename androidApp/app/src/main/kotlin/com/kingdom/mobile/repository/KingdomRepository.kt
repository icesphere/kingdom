package com.kingdom.mobile.repository

import com.kingdom.mobile.network.CreateGameRequest
import com.kingdom.mobile.network.GameCommandRequest
import com.kingdom.mobile.network.KingdomApiClient

class KingdomRepository(private val api: KingdomApiClient) {
    suspend fun access(password: String) = api.access(password)
    suspend fun login(username: String) = api.login(username)
    suspend fun catalog() = api.catalog()
    suspend fun lobby() = api.lobby()
    suspend fun sendLobbyChat(message: String) = api.sendLobbyChat(message)
    suspend fun createGame(request: CreateGameRequest) = api.createGame(request)
    suspend fun joinGame(gameId: String, password: String? = null) = api.joinGame(gameId, password)
    suspend fun snapshot(gameId: String) = api.snapshot(gameId)
    suspend fun command(gameId: String, request: GameCommandRequest) = api.command(gameId, request)
    suspend fun sendGameChat(gameId: String, message: String) = api.sendGameChat(gameId, message)
    suspend fun leaveGame(gameId: String) = api.leaveGame(gameId)
    suspend fun cancelGame(gameId: String) = api.cancelGame(gameId)
}
