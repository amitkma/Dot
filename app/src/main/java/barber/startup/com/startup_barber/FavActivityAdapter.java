package barber.startup.com.startup_barber;

import android.content.Context;
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

import java.util.ArrayList;

/**
 * Created by ayush on 28/2/16.
 */
public class FavActivityAdapter extends RecyclerView.Adapter<FavActivityAdapter.ViewHolder> {
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


        data = cartdata.get(position);
        holder.title.setText(data.getTitle());
        holder.price.setText("Rs " + data.getPrice());


        if (data.getUrl() != null) {

            Glide.with(mContext).load(data.getUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.back);
        }
    }

    private void remove(final int position) {


        ParseQuery<ParseObject> parseObjectParseQuery = new ParseQuery<ParseObject>("Fav");
        parseObjectParseQuery.fromPin(ParseUser.getCurrentUser().getUsername());
        parseObjectParseQuery.whereEqualTo("favourites", cartdata.get(position).getId());
        parseObjectParseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject object, ParseException e) {
                if (e == null) {
                    if (object != null) {
                        object.unpinInBackground(ParseUser.getCurrentUser().getUsername(), new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    cartdata.remove(position);
                                    notifyItemRemoved(position);
                                    if (getItemCount() == 0) {
                                        empty.setVisibility(View.VISIBLE);
                                        empty.setText("Empty");

                                    }
                                } else if (Application.DEBUG)
                                    Log.e("FavActAdapterUnpin", e.getMessage());

                            }
                        });
                    }
                } else if (Application.DEBUG) Log.e("FavActAdaptergetObject", e.getMessage());
            }
        });

    }


    @Override
    public int getItemCount() {


        return cartdata.size();
    }

    public void addData(Data data) {
        cartdata.add(data);
        notifyItemInserted(0);

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

        }


        @Override
        public void onClick(View v) {
            Data d = cartdata.get(getAdapterPosition());
            Log.d("d", d.getId());
            remove(getAdapterPosition());
        }
    }
}
