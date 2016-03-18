package barber.startup.com.startup_barber;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import barber.startup.com.startup_barber.Utility.ToggleActionItemColor;
import barber.startup.com.startup_barber.Utility.UserFavsAndCarts;

/**
 * Created by Amit on 17-03-2016.
 */
public class FragmentExtras extends Fragment {
    List<Data> listparseobject = new ArrayList<>();
    ProgressDialog progressDialog = null;
    private View[] mNavDrawerItemViews = null;
    private AppBarLayout appBarLayout;
    private RecyclerView recyclerView;
    private MainActivityAdapter adapter;
    private int category;
    private Menu menu;
    private ParseUser currentUser;
    private ProgressBar progressBar;
    private Context mContext;
    private boolean mUserDataChanged;
    private TextView checknet;
    private boolean dataChanged;
    private LinearLayout linearLayout;
    private View addView;
    private TextView titleTextView;
    private ImageView mImageView_fav;
    private View spacerView;
    private ImageView mImageView_addToCart;
    private ViewGroup mDrawerItemsListContainer;


    public FragmentExtras() {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        this.category = bundle.getInt("position");
        return inflater.inflate(R.layout.fragment_extras, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mDrawerItemsListContainer = (ViewGroup) view.findViewById(R.id.recyclerview_styles_fragment);
        progressBar = (ProgressBar) view.findViewById(R.id.data_loading_spinner);
        checknet = (TextView) view.findViewById(R.id.checkconnection);
        addView = LayoutInflater.from(getActivity()).inflate(R.layout.item_list_extra, linearLayout);

        listparseobject.clear();
        currentUser = ParseUser.getCurrentUser();
        setUpRecyclerView();

    }

    private void getFavCartIds() {
        Log.e("FRagment_services", "getFavCartIds called");
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", currentUser.getObjectId());
        query.fromPin(ParseUser.getCurrentUser().getUsername());
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    if (object != null) {
                        JSONArray arrayFav = object.getJSONArray("favLists");

                        if (arrayFav != null) {
                            if (arrayFav.length() > 0) {
                                if (mContext instanceof MainActivity) {
                                    menu = ((MainActivity) mContext).getMenu();
                                }
                                new ToggleActionItemColor(menu, mContext).makeIconRed(R.id.action_fav);
                            } else if (arrayFav.length() == 0) {
                                if (mContext instanceof MainActivity) {
                                    menu = ((MainActivity) mContext).getMenu();
                                }
                                new ToggleActionItemColor(menu, mContext).makeIconDefault(R.id.action_fav);
                            }
                            UserFavsAndCarts.listfav.clear();
                            for (int i = 0; i < arrayFav.length(); i++) {
                                try {
                                    UserFavsAndCarts.listfav.add(arrayFav.getString(i));
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }

                        }

                        JSONArray arrayCart = object.getJSONArray("cartLists");
                        if (arrayCart != null) {
                            if (arrayCart.length() > 0) {
                                if (mContext instanceof MainActivity) {
                                    menu = ((MainActivity) mContext).getMenu();
                                }
                                new ToggleActionItemColor(menu, mContext).makeIconRed(R.id.action_cart);
                            } else if (arrayCart.length() == 0) {
                                if (mContext instanceof MainActivity) {
                                    menu = ((MainActivity) mContext).getMenu();
                                }
                                new ToggleActionItemColor(menu, mContext).makeIconDefault(R.id.action_cart);
                            }
                            UserFavsAndCarts.listcart.clear();

                            for (int i = 0; i < arrayCart.length(); i++) {
                                try {
                                    UserFavsAndCarts.listcart.add(arrayCart.getString(i));
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }

                        }
                    }

                } else
                    Log.e("ERROR", e.getMessage() + " " + e.getCode());
                setUpRecyclerView();
            }
        });

    }

    public void setUpRecyclerView() {
        listparseobject.clear();
        Log.e("This", "method is called");
        final ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(Defaults.INFO_CLASS);
        parseQuery.whereEqualTo("Category", category);
        parseQuery.fromPin("data");
        parseQuery.orderByDescending("updatedAt");
        parseQuery.orderByAscending("subCategory");
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
                        td.subCategory = parseObject.getInt("subCategory");
                        td.subCategoryString = parseObject.getString("subCategoryName");
                        ParseFile parseFile = parseObject.getParseFile("image");
                        if (parseFile != null)
                            td.url = parseFile.getUrl();


                        if (UserFavsAndCarts.listfav.contains(parseObject.getObjectId()))
                            td.fav = true;


                        if (UserFavsAndCarts.listcart.contains(parseObject.getObjectId()))
                            td.cart = true;

                        listparseobject.add(td);

                    }


                }

                updateUI();
            }
        });

    }

    private void updateUI() {

        progressBar.setVisibility(View.GONE);
        boolean isSpecial = false;
        mNavDrawerItemViews = new View[listparseobject.size()];
        mDrawerItemsListContainer.removeAllViews();
        int i = 0;
        for (i = 0; i < listparseobject.size(); i++) {
            if (i == 0) {
                mDrawerItemsListContainer.addView(addSubheader(listparseobject.get(i), mDrawerItemsListContainer));
                mNavDrawerItemViews[i] = makeNavDrawerItem(listparseobject.get(i), mDrawerItemsListContainer, isSpecial);
                mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
            } else if (i < listparseobject.size() - 1) {
                if (listparseobject.get(i).getSubCategory() != listparseobject.get(i + 1).getSubCategory()) {
                    isSpecial = true;
                    //  mNavDrawerItemViews[i] = makeNavDrawerItem(listparseobject.get(i), mDrawerItemsListContainer, isSpecial);
                    //   mDrawerItemsListContainer.addView(addSubheader(listparseobject.get(i), mDrawerItemsListContainer));
                    mNavDrawerItemViews[i] = makeNavDrawerItem(listparseobject.get(i), mDrawerItemsListContainer, isSpecial);
                    mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
                    mDrawerItemsListContainer.addView(addSubheader(listparseobject.get(i + 1), mDrawerItemsListContainer));
                } else {
                    isSpecial = false;
                    mNavDrawerItemViews[i] = makeNavDrawerItem(listparseobject.get(i), mDrawerItemsListContainer, isSpecial);
                    mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
                }
            } else {
                mNavDrawerItemViews[i] = makeNavDrawerItem(listparseobject.get(i), mDrawerItemsListContainer, isSpecial);
                mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
            }
        }
    }

    private View addSubheader(Data data, ViewGroup container) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.item_subheader, container, false);
        TextView tv = (TextView) linearLayout.findViewById(R.id.subheader);
        tv.setText(data.getSubCategoryString());
        return linearLayout;
    }

    private View makeNavDrawerItem(final Data data, ViewGroup mDrawerItemsListContainer, boolean isSpecial) {
        Log.e("Error", "makeNav");
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.item_list_extra, mDrawerItemsListContainer, false);
        titleTextView = (TextView) relativeLayout.findViewById(R.id.card_title);
        mImageView_addToCart = (ImageView) relativeLayout.findViewById(R.id.addToCart_button);
        mImageView_fav = (ImageView) relativeLayout.findViewById(R.id.fav_button);
        mImageView_addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageView_addToCart.getColorFilter() == null) {
                    // mImageView_addToCart.setImageAlpha(255);
                    // mImageView_addToCart.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent_light), PorterDuff.Mode.SRC_IN);
                    updateCart(data);
                } else if (mImageView_addToCart.getColorFilter() != null)
                    Toast.makeText(mContext, "Already in cart", Toast.LENGTH_SHORT).show();
            }
        });

        mImageView_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageView_fav.getColorFilter() == null) {
                    // mImageView_fav.setImageAlpha(255);
                    // mImageView_fav.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent_light), PorterDuff.Mode.SRC_IN);
                    updateFavourites(data);

                } else if (mImageView_fav.getColorFilter() != null)
                    Toast.makeText(mContext, "Already in favourites", Toast.LENGTH_SHORT).show();
            }
        });

        spacerView = relativeLayout.findViewById(R.id.space_divider);
        if (isSpecial)
            spacerView.setVisibility(View.VISIBLE);
        else if (!isSpecial) {
            spacerView.setVisibility(View.GONE);
        }
        titleTextView.setText(data.getTitle());
        if (data.isFav()) {
            mImageView_fav.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent_light), PorterDuff.Mode.SRC_IN);
            mImageView_fav.setImageAlpha(255);
        } else {
            mImageView_fav.setColorFilter(null);
            mImageView_fav.setImageAlpha(138);
        }
        if (data.isCart()) {
            mImageView_addToCart.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent_light), PorterDuff.Mode.SRC_IN);
            mImageView_addToCart.setImageAlpha(255);
        } else {
            mImageView_addToCart.setColorFilter(null);
            mImageView_addToCart.setImageAlpha(138);
        }
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetailsActivity(data);
            }
        });
        return relativeLayout;
    }

    private void startDetailsActivity(Data data) {
        Data currentTrendData = data;
        Intent i = new Intent(mContext, DetailsActivityExtras.class);
        i.putExtra("objectData", currentTrendData);
        (mContext).startActivity(i);
    }

    private void updateCart(Data data) {
        final ParseUser parseUser = ParseUser.getCurrentUser();
        UserFavsAndCarts.listcart.add(data.getId());
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < UserFavsAndCarts.listcart.size(); i++) {
            jsonArray.put(UserFavsAndCarts.listcart.get(i));
        }
        parseUser.put("cartLists", jsonArray);
        parseUser.pinInBackground(ParseUser.getCurrentUser().getUsername(), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    setUpRecyclerView();
                    menu = ((MainActivity) getActivity()).getMenu();
                    new ToggleActionItemColor(menu, getActivity()).makeIconRed(R.id.action_cart);
                    parseUser.saveInBackground();
                } else e.printStackTrace();
            }
        });
    }

    private void updateFavourites(Data data) {
        final ParseUser parseUser = ParseUser.getCurrentUser();
        UserFavsAndCarts.listfav.add(data.getId());
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < UserFavsAndCarts.listfav.size(); i++) {
            jsonArray.put(UserFavsAndCarts.listfav.get(i));
        }
        parseUser.put("favLists", jsonArray);
        parseUser.pinInBackground(ParseUser.getCurrentUser().getUsername(), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    setUpRecyclerView();
                    menu = ((MainActivity) getActivity()).getMenu();
                    new ToggleActionItemColor(menu, getActivity()).makeIconRed(R.id.action_fav);
                    parseUser.saveInBackground();
                } else e.printStackTrace();
            }
        });
    }
}
