package com.topjal.loginregisterui;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnSuccessListener;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment implements CategoryAdapter.RecyclerItemClickListener {

    RecyclerView rv_category;
    CategoryAdapter categoryAdapter;
    ArrayList<CategoryModel> arrayList;
    DatabaseReference catRef;
    CategoryModel model;
    Dialog categoryDialog;
    CircleImageView edit_imaveView;
    Button edit_btn, edit_catUpdateBtn;
    EditText edit_catName, edit_setNo;

    Uri imageUri;
    StorageReference folderRef;
    StorageReference imageName;
    DatabaseReference databaseReference;
    String imageUrl, catName, noOfSets;
    int position;

    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_category, container, false);

        rv_category = v.findViewById(R.id.rv_category);

        //Category Dialog
        categoryDialog = new Dialog(getContext());
        categoryDialog.setContentView(R.layout.edit_category_dailouge);
        categoryDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        categoryDialog.setCancelable(false);

        edit_btn = categoryDialog.findViewById(R.id.edit_addImageBtn);
        edit_catName = categoryDialog.findViewById(R.id.edit_catNameET);
        edit_setNo = categoryDialog.findViewById(R.id.edit_seNoET);
        edit_catUpdateBtn = categoryDialog.findViewById(R.id.edit_catUpdateBtn);
        edit_imaveView = categoryDialog.findViewById(R.id.edit_imageView);

        //Creating folder under Storage (Categories)
        folderRef = FirebaseStorage.getInstance().getReference().child("Categories");

        arrayList = new ArrayList<>();

        getData();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rv_category.setLayoutManager(layoutManager);

        Log.e("Array List Size inside", String.valueOf(arrayList.size()) );
        categoryAdapter = new CategoryAdapter(arrayList, getContext(), categoryDialog, this);
        rv_category.setAdapter(categoryAdapter);

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);

            }
        });

        edit_catUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                catName = edit_catName.getText().toString();
                noOfSets = edit_setNo.getText().toString();

                if (edit_imaveView.getDrawable() == null || TextUtils.isEmpty(catName) || TextUtils.isEmpty(noOfSets))
                {
                    Toast.makeText(getContext(), "Please Fill Up All The Fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    imageName = folderRef.child("image" + imageUri.getLastPathSegment());
                    imageName.putFile(imageUri);
                    imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrl = String.valueOf(uri);
                            model = new CategoryModel(catName, Integer.valueOf(noOfSets), imageUrl);

                            databaseReference = FirebaseDatabase.getInstance().getReference("Categories");

                            databaseReference.orderByChild("name").equalTo(arrayList.get(position).getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                                    {
                                        snapshot.getRef().setValue(model);
                                    }
                                    Toast.makeText(getContext(), "Added Successfully", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    });
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();

            Glide.with(getContext()).load(imageUri).into(edit_imaveView);

        }

    }

    public void getData()
    {
        catRef = FirebaseDatabase.getInstance().getReference("Categories");
        catRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    model = snapshot.getValue(CategoryModel.class);
                    arrayList.add(model);
                }
                categoryAdapter.notifyDataSetChanged();
                Log.e("ArrayListSize getdata()", String.valueOf(arrayList.size()) );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void recyclerListClickListener(View v, int position) {

        this.position = position;
        Glide.with(getContext()).load(arrayList.get(position).getUrl()).into(edit_imaveView);
        edit_catName.setText(arrayList.get(position).getName());
        edit_setNo.setText(String.valueOf(arrayList.get(position).getSets()));
    }
}
