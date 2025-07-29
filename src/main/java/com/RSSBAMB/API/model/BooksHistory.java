package com.RSSBAMB.API.model;

import java.util.Date;

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
@Table(name="books_history")
public class BooksHistory {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false)
	private String mmsId;
	
	@Column(nullable=false)
	private String bookName;
	
	@Column(nullable=false)
	private int quantity;
	
	@Column(nullable=false)
	private int amount;
	
	@Column(nullable=false)
	private Date changeDate;
	
	@Column(nullable=false)
	private String changeType;   //Update or Add 
	
	 @Column(nullable = false)
	 private String changedBy; // You can store the user who made the change
	
	public BooksHistory(BookTemplateForHistory book, String changeType,String changedBy) {
		this.mmsId=book.getMmsId();
		this.amount=book.getAmount();
		this.quantity=book.getQuantity();
		this.bookName=book.getBookName();
		this.changeDate=new Date();
		this.changeType=changeType;
		this.changedBy=changedBy;
	}
	
	

}
