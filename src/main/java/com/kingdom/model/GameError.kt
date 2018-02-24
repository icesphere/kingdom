package com.kingdom.model

import java.util.*
import javax.persistence.*

@Table(name = "errors")
@Entity
class GameError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "errorid")
    var errorId: Int = 0

    var type: Int = 0

    var error: String? = null

    var date: Date? = null

    var history: String? = null

    val computerError: Boolean
        get() = type == COMPUTER_ERROR

    constructor()

    constructor(type: Int, error: String) {
        this.type = type
        this.error = error
        this.date = Date()
    }

    companion object {

        const val COMPUTER_ERROR = 1
        const val GAME_ERROR = 2
    }
}
