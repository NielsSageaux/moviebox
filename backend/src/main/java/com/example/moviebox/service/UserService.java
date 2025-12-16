package com.example.moviebox.service;

import com.example.moviebox.model.User;
import com.example.moviebox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User getDefaultUser() {
        // Récupère ou crée un utilisateur par défaut (pour les tests)
        return userRepository.findById(1L)
                .orElseGet(() -> {
                    // Respecte les contraintes NOT NULL et d'unicité
                    User defaultUser = new User("demo", "demo@moviebox.com", "demo");
                    return userRepository.save(defaultUser);
                });
    }
}
