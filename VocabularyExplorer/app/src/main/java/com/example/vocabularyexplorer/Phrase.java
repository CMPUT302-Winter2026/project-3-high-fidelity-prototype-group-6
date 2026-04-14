package com.example.vocabularyexplorer;
import java.util.ArrayList;

public class Phrase {
    private ArrayList<PhraseComponent> components;

    public Phrase(ArrayList<PhraseComponent> components) {
        this.components = components;
    }

    public ArrayList<PhraseComponent> getComponents() {
        return components;
    }

    public void setComponents(ArrayList<PhraseComponent> components) {
        this.components = components;
    }
}
