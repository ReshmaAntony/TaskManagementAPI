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
	
	 private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

	@Autowired
	TaskRepository taskRepository;

	public Task createTask(Task createTaskRequest) {

		TaskEntity createTaskResponse = new TaskEntity();
		BeanUtils.copyProperties(createTaskRequest, createTaskResponse);
		createTaskResponse = taskRepository.save(createTaskResponse);
		BeanUtils.copyProperties(createTaskResponse, createTaskRequest);
		return createTaskRequest;
	}

	public Task getTaskById(long id) {
		
		 if (id <= 0) {
	            logger.error("Invalid Task ID: {}", id);
	            throw new IllegalArgumentException("Invalid Task ID");
	        }
		TaskEntity getTaskByIdRepoResponse = new TaskEntity();
		getTaskByIdRepoResponse =taskRepository.findById(id) .orElseThrow(() -> new
		  TaskNotFoundException("Task not found"));
		 Task getTaskByIdResponse = new Task();
		 BeanUtils.copyProperties(getTaskByIdRepoResponse, getTaskByIdResponse);
			return getTaskByIdResponse;
		 
	}

	public List<Task> getTasksByStatus(String status) {

		Status taskStatus = status != null ? Status.valueOf(status.toUpperCase()) : null;

		// Fetch tasks from repository
		List<TaskEntity> tasksRepoResponse = (taskStatus != null) ? taskRepository.findByStatus(taskStatus)
				: taskRepository.findAll();
		

		
		  // Convert to DTO 
		return tasksRepoResponse.stream().map(task -> new Task
				(task.getId(), 
				 task.getTaskName(), 
				 task.getStatus(),
		         task.getDescription(), 
		         task.getCreatedBy(), 
		         task.getCreatedAt(), 
		         task.getUpdatedAt()))
				.collect(Collectors.toList());
		 
	}

	public Task updateTask(long id, Task updateTaskRequest) {

		
		  Task updatedTaskResponse = new Task(); Optional<TaskEntity> existingTask =
		  taskRepository.findById(id);
		  
		  if (existingTask.isPresent()) { // Task exists, retrieving it 
			  TaskEntity taskRepoReponse = existingTask.get();
		  
		  // Performing the update logic here
		  taskRepoReponse.setTaskName(updateTaskRequest.getTaskName());
		  taskRepoReponse.setDescription(updateTaskRequest.getDescription());
		  taskRepoReponse.setStatus(updateTaskRequest.getStatus());
		  
		  // Saving the updated task to Repository
		  taskRepository.save(taskRepoReponse);
		  
		  //copying the entity object properties to dto object
		  BeanUtils.copyProperties(taskRepoReponse, updatedTaskResponse); return
		  updatedTaskResponse; } else { // If the task with the given ID does not exist
		  throw new TaskNotFoundException("Task with id " + id + " not found"); }
		 
	}

	
	  public void deleteTask(Long id)throws TaskNotFoundException {
	  
	  
	  // Checking if the task exists 
		  if (!taskRepository.existsById(id)) { throw
	  new TaskNotFoundException("Task with id " + id + " not found"); }
	  
	  
	  // Deleting the task based on the id 
		  taskRepository.deleteById(id); }
	 

}
