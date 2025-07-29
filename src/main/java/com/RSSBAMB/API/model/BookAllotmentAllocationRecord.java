package com.RSSBAMB.API.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="allotmentrecord")
public class BookAllotmentAllocationRecord {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(nullable=false)
	private String mmsId;
	
	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	@Column(nullable=false)
	private int centreCode;
	
	@Column(nullable=false)
	private String bookName;
	
	@Column(nullable=false)
	private String type;
	
	@Column(nullable=false)
	private int amount;
	
	@Column(nullable=false)
	private int allotedQuantity;
	
	@Column(nullable=false)
	private String allocationType;
	
	@Column(nullable=false)
	private String Remarks;
	
	@Column(nullable=false)
	private int previousQuantity;
	
	@Column(nullable=false)
	private int previousBalance;
	
	@Column(nullable=false)
	private int currentBalance;
	
	@Column(nullable=false)
	private int totalQuantity;
	
	@Column(nullable=false)
	private Date allotedDate;
	
	@Column(nullable=false)
	private String allotedBy;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMmsId() {
		return mmsId;
	}

	public void setMmsId(String mmsId) {
		this.mmsId = mmsId;
	}

	public int getCentreCode() {
		return centreCode;
	}

	public void setCentreCode(int centreCode) {
		this.centreCode = centreCode;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public int getAmount() {
		return amount;
	}
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAllocationType() {
		return allocationType;
	}

	public void setAllocationType(String allocationType) {
		this.allocationType = allocationType;
	}

	public String getRemarks() {
		return Remarks;
	}

	public void setRemarks(String remarks) {
		Remarks = remarks;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getAllotedQuantity() {
		return allotedQuantity;
	}

	public void setAllotedQuantity(int allotedQuantity) {
		this.allotedQuantity = allotedQuantity;
	}

	public int getPreviousQuantity() {
		return previousQuantity;
	}

	public void setPreviousQuantity(int previousQuantity) {
		this.previousQuantity = previousQuantity;
	}

	public Date getAllotedDate() {
		return allotedDate;
	}

	public void setAllotedDate(Date allotedDate) {
		this.allotedDate = allotedDate;
	}

	public String getAllotedBy() {
		return allotedBy;
	}

	public void setAllotedBy(String allotedBy) {
		this.allotedBy = allotedBy;
	}

	public int getPreviousBalance() {
		return previousBalance;
	}

	public void setPreviousBalance(int previousBalance) {
		this.previousBalance = previousBalance;
	}

	public int getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(int currentBalance) {
		this.currentBalance = currentBalance;
	}
	
	
	
	
	

}
