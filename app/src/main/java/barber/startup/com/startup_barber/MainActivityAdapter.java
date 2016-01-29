package barber.startup.com.startup_barber;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by ayush on 29/1/16.
 */
public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Data> data = new ArrayList<>();
    private Context mContext;
    private Data currentTrendData;

    public MainActivityAdapter(Context context) {

        inflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public MainActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemviewLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemviewLayout, parent.getContext());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MainActivityAdapter.ViewHolder holder, int position) {


        currentTrendData = data.get(position);
        if (currentTrendData.getTitle() != null)
            holder.title.setText(currentTrendData.getTitle());
        if (currentTrendData.getUrl() != null) {
            Picasso.with(mContext).load(currentTrendData.url).into(holder.mImageView);

        }


    }

    @Override
    public int getItemCount() {
        return data.size();


    }

    public void addData(Data newTrendData) {
        data.add(newTrendData);
        notifyItemInserted(data.size() - 1);
    }

    public void addRefreshData(Data newTrendData) {
        data.add(0, newTrendData);
        notifyItemInserted(data.size() - 1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        final Context mcontext;
        private final TextView title;
        private ImageView mImageView;


        public ViewHolder(View itemView, Context context) {
            super(itemView);
            mcontext = context;

            title = (TextView) itemView.findViewById(R.id.card_title);
            mImageView = (ImageView) itemView.findViewById(R.id.card_image);


        }

        @Override
        public void onClick(View v) {

        }
    }
}
