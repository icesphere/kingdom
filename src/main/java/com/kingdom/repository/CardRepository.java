package com.kingdom.repository;

import com.kingdom.model.Card;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CardRepository extends CrudRepository<Card, Integer> {
    List<Card> findByPrizeCardOrderByNameAsc(boolean prizeCard);

    Card findByName(String name);

    List<Card> findByDeckStringAndTestingAndDisabledAndPrizeCardOrderByNameAsc(String deckString, boolean testing, boolean disabled, boolean prizeCard);

    List<Card> findByDeckStringAndPrizeCardOrderByNameAsc(String deckString, boolean prizeCard);

    List<Card> findByFanExpansionCardAndDisabledAndPrizeCardOrderByNameAsc(boolean fanExpansionCard, boolean disabled, boolean prizeCard);

}
