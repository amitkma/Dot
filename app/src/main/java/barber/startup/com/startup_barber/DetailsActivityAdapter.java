package barber.startup.com.startup_barber;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ayush on 9/3/16.
 */
public class DetailsActivityAdapter extends RecyclerView.Adapter<DetailsActivityAdapter.ViewHolder> {


    private final List<ServiceDescriptionFormat> barbersList;
    private final Context context;
    private ServiceDescriptionFormat tempData;
    private RecyclerView view;

    public DetailsActivityAdapter(Context context, List<ServiceDescriptionFormat> barberslist, RecyclerView view) {
        this.barbersList = barberslist;
        this.context=context;
        this.view = view;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemviewLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_detail, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemviewLayout, parent.getContext());
        return viewHolder;

    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        tempData = barbersList.get(position);
        holder.barber.setText(tempData.getBarberName());
        holder.price.setText("Rs. "+tempData.getServicePrice()+"/-");
        holder.time.setText("Time: "+tempData.getServiceTime()+" mins");


    }

    @Override
    public int getItemCount() {
        return barbersList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView barber;
        TextView price;
        TextView time;


        public ViewHolder(View itemView, Context context) {
            super(itemView);
            barber = (TextView) itemView.findViewById(R.id.barberNameTextView);
            price = (TextView) itemView.findViewById(R.id.barberPriceTextView);
            time = (TextView) itemView.findViewById(R.id.barberTimeTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.detail_list_item_id) {
                ServiceDescriptionFormat data = barbersList.get(getAdapterPosition());
                if(data.getServicePrice()>70){
                    Snackbar.make(view, "Price of selected services is more than 70.", Snackbar.LENGTH_LONG).show();
                }
                else if(Defaults.mNumberOfServicesLeft>0) {
                    Intent i = new Intent(context, Checkout.class);
                    i.putExtra("barberId", data.getBarberId());
                    i.putExtra("totalPrice", data.getServicePrice());
                    i.putExtra("totalTime", data.getServiceTime());
                    i.putExtra("barberName", data.getBarberName());
                    context.startActivity(i);
                }
                else if(Defaults.mNumberOfServicesLeft == 0){
                    Snackbar.make(view, "You don't have any free service left.", Snackbar.LENGTH_LONG).show();
                }
            }

        }
    }
}
