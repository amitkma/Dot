package barber.startup.com.startup_barber;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayush on 28/2/16.
 */
public class Fragment_services extends android.support.v4.app.Fragment {
    List<String> listcart = new ArrayList<String>();
    List<String> listfav = new ArrayList<String>();
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private MainActivityAdapter adapter;
    private int category;
    private String[] uri;


    public Fragment_services() {
    }

    public Fragment_services(int i) {
        this.category = i;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (Application.DEBUG)

            Log.d("Fragment", "onAttach" + category);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Application.DEBUG)
            Log.d("Fragment", "onCreate" + category);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Application.DEBUG)

            Log.d("Fragment", "onCreateView" + category);

        return inflater.inflate(R.layout.item_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (Application.DEBUG)

            Log.d("Fragment", "onViewCategory" + category);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_styles_fragment);
        recyclerView.setHasFixedSize(true);
        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);
        // adapter = new MainActivityAdapter(getContext());
        recyclerView.setAdapter(adapter);

        getFavlist();

    }

    private void getFavlist() {
        final ParseQuery<ParseObject> parseObjectParseQuery = new ParseQuery<ParseObject>("Fav");
        parseObjectParseQuery.fromPin(ParseUser.getCurrentUser().getUsername());
        parseObjectParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null)
                    if (objects != null) {
                        MainActivity.makeFavIconRed();
                        for (int i = 0; i < objects.size(); i++) {
                            final ParseObject parseObject = objects.get(i);
                            listfav.add(parseObject.getString("favourites"));
                        }

                        Log.d("frag_serv listfav", String.valueOf(listfav));

                        getCartlist();


                    }
            }


        });
    }

    private void getCartlist() {
        ParseQuery<ParseObject> parseObjectParseQuery2 = new ParseQuery<ParseObject>("Cart");
        parseObjectParseQuery2.fromPin("Cart" + ParseUser.getCurrentUser().getUsername());
        parseObjectParseQuery2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null)
                    if (objects != null) {
                        MainActivity.makeCartIconBlue();
                        for (int i = 0; i < objects.size(); i++) {
                            final ParseObject parseObject = objects.get(i);
                            listcart.add(parseObject.getString("cart"));

                        }
                        Log.d("frag_serv listcart", String.valueOf(listcart));

                        setUpRecyclerView();
                    }
            }
        });

    }

    public void setUpRecyclerView() {
        final ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(Defaults.DataClass);
        parseQuery.whereEqualTo("Category", category);
        parseQuery.fromPin("data");
        parseQuery.orderByDescending("updatedAt");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null && objects.size() > 0) {

                    uri = new String[objects.size()];
                    for (int i = 0; i < objects.size(); i++) {

                        final ParseObject parseObject = objects.get(i);
                        final Data td = new Data();
                        td.title = parseObject.getString("title");
                        td.price = parseObject.getString("price");
                        td.id = parseObject.getObjectId();

                        ParseFile parseFile = parseObject.getParseFile("image");
                        td.url = parseFile.getUrl();

                        uri[i] = td.getUrl();

                        Log.e("Fragment_services", "cart method is passed");
                        if (listfav.contains(parseObject.getObjectId()))
                            td.fav = true;


                        if (listcart.contains(parseObject.getObjectId()))
                            td.cart = true;

                        adapter.addData(td);

                    }
                } else if (e != null)
                    if (Application.DEBUG)
                        Log.e("Fragment_services", "setuprecyclerview " + e.getMessage());


            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Application.DEBUG)

            Log.d("Fragment", "onDestroy" + category);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (Application.DEBUG)

            Log.d("Fragment", "onDetach" + category);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (Application.DEBUG)

            Log.d("Fragment", "onStop" + category);

        Glide.get(getActivity()).clearMemory();


    }

    @Override
    public void onPause() {
        super.onPause();

        if (Application.DEBUG)

            Log.d("Fragment", "onPause" + category);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (Application.DEBUG)

            Log.d("Fragment", "onDestroyView" + category);


    }
}
