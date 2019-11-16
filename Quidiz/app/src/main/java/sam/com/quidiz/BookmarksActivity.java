package sam.com.quidiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookmarksActivity extends AppCompatActivity {

    public static final String FILE_NAME = "QUIDIZZ";
    public static final String KEY_NAME = "QUESTIONS";
    RecyclerView bookmark_RV;
    ArrayList<QuestionModel> bookmarkList;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        Toolbar bookmarkToolbar = findViewById(R.id.bookmarkToolbar);
        setSupportActionBar(bookmarkToolbar);
        getSupportActionBar().setTitle("Bookmarks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bookmark_RV = findViewById(R.id.bookmark_RV);

        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();

        getBookmarks();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        bookmark_RV.setLayoutManager(layoutManager);

        BookmarkAdapter adapter = new BookmarkAdapter(BookmarksActivity.this, bookmarkList);
        bookmark_RV.setAdapter(adapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        setBookmarks();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
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

    public void setBookmarks()
    {
        String json = gson.toJson(bookmarkList);
        editor.putString(KEY_NAME, json);
        editor.commit();
    }
}
