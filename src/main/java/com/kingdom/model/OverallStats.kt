package com.kingdom.model

class OverallStats {
    var gamesAgainstComputersPlayed: Int = 0
    var gamesAgainstHumansPlayed: Int = 0
    var gamesAgainstComputersWon: Int = 0

    var gamesAgainstHardComputerPlayed: Int = 0
    var gamesWonByHardComputer: Int = 0

    var gamesAgainstBMUComputerPlayed: Int = 0
    var gamesWonByBMUComputer: Int = 0

    var gamesQuit: Int = 0
    var numUsers: Int = 0
    var newAccountsCreated: Int = 0
    var newUsersWithGamePlayed: Int = 0
    var testGames: Int = 0
    var gamesAbandoned: Int = 0
    var showVictoryPointsGames: Int = 0
    var identicalStartingHandsGames: Int = 0
    var repeatedGames: Int = 0
    var mobileGames: Int = 0
    var gamesWithLeaders: Int = 0
    var annotatedGames: Int = 0
    var recentGames: Int = 0
    var recommendedSets: Int = 0

    val gamesPlayed: Int
        get() = gamesAgainstComputersPlayed + gamesAgainstHumansPlayed
}
