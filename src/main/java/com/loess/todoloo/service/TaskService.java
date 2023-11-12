package com.loess.todoloo.service;

import com.loess.todoloo.model.dto.request.TaskInfoRequest;
import com.loess.todoloo.model.dto.response.TaskInfoResponse;

import java.util.List;

public interface TaskService {

    TaskInfoResponse createTask(Long userId, TaskInfoRequest request);

    TaskInfoResponse getTask(Long userId, Long taskId);

    TaskInfoResponse changeTask(Long userId, Long taskId, TaskInfoRequest request);

    TaskInfoResponse finishTask(Long userId, Long taskId, TaskInfoRequest request);

    List<TaskInfoResponse> getMyTasks(Long userId, String sortBy, String sortOrder);

    List<TaskInfoResponse> getMyFamilyTasks(Long userId, Long familyMemberId, String sortBy, String sortOrder);

}
