package com.example.vocabularyexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

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
        View wordCount = findViewById(R.id.word_count);
        ImageButton collapseBtn = findViewById(R.id.collapse_menu);
        ImageButton expandBtn = findViewById(R.id.expand_menu);
        Slider relatednessSlider = findViewById(R.id.relatedness_slider);
        EditText searchInput = findViewById(R.id.search_input);

        Intent intent = getIntent();
        String searchString = intent.getStringExtra("search");
        
        if (searchString != null) {
            wordMapView.setCenterWord(searchString);
        }

        // Handle clicks on map nodes
        wordMapView.setOnNodeClickListener(word -> {
            Intent detailIntent = new Intent(WordMapScreen.this, WordDetailScreen.class);
            detailIntent.putExtra("word_data", word);
            startActivity(detailIntent);
        });

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
            wordCount.setVisibility(View.GONE);
            collapseBtn.setVisibility(View.GONE);
            expandBtn.setVisibility(View.VISIBLE);
            isMenuCollapsed = true;
        });

        expandBtn.setOnClickListener(v -> {
            topMenuContainer.setVisibility(View.VISIBLE);
            searchBar.setVisibility(View.VISIBLE);
            threeDotMenu.setVisibility(View.VISIBLE);
            wordCount.setVisibility(View.VISIBLE);
            collapseBtn.setVisibility(View.VISIBLE);
            expandBtn.setVisibility(View.GONE);
            isMenuCollapsed = false;
        });

        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }
}