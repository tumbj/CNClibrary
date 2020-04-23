package com.example.cnclibrary.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cnclibrary.R;
import com.example.cnclibrary.data.model.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    ArrayList<Book> books;
    FirebaseFirestore db;
    BookAdapter adapter ;
    RecyclerView recyclerView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });
        books = new ArrayList<>();
        setUpRecyclerView(root);
        setUpFirebase();
        loadDataFromFirebase();

        return root;
    }

    private void loadDataFromFirebase() {
        db.collection("books").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Log.i("tum"," work");
                    for(QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())){
                        Log.d("tum", doc.getId() + " => " + doc.getData());
                        Book book = new Book(doc.getString("name"),doc.getString("barcode"),
                                doc.getString("detail"),doc.getString("category"),
                                Integer.parseInt(doc.get("count").toString()),doc.getString("img"));
                        books.add(book);
                    }
                    adapter = new BookAdapter(books);
                    recyclerView.setAdapter(adapter);
                }else{
                    Log.i("tum","not work");
                }
            }
        });

    }

    private void setUpRecyclerView(View root) {
        recyclerView = root.findViewById(R.id.bookRecyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        recyclerView.setHasFixedSize(true);
    }

    private void setUpFirebase() {
        db = FirebaseFirestore.getInstance();
    }





}