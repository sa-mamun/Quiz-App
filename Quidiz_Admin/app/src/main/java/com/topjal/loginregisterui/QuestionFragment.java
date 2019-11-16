package com.topjal.loginregisterui;


import android.app.Dialog;
import android.net.LinkAddress;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionFragment extends Fragment implements QuestionAdapter.RecyclerItemListener{

    RecyclerView rv_question;
    Spinner spinner_cat, spinner_set;
    Button bt_show;
    QuestionAdapter questionAdapter;
    ArrayList<QuestionModel> qstnArrayList;
    ArrayList<String> catList;
    ArrayList<Integer> setList;
    ArrayList<CategoryModel> catArrayList;
    DatabaseReference catRef;
    DatabaseReference qstnRef;
    DatabaseReference editQstnRef;
    QuestionModel questionModel;
    CategoryModel categoryModel;
    String catName;
    int setNO, position;
    ArrayAdapter<String> catAdapter;
    ArrayAdapter<Integer> setAdapter;
    Dialog questionDialog;

    EditText edit_qstn, edit_opA, edit_opB, edit_opC, edit_opD, edit_ca, edit_setNo;
    Button edit_addQstnBtn;

    public QuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_question, container, false);

        rv_question = v.findViewById(R.id.rv_question);
        spinner_cat = v.findViewById(R.id.spinner_cat);
        spinner_set = v.findViewById(R.id.spinner_set);
        bt_show = v.findViewById(R.id.bt_show);

        //For Custom Dialog
        questionDialog = new Dialog(getContext());
        questionDialog.setContentView(R.layout.edit_question_dailog);
        questionDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        questionDialog.setCancelable(false);

        //Initializing dialog element
        edit_qstn = questionDialog.findViewById(R.id.edit_questionET);
        edit_opA = questionDialog.findViewById(R.id.edit_optionA_ET);
        edit_opB = questionDialog.findViewById(R.id.edit_optionB_ET);
        edit_opC = questionDialog.findViewById(R.id.edit_optionC_ET);
        edit_opD = questionDialog.findViewById(R.id.edit_optionD_ET);
        edit_ca = questionDialog.findViewById(R.id.edit_correctAns_ET);
        edit_setNo = questionDialog.findViewById(R.id.edit_setNo_ET);
        edit_addQstnBtn = questionDialog.findViewById(R.id.edit_QuestionBtn);

        catArrayList = new ArrayList<>();
        qstnArrayList = new ArrayList<>();
        catList = new ArrayList<>();
        setList = new ArrayList<>();

        getCategoryData();

        //Category Spinner Adapter
        catAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, catList);
        spinner_cat.setAdapter(catAdapter);

        //Set Spinner Adapter
        setAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, setList);
        spinner_set.setAdapter(setAdapter);


        //Fetching cat name and setting set no to spinner
        spinner_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                catName = parent.getSelectedItem().toString();

                setList.clear();
                for (int i=1; i<=catArrayList.get(position).getSets(); i++)
                {
                    setList.add(i);
                }
                setAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Fetching set no from spinner
        spinner_set.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                setNO = Integer.valueOf(parent.getSelectedItem().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bt_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getQuestionData();

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                rv_question.setLayoutManager(layoutManager);

                questionAdapter = new QuestionAdapter(getContext(), qstnArrayList, questionDialog, QuestionFragment.this, catName);
                rv_question.setAdapter(questionAdapter);

            }
        });

        edit_addQstnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String question = edit_qstn.getText().toString();
                String optA = edit_opA.getText().toString();
                String optB = edit_opB.getText().toString();
                String optC = edit_opC.getText().toString();
                String optD = edit_opD.getText().toString();
                String correctAns = edit_ca.getText().toString();
                String ed_setNo = edit_setNo.getText().toString();

                if (TextUtils.isEmpty(question) || TextUtils.isEmpty(optA) || TextUtils.isEmpty(optB) || TextUtils.isEmpty(optC) || TextUtils.isEmpty(optD) || TextUtils.isEmpty(correctAns) || TextUtils.isEmpty(catName) || TextUtils.isEmpty(ed_setNo))
                {
                    Toast.makeText(getContext(), "Please Fill Up All The Fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    questionModel = new QuestionModel(question, optA, optB, optC, optD, correctAns, Integer.valueOf(ed_setNo));
                    editQstnRef = FirebaseDatabase.getInstance().getReference("SETS");
                    editQstnRef.child(catName).child("questions").orderByChild("question").equalTo(qstnArrayList.get(position).getQuestion()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                snapshot.getRef().setValue(questionModel);
                            }
                            Toast.makeText(getContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }

            }
        });


        return v;
    }

    public void getQuestionData()
    {
        qstnRef = FirebaseDatabase.getInstance().getReference("SETS");
        qstnRef.child(catName).child("questions").orderByChild("setNo").equalTo(setNO).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                qstnArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    questionModel = snapshot.getValue(QuestionModel.class);
                    qstnArrayList.add(questionModel);
                }
                questionAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Method for getting Category data from firebase
    public void getCategoryData()
    {

        catRef = FirebaseDatabase.getInstance().getReference("Categories");
        catRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    categoryModel = snapshot.getValue(CategoryModel.class);
                    catArrayList.add(categoryModel);
                }

                for (int i=0; i<catArrayList.size(); i++)
                {
                    catList.add(catArrayList.get(i).getName());
                }
                catAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void recyclerClickListener(View v, int position) {

        this.position = position;
        edit_qstn.setText(qstnArrayList.get(position).getQuestion());
        edit_opA.setText(qstnArrayList.get(position).getQuestionA());
        edit_opB.setText(qstnArrayList.get(position).getQuestionB());
        edit_opC.setText(qstnArrayList.get(position).getQuestionC());
        edit_opD.setText(qstnArrayList.get(position).getQuestionD());
        edit_ca.setText(qstnArrayList.get(position).getCorrectANS());
        edit_setNo.setText(String.valueOf(qstnArrayList.get(position).getSetNo()));
    }
}
