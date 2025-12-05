package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.Resources;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Repository.ResourcesRepository;
import org.application.tsiktsemestraljob.demo.Repository.StudyGroupsRepository;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourcesService {
    private final ResourcesRepository resourcesRepository;
    private final StudyGroupsRepository studyGroupsRepository;
    private final UserRepository userRepository;
    private final ActivityLogsService activityLogsService;

    public List<Resources> findAll() {
        return resourcesRepository.findAll();
    }

    public Resources create(Long userId, Long groupId, Resources resources) {
        User creator = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id " + userId));
        StudyGroups studyGroup = studyGroupsRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("StudyGroup not found with id " + groupId));

        resources.setStudyGroup(studyGroup);
        resources.setUploadedBy(creator);
        Resources saved = resourcesRepository.save(resources);

        activityLogsService.log(creator,
                "RESOURCE_CREATED",
                "RESOURCE-ID: " + saved.getId());
        return saved;
    }

    public Resources findById(Long id) {
        return resourcesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resource cannot be find"));
    }

    public Resources update(Long id, Resources resources) {
       Resources oldResources = resourcesRepository.findById(id).orElse(null);
       if (oldResources == null) {throw new IllegalArgumentException("Resources not found");}
           if(resources.getPathOrUrl() != null) {oldResources.setPathOrUrl(resources.getPathOrUrl());}
           if(resources.getType() != null) {oldResources.setType(resources.getType());}
           oldResources.setTitle(resources.getTitle());
           if(resources.getUploadedAt() != null) {oldResources.setUploadedAt(resources.getUploadedAt());}
           return resourcesRepository.save(oldResources);
    }

    public void delete(Long id) {
        resourcesRepository.deleteById(id);
    }
}
