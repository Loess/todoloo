package com.loess.todoloo.controllers;

import com.loess.todoloo.model.dto.request.TaskInfoRequest;
import com.loess.todoloo.model.dto.response.TaskInfoResponse;
import com.loess.todoloo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Задачи")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor //конструктор для инъекции сервиса
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/new")
    @Operation(summary = "Создать новую задачу")
    public TaskInfoResponse createTask(@RequestHeader("userid") Long userId, @RequestBody TaskInfoRequest request) {
        return taskService.createTask(userId,request);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Получить описание задачи")
    public TaskInfoResponse getTask(@RequestHeader("userid") Long userId, @PathVariable Long taskId) {
        return taskService.getTask(userId,taskId);
    }

    @GetMapping("/my")
    @Operation(summary = "Получить мои задачи")
    public List<TaskInfoResponse> getMyTasks(@RequestHeader("userid") Long userId) {
        return taskService.getMyTasks(userId);
    }

    @GetMapping("/myFamily/{familyMemberId}")
    @Operation(summary = "Получить задачи пользователя из моей семьи")
    public List<TaskInfoResponse> getMyTasks(@RequestHeader("userid") Long userId, @PathVariable Long familyMemberId) {
        return taskService.getMyFamilyTasks(userId,familyMemberId);
    }

    @PatchMapping("/change/{taskId}")
    @Operation(summary = "Изменить задачу")
    public TaskInfoResponse changeTask(@RequestHeader("userid") Long userId,
                                       @PathVariable Long taskId,
                                       @RequestBody TaskInfoRequest request) {
        return taskService.changeTask(userId, taskId, request);
    }

}