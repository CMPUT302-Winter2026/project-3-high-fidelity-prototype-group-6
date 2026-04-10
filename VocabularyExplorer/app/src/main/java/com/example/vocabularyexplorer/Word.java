package com.example.vocabularyexplorer;

public class Word {
    private String title;
    private String definitions;

    public Word (String title, String definitions) {
        this.title = title;
        this.definitions = definitions;
    }

    public String getTitle() {
        return title;
    }

    public String getDefinitions() {
        return definitions;
    }
}
