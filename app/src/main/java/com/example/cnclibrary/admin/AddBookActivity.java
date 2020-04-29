package com.example.cnclibrary.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cnclibrary.MainActivity;
import com.example.cnclibrary.R;
import com.example.cnclibrary.data.model.Book;
import com.example.cnclibrary.ui.scanner.AddActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;


public class AddBookActivity extends AppCompatActivity {
    FirebaseFirestore db;
    Button submitBtn;
    EditText nameEditText;
    EditText detailEditText;
    TextView barcodeText;
    ProgressBar loadingProgressBar;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);


        // category spinner start
        Spinner dropdown = findViewById(R.id.categorySpinner);
        String[] items = new String[]{"Computer","Cartoon book","Science","English","Novel","etc"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        // category spinner end

        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String barcodeData = intent.getStringExtra("barcodeData");
        TextView barcodeText = findViewById(R.id.barcodeText);
        barcodeText.setText(barcodeData);

        loadingProgressBar = findViewById(R.id.addBookLoading);
        submitBtn = findViewById(R.id.button);
        nameEditText = (EditText)findViewById(R.id.nameEditText);
        detailEditText  = (EditText)findViewById(R.id.detailEditText);
        imageView = findViewById(R.id.imageView);
//        EditText countEditText = (EditText)findViewById(R.id.countEditText);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.GONE);
                if(barcodeText.getText().length()==0){{
                    barcodeText.setError("Please scan barcode first");
                }}else if (nameEditText.getText().length()==0){
                    nameEditText.setError("Please fill name");
                }else {
                    clk(v);
                    loadingProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public String encodeBase64(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
        resized.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        Log.i("base64",Base64.encodeToString(imageBytes, Base64.DEFAULT));
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public void clk(View view){
        Spinner category = findViewById(R.id.categorySpinner);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        String imageString = encodeBase64(bitmap);
        String name = String.valueOf(nameEditText.getText());
        final String barcode = (String) barcodeText.getText();
        String detail = String.valueOf(detailEditText.getText());
        String categorySelected = category.getSelectedItem().toString();
            //        int countBook = Integer.parseInt(String.valueOf(countEditText.getText()));
        int countBook = 1;
        Book newBook = new Book(name, barcode, detail, categorySelected, countBook, imageString);
            // add to db
        db.collection("books")
                    .document(barcode)
                    .set(newBook)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Log.d("add", "DocumentSnapshot added with ID: " + barcode);
                            Toast.makeText(AddBookActivity.this,"Add book success",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AddBookActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("add", "Error adding document", e);
                        }
                    });
    }

    public void scanClk(android.view.View view){
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            Uri uri = data.getData();
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageURI(uri);
        }
    }
    public void uploadClk(View view){
        Log.i("upload","upload was clicked");
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AddBookActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},200);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,0);
    }

    @Override
    public void onBackPressed() {
        TextView barcodeView = findViewById(R.id.barcodeText);
        if(nameEditText.getText().length()==0 && barcodeView.getText().length()==0){
            super.onBackPressed();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddBookActivity.this);
            builder.setTitle("Confirmation");
            builder.setMessage("Are you sure want to quit?");
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("no",null);
            builder.show();
        }
    }
}
