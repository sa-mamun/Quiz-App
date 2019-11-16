package com.topjal.loginregisterui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class QuestionActivity extends AppCompatActivity {

    Spinner catSpinner, setSpinner;
    EditText questionET, optAET, optBET, optCET, optDET, correctAnsET;
    Button addQstnBtn;
    DatabaseReference catRef;
    DatabaseReference qstnRef;
    CategoryModel categoryModel;
    ArrayList<CategoryModel> categoryModelArrayList;
    ArrayList<String> catNameList;
    ArrayList<Integer> setNoList;
    ArrayAdapter<String> catArrayAdapter;
    ArrayAdapter<Integer> setArrayAdapter;
    String setNo;
    String catName;
    QuestionModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        catSpinner = findViewById(R.id.catSpinner);
        setSpinner = findViewById(R.id.setSpinner);
        questionET = findViewById(R.id.questionET);
        optAET = findViewById(R.id.optionA_ET);
        optBET = findViewById(R.id.optionB_ET);
        optCET = findViewById(R.id.optionC_ET);
        optDET = findViewById(R.id.optionD_ET);
        correctAnsET = findViewById(R.id.correctAns_ET);
        addQstnBtn = findViewById(R.id.addQuestionBtn);

        Toolbar questionToolbar = findViewById(R.id.questionToolbar);
        setSupportActionBar(questionToolbar);
        getSupportActionBar().setTitle("Add Question");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categoryModelArrayList = new ArrayList<>();
        catNameList = new ArrayList<>();
        setNoList = new ArrayList<>();

        catRef = FirebaseDatabase.getInstance().getReference("Categories");

        getData();

        //Category Adapter
        catArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, catNameList);
        catSpinner.setAdapter(catArrayAdapter);

        //Set No Adapter
        setArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, setNoList);
        setSpinner.setAdapter(setArrayAdapter);

        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                catName = parent.getSelectedItem().toString();

                setNoList.clear();
                for (int i=1; i<=categoryModelArrayList.get(position).getSets(); i++)
                {
                    setNoList.add(i);
                }
                setArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setNo = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addQstnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String question = questionET.getText().toString();
                String optA = optAET.getText().toString();
                String optB = optBET.getText().toString();
                String optC = optCET.getText().toString();
                String optD = optDET.getText().toString();
                String correctAns = correctAnsET.getText().toString();

                if (TextUtils.isEmpty(question) || TextUtils.isEmpty(optA) || TextUtils.isEmpty(optB) || TextUtils.isEmpty(optC) || TextUtils.isEmpty(optD) || TextUtils.isEmpty(correctAns) || TextUtils.isEmpty(catName) || TextUtils.isEmpty(setNo))
                {
                    Toast.makeText(QuestionActivity.this, "Please Fill Up All The Fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    model = new QuestionModel(question, optA, optB, optC, optD, correctAns, Integer.valueOf(setNo));
                    qstnRef = FirebaseDatabase.getInstance().getReference("SETS");
                    String key = qstnRef.push().getKey();
                    qstnRef.child(catName).child("questions").child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(QuestionActivity.this, "Question Added Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });

    }

    public void getData()
    {
        catRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e("Add Value Listener", "Entered Databse and retreving" );
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    categoryModel = snapshot.getValue(CategoryModel.class);
                    categoryModelArrayList.add(categoryModel);
                    Log.e("list size", String.valueOf(categoryModelArrayList.size()) );

                }
                //Adding data to list
                for (int i=0; i<categoryModelArrayList.size(); i++)
                {
                    Log.e("Inside Category Name", "1" );
                    catNameList.add(categoryModelArrayList.get(i).getName());
                }
                catArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuestionActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
