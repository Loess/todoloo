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

import static com.loess.todoloo.utils.AuthUtils.extractUserIdFromToken;

@Tag(name = "Задачи")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor //конструктор для инъекции сервиса
@SecurityRequirement(name = "Authorization")
public class TaskController {

    private final TaskService taskService;

    @PutMapping("/new")
    @Operation(summary = "Создать новую задачу")
    public TaskInfoResponse createTask(@RequestHeader("Authorization") String token, @RequestBody TaskInfoRequest request) {
        return taskService.createTask(extractUserIdFromToken(token), request);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Получить описание задачи")
    public TaskInfoResponse getTask(@RequestHeader("Authorization") String token, @PathVariable Long taskId) {
        return taskService.getTask(extractUserIdFromToken(token), taskId);
    }

    @GetMapping("/my")
    @Operation(summary = "Получить мои задачи")
    public List<TaskInfoResponse> getMyTasks(@RequestHeader("Authorization") String token,
                                             @RequestParam(defaultValue = "priority") String sortBy,
                                             @RequestParam(defaultValue = "ASC") String sortOrder
    ) {
        return taskService.getMyTasks(extractUserIdFromToken(token), sortBy, sortOrder);
    }

    @GetMapping("/myFamily/{familyMemberId}")
    @Operation(summary = "Получить задачи пользователя из моей семьи")
    public List<TaskInfoResponse> getMyTasks(@RequestHeader("Authorization") String token,
                                             @PathVariable Long familyMemberId,
                                             @RequestParam(defaultValue = "priority") String sortBy,
                                             @RequestParam(defaultValue = "ASC") String sortOrder) {
        return taskService.getMyFamilyTasks(extractUserIdFromToken(token), familyMemberId, sortBy, sortOrder);
    }

    @PatchMapping("/change/{taskId}")
    @Operation(summary = "Изменить задачу")
    public TaskInfoResponse changeTask(@RequestHeader("Authorization") String token,
                                       @PathVariable Long taskId,
                                       @RequestBody TaskInfoRequest request) {
        return taskService.changeTask(extractUserIdFromToken(token), taskId, request);
    }

    @PatchMapping("/finish/{taskId}")
    @Operation(summary = "Выполнить задачу")
    public TaskInfoResponse finishTask(@RequestHeader("Authorization") String token,
                                       @PathVariable Long taskId,
                                       @RequestBody TaskInfoRequest request) {
        return taskService.finishTask(extractUserIdFromToken(token), taskId, request);
    }

}
