package barber.startup.com.startup_barber;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by ayush on 26/2/16.
 */
public class Fragment_HairStyles extends Fragment {
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private MainActivityAdapter beardsAdapter;
    private boolean DataLoaded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_beards, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_beards);
        mRecyclerView.setHasFixedSize(true);
        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView.setLayoutManager(gaggeredGridLayoutManager);
        beardsAdapter = new MainActivityAdapter(getContext());

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        mRecyclerView.setAdapter(beardsAdapter);

        final ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Data");
        if (check_connection())
            parseQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        else
            parseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ONLY);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                DataLoaded = true;
                for (int i = 0; i < objects.size(); i++) {
                    final ParseObject parseObject = objects.get(objects.size() - i - 1);
                    final Data td = new Data();
                    td.title = parseObject.getString("title");
                    td.price = parseObject.getString("price");
                    td.id = parseObject.getObjectId();
                    ParseFile parseFile = parseObject.getParseFile("image");
                    td.url = parseFile.getUrl();

                    beardsAdapter.addData(td);
                }

            }

        });


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && !DataLoaded)
            setUpRecyclerView();
    }

    public boolean check_connection() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

//For 3G check
        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();
//For WiFi Check
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();

        System.out.println(is3g + " net " + isWifi);

        if (!is3g && !isWifi) {

            return false;
        } else {

            return true;
        }


    }

}
