package org.example.service;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        boolean usernameExists = existsByUsername(user.getUsername());
        if (usernameExists) {
            throw new IllegalArgumentException("Username already exists");
        }

        boolean emailExists = existsByEmail(user.getEmail());
        if (emailExists) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
        }

        String rawPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);

        User savedUser = userRepository.save(user);
        return savedUser;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional;
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional;
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return allUsers;
    }

    public User updateUser(User user) {
        boolean userExists = userRepository.existsById(user.getId());
        if (!userExists) {
            throw new IllegalArgumentException("User not found");
        }

        String password = user.getPassword();
        if (password != null && !password.isEmpty()) {
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
        } else {
            Optional<User> existingUserOptional = userRepository.findById(user.getId());

            if (existingUserOptional.isPresent()) {
                User existingUser = existingUserOptional.get();

                user.setPassword(existingUser.getPassword());
            }
        }

        User updatedUser = userRepository.save(user);
        return updatedUser;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        boolean exists = userRepository.existsByUsername(username);
        return exists;
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        return exists;
    }
}
