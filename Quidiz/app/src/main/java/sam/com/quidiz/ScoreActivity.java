package sam.com.quidiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    TextView scoreTV, totalTV;
    Button doneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        scoreTV = findViewById(R.id.scoreTV);
        totalTV = findViewById(R.id.totalTV);
        doneBtn = findViewById(R.id.doneBtn);

        scoreTV.setText(String.valueOf(getIntent().getIntExtra("score", 0)));
        totalTV.setText("OUT OF "+String.valueOf(getIntent().getIntExtra("total", 0)));

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
