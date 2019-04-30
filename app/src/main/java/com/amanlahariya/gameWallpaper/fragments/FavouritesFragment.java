package com.amanlahariya.gameWallpaper.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.amanlahariya.gameWallpaper.R;
import com.amanlahariya.gameWallpaper.activities.MainActivity;
import com.amanlahariya.gameWallpaper.activities.SignIn;
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
import java.util.List;

public class FavouritesFragment extends Fragment {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    FirebaseUser user;
    List <Wallpaper> favList;
    WallpaperAdapter favAdapter;
    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        // Inflate the layout for this fragment
        if(user != null){
            return inflater.inflate(R.layout.fragment_favourites, container, false);
        }else{
            Intent intent = new Intent(getActivity(), SignIn.class);
            intent.putExtra("Fragment","FavouritesFragment");
            startActivity(intent);
            return null;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView_favourites);
        progressBar = view.findViewById(R.id.progressBar_favourites);
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(user != null){
            fetchFav();
        }
    }

    public void fetchFav(){
        final View view = getView();
        favList = new ArrayList<>();
        DatabaseReference dbFav = FirebaseDatabase.getInstance().getReference("users")
                .child(user.getUid())
                .child("favourites");
        dbFav.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if(dataSnapshot.exists()){
                    favAdapter = new WallpaperAdapter(getActivity(),favList);
                    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
                    recyclerView.setAdapter(favAdapter);
                    for(DataSnapshot category:dataSnapshot.getChildren()){
                        for(DataSnapshot wallpaper:category.getChildren()) {
                            String id = wallpaper.getKey();
                            String url = wallpaper.child("url").getValue(String.class);
                            Wallpaper w = new Wallpaper(id,url,category.getKey());
                            w.isFavourite = true;
                            favList.add(w);
                        }
                    }
                    favAdapter.notifyDataSetChanged();

                }else {
                    view.findViewById(R.id.textView_noFav).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
