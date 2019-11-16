package sam.com.quidiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

public class QuestionsActivity extends AppCompatActivity {

    public static final String FILE_NAME = "QUIDIZZ";
    public static final String KEY_NAME = "QUESTIONS";

    TextView questionTV, noOfQuestionTV;
    FloatingActionButton bookmarkBtn;
    Button optionABtn, optionBBtn, optionCBtn, optionDBtn, shareBtn, nextBtn;
    LinearLayout optionLL;
    private int count = 0;

    private List<QuestionModel> questionList;
    private List<QuestionModel> bookmarkList;

    private int position = 0;
    private int score = 0;
    private String categories;
    private int setNo;
    private Dialog loadingDialog;
    DatabaseReference questionRef;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private int matchedQuestionPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Toolbar toolbar = findViewById(R.id.questionToolbar);
        setSupportActionBar(toolbar);

        questionTV = findViewById(R.id.questionTV);
        noOfQuestionTV = findViewById(R.id.noOfQuestionTV);
        optionABtn = findViewById(R.id.optionABtn);
        optionBBtn = findViewById(R.id.optionBBtn);
        optionCBtn = findViewById(R.id.optionCBtn);
        optionDBtn = findViewById(R.id.optionDBtn);
        shareBtn = findViewById(R.id.shareBtn);
        nextBtn = findViewById(R.id.nextBtn);
        optionLL = findViewById(R.id.optionLL);
        bookmarkBtn = findViewById(R.id.bookmarkBtn);

        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();

        getBookmarks();
        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelMatch())
                {
                    bookmarkList.remove(matchedQuestionPosition);
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_icon));
                }
                else {
                    bookmarkList.add(questionList.get(position));
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark));
                }
            }
        });

        categories = getIntent().getStringExtra("categories");
        setNo = getIntent().getIntExtra("setNo", 1);

        //For ProgressBar
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.category_background));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        questionList = new ArrayList<>();

        loadingDialog.show();
        questionRef = FirebaseDatabase.getInstance().getReference();
        questionRef.child("SETS").child(categories).child("questions").orderByChild("setNo").equalTo(setNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot mSnapshot : dataSnapshot.getChildren())
                {
                    questionList.add(mSnapshot.getValue(QuestionModel.class));
                }

                if (questionList.size() > 0)
                {

                    for (int i=0; i<4; i++)
                    {
                        optionLL.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkAns((Button) v);
                            }
                        });
                    }

                    setAnimation(questionTV, 0, questionList.get(position).getQuestion());
                    nextBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            nextBtn.setEnabled(false);
                            nextBtn.setAlpha(0.5f);
                            position++;
                            count = 0;
                            setEnable(true);
                            if (position == questionList.size())
                            {
                                //Score Activity
                                Intent scoreIntent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                                scoreIntent.putExtra("score", score);
                                scoreIntent.putExtra("total", questionList.size());
                                startActivity(scoreIntent);
                                finish();
                                return;
                            }
//                Log.e("next btn", "Next button click" );
                            setAnimation(questionTV, 0, questionList.get(position).getQuestion());
//                Log.e("Data Value", questionList.get(position).getQuestion() );

                        }
                    });

                    shareBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String body = questionList.get(position).getQuestion()+ "\n" +
                                            questionList.get(position).getQuestionA()+ "\n" +
                                            questionList.get(position).getQuestionB()+ "\n" +
                                            questionList.get(position).getQuestionC()+ "\n" +
                                            questionList.get(position).getQuestionD();

                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Quidizz Challange");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, body);
                            startActivity(Intent.createChooser(shareIntent, "Share Via"));

                        }
                    });

                }else {
                    finish();
                    Toast.makeText(QuestionsActivity.this, "No Questions", Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuestionsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        setBookmarks();
    }

    private void setAnimation(final View view, final int value, final String data)
    {
//        Log.e("SetAnimation", "Under Set Animation" );
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                if (value == 0 && count < 4)
                {
//                    Log.e("OnAnimation", "ON animation start" );
                    String option = "";
                    if (count == 0)
                    {
//                        Log.e("Count 0", "Under On Animation Start" );
                        option = questionList.get(position).getQuestionA();
                    }
                    else if (count == 1)
                    {
//                        Log.e("Count 1", "Under On Animation Start" );
                        option = questionList.get(position).getQuestionB();
                    }
                    else if (count == 2)
                    {
//                        Log.e("Count 2", "Under On Animation Start" );
                        option = questionList.get(position).getQuestionC();
                    }
                    else if (count == 3)
                    {
//                        Log.e("Count 3", "Under On Animation Start" );
                        option = questionList.get(position).getQuestionD();
                    }
//                    Log.e("Callback method", "Under On Animation Start" );
                    setAnimation(optionLL.getChildAt(count), 0, option);
//                    Log.e("Count Before", "Under On Animation Start" );
                    count++;
//                    Log.e("Count After", "Under On Animation Start" );
                }

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                Log.e("on Animation end", "on animation end method" );
                try {
                    ((TextView)view).setText(data);
                    noOfQuestionTV.setText(position+1+"/"+questionList.size());

                    if (modelMatch())
                    {
                        bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark));
                    }
                    else {

                        bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_icon));
                    }
                }catch (ClassCastException ex)
                {
                    ((Button)view).setText(data);
                }
                //view.setTag(data);

//                Log.e("Data Value", data );
                if (value == 0)
                {
//                    Log.e("Callback method", "Under On Animation End" );
                    setAnimation(view, 1, data);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void checkAns(Button selectedOption)
    {
        setEnable(false);
        nextBtn.setEnabled(true);
        nextBtn.setAlpha(1);
        if (selectedOption.getText().toString().equals(questionList.get(position).getCorrectANS()))
        {
            score++;        //score counting
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00c853")));
        }
        else {
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
            for (int i=0; i<4; i++)
            {
                if (((Button)(optionLL.getChildAt(i))).getText().toString().equals(questionList.get(position).getCorrectANS()))
                {
                    ((Button)(optionLL.getChildAt(i))).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00c853")));
                }
            }
        }
    }

    public void setEnable(boolean select)
    {
        for (int i=0; i<4; i++)
        {
            optionLL.getChildAt(i).setEnabled(select);
            if (select)
            {
                optionLL.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7A7A7A")));
            }
        }
    }

    public void getBookmarks()
    {
        String Json = preferences.getString(KEY_NAME, "");
        Type type = new TypeToken<List<QuestionModel>>(){}.getType();
        bookmarkList = gson.fromJson(Json, type);
        if (bookmarkList == null)
        {
            bookmarkList = new ArrayList<>();
        }
    }

    public boolean modelMatch()
    {
        boolean matched = false;
        int i = 0;
        for (QuestionModel model : bookmarkList)
        {

            if (model.getQuestion().equals(questionList.get(position).getQuestion())
            && model.getCorrectANS().equals(questionList.get(position).getCorrectANS())
            && model.getSetNo() == questionList.get(position).getSetNo())
            {
                matched = true;
                matchedQuestionPosition = i;

            }
            i++;
        }

        return matched;
    }

    public void setBookmarks()
    {
        String json = gson.toJson(bookmarkList);
        editor.putString(KEY_NAME, json);
        editor.commit();
    }
}
