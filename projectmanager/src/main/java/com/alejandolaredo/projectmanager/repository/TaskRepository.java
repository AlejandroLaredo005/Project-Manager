package com.alejandolaredo.projectmanager.repository;

import com.alejandolaredo.projectmanager.model.Status;
import com.alejandolaredo.projectmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    List<Task> findByAssigneeId(Long assigneeId);
    List<Task> findByAssignee_IdAndProject_Id(Long assigneeId, Long projectId);
    List<Task> findByProject_IdAndStatus(Long projectId, Status status);
}
