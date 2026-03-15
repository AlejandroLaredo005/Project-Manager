package com.alejandolaredo.projectmanager.service;

import com.alejandolaredo.projectmanager.model.*;
import com.alejandolaredo.projectmanager.repository.ProjectMemberRepository;
import com.alejandolaredo.projectmanager.repository.ProjectRepository;
import com.alejandolaredo.projectmanager.repository.TaskRepository;
import com.alejandolaredo.projectmanager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    ProjectService projectService;

    @Nested
    @DisplayName("Create Project Tests")
    class CreateProjectTests {

        @Test
        void createProject_shouldThrowException_whenNameIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    projectService.createProject(null, null, null));
        }

        @Test
        void createProject_shouldThrowException_whenOwnerDoesNotExists() {
            assertThrows(EntityNotFoundException.class, () ->
                    projectService.createProject("Titulo", "Descripción", 1L));
        }

        @Test
        void createProject_shouldCreateProject_whenTitleIsCorrectAndUserExists() {
            User owner = createUser(1L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

            Project newProject = projectService.createProject("Titulo", "Description", 1L);

            assertEquals("Titulo", newProject.getName());
            assertEquals("Description", newProject.getDescription());
            assertEquals(owner, newProject.getOwner());

            verify(projectRepository).save(newProject);
        }
    }

    @Nested
    @DisplayName("Delete Project Tests")
    class DeleteProjectTests {

        @Test
        void deleteProject_shouldThrowException_whenProjectDoesNotExists() {
            assertThrows(EntityNotFoundException.class, () ->
                    projectService.deleteProject(1L, 1L));
        }

        @Test
        void deleteProject_shouldThrowException_whenDeleterIsNotOwner() {
            Project project = createProject();

            User owner = createUser(1L);

            owner.addOwnedProject(project);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

            assertThrows(IllegalStateException.class, () ->
                    projectService.deleteProject(2L, 1L));
        }

        @Test
        void deleteProject_shouldDeleteProject_whenDeleterIsOwner() {
            Project project = createProject();

            User owner = createUser(1L);

            owner.addOwnedProject(project);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

            projectService.deleteProject(1L, 1L);

            verify(projectRepository).delete(project);
        }
    }

    @Nested
    @DisplayName("Leave Project Tests")
    class LeaveProjectTests {

        @Test
        void leaveProject_shouldThrowException_whenProjectMemberDoesNotExists() {
            Project project = createProject();

            User owner = createUser(1L);

            owner.addOwnedProject(project);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

            assertThrows(EntityNotFoundException.class, () ->
                    projectService.leaveProject(1L, 1L));
        }

        @Test
        void leaveProject_shouldThrowException_whenOwnerTriesToLeave() {
            Project project = createProject();

            User owner = createUser(1L);

            owner.addOwnedProject(project);

            ProjectMember ownerMembership = new ProjectMember(owner, project, ProjectRole.ADMIN);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
            when(projectMemberRepository.findByUserIdAndProjectId(1L, 1L)).thenReturn(Optional.of(ownerMembership));

            assertThrows(IllegalStateException.class, () ->
                    projectService.leaveProject(1L, 1L));
        }

        @Test
        void leaveProject_shouldLeaveProject_whenUserTriesToLeave() {
            Project project = createProject();

            User owner = createUser(1L);

            owner.addOwnedProject(project);

            User leaver = createUser(2L);

            ProjectMember leaverMembership = new ProjectMember(leaver, project, ProjectRole.MEMBER);

            project.addMember(leaverMembership);
            leaver.addProjectMembership(leaverMembership);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
            when(projectMemberRepository.findByUserIdAndProjectId(2L, 1L)).thenReturn(Optional.of(leaverMembership));

            projectService.leaveProject(2L, 1L);

            verify(projectRepository).save(project);
        }

        @Test
        void leaveProject_shouldLeaveProject_whenUserTriesToLeaveAndIsAssignedToTasks() {
            Project project = createProject();

            User owner = createUser(1L);

            owner.addOwnedProject(project);

            User leaver = createUser(2L);

            ProjectMember leaverMembership = createProjectMember(leaver, project, ProjectRole.MEMBER, 1L);

            Task task = new Task(project, "title", "description", leaverMembership, Priority.MEDIUM, Status.TO_DO, owner);

            List<Task> tasks = List.of(task);

            project.addMember(leaverMembership);
            leaver.addProjectMembership(leaverMembership);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
            when(projectMemberRepository.findByUserIdAndProjectId(2L, 1L)).thenReturn(Optional.of(leaverMembership));
            when(taskRepository.findByAssigneeId(1L)).thenReturn(tasks);

            projectService.leaveProject(2L, 1L);

            assertNull(task.getAssignee());

            verify(projectRepository).save(project);
        }
    }

    @Nested
    @DisplayName("Remove User From Project Tests")
    class RemoveUserFromProjectTests {

        @Test
        void removeUserFromProject_shouldThrowException_whenDeleterIsNotAdmin() {
            Project project = createProject();

            User leaver = createUser(2L);

            ProjectMember leaverMembership = createProjectMember(leaver, project, ProjectRole.MEMBER, 1L);

            ProjectMember deleterMembership = createProjectMember(leaver, project, ProjectRole.MEMBER, 2L);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(userRepository.findById(2L)).thenReturn(Optional.of(leaver));
            when(projectMemberRepository.findByUserIdAndProjectId(2L, 1L)).thenReturn(Optional.of(leaverMembership));
            when(projectMemberRepository.findByUserIdAndProjectId(1L, 1L)).thenReturn(Optional.of(deleterMembership));

            assertThrows(IllegalStateException.class, () ->
                    projectService.removeUserFromProject(1L, 2L, 1L));
        }

        @Test
        void removeUserFromProject_shouldThrowException_whenDeletedIsOwner() {
            Project project = createProject();

            User leaver = createUser(2L);

            leaver.addOwnedProject(project);

            ProjectMember leaverMembership = createProjectMember(leaver, project, ProjectRole.ADMIN, 1L);

            ProjectMember deleterMembership = createProjectMember(leaver, project, ProjectRole.ADMIN, 2L);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(userRepository.findById(2L)).thenReturn(Optional.of(leaver));
            when(projectMemberRepository.findByUserIdAndProjectId(2L, 1L)).thenReturn(Optional.of(leaverMembership));
            when(projectMemberRepository.findByUserIdAndProjectId(1L, 1L)).thenReturn(Optional.of(deleterMembership));

            assertThrows(IllegalStateException.class, () ->
                    projectService.removeUserFromProject(1L, 2L, 1L));
        }

        @Test
        void removeUserFromProject_shouldThrowException_whenUserTriesToDeleteHimself() {
            Project project = createProject();

            User leaver = createUser(2L);

            User owner = createUser(3L);

            owner.addOwnedProject(project);

            ProjectMember leaverMembership = createProjectMember(leaver, project, ProjectRole.ADMIN, 1L);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(userRepository.findById(2L)).thenReturn(Optional.of(leaver));
            when(projectMemberRepository.findByUserIdAndProjectId(2L, 1L)).thenReturn(Optional.of(leaverMembership));

            assertThrows(IllegalStateException.class, () ->
                    projectService.removeUserFromProject(2L, 2L, 1L));
        }

        @Test
        void removeUserFromProject_shouldRemoveUser_whenDeleterIsAdmin() {
            Project project = createProject();

            User leaver = createUser(2L);

            User owner = createUser(3L);

            owner.addOwnedProject(project);

            ProjectMember leaverMembership = createProjectMember(leaver, project, ProjectRole.MEMBER, 2L);

            ProjectMember deleterMembership = createProjectMember(leaver, project, ProjectRole.ADMIN, 1L);

            Task task = new Task(project, "title", "description", leaverMembership, Priority.MEDIUM, Status.TO_DO, owner);

            List<Task> tasks = List.of(task);

            project.addMember(leaverMembership);
            leaver.addProjectMembership(leaverMembership);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(userRepository.findById(2L)).thenReturn(Optional.of(leaver));
            when(projectMemberRepository.findByUserIdAndProjectId(2L, 1L)).thenReturn(Optional.of(leaverMembership));
            when(projectMemberRepository.findByUserIdAndProjectId(1L, 1L)).thenReturn(Optional.of(deleterMembership));
            when(taskRepository.findByAssigneeId(2L)).thenReturn(tasks);

            projectService.removeUserFromProject(1L, 2L, 1L);

            assertNull(task.getAssignee());

            verify(projectRepository).save(project);
        }
    }

    @Nested
    @DisplayName("Add User To Project Tests")
    class AddUserToProjectTests {

        @Test
        void addUserToProject_shouldThrowException_whenProjectRoleIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    projectService.addUserToProject(1L, 2L, 1L, null));
        }

        @Test
        void addUserToProject_shouldThrowException_whenProjectMemberIsInTheProject() {
            User userToAdd = createUser(1L);

            Project project = createProject();

            when(userRepository.findById(1L)).thenReturn(Optional.of(userToAdd));
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectMemberRepository.existsByUserAndProject(userToAdd, project)).thenReturn(true);

            assertThrows(IllegalStateException.class, () ->
                    projectService.addUserToProject(2L, 1L, 1L, ProjectRole.MEMBER));
        }

        @Test
        void addUserToProject_shouldAddUser_whenAdderIsAdmin() {
            User userAdder = createUser(1L);

            User userToAdd = createUser(2L);

            Project project = createProject();

            ProjectMember adderProjectmember = new ProjectMember(userAdder, project, ProjectRole.ADMIN);

            when(userRepository.findById(2L)).thenReturn(Optional.of(userToAdd));
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectMemberRepository.existsByUserAndProject(userToAdd, project)).thenReturn(false);
            when(projectMemberRepository.findByUserIdAndProjectId(1L, 1L)).thenReturn(Optional.of(adderProjectmember));

            ProjectMember addedProjectmember = projectService.addUserToProject(1L, 2L, 1L, ProjectRole.MEMBER);

            assertEquals(ProjectRole.MEMBER, addedProjectmember.getRole());
            assertEquals(1L, addedProjectmember.getProject().getId());

            verify(projectRepository).save(project);
        }
    }

    @Nested
    @DisplayName("Transfer Ownership Tests")
    class TransferOwnershipTests {

        @Test
        void transferOwnership_shouldThrowException_whenUserToTransferDoesNotExists() {
            Project project = createProject();

            User owner = createUser(1L);

            owner.addOwnedProject(project);

            when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

            assertThrows(EntityNotFoundException.class, () ->
                    projectService.transferOwnership(1L, 2L, 1L));
        }

        @Test
        void transferOwnership_shouldTransferOwnership_whenOwnerTriesToTransferToAnotherMember() {
            Project project = createProject();

            User owner = createUser(1L);

            owner.addOwnedProject(project);

            User userToTransfer = createUser(2L);

            ProjectMember projectMemberToTransfer = new ProjectMember(userToTransfer, project, ProjectRole.MEMBER);

            when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectMemberRepository.findByUserIdAndProjectId(2L, 1L)).thenReturn(Optional.of(projectMemberToTransfer));

            ProjectMember updatedProjectMember = projectService.transferOwnership(1L, 2L, 1L);

            assertEquals(ProjectRole.ADMIN, updatedProjectMember.getRole());
            assertEquals(userToTransfer, project.getOwner());

            verify(projectRepository).save(project);
        }
    }

    @Nested
    @DisplayName("Change User Role Tests")
    class ChangeUserRoleTests {

        @Test
        void changeUserRole_shouldThrowException_whenProjectRoleIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    projectService.changeUserRole(1L, 2L, 1L, null));
        }

        @Test
        void changeUserRole_shouldThrowException_whenUserToChangeHasAlreadyThatRole() {
            Project project = createProject();

            User userChanger = createUser(1L);

            User userToChange = createUser(2L);

            User owner = createUser(3L);

            owner.addOwnedProject(project);

            ProjectMember projectMemberChanger = new ProjectMember(userChanger, project, ProjectRole.ADMIN);
            ProjectMember projectMemberToChange = new ProjectMember(userToChange, project, ProjectRole.MEMBER);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectMemberRepository.findByUserIdAndProjectId(1L, 1L)).thenReturn(Optional.of(projectMemberChanger));
            when(projectMemberRepository.findByUserIdAndProjectId(2L, 1L)).thenReturn(Optional.of(projectMemberToChange));

            assertThrows(IllegalStateException.class, () ->
                    projectService.changeUserRole(1L, 2L, 1L, ProjectRole.MEMBER));
        }

        @Test
        void changeUserRole_shouldChangeRole_whenChangerIsAdmin() {
            Project project = createProject();

            User userChanger = createUser(1L);

            User userToChange = createUser(2L);

            User owner = createUser(3L);

            owner.addOwnedProject(project);

            ProjectMember projectMemberChanger = new ProjectMember(userChanger, project, ProjectRole.ADMIN);
            ProjectMember projectMemberToChange = new ProjectMember(userToChange, project, ProjectRole.MEMBER);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectMemberRepository.findByUserIdAndProjectId(1L, 1L)).thenReturn(Optional.of(projectMemberChanger));
            when(projectMemberRepository.findByUserIdAndProjectId(2L, 1L)).thenReturn(Optional.of(projectMemberToChange));

            ProjectMember changedProjectMember = projectService.changeUserRole(1L, 2L, 1L, ProjectRole.ADMIN);

            assertEquals(ProjectRole.ADMIN, changedProjectMember.getRole());

            verify(projectMemberRepository).save(projectMemberToChange);
        }
    }

    private static Project createProject() {
        Project project = new Project("titulo", "description");
        project.setId(1L);
        return project;
    }

    private static User createUser(Long id) {
        User user = new User("email", "password", "user" + id);
        user.setId(id);
        return user;
    }

    private static ProjectMember createProjectMember(User user, Project project, ProjectRole projectRole, Long id) {
        ProjectMember projectMember = new ProjectMember(user, project, projectRole);
        projectMember.setId(id);
        return projectMember;
    }
}
