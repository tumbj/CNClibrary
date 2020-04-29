package com.example.cnclibrary.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.cnclibrary.MainActivity;
import com.example.cnclibrary.R;
import com.example.cnclibrary.data.model.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;


public class ShowAdminDialogFragment extends AppCompatDialogFragment {
    private ImageView imageViewDialog;
    private TextView textTitle;
    private TextView textCategory;
    private TextView textDetail;
    private Button editBtn;
    private Button deleteBtn;
    private Book book;

    public ShowAdminDialogFragment(Book book) {
        this.book = book;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.show_admin_item_dialog, null);
//        db = new DatabaseCRUD(getContext());
        Log.i("tum","book information  "+book.getName());
        builder.setView(view);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        editBtn = view.findViewById(R.id.editBookBtn);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowAdminDialogFragment.this.getDialog().cancel();
            }
        });

        textTitle = view.findViewById(R.id.textTitle);
        textCategory = view.findViewById(R.id.textCategory);
        textDetail = view.findViewById(R.id.textDetail);
        imageViewDialog = view.findViewById(R.id.imageViewDialog);

        String name = book.getName();
        String category = book.getCategory();
        String detail = book.getDetail();

        textTitle.setText(name);
        textCategory.setText("category : "+category);
        textDetail.setText("detail " +detail);
        Bitmap decodedImg = decodeBase64(book.getImg());
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        decodedImg.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteImage=stream.toByteArray();
        String barcode = book.getBarcode();
        imageViewDialog.setImageBitmap(decodedImg);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditBookActivity.class);
                intent.putExtra("image", byteImage);
                intent.putExtra("name", name);
                intent.putExtra("detail", detail);
                intent.putExtra("category", category);
                intent.putExtra("barcode",barcode);
                startActivity(intent);
            }
        });
        deleteBtn = view.findViewById(R.id.deleteBookBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConfirmDialog(barcode);
            }
        });
        return builder.create();
    }
    public Bitmap decodeBase64(String encodeImg){
        byte[] imageBytes = Base64.decode(encodeImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
    public void openConfirmDialog(String barcode){
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment(barcode);
        confirmDialogFragment.show(getActivity().getSupportFragmentManager(),"DELETE");

    }
}

