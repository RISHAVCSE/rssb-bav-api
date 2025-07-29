package com.RSSBAMB.API.DTO;


public class BooksRegisterDTO {
	private String mmsId;
	private String bookName;
	private int amount;
	private int quantity;
	private int stockavailable;
	
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
	public int getStockavailable() {
		return stockavailable;
	}
	public void setStockavailable(int stockavailable) {
		this.stockavailable = stockavailable;
	}
	

}
