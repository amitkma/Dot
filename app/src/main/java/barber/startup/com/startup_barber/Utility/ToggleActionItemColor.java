package barber.startup.com.startup_barber.Utility;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;

import barber.startup.com.startup_barber.R;

/**
 * Created by Amit on 09-03-2016.
 */
public class ToggleActionItemColor {

    private final Menu menu;
    private final Context context;

    public ToggleActionItemColor(Menu menu, Context context) {
        this.menu = menu;
        this.context = context;
    }

    public void makeIconRed(int id) {
        MenuItem item = menu.findItem(id);
        Drawable newIcon = item.getIcon();
        newIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent_light), PorterDuff.Mode.SRC_IN);
        item.setIcon(newIcon);
    }


}
