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
import androidx.appcompat.app.AppCompatActivity;
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

        // this handles the novice/advanced modes used throughout the app
        SharedPreferences sharedPreferences = getSharedPreferences("mode", MODE_PRIVATE);
        if (sharedPreferences.getString("mode", null) == null) {
            sharedPreferences.edit().putString("mode", "novice").apply();
        }

        wordCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, WordMapScreen.class);
            startActivity(intent);
        });

        categories.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, CuratedCategoriesScreen.class);
            startActivity(intent);
        });

        mainSearchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchText = mainSearchInput.getText().toString();
                closeKeyboard();
                performSearch(searchText);
                return true;
            }
            return false;
        });

        searchButton.setOnClickListener(v -> showSearchBottomSheet());
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