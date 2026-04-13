package com.example.vocabularyexplorer;

import java.io.Serializable;

public class Word implements Serializable {
    private final String title;
    private final String definitions;
    private String creePhrase1;
    private String englishPhrase1;
    private String creePhrase2;
    private String englishPhrase2;

    public Word (String title, String definitions) {
        this.title = title;
        this.definitions = definitions;
        this.creePhrase1 = null;
        this.englishPhrase1 = null;
        this.creePhrase2 = null;
        this.englishPhrase2 = null;
    }

    public String getTitle() {
        return title;
    }

    public String getDefinitions() {
        return definitions;
    }

    public String getCreePhrase1() {
        return creePhrase1;
    }

    public String getEnglishPhrase1() {
        return englishPhrase1;
    }

    public String getCreePhrase2() {
        return creePhrase2;
    }

    public String getEnglishPhrase2() {
        return englishPhrase2;
    }

    public void setCreePhrase1(String creePhrase1) {
        this.creePhrase1 = creePhrase1;
    }

    public void setEnglishPhrase1(String englishPhrase1) {
        this.englishPhrase1 = englishPhrase1;
    }

    public void setCreePhrase2(String creePhrase2) {
        this.creePhrase2 = creePhrase2;
    }

    public void setEnglishPhrase2(String englishPhrase2) {
        this.englishPhrase2 = englishPhrase2;
    }
}