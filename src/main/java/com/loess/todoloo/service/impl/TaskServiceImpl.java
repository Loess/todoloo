package com.loess.todoloo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loess.todoloo.exceptions.CustomException;
import com.loess.todoloo.model.db.entity.Task;
import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.db.repository.TaskRepo;
import com.loess.todoloo.model.dto.request.TaskInfoRequest;
import com.loess.todoloo.model.dto.response.TaskInfoResponse;
import com.loess.todoloo.model.dto.response.UserInfoResponse;
import com.loess.todoloo.model.enums.Role;
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
        User assignee = userService.getUserByIdNullable(request.getAssigneeId());
        //проверка на семью и роль в assignee
        if (assignee != null && assignee != task.getAuthor() &&
                !(task.getAuthor().getFamily() == assignee.getFamily() &&
                        task.getAuthor().getRole() == Role.PARENT && assignee.getRole() == Role.KID))
            throw new CustomException("You cannot assign task to this user", HttpStatus.BAD_REQUEST);

        task.setAssignee(assignee);
        task.setCreationDate(LocalDateTime.now());
        task.setStatusDate(LocalDateTime.now());
        if (StringUtils.isBlank(task.getSummary())) {
            throw new CustomException("Empty summary is not accepted", HttpStatus.BAD_REQUEST);
        }

        Task saved = taskRepo.save(task);
        return addUserNamesToTaskInfoResponse(userId, mapper.convertValue(saved, TaskInfoResponse.class));
    }

    @Override
    public TaskInfoResponse getTask(Long userId, Long taskId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new CustomException("Task not found", HttpStatus.NOT_FOUND));
        User user = userService.getUserById(userId);
        //отдавать только таски свои/семьи/родителю детёвые
        if ((task.getAuthor() != user) &&
                (task.getAssignee() != user) &&
                !(task.getAssignee() != null &&
                        user.getFamily() == task.getAssignee().getFamily() &&
                        user.getRole() == Role.PARENT &&
                        task.getAssignee().getRole() == Role.KID) &&
                !(task.getAuthor() != null &&
                        user.getFamily() == task.getAuthor().getFamily() &&
                        user.getRole() == Role.PARENT &&
                        task.getAuthor().getRole() == Role.KID)
        )
            throw new CustomException("You have no rights to view this task", HttpStatus.FORBIDDEN);

        return addUserNamesToTaskInfoResponse(userId, mapper.convertValue(task, TaskInfoResponse.class));
    }

    @Override
    public TaskInfoResponse changeTask(Long userId, Long taskId, TaskInfoRequest request) {
        Task task = taskRepo.findById(taskId).orElseThrow();
        User user = userService.getUserById(userId);
        User assignee = userService.getUserByIdNullable(request.getAssigneeId());
        //только таски свои/семьи/родителю детёвые
        if ((task.getAuthor() != user) &&
                (task.getAssignee() != user) &&
                !(task.getAssignee() != null &&
                        user.getFamily() == task.getAssignee().getFamily() &&
                        user.getRole() == Role.PARENT &&
                        task.getAssignee().getRole() == Role.KID) &&
                !(task.getAuthor() != null &&
                        user.getFamily() == task.getAuthor().getFamily() &&
                        user.getRole() == Role.PARENT &&
                        task.getAuthor().getRole() == Role.KID)
        )
            throw new CustomException("You have no rights to change this task", HttpStatus.FORBIDDEN);
        //проверка на семью и роль в assignee
        if (assignee != null && assignee != task.getAuthor() &&
                !(task.getAuthor().getFamily() == assignee.getFamily() &&
                        task.getAuthor().getRole() == Role.PARENT && assignee.getRole() == Role.KID))
            throw new CustomException("You cannot assign task to this user", HttpStatus.BAD_REQUEST);

        if (StringUtils.isBlank(request.getSummary())) {
            throw new CustomException("Empty summary is not accepted", HttpStatus.BAD_REQUEST);
        }
        if (request.getStatus() != null && request.getStatus() != task.getStatus()) {
            task.setStatusDate(LocalDateTime.now());
            task.setStatus(request.getStatus());
        } else if (request.getStatus() == null) {
            task.setStatusDate(LocalDateTime.now());
            task.setStatus(TaskStatus.DRAFT);
        }
        //ToDo: Status workflow и выполнение задач + награда или отдельный метод!
        task.setNeedVerify(request.getNeedVerify());
        task.setSummary(request.getSummary());
        task.setDescription(request.getDescription());
        task.setAssignee(assignee);
        task.setTextReward(request.getTextReward());
        task.setDescription(request.getDescription());
        task.setRewardAmount(request.getRewardAmount() == null ? 0 : request.getRewardAmount());
        task.setPriority(request.getPriority() == null ? 0 : request.getPriority());
        task.setRightAnswers(request.getRightAnswers());

        Task save = taskRepo.save(task);
        return mapper.convertValue(save, TaskInfoResponse.class);
    }

    @Override
    public List<TaskInfoResponse> getMyTasks(Long userId) {
        return getMyFamilyTasks(userId, userId);
    }

    @Override
    public List<TaskInfoResponse> getMyFamilyTasks(Long userId, Long familyMemberId) {
        //todo: фильтр, сортировка, поиск по автору

        User user = userService.getUserById(userId);
        User userWatched = userService.getUserById(familyMemberId);
        //отдавать только таски свои/семьи/родителю детёвые
        if (user.getFamily() == null && !userId.equals(familyMemberId)) // нет семьи, смотрит другого
            throw new CustomException("You have no rights to view tasks of this user", HttpStatus.FORBIDDEN);
        else if (!userId.equals(familyMemberId) && //смотрит другого
                !(user.getFamily() == userWatched.getFamily() && user.getRole() == Role.PARENT && userWatched.getRole() == Role.KID))
            throw new CustomException("You have no rights to view tasks of this user", HttpStatus.FORBIDDEN);

        List<TaskInfoResponse> collect = taskRepo.findAllByAssigneeId(familyMemberId).stream()
                .map(task -> mapper.convertValue(task, TaskInfoResponse.class))
                .map(taskInfoResponse -> {
                    return addUserNamesToTaskInfoResponse(userId, taskInfoResponse);
                })
                .collect(Collectors.toList());
        return collect;
    }

    private TaskInfoResponse addUserNamesToTaskInfoResponse(Long userId, TaskInfoResponse taskInfoResponse) {
        UserInfoResponse author = taskInfoResponse.getAuthor();
        if (author != null) {
            UserInfoResponse auth = userService.getUserInfoByIdNullable(userId, author.getId());
            if (auth != null) {
                author.setName(auth.getName());
            }
            taskInfoResponse.setAuthor(author);
        }
        UserInfoResponse assignee = taskInfoResponse.getAssignee();
        if (assignee != null) {
            UserInfoResponse ass = userService.getUserInfoByIdNullable(userId, assignee.getId());
            if (ass != null) {
                assignee.setName(ass.getName());
            }
            taskInfoResponse.setAssignee(assignee);
        }
        return taskInfoResponse;
    }

}
