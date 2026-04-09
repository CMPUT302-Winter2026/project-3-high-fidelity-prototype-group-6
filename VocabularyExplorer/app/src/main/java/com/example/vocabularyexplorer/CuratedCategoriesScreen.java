package com.example.vocabularyexplorer;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CuratedCategoriesScreen extends AppCompatActivity {
    private final ArrayList<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.curated_categories_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupData(categoryList);
        setupRecyclerView(categoryList);
    }

    private void setupRecyclerView(ArrayList<Category> categoryList) {
        RecyclerView categoryRecyclerView = findViewById(R.id.categories_recyclerview);
        CuratedCategoriesAdapter categoriesAdapter = new CuratedCategoriesAdapter(this, categoryList);
        categoryRecyclerView.setAdapter(categoriesAdapter);
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void setupData(ArrayList<Category> categoryList) {
        categoryList.add(new Category("Food", R.drawable.food));
        categoryList.add(new Category("Weather", R.drawable.weather));
        categoryList.add(new Category("Colour", R.drawable.colour));
        categoryList.add(new Category("Nature", R.drawable.nature));
        categoryList.add(new Category("Body", R.drawable.body));
        categoryList.add(new Category("Family", R.drawable.family));
        categoryList.add(new Category("Hobbies", R.drawable.hobbies));
        categoryList.add(new Category("Movement", R.drawable.movement));
        categoryList.add(new Category("Sports", R.drawable.sports));
        categoryList.add(new Category("Animals", R.drawable.animals));
    }
}