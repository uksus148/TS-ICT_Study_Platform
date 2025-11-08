package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.ActivityLogs;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Repository.ActivityLogsRepository;
import org.application.tsiktsemestraljob.demo.Repository.StudyGroupsRepository;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogsService {
    private final ActivityLogsRepository activityLogsRepository;
    private final UserRepository userRepository;
    private final StudyGroupsRepository studyGroupsRepository;

    public List<ActivityLogs> findAll() {
        return activityLogsRepository.findAll();
    }

    public ActivityLogs findById(Long id) {
        return activityLogsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ActivityLog not found with id " + id));
    }

    public ActivityLogs create(Long userId, ActivityLogs activityLogs) {
        User creator = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id " + userId));

        activityLogs.setUser(creator);
        return activityLogsRepository.save(activityLogs);
    }

    public ActivityLogs update(Long id, ActivityLogs activityLogs) {
        ActivityLogs oldActivityLogs = activityLogsRepository.findById(id).orElse(null);
        if (oldActivityLogs == null) {throw new IllegalArgumentException("ActivityLogs not found");}
        if(activityLogs.getDetails() != null) {oldActivityLogs.setDetails(activityLogs.getDetails());}
        if(activityLogs.getTimestamp() != null) {oldActivityLogs.setTimestamp(activityLogs.getTimestamp());}
        oldActivityLogs.setUser(activityLogs.getUser());
        oldActivityLogs.setAction(activityLogs.getAction());
        return activityLogsRepository.save(oldActivityLogs);
    }

    public void delete(Long id) {
        activityLogsRepository.deleteById(id);
    }
}
