package barber.startup.com.startup_barber;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by ayush on 4/2/16.
 */
public class Cart {


    static ParseUser parseUser = ParseUser.getCurrentUser();


    public static void getCartItemsId() {


        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(parseUser.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    if (object.getList("cart") != null) {
                        List<String> list = object.getList("cart");
                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Data");
                        for (int i = 0; i < list.size(); i++) {
                            query.whereEqualTo("objectId", list.get(i));
                        }
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {

                            }
                        });
                    }
                } else e.printStackTrace();
            }
        });


    }
}
