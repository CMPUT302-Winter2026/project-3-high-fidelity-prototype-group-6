package com.example.vocabularyexplorer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CuratedCategoriesAdapter extends RecyclerView.Adapter<CuratedCategoriesAdapter.CuratedCategoriesViewHolder> {
    Context context;
    ArrayList<Category> categoryList;

    public CuratedCategoriesAdapter(Context context, ArrayList<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CuratedCategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_category, parent, false);
        return new CuratedCategoriesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CuratedCategoriesViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryTitle.setText(category.getTitle());
        holder.categoryImage.setImageResource(category.getImage());
        holder.categoryLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, CuratedCategoriesWordsScreen.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CuratedCategoriesViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout categoryLayout;
        TextView categoryTitle;
        ImageView categoryImage;

        public CuratedCategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryLayout = itemView.findViewById(R.id.category_layout);
            categoryTitle = itemView.findViewById(R.id.category_title);
            categoryImage = itemView.findViewById(R.id.category_image);
        }
    }
}
