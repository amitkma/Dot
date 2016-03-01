package barber.startup.com.startup_barber;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by ayush on 28/2/16.
 */
public class Fragment_services extends Fragment {
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private MainActivityAdapter adapter;
    private boolean DataLoaded = false;
    private int category;

    public Fragment_services(int i) {
        this.category = i;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_styles_fragment);
        recyclerView.setHasFixedSize(true);
        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);
        adapter = new MainActivityAdapter(getContext());
        recyclerView.setAdapter(adapter);
        setUpRecyclerView();
    }

    public void setUpRecyclerView() {
        final ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Data");
        parseQuery.whereEqualTo("Category", category);
        parseQuery.fromPin("data");
        parseQuery.orderByDescending("updatedAt");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null && objects.size() > 0) {

                    for (int i = 0; i < objects.size(); i++) {
                        final ParseObject parseObject = objects.get(i);
                        final Data td = new Data();
                        td.title = parseObject.getString("title");
                        td.price = parseObject.getString("price");
                        td.id = parseObject.getObjectId();

                        ParseFile parseFile = parseObject.getParseFile("image");
                        td.url = parseFile.getUrl();


                        ParseQuery<ParseObject> parseObjectParseQuery = new ParseQuery<ParseObject>("Fav");
                        parseObjectParseQuery.fromPin(ParseUser.getCurrentUser().getUsername());
                        parseObjectParseQuery.whereEqualTo("favourites", parseObject.getObjectId());
                        parseObjectParseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {

                                if (e == null && object != null) {
                                    td.fav = true;
                                    ParseQuery<ParseObject> parseObjectParseQuery = new ParseQuery<ParseObject>("Cart");
                                    parseObjectParseQuery.fromPin("Cart" + ParseUser.getCurrentUser().getUsername());
                                    parseObjectParseQuery.whereEqualTo("cart", parseObject.getObjectId());
                                    parseObjectParseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject object, ParseException e) {
                                            if (e == null && object != null) {
                                                td.cart = true;
                                                Log.e("Fragment_services", "cart method is passed");
                                                adapter.addData(td);
                                            } else adapter.addData(td);

                                        }
                                    });

                                } else {
                                    ParseQuery<ParseObject> parseObjectParseQuery = new ParseQuery<ParseObject>("Cart");
                                    parseObjectParseQuery.fromPin("Cart" + ParseUser.getCurrentUser().getUsername());
                                    parseObjectParseQuery.whereEqualTo("cart", parseObject.getObjectId());
                                    parseObjectParseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject object, ParseException e) {
                                            if (e == null && object != null) {
                                                td.cart = true;
                                                adapter.addData(td);
                                            } else adapter.addData(td);

                                        }
                                    });
                                }

                            }
                        });
                    }
                } else if (e != null)
                    e.printStackTrace();

            }

        });

    }


}
