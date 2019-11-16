package sam.com.quidiz;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    ArrayList<CategoryModel> categoryList;
    Context context;

    public CategoryAdapter(ArrayList<CategoryModel> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.categories_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
//        Log.e("onCreateViewHolder", "on create view holder" );
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(categoryList.get(position).getUrl(), categoryList.get(position).getName(), categoryList.get(position).getSets());
//        Log.e("onBindViewHolder", "on Bind View Holder" );
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        CircleImageView imageView;
        TextView categoryTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            categoryTitle = itemView.findViewById(R.id.categoryName);
//            Log.e("View holder constructor", "My view Holder" );

        }

        public void setData(String url, final String title, final int sets)
        {
            Glide.with(context).load(url).into(imageView);
            categoryTitle.setText(title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent setIntent = new Intent(itemView.getContext(), SetsActivity.class);
                    setIntent.putExtra("title", title);
                    setIntent.putExtra("sets", sets);
//                    Log.e("setData", "setData" );
//                    Toast.makeText(itemView.getContext(), String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                    itemView.getContext().startActivity(setIntent);

                }
            });

        }
    }

}
