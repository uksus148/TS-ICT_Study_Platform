package org.application.tsiktsemestraljob.demo.Controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "Get all tasks",
            description = "This endpoint implement an get all tasks logic, he takes no parameters and return"
            + "an service-layer method that get all tasks"
    )
    @GetMapping
    public List<TaskResponseDTO> getTasks() {
        return taskService.getAllTasks()
                .stream()
                .map(TaskMapper::toDto)
                .toList();
    }

    @Operation(
            summary = "Get task by id",
            description = "This endpoint implement an get task by id logic, he takes id as parameter and return" +
                    "a service-layer method who get task by id"
    )
    @GetMapping("/{id}")
    public TaskResponseDTO getTask(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return TaskMapper.toDto(task);
    }

    @Operation(
            summary = "Create task",
            description = "This endpoint implement an create task logic, he takes an group id and request dto" +
                    "as parameter,and returns a service-layer method who create task"
    )
    @PostMapping("/{groupId}")
    public TaskResponseDTO createTask(@PathVariable Long groupId ,@RequestBody TaskRequestDTO dto) {
        Task task = taskService.createTask(groupId, TaskMapper.toEntity(dto));
        return TaskMapper.toDto(task);
    }

    @Operation(
            summary = "Update task",
            description = "This endpoint implement an update task logic, he takes id and request dto" +
                    "as parameter, and returns a service-layers method who update task"
    )
    @PutMapping("/{id}")
    public TaskResponseDTO updateTask(@PathVariable Long id, @RequestBody TaskRequestDTO dto) {
        Task newTask = taskService.updateTask(id, TaskMapper.toEntity(dto));
        return TaskMapper.toDto(newTask);
    }

    @Operation(
            summary = "Delete task",
            description = "This endpoint implement an delete task logic, he takes no parameters" +
                    "and returns a service-layer method who delete task"
    )
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
