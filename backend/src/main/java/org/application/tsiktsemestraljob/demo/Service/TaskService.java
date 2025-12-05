package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.Task;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Repository.StudyGroupsRepository;
import org.application.tsiktsemestraljob.demo.Repository.TaskRepository;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository repository;
    private final UserRepository userRepository;
    private final StudyGroupsRepository studyGroupsRepository;
    private final ActivityLogsService activityLogsService;

    public Task createTask(Long userId, Long groupId, Task task) {
        User creator = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id " + userId));
        StudyGroups studyGroup = studyGroupsRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("StudyGroup not found with id " + groupId));
        task.setCreatedBy(creator);
        task.setStudyGroup(studyGroup);

        Task saved = repository.save(task);
        activityLogsService.log(creator,
                "CREATE_TASK",
                "TASK-ID : " + saved.getId());

        return saved;
    }

    public Task getTaskById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    public Task updateTask(Long id ,Task task) {
        Task taskToUpdate = repository.findById(id).orElse(null);
        if (taskToUpdate == null) {throw new IllegalArgumentException("Task not found");}
        if(task.getStatus() != null) {taskToUpdate.setStatus(task.getStatus());}
        if(task.getDescription() != null) {taskToUpdate.setDescription(task.getDescription());}
        if(task.getDeadline() != null) {taskToUpdate.setDeadline(task.getDeadline());}
        if(task.getCreatedBy() != null) {taskToUpdate.setCreatedBy(task.getCreatedBy());}
        taskToUpdate.setTitle(task.getTitle());
        taskToUpdate.setCreatedAt(task.getCreatedAt());
        return repository.save(taskToUpdate);
    }

    public void deleteTask(Long taskId, Long userId) {
        Task task = repository.findById(taskId).orElse(null);
        if (task == null) {throw new IllegalArgumentException("Task not found");}

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id " + userId));

        boolean isTaskCreator = task.getCreatedBy().getId().equals(userId);
        boolean isGroupCreator = task.getStudyGroup().getCreatedBy().getId().equals(userId);

        if (!isTaskCreator && !isGroupCreator) {
            throw new IllegalArgumentException("User has no rights to delete this task");
        }

        activityLogsService.log(user,
                "TASK_DELETED",
                "TASK-ID: " + taskId);

        repository.deleteById(taskId);
    }

}
