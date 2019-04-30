package com.amanlahariya.gameWallpaper.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amanlahariya.gameWallpaper.R;
import com.amanlahariya.gameWallpaper.fragments.WallpaperFragment;
import com.amanlahariya.gameWallpaper.models.Category;
import com.bumptech.glide.Glide;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;
    //public static int mCount = 0;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_category,viewGroup,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int position) {
        Category c = categoryList.get(position);
        categoryViewHolder.textView.setText(c.name);
        Glide.with(context)
                .load(c.thumb)
                .into(categoryViewHolder.imageView);

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textView;
        ImageView imageView;

        private CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_Category);
            imageView = itemView.findViewById(R.id.imageView_Category);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            //----------Counter for showing rating popUp---------//
            //mCount++;

            Category c = categoryList.get(getAdapterPosition());

            Bundle bundle = new Bundle();
            bundle.putString("category",c.name);

            Fragment fragment = new WallpaperFragment();
            fragment.setArguments(bundle);

            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_main_placeholder,fragment)
                    .addToBackStack(null)
                    .commit();

        }
    }

}
