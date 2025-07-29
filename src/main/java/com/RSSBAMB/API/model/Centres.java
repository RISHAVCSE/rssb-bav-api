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

import lombok.Data;

@Data
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
	
	public int getCentreCode() {
		return centreCode;
	}
	public void setCentreCode(int centreCode) {
		this.centreCode = centreCode;
	}
	public String getCentreName() {
		return centreName;
	}
	public void setCentreName(String centreName) {
		this.centreName = centreName;
	}
	public Integer getSanctionedAmount() {
		return sanctionedAmount;
	}
	public void setSanctionedAmount(Integer sanctionedAmount) {
		this.sanctionedAmount = sanctionedAmount;
	}
	public Integer getAmountUtilized() {
		return amountUtilized;
	}
	public void setAmountUtilized(Integer amountUtilized) {
		this.amountUtilized = amountUtilized;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(Integer phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	

	
	
																		
}
