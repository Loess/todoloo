package com.loess.todoloo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loess.todoloo.exceptions.CustomException;
import com.loess.todoloo.model.db.entity.Task;
import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.db.repository.TaskRepo;
import com.loess.todoloo.model.db.repository.UserRepo;
import com.loess.todoloo.model.dto.request.TaskInfoRequest;
import com.loess.todoloo.model.dto.response.TaskInfoResponse;
import com.loess.todoloo.model.enums.TaskStatus;
import com.loess.todoloo.service.TaskService;
import com.loess.todoloo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor //конструктор для инъекции репо
public class TaskServiceImpl implements TaskService {

    private final ObjectMapper mapper;
    private final TaskRepo taskRepo;
    private final UserService userService;

    @Override
    public TaskInfoResponse createTask(Long userId, TaskInfoRequest request) {
        Task task = mapper.convertValue(request, Task.class);
        task.setAuthor(userService.getUserById(userId));
        task.setAssignee(userService.getUserById(request.getAssigneeId()));
        task.setCreationDate(LocalDateTime.now());
        task.setStatusDate(LocalDateTime.now());
        if (StringUtils.isBlank(task.getSummary())) {
            throw new CustomException("Empty summary is not accepted", HttpStatus.BAD_REQUEST);
        }

        Task saved = taskRepo.save(task);
        return mapper.convertValue(saved, TaskInfoResponse.class);
    }

    @Override
    public TaskInfoResponse getTask(Long userId, Long taskId) {
        //todo: отдавать только таски семьи/родителю детёвые
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new CustomException("Task not found", HttpStatus.NOT_FOUND));

        return mapper.convertValue(task, TaskInfoResponse.class);
    }

    @Override
    public TaskInfoResponse changeTask(Long userId, Long taskId, TaskInfoRequest request) {
        //todo: давать только свои таски/родителю детёвые
        Task task = taskRepo.findById(taskId).orElseThrow();
        User assignee = userService.getUserById(request.getAssigneeId());

        if (StringUtils.isBlank(request.getSummary())) {
            throw new CustomException("Empty summary is not accepted", HttpStatus.BAD_REQUEST);
        }
        if (request.getStatus() != null && request.getStatus() != task.getStatus()) {
            task.setStatusDate(LocalDateTime.now());
            task.setStatus(request.getStatus());
        }
        task.setNeedVerify(request.getNeedVerify());
        task.setSummary(request.getSummary());
        task.setDescription(request.getDescription());
        task.setAssignee(assignee);
        task.setTextReward(request.getTextReward());
        task.setDescription(request.getDescription());
        task.setRewardAmount(request.getRewardAmount() == null ? 0 : request.getRewardAmount());
        task.setPriority(request.getPriority() == null ? 0 : request.getPriority());
        task.setRightAnswers(request.getRightAnswers());

        //спросить как быть, если мб пустые - делать обязательный датасет в TaskInfoRequest ?

        Task save = taskRepo.save(task);
        return mapper.convertValue(save, TaskInfoResponse.class);
    }

    @Override
    public List<TaskInfoResponse> getMyTasks(Long userId) {
        //todo: давать только свои таски, фильтр, сортировка
//        return taskRepo.findAll().stream()
//                //.filter(task -> task.getStatus() != TaskStatus.CLOSED && task.getStatus() != TaskStatus.DONE) //плохо, надо фильтровать в БД
//                .map(task -> mapper.convertValue(task, TaskInfoResponse.class))
//                .collect(Collectors.toList());
        return taskRepo.findAllByAssigneeId(userId).stream()
                .map(task -> mapper.convertValue(task, TaskInfoResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskInfoResponse> getMyFamilyTasks(Long userId, Long familyMemberId) {
        //todo: давать только семейные таски/детей, фильтр, сортировка
        return null;
    }
}
