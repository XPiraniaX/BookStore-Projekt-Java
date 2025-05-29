package org.example.service;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public abstract class AbstractUserService {

    protected UserRepository userRepository;
    protected PasswordEncoder passwordEncoder;

    public AbstractUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public abstract User registerUser(User user);

    @Transactional(readOnly = true)
    public abstract Optional<User> findByUsername(String username);

    @Transactional(readOnly = true)
    public abstract Optional<User> findById(Long id);

    @Transactional(readOnly = true)
    public abstract List<User> findAllUsers();

    public abstract User updateUser(User user);

    public abstract void deleteUser(Long id);

    @Transactional(readOnly = true)
    public abstract boolean existsByUsername(String username);

    @Transactional(readOnly = true)
    public abstract boolean existsByEmail(String email);
}