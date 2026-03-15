package com.alejandolaredo.projectmanager.service;

import com.alejandolaredo.projectmanager.model.*;
import com.alejandolaredo.projectmanager.repository.ProjectMemberRepository;
import com.alejandolaredo.projectmanager.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {

        @Test
        void createUser_shouldThrowException_whenEmailIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    userService.createUser(null, "password", "name"));
        }

        @Test
        void createUser_shouldThrowException_whenPasswordIsEmpty() {
            assertThrows(IllegalArgumentException.class, () ->
                    userService.createUser("email", "", "name"));
        }

        @Test
        void createUser_shouldThrowException_whenNameIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    userService.createUser("email", "password", null));
        }

        @Test
        void createUser_shouldThrowException_whenUserAlreadyExists() {
            when(userRepository.existsByEmail("email")).thenReturn(true);

            assertThrows(EntityExistsException.class, () ->
                    userService.createUser("email", "password", "name"));
        }

        @Test
        void createUser_shouldCreateUser_whenUserDoesNotExists() {
            when(userRepository.existsByEmail("email")).thenReturn(false);

            User newUser = userService.createUser("EmAil", "password", "name");

            assertEquals("email", newUser.getEmail());

            verify(userRepository).save(newUser);
        }
    }

    @Nested
    @DisplayName("Update Username Tests")
    class UpdateUsernameTests {

        @Test
        void updateUsername_shouldThrowException_whenNewUsernameIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    userService.updateUsername(1L, null));
        }

        @Test
        void updateUsername_shouldThrowException_whenUserDoesNotExists() {
            assertThrows(EntityNotFoundException.class, () ->
                    userService.updateUsername(1L, "newUsername"));
        }

        @Test
        void updateUsername_shouldThrowException_whenUserIsDeleted() {
            User userToUpdate = createUser();
            userToUpdate.setDeleted(true);

            when(userRepository.findById(1L)).thenReturn(Optional.of(userToUpdate));

            assertThrows(IllegalArgumentException.class, () ->
                    userService.updateUsername(1L, "newUsername"));
        }

        @Test
        void updateUsername_shouldThrowException_whenNewUsernameIsEqualsToCurrentUsername() {
            User userToUpdate = createUser();

            when(userRepository.findById(1L)).thenReturn(Optional.of(userToUpdate));

            assertThrows(IllegalArgumentException.class, () ->
                    userService.updateUsername(1L, "name"));
        }

        @Test
        void updateUsername_shouldUpdateUser_whenNewUsernameIsCorrectAndUserIsNotDeleted() {
            User userToUpdate = createUser();

            when(userRepository.findById(1L)).thenReturn(Optional.of(userToUpdate));

            User userUpdated = userService.updateUsername(1L, "newUsername");

            assertEquals("newUsername", userUpdated.getName());

            verify(userRepository).save(userUpdated);
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        void deleteUser_shouldThrowException_whenUserHasOwnedProjects() {
            User userToDelete = createUser();

            Project project = new Project("name", "description");
            userToDelete.addOwnedProject(project);

            when(userRepository.findById(1L)).thenReturn(Optional.of(userToDelete));

            assertThrows(IllegalArgumentException.class, () ->
                    userService.deleteUser(1L));
        }

        @Test
        void deleteUser_shouldDeleteUser_whenUserWasDeletedBefore() {
            User userToDelete = createUser();
            userToDelete.setDeleted(true);

            when(userRepository.findById(1L)).thenReturn(Optional.of(userToDelete));

            assertThrows(IllegalStateException.class, () ->
                    userService.deleteUser(1L));
        }

        @Test
        void deleteUser_shouldDeleteUser_whenUserDoesNotHaveAssignedTasks() {
            User userToDelete = createUser();

            when(userRepository.findById(1L)).thenReturn(Optional.of(userToDelete));

            User deletedUser = userService.deleteUser(1L);

            assertTrue(deletedUser.isDeleted());

            verify(userRepository).save(deletedUser);
        }

        @Test
        void deleteUser_shouldDeleteUser_whenUserHasAssignedTasks() {
            User userToDelete = createUser();

            Project project = new Project("name", "description");

            ProjectMember membership = new ProjectMember(userToDelete, project, ProjectRole.MEMBER);
            userToDelete.addProjectMembership(membership);

            Task task = new Task(project, "title", "description", membership, Priority.MEDIUM, Status.TO_DO, userToDelete);
            membership.getAssignedTasks().add(task);

            when(userRepository.findById(1L)).thenReturn(Optional.of(userToDelete));

            User deletedUser = userService.deleteUser(1L);

            assertTrue(deletedUser.isDeleted());
            assertNull(task.getAssignee());

            verify(projectMemberRepository).delete(membership);
            verify(userRepository).save(deletedUser);
        }
    }

    private static User createUser() {
        User user = new User("email", "password", "name");
        user.setId(1L);
        return user;
    }
}
