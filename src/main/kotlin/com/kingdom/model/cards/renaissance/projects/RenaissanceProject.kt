package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.Project

abstract class RenaissanceProject(name: String, cost: Int) : Project(name, Deck.Renaissance, cost)