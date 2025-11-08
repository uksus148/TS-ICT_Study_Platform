package org.application.tsiktsemestraljob.demo.Repository;

import org.application.tsiktsemestraljob.demo.Entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {}
