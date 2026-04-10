package com.example.vocabularyexplorer;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CuratedCategoriesWordsScreen extends AppCompatActivity {
    private final ArrayList<Word> wordList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.curated_categories_words_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupRecyclerView(wordList);
        setupData(wordList);
    }

    private void setupRecyclerView(ArrayList<Word> wordList) {
        RecyclerView wordRecyclerView = findViewById(R.id.words_recyclerview);
        CuratedCategoriesWordsAdapter wordsAdapter = new CuratedCategoriesWordsAdapter(this, wordList);
        wordRecyclerView.setAdapter(wordsAdapter);
        wordRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
    }

    private void setupData(ArrayList<Word> wordList) {
        wordList.add(new Word("itasinâson", "1. colour"));
        wordList.add(new Word("mihko-", "1. red"));
        wordList.add(new Word("sîpihko-", "1. blue"));
        wordList.add(new Word("askihtako-", "1. blue\n2. blue, blue-green"));
        wordList.add(new Word("osâwi-", "1. yellow, orange, brown"));
        wordList.add(new Word("osâwâs", "1. orange"));
    }
}