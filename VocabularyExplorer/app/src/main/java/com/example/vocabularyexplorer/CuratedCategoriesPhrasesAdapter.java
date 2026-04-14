package com.example.vocabularyexplorer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CuratedCategoriesPhrasesAdapter extends RecyclerView.Adapter<CuratedCategoriesPhrasesAdapter.CuratedCategoriesPhrasesViewHolder> {
    Context context;
    ArrayList<Phrase> phraseList;

    public CuratedCategoriesPhrasesAdapter(Context context, ArrayList<Phrase> phraseList) {
        this.context = context;
        this.phraseList = phraseList;
    }

    @NonNull
    @Override
    public CuratedCategoriesPhrasesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_phrase, parent, false);
        return new CuratedCategoriesPhrasesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CuratedCategoriesPhrasesViewHolder holder, int position) {
        Phrase phrase = phraseList.get(position);
        ArrayList<PhraseComponent> components = phrase.getComponents();

        // Arrays to manage the views easily
        TextView[] titleViews = {holder.wordTitle, holder.wordTitle2, holder.wordTitle3};
        TextView[] definitionViews = {holder.wordDefinitions, holder.wordDefinitions2, holder.wordDefinitions3};

        // Reset views
        for (TextView tv : titleViews) {
            tv.setVisibility(View.GONE);
            tv.setText("");
        }
        for (TextView tv : definitionViews) {
            tv.setVisibility(View.GONE);
            tv.setText("");
        }

        // assume the components are ordered: Cree parts first, then English parts.
        for (int i = 0; i < components.size(); i++) {
            PhraseComponent component = components.get(i);
            if (i < 3) {
                titleViews[i].setVisibility(View.VISIBLE);
                titleViews[i].setText(component.getText());
                titleViews[i].setBackgroundColor(component.getColor());
            } else if (i < 6) {
                definitionViews[i - 3].setVisibility(View.VISIBLE);
                definitionViews[i - 3].setText(component.getText());
                definitionViews[i - 3].setBackgroundColor(component.getColor());
            }
        }
    }

    @Override
    public int getItemCount() {
        return phraseList.size();
    }

    public static class CuratedCategoriesPhrasesViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout wordLayout;
        TextView wordTitle, wordTitle2, wordTitle3;
        TextView wordDefinitions, wordDefinitions2, wordDefinitions3;

        public CuratedCategoriesPhrasesViewHolder(@NonNull View itemView) {
            super(itemView);
            wordLayout = itemView.findViewById(R.id.word_layout);
            wordTitle = itemView.findViewById(R.id.word_title);
            wordTitle2 = itemView.findViewById(R.id.word_title2);
            wordTitle3 = itemView.findViewById(R.id.word_title3);
            wordDefinitions = itemView.findViewById(R.id.word_definitions);
            wordDefinitions2 = itemView.findViewById(R.id.word_definitions2);
            wordDefinitions3 = itemView.findViewById(R.id.word_definitions3);
        }
    }
}
