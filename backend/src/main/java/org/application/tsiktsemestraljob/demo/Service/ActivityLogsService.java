package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.ActivityLogs;
import org.application.tsiktsemestraljob.demo.Repository.ActivityLogsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogsService {
    private final ActivityLogsRepository activityLogsRepository;

    public List<ActivityLogs> findAll() {
        return activityLogsRepository.findAll();
    }

    public ActivityLogs findById(Long id) {
        return activityLogsRepository.findById(id).orElse(null);
    }

    public ActivityLogs create(ActivityLogs activityLogs) {
        return activityLogsRepository.save(activityLogs);
    }

    public ActivityLogs update(ActivityLogs activityLogs) {
        return activityLogsRepository.save(activityLogs);
    }

    public void delete(Long id) {
        activityLogsRepository.deleteById(id);
    }
}
