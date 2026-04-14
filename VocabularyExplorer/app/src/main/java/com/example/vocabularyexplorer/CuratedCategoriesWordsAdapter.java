package com.example.vocabularyexplorer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CuratedCategoriesWordsAdapter extends RecyclerView.Adapter<CuratedCategoriesWordsAdapter.CuratedCategoriesWordsViewHolder> {
    Context context;
    ArrayList<Word> wordList;

    public CuratedCategoriesWordsAdapter(Context context, ArrayList<Word> wordList) {
        this.context = context;
        this.wordList = wordList;
    }

    @NonNull
    @Override
    public CuratedCategoriesWordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_word, parent, false);
        return new CuratedCategoriesWordsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CuratedCategoriesWordsViewHolder holder, int position) {
        Word word = wordList.get(position);
        holder.wordTitle.setText(word.getTitle());
        holder.wordDefinitions.setText(word.getDefinitions());
        holder.wordLayout.setOnClickListener(v -> {
            Intent detailIntent = new Intent(context, WordDetailScreen.class);
            detailIntent.putExtra("word_data", word);
            context.startActivity(detailIntent);
        });
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public static class CuratedCategoriesWordsViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout wordLayout;
        TextView wordTitle;
        TextView wordDefinitions;

        public CuratedCategoriesWordsViewHolder(@NonNull View itemView) {
            super(itemView);
            wordLayout = itemView.findViewById(R.id.word_layout);
            wordTitle = itemView.findViewById(R.id.word_title);
            wordDefinitions = itemView.findViewById(R.id.word_definitions);
        }
    }
}
