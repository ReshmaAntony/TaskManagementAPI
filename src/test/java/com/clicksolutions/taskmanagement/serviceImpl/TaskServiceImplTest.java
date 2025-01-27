package com.clicksolutions.taskmanagement.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.clicksolutions.taskmanagement.dto.Task;
import com.clicksolutions.taskmanagement.entity.Status;
import com.clicksolutions.taskmanagement.entity.TaskEntity;
import com.clicksolutions.taskmanagement.exception.TaskNotFoundException;
import com.clicksolutions.taskmanagement.repository.TaskRepository;

class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTask() {
        Task createTaskRequest = new Task(1L, "Test Task", Status.PENDING, "Test description", "Admin", LocalDateTime.now(), LocalDateTime.now());
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(1L);
        taskEntity.setTaskName("Test Task");
        taskEntity.setStatus(Status.PENDING);
        taskEntity.setDescription("Test description");

        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

        Task createdTask = taskService.createTask(createTaskRequest);

        assertNotNull(createdTask);
        assertEquals(1L, createdTask.getTaskName());
        assertEquals("Test Task", createdTask.getTaskName());
        verify(taskRepository, times(1)).save(any(TaskEntity.class));
    }

    @Test
    void testGetTaskById() {
        long taskId = 1L;
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setTaskName("Sample Task");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        Task fetchedTask = taskService.getTaskById(taskId);

        assertNotNull(fetchedTask);
        assertEquals(taskId, fetchedTask.getTaskId());
        assertEquals("Sample Task", fetchedTask.getTaskName());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void testGetTaskById_NotFound() {
        long taskId = 999L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(taskId));

        assertEquals("Task not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void testGetTasksByStatus() {
        String status = "PENDING";
        List<TaskEntity> tasks = new ArrayList<>();
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(1L);
        taskEntity.setTaskName("Task 1");
        taskEntity.setStatus(Status.PENDING);
        tasks.add(taskEntity);

        when(taskRepository.findByStatus(Status.PENDING)).thenReturn(tasks);

        List<Task> fetchedTasks = taskService.getTasksByStatus(status);

        assertNotNull(fetchedTasks);
        assertEquals(1, fetchedTasks.size());
        assertEquals("Task 1", fetchedTasks.get(0).getTaskName());
        verify(taskRepository, times(1)).findByStatus(Status.PENDING);
    }

    @Test
    void testUpdateTask() {
        long taskId = 1L;
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setTaskName("Old Task");

        Task updateTaskRequest = new Task(taskId, "Updated Task", Status.COMPLETED, "Updated description", "Admin", LocalDateTime.now(), LocalDateTime.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

        Task updatedTask = taskService.updateTask(taskId, updateTaskRequest);

        assertNotNull(updatedTask);
        assertEquals(taskId, updatedTask.getTaskId());
        assertEquals("Updated Task", updatedTask.getTaskName());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(TaskEntity.class));
    }

    @Test
    void testUpdateTask_NotFound() {
        long taskId = 999L;
        Task updateTaskRequest = new Task(taskId, "Updated Task", Status.COMPLETED, "Updated description", "Admin", LocalDateTime.now(), LocalDateTime.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(taskId, updateTaskRequest));

        assertEquals("Task with id 999 not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void testDeleteTask() {
        long taskId = 1L;

        when(taskRepository.existsById(taskId)).thenReturn(true);

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).existsById(taskId);
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void testDeleteTask_NotFound() {
        long taskId = 999L;

        when(taskRepository.existsById(taskId)).thenReturn(false);

        Exception exception = assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(taskId));

        assertEquals("Task with id 999 not found", exception.getMessage());
        verify(taskRepository, times(1)).existsById(taskId);
        verify(taskRepository, never()).deleteById(taskId);
    }
}
