package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.ActivityLogs;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Repository.ActivityLogsRepository;
import org.application.tsiktsemestraljob.demo.Repository.StudyGroupsRepository;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityLogsService {
    private final ActivityLogsRepository activityLogsRepository;

    public void log(User user, String action, String details) {
        ActivityLogs activityLogs = new ActivityLogs();
        activityLogs.setUser(user);
        activityLogs.setAction(action);
        activityLogs.setDetails(details);
        activityLogsRepository.save(activityLogs);
    }
}
