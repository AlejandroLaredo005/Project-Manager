package com.alejandolaredo.projectmanager.service;

import com.alejandolaredo.projectmanager.model.*;
import com.alejandolaredo.projectmanager.repository.ProjectMemberRepository;
import com.alejandolaredo.projectmanager.repository.ProjectRepository;
import com.alejandolaredo.projectmanager.repository.TaskRepository;
import com.alejandolaredo.projectmanager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;

    public ProjectService(ProjectRepository projectRepository,
                          UserRepository userRepository,
                          ProjectMemberRepository projectMemberRepository,
                          TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Project createProject(String name, String description, Long ownerId) {
        // Validación básica
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proyecto es obligatorio");
        }

        // Busca owner
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + ownerId));

        Project project = new Project(name.trim(), description);
        owner.addOwnedProject(project);

        ProjectMember projectMember = new ProjectMember(owner, project, ProjectRole.ADMIN);

        project.addMember(projectMember);
        owner.addProjectMembership(projectMember);

        projectRepository.save(project);

        return project;
    }

    @Transactional
    public ProjectMember addUserToProject(Long currentUserId, Long userIdToAdd, Long projectId, ProjectRole projectRole) {
        if (projectRole == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        // Busca user que vamos a añadir
        User userToAdd = userRepository.findById(userIdToAdd)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + userIdToAdd));

        if (userToAdd.isDeleted()) {
            throw new IllegalStateException("El usuario al que queremos añadir está eliminado");
        }

        // Busca project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado: " + projectId));

        if (projectMemberRepository.existsByUserAndProject(userToAdd, project)) {
            throw new IllegalStateException("El usuario ya pertenece al proyecto");
        }

        // Tenemos que comprobar que el usuario que está intentando añadir al otro es admin
        ProjectMember currentProjectMember = projectMemberRepository.findByUserIdAndProjectId(currentUserId, project.getId())
                .orElseThrow(() -> new EntityNotFoundException("El usuario actual no esta en el proyecto"));

        if (currentProjectMember.getRole() != (ProjectRole.ADMIN)) {
            throw new IllegalStateException("El usuario no puede ser añadido porque el usuario que lo intenta añadir no es admin");
        }

        ProjectMember projectMemberToAdd = new ProjectMember(userToAdd, project, projectRole);

        ProjectMember savedMember = projectMemberRepository.save(projectMemberToAdd);

        project.addMember(savedMember);
        userToAdd.addProjectMembership(savedMember);

        return savedMember;
    }

    @Transactional
    public void removeUserFromProject(Long currentUserId, Long userIdToRemove, Long projectId) {
        // Busca al usuario que vamos a quitar
        User userToRemove = userRepository.findById(userIdToRemove)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + userIdToRemove));

        // Busca project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado: " + projectId));

        // Obtenemos la membresía del usuario al que queremos eliminar
        ProjectMember projectMemberToRemove = projectMemberRepository.findByUserIdAndProjectId(userIdToRemove, projectId)
                .orElseThrow(() -> new EntityNotFoundException("El usuario al que queremos eliminar no esta en el proyecto"));

        // Obtenemos la membresía del usuario que va a eliminar al otro
        ProjectMember currentProjectMember = projectMemberRepository.findByUserIdAndProjectId(currentUserId, project.getId())
                .orElseThrow(() -> new EntityNotFoundException("El usuario actual no esta en el proyecto"));

        // Tenemos que comprobar que el usuario que está intentando eliminar al otro es admin
        if (currentProjectMember.getRole() != ProjectRole.ADMIN) {
            throw new IllegalStateException("El usuario no puede ser eliminado porque el usuario que lo intenta eliminar no es admin");
        }

        // Comprobamos que no estamos intentando eliminar al owner
        if (project.getOwner().getId().equals(userIdToRemove)) {
            throw new IllegalStateException("El usuario al que se intenta eliminar es el propietario del proyecto");
        }

        // Comprobamos que el usuario no se esté borrando a sí mismo
        if (currentUserId.equals(userIdToRemove)) {
            throw new IllegalStateException("El usuario se está intentando eliminar a sí mismo");
        }

        List<Task> assignedTasks = taskRepository.findByAssigneeId(projectMemberToRemove.getId());

        for (Task task : assignedTasks) {
            task.setAssignee(null);
        }

        // Eliminamos al usuario
        userToRemove.removeProjectMembership(projectMemberToRemove);
        project.removeMember(projectMemberToRemove);

        projectRepository.save(project);
    }

    @Transactional
    public void leaveProject(Long userId, Long projectId) {
        // Busca al usuario que vamos a quitar
        User userToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + userId));

        // Busca project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado: " + projectId));

        // Obtenemos la membresía del usuario al que queremos eliminar
        ProjectMember projectMemberToRemove = projectMemberRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new EntityNotFoundException("El usuario no esta en el proyecto"));

        // Comprobamos que el owner no esté intentando salir, debe quitarse la categoría de owner primero
        if (project.getOwner().getId().equals(userId)) {
            throw new IllegalStateException("El usuario que intenta salir es el propietario, primero debe delegar el proyecto a oro usuario");
        }

        List<Task> assignedTasks = taskRepository.findByAssigneeId(projectMemberToRemove.getId());

        for (Task task : assignedTasks) {
            task.setAssignee(null);
        }

        // Eliminamos al usuario
        userToRemove.removeProjectMembership(projectMemberToRemove);
        project.removeMember(projectMemberToRemove);

        projectRepository.save(project);
    }

    @Transactional
    public ProjectMember changeUserRole(Long adminUserId, Long userIdToChange, Long projectId, ProjectRole projectRole) {
        if (projectRole == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        // Busca project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado: " + projectId));

        // Obtenemos la membresía del usuario que queremos cambiar
        ProjectMember projectMemberToChange = projectMemberRepository.findByUserIdAndProjectId(userIdToChange, projectId)
                .orElseThrow(() -> new EntityNotFoundException("El usuario al que queremos cambiar no esta en el proyecto"));

        // Obtenemos la membresía del usuario que va a cambiar al otro
        ProjectMember currentAdminProjectMember = projectMemberRepository.findByUserIdAndProjectId(adminUserId, project.getId())
                .orElseThrow(() -> new EntityNotFoundException("El usuario que va a cambiar al otro no esta en el proyecto"));

        // Tenemos que comprobar que el usuario que está intentando cambiar al otro es admin
        if (currentAdminProjectMember.getRole() != ProjectRole.ADMIN) {
            throw new IllegalStateException("El usuario no puede ser cambiado porque el usuario que lo intenta cambiar no es admin");
        }

        // Comprobamos que no estamos intentando cambiar al owner
        if (project.getOwner().getId().equals(userIdToChange)) {
            throw new IllegalStateException("El usuario al que se intenta cambiar es el propietario del proyecto");
        }

        // Comprobamos que el usuario no se esté cambiando a sí mismo
        if (adminUserId.equals(userIdToChange)) {
            throw new IllegalStateException("El usuario se está intentando cambiar a sí mismo");
        }

        // Comprobamos que el usuario que queremos cambiar no tenga ya ese rol
        if (projectMemberToChange.getRole() == projectRole) {
            throw new IllegalStateException("El usuario que queremos cambiar ya tenía el rol seleccionado");
        }

        // Cambiamos el rol del usuario
        projectMemberToChange.setRole(projectRole);

        projectMemberRepository.save(projectMemberToChange);

        return projectMemberToChange;
    }

    @Transactional
    public ProjectMember transferOwnership(Long currentOwnerId, Long userIdToChange, Long projectId) {
        // Comprobamos que el usuario no se esté cambiando a sí mismo
        if (currentOwnerId.equals(userIdToChange)) {
            throw new IllegalStateException("El usuario se está intentando cambiar a sí mismo");
        }

        // Busca al owner actual
        User currentOwner = userRepository.findById(currentOwnerId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + currentOwnerId));

        // Busca project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado: " + projectId));

        // Comprobamos que el usuario sea el propietario de ese proyecto
        if (!project.getOwner().getId().equals(currentOwnerId)) {
            throw new IllegalStateException("El usuario no es el propietario de este proyecto");
        }

        // Comprobamos que el usuario al que queremos hacer owner esté en el proyecto
        ProjectMember projectMemberToChange = projectMemberRepository.findByUserIdAndProjectId(userIdToChange, projectId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + userIdToChange));

        // Obtenemos su usuario
        User userToChange = projectMemberToChange.getUser();

        // Transferimos el rango de owner al otro usuario
        currentOwner.removeOwnedProject(project);
        userToChange.addOwnedProject(project);

        // Comprobamos que el nuevo owner sea admin, si no lo es se lo ponemos
        if (projectMemberToChange.getRole() != ProjectRole.ADMIN) {
            projectMemberToChange.setRole(ProjectRole.ADMIN);
        }

        projectRepository.save(project);

        return projectMemberToChange;
    }

    @Transactional
    public void deleteProject(Long ownerId, Long projectId) {
        // Busca project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado: " + projectId));

        // Comprobamos que el usuario sea el propietario de ese proyecto
        if (!project.getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("El usuario no es el propietario de este proyecto");
        }

        // Borramos el proyecto
        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public List<Project> getUserProjects(Long userId) {
        // Buscamos al usuario del cual queremos sus proyectos
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + userId));

        // Comprobamos que el usuario no esté eliminado
        if (user.isDeleted()) {
            throw new IllegalStateException("El usuario no existe");
        }

        // Obtenemos sus proyectos
        return user.getProjectMemberships()
                .stream()
                .map(ProjectMember::getProject)
                .toList();
    }
}
