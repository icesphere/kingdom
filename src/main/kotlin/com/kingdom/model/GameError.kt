package com.kingdom.model

import java.util.*

class GameError(var type: Int, error: String) {

    var error: String? = error

    var date: Date? = null

    var history: String? = null

    val computerError: Boolean
        get() = type == COMPUTER_ERROR

    companion object {
        const val COMPUTER_ERROR = 1
        const val GAME_ERROR = 2
    }

    init {
        this.date = Date()
    }
}
