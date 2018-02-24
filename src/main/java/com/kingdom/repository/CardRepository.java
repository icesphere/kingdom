package com.kingdom.repository;

import com.kingdom.model.Card;
import com.kingdom.model.Deck;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CardRepository extends CrudRepository<Card, Integer> {
    List<Card> findByPrizeCardOrderByNameAsc(boolean prizeCard);

    Card findByName(String name);

    List<Card> findByDeckAndTestingAndDisabledAndPrizeCardOrderByNameAsc(Deck deck, boolean testing, boolean disabled, boolean prizeCard);

    List<Card> findByDeckAndPrizeCardOrderByNameAsc(Deck deck, boolean prizeCard);

    List<Card> findByFanExpansionCardAndDisabledAndPrizeCardOrderByNameAsc(boolean fanExpansionCard, boolean disabled, boolean prizeCard);

}
