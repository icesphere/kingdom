package com.kingdom.model

import java.util.*
import javax.persistence.*

@Table(name = "games")
@Entity
class GameHistory {

    @Id
    @Column(name = "gameid")
    lateinit var gameId: String

    @Column(name = "start_date")
    var startDate: Date? = null

    @Column(name = "end_date")
    var endDate: Date? = null

    @Column(name = "num_players")
    var numPlayers: Int = 0

    @Column(name = "num_computer_players")
    var numComputerPlayers: Int = 0

    var cards: String? = null

    var custom: Boolean = false

    @Column(name = "game_end_reason")
    var gameEndReason: String? = null

    @Column(name = "test_game")
    var testGame: Boolean = false

    @Column(name = "abandoned_game")
    var abandonedGame: Boolean = false

    var winner = ""

    @Column(name = "show_victory_points")
    var showVictoryPoints: Boolean = false

    @Column(name = "identical_starting_hands")
    var identicalStartingHands: Boolean = false

    var repeated: Boolean = false

    var mobile: Boolean = false

    @Column(name = "recent_game")
    var recentGame: Boolean = false

    @Column(name = "recommended_set")
    var recommendedSet: Boolean = false
}
