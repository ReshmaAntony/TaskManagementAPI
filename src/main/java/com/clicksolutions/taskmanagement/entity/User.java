package com.clicksolutions.taskmanagement.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(name ="users", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"email"})})
@Data
public class User implements UserDetails{

	private static final long serialVersionUID = 1L;

		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private long userId;

	    @Column(nullable = false)
	    private String username;

	    @Column(nullable = false)
	    private String password;
	    
	    @Column(name="email", nullable = false, unique = true)
		private String email;

	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false)
	    private Role role;
	
		@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<TaskEntity> tasks;

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return List.of(new SimpleGrantedAuthority(role.name()));
		}
		
		@Override
	    public boolean isAccountNonExpired() {
	        return true;
	    }

	    @Override
	    public boolean isAccountNonLocked() {
	        return true;
	    }

	    @Override
	    public boolean isCredentialsNonExpired() {
	        return true;
	    }

	    @Override
	    public boolean isEnabled() {
	        return true;
	    }

		@Override
		public String getPassword() {
			
			return null;
		}

		@Override
		public String getUsername() {
			
			return username;
		}

}
