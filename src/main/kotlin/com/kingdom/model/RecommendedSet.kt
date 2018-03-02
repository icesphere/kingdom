package com.kingdom.model

import javax.persistence.*

@Table(name = "recommended_sets")
@Entity
class RecommendedSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0

    var name = ""

    var deck = ""

    var cards = ""
}
