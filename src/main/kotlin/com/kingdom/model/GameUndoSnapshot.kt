package com.kingdom.model

import com.kingdom.model.players.Player
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class GameUndoSnapshot private constructor(private val bytes: ByteArray) : Serializable {

    fun matches(game: Game): Boolean {
        return try {
            bytes.contentEquals(serialize(game))
        } catch (_: Throwable) {
            false
        }
    }

    fun restoreInto(game: Game) {
        val restoredGame = deserialize(bytes)

        copySerializableFields(restoredGame, game)
        rewireRestoredPlayers(game)
    }

    companion object {
        fun capture(game: Game): GameUndoSnapshot {
            return GameUndoSnapshot(serialize(game))
        }

        private fun serialize(game: Game): ByteArray {
            val output = ByteArrayOutputStream()
            ObjectOutputStream(output).use { it.writeObject(game) }
            return output.toByteArray()
        }

        private fun deserialize(bytes: ByteArray): Game {
            return ObjectInputStream(ByteArrayInputStream(bytes)).use { it.readObject() as Game }
        }

        private fun copySerializableFields(source: Game, target: Game) {
            allFields(Game::class.java)
                    .filterNot { Modifier.isStatic(it.modifiers) }
                    .filterNot { Modifier.isTransient(it.modifiers) }
                    .forEach { field ->
                        field.isAccessible = true
                        field.set(target, field.get(source))
                    }
        }

        private fun rewireRestoredPlayers(game: Game) {
            game.players.forEach { setPlayerGame(it, game) }
            game.playerMap.clear()
            game.players.forEach { game.playerMap[it.userId] = it }
        }

        private fun setPlayerGame(player: Player, game: Game) {
            val field = allFields(player.javaClass).first { it.name == "game" }
            field.isAccessible = true
            field.set(player, game)
        }

        private fun allFields(type: Class<*>): List<Field> {
            val fields = mutableListOf<Field>()
            var current: Class<*>? = type
            while (current != null && current != Any::class.java) {
                fields.addAll(current.declaredFields)
                current = current.superclass
            }
            return fields
        }
    }
}

data class PendingUndoCommand(
        val actorUserId: String,
        val actorUsername: String,
        val summary: String,
        val snapshot: GameUndoSnapshot,
        var revealedSharedInfo: Boolean = false
)

data class LastUndoableCommand(
        val actorUserId: String,
        val actorUsername: String,
        val summary: String,
        val snapshot: GameUndoSnapshot,
        val revealedSharedInfo: Boolean
)

data class UndoApprovalRequest(
        val actorUserId: String,
        val actorUsername: String,
        val summary: String,
        val snapshot: GameUndoSnapshot,
        val pendingApproverUserIds: MutableSet<String>,
        val approvedUserIds: MutableSet<String> = mutableSetOf()
)
