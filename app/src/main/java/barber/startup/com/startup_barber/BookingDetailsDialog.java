package barber.startup.com.startup_barber;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Amit on 12-03-2016.
 */
public class BookingDetailsDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm)
                .setMessage(Defaults.barberName+"\n\nDate: "+Defaults.dateformatintent+"\nTime: "+Defaults.timeSlot+"\n\nTotal: Rs."+Defaults.price)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        new CheckOutClass(getActivity()).updateParseSlot(Defaults.availableTimeSlots);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }
}
