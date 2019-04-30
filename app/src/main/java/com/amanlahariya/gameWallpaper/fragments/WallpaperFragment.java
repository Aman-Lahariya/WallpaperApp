package com.amanlahariya.gameWallpaper.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.amanlahariya.gameWallpaper.R;
import com.amanlahariya.gameWallpaper.adapters.WallpaperAdapter;
import com.amanlahariya.gameWallpaper.models.Wallpaper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WallpaperFragment extends Fragment {

    private String catName = null;
    private ProgressBar progressBar;
    private List<Wallpaper> wallpaperList;
    private List<Wallpaper> favList;
    private WallpaperAdapter wallpaperAdapter;
    private FirebaseUser user = null;
    private boolean loggedIn = false;

    public WallpaperFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        catName = getArguments().getString("category");
        return inflater.inflate(R.layout.fragment_wallpaper,container,false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progressBar_wallpaper);
        progressBar.setVisibility(view.VISIBLE);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_wallpaper);
        wallpaperList = new ArrayList<>();
        wallpaperAdapter = new WallpaperAdapter(getActivity(),wallpaperList);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.setAdapter(wallpaperAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            loggedIn = true;
            favList = new ArrayList<>();
            fetchFav();
        }else {
            fetchWallpapers();
        }

    }

    public void fetchWallpapers(){
        DatabaseReference dbWallpaers;
        dbWallpaers = FirebaseDatabase.getInstance().getReference("images").child(catName);
        dbWallpaers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                    wallpaperList.clear();
                    for(DataSnapshot ds: dataSnapshot.getChildren()){

                        String id = ds.getKey();
                        String name = ds.child("title").getValue(String.class);
                        String url = ds.child("url").getValue(String.class);
                        Wallpaper w = new Wallpaper(id,name,url,catName);
                        if(loggedIn){
                            if(isFavourite(w)){
                                w.isFavourite = true;
                            }
                        }
                        wallpaperList.add(w);
                    }
                    wallpaperAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void fetchFav(){
        DatabaseReference dbFavs;
        dbFavs = FirebaseDatabase.getInstance().getReference("users")
                .child(user.getUid())
                .child("favourites")
                .child(catName);
        dbFavs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        favList = new ArrayList<>();
                        String id = ds.getKey();
                        String url = ds.child("url").getValue(String.class);
                        favList.add(new Wallpaper(id,url));
                    }
                }
                fetchWallpapers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean isFavourite(Wallpaper w){
        for(Wallpaper f : favList){
            if(f.id.equals(w.id)){
                return true;
            }
        }
        return false;
    }

}
