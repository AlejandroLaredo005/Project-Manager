package com.alejandolaredo.projectmanager.controller;

import com.alejandolaredo.projectmanager.dto.request.*;
import com.alejandolaredo.projectmanager.dto.response.ProjectMemberResponseDTO;
import com.alejandolaredo.projectmanager.dto.response.ProjectResponseDTO;
import com.alejandolaredo.projectmanager.model.Project;
import com.alejandolaredo.projectmanager.model.ProjectMember;
import com.alejandolaredo.projectmanager.model.User;
import com.alejandolaredo.projectmanager.repository.UserRepository;
import com.alejandolaredo.projectmanager.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final UserRepository userRepository;

    public ProjectController(ProjectService projectService, UserRepository userRepository) {
        this.projectService = projectService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ProjectResponseDTO createProject(@Valid @RequestBody CreateProjectRequest request,
                                            Authentication authentication) {
        User user = getUser(authentication);

        Project project = projectService.createProject(
                request.getName(),
                request.getDescription(),
                user.getId()
        );

        return ProjectResponseDTO.fromEntity(project);
    }

    @PostMapping("/{projectId}/members")
    public ProjectMemberResponseDTO addUserToProject(@PathVariable Long projectId,
                                                     @Valid @RequestBody AddUserToProjectRequest request,
                                                     Authentication authentication) {
        User user = getUser(authentication);

        ProjectMember projectMember = projectService.addUserToProject(
                user.getId(),
                request.getUserIdToAdd(),
                projectId,
                request.getRole()
        );

        return ProjectMemberResponseDTO.fromEntity(projectMember);
    }

    @DeleteMapping("/{projectId}/members")
    public void removeUserFromProject(@PathVariable Long projectId,
                                      @Valid @RequestBody RemoveUserFromProjectRequest request,
                                      Authentication authentication) {
        User user = getUser(authentication);

        projectService.removeUserFromProject(
                user.getId(),
                request.getUserIdToRemove(),
                projectId
        );
    }

    @PostMapping("/{projectId}/leave")
    public void leaveProject(@PathVariable Long projectId,
                             Authentication authentication) {
        User user = getUser(authentication);

        projectService.leaveProject(
                user.getId(),
                projectId
        );
    }

    @PatchMapping("/{projectId}/members/role")
    public ProjectMemberResponseDTO changeUserRole(@PathVariable Long projectId,
                                                   @Valid @RequestBody ChangeProjectUserRoleRequest request,
                                                   Authentication authentication) {
        User user = getUser(authentication);

        ProjectMember projectMember = projectService.changeUserRole(
                user.getId(),
                request.getUserIdToChange(),
                projectId,
                request.getRole()
        );

        return ProjectMemberResponseDTO.fromEntity(projectMember);
    }

    @PatchMapping("/{projectId}/ownership")
    public ProjectMemberResponseDTO transferOwnership(@PathVariable Long projectId,
                                                      @Valid @RequestBody TransferProjectOwnership request,
                                                      Authentication authentication) {
        User user = getUser(authentication);

        ProjectMember projectMember = projectService.transferOwnership(
                user.getId(),
                request.getUserIdToChange(),
                projectId
        );

        return ProjectMemberResponseDTO.fromEntity(projectMember);
    }

    @DeleteMapping("/{projectId}")
    public void deleteProject(@PathVariable Long projectId, Authentication authentication) {
        User user = getUser(authentication);

        projectService.deleteProject(user.getId(), projectId);
    }

    @GetMapping("/my")
    public List<ProjectResponseDTO> getMyProjects(Authentication authentication) {
        User user = getUser(authentication);

        return projectService.getUserProjects(user.getId())
                .stream()
                .map(ProjectResponseDTO::fromEntity)
                .toList();
    }

    private User getUser(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
