package barber.startup.com.startup_barber;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ayush on 5/2/16.
 */
public class CartActivityAdapter extends RecyclerView.Adapter<CartActivityAdapter.ViewHolder> {

    ArrayList<Data> cartdata = new ArrayList<>();
    Data data;
    private Context mContext;

    public CartActivityAdapter(Context c) {
        mContext = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        View itemviewLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_card_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemviewLayout, parent.getContext());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        data = cartdata.get(position);
        holder.title.setText(data.getTitle());
        holder.price.setText("Rs " + data.getPrice());

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartdata.remove(position);
                notifyItemRemoved(position);
                CartDisplay.remove_item(mContext, position);


            }
        });
        Picasso.with(mContext).load(data.getUrl()).into(holder.img);

    }

    @Override
    public int getItemCount() {
        return cartdata.size();
    }

    public void addData(Data data) {
        cartdata.add(data);
        notifyItemInserted(getItemCount() - 1);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
        }

    }
}
