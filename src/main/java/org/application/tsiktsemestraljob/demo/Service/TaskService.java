package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.Task;
import org.application.tsiktsemestraljob.demo.Repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository repository;

    public Task createTask(Task task) {
        return repository.save(task);
    }

    public Task getTaskById(Long id) {
        return repository.getById(id);
    }

    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    public Task updateTask(Long id ,Task task) {
        repository.deleteById(id);
        return repository.save(task);
    }

    public void deleteTask(Long id) {
        repository.deleteById(id);
    }

}
