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
import com.example.cnclibrary.data.model.UserBookHistory;
import com.example.cnclibrary.ui.dashboard.MyBagActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;

import java.util.Arrays;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;
import static android.app.PendingIntent.getActivity;


public class BorrowActivity extends Activity implements ZXingScannerView.ResultHandler {
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){
                Toast.makeText(BorrowActivity.this,"Permission is granted",Toast.LENGTH_LONG).show();
            }
            else{
                requestPermission();
            }
        }
    }



    public boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(BorrowActivity.this, CAMERA)== PackageManager.PERMISSION_GRANTED);
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
        new AlertDialog.Builder(BorrowActivity.this)
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String uid = mAuth.getUid();
                // add history to book
                UserBookHistory userBookHistory = new UserBookHistory(book.getBarcode());
                assert uid != null;
                db.collection("users").document(uid).collection("bags")
                        .add(userBookHistory).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

                BookHistory bookHistory = new BookHistory(mAuth.getUid());
                db.collection("books").document(book.getBarcode()).collection("histories")
                        .add(bookHistory).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
                db.collection("books").document(book.getBarcode()).update("isFree",false).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("db", Arrays.toString(e.getStackTrace()));

                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(BorrowActivity.this,"Borrow success",Toast.LENGTH_LONG).show();
                        mScannerView.resumeCameraPreview(BorrowActivity.this);
                        Intent intent = new Intent(BorrowActivity.this, MyBagActivity.class);
                        startActivity(intent);
                    }
                });
            }

        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i("vac","cancel was click");
                // If you would like to resume scanning, call this method below:
                mScannerView.resumeCameraPreview(BorrowActivity.this);
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setTitle("You want to borrow this book ?");

        String barcode = rawResult.getText();
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
                            if(isFree){ // check this book have free ?
                                book.setName(bookName);
                                book.setBarcode(barcode);
                                Log.i("barcode",barcode);
                                dialog.setMessage("The book name: "+book.getName());
                                dialog.show();
                            }else {
                                Toast.makeText(BorrowActivity.this,bookName+" has borrowed.",Toast.LENGTH_LONG).show();
                                mScannerView.resumeCameraPreview(BorrowActivity.this);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"Can't find this book in DB",Toast.LENGTH_LONG).show();
                            Log.d("vac", "No such document");
                            mScannerView.resumeCameraPreview(BorrowActivity.this);
                        }
                    } else {
                        Log.d("vac", "get failed with ", task.getException());
                    }
                }
            });

    }

}