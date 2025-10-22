package com.sbbc.rch.realchatapp.services;

import com.sbbc.rch.realchatapp.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public void setUserOnlineStatus(String username, boolean isOnline) {
        userRepository.updateUserOnlineStatus(username,isOnline);

    }
}
