package com.amanlahariya.gameWallpaper.activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amanlahariya.gameWallpaper.R;
import com.amanlahariya.gameWallpaper.adapters.CategoryAdapter;
import com.amanlahariya.gameWallpaper.fragments.FavouritesFragment;
import com.amanlahariya.gameWallpaper.fragments.HomeFragment;
import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView imageView_Header;
    String openPrevFragment = null;
    String appName = "Gmail";
    String packageName = "com.google.android.gm";
    Context context;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        FirebaseApp.initializeApp(context);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        if(getIntent().hasExtra("Fragment")){
            openPrevFragment = getIntent().getStringExtra("Fragment");
        }

        Fragment openFragment = new HomeFragment();
        if(openPrevFragment !=null){
            switch(openPrevFragment){
                case "FavouritesFragment":
                    openFragment = new FavouritesFragment();
                    break;
            }
        }
        displayFragment(openFragment);

        // To inflate header
        initializeHeader(navigationView);

        //---------Rating PopUp condition check----------//
        /*if(CategoryAdapter.mCount > 3)
            showPopUp();*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            finish();
            Intent intent = new Intent(MainActivity.this,SignIn.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = new HomeFragment();
        if (id == R.id.nav_home) {
            fragment =  new HomeFragment();
            toolbar.setTitle("Game Wallpaper App");
        } else if (id == R.id.nav_favourites) {
            fragment = new FavouritesFragment();
            toolbar.setTitle("Favourites");
        } else if (id == R.id.nav_removeAds) {
            toolbar.setTitle("Game Wallpaper App");
        } else if (id == R.id.nav_share) {
            shareApp();
            toolbar.setTitle("Game Wallpaper App");
        } else if (id == R.id.nav_rateUs) {
            showPopUp();
            toolbar.setTitle("Game Wallpaper App");
        }else if (id == R.id.nav_aboutMe){
            toolbar.setTitle("Game Wallpaper App");
        }else if (id == R.id.nav_report){
            openApp(context, appName, packageName);
            toolbar.setTitle("Game Wallpaper App");
        }
        displayFragment(fragment);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void displayFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_main_placeholder,fragment)
                .commit();
    }

    //To inflate imageView & name
    private void initializeHeader(NavigationView navigationView){
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        View hView = navigationView.getHeaderView(0);
        TextView textView_User = hView.findViewById(R.id.textView_HeaderUser);

        if(mUser != null){
            imageView_Header = hView.findViewById(R.id.imageView_Header);
            Glide.with(this)
                    .load(mUser.getPhotoUrl())
                    .into(imageView_Header);
            textView_User.setText(mUser.getDisplayName());
        }else {

            textView_User.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    Intent intent = new Intent(MainActivity.this,SignIn.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void shareApp(){
        Intent intent = new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT,"Download this awesome game wallpaper's app https://play.google.com/store/apps/details?id=com.amanlahariya.gameWallpaper");
        startActivity(Intent.createChooser(intent,"Choose an app"));
    }

    /*----------Code for Rating PopUP----------*/
    public void showPopUp(){
        final Dialog mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.rating_pop_up);
        final RatingBar mRating = mDialog.findViewById(R.id.ratingBar);
        Button remindMe = mDialog.findViewById(R.id.remindMe);

        mRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                float rating = mRating.getRating();
                if(rating>=3){
                    //CategoryAdapter.mCount=0;
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    // To count with Play market backstack, After pressing back button,
                    // to taken back to our application, we need to add following flags to intent.
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                    }
                    mDialog.dismiss();
                    Toast.makeText(context, "Thank You for rating us!", Toast.LENGTH_SHORT).show();
                }
                else{
                    openApp(context, appName, packageName);
                    /*TODO:mCount*/
                    //CategoryAdapter.mCount=0;
                    mDialog.dismiss();
                }
            }
        });

        remindMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CategoryAdapter.mCount=0;
                mDialog.dismiss();
            }
        });
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();
    }

    /*-----------Code to open GMail with validation----------*/
    public  static void openApp(Context context, String appName, String packageName) {
        if (isAppInstalled(context, packageName))
            if (isAppEnabled(context, packageName)) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setPackage("com.google.android.gm");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"developer.amanlahariya@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Game Wallpapers app");
                intent.setType("message/rfc822");
                context.startActivity(intent);
                Toast.makeText(context, "Tell us what went wrong and we'll work on it!", Toast.LENGTH_LONG).show();
            }
            else Toast.makeText(context, appName + " app is not enabled.", Toast.LENGTH_SHORT).show();
        else Toast.makeText(context, appName + " app is not installed.", Toast.LENGTH_SHORT).show();
    }

    private static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return false;
    }

    private static boolean isAppEnabled(Context context, String packageName) {
        boolean appStatus = false;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(packageName, 0);
            if (ai != null) {
                appStatus = ai.enabled;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appStatus;
    }
}
