package com.example.vocabularyexplorer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class CuratedCategoriesWordsScreen extends AppCompatActivity {
    private final ArrayList<Word> wordList = new ArrayList<>();
    private final ArrayList<Phrase> phraseList = new ArrayList<>();
    private RecyclerView wordRecyclerView;
    private RecyclerView phraseRecyclerView;
    private TextView categoryTooltip;

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

        TextView categoryTitle = findViewById(R.id.title);
        categoryTooltip = findViewById(R.id.categories_tooltip);

        Intent intent = getIntent();
        String title = intent.getStringExtra("category");
        categoryTitle.setText("Category: " + title);

        wordRecyclerView = findViewById(R.id.words_recyclerview);
        phraseRecyclerView = findViewById(R.id.phrases_recyclerview);

        setupTabLayout();
        // IMPORTANT: Setup data BEFORE setting up the recyclerview
        setupData(wordList, phraseList, title);
        setupRecyclerView(wordList, phraseList);
    }

    private void setupTabLayout() {
        TabLayout tabLayout = findViewById(R.id.top_tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    wordRecyclerView.setVisibility(VISIBLE);
                    phraseRecyclerView.setVisibility(GONE);
                    categoryTooltip.setVisibility(VISIBLE);
                } else if (tab.getPosition() == 1) {
                    phraseRecyclerView.setVisibility(VISIBLE);
                    wordRecyclerView.setVisibility(GONE);
                    categoryTooltip.setVisibility(GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupRecyclerView(ArrayList<Word> wordList, ArrayList<Phrase> phraseList) {
        CuratedCategoriesWordsAdapter wordsAdapter = new CuratedCategoriesWordsAdapter(this, wordList);
        wordRecyclerView.setAdapter(wordsAdapter);
        wordRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        CuratedCategoriesPhrasesAdapter phrasesAdapter = new CuratedCategoriesPhrasesAdapter(this, phraseList);
        phraseRecyclerView.setAdapter(phrasesAdapter);
        phraseRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
    }

    private void setupData(ArrayList<Word> wordList, ArrayList<Phrase> phraseList, String category) {
        wordList.clear();
        phraseList.clear();

        if (category != null && category.equalsIgnoreCase("colour")) {
            wordList.add(new Word("itasinâson", "1. colour"));
            wordList.add(new Word("mihko-", "1. red"));
            wordList.add(new Word("sîpihko-", "1. blue"));
            wordList.add(new Word("askihtako-", "1. blue\n2. blue, blue-green"));
            wordList.add(new Word("osâwi-", "1. yellow, orange, brown"));
            wordList.add(new Word("osâwâs", "1. orange"));

            // Dummy Phrase 1: "maskihkîwiyiniw"
            ArrayList<PhraseComponent> components1 = new ArrayList<>();
            components1.add(new PhraseComponent("maskihkî", Color.parseColor("#B9D992")));
            components1.add(new PhraseComponent("w", Color.parseColor("#A686E6")));
            components1.add(new PhraseComponent("iyiniw", Color.parseColor("#E9B972")));
            components1.add(new PhraseComponent("someone who", Color.parseColor("#E9B972")));
            components1.add(new PhraseComponent("treats", Color.parseColor("#A686E6")));
            components1.add(new PhraseComponent("illness", Color.parseColor("#B9D992")));
            phraseList.add(new Phrase(components1));

            // Dummy Phrase 2: "mihkosiw"
            ArrayList<PhraseComponent> components2 = new ArrayList<>();
            components2.add(new PhraseComponent("mihko", Color.parseColor("#FFCDD2")));
            components2.add(new PhraseComponent("siw", Color.parseColor("#C8E6C9")));
            components2.add(new PhraseComponent("", Color.TRANSPARENT));
            components2.add(new PhraseComponent("it is", Color.parseColor("#C8E6C9")));
            components2.add(new PhraseComponent("red", Color.parseColor("#FFCDD2")));
            components2.add(new PhraseComponent("", Color.TRANSPARENT));
            phraseList.add(new Phrase(components2));
        }
    }
}
