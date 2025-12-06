package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.Task;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Enums.TaskStatus;
import org.application.tsiktsemestraljob.demo.Repository.StudyGroupsRepository;
import org.application.tsiktsemestraljob.demo.Repository.TaskRepository;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.application.tsiktsemestraljob.demo.Authorization.AuthenticationProcess.CurrentUser.getCurrentUser;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository repository;
    private final StudyGroupsRepository studyGroupsRepository;
    private final ActivityLogsService activityLogsService;
    private final MembershipService membershipService;

    public Task createTask(Long groupId, Task task) {
        User creator = getCurrentUser();
        StudyGroups studyGroup = studyGroupsRepository.findById(groupId).orElseThrow(()
                -> new IllegalArgumentException("StudyGroup not found with id " + groupId));

        if(!membershipService.isOwner(creator.getId(), studyGroup.getGroupId())) {
            throw new AccessDeniedException("Only owner of group can create tasks");
        }

        task.setCreatedBy(creator);
        task.setStudyGroup(studyGroup);
        task.setStatus(TaskStatus.TODO);

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
        User creator = getCurrentUser();
        Task taskToUpdate = repository.findById(id).orElse(null);
        if (taskToUpdate == null) {throw new IllegalArgumentException("Task not found");}
        if(!membershipService.isOwner(creator.getId(), taskToUpdate.getStudyGroup().getGroupId())) {
            throw new AccessDeniedException("Only owner of group can create tasks");
        }

        if(task.getStatus() != null) {taskToUpdate.setStatus(task.getStatus());}
        if(task.getDescription() != null) {taskToUpdate.setDescription(task.getDescription());}
        if(task.getDeadline() != null) {taskToUpdate.setDeadline(task.getDeadline());}
        if(task.getCreatedBy() != null) {taskToUpdate.setCreatedBy(task.getCreatedBy());}
        taskToUpdate.setTitle(task.getTitle());
        taskToUpdate.setCreatedAt(task.getCreatedAt());

        activityLogsService.log(creator,
                "UPDATE_TASK"
        ,"TASK-ID : " + task.getId());

        return repository.save(taskToUpdate);
    }

    public void deleteTask(Long taskId) {
        Task task = repository.findById(taskId).orElse(null);
        if (task == null) {throw new IllegalArgumentException("Task not found");}

        User user = getCurrentUser();

        if(!membershipService.isOwner(user.getId(), task.getStudyGroup().getGroupId())) {
            throw new AccessDeniedException("Only owner of group can remove tasks");
        }

        activityLogsService.log(user,
                "TASK_DELETED",
                "TASK-ID: " + taskId);

        repository.deleteById(taskId);
    }

}
