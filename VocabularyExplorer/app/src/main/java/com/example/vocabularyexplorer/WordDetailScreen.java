package com.example.vocabularyexplorer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class WordDetailScreen extends AppCompatActivity {
    private TextView modeText;
    private SwitchCompat modeSwitch;
    private TextView wordTitle;
    private ImageButton wordAudio;
    private ImageButton wordShare;
    private TextView wordDefinitions;
    private TextView wordCreePhrase1;
    private TextView wordEnglishPhrase1;
    private TextView wordCreePhrase2;
    private TextView wordEnglishPhrase2;

    private TextView wordSyllabics;
    private ImageView wordNoviceLabel;
    private TextView wordAdvancedLabel;
    private TextView wordIPATranscription;
    private ConstraintLayout morphologyLayout;
    private ImageView wordParts;
    private TextView wordMorphology;
    private ConstraintLayout originLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.word_detail_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.word_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        SharedPreferences sharedPreferences = getSharedPreferences("mode", MODE_PRIVATE);
        switchModes(sharedPreferences.getString("mode", "novice"));
        replaceInformation();

        modeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String mode = isChecked ? "advanced" : "novice";
            Toast.makeText(WordDetailScreen.this, (isChecked ? "Advanced" : "Novice") + " Mode enabled", Toast.LENGTH_SHORT).show();
            sharedPreferences.edit().putString("mode", mode).apply();
            switchModes(mode);
        });

        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        modeText = findViewById(R.id.mode_text);
        modeSwitch = findViewById(R.id.mode_switch);
        wordTitle = findViewById(R.id.word_title);
        wordAudio = findViewById(R.id.word_audio);
        wordShare = findViewById(R.id.word_share);
        wordDefinitions = findViewById(R.id.word_definitions);
        wordCreePhrase1 = findViewById(R.id.word_cree_phrase1);
        wordEnglishPhrase1 = findViewById(R.id.word_english_phrase1);
        wordCreePhrase2 = findViewById(R.id.word_cree_phrase2);
        wordEnglishPhrase2 = findViewById(R.id.word_english_phrase2);
        wordSyllabics = findViewById(R.id.word_syllabics);
        wordNoviceLabel = findViewById(R.id.word_novice_label);
        wordAdvancedLabel = findViewById(R.id.word_advanced_label);
        wordIPATranscription = findViewById(R.id.word_ipa_transcription);
        morphologyLayout = findViewById(R.id.morphology_layout);
        wordParts = findViewById(R.id.word_parts);
        wordMorphology = findViewById(R.id.word_morphology);
        originLayout = findViewById(R.id.origin_layout);
    }

    private void switchModes(String mode) {
        if (Objects.equals(mode, "novice")) {
            modeSwitch.setChecked(false);
            modeText.setText("Novice Mode");
            wordSyllabics.setVisibility(GONE);
            wordNoviceLabel.setVisibility(GONE);
            wordAdvancedLabel.setVisibility(GONE);
            wordIPATranscription.setVisibility(GONE);
            morphologyLayout.setVisibility(GONE);
            originLayout.setVisibility(GONE);
        } else if (Objects.equals(mode, "advanced")) {
            modeSwitch.setChecked(true);
            modeText.setText("Advanced Mode");
            wordSyllabics.setVisibility(VISIBLE);
            wordNoviceLabel.setVisibility(VISIBLE);
            wordAdvancedLabel.setVisibility(VISIBLE);
            wordIPATranscription.setVisibility(VISIBLE);
            morphologyLayout.setVisibility(VISIBLE);
            originLayout.setVisibility(VISIBLE);
        }
    }

    private void replaceInformation() {
        Word word = getIntent().getSerializableExtra("word_data", Word.class);
        if (word != null) {
            wordTitle.setText(word.getTitle());
            wordDefinitions.setText(word.getDefinitions());
            
            // Handle phrases visibility
            if (word.getCreePhrase1() != null) {
                wordCreePhrase1.setText(android.text.Html.fromHtml(word.getCreePhrase1(), android.text.Html.FROM_HTML_MODE_LEGACY));
                wordCreePhrase1.setVisibility(VISIBLE);
            } else {
                wordCreePhrase1.setVisibility(GONE);
            }

            if (word.getEnglishPhrase1() != null) {
                wordCreePhrase1.setText(android.text.Html.fromHtml(word.getCreePhrase1(), android.text.Html.FROM_HTML_MODE_LEGACY));
                wordEnglishPhrase1.setVisibility(VISIBLE);
            } else {
                wordEnglishPhrase1.setVisibility(GONE);
            }

            // You can add logic for Phrase 2 similarly...
        }
    }
}