package com.alejandolaredo.projectmanager.repository;

import com.alejandolaredo.projectmanager.model.Project;
import com.alejandolaredo.projectmanager.model.ProjectMember;
import com.alejandolaredo.projectmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    boolean existsByUserAndProject(User user, Project project);
    Optional<ProjectMember> findByUserIdAndProjectId(Long userId, Long projectId);
}
