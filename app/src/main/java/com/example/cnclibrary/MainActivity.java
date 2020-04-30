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
import com.example.cnclibrary.ui.dashboard.MyBagActivity;
import com.example.cnclibrary.ui.home.BookAdapter;
import com.example.cnclibrary.ui.home.HomeFragment;
import com.example.cnclibrary.ui.login.LoginActivity;
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
import com.google.firebase.firestore.DocumentSnapshot;
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

    public static String ROLE = "user"; // default is user
    //have user, admin
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        db = FirebaseFirestore.getInstance();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration= new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                    .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

//// google sign on
        mAuth = FirebaseAuth.getInstance();
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
    public void  myBagClk(android.view.View view) {
        Intent intent = new Intent(this, MyBagActivity.class);
        startActivity(intent);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(mAuth.getCurrentUser()!=null){
            FirebaseUser currentUser = mAuth.getCurrentUser();
            Log.i("tum","main ;current user :"+currentUser.getDisplayName());
            DocumentReference docRef = db.collection("users").document(currentUser.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if(documentSnapshot.exists()){
                            ROLE = (String) documentSnapshot.get("role");
                        }
                    }
                }
            });
            //        updateUI(currentUser);
        }else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }


}
