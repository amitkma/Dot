package barber.startup.com.startup_barber;

import android.content.Context;
import android.content.res.TypedArray;

/**
 * Created by ayush on 20/2/16.
 */

public class Utils {

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

}
