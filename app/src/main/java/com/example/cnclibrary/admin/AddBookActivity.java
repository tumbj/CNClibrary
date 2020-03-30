package com.example.cnclibrary.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.cnclibrary.R;
import com.example.cnclibrary.data.model.Book;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.io.IOException;


public class AddBookActivity extends AppCompatActivity {
    FirebaseFirestore db;



        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);


        // category spinner start
        Spinner dropdown = findViewById(R.id.categorySpinner);
        String[] items = new String[]{"Computer","Cartoon book","Science","Science","Novel","etc"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        // category spinner end

        db = FirebaseFirestore.getInstance();
    }

    public void clk(View view){
        EditText nameEditText = (EditText)findViewById(R.id.nameEditText);
        EditText detailEditText  = (EditText)findViewById(R.id.detailEditText);
        Spinner category = findViewById(R.id.categorySpinner);
//        ImageView img = findViewById(R.id.imageView);

        String name = String.valueOf(nameEditText.getText());
        String barcode = "barcode";
        String detail = String.valueOf(detailEditText.getText());
        String categorySelected = category.getSelectedItem().toString();
        int countBook = 1;

        String input = name+" "+detail+categorySelected;
        Book newBook = new Book(name,barcode,detail,categorySelected,countBook);
        db.collection("books")
                .add(newBook)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d("add", "DocumentSnapshot added with ID: " + documentReference.getId());
                }
                })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("add", "Error adding document", e);
                        }
                    });
        Log.i("vac",input);



//        Intent intent = new Intent(this,AddBookActivity.class);
//        Log.i("vsc",wc.countWord()+"");
//        intent.putExtra("msg",wc.countWord()+"");
//        startActivity(intent);
    }


    //// upload img zone start
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Uri uri = data.getData();
//        ImageView imageView = findViewById(R.id.imageView);
//        imageView.setImageURI(uri);
//    }
//    public void uploadClk(View view){
//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(AddBookActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},200);
//            return;
//        }
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent,0);
//    }
    //// end zone upload img
}
