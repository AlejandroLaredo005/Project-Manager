package com.alejandolaredo.projectmanager.service;

import com.alejandolaredo.projectmanager.model.ProjectMember;
import com.alejandolaredo.projectmanager.model.Task;
import com.alejandolaredo.projectmanager.model.User;
import com.alejandolaredo.projectmanager.repository.ProjectMemberRepository;
import com.alejandolaredo.projectmanager.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       ProjectMemberRepository projectMemberRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(String email, String password, String name) {
        // Validamos que los datos no sean nulos
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Los campos no pueden estar vacíos ni ser nulos");
        }

        // Si ha puesto espacios o mayúsculas en el email se los quitamos
        email = email.trim().toLowerCase();

        // Comprobamos que no exista ya un usuario con ese email
        if (userRepository.existsByEmail(email)) {
            throw new EntityExistsException("Ya existe un usuario con ese email");
        }

        String encodedPassword = passwordEncoder.encode(password);

        // Creamos el usuario
        User newUser = new User(email, encodedPassword, name);

        userRepository.save(newUser);

        return newUser;
    }

    @Transactional
    public User updateUsername(Long userId, String newUsername) {
        // Validamos que el nombre no sea nulo ni este vacío
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío ni ser nulo");
        }

        // Comprobamos que el usuario exista
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("No existe un usuario con esa id"));

        // Comprobamos que el usuario no esté eliminado
        if (user.isDeleted()) {
            throw new IllegalStateException("El usuario está marcado como eliminado");
        }

        // Comprobamos que el usuario no tuviera ya ese nombre previamente
        if (user.getName().equals(newUsername)) {
            throw new IllegalArgumentException("El usuario ya tenía ese nombre");
        }

        // Actualizamos su nombre
        user.setName(newUsername);

        userRepository.save(user);

        return user;
    }

    @Transactional
    public User deleteUser(Long userId) {
        // Comprobamos que el usuario exista
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("No existe un usuario con esa id"));

        // Comprobamos que no esté ya eliminado
        if (user.isDeleted()) {
            throw new IllegalStateException("El usuario ya estaba eliminado");
        }

        // Comprobamos que el usuario no sea owner de ningún proyecto
        if (user.getOwnedProjects() != null && !user.getOwnedProjects().isEmpty()) {
            throw new IllegalArgumentException("El usuario es propietario de algún proyecto");
        }

        // Hallamos las membresías del usuario
        Set<ProjectMember> memberships = new HashSet<>(user.getProjectMemberships());

        // Eliminamos todas las membresías del usuario y desAsignamos sus tareas
        for (ProjectMember membership : memberships) {
            // DesAsignamos sus tareas
            for (Task task : membership.getAssignedTasks()) {
                task.setAssignee(null);
            }
            // Eliminamos la membresía
            projectMemberRepository.delete(membership);
        }

        user.setDeleted(true);

        userRepository.save(user);

        return user;
    }
}
