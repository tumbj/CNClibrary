package com.example.cnclibrary.admin.data.model;

public class ListBorrowed {
    private String bookName;
    private String userid;
    private String start_date;
    private String imgEncoded;


    public ListBorrowed(String bookName, String userid, String start_date, String imgEncoded) {
        this.bookName = bookName;
        this.userid = userid;
        this.start_date = start_date;
        this.imgEncoded = imgEncoded;
    }
    public ListBorrowed(){
        this.bookName = null;

    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getImgEncoded() {
        return imgEncoded;
    }

    public void setImgEncoded(String imgEncoded) {
        this.imgEncoded = imgEncoded;
    }
}
