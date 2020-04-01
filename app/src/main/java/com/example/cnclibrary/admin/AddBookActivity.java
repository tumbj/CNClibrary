package com.example.cnclibrary.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cnclibrary.MainActivity;
import com.example.cnclibrary.R;
import com.example.cnclibrary.data.model.Book;
import com.example.cnclibrary.ui.scanner.AddActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;


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
        Intent intent = getIntent();
        String barcodeData = intent.getStringExtra("barcodeData");
        TextView barcodeText = findViewById(R.id.barcodeText);
        barcodeText.setText(barcodeData);
    }

    public String encodeBase64(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
        resized.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        Log.i("base64",Base64.encodeToString(imageBytes, Base64.DEFAULT));
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
    public Bitmap decodeBase64(String encodeImg){
        byte[] imageBytes = Base64.decode(encodeImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//        imageview2.setImageBitmap(decodedImage);
    }

    public void clk(View view){
        EditText nameEditText = (EditText)findViewById(R.id.nameEditText);
        EditText detailEditText  = (EditText)findViewById(R.id.detailEditText);
        EditText countEditText = (EditText)findViewById(R.id.countEditText);
        TextView barcodeText = findViewById(R.id.barcodeText);
        Spinner category = findViewById(R.id.categorySpinner);
        ImageView imageView = findViewById(R.id.imageView);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        String imageString = encodeBase64(bitmap);
        String name = String.valueOf(nameEditText.getText());
        final String barcode = (String) barcodeText.getText();
        String detail = String.valueOf(detailEditText.getText());
        String categorySelected = category.getSelectedItem().toString();
        int countBook = Integer.parseInt(String.valueOf(countEditText.getText()));

        String input = name+" "+detail+categorySelected;
        Book newBook = new Book(name,barcode,detail,categorySelected,countBook,imageString);

        // add to db
        db.collection("books")
                .document(barcode)
                .set(newBook)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                    public void onSuccess(Void avoid) {
                        Log.d("add", "DocumentSnapshot added with ID: " + barcode);
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
        Log.i("vac",input);

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
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AddBookActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},200);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,0);
    }
}
