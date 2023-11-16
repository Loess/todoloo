package com.loess.todoloo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loess.todoloo.exceptions.CustomException;
import com.loess.todoloo.model.db.entity.Family;
import com.loess.todoloo.model.db.entity.Notification;
import com.loess.todoloo.model.db.entity.Task;
import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.db.repository.TaskRepo;
import com.loess.todoloo.model.dto.request.TaskInfoRequest;
import com.loess.todoloo.model.dto.response.TaskInfoResponse;
import com.loess.todoloo.model.enums.Role;
import com.loess.todoloo.model.enums.TaskStatus;
import com.loess.todoloo.service.NotificationService;
import com.loess.todoloo.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskServiceImplTest {

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskRepo taskRepo;
    @Mock
    private UserService userService;
    @Mock
    private NotificationService notificationService;

    @Spy
    private ObjectMapper mapper;

    @Test
    public void createTask() {
        Family family = new Family();

        Long userId = 1L;
        User author = new User();
        author.setId(userId);
        author.setFamily(family);
        author.setRole(Role.PARENT);

        Long assigneeId = 2L;
        User assignee = new User();
        assignee.setId(assigneeId);
        assignee.setFamily(family);
        assignee.setRole(Role.KID);

        TaskInfoRequest request = new TaskInfoRequest();
        request.setAssigneeId(2L);
        request.setSummary("Test task");

        when(userService.getUserById(userId)).thenReturn(author);
        when(userService.getUserByIdNullable(assigneeId)).thenReturn(assignee);
        when(taskRepo.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Мок сохранения и возврат переданного объекта

        TaskInfoResponse response = taskService.createTask(userId, request);

        assertNotNull(response);
        assertEquals(request.getSummary(), response.getSummary());
        assertEquals(userId, response.getAuthor().getId());
        assertEquals(assigneeId, response.getAssignee().getId());
        assertNotNull(response.getCreationDate());
        assertNotNull(response.getStatusDate());

        verify(userService).getUserById(userId);
        verify(userService).getUserByIdNullable(assigneeId);
        verify(taskRepo, times(1)).save(any(Task.class));
    }

    @Test(expected = CustomException.class)
    public void createTask_wrongRights() {
        Family family = new Family();

        Long userId = 1L;
        User author = new User();
        author.setId(userId);
        author.setFamily(family);
        author.setRole(Role.KID);

        Long assigneeId = 2L;
        User assignee = new User();
        assignee.setId(assigneeId);
        assignee.setFamily(family);
        assignee.setRole(Role.PARENT);

        TaskInfoRequest request = new TaskInfoRequest();
        request.setAssigneeId(2L);
        request.setSummary("Test task");

        when(userService.getUserById(userId)).thenReturn(author);
        when(userService.getUserByIdNullable(assigneeId)).thenReturn(assignee);

        taskService.createTask(userId, request);
    }

    @Test(expected = CustomException.class)
    public void createTask_emptySummary() {
        Family family = new Family();

        Long userId = 1L;
        User author = new User();
        author.setId(userId);
        author.setFamily(family);
        author.setRole(Role.KID);

        TaskInfoRequest request = new TaskInfoRequest();
        request.setAssigneeId(2L);
        request.setSummary("");

        when(userService.getUserById(userId)).thenReturn(author);

        taskService.createTask(userId, request);
    }

    @Test
    public void getTask() {
        Family family = new Family();

        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setRole(Role.PARENT);
        user.setFamily(family);

        User assignee = new User();
        assignee.setId(3L);
        assignee.setRole(Role.KID);
        assignee.setFamily(family);

        Long taskId = 2L;
        Task task = new Task();
        task.setId(taskId);
        task.setAssignee(assignee);
        task.setAuthor(user);


        when(userService.getUserById(userId)).thenReturn(user);
        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));

        TaskInfoResponse response = taskService.getTask(userId, taskId);

        assertNotNull(response);

        verify(userService).getUserById(userId);
        verify(taskRepo).findById(taskId);
    }

    @Test(expected = CustomException.class)
    public void getTask_wrongRole() {
        Family family = new Family();

        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setRole(Role.PARENT);
        user.setFamily(family);

        User assignee = new User();
        assignee.setId(3L);
        assignee.setRole(Role.PARENT);
        assignee.setFamily(family);

        Long taskId = 2L;
        Task task = new Task();
        task.setId(taskId);
        task.setAssignee(assignee);
        task.setAuthor(assignee);

        when(userService.getUserById(userId)).thenReturn(user);
        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));

        taskService.getTask(userId, taskId);
    }

    @Test
    public void changeTask() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Long taskId = 2L;
        Task task = new Task();
        task.setId(taskId);
        task.setAuthor(user);

        TaskInfoRequest request = new TaskInfoRequest();
        request.setSummary("New Summary");

        when(userService.getUserById(userId)).thenReturn(user);
        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepo.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Мок сохранения и возврат переданного объекта

        TaskInfoResponse response = taskService.changeTask(userId, taskId, request);

        assertNotNull(response);
        verify(userService).getUserById(userId);
        verify(taskRepo).findById(taskId);
        verify(taskRepo).save(task);
    }

    @Test(expected = CustomException.class)
    public void changeTask_wrongRights() {
        Family family = new Family();
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setFamily(family);
        user.setRole(Role.KID);

        Long assigneeId = 1L;
        User assignee = new User();
        assignee.setId(assigneeId);
        assignee.setFamily(null);
        assignee.setRole(Role.PARENT);

        Long taskId = 2L;
        Task task = new Task();
        task.setId(taskId);
        task.setAuthor(assignee);
        task.setAssignee(assignee);

        TaskInfoRequest request = new TaskInfoRequest();
        request.setSummary("New Summary");

        when(userService.getUserById(userId)).thenReturn(assignee);
        when(userService.getUserById(assigneeId)).thenReturn(user);
        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));

        TaskInfoResponse response = taskService.changeTask(assigneeId, taskId, request);

        assertNotNull(response);
        verify(userService).getUserById(userId);
        verify(taskRepo).findById(taskId);
        verify(taskRepo).save(task);
    }

    @Test
    public void finishTask_noVerify() {
        Long userId = 1L;
        Long assigneeId = 2L;
        Long taskId = 2L;

        User user = new User();
        user.setId(userId);

        User assignee = new User();
        assignee.setId(assigneeId);
        assignee.setRole(Role.KID);

        Task task = new Task();
        task.setId(taskId);
        task.setAssignee(assignee);
        task.setAuthor(user);
        task.setStatus(TaskStatus.OPENED);
        task.setRightAnswers("answer1\nanswer2\nanswer3");
        task.setNeedVerify(false);

        TaskInfoRequest request = new TaskInfoRequest();
        request.setRightAnswers("answer2 "); // correct answer

        when(userService.getUserById(userId)).thenReturn(user);
        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepo.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Мок сохранения и возврат переданного объекта

        TaskInfoResponse response = taskService.finishTask(userId, taskId, request);

        assertNotNull(response);
        assertEquals(TaskStatus.DONE, task.getStatus());
        verify(taskRepo).save(task);
    }

    @Test
    public void finishTask_needVerify() {
        Long userId = 1L;
        Long assigneeId = 2L;
        Long taskId = 2L;

        User user = new User();
        user.setId(userId);

        User assignee = new User();
        assignee.setId(assigneeId);
        assignee.setRole(Role.KID);

        Task task = new Task();
        task.setId(taskId);
        task.setAssignee(assignee);
        task.setAuthor(user);
        task.setStatus(TaskStatus.OPENED);
//        task.setRightAnswers("answer1\nanswer2\nanswer3"); //по логике task или верифай, или правильные ответы
        task.setNeedVerify(true);

        TaskInfoRequest request = new TaskInfoRequest();
//        request.setRightAnswers("answer2 "); // correct answer

        when(userService.getUserById(userId)).thenReturn(user);
        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
        when(notificationService.createNotification(any(),any(),any())).thenReturn(new Notification()); // мок assignee
        when(taskRepo.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Мок сохранения и возврат переданного объекта

        TaskInfoResponse response = taskService.finishTask(userId, taskId, request);

        assertNotNull(response);
        assertEquals(TaskStatus.IN_REVIEW, task.getStatus());
        verify(taskRepo).save(task);
    }

    @Test
    public void finishTask_wrongAnswer() {
        Long userId = 1L;
        Long taskId = 2L;

        User user = new User();
        user.setId(userId);

        Task task = new Task();
        task.setId(taskId);
        task.setAssignee(user);
        task.setAuthor(user);
        task.setStatus(TaskStatus.OPENED);
        task.setRightAnswers("answer1\nanswer2\nanswer3");

        TaskInfoRequest request = new TaskInfoRequest();
        request.setRightAnswers("answer20 "); // non-correct answer

        when(userService.getUserById(userId)).thenReturn(user);
        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepo.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Мок сохранения и возврат переданного объекта

        TaskInfoResponse response = taskService.finishTask(userId, taskId, request);

        assertNotNull(response);
        assertEquals(TaskStatus.OPENED, task.getStatus());
        verify(taskRepo).save(task);
    }

    @Test
    public void getMyTasks() {
        Long userId = 1L;
        Long familyMemberId = 2L;
        String sortBy = "date";
        String sortOrder = "asc";

        User user = new User();
        user.setId(userId);
        user.setFamily(new Family());

        User familyMember = new User();
        familyMember.setId(familyMemberId);
        familyMember.setFamily(new Family());

        List<Task> tasks = new ArrayList<>();
        Task task1 = new Task();
        task1.setId(101L);
        task1.setAssignee(familyMember);
        tasks.add(task1);

        Task task2 = new Task();
        task2.setId(102L);
        task2.setAuthor(familyMember);
        tasks.add(task2);

        when(userService.getUserById(userId)).thenReturn(user);
        when(taskRepo.findAllByAssigneeId(userId, Sort.by(Sort.Direction.ASC, "creationDate"))).thenReturn(tasks);

        List<TaskInfoResponse> taskInfoResponses = taskService.getMyTasks(userId, sortBy, sortOrder);

        assertNotNull(taskInfoResponses);
        assertEquals(2, taskInfoResponses.size());
    }

    @Test(expected = CustomException.class)
    public void getMyFamilyTasks() {
        Long userId = 1L;
        Long user2Id = 2L;
        String sortBy = "priority";
        String sortOrder = "desc";

        User user = new User();
        user.setId(userId);

        User user2 = new User();
        user2.setId(user2Id);

        List<Task> tasks = new ArrayList<>();
        Task task1 = new Task();
        task1.setId(101L);
        task1.setAssignee(user2);
        tasks.add(task1);

        Task task2 = new Task();
        task2.setId(102L);
        task2.setAuthor(user2);
        tasks.add(task2);

        when(userService.getUserById(userId)).thenReturn(user);

        taskService.getMyFamilyTasks(userId, user2Id, sortBy, sortOrder);

    }
}