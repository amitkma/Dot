package barber.startup.com.startup_barber;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
public class Adapter_Barber extends RecyclerView.Adapter<Adapter_Barber.ViewHolder> {


    private final List<Format_Barber> barbersList;
    private final Context context;
    private Format_Barber tempData;

    public Adapter_Barber(Context context, List<Format_Barber> barberslist) {
        this.barbersList = barberslist;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemviewLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_barber, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemviewLayout, parent.getContext());
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        tempData = barbersList.get(position);
        holder.barber.setText(tempData.getBarber());

        holder.price.setText("Total Price: " + Integer.toString(tempData.getPrice()));
        holder.time.setText("Total time : " + Integer.toString(tempData.getTime()));


    }

    @Override
    public int getItemCount() {
        return barbersList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView barber;
        TextView price;
        TextView time;
        ImageView go;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            barber = (TextView) itemView.findViewById(R.id.barber);
            price = (TextView) itemView.findViewById(R.id.price);
            time = (TextView) itemView.findViewById(R.id.totaltime);
            go = (ImageView) itemView.findViewById(R.id.go);
            go.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.go) {
                Format_Barber data = barbersList.get(getAdapterPosition());
                Intent i=new Intent(context,Checkout.class);
                i.putExtra("barberId",data.getBarberId());
                i.putExtra("totalPrice",data.getPrice());
                i.putExtra("totalTime",data.getTime());
                i.putExtra("barberName",data.getBarber());

                context.startActivity(i);
            }

        }
    }
}
