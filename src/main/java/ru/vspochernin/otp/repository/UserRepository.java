package ru.vspochernin.otp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vspochernin.otp.model.User;
import ru.vspochernin.otp.model.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByRole(UserRole role);
    List<User> findAllByRoleNot(String role);
}