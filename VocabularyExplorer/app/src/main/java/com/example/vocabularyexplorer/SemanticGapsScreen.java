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
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class SemanticGapsScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.semantic_gaps_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.semantic_gaps), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
            startActivity(new Intent(this, CuratedCategoriesScreen.class));
            bottomSheetDialog.dismiss();
        });
        minigame.setOnClickListener(v -> {
            startActivity(new Intent(this, MatchingMinigameScreen.class));
            bottomSheetDialog.dismiss();
        });
        semanticGaps.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            // Already on semantic gaps screen
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
}
