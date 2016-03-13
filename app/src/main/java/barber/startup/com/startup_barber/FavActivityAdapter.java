package barber.startup.com.startup_barber;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import barber.startup.com.startup_barber.Utility.NetworkCheck;
import barber.startup.com.startup_barber.Utility.ToggleActionItemColor;
import barber.startup.com.startup_barber.Utility.UserFavsAndCarts;

/**
 * Created by ayush on 28/2/16.
 */
public class FavActivityAdapter extends RecyclerView.Adapter<FavActivityAdapter.ViewHolder> {
    private final TextView empty;

   List<Data> cartdata = new ArrayList<>();
    Data data;
    private Context mContext;
    private Menu menu;
    private int heightpixels;

    public FavActivityAdapter(Context c, List<Data> listFav, TextView empty) {
        this.empty = empty;
        this.cartdata = listFav;
        mContext = c;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        View itemviewLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fav, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemviewLayout, parent.getContext());

        final float scale = mContext.getResources().getDisplayMetrics().density;
        heightpixels = (int) (((MainActivity.a) - 16) * scale + 0.5f);
        viewHolder.rl.getLayoutParams().height = (heightpixels) / 2;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        data = cartdata.get(position);
        holder.price.setText("Rs. "+data.getPrice()+"/-");

        if (data.isCart()) {
            holder.cart.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent_light),  PorterDuff.Mode.SRC_IN);
            holder.cart.setImageAlpha(255);
        } else {
            holder.cart.setColorFilter(null);
        holder.cart.setImageAlpha(138);}


        if (data.getUrl() != null) {
            Glide.with(mContext).load(data.getUrl()).crossFade().centerCrop().listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    Toast.makeText(mContext, "Error in connection", Toast.LENGTH_LONG).show();
                    return true;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }
            }).diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.back);
        }
    }

    private void remove(final int position) {
        UserFavsAndCarts.listfav.remove(cartdata.get(position).getId());
        MainActivity.dataUpdated =true;
        final ParseUser parseUser = ParseUser.getCurrentUser();
        JSONArray jsonArray = new JSONArray();
        for(int i =0; i<UserFavsAndCarts.listfav.size(); i++){
            jsonArray.put(UserFavsAndCarts.listfav.get(i));
        }
        parseUser.put("favLists", jsonArray);
        parseUser.pinInBackground(ParseUser.getCurrentUser().getUsername(), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){

                    cartdata.remove(position);
                    notifyItemRemoved(position);
                    if (getItemCount() == 0) {
                        empty.setVisibility(View.VISIBLE);
                        empty.setText("Empty");
                    }
                    parseUser.saveEventually();

                }
            }
        });

    }


    @Override
    public int getItemCount() {


        return cartdata.size();
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


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RelativeLayout rl;
        TextView price;
        ImageView remove;
        ImageView cart;
        ImageView back;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            rl = (RelativeLayout)itemView.findViewById(R.id.rlll);
            price = (TextView) itemView.findViewById(R.id.cart_price);
            remove = (ImageView) itemView.findViewById(R.id.removeFromFav);
            back = (ImageView) itemView.findViewById(R.id.cart_image);
            cart = (ImageView) itemView.findViewById(R.id.addToCart);
            remove.setOnClickListener(this);
            cart.setOnClickListener(this);
            back.setOnClickListener(this);

        }


        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.removeFromFav) {
                Data d = cartdata.get(getAdapterPosition());
                Log.d("d", d.getId());
                remove(getAdapterPosition());
            }
          else  if (v.getId() == R.id.addToCart) {

                if (cart.getColorFilter() == null) {
                    updateCart();

                } else if (cart.getColorFilter() != null)
                    Toast.makeText(mContext, "Already in Cart", Toast.LENGTH_SHORT).show();

            }
            else if(v.getId() == R.id.cart_image){
                if(NetworkCheck.checkConnection(mContext)) {
                    Data currentTrendData = cartdata.get(getAdapterPosition());
                    Intent i = new Intent(mContext, DetailsActivity.class);
                    i.putExtra("objectData", currentTrendData);
                    (mContext).startActivity(i);
                }
                else
                    Snackbar.make(v, "Error in connection", Snackbar.LENGTH_LONG).show();
            }

        }

        private void updateCart() {
            final ParseUser parseUser = ParseUser.getCurrentUser();
            UserFavsAndCarts.listcart.add(cartdata.get(getAdapterPosition()).getId());
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < UserFavsAndCarts.listcart.size(); i++) {
                jsonArray.put(UserFavsAndCarts.listcart.get(i));
            }
            parseUser.put("cartLists", jsonArray);
            parseUser.pinInBackground(ParseUser.getCurrentUser().getUsername(), new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        if (mContext instanceof Favourites) {
                            menu = ((MainActivity) Defaults.context).getMenu();
                        }

                        new ToggleActionItemColor(menu, mContext).makeIconRed(R.id.action_cart);
                        cart.setColorFilter(ContextCompat.getColor(mContext, R.color.accent), PorterDuff.Mode.SRC_IN);
                        parseUser.saveInBackground();
                    } else e.printStackTrace();
                }
            });


        }
    }

}
