package com.RSSBAMB.API.DTO;

import jakarta.persistence.Column;

public class BooksRegisterDTO {
	private String mms_Id;
	private String book_name;
	private int quantity;
	private int amount;
	public String getMms_Id() {
		return mms_Id;
	}
	public void setMms_Id(String mms_Id) {
		this.mms_Id = mms_Id;
	}
	public String getBook_name() {
		return book_name;
	}
	public void setBook_name(String book_name) {
		this.book_name = book_name;
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
	

}
