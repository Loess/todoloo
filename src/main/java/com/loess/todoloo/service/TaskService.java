package com.loess.todoloo.service;

//import com.example.demo.model.db.entity.User;
import com.loess.todoloo.model.dto.response.TaskInfoResponse;
import com.loess.todoloo.model.dto.request.TaskInfoRequest;

import java.util.List;

public interface TaskService {

    TaskInfoResponse createTask(Long userId, TaskInfoRequest request);

    TaskInfoResponse getTask(Long userId, Long taskId);

    TaskInfoResponse changeTask(Long userId, Long taskId, TaskInfoRequest request);

    List<TaskInfoResponse> getMyTasks(Long userId);

    List<TaskInfoResponse> getMyFamilyTasks(Long userId, Long familyMemberId);
}
