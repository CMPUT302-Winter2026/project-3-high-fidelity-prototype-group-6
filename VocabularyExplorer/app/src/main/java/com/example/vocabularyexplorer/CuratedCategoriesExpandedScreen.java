package com.example.vocabularyexplorer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class CuratedCategoriesExpandedScreen extends AppCompatActivity {
    private final ArrayList<Word> wordList = new ArrayList<>();
    private final ArrayList<Phrase> phraseList = new ArrayList<>();
    private RecyclerView wordRecyclerView;
    private RecyclerView phraseRecyclerView;
    private TextView categoryTooltip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.curated_categories_expanded_screen);
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
        setupData(wordList, phraseList, title);
        setupRecyclerView(wordList, phraseList);
        setupBottomBar();
    }

    private void setupBottomBar() {
        ImageButton backButton = findViewById(R.id.back_button);
        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton searchButton = findViewById(R.id.search_button);
        ImageButton menuButton = findViewById(R.id.menu_button);

        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        searchButton.setOnClickListener(v -> showSearchBottomSheet());
        menuButton.setOnClickListener(v -> showNavigationMenu());
    }

    private void showNavigationMenu() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.navigation_popout_menu, null);
        bottomSheetDialog.setContentView(sheetView);

        SwitchCompat modeSwitch = sheetView.findViewById(R.id.mode_switch);
        TextView modeText = sheetView.findViewById(R.id.mode_text);
        ConstraintLayout categories = sheetView.findViewById(R.id.categories);
        ConstraintLayout minigame = sheetView.findViewById(R.id.minigame);
        ConstraintLayout semanticGaps = sheetView.findViewById(R.id.semantic_gaps);
        ConstraintLayout closeButton = sheetView.findViewById(R.id.close);

        SharedPreferences sharedPreferences = getSharedPreferences("mode", MODE_PRIVATE);
        String currentMode = sharedPreferences.getString("mode", "novice");

        modeSwitch.setChecked(currentMode.equals("advanced"));
        modeText.setText(currentMode.equals("advanced") ? "Advanced Mode" : "Novice Mode");

        modeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String newMode = isChecked ? "advanced" : "novice";
            sharedPreferences.edit().putString("mode", newMode).apply();
            modeText.setText(isChecked ? "Advanced Mode" : "Novice Mode");
        });

        categories.setOnClickListener(v -> {
            Intent intent = new Intent(this, CuratedCategoriesScreen.class);
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
        minigame.setOnClickListener(v -> {
            Intent intent = new Intent(this, MatchingMinigameScreen.class);
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
        semanticGaps.setOnClickListener(v -> {
            Intent intent = new Intent(this, SemanticGapsScreen.class);
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private void showSearchBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.search_bottom_sheet, null);
        bottomSheetDialog.setContentView(sheetView);

        EditText searchInput = sheetView.findViewById(R.id.search_input);
        searchInput.requestFocus();

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchInput.getText().toString());
                bottomSheetDialog.dismiss();
                return true;
            }
            return false;
        });

        bottomSheetDialog.show();
    }

    private void performSearch(String searchString) {
        if (searchString == null || searchString.trim().isEmpty()) return;
        closeKeyboard();
        Intent intent = new Intent(this, WordMapScreen.class);
        intent.putExtra("search", searchString);
        startActivity(intent);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setupTabLayout() {
        TabLayout tabLayout = findViewById(R.id.top_tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    wordRecyclerView.setVisibility(VISIBLE);
                    phraseRecyclerView.setVisibility(GONE);
                    if (categoryTooltip != null) categoryTooltip.setVisibility(VISIBLE);
                } else if (tab.getPosition() == 1) {
                    phraseRecyclerView.setVisibility(VISIBLE);
                    wordRecyclerView.setVisibility(GONE);
                    if (categoryTooltip != null) categoryTooltip.setVisibility(GONE);
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

            ArrayList<PhraseComponent> components1 = new ArrayList<>();
            components1.add(new PhraseComponent("kaskitêw", Color.parseColor("#e91fe9"), true));
            components1.add(new PhraseComponent("â", Color.parseColor("#29ed28"), true));
            components1.add(new PhraseComponent("It is", Color.parseColor("#29ed28"), false));
            components1.add(new PhraseComponent("black", Color.parseColor("#e91fe9"), false));
            phraseList.add(new Phrase(components1));

            ArrayList<PhraseComponent> components2 = new ArrayList<>();
            components2.add(new PhraseComponent("mihko", Color.parseColor("#FFCDD2"), true));
            components2.add(new PhraseComponent("siw", Color.parseColor("#C8E6C9"), true));
            components2.add(new PhraseComponent("it is", Color.parseColor("#C8E6C9"), false));
            components2.add(new PhraseComponent("red", Color.parseColor("#FFCDD2"), false));
            phraseList.add(new Phrase(components2));
        }
    }
}
