package com.example.vocabularyexplorer;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MatchingMinigameScreen extends AppCompatActivity {

    // -----------------------------------------------------------------------
    // Data model
    // -----------------------------------------------------------------------

    /**
     * A single round of the minigame.
     * correctOrder[i] is the answer that belongs in slot (i+1).
     */
    private static class Round {
        final String wordImageDescription; // swap for real drawable id when ready
        final String[] correctOrder;       // index 0 → slot 1, index 4 → slot 5

        Round(String wordImageDescription, String[] correctOrder) {
            this.wordImageDescription = wordImageDescription;
            this.correctOrder = correctOrder;
        }
    }

    // -----------------------------------------------------------------------
    // Sample data  — replace / extend as needed
    // -----------------------------------------------------------------------
    private final List<Round> rounds = new ArrayList<>(Arrays.asList(
            new Round("pawâcakinâsis-pîsim",
                    new String[]{"mikisiwi-pîsim", "pîsim", "kôna", "sîkwan", "masinahikan"})
    ));

    // -----------------------------------------------------------------------
    // State
    // -----------------------------------------------------------------------
    private int currentRound = 0;
    private int totalScore  = 0;
    private boolean submitted = false;

    /** Current text shown in each slot (index 0 = slot 1). */
    private final String[] slotContents = new String[5];

    // -----------------------------------------------------------------------
    // View references
    // -----------------------------------------------------------------------
    private TextView   scoreDisplay;
    private TextView[] answerViews  = new TextView[5];
    private TextView[] resultViews  = new TextView[5];
    private MaterialCardView[] cards = new MaterialCardView[5];
    private View[] cardBackgrounds = new View[5];
    private ImageButton submitButton;
    private MaterialCardView helpCard, finishCard, nextCard;

    // Drag state
    private int dragSourceSlot = -1;

    // -----------------------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.matching_minigame_screen);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.matching_minigame), (v, insets) -> {
                    Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
                    return insets;
                });

        bindViews();
        setupDragAndDrop();
        setupButtons();
        loadRound(currentRound);
    }

    // -----------------------------------------------------------------------
    // View binding
    // -----------------------------------------------------------------------

    private void bindViews() {
        scoreDisplay = findViewById(R.id.score_display);

        int[] answerIds     = {R.id.answer1, R.id.answer2, R.id.answer3, R.id.answer4, R.id.answer5};
        int[] resultIds     = {R.id.result1, R.id.result2, R.id.result3, R.id.result4, R.id.result5};
        int[] cardIds       = {R.id.card1,   R.id.card2,   R.id.card3,   R.id.card4,   R.id.card5};
        int[] bgIds         = {R.id.card_bg1, R.id.card_bg2, R.id.card_bg3, R.id.card_bg4, R.id.card_bg5};

        for (int i = 0; i < 5; i++) {
            answerViews[i]    = findViewById(answerIds[i]);
            resultViews[i]    = findViewById(resultIds[i]);
            cards[i]          = findViewById(cardIds[i]);
            cardBackgrounds[i] = findViewById(bgIds[i]);
        }

        submitButton = findViewById(R.id.submit_button);
        helpCard     = findViewById(R.id.help_card);
        finishCard   = findViewById(R.id.finish_card);
        nextCard     = findViewById(R.id.next_card);
    }

    // -----------------------------------------------------------------------
    // Round loading
    // -----------------------------------------------------------------------

    private void loadRound(int index) {
        submitted = false;
        Round round = rounds.get(index);

        // Shuffle answers into slots
        List<String> shuffled = new ArrayList<>(Arrays.asList(round.correctOrder));
        Collections.shuffle(shuffled);
        for (int i = 0; i < 5; i++) {
            slotContents[i] = shuffled.get(i);
            answerViews[i].setText(slotContents[i]);
            resultViews[i].setVisibility(View.GONE);
            resetCardColor(cards[i], cardBackgrounds[i]);
        }

        // Update word card: swap setTag/setImageResource for real data
        // findViewById(R.id.matching_word_card).setImageResource(round.drawableId);

        // Button visibility
        submitButton.setVisibility(View.VISIBLE);
        // hide submit's parent card too
        finishCard.setVisibility(View.GONE);
        nextCard.setVisibility(View.GONE);
        helpCard.setVisibility(View.VISIBLE);
    }

    // -----------------------------------------------------------------------
    // Drag and drop
    // -----------------------------------------------------------------------

    private void setupDragAndDrop() {
        int[] dragHandleIds = {
                R.id.drag_handle1, R.id.drag_handle2, R.id.drag_handle3,
                R.id.drag_handle4, R.id.drag_handle5
        };

        for (int i = 0; i < 5; i++) {
            final int slotIndex = i;
            ImageButton handle = findViewById(dragHandleIds[i]);

            // Long-press the drag handle to start drag
            handle.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !submitted) {
                    dragSourceSlot = slotIndex;
                    View.DragShadowBuilder shadow = new View.DragShadowBuilder(cards[slotIndex]);
                    cards[slotIndex].startDragAndDrop(null, shadow, slotIndex, 0);
                    return true;
                }
                return false;
            });

            // Each card is a valid drop target
            cards[i].setOnDragListener(createDropListener(slotIndex));
        }
    }

    private View.OnDragListener createDropListener(int targetSlot) {
        return (v, event) -> {
            switch (event.getAction()) {

                case DragEvent.ACTION_DRAG_ENTERED:
                    highlightCard(cards[targetSlot], true);
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                case DragEvent.ACTION_DRAG_ENDED:
                    highlightCard(cards[targetSlot], false);
                    return true;

                case DragEvent.ACTION_DROP:
                    highlightCard(cards[targetSlot], false);
                    int sourceSlot = (int) event.getLocalState();
                    if (sourceSlot != targetSlot) {
                        swapSlots(sourceSlot, targetSlot);
                    }
                    return true;

                default:
                    return true;
            }
        };
    }

    private void swapSlots(int a, int b) {
        String tmp = slotContents[a];
        slotContents[a] = slotContents[b];
        slotContents[b] = tmp;

        answerViews[a].setText(slotContents[a]);
        answerViews[b].setText(slotContents[b]);
    }

    // -----------------------------------------------------------------------
    // Submit logic
    // -----------------------------------------------------------------------

    private void setupButtons() {
        submitButton.setOnClickListener(v -> handleSubmit());

        ImageButton helpButton = findViewById(R.id.help_button);
        helpButton.setOnClickListener(v -> showHint());

        ImageButton nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(v -> {
            currentRound = (currentRound + 1) % rounds.size();
            loadRound(currentRound);
        });

        ImageButton finishButton = findViewById(R.id.finish_button);
        finishButton.setOnClickListener(v -> finish()); // replace with nav to results screen

        // Bottom bar
        findViewById(R.id.back_button).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.home_button).setOnClickListener(v -> finish());
    }

    private void handleSubmit() {
        if (submitted) return;
        submitted = true;

        Round round = rounds.get(currentRound);
        int roundScore = 0;

        for (int i = 0; i < 5; i++) {
            boolean correct = slotContents[i].equals(round.correctOrder[i]);
            if (correct) roundScore++;

            // Show checkmark (green) or cross (red)
            resultViews[i].setText(correct ? "✓" : "✗");
            resultViews[i].setTextColor(correct
                    ? getColor(R.color.green)
                    : Color.RED);
            resultViews[i].setVisibility(View.VISIBLE);

            // Tint the inner card background view
            animateViewColor(cardBackgrounds[i], correct
                    ? 0xFFDFF5E1   // light green
                    : 0xFFFFE0E0); // light red
        }

        totalScore += roundScore;
        scoreDisplay.setText("Score: " + totalScore);

        // Show Submit -> hide; always show both Finish and Next
        submitButton.setVisibility(View.GONE);
        helpCard.setVisibility(View.GONE);
        finishCard.setVisibility(View.VISIBLE);
        nextCard.setVisibility(View.VISIBLE);
    }

    // -----------------------------------------------------------------------
    // Instructions popup
    // -----------------------------------------------------------------------

    private void showHint() {
        // Inflate a custom dialog layout built programmatically
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.RoundedDialogTheme);

        // Root card
        com.google.android.material.card.MaterialCardView card =
                new com.google.android.material.card.MaterialCardView(this);
        card.setRadius(dpToPx(16));
        card.setCardElevation(dpToPx(8));
        card.setCardBackgroundColor(Color.WHITE);

        // Outer padding container
        android.widget.LinearLayout outer = new android.widget.LinearLayout(this);
        outer.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = dpToPx(20);
        outer.setPadding(pad, pad, pad, pad);

        // ── Row 1: ⓘ  title  ✕ ──
        android.widget.LinearLayout titleRow = new android.widget.LinearLayout(this);
        titleRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        titleRow.setGravity(android.view.Gravity.CENTER_VERTICAL);

        // Info icon
        android.widget.ImageView infoIcon = new android.widget.ImageView(this);
        infoIcon.setImageResource(android.R.drawable.ic_dialog_info);
        infoIcon.setColorFilter(Color.BLACK);
        int iconSize = dpToPx(24);
        android.widget.LinearLayout.LayoutParams iconParams =
                new android.widget.LinearLayout.LayoutParams(iconSize, iconSize);
        iconParams.setMarginEnd(dpToPx(10));
        infoIcon.setLayoutParams(iconParams);

        // Title
        android.widget.TextView title = new android.widget.TextView(this);
        title.setText("Game Instructions");
        title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 18);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setTextColor(Color.BLACK);
        android.widget.LinearLayout.LayoutParams titleParams =
                new android.widget.LinearLayout.LayoutParams(
                        0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        title.setLayoutParams(titleParams);

        // ✕ close button
        android.widget.ImageButton closeBtn = new android.widget.ImageButton(this);
        closeBtn.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        closeBtn.setBackground(null);
        closeBtn.setColorFilter(Color.BLACK);
        int closeBtnSize = dpToPx(24);
        android.widget.LinearLayout.LayoutParams closeParams =
                new android.widget.LinearLayout.LayoutParams(closeBtnSize, closeBtnSize);
        closeBtn.setLayoutParams(closeParams);

        titleRow.addView(infoIcon, iconParams);
        titleRow.addView(title, titleParams);
        titleRow.addView(closeBtn, closeParams);

        // ── Row 2: body text ──
        android.widget.TextView body = new android.widget.TextView(this);
        body.setText("To play the matching game, order the five words (by dragging them) based on their highest relatedness to the word in the middle card. Check your answer by clicking submit and you'll receive points with how many you get correct!");
        body.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
        body.setTextColor(Color.BLACK);
        android.widget.LinearLayout.LayoutParams bodyParams =
                new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        bodyParams.topMargin = dpToPx(12);
        body.setLayoutParams(bodyParams);

        outer.addView(titleRow);
        outer.addView(body, bodyParams);
        card.addView(outer);

        builder.setView(card);
        builder.setCancelable(true);

        android.app.AlertDialog dialog = builder.create();

        // Transparent window background so only the card shows
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
        }

        closeBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // -----------------------------------------------------------------------
    // Visual helpers
    // -----------------------------------------------------------------------

    private void highlightCard(MaterialCardView card, boolean highlight) {
        card.setStrokeColor(highlight ? 0xFF6200EE : Color.TRANSPARENT);
        card.setStrokeWidth(highlight ? 4 : 0);
    }

    private void resetCardColor(MaterialCardView card, View bg) {
        card.setCardBackgroundColor(Color.WHITE);
        card.setStrokeWidth(0);
        bg.setBackgroundColor(Color.TRANSPARENT);
        // Re-apply the rounded_rectangle drawable
        bg.setBackgroundResource(R.drawable.rounded_rectangle);
    }

    private void animateViewColor(View view, int targetColor) {
        int startColor = Color.TRANSPARENT;
        ValueAnimator animator = ValueAnimator.ofObject(
                new ArgbEvaluator(), startColor, targetColor);
        animator.setDuration(350);
        animator.addUpdateListener(a ->
                view.setBackgroundColor((int) a.getAnimatedValue()));
        animator.start();
    }
}