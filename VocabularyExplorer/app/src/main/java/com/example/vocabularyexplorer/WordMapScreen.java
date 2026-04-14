package com.example.vocabularyexplorer;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.Slider;

public class WordMapScreen extends AppCompatActivity {
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
        ImageButton helpButton = findViewById(R.id.help_button);
        Slider relatednessSlider = findViewById(R.id.relatedness_slider);
        EditText searchInput = findViewById(R.id.search_input);

        ImageButton btnReturnToCenter = findViewById(R.id.return_to_center);
        ImageButton btnZoomIn = findViewById(R.id.zoom_in);
        ImageButton btnZoomOut = findViewById(R.id.zoom_out);

        Intent intent = getIntent();
        String searchString = intent.getStringExtra("search");
        
        if (searchString != null) {
            wordMapView.setCenterWord(searchString);
            searchInput.setText(searchString);
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

        helpButton.setOnClickListener(v -> showHint());

        // Control Listeners
        btnReturnToCenter.setOnClickListener(v -> wordMapView.returnToCenter());
        btnZoomIn.setOnClickListener(v -> wordMapView.zoomIn());
        btnZoomOut.setOnClickListener(v -> wordMapView.zoomOut());

        threeDotMenu.setOnClickListener(v -> showPopoutMenu());

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchInput.getText().toString();
                if (!query.isEmpty()) {
                    wordMapView.setCenterWord(query);
                    closeKeyboard();
                }
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
        });

        expandBtn.setOnClickListener(v -> {
            topMenuContainer.setVisibility(View.VISIBLE);
            searchBar.setVisibility(View.VISIBLE);
            threeDotMenu.setVisibility(View.VISIBLE);
            wordCountText.setVisibility(View.VISIBLE);
            collapseBtn.setVisibility(View.VISIBLE);
            expandBtn.setVisibility(View.GONE);
        });

        setupBottomBar();
    }

    private void showHint() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.RoundedDialogTheme);
        com.google.android.material.card.MaterialCardView card = new com.google.android.material.card.MaterialCardView(this);
        card.setRadius(dpToPx(16));
        card.setCardElevation(dpToPx(8));
        card.setCardBackgroundColor(Color.WHITE);

        android.widget.LinearLayout outer = new android.widget.LinearLayout(this);
        outer.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = dpToPx(20);
        outer.setPadding(pad, pad, pad, pad);

        android.widget.LinearLayout titleRow = new android.widget.LinearLayout(this);
        titleRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        titleRow.setGravity(android.view.Gravity.CENTER_VERTICAL);

        android.widget.ImageView infoIcon = new android.widget.ImageView(this);
        infoIcon.setImageResource(android.R.drawable.ic_dialog_info);
        infoIcon.setColorFilter(Color.BLACK);
        int iconSize = dpToPx(24);
        android.widget.LinearLayout.LayoutParams iconParams = new android.widget.LinearLayout.LayoutParams(iconSize, iconSize);
        iconParams.setMarginEnd(dpToPx(10));

        android.widget.TextView title = new android.widget.TextView(this);
        title.setText("Word Map Help");
        title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 18);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setTextColor(Color.BLACK);
        android.widget.LinearLayout.LayoutParams titleParams = new android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        android.widget.ImageButton closeBtn = new android.widget.ImageButton(this);
        closeBtn.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        closeBtn.setBackground(null);
        closeBtn.setColorFilter(Color.BLACK);
        int closeBtnSize = dpToPx(24);
        android.widget.LinearLayout.LayoutParams closeParams = new android.widget.LinearLayout.LayoutParams(closeBtnSize, closeBtnSize);

        titleRow.addView(infoIcon, iconParams);
        titleRow.addView(title, titleParams);
        titleRow.addView(closeBtn, closeParams);

        android.widget.TextView body = new android.widget.TextView(this);
        body.setText("The center node represents your searched word. The green and yellow backgrounds represent a rough estimate of the relatedness value of the word node that's on it. Connections between words are considered arbitrary. Move the relatedness slider to adjust how many words you want to see respectively Click on a word node to see more details.");
        body.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
        body.setTextColor(Color.BLACK);
        android.widget.LinearLayout.LayoutParams bodyParams = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        bodyParams.topMargin = dpToPx(12);

        outer.addView(titleRow);
        outer.addView(body, bodyParams);
        card.addView(outer);
        builder.setView(card);
        builder.setCancelable(true);

        android.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
        }
        closeBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
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

    private void showPopoutMenu() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.word_map_popout_menu, null);
        bottomSheetDialog.setContentView(sheetView);

        ConstraintLayout closeButton = sheetView.findViewById(R.id.close);
        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }
}
