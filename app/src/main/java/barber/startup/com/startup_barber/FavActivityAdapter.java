package barber.startup.com.startup_barber;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

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
     /**   int pixels = (int) (((MainActivity.a) - 16) * scale + 0.5f);


        viewHolder.rl.getLayoutParams().height = (pixels) / 2;
      */
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        data = cartdata.get(position);
        holder.title.setText(data.getTitle());
        holder.price.setText("Rs " + data.getPrice());

        if (data.isCart()) {
            holder.cart.setColorFilter(Color.BLUE);
        } else holder.cart.setColorFilter(null);

        if (data.getUrl() != null) {
            Glide.with(mContext).load(data.getUrl()).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.back);
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
            remove.setOnClickListener(this);
            cart.setOnClickListener(this);
            back.setOnClickListener(this);

        }


        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.button_remove) {
                Data d = cartdata.get(getAdapterPosition());
                Log.d("d", d.getId());
                remove(getAdapterPosition());
            }
          else  if (v.getId() == R.id.button_cart) {

                if (cart.getColorFilter() == null) {
                    updateCart();

                } else if (cart.getColorFilter() != null)
                    Toast.makeText(mContext, "Already in Cart", Toast.LENGTH_SHORT).show();

            }
            else if(v.getId() == R.id.image){
                Data currentTrendData = cartdata.get(getAdapterPosition());
                Intent i = new Intent(mContext, DetailsActivity.class);
                i.putExtra("objectData", currentTrendData);
                (mContext).startActivity(i);
            }

        }

        private void updateCart() {

            ParseObject parseObject = new ParseObject(Defaults.CartClass);
            parseObject.put("cart", cartdata.get(getAdapterPosition()).getId());
            parseObject.pinInBackground("Cart" + ParseUser.getCurrentUser().getUsername(), new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        if (mContext instanceof Favourites) {
                            menu = ((Favourites) mContext).getMenu();
                        }
                        new ToggleActionItemColor(menu, mContext).makeIconRed(R.id.action_cart);
                        cart.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent_light));
                        if (Application.DEBUG)
                            Log.d("FavActivityAdapter", "Saved" + cartdata.get(getAdapterPosition()).getId());
                    } else e.printStackTrace();
                }
            });


        }

    }

}
