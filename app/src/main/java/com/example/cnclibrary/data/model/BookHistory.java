package com.example.cnclibrary.data.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookHistory {
    private String user_id;
    private String start_date;
    private String end_date;

    public BookHistory(String user_id) {
        this.user_id = user_id;
        this.start_date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        this.end_date = null;
    }

    public BookHistory(){
        this.user_id = null;
        this.start_date = null;
        this.end_date = null;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
}
