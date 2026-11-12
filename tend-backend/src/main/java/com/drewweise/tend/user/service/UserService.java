
package com.drewweise.tend.user.service;

import com.drewweise.tend.user.model.User;
import com.drewweise.tend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User getOrCreateUser(String cognitoUserId) {
        return userRepository.findByCognitoUserId(cognitoUserId)
                .orElseGet(() ->
                        userRepository.save(new User(cognitoUserId))
                );
    }
}
