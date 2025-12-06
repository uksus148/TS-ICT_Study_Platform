package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.Resources;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Repository.ResourcesRepository;
import org.application.tsiktsemestraljob.demo.Repository.StudyGroupsRepository;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.application.tsiktsemestraljob.demo.Authorization.AuthenticationProcess.CurrentUser.getCurrentUser;

@Service
@RequiredArgsConstructor
public class ResourcesService {
    private final ResourcesRepository resourcesRepository;
    private final StudyGroupsRepository studyGroupsRepository;
    private final ActivityLogsService activityLogsService;

    public List<Resources> findAll() {
        return resourcesRepository.findAll();
    }

    public Resources create(Long groupId, Resources resources) {
        User creator = getCurrentUser();
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
        User currentUser = getCurrentUser();
       Resources oldResources = resourcesRepository.findById(id).orElse(null);
       if (oldResources == null) {throw new IllegalArgumentException("Resources not found");}

       if(!oldResources.getUploadedBy().equals(currentUser)) {
           throw new AccessDeniedException("You are not owner of this resource");
       }

           if(resources.getPathOrUrl() != null) {oldResources.setPathOrUrl(resources.getPathOrUrl());}
           if(resources.getType() != null) {oldResources.setType(resources.getType());}
           oldResources.setTitle(resources.getTitle());
           if(resources.getUploadedAt() != null) {oldResources.setUploadedAt(resources.getUploadedAt());}

           activityLogsService.log(currentUser,
                   "UPDATE_RESOURCE"
           , "RESOURCE-ID: " + oldResources.getId());
           return resourcesRepository.save(oldResources);
    }

    public void delete(Long id) {
        User currentUser = getCurrentUser();
        Resources resources = findById(id);

        if(!resources.getUploadedBy().equals(currentUser)) {
            throw new AccessDeniedException("You are not owner of this resource");
        }

        activityLogsService.log(currentUser,
                "DELETE_RESOURCES"
        , "RESOURCE-ID: " + resources.getId());

        resourcesRepository.deleteById(id);
    }
}
