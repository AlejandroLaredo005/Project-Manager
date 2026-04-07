package com.alejandolaredo.projectmanager.controller;

import com.alejandolaredo.projectmanager.dto.request.*;
import com.alejandolaredo.projectmanager.dto.response.TaskResponseDTO;
import com.alejandolaredo.projectmanager.model.Task;
import com.alejandolaredo.projectmanager.model.User;
import com.alejandolaredo.projectmanager.repository.UserRepository;
import com.alejandolaredo.projectmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    public TaskController(TaskService taskService, UserRepository userRepository) {
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public TaskResponseDTO createTask(@Valid @RequestBody CreateTaskRequest request,
                                      Authentication authentication) {
        User user = getUser(authentication);

        Task task = taskService.createTask(
                request.getProjectId(),
                request.getTitle(),
                request.getDescription(),
                request.getAssigneeId(),
                request.getPriority(),
                request.getStatus(),
                user.getId()
        );

        return TaskResponseDTO.fromEntity(task);
    }

    @PatchMapping("/{taskId}/status")
    public TaskResponseDTO changeStatus(@PathVariable Long taskId,
                                        @Valid @RequestBody ChangeTaskStatusRequest request,
                                        Authentication authentication) {
        User user = getUser(authentication);

        Task task = taskService.changeStatus(
                taskId,
                user.getId(),
                request.getStatus()
        );

        return TaskResponseDTO.fromEntity(task);
    }

    @PatchMapping("/{taskId}/priority")
    public TaskResponseDTO changePriority(@PathVariable Long taskId,
                                          @Valid @RequestBody ChangeTaskPriorityRequest request,
                                          Authentication authentication) {
        User user = getUser(authentication);

        Task task = taskService.changePriority(
                taskId,
                user.getId(),
                request.getPriority()
        );

        return TaskResponseDTO.fromEntity(task);
    }

    @PatchMapping("/{taskId}")
    public TaskResponseDTO updateTask(@PathVariable Long taskId,
                                      @Valid @RequestBody UpdateTaskRequest request,
                                      Authentication authentication) {
        User user = getUser(authentication);

        Task task = taskService.updateTask(
                taskId,
                request.getTitle(),
                request.getDescription(),
                user.getId()
        );

        return TaskResponseDTO.fromEntity(task);
    }

    @PatchMapping("/{taskId}/assign")
    public TaskResponseDTO assignTask(@PathVariable Long taskId,
                                      @Valid @RequestBody AssignTaskRequest request,
                                      Authentication authentication) {
        User user = getUser(authentication);

        Task task = taskService.assignTask(
                taskId,
                user.getId(),
                request.getAssignedId()
        );

        return TaskResponseDTO.fromEntity(task);
    }

    @PatchMapping("/{taskId}/unassign")
    public TaskResponseDTO unassignTask(@PathVariable Long taskId,
                                        Authentication authentication) {
        User user = getUser(authentication);

        Task task = taskService.unassignTask(
                taskId,
                user.getId()
        );

        return TaskResponseDTO.fromEntity(task);
    }

    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable Long taskId,
                           Authentication authentication) {
        User user = getUser(authentication);

        taskService.deleteTask(taskId, user.getId());
    }

    private User getUser(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
