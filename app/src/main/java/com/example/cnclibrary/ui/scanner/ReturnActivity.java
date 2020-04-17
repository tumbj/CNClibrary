package com.example.cnclibrary.ui.scanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cnclibrary.MainActivity;
import com.example.cnclibrary.data.model.Book;
import com.example.cnclibrary.data.model.BookHistory;
import com.example.cnclibrary.data.model.History;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;


public class ReturnActivity extends Activity implements ZXingScannerView.ResultHandler {
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;
    FirebaseFirestore db;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
        db = FirebaseFirestore.getInstance();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){
                Toast.makeText(ReturnActivity.this,"Permission is granted",Toast.LENGTH_LONG).show();
            }
            else{
                requestPermission();
            }
        }
    }



    public boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(ReturnActivity.this, CAMERA)== PackageManager.PERMISSION_GRANTED);
    }

    public void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{CAMERA},REQUEST_CAMERA);
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ReturnActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        final Book book = new Book();
        final String barcode = rawResult.getText();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i("vac","ok was click");
                History history = new History(book.getBarcode());
                // get userid from history in book ; update end_date
//                db.collection("bags").document("userid")
//                        .update("books", FieldValue.arrayUnion(history)).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("db", Arrays.toString(e.getStackTrace()));
//                    }
//                });
                final DocumentReference docRef = db.collection("books").document(barcode);
                final BookHistory bookHistory = new BookHistory();
                // get data
                docRef.collection("histories").whereEqualTo("end_date",null).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("tum", document.getId() + " => " + document.getData());
                                String start_date = (String) document.getData().get("start_date");
                                Log.i("tum","start_date = "+start_date);
                                String key = document.getId();
                                bookHistory.setStart_date(start_date);
                                bookHistory.setUser_id("userid");
                                bookHistory.setEnd_date(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                                Log.i("tum","end date in : "+bookHistory.getEnd_date());
                                // add history to book
                                db.collection("books").document(book.getBarcode()).collection("histories")
                                        .document(key).set(bookHistory).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        }else {
                            Log.d("db", "Error getting documents: ", task.getException());
                        }
                    }
                });
                Log.i("tum","end date : "+bookHistory.getEnd_date());



                db.collection("books").document(book.getBarcode()).update("isFree",true).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("db", Arrays.toString(e.getStackTrace()));

                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mScannerView.resumeCameraPreview(ReturnActivity.this);
                        Intent intent = new Intent(ReturnActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

            }

        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i("vac","cancel was click");
                // If you would like to resume scanning, call this method below:
                mScannerView.resumeCameraPreview(ReturnActivity.this);
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setTitle("You want to return this book ?");


        Log.v("scanner",barcode); // Prints scan results
        // get book from DB
        DocumentReference docRef = db.collection("books").document(barcode);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> obj = document.getData();
                        String barcode = String.valueOf(obj.get("barcode"));
                        String bookName = String.valueOf(obj.get("name"));
                        boolean isFree = (boolean) obj.get("isFree");
                        if(!isFree){ // check this book have free ?
                            book.setName(bookName);
                            book.setBarcode(barcode);
                            Log.i("barcode",barcode);
                            dialog.setMessage("The book name: "+book.getName());
                            dialog.show();
                        }else {
                            Toast.makeText(ReturnActivity.this,bookName+" has borrowed.",Toast.LENGTH_LONG).show();
                            mScannerView.resumeCameraPreview(ReturnActivity.this);
                        }
                    } else {
                        Log.d("vac", "No such document");
                    }
                } else {
                    Log.d("vac", "get failed with ", task.getException());
                }
            }
        });

    }
}


