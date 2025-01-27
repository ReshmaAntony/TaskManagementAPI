package com.clicksolutions.taskmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clicksolutions.taskmanagement.entity.Role;
import com.clicksolutions.taskmanagement.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByEmail(String email);
	 
	User findByRole(Role role);

}
