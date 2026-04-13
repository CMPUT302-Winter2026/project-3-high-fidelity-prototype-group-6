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
        EditText mainSearchInput = findViewById(R.id.search_input);
        ImageButton searchButton = findViewById(R.id.search_button);
        ImageButton menuButton = findViewById(R.id.menu_button);

        TextView matchingMinigame = findViewById(R.id.minigame);

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

        mainSearchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(mainSearchInput.getText().toString());
                return true;
            }
            return false;
        });

        searchButton.setOnClickListener(v -> showSearchBottomSheet());
        menuButton.setOnClickListener(v -> showNavigationMenu());
    }

    private Word getWordOfDay() {
        Context context = getBaseContext();
        Word wordOfDay = new Word("pawâcakinâsis-pîsim", "1. Frost-Exploding Trees Moon\n2. Blizzard Moon\n3. December");
        wordOfDay.setSyllabics("ᒪᐢᑭᐦᑭᐩ");
        wordOfDay.setAdvancedLabel("NI-2");
        wordOfDay.setIPATranscription("/ˈmʌs.kɪh.kiː/");
        wordOfDay.setCreePhrase1(context.getString(R.string.maskihkiy_cree_phrase1));
        wordOfDay.setEnglishPhrase1(context.getString(R.string.maskihkiy_english_phrase1));
        wordOfDay.setCreePhrase2(context.getString(R.string.maskihkiy_cree_phrase2));
        wordOfDay.setEnglishPhrase2(context.getString(R.string.maskihkiy_english_phrase2));
        wordOfDay.setMorphologyImage(R.drawable.maskihkiy_word_parts);
        wordOfDay.setMorphology(context.getString(R.string.maskihkiy_morphology));
        return wordOfDay;
    }

    private void showNavigationMenu() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.navigation_popout_menu, null);
        bottomSheetDialog.setContentView(sheetView);

        SwitchCompat modeSwitch = sheetView.findViewById(R.id.mode_switch);
        TextView modeText = sheetView.findViewById(R.id.mode_text);
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