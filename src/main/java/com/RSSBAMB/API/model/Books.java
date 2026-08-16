package com.RSSBAMB.API.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import serviceInterface.BookTemplateForHistory;


@Data
@ToString(exclude = "centreBooks")
@EqualsAndHashCode(exclude = "centreBooks")
@Entity
@Table(name="books")
public class Books implements BookTemplateForHistory {
		
	
	@Id
	@Column(nullable=false,unique=true)
	private String mmsId;
	@Column(nullable=false)
	private String bookName;
	@Column(nullable=false)
	private int quantity;
	@Column(nullable=false)
	private int amount;
	@Column(nullable = true)
	private Integer type;
	
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
	@OneToMany(mappedBy="book", cascade=CascadeType.ALL,orphanRemoval=true)
    @JsonManagedReference
    private List<CentreBook> centreBooks;
	
	
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
	public BooksHistory createHistoryRecord(String changeType, String changedBy) {
		return new BooksHistory(this,changeType,changedBy);
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
