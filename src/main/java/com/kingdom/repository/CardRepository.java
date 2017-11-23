package com.kingdom.repository;

import com.kingdom.model.Card;
import org.springframework.data.repository.CrudRepository;

public interface CardRepository extends CrudRepository<Card, Integer> {
}
