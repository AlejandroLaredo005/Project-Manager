package com.alejandolaredo.projectmanager.service;

import com.alejandolaredo.projectmanager.model.*;
import com.alejandolaredo.projectmanager.repository.ProjectMemberRepository;
import com.alejandolaredo.projectmanager.repository.ProjectRepository;
import com.alejandolaredo.projectmanager.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @InjectMocks
    private TaskService taskService;

    @Nested
    @DisplayName("Change Priority Tests")
    class ChangePriorityTests {

        @Test
        void changePriority_shouldChangePriority_whenMemberIsAdmin() {
            Project project = createProject(1L);

            Task task = createTask(1L, project, Priority.LOW);

            ProjectMember admin = createProjectMember(2L, project, ProjectRole.ADMIN);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(admin));

            Task updatedTask = taskService.changePriority(1L, 2L, Priority.HIGH);

            assertEquals(Priority.HIGH, updatedTask.getPriority());
        }

        @Test
        void changePriority_shouldThrowException_whenPriorityIsSame() {
            Project project = createProject(1L);

            Task task = createTask(1L, project, Priority.HIGH);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

            assertThrows(IllegalArgumentException.class, () ->
                    taskService.changePriority(1L, 2L, Priority.HIGH));
        }

        @Test
        void changePriority_shouldThrowException_whenProjectIsDifferent() {
            Project project = createProject(1L);
            Project project2 = createProject(2L);

            Task task = createTask(1L, project, Priority.LOW);

            ProjectMember admin = createProjectMember(2L, project2, ProjectRole.ADMIN);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(admin));

            assertThrows(IllegalArgumentException.class, () ->
                    taskService.changePriority(1L, 2L, Priority.HIGH));
        }

        @Test
        void changePriority_shouldThrowException_whenTaskIsClosed() {
            Project project = createProject(1L);

            Task taskClosed = createTask(1L, project, Priority.LOW);
            taskClosed.setStatus(Status.DONE);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(taskClosed));

            assertThrows(IllegalArgumentException.class, () ->
                    taskService.changePriority(1L, 2L, Priority.HIGH));
        }

        @Test
        void changePriority_shouldThrowException_whenPriorityIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    taskService.changePriority(1L, 2L, null));
        }
    }

    @Nested
    @DisplayName("Change Status Tests")
    class ChangeStatusTests {

        @Test
        void changeStatus_shouldThrowException_whenStatusIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    taskService.changeStatus(1L, 2L, null));
        }

        @Test
        void changeStatus_shouldThrowException_whenStatusIsSame() {
            Project project = createProject(1L);

            Task task = createTask(1L, project, Priority.HIGH);
            task.setStatus(Status.TO_DO);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

            assertThrows(IllegalArgumentException.class, () ->
                    taskService.changeStatus(1L, 2L, Status.TO_DO));
        }

        @Test
        void changeStatus_shouldThrowException_whenMemberIsNotAdminAndNewStatusIsDone() {
            Project project = createProject(1L);

            Task task = createTask(1L, project, Priority.HIGH);

            ProjectMember member = createProjectMember(2L, project, ProjectRole.MEMBER);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(member));

            assertThrows(IllegalArgumentException.class, () ->
                    taskService.changeStatus(1L, 2L, Status.DONE));
        }

        @Test
        void changeStatus_shouldThrowException_whenMemberIsNotAdminAndStatusIsDone() {
            Project project = createProject(1L);

            Task task = createTask(1L, project, Priority.HIGH);
            task.setStatus(Status.DONE);

            ProjectMember member = createProjectMember(2L, project, ProjectRole.MEMBER);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(member));

            assertThrows(IllegalArgumentException.class, () ->
                    taskService.changeStatus(1L, 2L, Status.TO_DO));
        }

        @Test
        void changeStatus_shouldThrowException_whenMemberIsNotAdminAndIsNotAssignedToTheTask() {
            Project project = createProject(1L);

            Task task = createTask(1L, project, Priority.HIGH);
            task.setStatus(Status.TO_DO);

            ProjectMember member = createProjectMember(2L, project, ProjectRole.MEMBER);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(member));

            assertThrows(IllegalArgumentException.class, () ->
                    taskService.changeStatus(1L, 2L, Status.IN_PROGRESS));
        }

        @Test
        void changeStatus_shouldChangeStatus_whenMemberIsNotAdminAndIsAssignedToTheTask() {
            Project project = createProject(1L);

            Task task = createTask(1L, project, Priority.HIGH);
            task.setStatus(Status.TO_DO);

            ProjectMember member = createProjectMember(2L, project, ProjectRole.MEMBER);
            task.setAssignee(member);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(member));

            Task updatedTask = taskService.changeStatus(1L, 2L, Status.IN_PROGRESS);

            assertEquals(Status.IN_PROGRESS, updatedTask.getStatus());
        }

        @Test
        void changeStatus_shouldChangeStatus_whenMemberIsAdmin() {
            Project project = createProject(1L);

            Task task = createTask(1L, project, Priority.HIGH);
            task.setStatus(Status.TO_DO);

            ProjectMember admin = createProjectMember(2L, project, ProjectRole.ADMIN);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(admin));

            Task updatedTask = taskService.changeStatus(1L, 2L, Status.DONE);

            assertEquals(Status.DONE, updatedTask.getStatus());
        }
    }

    @Nested
    @DisplayName("Delete Task Tests")
    class DeleteTaskTests {

        @Test
        void deleteTask_shouldThrowException_whenMemberIsNotAdmin() {
            Project project = createProject(1L);

            Task task = createTask(1L, project, Priority.LOW);

            ProjectMember projectMember = createProjectMember(2L, project, ProjectRole.MEMBER);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(projectMember));

            assertThrows(IllegalArgumentException.class, () ->
                    taskService.deleteTask(1L, 2L));
        }

        @Test
        void deleteTask_shouldDeleteTask_whenMemberIsAdmin() {
            Project project = createProject(1L);

            Task task = createTask(1L, project, Priority.HIGH);

            ProjectMember admin = createProjectMember(2L, project, ProjectRole.ADMIN);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(admin));

            taskService.deleteTask(1L, 2L);

            verify(projectRepository).save(project);
        }

        @Test
        void deleteTask_shouldThrowException_whenTaskIsNotFound() {
            assertThrows(EntityNotFoundException.class, () ->
                    taskService.deleteTask(1L, 2L));
        }

        @Test
        void deleteTask_shouldThrowException_whenProjectMemberIsNotFound() {
            Project project = createProject(1L);

            Task task = createTask(1L, project, Priority.HIGH);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

            assertThrows(EntityNotFoundException.class, () ->
                    taskService.deleteTask(1L, 2L));
        }
    }

    @Nested
    @DisplayName("Update Task Tests")
    class UpdateClassTests {

        @Test
        void updateTask_shouldThrowException_whenTitleIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    taskService.updateTask(1L, null, null, 2L));
        }

        @Test
        void updateTask_shouldUpdateTask_whenMemberIsAdmin() {
            Project project = createProject(1L);

            Task task = createTask(1L, project, Priority.HIGH);

            ProjectMember admin = createProjectMember(2L, project, ProjectRole.ADMIN);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(admin));

            Task updatedTask = taskService.updateTask(1L, "New Title", "New Description", 2L);
            assertEquals("New Title", updatedTask.getTitle());
            assertEquals("New Description", updatedTask.getDescription());
        }

        @Test
        void updateTask_shouldUpdateTask_whenMemberIsAdminAndDescriptionIsNull() {
            Project project = createProject(1L);

            Task task = createTask(2L, project, Priority.HIGH);

            ProjectMember admin = createProjectMember(2L, project, ProjectRole.ADMIN);

            when(taskRepository.findById(2L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(admin));

            Task updatedTask = taskService.updateTask(2L, "New Title", null, 2L);
            assertEquals("New Title", updatedTask.getTitle());
            assertNull(task.getDescription());
        }
    }

    @Nested
    @DisplayName("Create Task Tests")
    class CreateTaskTests {

        @Test
        void createTask_shouldThrowException_whenTitleIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    taskService.createTask(1L, null, null, 2L, null, null, 2L));
        }

        @Test
        void createTask_shouldThrowException_whenUserNotExists() {
            assertThrows(EntityNotFoundException.class, () ->
                    taskService.createTask(1L, "Title", null, 2L, null, null, 2L));
        }

        @Test
        void createTask_shouldCreateTask_whenTitleIsNotNullAndAssigneeIsNotNull() {
            Project project = createProject(1L);

            User creatorUser = new User("Useremail", "userpassword", "username");
            creatorUser.setId(10L);

            ProjectMember creatorProjectmember = createProjectMember(2L, project, ProjectRole.ADMIN);
            creatorProjectmember.setUser(creatorUser);

            ProjectMember assigneeProjectmember = createProjectMember(3L, project, ProjectRole.MEMBER);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectMemberRepository.findByUserIdAndProjectId(10L, 1L)).thenReturn(Optional.of(creatorProjectmember));
            when(projectMemberRepository.findById(3L)).thenReturn(Optional.of(assigneeProjectmember));

            Task newTask = taskService.createTask(1L, "Title", "Description", 3L,
                    Priority.MEDIUM, Status.TO_DO, 10L);

            assertEquals("Title", newTask.getTitle());
            assertEquals(project, newTask.getProject());
            assertEquals(assigneeProjectmember, newTask.getAssignee());
            assertEquals(creatorUser, newTask.getCreatedBy());

            verify(projectRepository).save(project);
        }

        @Test
        void createTask_shouldCreateTask_whenTitleIsNotNullAndAssigneeIsNull() {
            Project project = createProject(1L);

            User creatorUser = new User("Useremail", "userpassword", "username");
            creatorUser.setId(10L);

            ProjectMember creatorProjectmember = createProjectMember(2L, project, ProjectRole.ADMIN);
            creatorProjectmember.setUser(creatorUser);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectMemberRepository.findByUserIdAndProjectId(10L, 1L)).thenReturn(Optional.of(creatorProjectmember));

            Task newTask = taskService.createTask(1L, "Title", "Description", null,
                    Priority.MEDIUM, Status.TO_DO, 10L);

            assertEquals("Title", newTask.getTitle());
            assertEquals(project, newTask.getProject());
            assertNull(newTask.getAssignee());
            assertEquals(creatorUser, newTask.getCreatedBy());

            verify(projectRepository).save(project);
        }
    }

    @Nested
    @DisplayName("Assign Task Tests")
    class AssignTaskTests {

        @Test
        void assignTask_shouldThrowException_whenTaskIsPreviouslyAssigned() {
            Project project = createProject(1L);

            ProjectMember assigneedProjectmember = createProjectMember(2L, project, ProjectRole.ADMIN);

            Task task = createTask(1L, project, Priority.MEDIUM);
            task.setAssignee(assigneedProjectmember);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

            assertThrows(IllegalArgumentException.class, () ->
                    taskService.assignTask(1L, 2L, 2L));
        }

        @Test
        void assignTask_shouldThrowException_whenAssignerIsMemberAndTryToAssignToAnotherUser() {
            Project project = createProject(1L);

            ProjectMember assignerProjectmember = createProjectMember(2L, project, ProjectRole.MEMBER);

            Task task = createTask(1L, project, Priority.MEDIUM);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(assignerProjectmember));

            assertThrows(IllegalArgumentException.class, () ->
                    taskService.assignTask(1L, 2L, 3L));
        }

        @Test
        void assignTask_shouldAssignTask_whenAssignerIsMemberAndTryToAssignToItself() {
            Project project = createProject(1L);

            ProjectMember assignerProjectmember = createProjectMember(2L, project, ProjectRole.MEMBER);

            Task task = createTask(1L, project, Priority.MEDIUM);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(assignerProjectmember));

            Task updatedTask = taskService.assignTask(1L, 2L, 2L);

            assertEquals(assignerProjectmember, updatedTask.getAssignee());
        }

        @Test
        void assignTask_shouldThrowException_whenAssignedNotExists() {
            Project project = createProject(1L);

            ProjectMember assignerProjectmember = createProjectMember(2L, project, ProjectRole.ADMIN);

            Task task = createTask(1L, project, Priority.MEDIUM);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(assignerProjectmember));

            assertThrows(EntityNotFoundException.class, () ->
                    taskService.assignTask(1L, 2L, 4L));
        }

        @Test
        void assignTask_shouldAssignTask_whenAssignerIsAdminAndTryToAssignToItself() {
            Project project = createProject(1L);

            ProjectMember assignerProjectmember = createProjectMember(2L, project, ProjectRole.ADMIN);

            Task task = createTask(1L, project, Priority.MEDIUM);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(assignerProjectmember));

            Task updatedTask = taskService.assignTask(1L, 2L, 2L);

            assertEquals(assignerProjectmember, updatedTask.getAssignee());
        }

        @Test
        void assignTask_shouldAssignTask_whenAssignerIsAdminAndTryToAssignToOtherUser() {
            Project project = createProject(1L);

            ProjectMember assignerProjectmember = createProjectMember(2L, project, ProjectRole.ADMIN);
            ProjectMember assignedProjectmember = createProjectMember(3L, project, ProjectRole.MEMBER);

            Task task = createTask(1L, project, Priority.MEDIUM);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(assignerProjectmember));
            when(projectMemberRepository.findById(3L)).thenReturn(Optional.of(assignedProjectmember));

            Task updatedTask = taskService.assignTask(1L, 2L, 3L);

            assertEquals(assignedProjectmember, updatedTask.getAssignee());
        }
    }

    @Nested
    @DisplayName("Unassign Task Tests")
    class UnassignTaskTests {

        @Test
        void unassignTask_shouldThrowException_whenTaskIsPreviouslyUnassigned() {
            Project project = createProject(1L);

            Task task = createTask(1L, project, Priority.MEDIUM);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

            assertThrows(IllegalArgumentException.class, () ->
                    taskService.unassignTask(1L, 2L));
        }

        @Test
        void unassignTask_shouldThrowException_whenUnAssignerIsMemberAndTryToUnAssignToAnotherUser() {
            Project project = createProject(1L);

            ProjectMember unassignerProjectmember = createProjectMember(2L, project, ProjectRole.MEMBER);
            ProjectMember assignedProjectmember = createProjectMember(3L, project, ProjectRole.MEMBER);

            Task task = createTask(1L, project, Priority.MEDIUM);
            task.setAssignee(assignedProjectmember);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(unassignerProjectmember));

            assertThrows(IllegalArgumentException.class, () ->
                    taskService.unassignTask(1L, 2L));
        }

        @Test
        void unassignTask_shouldUnAssignTask_whenUnAssignerIsMemberAndTryToUnassignToItself() {
            Project project = createProject(1L);

            ProjectMember unassignerProjectmember = createProjectMember(2L, project, ProjectRole.MEMBER);

            Task task = createTask(1L, project, Priority.MEDIUM);
            task.setAssignee(unassignerProjectmember);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(unassignerProjectmember));

            Task updatedTask = taskService.unassignTask(1L, 2L);

            assertNull(updatedTask.getAssignee());
        }

        @Test
        void unassignTask_shouldUnassignTask_whenUnAssignerIsAdminAndTryToUnAssignToItself() {
            Project project = createProject(1L);

            ProjectMember unassignerProjectmember = createProjectMember(2L, project, ProjectRole.ADMIN);

            Task task = createTask(1L, project, Priority.MEDIUM);
            task.setAssignee(unassignerProjectmember);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(unassignerProjectmember));

            Task updatedTask = taskService.unassignTask(1L, 2L);

            assertNull(updatedTask.getAssignee());
        }

        @Test
        void unassignTask_shouldUnAssignTask_whenUnAssignerIsAdminAndTryToUnAssignToOtherUser() {
            Project project = createProject(1L);

            ProjectMember unassignerProjectmember = createProjectMember(2L, project, ProjectRole.ADMIN);
            ProjectMember assignedProjectmember = createProjectMember(3L, project, ProjectRole.MEMBER);

            Task task = createTask(1L, project, Priority.MEDIUM);
            task.setAssignee(assignedProjectmember);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(unassignerProjectmember));

            Task updatedTask = taskService.unassignTask(1L, 2L);

            assertNull(updatedTask.getAssignee());
        }
    }

    private static Project createProject(Long id) {
        Project project = new Project();
        project.setId(id);
        return project;
    }

    private static Task createTask(Long id, Project project, Priority priority) {
        Task task = new Task();
        task.setId(id);
        task.setProject(project);
        task.setPriority(priority);
        return task;
    }

    private static ProjectMember createProjectMember(Long id, Project project, ProjectRole projectRole) {
        ProjectMember projectMember = new ProjectMember();
        projectMember.setId(id);
        projectMember.setProject(project);
        projectMember.setRole(projectRole);
        return projectMember;
    }
}
