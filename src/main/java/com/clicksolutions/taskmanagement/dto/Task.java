package com.clicksolutions.taskmanagement.dto;

import java.time.LocalDateTime;

import com.clicksolutions.taskmanagement.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuppressWarnings("unused")
public class Task {
	

	private long taskId;
	private String taskName;
	private Status status;
	private String description;
	private long createdBy;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	
}
