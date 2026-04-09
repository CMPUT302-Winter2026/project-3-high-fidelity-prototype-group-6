package com.example.vocabularyexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        wordCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, WordMapScreen.class);
            startActivity(intent);
        });

        TextView categories = findViewById(R.id.categories);
        categories.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, CuratedCategoriesScreen.class);
            startActivity(intent);
        });
    }
}