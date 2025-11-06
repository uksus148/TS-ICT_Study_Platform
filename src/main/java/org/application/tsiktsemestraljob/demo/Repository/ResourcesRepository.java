package org.application.tsiktsemestraljob.demo.Repository;

import org.application.tsiktsemestraljob.demo.Entities.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourcesRepository extends JpaRepository<Resources, Long> {}
