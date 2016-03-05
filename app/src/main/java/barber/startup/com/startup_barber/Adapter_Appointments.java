package barber.startup.com.startup_barber;

import android.content.Context;
import android.support.v4.content.FileProvider;
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
    ArrayList<FormatAppointments> appointmentdata = new ArrayList<>();
    private Context mContext;
    private FormatAppointments data;

    @Override
    public Adapter_Appointments.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        View itemviewLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointments, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemviewLayout, parent.getContext());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(Adapter_Appointments.ViewHolder holder, int position) {

        data = appointmentdata.get(position);
        holder.position.setText("#Appointment: " + position);
        holder.date.setText("Date: " + data.getDate());
        holder.timeslot.setText("Timeslot: " + data.getTimeslot());
        holder.barber.setText("Barber: " + data.getBarber() + " Bhawan");
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
        TextView date;
        TextView timeslot;
        TextView barber;
        TextView position;


        public ViewHolder(View itemView, Context context) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            timeslot = (TextView) itemView.findViewById(R.id.timeslot);
            barber = (TextView) itemView.findViewById(R.id.barber);
            position = (TextView) itemView.findViewById(R.id.position);


        }

    }
}

