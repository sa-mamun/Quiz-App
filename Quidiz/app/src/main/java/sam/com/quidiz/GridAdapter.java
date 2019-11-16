package sam.com.quidiz;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

public class GridAdapter extends BaseAdapter {

    private int sets = 0;
    private String categories;

    public GridAdapter(int sets, String categories) {
        this.sets = sets;
        this.categories = categories;
    }

    @Override
    public int getCount() {
        return sets;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View view;

        if (convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.set_item, parent, false);
        }
        else
        {
            view = convertView;
        }

        TextView setNoTv = view.findViewById(R.id.setNoTV);
        setNoTv.setText(String.valueOf(position+1));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent questionIntent = new Intent(parent.getContext(), QuestionsActivity.class);
                questionIntent.putExtra("categories", categories);
                questionIntent.putExtra("setNo", position+1);
                parent.getContext().startActivity(questionIntent);

            }
        });

        return view;
    }
}
