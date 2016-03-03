package barber.startup.com.startup_barber;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ayush on 28/2/16.
 */
public class FavActivityAdapter extends RecyclerView.Adapter<FavActivityAdapter.ViewHolder> {
    static int height = 0;
    private final TextView empty;

    ArrayList<Data> cartdata = new ArrayList<>();
    Data data;
    private Context mContext;

    public FavActivityAdapter(Context c, TextView empty) {
        this.empty = empty;
        mContext = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        View itemviewLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fav, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemviewLayout, parent.getContext());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        height = holder.back.getLayoutParams().height;

        data = cartdata.get(position);
        holder.title.setText(data.getTitle());
        holder.price.setText("Rs " + data.getPrice());

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseQuery<ParseObject> parseObjectParseQuery = new ParseQuery<ParseObject>("Fav");
                parseObjectParseQuery.fromPin(ParseUser.getCurrentUser().getUsername());
                parseObjectParseQuery.whereEqualTo("favourites", cartdata.get(position).getId());
                parseObjectParseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            if (object != null) {
                                object.unpinInBackground(ParseUser.getCurrentUser().getUsername(), new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {

                                            Log.d("object", "removed");
                                            cartdata.remove(position);
                                            notifyItemRemoved(position);
                                            if (cartdata.size() == 0) {
                                                empty.setVisibility(View.VISIBLE);
                                                empty.setText("Empty");

                                            }
                                        } else e.printStackTrace();

                                    }
                                });
                            }
                        } else e.printStackTrace();
                    }
                });


            }
        });


        holder.cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        if (data.getUrl() != null) {
            /** if (check_connection())
             Picasso.with(mContext).load(data.getUrl()).centerInside().resize(height, height).into(holder.back);
             else
             Picasso.with(mContext).load(data.getUrl()).networkPolicy(NetworkPolicy.OFFLINE).centerInside().resize(height, height).into(holder.back);
             */
            Glide.with(mContext).load(data.getUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.back);
        }
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


        return cartdata.size();
    }

    public void addData(Data data) {
        cartdata.add(data);
        notifyItemInserted(getItemCount() - 1);

    }

    public void removeAllviews(TextView empty) {

        int l = cartdata.size();
        for (int i = 0; i < l; i++) {
            cartdata.remove(0);
            notifyItemRemoved(0);
        }
        empty.setVisibility(View.VISIBLE);
        empty.setText("Empty");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView price;
        ImageView remove;
        ImageView cart;
        ImageView back;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            price = (TextView) itemView.findViewById(R.id.price);
            remove = (ImageView) itemView.findViewById(R.id.button_remove);
            back = (ImageView) itemView.findViewById(R.id.image);
            cart = (ImageView) itemView.findViewById(R.id.button_cart);
        }

    }
}
