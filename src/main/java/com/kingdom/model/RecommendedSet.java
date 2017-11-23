package com.kingdom.model;

import javax.persistence.*;

@Table(name = "recommended_sets")
@Entity
public class RecommendedSet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name = "";

    private String deck = "";

    private String cards = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeck() {
        return deck;
    }

    public void setDeck(String deck) {
        this.deck = deck;
    }

    public String getCards() {
        return cards;
    }

    public void setCards(String cards) {
        this.cards = cards;
    }
}
