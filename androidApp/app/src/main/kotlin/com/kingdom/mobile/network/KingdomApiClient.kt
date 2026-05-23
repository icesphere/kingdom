package com.kingdom.mobile.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KingdomApiClient(
    private val baseUrl: String,
    private val client: HttpClient = defaultClient()
) {
    suspend fun access(password: String) {
        client.post("$baseUrl/api/access") {
            contentType(ContentType.Application.Json)
            setBody(AccessRequest(password))
        }
    }

    suspend fun login(username: String): UserDto =
        client.post("$baseUrl/api/session") {
            contentType(ContentType.Application.Json)
            setBody(SessionRequest(username))
        }.body()

    suspend fun logout() {
        client.post("$baseUrl/api/logout")
    }

    suspend fun catalog(): CatalogDto = client.get("$baseUrl/api/catalog").body()

    suspend fun lobby(): LobbySnapshotDto = client.get("$baseUrl/api/lobby").body()

    suspend fun sendLobbyChat(message: String): LobbySnapshotDto =
        client.post("$baseUrl/api/lobby/chat") {
            contentType(ContentType.Application.Json)
            setBody(ChatRequest(message))
        }.body()

    suspend fun createGame(request: CreateGameRequest): GameSnapshotDto =
        client.post("$baseUrl/api/games") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun joinGame(gameId: String, password: String? = null): GameSnapshotDto =
        client.post("$baseUrl/api/games/$gameId/join") {
            contentType(ContentType.Application.Json)
            setBody(JoinGameRequest(password))
        }.body()

    suspend fun leaveGame(gameId: String): LobbySnapshotDto = client.post("$baseUrl/api/games/$gameId/leave").body()

    suspend fun cancelGame(gameId: String): LobbySnapshotDto = client.post("$baseUrl/api/games/$gameId/cancel").body()

    suspend fun snapshot(gameId: String): GameSnapshotDto = client.get("$baseUrl/api/games/$gameId/snapshot").body()

    suspend fun command(gameId: String, request: GameCommandRequest): GameSnapshotDto =
        client.post("$baseUrl/api/games/$gameId/commands") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun sendGameChat(gameId: String, message: String): GameSnapshotDto =
        client.post("$baseUrl/api/games/$gameId/chat") {
            contentType(ContentType.Application.Json)
            setBody(ChatRequest(message))
        }.body()

    suspend fun results(gameId: String): GameSnapshotDto = client.get("$baseUrl/api/games/$gameId/results").body()

    companion object {
        fun defaultClient(): HttpClient = HttpClient(Android) {
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }
        }
    }
}
