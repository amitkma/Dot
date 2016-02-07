package barber.startup.com.startup_barber;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by ayush on 29/1/16.
 */
public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {
    static int height = 0;
    static int width = 0;
    ParseUser parseUser = ParseUser.getCurrentUser();
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
    public void onBindViewHolder(final MainActivityAdapter.ViewHolder holder, final int position) {

        height = holder.mImageView.getLayoutParams().height;
        currentTrendData = data.get(position);
        if (currentTrendData.getTitle() != null)
            holder.title.setText(currentTrendData.getTitle());
        if (currentTrendData.getUrl() != null) {
            if (check_connection())
                Picasso.with(mContext).load(currentTrendData.getUrl()).centerInside().resize(height, height).into(holder.mImageView);
            else
                Picasso.with(mContext).load(currentTrendData.getUrl()).networkPolicy(NetworkPolicy.OFFLINE).centerInside().resize(height, height).into(holder.mImageView);

        }
        if (currentTrendData.getPrice() != null) {
            holder.price.setText("Rs " + currentTrendData.getPrice());
        }

        holder.mImageView_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_connection()) {
                    updateCart();

                } else
                    Toast.makeText(mContext, "poor Network", Toast.LENGTH_SHORT).show();

            }

            private void updateCart() {

                String id = data.get(position).id;
                parseUser.addUnique("cart", id);
                parseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(mContext, "Succesfully added to cart", Toast.LENGTH_SHORT).show();
                            BaseActivity.updatecart();
                        }
                        if (e != null) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "Something went wrong! Try again", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });


    }

    public boolean check_connection() {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

//For 3G check
        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();
//For WiFi Check
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();

        System.out.println(is3g + " net " + isWifi);

        if (!is3g && !isWifi) {

            return false;
        } else {

            return true;
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


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        final Context mcontext;
        private final TextView title;
        private final TextView price;
        private ImageView mImageView;
        private ImageView mImageView_options;


        public ViewHolder(View itemView, Context context) {
            super(itemView);
            mcontext = context;

            title = (TextView) itemView.findViewById(R.id.card_title);
            price = (TextView) itemView.findViewById(R.id.card_price);
            mImageView = (ImageView) itemView.findViewById(R.id.card_image);
            mImageView_options = (ImageView) itemView.findViewById(R.id.options);

        }


        @Override
        public void onClick(View v) {

        }
    }

}
