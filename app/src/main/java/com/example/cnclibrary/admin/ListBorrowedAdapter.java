package com.example.cnclibrary.admin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cnclibrary.R;
import com.example.cnclibrary.admin.data.model.ListBorrowed;

import java.util.ArrayList;

public class ListBorrowedAdapter  extends RecyclerView.Adapter<ListBorrowedAdapter.Holder>{
    private ArrayList<ListBorrowed> mDataSet;
    private ItemClickListener mListener;



    public ListBorrowedAdapter(ArrayList<ListBorrowed> mDataSet) {
        this.mDataSet = mDataSet;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item,parent,false);
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
        TextView textStartDate;
        TextView textUserid;
        ImageView imageView;


        public Holder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textTitle);
            imageView = itemView.findViewById(R.id.imageView);
            textStartDate = itemView.findViewById(R.id.textStartDate);
            textUserid = itemView.findViewById(R.id.textUserid);
            itemView.setOnClickListener(this);
        }
        public Bitmap decodeBase64(String encodeImg){
            byte[] imageBytes = Base64.decode(encodeImg, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//        imageview2.setImageBitmap(decodedImage);
        }

        public void setItem(int position){
            textName.setText("name : "+ mDataSet.get(position).getBookName());
            textUserid.setText("user : "+mDataSet.get(position).getUserid());
            textStartDate.setText("start date : "+mDataSet.get(position).getStart_date());
            imageView.setImageBitmap(decodeBase64(mDataSet.get(position).getImgEncoded()));
        }

        @Override
        public void onClick(View view) {
            if(mListener!=null){
                mListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
