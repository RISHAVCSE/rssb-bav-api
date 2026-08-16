package com.RSSBAMB.API.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="centre_list")
public class Centres {
	
	@Id
	@Column(nullable=false,unique=true)
	private int centreCode;
	@Column(nullable=false)
	private String centreName;
	@Column(nullable=true)
	private Integer sanctionedAmount;
	
	@Column(nullable=true)
	private Integer amountUtilized;
	
	@Column(nullable=true)
	private String email;
	
	@Column(nullable=true)
	private Integer phoneNumber;
	
	@OneToMany(mappedBy="centre", cascade=CascadeType.ALL)
	private List<CentreBook> centreBooks;


}
