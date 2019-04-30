package com.amanlahariya.gameWallpaper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amanlahariya.gameWallpaper.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends AppCompatActivity {

    private static final String TAG = "SignIn";
    private static final int GOOGLE_SIGN_IN_CODE = 101;
    private GoogleSignInClient googleSignInClient;
    private FirebaseUser firebaseUser;
    String fragment = null;
    //String activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragment = getIntent().getStringExtra("Fragment");
        //activity = getIntent().getStringExtra("Activity");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser == null){
            setContentView(R.layout.activity_sign_in);
            findViewById(R.id.progressBar_SignIn).setVisibility(View.GONE);
        }
        else{
            setContentView(R.layout.activity_logged_in);
        }

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //When user is logged in
        if(firebaseUser != null){
            ImageView profilePic = findViewById(R.id.imageView_profilePic);
            TextView user = findViewById(R.id.textView_UserName);
            TextView email = findViewById(R.id.textView_Email);

            Glide.with(getApplicationContext())
                    .load(firebaseUser.getPhotoUrl())
                    .into(profilePic);
            user.setText(firebaseUser.getDisplayName());
            email.setText(firebaseUser.getEmail());

            //TO Log Out
            findViewById(R.id.tv_logOut).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                            Intent intent = new Intent(SignIn.this,MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            });
        }//When user is not logged in
        else{
            findViewById(R.id.googleButton_SignIn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = googleSignInClient.getSignInIntent();
                    startActivityForResult(intent,GOOGLE_SIGN_IN_CODE);
                    Toast.makeText(SignIn.this,"Please wait!",Toast.LENGTH_SHORT).show();
                    findViewById(R.id.progressBar_SignIn).setVisibility(View.VISIBLE);
                }
            });
        }
    }

/*Will run only when not logged in*/
/*Code specific to Sign-In*/
    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                //updateUI(null);
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        findViewById(R.id.progressBar_SignIn).setVisibility(View.VISIBLE);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(SignIn.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    finish();

                    //Default re-direct to main activity if login id successful
                    Intent intent = new Intent(SignIn.this,MainActivity.class);
                    if(fragment !=null){
                        //over-rides redirect to the fragment from where we arrived
                        intent.putExtra("Fragment",fragment);
                    }
                    startActivity(intent);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(SignIn.this,"Login Failed!",Toast.LENGTH_LONG).show();
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                    //updateUI(null);
                }
                findViewById(R.id.progressBar_SignIn).setVisibility(View.GONE);
            }
        });
    }// [END auth_with_google]

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        } else {
            getFragmentManager().popBackStack();
        }
    }
}
