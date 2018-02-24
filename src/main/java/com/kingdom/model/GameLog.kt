package com.kingdom.model


import javax.persistence.*

@Table(name = "game_log")
@Entity
class GameLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "logid")
    var logId: Int = 0

    @Column(name = "gameid")
    var gameId: Int = 0

    var log: String? = null
}
