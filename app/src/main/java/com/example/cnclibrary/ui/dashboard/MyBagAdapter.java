package com.example.cnclibrary.ui.dashboard;

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
import com.example.cnclibrary.data.model.BookInBag;
import com.example.cnclibrary.data.model.UserBookHistory;
import com.example.cnclibrary.ui.home.ShowDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.cnclibrary.MainActivity.ROLE;

public class MyBagAdapter extends RecyclerView.Adapter<MyBagAdapter.Holder>{
    private ArrayList<BookInBag> mDataSet;
    private ItemClickListener mListener;
    private View view;
    FirebaseFirestore db;

    public MyBagAdapter(ArrayList<BookInBag> mDataSet) {
        this.mDataSet = mDataSet;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item_my_bag,parent,false);
        Holder holder = new Holder(view);
        db = FirebaseFirestore.getInstance();
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
        TextView startDateView;

        public Holder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.book_title);
            imageView = itemView.findViewById(R.id.book_image);
            startDateView = itemView.findViewById(R.id.start_date_view);
            itemView.setOnClickListener(this);
        }
        public Bitmap decodeBase64(String encodeImg){
            byte[] imageBytes = Base64.decode(encodeImg, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }

        public void setItem(final int position){
            Log.i("tum","start_date : "+mDataSet.get(position).getStart_date());
            textName.setText(mDataSet.get(position).getName()+"");
            startDateView.setText("start date: "+mDataSet.get(position).getStart_date()+"");
            imageView.setImageBitmap(decodeBase64(mDataSet.get(position).getImg()));
        }

        @Override
        public void onClick(View view) {
            if(mListener!=null){
                mListener.onItemClick(getAdapterPosition());
            }
        }

    }


}

