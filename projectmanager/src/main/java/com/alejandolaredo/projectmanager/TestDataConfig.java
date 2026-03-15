package com.alejandolaredo.projectmanager;

import com.alejandolaredo.projectmanager.model.Project;
import com.alejandolaredo.projectmanager.model.ProjectMember;
import com.alejandolaredo.projectmanager.model.ProjectRole;
import com.alejandolaredo.projectmanager.model.User;
import com.alejandolaredo.projectmanager.repository.ProjectMemberRepository;
import com.alejandolaredo.projectmanager.repository.ProjectRepository;
import com.alejandolaredo.projectmanager.repository.UserRepository;
import com.alejandolaredo.projectmanager.service.ProjectService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestDataConfig {

    @Bean
    CommandLineRunner testUser(UserRepository userRepository,
                               ProjectRepository projectRepository,
                               ProjectService projectService,
                               ProjectMemberRepository projectMemberRepository) {
        return args -> {
            // Crear usuario si no existe
            User user;
            if (!userRepository.existsByEmail("test@test.com")) {
                user = new User();
                user.setEmail("test@test.com");
                user.setPassword("passwordprueba");
                user.setName("nombreprueba");

                userRepository.save(user);

                System.out.println("Usuario creado: " + user);
            } else {
                user = userRepository.findByEmail("test@test.com").get();
                System.out.println("Usuario ya existe: " + user);
            }

            User user2 = new User("email2@gmail.com", "password2", "nombre2");
            userRepository.save(user2);
            System.out.println("Usuario creado: " + user2);

            // Crear proyecto si no existe
            Project project;
            if (projectRepository.count() == 0) {
                String name = "Proyecto de Prueba";
                String description = "Este es un proyecto de prueba";

                project = projectService.createProject(name, description, user2.getId());

                System.out.println("Proyecto creado: " + project.getName());
            } else {
                project = projectRepository.findAll().get(0);
                System.out.println("Proyecto ya existe: " + project.getName());
            }

            ProjectMember newProjectMember = projectService.addUserToProject(user2.getId(),
                    user.getId(), project.getId(), ProjectRole.MEMBER);

            System.out.println("Miembro añadido al proyecto " + project.getName() + ": " + newProjectMember);

            // projectService.deleteProject(user2.getId(), project.getId());
            // projectService.leaveProject(user.getId(), project.getId());
            // projectService.leaveProject(user2.getId(), project.getId());

            // projectService.removeUserFromProject(user2.getId(), user.getId(), project.getId());
            // projectService.removeUserFromProject(user.getId(), user2.getId(), project.getId());

            // projectService.changeUserRole(user2.getId(), user.getId(), project.getId(), ProjectRole.MEMBER);
            // projectService.changeUserRole(user.getId(), user2.getId(), project.getId(), ProjectRole.MEMBER);
            // projectService.changeUserRole(user2.getId(), user.getId(), project.getId(), ProjectRole.ADMIN);
            // projectService.changeUserRole(user.getId(), user2.getId(), project.getId(), ProjectRole.MEMBER);

            // projectService.transferOwnership(user2.getId(), user.getId(), project.getId());
            // projectService.removeUserFromProject(user.getId(), user2.getId(), project.getId());

            // Hasta aquí todo bien
        };
    }
}
