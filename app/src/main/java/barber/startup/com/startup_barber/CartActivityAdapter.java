package barber.startup.com.startup_barber;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import barber.startup.com.startup_barber.Utility.UserFavsAndCarts;

/**
 * Created by ayush on 5/2/16.
 */
public class CartActivityAdapter extends RecyclerView.Adapter<CartActivityAdapter.ViewHolder> {

    private final TextView empty;
    List<Data> cartdata = new ArrayList<>();
    Data data;
    private Context mContext;

    public CartActivityAdapter(Context c, List<Data> listcart, TextView empty) {
        this.empty = empty;
        this.cartdata = listcart;
        mContext = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        View itemviewLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemviewLayout, parent.getContext());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        data = cartdata.get(position);
        holder.title.setText(data.getTitle());
        holder.price.setText("Rs " + data.getPrice());

        Glide.clear(holder.img);
        if (data.getUrl() != null) {


            Glide.with(mContext).load(data.getUrl()).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.img);
        }

    }

    private void remove(final int position) {
        UserFavsAndCarts.listcart.remove(cartdata.get(position).getId());
        MainActivity.dataUpdated =true;
        final ParseUser parseUser = ParseUser.getCurrentUser();
        JSONArray jsonArray = new JSONArray();
        for(int i =0; i<UserFavsAndCarts.listcart.size(); i++){
            jsonArray.put(UserFavsAndCarts.listcart.get(i));
        }
        parseUser.put("cartLists", jsonArray);
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



    public void removeAllviews(TextView empty) {

        int l = cartdata.size();
        for (int i = 0; i < l; i++) {
            cartdata.remove(0);
            notifyItemRemoved(0);
        }
        empty.setVisibility(View.VISIBLE);
        empty.setText("Empty");
    }


    @Override
    public int getItemCount() {
        return cartdata.size();
    }

    public void addData(Data data) {
        cartdata.add(data);
        notifyItemInserted(0);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView price;
        TextView remove;
        ImageView img;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.cart_title);
            price = (TextView) itemView.findViewById(R.id.cart_price);
            remove = (TextView) itemView.findViewById(R.id.remove);
            img = (ImageView) itemView.findViewById(R.id.cart_image);
            remove.setOnClickListener(this);
            img.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.remove:
                    remove(getAdapterPosition());
                    break;
                case R.id.cart_image:
                    Data currentTrendData = cartdata.get(getAdapterPosition());
                    Intent i = new Intent(mContext, DetailsActivity.class);
                    i.putExtra("objectData", currentTrendData);
                    (mContext).startActivity(i);
                    break;
            }

        }
    }


}
