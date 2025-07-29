package com.RSSBAMB.API.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="centre_book")
public class CentreBook {
	
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "mmsId", nullable = false)
	private Books book;
	
	@ManyToOne
	@JoinColumn(name="centreCode",nullable=false)
	private Centres centre;
	
	@Column(nullable=false)
	private int allocatedQuantity;
	
	@Column
	private Date allotedDate;

	public Date getAllotedDate() {
		return allotedDate;
	}

	public void setAllotedDate(Date allotedDate) {
		this.allotedDate = allotedDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Books getBook() {
		return book;
	}

	public void setBook(Books book) {
		this.book = book;
	}

	public Centres getCentre() {
		return centre;
	}

	public void setCentre(Centres centre) {
		this.centre = centre;
	}

	public int getAllocatedQuantity() {
		return allocatedQuantity;
	}

	public void setAllocatedQuantity(int allocatedQuantity) {
		this.allocatedQuantity = allocatedQuantity;
	}
	
	
	

}
