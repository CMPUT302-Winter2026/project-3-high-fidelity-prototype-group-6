package com.example.vocabularyexplorer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton wordCard = findViewById(R.id.word_of_the_day_card);
        TextView categories = findViewById(R.id.categories);
        TextView matchingMinigame = findViewById(R.id.minigame);
        TextView semanticGaps = findViewById(R.id.semantic_gaps);
        EditText mainSearchInput = findViewById(R.id.search_input);

        ImageButton backButton = findViewById(R.id.back_button);
        ImageButton searchButton = findViewById(R.id.search_button);
        ImageButton menuButton = findViewById(R.id.menu_button);

        SharedPreferences sharedPreferences = getSharedPreferences("mode", MODE_PRIVATE);
        if (sharedPreferences.getString("mode", null) == null) {
            sharedPreferences.edit().putString("mode", "novice").apply();
        }

        Word wordOfDay = getWordOfDay();
        wordCard.setOnClickListener(v -> {
            Intent detailIntent = new Intent(HomeScreen.this, WordDetailScreen.class);
            detailIntent.putExtra("word_data", wordOfDay);
            startActivity(detailIntent);
        });

        categories.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, CuratedCategoriesScreen.class);
            startActivity(intent);
        });

        matchingMinigame.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, MatchingMinigameScreen.class);
            startActivity(intent);
        });

        semanticGaps.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, SemanticGapsScreen.class);
            startActivity(intent);
        });

        mainSearchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(mainSearchInput.getText().toString());
                return true;
            }
            return false;
        });

        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        searchButton.setOnClickListener(v -> showSearchBottomSheet());
        menuButton.setOnClickListener(v -> showNavigationMenu());
    }

    private Word getWordOfDay() {
        Context context = getBaseContext();
        Word wordOfDay = new Word("pîsim", "1. Sun\n2. Moon\n3. Month");
        wordOfDay.setSyllabics("ᐲᓯᒼ");
        wordOfDay.setAdvancedLabel("NI-3");
        wordOfDay.setIPATranscription("/piːˈsɪm/");
        wordOfDay.setCreePhrase1(context.getString(R.string.pisim_cree_phrase1));
        wordOfDay.setEnglishPhrase1(context.getString(R.string.pisim_english_phrase1));
        wordOfDay.setCreePhrase2(context.getString(R.string.pisim_cree_phrase2));
        wordOfDay.setEnglishPhrase2(context.getString(R.string.pisim_english_phrase2));
        wordOfDay.setMorphology(context.getString(R.string.pisim_morphology));
        return wordOfDay;
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
            Intent intent = new Intent(HomeScreen.this, CuratedCategoriesScreen.class);
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
        minigame.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, MatchingMinigameScreen.class);
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
        semanticGaps.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, SemanticGapsScreen.class);
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
        Intent intent = new Intent(HomeScreen.this, WordMapScreen.class);
        intent.putExtra("search", searchString);
        startActivity(intent);
    }

    public void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}