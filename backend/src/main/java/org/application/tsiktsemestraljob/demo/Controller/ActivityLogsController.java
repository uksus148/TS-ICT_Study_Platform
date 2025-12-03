package org.application.tsiktsemestraljob.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.ActivityLogs;
import org.application.tsiktsemestraljob.demo.Service.ActivityLogsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activitylogs")
@RequiredArgsConstructor
public class ActivityLogsController {
    private final ActivityLogsService activityLogsService;

    @GetMapping
    public List<ActivityLogs> getActivityLogs() {
        return activityLogsService.findAll();
    }

    @GetMapping("/{id}")
    public ActivityLogs getActivityLogs(@PathVariable Long id) {
        return activityLogsService.findById(id);
    }

    @PostMapping("/{userId}")
    public ActivityLogs createActivityLogs(@PathVariable Long userId, @RequestBody ActivityLogs activityLogs) {
        return activityLogsService.create(userId, activityLogs);
    }

    @PutMapping("/{id}")
    public ActivityLogs updateActivityLogs(@PathVariable Long id ,@RequestBody ActivityLogs activityLogs) {
        return activityLogsService.update(id, activityLogs);
    }

    @DeleteMapping("/{id}")
    public void deleteActivityLogs(@PathVariable Long id) {
        activityLogsService.delete(id);
    }
}
