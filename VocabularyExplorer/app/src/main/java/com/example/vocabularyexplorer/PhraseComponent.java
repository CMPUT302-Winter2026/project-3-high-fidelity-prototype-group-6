package com.example.vocabularyexplorer;

public class PhraseComponent {
    private String text;
    private int color;
    private boolean isCree;

    public PhraseComponent(String text, int color, boolean isCree) {
        this.text = text;
        this.color = color;
        this.isCree = isCree;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isCree() {
        return isCree;
    }

    public void setCree(boolean cree) {
        isCree = cree;
    }
}
