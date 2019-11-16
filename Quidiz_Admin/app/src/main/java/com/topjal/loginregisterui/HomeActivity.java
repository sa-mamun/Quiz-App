package com.topjal.loginregisterui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    CardView categoryCV, questionCV, customizeCV, profileCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar homeToolbar = findViewById(R.id.homeToolbar);
        categoryCV = findViewById(R.id.categoryCV);
        questionCV = findViewById(R.id.questionCV);
        customizeCV = findViewById(R.id.customizeCV);
        profileCV = findViewById(R.id.profileCV);

        setSupportActionBar(homeToolbar);
        getSupportActionBar().setTitle("Admin Home");

        categoryCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categoryIntent = new Intent(HomeActivity.this, CategoryActivity.class);
                startActivity(categoryIntent);
            }
        });

        questionCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent questionIntent = new Intent(HomeActivity.this, QuestionActivity.class);
                startActivity(questionIntent);
            }
        });

        customizeCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent customizeIntent = new Intent(HomeActivity.this, CustomizeItemActivity.class);
                startActivity(customizeIntent);
            }
        });

    }

}
