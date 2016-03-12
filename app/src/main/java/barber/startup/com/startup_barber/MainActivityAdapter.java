package barber.startup.com.startup_barber;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import barber.startup.com.startup_barber.Utility.NetworkCheck;
import barber.startup.com.startup_barber.Utility.ToggleActionItemColor;
import barber.startup.com.startup_barber.Utility.UserFavsAndCarts;

/**
 * Created by ayush on 29/1/16.
 */
public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {

    private Context context;
    private List<Data> data = new ArrayList<>();
    private Context mContext;
    private Data currentTrendData;
    private Menu menu;
    private int heightpixels;
    private int widthpixels;


    public MainActivityAdapter() {

    }

    public MainActivityAdapter(List<Data> listparseobject, Context context) {
        this.mContext = context;
        this.data = listparseobject;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemviewLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);


        ViewHolder viewHolder = new ViewHolder(itemviewLayout, parent.getContext());



        final float scale = context.getResources().getDisplayMetrics().density;
        heightpixels = (int) (((MainActivity.a) - 16) * scale + 0.5f);
        viewHolder.rl.getLayoutParams().height = (heightpixels) / 2;
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
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
                final ParseUser parseUser = ParseUser.getCurrentUser();
                UserFavsAndCarts.listfav.add(data.get(position).getId());
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < UserFavsAndCarts.listfav.size(); i++) {
                    jsonArray.put(UserFavsAndCarts.listfav.get(i));
                }
                parseUser.put("favLists", jsonArray);
                parseUser.pinInBackground(ParseUser.getCurrentUser().getUsername(), new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            if (mContext instanceof MainActivity) {
                                menu = ((MainActivity) mContext).getMenu();
                            }
                            new ToggleActionItemColor(menu, mContext).makeIconRed(R.id.action_fav);
                            holder.mImageView_fav.setImageAlpha(255);
                            holder.mImageView_fav.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent_light), PorterDuff.Mode.SRC_IN);
                            parseUser.saveInBackground();
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
                final ParseUser parseUser = ParseUser.getCurrentUser();
                UserFavsAndCarts.listcart.add(data.get(position).getId());
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < UserFavsAndCarts.listcart.size(); i++) {
                    jsonArray.put(UserFavsAndCarts.listcart.get(i));
                }
                parseUser.put("cartLists", jsonArray);
                parseUser.pinInBackground(ParseUser.getCurrentUser().getUsername(), new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            if (mContext instanceof MainActivity) {
                                menu = ((MainActivity) mContext).getMenu();
                            }

                            new ToggleActionItemColor(menu, mContext).makeIconRed(R.id.action_cart);
                            holder.mImageView_addToCart.setImageAlpha(255);
                            holder.mImageView_addToCart.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent_light), PorterDuff.Mode.SRC_IN);
                            parseUser.saveInBackground();
                            if (Application.DEBUG)
                                Log.d("MainActivityAdapter", "Saved" + data.get(position).getId());
                        } else e.printStackTrace();
                    }
                });


            }
        });


        if (currentTrendData.isFav()) {
            holder.mImageView_fav.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent_light), PorterDuff.Mode.SRC_IN);
            holder.mImageView_fav.setImageAlpha(255);
        } else {
            holder.mImageView_fav.setColorFilter(null);
            holder.mImageView_fav.setImageAlpha(138);
        }

        if (currentTrendData.isCart()) {
            holder.mImageView_addToCart.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent_light), PorterDuff.Mode.SRC_IN);
            holder.mImageView_addToCart.setImageAlpha(255);
        } else {
            holder.mImageView_addToCart.setColorFilter(null);
            holder.mImageView_addToCart.setImageAlpha(138);
        }


        if (currentTrendData.getTitle() != null)
            holder.title.setText(currentTrendData.getTitle());

        holder.mImageView.setImageResource(0);
        if (currentTrendData.getUrl() != null) {
            Glide.with(mContext).load(currentTrendData.getUrl()).centerCrop().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.mImageView);
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


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final Context mcontext;
        private final TextView title;
        private final TextView price;
        private ImageView mImageView;
        private ImageView mImageView_addToCart;
        private ImageView mImageView_fav;
        private RelativeLayout rl;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            mcontext = context;

            title = (TextView) itemView.findViewById(R.id.card_title);
            price = (TextView) itemView.findViewById(R.id.card_price);
            mImageView = (ImageView) itemView.findViewById(R.id.card_image);
            mImageView_addToCart = (ImageView) itemView.findViewById(R.id.addToCart_button);
            mImageView_fav = (ImageView) itemView.findViewById(R.id.fav_button);
            rl = (RelativeLayout) itemView.findViewById(R.id.rlll);
            mImageView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
        if(v.getId() == R.id.card_image){
            if(NetworkCheck.checkConnection(mContext)) {
                Data currentTrendData = data.get(getAdapterPosition());
                Intent i = new Intent(mContext, DetailsActivity.class);
                i.putExtra("objectData", currentTrendData);
                (mContext).startActivity(i);
            }
            else
                Snackbar.make(v, "Error in connection", Snackbar.LENGTH_LONG).show();

        }
        }
    }

    public void refreshData(Data newData) {
        data.add(newData);
        notifyItemInserted(data.size() - 1);
    }
}
