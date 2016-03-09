package barber.startup.com.startup_barber;

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
public class Fragment_services_test extends android.support.v4.app.Fragment {


    private RecyclerView recyclerView;

    private int category;
    private String[] uri;
   private List<String> listcart = new ArrayList<String>();
   private List<String> listfav = new ArrayList<String>();
    private List<Data> listparseobject = new ArrayList<Data>();


    public Fragment_services_test() {

    }

    public Fragment_services_test(int i) {
        //int genderCode=ParseUser.getCurrentUser().getInt("genderCode");
        this.category = i;
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
        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);




    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        Log.d("Fragment", "called menu" + category);

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Fragment", "onstart" + category);

        getFavlist();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("Fragment", "onstart" + category);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Fragment", "onResume" + category);

    }

    private void getFavlist() {
        final ParseQuery<ParseObject> parseObjectParseQuery = new ParseQuery<ParseObject>("Fav");
        parseObjectParseQuery.fromPin(ParseUser.getCurrentUser().getUsername());
        parseObjectParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects != null) {
                        if (objects.size() > 0) {
                            Log.d("passed", "passed");
                            MainActivity.makeFavIconRed();
                            for (int i = 0; i < objects.size(); i++) {
                                final ParseObject parseObject = objects.get(i);
                                listfav.add(parseObject.getString("favourites"));
                            }

                            Log.d("frag_serv listfav", String.valueOf(listfav));


                        }
                    }


                }else  Log.e("servicesfav", e.getMessage());
                getCartlist();
            }


        });
    }

    private void getCartlist() {
        ParseQuery<ParseObject> parseObjectParseQuery2 = new ParseQuery<ParseObject>("Cart");
        parseObjectParseQuery2.fromPin("Cart" + ParseUser.getCurrentUser().getUsername());
        parseObjectParseQuery2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects != null) {
                        if (objects.size() > 0) {
                            MainActivity.makeCartIconBlue();
                            for (int i = 0; i < objects.size(); i++) {
                                final ParseObject parseObject = objects.get(i);
                                listcart.add(parseObject.getString("cart"));

                            }
                            Log.d("frag_serv listcart", String.valueOf(listcart));
                        }

                    }
                } else Log.e("servicescart", e.getMessage());
                setUpRecyclerView();
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

                        if (listfav.contains(parseObject.getObjectId()))
                            td.fav = true;


                        if (listcart.contains(parseObject.getObjectId()))
                            td.cart = true;

                        listparseobject.add(td);


                    }

                    MainActivityAdapter adapter = new MainActivityAdapter(listparseobject, getContext());
                    recyclerView.setAdapter(adapter);

                } else if (e != null)
                    if (Application.DEBUG)
                        Log.e("Fragment_services", "setuprecyclerview " + e.getMessage());


            }
        });

    }


    @Override
    public void onStop() {
        super.onStop();
        if (Application.DEBUG)

            Log.d("Fragment", "onStop" + category);

        Glide.get(getActivity()).clearMemory();


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Fragment", "onDestroy" + category);



    }
}
