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
        return dao.getUsers();
	}

    public List<User> getUsers(String stat, Integer value) {
        return dao.getUsers(stat, value);
    }

    public User getUser(int userId){
        return dao.getUser(userId);
    }

    public User getUser(String username, String password){
        return dao.getUser(username, password);
    }

    public User getUser(String username) {
        return dao.getUser(username);
    }

    public User getUserByEmail(String email) {
        return dao.getUserByEmail(email);
    }

    public boolean usernameExists(String username) {
        return dao.usernameExists(username);
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
