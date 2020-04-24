package com.example.cnclibrary.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.cnclibrary.R;
import com.example.cnclibrary.data.model.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ShowDialogFragment extends AppCompatDialogFragment {
    private ImageView imageViewDialog;
    private TextView textTitle;
    private TextView textCategory;
    private TextView textDetail;
    private Book book;

    public ShowDialogFragment(Book book) {
        this.book = book;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.show_item_dialog, null);
//        db = new DatabaseCRUD(getContext());
        Log.i("tum","book information  "+book.getName());
        builder.setView(view);


        FloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialogFragment.this.getDialog().cancel();
            }
        });
//                .setTitle("Edit item")
//                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        String name = nameEditText.getText().toString();
//                        double price = Double.parseDouble(priceEditText.getText().toString());
//                        int count = Integer.parseInt(countEditText.getText().toString());
////                        Log.d("test","name: "+name+", price: "+price+", count: "+count+", key: "+itemWithKey.getKey());
////                        db.updateItem(itemWithKey.getKey(),new Item(name, count, price));
//
//                    }
//                });
//                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        EditDialogFragment.this.getDialog().cancel();
//                    }
//                }).setNeutralButton("DELETE", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Log.d("delete","delete was clicked");
//                        db.deleteItem(itemWithKey.getKey());
//                    }
//                });


        textTitle = view.findViewById(R.id.textTitle);
        textCategory = view.findViewById(R.id.textCategory);
        textDetail = view.findViewById(R.id.textDetail);
        imageViewDialog = view.findViewById(R.id.imageViewDialog);

        textTitle.setText(book.getName());
        textCategory.setText("category : "+book.getCategory());
        textDetail.setText("detail " +book.getDetail());
        imageViewDialog.setImageBitmap(decodeBase64(book.getImg()));

        return builder.create();
    }
    public Bitmap decodeBase64(String encodeImg){
        byte[] imageBytes = Base64.decode(encodeImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}
