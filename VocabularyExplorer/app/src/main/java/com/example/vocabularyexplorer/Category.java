package com.example.vocabularyexplorer;

public class Category {
    private final String title;
    private final int image;

    public Category(String title, int image) {
        this.title = title;
        this.image = image;
    }

    public String getTitle() { return title; }
    public int getImage() { return image; }
}
