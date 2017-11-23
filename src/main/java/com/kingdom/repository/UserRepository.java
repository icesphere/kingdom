package com.kingdom.repository;

import com.kingdom.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {

    List<User> findAllByOrderByLastLogin();

    User findByEmail(String email);

    User findByUsername(String username);

    User findByUsernameAndPassword(String username, String password);
}
