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

    public Task createTask(Long userId, Long groupId, Task task) {
        User creator = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id " + userId));
        StudyGroups studyGroup = studyGroupsRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("StudyGroup not found with id " + groupId));
        task.setCreatedBy(creator);
        task.setStudyGroup(studyGroup);
        return repository.save(task);
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

    public void deleteTask(Long id) {
        repository.deleteById(id);
    }

}
