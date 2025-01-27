package com.clicksolutions.taskmanagement.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clicksolutions.taskmanagement.dto.Task;
import com.clicksolutions.taskmanagement.exception.InvalidTaskException;
import com.clicksolutions.taskmanagement.exception.TaskNotFoundException;
import com.clicksolutions.taskmanagement.serviceImpl.TaskServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/v1/api/tasks")
public class TaskController {
	
	private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

	@Autowired
	TaskServiceImpl taskService;

	 @Operation(
		        summary = "Create a new task",
		        description = "Creates a task in the database and returns the created task.",
		        responses = {
		            @ApiResponse(responseCode = "201", description = "Task successfully created", 
		                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
		            @ApiResponse(responseCode = "400", description = "Invalid input")
		        }
		    )
	@PostMapping("/create-task")
	public ResponseEntity<Task> createTask(@RequestBody Task createTaskRequest){
		 
		 logger.info("Received request to create task: {}", createTaskRequest);

		if (createTaskRequest == null || createTaskRequest.getTaskId() == 0) {
	            throw new InvalidTaskException("Task details are invalid or missing");
	        }
		Task createTaskResponse = taskService.createTask(createTaskRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(createTaskResponse);
	}

	   @Operation(
		        summary = "Retrieve a task by ID",
		        description = "Fetches a task using its ID from the DB.",
		        responses = {
		            @ApiResponse(responseCode = "200", description = "Task found", 
		                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
		            @ApiResponse(responseCode = "404", description = "Task not found")
		        }
		    )
	@GetMapping("/retrieve-task/{id}")
	public ResponseEntity<Task> getTaskById(@PathVariable long id){
		  
		   Task task = taskService.getTaskById(id);
		   if (task == null) {
	            throw new TaskNotFoundException("Task with ID " + id + " not found");
	        }
		return ResponseEntity.ofNullable(task);
	}

	   @Operation(
		        summary = "Retrieve tasks by status",
		        description = "Fetches tasks based on their status from the DB.",
		        responses = {
		            @ApiResponse(responseCode = "200", description = "List of tasks based on the given standard", 
		                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
		            @ApiResponse(responseCode = "400", description = "Invalid status value provided")
		        }
		    )
	@GetMapping("/get-task")
	public ResponseEntity<List<Task>> getTasksByStatus(@RequestParam(required = true) String status) {
		   
		   if (status == null || status.isEmpty()) {
	            throw new InvalidTaskException("Status cannot be null or empty");
	        }
		List<Task> tasks = taskService.getTasksByStatus(status);
		 if (tasks == null || tasks.isEmpty()) {
	            throw new TaskNotFoundException("No tasks found with status: " + status);
	        }
		return ResponseEntity.ofNullable(tasks);
	}

	   @Operation(
		        summary = "Update a task",
		        description = "Updates an existing task by ID.",
		        responses = {
		            @ApiResponse(responseCode = "200", description = "Task updated successfully", 
		                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
		            @ApiResponse(responseCode = "404", description = "Task not found")
		        }
		    )
	@PutMapping("/update-task/{id}")
	public ResponseEntity<Task> updateTask(@PathVariable long id,@RequestBody Task updateTaskRequest){

		   if (updateTaskRequest == null || updateTaskRequest.getTaskId() == 0) {
	            throw new InvalidTaskException("Task details are invalid or missing");
	        }
		Task updatedTaskResponse = taskService.updateTask(id,updateTaskRequest);
		
		if (updatedTaskResponse == null) {
            throw new TaskNotFoundException("Task with ID " + id + " not found");
        }
		return ResponseEntity.ofNullable(updatedTaskResponse);
	}

	   @Operation(
		        summary = "Delete a task",
		        description = "Deletes a task by ID. Only accessible by admins.",
		        responses = {
		            @ApiResponse(responseCode = "204", description = "Task successfully deleted"),
		            @ApiResponse(responseCode = "403", description = "Forbidden"),
		            @ApiResponse(responseCode = "404", description = "Task not found")
		        }
		    )
	  @DeleteMapping("/delete-task/{id}") 
	  @Secured("ROLE_ADMIN") 
	  public ResponseEntity<Void> deleteTask(@PathVariable long id) {
	  
		   if (id==0) {
	            throw new TaskNotFoundException("Task with ID " + id + " not found");
	        }
		  taskService.deleteTask(id); 
		  return ResponseEntity.noContent().build(); 
	  }
	 



}
