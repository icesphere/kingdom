package com.kingdom.model

import com.kingdom.model.players.Player
import com.kingdom.util.KingdomUtil

import javax.persistence.*

@Table(name = "game_users")
@Entity
class GameUserHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gameuserid")
    var gameUserId: Int = 0

    @Column(name = "gameid")
    lateinit var gameId: String

    @Column(name = "userid")
    var userId: Int = 0

    var points: Int = 0

    var winner: Boolean = false

    var quit: Boolean = false

    var cards = ""

    @Column(name = "victory_coins")
    var victoryCoins: Int = 0

    var turns: Int = 0

    @Column(name = "margin_of_victory")
    var marginOfVictory: Int = 0

    @Transient
    var username = ""

    constructor()

    constructor(gameId: String, player: Player) {
        this.gameId = gameId
        this.userId = player.userId
        this.points = player.victoryPoints
        this.winner = player.isWinner
        this.quit = player.isQuit
        this.cards = KingdomUtil.groupCards(player.allCards, false)
        this.victoryCoins = player.victoryCoins
        this.turns = player.turns
        this.marginOfVictory = player.marginOfVictory
    }
}
