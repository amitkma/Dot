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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

/**
 * Created by ayush on 29/1/16.
 */
public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {
    static int height = 0;
    static int width = 0;
    private Context context;
    private ArrayList<Data> data = new ArrayList<>();
    private Context mContext;
    private Data currentTrendData;

    public MainActivityAdapter(Context context) {
        mContext = context;

    }

    @Override
    public MainActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemviewLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemviewLayout, parent.getContext());
        viewHolder.mImageView.getLayoutParams().height = (MainActivity.height - 104) / 2;
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final MainActivityAdapter.ViewHolder holder, final int position) {

        height = holder.mImageView.getLayoutParams().height;
        if (Application.DEBUG)
            Log.d("height", String.valueOf(height));

        currentTrendData = data.get(position);

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

                ParseObject parseObject = new ParseObject(Defaults.FavouritesClass);
                parseObject.put("favourites", data.get(position).getId());
                parseObject.pinInBackground(ParseUser.getCurrentUser().getUsername(), new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            MainActivity.makeFavIconRed();
                            holder.mImageView_fav.setColorFilter(Color.RED);

                            if (Application.DEBUG)
                                Log.d("MainActivityAdapter", "Saved" + data.get(position).getId());
                        } else e.printStackTrace();
                    }
                });


            }
        });


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

                ParseObject parseObject = new ParseObject(Defaults.CartClass);
                parseObject.put("cart", data.get(position).getId());
                parseObject.pinInBackground("Cart" + ParseUser.getCurrentUser().getUsername(), new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            MainActivity.makeCartIconBlue();
                            holder.mImageView_addToCart.setColorFilter(Color.BLUE);
                            if (Application.DEBUG)
                                Log.d("MainActivityAdapter", "Saved" + data.get(position).getId());
                        } else e.printStackTrace();
                    }
                });


            }
        });


        if (currentTrendData.isFav()) {
            holder.mImageView_fav.setColorFilter(Color.RED);
        } else holder.mImageView_fav.setColorFilter(null);

        if (currentTrendData.isCart()) {
            holder.mImageView_addToCart.setColorFilter(Color.BLUE);
        } else holder.mImageView_addToCart.setColorFilter(null);


        if (currentTrendData.getTitle() != null)
            holder.title.setText(currentTrendData.getTitle());


        if (currentTrendData.getUrl() != null) {

            Glide.with(mContext).load(currentTrendData.getUrl()).override(height, height).diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.mImageView);
        }

        if (currentTrendData.getPrice() != null) {
            holder.price.setText("Rs " + currentTrendData.getPrice());
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
