package org.application.tsiktsemestraljob.demo.Repository;

import org.application.tsiktsemestraljob.demo.Entities.ActivityLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogsRepository extends JpaRepository<ActivityLogs, Long> {
}
