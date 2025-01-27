package com.clicksolutions.taskmanagement.entity;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Tasks")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TaskEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String taskName;
	
	@Column(nullable = false)
	private Status status;
	
	private String description;
	
	private long createdBy;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	@Column(nullable = false)
	private LocalDateTime updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id")
	private User user;


	
	
	@PrePersist
    protected void createdTime() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void updatedTime() {
        this.updatedAt = LocalDateTime.now();
    }
	
	
}
