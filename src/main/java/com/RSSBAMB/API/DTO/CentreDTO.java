package com.RSSBAMB.API.DTO;

import jakarta.persistence.Column;

public class CentreDTO {
	private int centreCode;
	private String centreName;
	private Integer sanctionedAmount;
	private Integer amountUtilized;
	private String email;
	private Integer phoneNumber;


	
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

	public Integer getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(Integer phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	
	



}
