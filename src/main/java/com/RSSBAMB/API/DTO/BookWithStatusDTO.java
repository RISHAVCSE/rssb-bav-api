package com.RSSBAMB.API.DTO;

public class BookWithStatusDTO {
	 private String mmsId;
	    private String bookName;
	    private int quantity;
	    private int amount;
	    private int pendingForApprovalQuantity;
	    private int allotedQuantity;
	    
	    
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


		public int getAllotedQuantity() {
			return allotedQuantity;
		}


		public void setAllotedQuantity(int allotedQuantity) {
			this.allotedQuantity = allotedQuantity;
		}


		public int getPendingForApprovalQuantity() {
			return pendingForApprovalQuantity;
		}


		public void setPendingForApprovalQuantity(int pendingForApprovalQuantity) {
			this.pendingForApprovalQuantity = pendingForApprovalQuantity;
		}


		public BookWithStatusDTO(String mmsId, String bookName, int quantity, int amount, 
                int allotedQuantity, int pendingForApprovalQuantity) {
this.mmsId = mmsId;
this.bookName = bookName;
this.quantity = quantity;
this.amount = amount;
this.allotedQuantity = allotedQuantity;
this.pendingForApprovalQuantity = pendingForApprovalQuantity;
}

}
