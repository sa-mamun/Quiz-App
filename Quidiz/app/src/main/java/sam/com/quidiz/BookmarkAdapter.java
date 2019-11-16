package sam.com.quidiz;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.MyViewHolder> {

    Context context;
    ArrayList<QuestionModel> bookmarkList;

    public BookmarkAdapter(Context context, ArrayList<QuestionModel> bookmarkList)
    {
        this.context = context;
        this.bookmarkList = bookmarkList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.bookmark_item_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(bookmarkList.get(position).getQuestion(), bookmarkList.get(position).getCorrectANS(), position);
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView question_TV, answer_TV;
        ImageButton delete_Btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            question_TV = itemView.findViewById(R.id.question_TV);
            answer_TV = itemView.findViewById(R.id.answer_TV);
            delete_Btn = itemView.findViewById(R.id.delete_Btn);

        }

        public void setData(String question, String answer, final int position)
        {
            this.question_TV.setText(question);
            this.answer_TV.setText(answer);
            Log.e("Position", String.valueOf(position));

            delete_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Position Click", String.valueOf(position));
                    bookmarkList.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                }
            });
        }
    }
}
