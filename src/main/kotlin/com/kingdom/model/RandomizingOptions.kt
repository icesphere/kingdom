package com.kingdom.model

import com.kingdom.model.cards.*
import java.util.ArrayList

class RandomizingOptions {
    var isOneOfEachCost: Boolean = false
    var isOneWithBuy: Boolean = false
    var isOneWithActions: Boolean = false
    var isDefenseForAttack: Boolean = false
    var customCardSelection: List<Card> = ArrayList(0)
    var numEventsAndLandmarksAndProjectsAndWays: Int = 2
    var customEventSelection: List<Event> = ArrayList(0)
    var customLandmarkSelection: List<Landmark> = ArrayList(0)
    var customProjectSelection: List<Project> = ArrayList(0)
    var customWaySelection: List<Way> = ArrayList(0)
    var customTraitSelection: List<Trait> = ArrayList(0)
    var isIncludeEvents: Boolean = true
    var isIncludeLandmarks: Boolean = true
    var isIncludeProjects: Boolean = true
    var isIncludeWays: Boolean = true
    var isIncludeTraits: Boolean = true
    var excludedCards: List<Card> = ArrayList(0)
    var isSwappingCard: Boolean = false
    var cardToReplaceIndex: Int = 0
    var cardToReplace: Card? = null
}
