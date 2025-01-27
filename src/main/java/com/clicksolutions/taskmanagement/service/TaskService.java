package com.clicksolutions.taskmanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clicksolutions.taskmanagement.dto.Task;

@Service
public interface TaskService {

	 Task createTask(Task task);
	
	 Task getTaskById(long id);
	
	 List<Task> getTasksByStatus(String status);
	
	 Task updateTask(long id, Task task);
	
	 void deleteTask(Long id);
}
