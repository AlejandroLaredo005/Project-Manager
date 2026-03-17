package com.alejandolaredo.projectmanager.controller;

import com.alejandolaredo.projectmanager.dto.request.*;
import com.alejandolaredo.projectmanager.dto.response.ProjectMemberResponseDTO;
import com.alejandolaredo.projectmanager.dto.response.ProjectResponseDTO;
import com.alejandolaredo.projectmanager.model.Project;
import com.alejandolaredo.projectmanager.model.ProjectMember;
import com.alejandolaredo.projectmanager.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ProjectResponseDTO createProject(@Valid @RequestBody CreateProjectRequest request) {
        Project project = projectService.createProject(
                request.getName(),
                request.getDescription(),
                request.getOwnerId()
        );

        return ProjectResponseDTO.fromEntity(project);
    }

    @PostMapping("/{projectId}/members")
    public ProjectMemberResponseDTO addUserToProject(@PathVariable Long projectId,
                                                     @Valid @RequestBody AddUserToProjectRequest request) {
        ProjectMember projectMember = projectService.addUserToProject(
                request.getCurrentUserId(),
                request.getUserIdToAdd(),
                projectId,
                request.getRole()
        );

        return ProjectMemberResponseDTO.fromEntity(projectMember);
    }

    @DeleteMapping("/{projectId}/members")
    public void removeUserFromProject(@PathVariable Long projectId,
                                      @Valid @RequestBody RemoveUserFromProjectRequest request) {
        projectService.removeUserFromProject(
                request.getCurrentUserId(),
                request.getUserIdToRemove(),
                projectId
        );
    }

    @PostMapping("/{projectId}/leave")
    public void leaveProject(@PathVariable Long projectId,
                             @Valid @RequestBody LeaveProjectRequest request) {
        projectService.leaveProject(
                request.getUserId(),
                projectId
        );
    }

    @PatchMapping("/{projectId}/members/role")
    public ProjectMemberResponseDTO changeUserRole(@PathVariable Long projectId,
                                                   @Valid @RequestBody ChangeProjectUserRoleRequest request) {
        ProjectMember projectMember = projectService.changeUserRole(
                request.getAdminUserId(),
                request.getUserIdToChange(),
                projectId,
                request.getRole()
        );

        return ProjectMemberResponseDTO.fromEntity(projectMember);
    }

    @PatchMapping("/{projectId}/ownership")
    public ProjectMemberResponseDTO transferOwnership(@PathVariable Long projectId,
                                                      @Valid @RequestBody TransferProjectOwnership request) {
        ProjectMember projectMember = projectService.transferOwnership(
                request.getCurrentOwnerId(),
                request.getUserIdToChange(),
                projectId
        );

        return ProjectMemberResponseDTO.fromEntity(projectMember);
    }

    @DeleteMapping("/{projectId}")
    public void deleteProject(@PathVariable Long projectId, @RequestParam Long ownerId) {
        projectService.deleteProject(ownerId, projectId);
    }
}
