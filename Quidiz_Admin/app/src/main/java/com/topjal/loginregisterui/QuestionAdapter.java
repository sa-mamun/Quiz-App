package com.topjal.loginregisterui;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<QuestionModel> arrayList;
    DatabaseReference delRef;
    private Dialog questionDialog;
    private RecyclerItemListener recyclerItemListener;
    private String catName;

    public QuestionAdapter(Context context, ArrayList<QuestionModel> arrayList, Dialog questionDialog, RecyclerItemListener recyclerItemListener, String catName) {

        this.context = context;
        this.arrayList = arrayList;
        this.questionDialog = questionDialog;
        this.recyclerItemListener = recyclerItemListener;
        this.catName = catName;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.question_item_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.setData(arrayList.get(position).getQuestion(), arrayList.get(position).getQuestionA(), arrayList.get(position).getQuestionB(), arrayList.get(position).getQuestionC(), arrayList.get(position).getQuestionD(), arrayList.get(position).getCorrectANS(), arrayList.get(position).getSetNo(), position);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{


        TextView tv_qstn, tv_opA, tv_opB, tv_opC, tv_opD, tv_ca, tv_set;
        ImageButton ib_edit, ib_delete, ib_close;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_qstn = itemView.findViewById(R.id.tv_qstn);
            tv_opA = itemView.findViewById(R.id.tv_opA);
            tv_opB = itemView.findViewById(R.id.tv_opB);
            tv_opC = itemView.findViewById(R.id.tv_opC);
            tv_opD = itemView.findViewById(R.id.tv_opD);
            tv_ca = itemView.findViewById(R.id.tv_ca);
            tv_set = itemView.findViewById(R.id.tv_set);
            ib_edit = itemView.findViewById(R.id.ib_editBtn);
            ib_delete = itemView.findViewById(R.id.ib_deleteBtn);

            ib_close = questionDialog.findViewById(R.id.ib_close);

            ib_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    questionDialog.show();
                    recyclerItemListener.recyclerClickListener(v, getAdapterPosition());

                }
            });

            ib_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    questionDialog.dismiss();
                }
            });

            ib_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delRef = FirebaseDatabase.getInstance().getReference("SETS");
                    delRef.child(catName).child("questions").orderByChild("question").equalTo(arrayList.get(getAdapterPosition()).getQuestion()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                snapshot.getRef().removeValue();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });

        }

        public void setData(String qstn, String opA, String opB, String opC, String opD, String ca, int set, final int position){

            tv_qstn.setText(qstn);
            tv_opA.setText(opA);
            tv_opB.setText(opB);
            tv_opC.setText(opC);
            tv_opD.setText(opD);
            tv_ca.setText(ca);
            tv_set.setText(String.valueOf(set));
            Log.e("positon of adapter", String.valueOf(position) );

        }
    }

    public interface RecyclerItemListener
    {
        public void recyclerClickListener(View v, int position);
    }

}
