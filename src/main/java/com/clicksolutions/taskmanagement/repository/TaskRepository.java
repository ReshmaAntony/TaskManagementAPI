package com.clicksolutions.taskmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clicksolutions.taskmanagement.entity.Status;
import com.clicksolutions.taskmanagement.entity.TaskEntity;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

	List<TaskEntity> findByStatus(Status taskStatus);

	 

	
}
