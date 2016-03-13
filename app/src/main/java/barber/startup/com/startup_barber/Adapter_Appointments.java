package barber.startup.com.startup_barber;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ayush on 5/3/16.
 */
public class Adapter_Appointments extends RecyclerView.Adapter<Adapter_Appointments.ViewHolder> {
    private final Context c;
    ArrayList<FormatAppointments> appointmentdata = new ArrayList<>();
    private FormatAppointments data;

    public Adapter_Appointments(Context c) {

        this.c = c;
    }


    @Override
    public Adapter_Appointments.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemviewLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointments, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemviewLayout, parent.getContext());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(Adapter_Appointments.ViewHolder holder, int position) {

        data = appointmentdata.get(position);
        holder.numberServices.setText(Integer.toString(data.getNumberOfServices()) + " services included");
        holder.timeslot.setText(data.getDate() + " " + data.getTimeslot());
        holder.barber.setText(data.getBarber());
        holder.position.setText(Integer.toString(position + 1));
        holder.textPrice.setText("Rs. " + data.getTotalPrice() + "/-");
    }

    @Override
    public int getItemCount() {
        return appointmentdata.size();
    }

    public void addData(FormatAppointments data) {
        appointmentdata.add(data);
        notifyItemInserted(0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView numberServices;
        TextView timeslot;
        TextView barber;
        TextView position;
        TextView textPrice;


        public ViewHolder(View itemView, Context context) {
            super(itemView);
            position = (TextView) itemView.findViewById(R.id.number_services_appointment);
            timeslot = (TextView) itemView.findViewById(R.id.timeslot);
            barber = (TextView) itemView.findViewById(R.id.barberName);
            numberServices = (TextView) itemView.findViewById(R.id.position);
            textPrice = (TextView) itemView.findViewById(R.id.confirm_price);


        }

    }
}

