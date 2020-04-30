package com.example.cnclibrary.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cnclibrary.R;
import com.example.cnclibrary.admin.data.model.ListBorrowed;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ListBorrowedActivity extends AppCompatActivity {
    FirebaseFirestore db;
    ProgressBar progressBar;
    TextView alertView;
    ImageView outOfBook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_borrowed);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final ArrayList<ListBorrowed> items = new ArrayList<>();
        final ListBorrowedAdapter adapter = new ListBorrowedAdapter(items);
        db = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.list_borrowed_progress_bar);
        outOfBook = findViewById(R.id.list_borrow_out_of_book_view);
        alertView = findViewById(R.id.list_borrow_alert_view);

        outOfBook.setVisibility(View.GONE);
        alertView.setVisibility(View.GONE);
        alertView.setText("Not have book in bag!!!");

        progressBar.setVisibility(View.VISIBLE);
        db.collection("books").whereEqualTo("isFree",false).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("tum", document.getId() + " => " + document.getData());
                        String key = document.getId();
                        //// get book was borrowed from DB
                        final ListBorrowed listBorrowed = new ListBorrowed();
                        listBorrowed.setImgEncoded((String) document.get("img"));
                        listBorrowed.setBookName((String) document.get("name"));
                        db.collection("books").document(key).collection("histories").whereEqualTo("end_date",null).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(QueryDocumentSnapshot document : task.getResult()){
                                        Log.d("tum", document.getId() + " => " + document.getData());
                                        String userid = (String) document.get("user_id");
                                        db.collection("users")
                                                .document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        String email = (String) task.getResult().get("email");
                                                        listBorrowed.setUserid(email);
                                                        listBorrowed.setStart_date((String) document.get("start_date"));
                                                        items.add(listBorrowed);
                                                        adapter.notifyDataSetChanged();
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                });
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                outOfBook.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        });
                    }
                }else {
                    Log.d("db", "Error getting documents: ", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                outOfBook.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }).continueWith(new Continuation<QuerySnapshot, Object>() {
            @Override
            public Object then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if(task.getResult().isEmpty()){
                    alertView.setVisibility(View.VISIBLE);
                    outOfBook.setVisibility(View.VISIBLE);
                }
//                progressBar.setVisibility(View.GONE);
                return null;
            }
        });


        recyclerView.setAdapter(adapter);
    }
}
