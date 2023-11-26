package com.loess.todoloo.controllers;

import com.loess.todoloo.model.dto.request.TaskInfoRequest;
import com.loess.todoloo.model.dto.response.TaskInfoResponse;
import com.loess.todoloo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Задачи")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor //конструктор для инъекции сервиса
@SecurityRequirement(name = "Authorization")
public class TaskController {

    private final TaskService taskService;

    @PutMapping("/new")
    @Operation(summary = "Создать новую задачу")
    public TaskInfoResponse createTask(@RequestHeader("userid") Long userId, @RequestBody TaskInfoRequest request) {
        return taskService.createTask(userId, request);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Получить описание задачи")
    public TaskInfoResponse getTask(@RequestHeader("userid") Long userId, @PathVariable Long taskId) {
        return taskService.getTask(userId, taskId);
    }

    @GetMapping("/my")
    @Operation(summary = "Получить мои задачи")
    public List<TaskInfoResponse> getMyTasks(@RequestHeader("userid") Long userId,
                                             @RequestParam(defaultValue = "priority") String sortBy,
                                             @RequestParam(defaultValue = "ASC") String sortOrder
    ) {
        return taskService.getMyTasks(userId, sortBy, sortOrder);
    }

    @GetMapping("/myFamily/{familyMemberId}")
    @Operation(summary = "Получить задачи пользователя из моей семьи")
    public List<TaskInfoResponse> getMyTasks(@RequestHeader("userid") Long userId,
                                             @PathVariable Long familyMemberId,
                                             @RequestParam(defaultValue = "priority") String sortBy,
                                             @RequestParam(defaultValue = "ASC") String sortOrder) {
        return taskService.getMyFamilyTasks(userId, familyMemberId, sortBy, sortOrder);
    }

    @PatchMapping("/change/{taskId}")
    @Operation(summary = "Изменить задачу")
    public TaskInfoResponse changeTask(@RequestHeader("userid") Long userId,
                                       @PathVariable Long taskId,
                                       @RequestBody TaskInfoRequest request) {
        return taskService.changeTask(userId, taskId, request);
    }

    @PatchMapping("/finish/{taskId}")
    @Operation(summary = "Выполнить задачу")
    public TaskInfoResponse finishTask(@RequestHeader("userid") Long userId,
                                       @PathVariable Long taskId,
                                       @RequestBody TaskInfoRequest request) {
        return taskService.finishTask(userId, taskId, request);
    }

}
