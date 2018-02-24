package com.kingdom.model

class PlayerStats {

    var gamesWon: Int = 0
    var gamesLost: Int = 0
    var gamesQuit: Int = 0
    var averageMarginOfVictory: Double = 0.toDouble()

    var gamesAgainstComputerWon: Int = 0
    var gamesAgainstComputerLost: Int = 0
    var gamesAgainstComputerQuit: Int = 0
    var testGames: Int = 0
    var averageMarginOfVictoryAgainstComputer: Double = 0.toDouble()

    val gamesPlayed: Int
        get() = gamesWon + gamesLost + gamesQuit

    val gamesAgainstComputerPlayed: Int
        get() = gamesAgainstComputerWon + gamesAgainstComputerLost + gamesAgainstComputerQuit
}
