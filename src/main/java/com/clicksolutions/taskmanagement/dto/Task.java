package com.clicksolutions.taskmanagement.dto;

import java.time.Instant;
import java.time.LocalDateTime;

import com.clicksolutions.taskmanagement.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class Task {
	

	private long taskId;
	private String taskName;
	private Status status;
	private String description;
	private String createdBy;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	
}
