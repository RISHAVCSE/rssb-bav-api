package com.RSSBAMB.API.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import serviceInterface.BookTemplateForHistory;

@Data
@Entity
@Table(name="book_allocation")
public class BookAllocation implements BookTemplateForHistory {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate the ID
    private Long id;
	
	@Column(nullable=false)
	private int CentreCode;
	
	@Column(nullable=false)
	private String mmsId;
	
	@Column(nullable=false)
	private String bookName;
	
	@Column(nullable=false)
	private int quantity;
	
	@Column(nullable=false)
	private int amount;
	
	@Column(nullable=false)
	private LocalDateTime allocationTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getCentreCode() {
		return CentreCode;
	}

	public void setCentreCode(int centreCode) {
		CentreCode = centreCode;
	}

	public String getMmsId() {
		return mmsId;
	}

	public void setMmsId(String mmsId) {
		this.mmsId = mmsId;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public LocalDateTime getAllocationTime() {
		return allocationTime;
	}

	public void setAllocationTime(LocalDateTime allocationTime) {
		this.allocationTime = allocationTime;
	}
	
	public BooksHistory createHistoryRecord(String changeType, String changedBy) {
		return new BooksHistory(this,changeType,changedBy);
	}
	
	
	

}
