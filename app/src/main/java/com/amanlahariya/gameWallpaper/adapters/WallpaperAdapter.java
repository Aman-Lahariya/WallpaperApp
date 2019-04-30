package com.amanlahariya.gameWallpaper.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amanlahariya.gameWallpaper.R;
import com.amanlahariya.gameWallpaper.activities.MainActivity;
import com.amanlahariya.gameWallpaper.activities.PreviewWallpaperActivity;
import com.amanlahariya.gameWallpaper.fragments.WallpaperFragment;
import com.amanlahariya.gameWallpaper.models.Wallpaper;
import com.bumptech.glide.Glide;

import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.CategoryViewHolder> {

    private Context context;
    private List<Wallpaper> wallpaperList;

    public WallpaperAdapter(Context context, List<Wallpaper> wallpaperList) {
        this.context = context;
        this.wallpaperList = wallpaperList;
    }
    public WallpaperAdapter() {

    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_wallpaper,viewGroup,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int position) {
        Wallpaper w = wallpaperList.get(position);
        Glide.with(context)
                .load(w.url)
                .into(categoryViewHolder.imageView);

    }

    @Override
    public int getItemCount() {
        return wallpaperList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;

        private CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_Wallpaper);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Wallpaper wl = wallpaperList.get(getAdapterPosition());
            Intent intent = new Intent(v.getContext(), PreviewWallpaperActivity.class);
            intent.putExtra("Wallpaper",wl);
            v.getContext().startActivity(intent);
        }
    }



}
