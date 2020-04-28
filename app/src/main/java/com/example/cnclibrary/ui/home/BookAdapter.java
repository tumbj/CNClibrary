package com.example.cnclibrary.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cnclibrary.R;
import com.example.cnclibrary.admin.ShowAdminDialogFragment;
import com.example.cnclibrary.data.model.Book;

import java.util.ArrayList;

import static com.example.cnclibrary.MainActivity.ROLE;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.Holder>{
    private ArrayList<Book> mDataSet;
    private ItemClickListener mListener;
    private View view;


    public BookAdapter(ArrayList<Book> mDataSet) {
        this.mDataSet = mDataSet;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item_book,parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position)
    {
        holder.setItem(position);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public interface ItemClickListener{
        void onItemClick(int position);
    }
    public void setItemClickListener(ItemClickListener listener){
        mListener = listener;
    }



    class Holder extends RecyclerView.ViewHolder implements  View.OnClickListener {

        TextView textName;
        ImageView imageView;
        CardView cardView;

        public Holder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.book_title);
            imageView = itemView.findViewById(R.id.book_image);
            cardView = itemView.findViewById(R.id.cardView);
            itemView.setOnClickListener(this);
        }
        public Bitmap decodeBase64(String encodeImg){
            byte[] imageBytes = Base64.decode(encodeImg, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//        imageview2.setImageBitmap(decodedImage);
        }

        public void setItem(final int position){
            textName.setText(mDataSet.get(position).getName()+"");
            imageView.setImageBitmap(decodeBase64(mDataSet.get(position).getImg()));
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("role","role is"+ROLE);
                    if(ROLE.equals("user")){
                        showDialog(mDataSet.get(position));
                    }else{
                        showAdminDialog(mDataSet.get(position));
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            if(mListener!=null){
                mListener.onItemClick(getAdapterPosition());
            }
        }
    }
    public void showDialog(Book book){
        ShowDialogFragment showDialogFragment = new ShowDialogFragment(book);
        showDialogFragment.show(((AppCompatActivity) view.getContext()).getSupportFragmentManager(),"Show");
    }
    public void showAdminDialog(Book book){
        ShowAdminDialogFragment showAdminDialogFragment = new ShowAdminDialogFragment(book);
        showAdminDialogFragment.show(((AppCompatActivity) view.getContext()).getSupportFragmentManager(),"Show");
    }

}

