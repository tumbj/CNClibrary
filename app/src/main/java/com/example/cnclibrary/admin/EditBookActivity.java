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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.cnclibrary.MainActivity;
import com.example.cnclibrary.R;
import com.example.cnclibrary.data.model.Book;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;

public class EditBookActivity extends AppCompatActivity {

    private Spinner category;
    private ImageView imageView;
    private EditText nameEditText;
    private EditText detailEditText;
    FirebaseFirestore db;
    String barcode ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        category = findViewById(R.id.categorySpinner2);
        nameEditText = findViewById(R.id.nameEditText2);
        detailEditText = findViewById(R.id.detailEditText2);
        imageView = findViewById(R.id.bookImageView);
        String[] items = new String[]{"Computer","Cartoon book","Science","English","Novel","etc"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        category.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        byte[] imgBitmap = intent.getByteArrayExtra("image");
        String category = intent.getStringExtra("category");
        String detail = intent.getStringExtra("detail");
        barcode = intent.getStringExtra("barcode");
        int position = -1;
        for (int i = 0; i < items.length; i++) {
            if(items[i].equals(category)){
                position = i;
                break;
            }
        }
        Bitmap bmp = BitmapFactory.decodeByteArray(imgBitmap, 0, imgBitmap.length);
        imageView.setImageBitmap(bmp);
        this.category.setSelection(position);
        nameEditText.setText(name);
        detailEditText.setText(detail);

    }
    /// upload zone
    public void uploadClk(View view){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(EditBookActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},200);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            Uri uri = data.getData();
            ImageView imageView = findViewById(R.id.bookImageView);
            imageView.setImageURI(uri);
        }
    }
    /// end
    public String encodeBase64(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
        resized.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
    public void clk(View view){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        String imageString = encodeBase64(bitmap);
        String name = String.valueOf(nameEditText.getText());
        String detail = String.valueOf(detailEditText.getText());
        String categorySelected = category.getSelectedItem().toString();
//        int countBook = Integer.parseInt(String.valueOf(countEditText.getText()));

        Book newBook = new Book(name,barcode,detail,categorySelected,1,imageString);

        // add to db
        db.collection("books")
                .document(barcode)
                .set(newBook)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        Log.d("add", "DocumentSnapshot added with ID: " + barcode);
                        Intent intent = new Intent(EditBookActivity.this, MainActivity.class);
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
}
