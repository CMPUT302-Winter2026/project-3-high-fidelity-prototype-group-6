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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.Slider;

public class WordMapScreen extends AppCompatActivity {

    private boolean isMenuCollapsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_map);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);

        WordMapView wordMapView = findViewById(R.id.wordMapView);
        View topMenuContainer = findViewById(R.id.container);
        View searchBar = findViewById(R.id.search_bar);
        View threeDotMenu = findViewById(R.id.three_dot_menu);
        TextView wordCountText = findViewById(R.id.word_count);
        ImageButton collapseBtn = findViewById(R.id.collapse_menu);
        ImageButton expandBtn = findViewById(R.id.expand_menu);
        Slider relatednessSlider = findViewById(R.id.relatedness_slider);
        EditText searchInput = findViewById(R.id.search_input);

        ImageButton btnReturnToCenter = findViewById(R.id.return_to_center);
        ImageButton btnZoomIn = findViewById(R.id.zoom_in);
        ImageButton btnZoomOut = findViewById(R.id.zoom_out);

        Intent intent = getIntent();
        String searchString = intent.getStringExtra("search");
        
        if (searchString != null) {
            wordMapView.setCenterWord(searchString);
        }

        // Map Listeners
        wordMapView.setOnNodeClickListener(word -> {
            Intent detailIntent = new Intent(WordMapScreen.this, WordDetailScreen.class);
            detailIntent.putExtra("word_data", word);
            startActivity(detailIntent);
        });

        wordMapView.setOnMapChangeListener(visibleNodeCount -> {
            wordCountText.setText("Word Count: " + visibleNodeCount);
        });
        
        // Initial count update
        wordMapView.notifyMapChanged();

        // Control Listeners
        btnReturnToCenter.setOnClickListener(v -> wordMapView.returnToCenter());
        btnZoomIn.setOnClickListener(v -> wordMapView.zoomIn());
        btnZoomOut.setOnClickListener(v -> wordMapView.zoomOut());

        threeDotMenu.setOnClickListener(v -> showPopoutMenu());

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                wordMapView.setCenterWord(searchInput.getText().toString());
                return true;
            }
            return false;
        });

        relatednessSlider.addOnChangeListener((slider, value, fromUser) -> {
            wordMapView.setRelatednessThreshold(value);
        });

        collapseBtn.setOnClickListener(v -> {
            topMenuContainer.setVisibility(View.GONE);
            searchBar.setVisibility(View.GONE);
            threeDotMenu.setVisibility(View.GONE);
            wordCountText.setVisibility(View.GONE);
            collapseBtn.setVisibility(View.GONE);
            expandBtn.setVisibility(View.VISIBLE);
            isMenuCollapsed = true;
        });

        expandBtn.setOnClickListener(v -> {
            topMenuContainer.setVisibility(View.VISIBLE);
            searchBar.setVisibility(View.VISIBLE);
            threeDotMenu.setVisibility(View.VISIBLE);
            wordCountText.setVisibility(View.VISIBLE);
            collapseBtn.setVisibility(View.VISIBLE);
            expandBtn.setVisibility(View.GONE);
            isMenuCollapsed = false;
        });

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
        if (! (this instanceof WordMapScreen)) finish(); // Prevent stack build up if already in map
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showPopoutMenu() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.word_map_popout_menu, null);
        bottomSheetDialog.setContentView(sheetView);

        ConstraintLayout closeButton = sheetView.findViewById(R.id.close);
        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }
}
