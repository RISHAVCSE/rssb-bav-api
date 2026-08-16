package com.RSSBAMB.API.DTO;

public class CentreBookTable {
    private String mmsId;
    private String bookName;
    private int quantity;
    private int  amount;

    public CentreBookTable(int quantity, String mmsId, String bookName, int amount) {
        this.quantity = quantity;
        this.mmsId = mmsId;
        this.bookName = bookName;
        this.amount = amount;
    }
    public CentreBookTable() {}


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

}
