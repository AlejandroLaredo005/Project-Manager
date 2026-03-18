package com.alejandolaredo.projectmanager.controller;

import com.alejandolaredo.projectmanager.dto.request.*;
import com.alejandolaredo.projectmanager.dto.response.TaskResponseDTO;
import com.alejandolaredo.projectmanager.model.Task;
import com.alejandolaredo.projectmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public TaskResponseDTO createTask(@Valid @RequestBody CreateTaskRequest request) {
        Task task = taskService.createTask(
                request.getProjectId(),
                request.getTitle(),
                request.getDescription(),
                request.getAssigneeId(),
                request.getPriority(),
                request.getStatus(),
                request.getCreatedById()
        );

        return TaskResponseDTO.fromEntity(task);
    }

    @PatchMapping("/{taskId}/status")
    public TaskResponseDTO changeStatus(@PathVariable Long taskId,
                                        @Valid @RequestBody ChangeTaskStatusRequest request) {
        Task task = taskService.changeStatus(
                taskId,
                request.getProjectMemberId(),
                request.getStatus()
        );

        return TaskResponseDTO.fromEntity(task);
    }

    @PatchMapping("/{taskId}/priority")
    public TaskResponseDTO changePriority(@PathVariable Long taskId,
                                          @Valid @RequestBody ChangeTaskPriorityRequest request) {
        Task task = taskService.changePriority(
                taskId,
                request.getProjectMemberId(),
                request.getPriority()
        );

        return TaskResponseDTO.fromEntity(task);
    }

    @PatchMapping("/{taskId}")
    public TaskResponseDTO updateTask(@PathVariable Long taskId,
                                      @Valid @RequestBody UpdateTaskRequest request) {
        Task task = taskService.updateTask(
                taskId,
                request.getTitle(),
                request.getDescription(),
                request.getUpdaterId()
        );

        return TaskResponseDTO.fromEntity(task);
    }

    @PatchMapping("/{taskId}/assign")
    public TaskResponseDTO assignTask(@PathVariable Long taskId,
                                      @Valid @RequestBody AssignTaskRequest request) {
        Task task = taskService.assignTask(
                taskId,
                request.getAssignerId(),
                request.getAssignedId()
        );

        return TaskResponseDTO.fromEntity(task);
    }

    @PatchMapping("/{taskId}/unassign")
    public TaskResponseDTO unassignTask(@PathVariable Long taskId,
                                        @Valid @RequestBody UnassignTaskRequest request) {
        Task task = taskService.unassignTask(
                taskId,
                request.getProjectMemberId()
        );

        return TaskResponseDTO.fromEntity(task);
    }

    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable Long taskId,
                           @RequestParam Long projectMemberId) {
        taskService.deleteTask(taskId, projectMemberId);
    }
}
