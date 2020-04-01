package com.example.cnclibrary.data.model;

public class Book {
    private String name;
    private String barcode;
    private String detail;
    private String category;
    private int count;
    private String img;

    public Book(String name, String barcode, String detail, String category, int count,String img) {
        this.name = name;
        this.barcode = barcode;
        this.detail = detail;
        this.category = category;
        this.count = count;
        this.img = img;
    }
    public Book(){
        this.name = "";
        this.barcode = "";
        this.detail = "";
        this.category = "";
        this.count = 0;
        this.img = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
