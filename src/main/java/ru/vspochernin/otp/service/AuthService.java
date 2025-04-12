package ru.vspochernin.otp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vspochernin.otp.dto.RegisterRequest;
import ru.vspochernin.otp.exception.AdminAlreadyExistsException;
import ru.vspochernin.otp.exception.UsernameAlreadyExistsException;
import ru.vspochernin.otp.model.User;
import ru.vspochernin.otp.model.UserRole;
import ru.vspochernin.otp.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username is already taken!");
        }

        if (request.getRole() == UserRole.ADMIN && userRepository.existsByRole(UserRole.ADMIN)) {
            throw new AdminAlreadyExistsException("Admin already exists!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : UserRole.USER);
        user.setEmail(request.getEmail());
        user.setTelegram(request.getTelegram());
        user.setPhone(request.getPhone());

        return userRepository.save(user);
    }
} 