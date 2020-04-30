package com.example.cnclibrary.data.model;

public class BookInBag {
    private String img;
    private String name;
    private String start_date;

    public BookInBag(String img, String name, String start_date) {
        this.img = img;
        this.name = name;
        this.start_date = start_date;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }
}
