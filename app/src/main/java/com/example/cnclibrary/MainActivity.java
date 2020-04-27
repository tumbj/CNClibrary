package com.example.cnclibrary;//package com.example.cnclibrary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.cnclibrary.admin.AddBookActivity;
import com.example.cnclibrary.admin.ListBorrowedActivity;
import com.example.cnclibrary.data.model.Book;
import com.example.cnclibrary.data.model.User;
import com.example.cnclibrary.ui.home.BookAdapter;
import com.example.cnclibrary.ui.home.HomeFragment;
import com.example.cnclibrary.ui.scanner.BorrowActivity;
import com.example.cnclibrary.ui.scanner.ReturnActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    GoogleSignInClient mGoogleSignInClient ;
    int GOOGLE_SIGN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        db = FirebaseFirestore.getInstance();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

//// google sign on
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    public void borrowClk(android.view.View view) {
        Intent intent = new Intent(this, BorrowActivity.class);
        startActivity(intent);
    }

    public void returnClk(android.view.View view) {
        Intent intent = new Intent(this, ReturnActivity.class);
        startActivity(intent);
    }

    public void addBookClk(android.view.View view) {
        Intent intent = new Intent(this, AddBookActivity.class);
        startActivity(intent);
    }

    public void  onClkListBorrowed(android.view.View view) {
        Intent intent = new Intent(this, ListBorrowedActivity.class);
        startActivity(intent);
    }
    public void onClkSignIn(android.view.View view){
        Log.i("tum","sign in was clicked1");

        signIn();
    }
    public void onClkSignOut(android.view.View view){
        Log.i("tum","sign out was clicked1");
        mAuth.signOut();
    }
    private void signIn() {
        Log.i("tum","sign in was clicked2");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,GOOGLE_SIGN);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(mAuth.getCurrentUser()!=null){
            FirebaseUser currentUser = mAuth.getCurrentUser();
            Log.i("tum","current user :"+currentUser.getDisplayName());
            //        updateUI(currentUser);
        }else {
            signIn();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.i("tum","account "+account.getDisplayName());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("tum", "Google sign in failed", e);
                e.printStackTrace();
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("tum", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("tum", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                            storeUserToDB(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("tum", "signInWithCredential:failure", task.getException());
//                            Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }

    private void storeUserToDB(FirebaseUser user) {
        Log.i("auth","display name :"+user.getDisplayName()+", email : "+user.getEmail()
        + ",uid : "+user.getUid());
        String uid = user.getUid();
        String displayName = user.getDisplayName();
        String email = user.getEmail();
        User newUser = new User(uid,email,displayName);
        db.collection("users").document(uid).set(newUser).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("db","cannot create user to db");
            }
        });


    }

}
