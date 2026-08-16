package com.RSSBAMB.API.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Data
@Entity
@Table(name="users" , uniqueConstraints = @UniqueConstraint(columnNames = "keycloak_id"))
public class User {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="keycloak_id", nullable=false, unique=true)
	private String keycloakId;

	@Column(nullable=false, unique=true)
	private String username;

	private String email;
	private String firstName;
	private String lastName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private Role role;
	private String createdBy;

	@ManyToOne
	@JoinColumn(name="parent_user_id")
	private User parentUser;

	@OneToMany(mappedBy = "parentUser")
	private List<User> subUsers;


}
