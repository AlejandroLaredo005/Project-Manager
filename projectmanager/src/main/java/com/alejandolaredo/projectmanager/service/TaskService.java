package com.alejandolaredo.projectmanager.service;

import com.alejandolaredo.projectmanager.model.*;
import com.alejandolaredo.projectmanager.repository.ProjectMemberRepository;
import com.alejandolaredo.projectmanager.repository.ProjectRepository;
import com.alejandolaredo.projectmanager.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;

    public TaskService(ProjectRepository projectRepository,
                       ProjectMemberRepository projectMemberRepository,
                       TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Task createTask(Long projectId, String title, String description, Long assigneeId,
                           Priority priority, Status status, Long creatorUserId) {
        // Validamos que los campos sean correctos
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("El título del proyecto es obligatorio");
        }

        // Buscamos Project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("No existe un proyecto con ese id"));

        // Comprobamos que el creador de la tarea pertenezca al proyecto
        ProjectMember creatorProjectmember = projectMemberRepository.findByUserIdAndProjectId(creatorUserId, projectId)
                .orElseThrow(() -> new EntityNotFoundException("El usuario que intenta crear la tarea no está en el proyecto"));


        // Comprobamos que el que está intentando crear la tarea sea admin
        validateAdmin(creatorProjectmember);

        User creatorUser = creatorProjectmember.getUser();

        // Validamos si assigneeId es correcto en caso de haber puesto uno
        ProjectMember assignee = null;
        if (assigneeId != null) {
            assignee = projectMemberRepository.findById(assigneeId)
                    .orElseThrow(() -> new EntityNotFoundException("El usuario asignado no existe"));

            if (!assignee.getProject().getId().equals(projectId)) {
                throw new IllegalStateException("El assignee no pertenece al proyecto");
            }
        }

        Task task = new Task(project, title, description, assignee, priority, status, creatorUser);

        Task savedTask = taskRepository.save(task);

        project.addTask(task);

        return savedTask;
    }

    @Transactional
    public Task changeStatus(Long taskId, Long projectMemberId, Status status) {
        // Comprobamos que el estado no sea nulo
        if (status == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }

        // Comprobamos que la tarea existe
        Task task = getTask(taskId);

        // Comprobamos que la tarea no tuviera ya ese estado
        if (task.getStatus() == status) {
            throw new IllegalArgumentException("La tarea ya tenía ese estado");
        }

        // Comprobamos que el projectMember exista
        ProjectMember projectMember = getProjectMember(projectMemberId);

        // Comprobamos que la tarea y el miembro pertenezcan al mismo proyecto
        validateSameProject(task, projectMember);

        // Comprobamos si es admin o miembro
        if (projectMember.getRole() == ProjectRole.MEMBER) {
            // Si es miembro no permite cambiar a DONE ni tampoco operar si la tarea ya está cerrada
            if (status.equals(Status.DONE) || task.getStatus().equals(Status.DONE)) {
                throw new IllegalArgumentException("Un miembro no puede cerrar tareas ni reabrir tareas ya cerradas");
            }

            // Como es miembro, comprobamos que tenga la tarea asignada
            if (task.getAssignee() == null || !task.getAssignee().getId().equals(projectMember.getId())) {
                throw new IllegalArgumentException("Un miembro no puede cambiar una tarea que no tiene asignada");
            }
        }

        // Cambiamos el estado
        task.setStatus(status);
        return task;
    }

    @Transactional
    public Task assignTask (Long taskId, Long assignerId, Long assignedId) {
        // Comprobamos si la tarea existe
        Task task = getTask(taskId);

        // Comprueba que la tarea no tenga a nadie asignado ya
        if (task.getAssignee() != null) {
            throw new IllegalArgumentException("Esta tarea ya tiene a alguien asignado");
        }

        // Comprobamos que la tarea no esté cerrada
        validateTaskNotClosed(task);

        // Buscamos al miembro que quiere asignarle la tarea a otro
        ProjectMember assigner = getProjectMember(assignerId);

        // Comprobamos que la tarea y el assigner sean del mismo proyecto
        if (!task.getProject().getId().equals(assigner.getProject().getId())) {
            throw new IllegalArgumentException("El assigner y la tarea no pertenecen al mismo proyecto");
        }

        // Comprobamos si el usuario se está intentando asignar la tarea a sí mismo
        boolean autoAssign = assignerId.equals(assignedId);

        // Lógica si el usuario es un miembro
        if (assigner.getRole() == ProjectRole.MEMBER) {
            // El miembro solo puede asignarse a sí mismo tareas
            if (!autoAssign) {
                throw new IllegalArgumentException("Un miembro no puede asignar una tarea a alguien que no sea el mismo");
            }

            // Le asignamos la tarea
            task.setAssignee(assigner);
            return task;
        }

        // Comprobamos si el admin se esta asignando la tarea a sí mismo
        if (autoAssign) {
            // Le asignamos la tarea
            task.setAssignee(assigner);
            return task;
        }

        // Comprobamos que el otro usuario exista en el proyecto
        ProjectMember assigned = getProjectMember(assignedId);

        // Comprobamos que la tarea y el assigned sean del mismo proyecto
        if (!task.getProject().getId().equals(assigned.getProject().getId())) {
            throw new IllegalArgumentException("El assigned y la tarea no pertenecen al mismo proyecto");
        }

        // Le asignamos la tarea
        task.setAssignee(assigned);
        return task;
    }

    @Transactional
    public Task unassignTask (Long taskId, Long projectMemberId) {
        // Comprobamos si la tarea existe
        Task task = getTask(taskId);

        // Comprobamos que la tarea no esté cerrada
        validateTaskNotClosed(task);

        // Comprobamos que la tarea tenga a alguien asignado
        if (task.getAssignee() == null) {
            throw new IllegalArgumentException("La tarea no tiene ningún miembro asignado");
        }

        // Comprobamos que el projectMember exista y este en el mismo proyecto que la tarea
        ProjectMember projectMember = getProjectMember(projectMemberId);

        // Comprobamos que la tarea y el miembro sean del mismo proyecto
        validateSameProject(task, projectMember);

        // Comprobamos si el projectMember es admin o member
        if (projectMember.getRole() == ProjectRole.ADMIN) {
            // Si es admin no comprobamos nada más
            task.setAssignee(null);
            return task;
        }

        // Si es miembro comprobamos que tenga esa tarea asignada
        if (!task.getAssignee().getId().equals(projectMember.getId())) {
            throw new IllegalArgumentException("Un miembro solo puede desasignar una tarea que él tenga asignada");
        }

        // Si la tiene asignada la desasignamos
        task.setAssignee(null);
        return task;
    }

    @Transactional
    public Task updateTask (Long taskId, String title, String description, Long updaterId) {
        // Comprobamos que el título esté bien escrito
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la tarea no puede estar vacío ni ser nulo");
        }

        // Comprobamos si la tarea existe
        Task task = getTask(taskId);

        // Comprobamos que la tarea no esté cerrada
        validateTaskNotClosed(task);

        // Comprobamos que el projectMember exista
        ProjectMember updater = getProjectMember(updaterId);

        // Comprobamos que la tarea y el que la intenta cambiar pertenecen al mismo proyecto
        validateSameProject(task, updater);

        // Comprobamos que el que está intentando cambiar la tarea sea admin
        validateAdmin(updater);

        // Actualizamos la tarea
        task.setTitle(title);
        task.setDescription(description);

        return task;
    }

    @Transactional
    public Task changePriority (Long taskId, Long projectMemberId, Priority priority) {
        // Comprobamos que el priority no sea nulo
        if (priority == null) {
            throw new IllegalArgumentException("La prioridad no puede ser nula");
        }

        // Buscamos la tarea
        Task task = getTask(taskId);

        // Comprobamos que la tarea no esté cerrada
        validateTaskNotClosed(task);

        // Comprobamos que la tarea no tenga ya la prioridad que queremos poner
        if (task.getPriority() == priority) {
            throw new IllegalArgumentException("La tarea ya tenía esa prioridad");
        }

        // Buscamos el projectMember
        ProjectMember projectMember = getProjectMember(projectMemberId);

        // Comprobamos que la tarea y el projectMember sean del mismo proyecto
        validateSameProject(task, projectMember);

        // Solo un admin puede cambiar la prioridad de una tarea
        validateAdmin(projectMember);

        // Cambiamos la prioridad
        task.setPriority(priority);
        return task;
    }

    @Transactional
    public void deleteTask (Long taskId, Long projectMemberId) {
        // Buscamos la tarea
        Task task = getTask(taskId);

        // Buscamos el projectMember
        ProjectMember projectMember = getProjectMember(projectMemberId);

        // Comprobamos que la tarea y el projectMember sean del mismo proyecto
        validateSameProject(task, projectMember);

        // Solo un admin puede borrar una tarea
        validateAdmin(projectMember);

        // Buscamos el proyecto
        Project project = task.getProject();

        // Eliminamos la tarea
        project.removeTask(task);
        projectRepository.save(project);
    }

    // Métodos privados para refactorización
    private Task getTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("No existe una tarea con ese id"));
    }

    private ProjectMember getProjectMember(Long projectMemberId) {
        return projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() -> new EntityNotFoundException("No existe un miembro con el id " + projectMemberId));
    }

    private static void validateSameProject(Task task, ProjectMember projectMember) {
        if (!task.getProject().getId().equals(projectMember.getProject().getId())) {
            throw new IllegalArgumentException("El projectMember y la tarea no pertenecen al mismo proyecto");
        }
    }

    private static void validateAdmin(ProjectMember projectMember) {
        if (projectMember.getRole() != ProjectRole.ADMIN) {
            throw new IllegalArgumentException("Solo un admin puede cambiar una tarea");
        }
    }

    private static void validateTaskNotClosed(Task task) {
        if (task.getStatus() == Status.DONE) {
            throw new IllegalArgumentException("La tarea esta cerrada, hay que reabrirla antes de modificar datos");
        }
    }
}
