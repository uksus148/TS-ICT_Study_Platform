package org.application.tsiktsemestraljob.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.Task;
import org.application.tsiktsemestraljob.demo.Service.TaskService;
import org.application.tsiktsemestraljob.demo.DTO.TaskDTO.TaskMapper;
import org.application.tsiktsemestraljob.demo.DTO.TaskDTO.TaskRequestDTO;
import org.application.tsiktsemestraljob.demo.DTO.TaskDTO.TaskResponseDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public List<TaskResponseDTO> getTasks() {
        return taskService.getAllTasks()
                .stream()
                .map(TaskMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public TaskResponseDTO getTask(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return TaskMapper.toDto(task);
    }

    @PostMapping("/{groupId}")
    public TaskResponseDTO createTask(@PathVariable Long groupId ,@RequestBody TaskRequestDTO dto) {
        Task task = taskService.createTask(groupId, TaskMapper.toEntity(dto));
        return TaskMapper.toDto(task);
    }

    @PutMapping("/{id}")
    public TaskResponseDTO updateTask(@PathVariable Long id, @RequestBody TaskRequestDTO dto) {
        Task newTask = taskService.updateTask(id, TaskMapper.toEntity(dto));
        return TaskMapper.toDto(newTask);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
