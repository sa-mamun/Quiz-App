package com.topjal.loginregisterui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryActivity extends AppCompatActivity {

    CircleImageView imageView;
    EditText categoryET, seNoET;
    Button addImageBtn, addCatBtn;
    Uri imageUri;
    CategoryModel model;
    StorageReference folderRef;
    StorageReference imageName;
    DatabaseReference databaseReference;
    String imageUrl, name, noOfSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        //Log.e("On Create", "On Create method" );

        Toolbar catToolbar = findViewById(R.id.categoryToolbar);
        setSupportActionBar(catToolbar);
        getSupportActionBar().setTitle("Add Categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.imageView);
        categoryET = findViewById(R.id.catNameET);
        seNoET = findViewById(R.id.seNoET);
        addImageBtn = findViewById(R.id.addImageBtn);
        addCatBtn = findViewById(R.id.addCatBtn);

        //Creating folder under Storage (Categories)
        folderRef = FirebaseStorage.getInstance().getReference().child("Categories");

        //Adding Image
        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Log.e("Add Image", "On Create add Image" );

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);

            }
        });

        addCatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("Add Button", "On Create add Button" );

                name = categoryET.getText().toString();
                noOfSets = seNoET.getText().toString();

                if (imageView.getDrawable() == null || TextUtils.isEmpty(name) || TextUtils.isEmpty(noOfSets))
                {
                    Toast.makeText(CategoryActivity.this, "Please Fill Up All The Fields", Toast.LENGTH_SHORT).show();
                }
                else{

                    imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrl = String.valueOf(uri);
                            model = new CategoryModel(name, Integer.valueOf(noOfSets), imageUrl);

                            databaseReference = FirebaseDatabase.getInstance().getReference("Categories");
                            String key = databaseReference.push().getKey();
                            databaseReference.child(key).setValue(model);
                            Toast.makeText(CategoryActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                            categoryET.setText("");
                            seNoET.setText("");
//                            imageView.setImageDrawable(null);

                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e("On Activity Result", "Over ride method" );
        if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();

            Glide.with(CategoryActivity.this).load(imageUri).into(imageView);

            imageName = folderRef.child("image" + imageUri.getLastPathSegment());
            imageName.putFile(imageUri);

        }

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
