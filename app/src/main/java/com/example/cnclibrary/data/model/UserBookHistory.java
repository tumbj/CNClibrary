package com.example.cnclibrary.data.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserBookHistory {
    private String barcode;
    private String start_date;
    private String end_date;

    public UserBookHistory(String barcode) {
        this.barcode = barcode;
        this.start_date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        this.end_date = null;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
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
