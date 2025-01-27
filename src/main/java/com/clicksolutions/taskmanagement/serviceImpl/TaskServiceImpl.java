package com.clicksolutions.taskmanagement.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clicksolutions.taskmanagement.dto.Task;
import com.clicksolutions.taskmanagement.entity.Status;
import com.clicksolutions.taskmanagement.entity.TaskEntity;
import com.clicksolutions.taskmanagement.exception.TaskNotFoundException;
import com.clicksolutions.taskmanagement.repository.TaskRepository;
import com.clicksolutions.taskmanagement.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    TaskRepository taskRepository;

    @Override
    public Task createTask(Task createTaskRequest) {
        logger.info("Creating a new task: {}", createTaskRequest);

        TaskEntity createTaskResponse = new TaskEntity();
      //  BeanUtils.copyProperties(createTaskRequest, createTaskResponse);
      //  logger.info("Task created successfully with ID: {}", createTaskResponse.getId());
        createTaskResponse.setId(createTaskRequest.getTaskId());
        createTaskResponse.setTaskName(createTaskRequest.getTaskName());
        createTaskResponse.setDescription(createTaskRequest.getDescription());
        createTaskResponse.setCreatedAt(createTaskRequest.getCreatedAt());
        createTaskResponse.setCreatedBy(createTaskRequest.getCreatedBy());
        createTaskResponse.setUpdatedAt(createTaskRequest.getUpdatedAt());
        createTaskResponse.setStatus(createTaskRequest.getStatus());
        
        createTaskResponse = taskRepository.save(createTaskResponse);
        
        Task result = new Task();
        logger.info("Task created successfully with ID: {}here is the response from repo", result);
        
        result.setCreatedAt(createTaskResponse.getCreatedAt());
        result.setUpdatedAt(createTaskResponse.getUpdatedAt());
        result.setCreatedBy(createTaskResponse.getCreatedBy());
        result.setDescription(createTaskResponse.getCreatedBy());
        result.setTaskId(createTaskResponse.getId());
        result.setTaskName(createTaskResponse.getTaskName());
        result.setStatus(createTaskResponse.getStatus());

        logger.info("Task created successfully with ID: {}", createTaskResponse.getId());
        return result;
    }

    @Override
    public Task getTaskById(long id) {
        if (id <= 0) {
            logger.error("Invalid Task ID: {}", id);
            throw new IllegalArgumentException("Invalid Task ID");
        }

        logger.info("Fetching task by ID: {}", id);
        TaskEntity getTaskByIdRepoResponse = taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Task not found with ID: {}", id);
                    return new TaskNotFoundException("Task not found");
                });

        Task getTaskByIdResponse = new Task();
        BeanUtils.copyProperties(getTaskByIdRepoResponse, getTaskByIdResponse);

        logger.info("Task fetched successfully: {}", getTaskByIdResponse);
        return getTaskByIdResponse;
    }

    @Override
    public List<Task> getTasksByStatus(String status) {
        logger.info("Fetching tasks by status: {}", status);

        Status taskStatus = (status != null) ? Status.valueOf(status.toUpperCase()) : null;

        List<TaskEntity> tasksRepoResponse = (taskStatus != null) 
                ? taskRepository.findByStatus(taskStatus) 
                : taskRepository.findAll();

        logger.info("Fetched {} task(s) from the repository", tasksRepoResponse.size());

        return tasksRepoResponse.stream()
                .map(task -> new Task(
                        task.getId(),
                        task.getTaskName(),
                        task.getStatus(),
                        task.getDescription(),
                        task.getCreatedBy(),
                        task.getCreatedAt(),
                        task.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public Task updateTask(long id, Task updateTaskRequest) {
        logger.info("Updating task with ID: {}", id);

        Optional<TaskEntity> existingTask = taskRepository.findById(id);

        if (existingTask.isPresent()) {
            TaskEntity taskRepoResponse = existingTask.get();

            logger.info("Task found with ID: {}, performing updates", id);
            taskRepoResponse.setTaskName(updateTaskRequest.getTaskName());
            taskRepoResponse.setDescription(updateTaskRequest.getDescription());
            taskRepoResponse.setStatus(updateTaskRequest.getStatus());

            taskRepository.save(taskRepoResponse);

            Task updatedTaskResponse = new Task();
            BeanUtils.copyProperties(taskRepoResponse, updatedTaskResponse);

            logger.info("Task updated successfully: {}", updatedTaskResponse);
            return updatedTaskResponse;
        } else {
            logger.error("Task with ID: {} not found", id);
            throw new TaskNotFoundException("Task with id " + id + " not found");
        }
    }

    @Override
    public void deleteTask(Long id) throws TaskNotFoundException {
        logger.info("Deleting task with ID: {}", id);

        if (!taskRepository.existsById(id)) {
            logger.error("Task with ID: {} not found", id);
            throw new TaskNotFoundException("Task with id " + id + " not found");
        }

        taskRepository.deleteById(id);
        logger.info("Task with ID: {} deleted successfully", id);
    }
}
