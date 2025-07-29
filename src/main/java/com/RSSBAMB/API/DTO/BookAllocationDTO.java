package com.RSSBAMB.API.DTO;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class BookAllocationDTO {
	private int centreCode;
	private LocalDateTime currentTime;
	private List<BookAllocationDetail> books;
	
	@Data
	public static class BookAllocationDetail{
		private String mmsId;
		private String bookName;
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
		private int quantity;
		private int amount;
		private LocalDateTime allocationTime;
	}
	
	public List<BookAllocationDetail> getBooks(){
		return books;
	}
	public void setBooks(List<BookAllocationDetail> books) {
		this.books=books;
	}
	public int getCentreCode() {
		return centreCode;
	}
	public void setCentreCode(int centreCode) {
		this.centreCode = centreCode;
	}
	public LocalDateTime getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(LocalDateTime currentTime) {
		this.currentTime = currentTime;
	}  

}
