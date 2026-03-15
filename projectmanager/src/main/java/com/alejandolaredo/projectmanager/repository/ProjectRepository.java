package com.alejandolaredo.projectmanager.repository;

import com.alejandolaredo.projectmanager.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
