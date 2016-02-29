package barber.startup.com.startup_barber;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

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

        ParseQuery<ParseObject> parseObjectParseQuery = new ParseQuery<ParseObject>("fav");
        parseObjectParseQuery.fromPin(ParseUser.getCurrentUser().getUsername());
        parseObjectParseQuery.whereEqualTo("favourites", data.get(position).getId());
        parseObjectParseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

                holder.mImageView_fav.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(View v) {
                        if (holder.mImageView_fav.getColorFilter() == null) {
                            updateFavourites();

                        } else if (holder.mImageView_fav.getColorFilter() != null)
                            Toast.makeText(mContext, "Already in favourites", Toast.LENGTH_SHORT).show();
                    }

                    private void updateFavourites() {

                        ParseObject parseObject = new ParseObject("fav");
                        parseObject.put("favourites", data.get(position).getId());
                        parseObject.pinInBackground(ParseUser.getCurrentUser().getUsername(), new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    BaseActivity.make_favIcon_red();
                                    holder.mImageView_fav.setColorFilter(Color.RED);
                                    Log.d("saved", data.get(position).getTitle());
                                } else e.printStackTrace();
                            }
                        });


                    }
                });
                if (e == null) {
                    if (object != null) {
                        holder.mImageView_fav.setColorFilter(Color.RED);
                    }
                }
            }
        });


        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Cart");
        parseQuery.fromPin("Cart" + ParseUser.getCurrentUser().getUsername());
        parseQuery.whereEqualTo("cart", data.get(position).getId());
        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {


                holder.mImageView_addToCart.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(View v) {
                        if (holder.mImageView_addToCart.getColorFilter() == null) {
                            updateCart();

                        } else if (holder.mImageView_addToCart.getColorFilter() != null)
                            Toast.makeText(mContext, "Already in Cart", Toast.LENGTH_SHORT).show();


                    }

                    private void updateCart() {

                        ParseObject parseObject = new ParseObject("Cart");
                        parseObject.put("cart", data.get(position).getId());
                        parseObject.pinInBackground("Cart" + ParseUser.getCurrentUser().getUsername(), new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                    BaseActivity.make_cartIcon_blue();
                                    holder.mImageView_addToCart.setColorFilter(Color.BLUE);
                                    Log.d("savedincart", data.get(position).getTitle());
                                } else e.printStackTrace();
                            }
                        });


                    }
                });
                if (e == null) {
                    if (object != null) {
                        holder.mImageView_addToCart.setColorFilter(Color.BLUE);
                    }
                }
            }
        });


        if (currentTrendData.getUrl() != null) {
            if (check_connection())
                Picasso.with(mContext).load(currentTrendData.getUrl()).memoryPolicy(MemoryPolicy.NO_STORE).centerInside().resize(height, height).into(holder.mImageView);
            else
                Picasso.with(mContext).load(currentTrendData.getUrl()).memoryPolicy(MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.OFFLINE).centerInside().resize(height, height).into(holder.mImageView);

        }


        if (currentTrendData.getPrice() != null) {
            holder.price.setText("Rs " + currentTrendData.getPrice());
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
        private ImageView mImageView_addToCart;
        private ImageView mImageView_fav;


        public ViewHolder(View itemView, Context context) {
            super(itemView);
            mcontext = context;

            title = (TextView) itemView.findViewById(R.id.card_title);
            price = (TextView) itemView.findViewById(R.id.card_price);
            mImageView = (ImageView) itemView.findViewById(R.id.card_image);
            mImageView_addToCart = (ImageView) itemView.findViewById(R.id.addToCart_button);
            mImageView_fav = (ImageView) itemView.findViewById(R.id.fav_button);

        }


        @Override
        public void onClick(View v) {

        }
    }

}
