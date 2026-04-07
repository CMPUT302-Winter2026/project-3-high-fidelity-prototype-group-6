package com.example.vocabularyexplorer;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.slider.Slider;

public class WordMapActivity extends AppCompatActivity {

    private boolean isMenuCollapsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_map);

        WordMapView wordMapView = findViewById(R.id.wordMapView);
        View topMenuContainer = findViewById(R.id.container);
        View searchBar = findViewById(R.id.search_bar);
        View threeDotMenu = findViewById(R.id.three_dot_menu);
        View wordCount = findViewById(R.id.word_count);
        ImageButton collapseBtn = findViewById(R.id.collapse_menu);
        ImageButton expandBtn = findViewById(R.id.expand_menu);
        Slider relatednessSlider = findViewById(R.id.relatedness_slider);

        // Connect Slider to Map Logic
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