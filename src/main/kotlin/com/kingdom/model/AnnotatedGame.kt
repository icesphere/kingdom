package com.kingdom.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "annotated_games")
@Entity
class AnnotatedGame {

    @Id
    @Column(name = "gameid")
    var gameId: Int = 0

    var title = ""

    var cards = ""

    @Column(name = "include_colony_and_platinum")
    var includeColonyAndPlatinum: Boolean = false
}
