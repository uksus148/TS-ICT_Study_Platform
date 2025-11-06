package org.application.tsiktsemestraljob.demo.Controller;

import lombok.Getter;
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
    public ActivityLogs getActivityLogs(@PathVariable long id) {
        return activityLogsService.findById(id);
    }

    @PostMapping
    public ActivityLogs createActivityLogs(@RequestBody ActivityLogs activityLogs) {
        return activityLogsService.create(activityLogs);
    }

    @PutMapping
    public ActivityLogs updateActivityLogs(@RequestBody ActivityLogs activityLogs) {
        return activityLogsService.update(activityLogs);
    }

    @DeleteMapping("/{id}")
    public void deleteActivityLogs(@PathVariable Long id) {
        activityLogsService.delete(id);
    }
}
