package com.alejandolaredo.projectmanager.service;

import com.alejandolaredo.projectmanager.dto.response.AuthResponse;
import com.alejandolaredo.projectmanager.model.User;
import com.alejandolaredo.projectmanager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(String email, String password) {
        // Comprobamos que el email y contraseña no sean nulos ni vacíos
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Los campos no pueden estar vacíos ni ser nulos");
        }

        // Comprobamos si existe el usuario con ese email
        email = email.trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email o contraseña incorrectos"));

        // Comprobamos que el usuario no esté eliminado
        if (user.isDeleted()) {
            throw new IllegalArgumentException("Email o contraseña incorrectos");
        }

        // Comprobamos si la contraseña es correcta o no
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Email o contraseña incorrectos");
        }

        // Generamos el token
        String token = jwtService.generateToken(user.getEmail());

        // Devolvemos el token
        return new AuthResponse(token);
    }
}
