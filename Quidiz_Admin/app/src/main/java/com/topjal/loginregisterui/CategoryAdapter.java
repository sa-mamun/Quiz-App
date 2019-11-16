package com.topjal.loginregisterui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    ArrayList<CategoryModel> categoryList;
    Context context;
    Dialog categoryDialog;
    RecyclerItemClickListener recyclerItemClickListener;
    DatabaseReference delCatRef;
    DatabaseReference delQstnRef;

    public CategoryAdapter(ArrayList<CategoryModel> categoryList, Context context, Dialog categoryDialog, RecyclerItemClickListener recyclerItemClickListener) {
        this.categoryList = categoryList;
        this.context = context;
        this.categoryDialog = categoryDialog;
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.category_item_row, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(categoryList.get(position).getUrl(), categoryList.get(position).getName(), categoryList.get(position).getSets(), position);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        CircleImageView iv_image;
        TextView categoryTitle, setNo;
        ImageButton ib_edit, ib_delete, ib_close;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_image = itemView.findViewById(R.id.iv_image);
            categoryTitle = itemView.findViewById(R.id.tv_catName);
            setNo = itemView.findViewById(R.id.tv_setNo);
            ib_edit = itemView.findViewById(R.id.ib_edit);
            ib_delete = itemView.findViewById(R.id.ib_delete);

            ib_close = categoryDialog.findViewById(R.id.ib_close);

            ib_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    categoryDialog.show();
                    recyclerItemClickListener.recyclerListClickListener(v, getAdapterPosition());

                }
            });

            //Listener for closing the dialog
            ib_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    categoryDialog.dismiss();

                }
            });

            ib_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    delCatRef = FirebaseDatabase.getInstance().getReference("Categories");
                    delQstnRef = FirebaseDatabase.getInstance().getReference("SETS");
                    delCatRef.orderByChild("name").equalTo(categoryList.get(getAdapterPosition()).getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                snapshot.getRef().removeValue();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(v.getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    delQstnRef.child(categoryList.get(getAdapterPosition()).getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().removeValue();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });


        }

        public void setData(final String url, final String title, final int sets, int position)
        {
            Glide.with(context).load(url).into(iv_image);
            categoryTitle.setText(title);
            setNo.setText(String.valueOf(sets));

        }

    }

    public interface RecyclerItemClickListener
    {
        public void recyclerListClickListener(View v, int position);
    }


}
