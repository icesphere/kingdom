package com.kingdom.service;

import com.kingdom.model.User;
import com.kingdom.repository.UserDao;
import com.kingdom.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManager {   

    private UserDao dao;
    private UserRepository userRepository;

    public UserManager(UserDao dao, UserRepository userRepository) {
        this.dao = dao;
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return userRepository.findAllByOrderByLastLogin();
	}

    public List<User> getUsers(String stat, Integer value) {
        return dao.getUsers(stat, value);
    }

    public User getUser(int userId){
        return userRepository.findOne(userId);
    }

    public User getUser(String username, String password){
        return userRepository.findByUsernameAndPassword(username, password);
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    public void deleteUser(User user){
        userRepository.delete(user);
    }

    public void calculateGameStats(User user) {
        if (user.getStats() == null) {
            dao.calculateGameStats(user);
        }
    }

    public int getErrorCount() {
        return dao.getErrorCount();
    }
}
