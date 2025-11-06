package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.Resources;
import org.application.tsiktsemestraljob.demo.Repository.ResourcesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourcesService {
    private final ResourcesRepository resourcesRepository;

    public List<Resources> findAll() {
        return resourcesRepository.findAll();
    }

    public Resources create(Resources resources) {
        return resourcesRepository.save(resources);
    }

    public Optional<Resources> findById(Long id) {
        return resourcesRepository.findById(id);
    }

    public Resources update(Long id, Resources resources) {
        resourcesRepository.deleteById(id);
        return resourcesRepository.save(resources);
    }

    public void delete(Long id) {
        resourcesRepository.deleteById(id);
    }
}
