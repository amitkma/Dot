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
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
        Log.d("Re", "Reaached");
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

                if (e == null) {
                    DataLoaded = true;
                    if (objects.size() == 0)
                        fetch_from_server();

                    else {

                        if (XInitialization.APPDEBUG)
                            Log.d("Fragment", "fetched locally");

                        for (int i = 0; i < objects.size(); i++) {
                            final ParseObject parseObject = objects.get(objects.size() - i - 1);
                            final Data td = new Data();
                            td.parseobject = parseObject;
                            td.title = parseObject.getString("title");
                            td.price = parseObject.getString("price");
                            td.id = parseObject.getObjectId();
                            ParseFile parseFile = parseObject.getParseFile("image");
                            td.url = parseFile.getUrl();

                            adapter.addData(td);
                        }
                    }


                } else
                    e.printStackTrace();
            }


        });

    }

    private void fetch_from_server() {
        ParseQuery<ParseObject> parseQueryS = new ParseQuery<ParseObject>("Data");
        parseQueryS.whereEqualTo("Category", category);

        parseQueryS.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    DataLoaded = true;
                    for (int i = 0; i < objects.size(); i++) {
                        final ParseObject parseObject = objects.get(objects.size() - i - 1);
                        final Data td = new Data();
                        td.parseobject = parseObject;
                        td.title = parseObject.getString("title");

                        td.price = parseObject.getString("price");
                        td.id = parseObject.getObjectId();
                        ParseFile parseFile = parseObject.getParseFile("image");
                        td.url = parseFile.getUrl();

                        adapter.addData(td);
                    }

                    unpinAndRepin(objects);
                } else e.printStackTrace();
            }
        });
    }


    private void unpinAndRepin(List<ParseObject> objects) {

        ParseObject.unpinAllInBackground("data", objects, new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (XInitialization.APPDEBUG)
                    Log.d("pin", "deletedAll");
            }
        });
        ParseObject.pinAllInBackground("data", objects, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (XInitialization.APPDEBUG)
                    Log.d("pin", "pinnedAll");
            }
        });
    }

}
