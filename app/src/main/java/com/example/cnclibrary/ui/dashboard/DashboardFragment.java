package com.example.cnclibrary.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.cnclibrary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.cnclibrary.MainActivity.ROLE;


public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Button addBtn ;
    Button listBtn ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        addBtn = root.findViewById(R.id.addBookBtn);
        listBtn = root.findViewById(R.id.listBorrowedBtn);
        addBtn.setVisibility(View.VISIBLE);
        listBtn.setVisibility(View.VISIBLE);
        if(ROLE.equals("user")){
             addBtn.setVisibility(View.INVISIBLE);
             listBtn.setVisibility(View.INVISIBLE);
        }

        return root;
    }
}