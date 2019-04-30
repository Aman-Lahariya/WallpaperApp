package com.amanlahariya.gameWallpaper.activities;

import android.Manifest;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.amanlahariya.gameWallpaper.BuildConfig;
import com.amanlahariya.gameWallpaper.R;
import com.amanlahariya.gameWallpaper.models.Wallpaper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class PreviewWallpaperActivity extends AppCompatActivity {

    private static final String FILE_PROVIDER = BuildConfig.APPLICATION_ID + ".provider";
    ImageView imageView;
    CheckBox favourite;
    ImageButton download, share, setWall;
    Wallpaper wl;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_wallpaper);
        /*StrictMode.VmPolicy policy = new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setVmPolicy(policy);*/
        context = this;
        wl = (Wallpaper) getIntent().getSerializableExtra("Wallpaper");

        initializeWidgets();

        Glide.with(getApplicationContext())
                .load(wl.url)
                .into(imageView);
        if(wl.isFavourite){
            favourite.setChecked(true);
        }
        addListeners();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    public void initializeWidgets(){
        imageView = findViewById(R.id.imageView_Preview_Wallpaper);
        favourite = findViewById(R.id.cb_fav);
        download = findViewById(R.id.ib_download);
        share = findViewById(R.id.ib_share);
        setWall = findViewById(R.id.ib_setWall);
    }

    /* --- Adding Listeners --- */
    public void addListeners(){

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadWallpaper();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWallpaper();
            }
        });

        setWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWallpaper();
            }
        });

        favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(FirebaseAuth.getInstance().getCurrentUser() == null){
                    Toast.makeText(PreviewWallpaperActivity.this,"Please login first...",Toast.LENGTH_SHORT).show();
                    buttonView.setChecked(false);
                    Intent signInIntent = new Intent(PreviewWallpaperActivity.this,SignIn.class);
                    startActivity(signInIntent);
                }else{

                    DatabaseReference dbFavRef = FirebaseDatabase.getInstance().getReference("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("favourites")
                            .child(wl.category);

                    if(isChecked){
                        dbFavRef.child(wl.id)
                                .child("url").setValue(wl.url);
                    }else {
                        dbFavRef.child(wl.id).removeValue();
                        wl.isFavourite = false;
                    }
                }
            }
        });
    }

    public void shareWallpaper(){

        Glide.with(context)
            .asBitmap()
            .load(wl.url)
            .into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    Uri imageUri = getLocalBitmap(resource);
                    intent.putExtra(Intent.EXTRA_STREAM,imageUri);
                    startActivity(Intent.createChooser(intent,"Choose application to share wallpaper with..."));
                }
            });


    }

    private Uri getLocalBitmap(Bitmap bmp){
        Uri bmpUri = null;
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "game_wallpaper_"+System.currentTimeMillis()+".png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.close();
            bmpUri = getUri(context,file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    //For backward compatabality
    private Uri getUri(Context context,File file){
         if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            return Uri.fromFile(file);
        } else {
            return FileProvider.getUriForFile(context, FILE_PROVIDER, file);
        }
    }


    private void downloadWallpaper(){
        Glide.with(context)
                .asBitmap()
                .load(wl.url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        Uri imageUri = saveWallpaperAndGetUri(resource);
                        if(imageUri != null) {
                            Toast.makeText(context,"Wallpaper downloaded successfully!",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(imageUri,"image/*");
                            startActivity(Intent.createChooser(intent, "Open wallpaper with..."));
                        }
                    }
                });
    }

    private Uri saveWallpaperAndGetUri(Bitmap bitmap){

        checkPermission();

        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/Game_Wallpapers");
        folder.mkdirs();
        File file = new File(folder,wl.id +".jpg");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.flush();
            outputStream.close();
            return getUri(context,file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            //To check whether we had request permission before or not.
            if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                //Explanation to y we need permission
                Toast.makeText(context,"We need storage permission so that you can download wallpapers on your phone!",Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",context.getPackageName(),null);
                intent.setData(uri);
                startActivity(intent);
            }else{
                ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
            }
        }
    }

    private void setWallpaper(){
        final WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
        findViewById(R.id.progressBar_preview).setVisibility(View.VISIBLE);
        Toast.makeText(context,"Please wait while we are setting wallpaper for you",Toast.LENGTH_SHORT).show();
        WindowManager windowManager;windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Glide.with(context)
        .asBitmap()
        .load(wl.url)
        .into(new SimpleTarget<Bitmap>(width,height) {
              @Override
              public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                  findViewById(R.id.progressBar_preview).setVisibility(View.GONE);
                  try {
                      myWallpaperManager.setBitmap(resource);
                      Toast.makeText(context, "Walpaper Set Successfully", Toast.LENGTH_LONG).show();
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }
          }
        );
    }
}
