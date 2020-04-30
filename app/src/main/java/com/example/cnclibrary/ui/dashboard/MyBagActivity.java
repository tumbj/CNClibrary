package com.example.cnclibrary.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cnclibrary.R;
import com.example.cnclibrary.data.model.Book;
import com.example.cnclibrary.data.model.BookInBag;
import com.example.cnclibrary.data.model.User;
import com.example.cnclibrary.data.model.UserBookHistory;
import com.example.cnclibrary.ui.home.BookAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MyBagActivity extends AppCompatActivity {
    MyBagAdapter adapter;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ArrayList<BookInBag> books;
    ProgressBar progressBar;
    TextView alertView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bag);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        books = new ArrayList<>();
        progressBar = findViewById(R.id.my_bag_progress_bar);
        alertView = findViewById(R.id.alert_view);
        alertView.setVisibility(View.GONE);
        alertView.setText("Not have book in bag!!!");
        setUpRecyclerView();
        loadDataFromFirebase();
    }
    private void loadDataFromFirebase() {
        String uid = mAuth.getUid();
        progressBar.setVisibility(View.VISIBLE);
        db.collection("users").document(uid).collection("bags")
                .whereEqualTo("end_date",null).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        if (doc.exists()) {
                            String start_date = (String) doc.get("start_date");
                            String barcode = (String) doc.get("barcode");
                            db.collection("books").document(barcode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot doc = task.getResult();
                                    BookInBag book = new BookInBag((String) doc.get("img"), (String) doc.get("name"), start_date);
                                    books.add(book);
                                    Log.i("tum", "book length" + books.size());
                                    adapter = new MyBagAdapter(books);
                                    recyclerView.setAdapter(adapter);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }else {
                            Log.i("tum","doc not exist");
                        }
                    }
                }else{
                    Log.i("tum","not found book from DB");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        }).continueWith(new Continuation<QuerySnapshot, Object>() {
            @Override
            public Object then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if(task.getResult().isEmpty()){
                    alertView.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
                return null;
            }
        });
    }
    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.myBagRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setHasFixedSize(true);
    }
}
