package barber.startup.com.startup_barber;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import barber.startup.com.startup_barber.Utility.NetworkCheck;

public class FeedbackActivity extends AppCompatActivity {

    private EditText newIdeaField;
    private boolean hideMenuIcon = true;

    private CheckBox mExpertArticles;
    private CheckBox mExpertAnswer;
    private CheckBox mReviewBarbers;
    private CheckBox mReviewProducts;
    private CheckBox mReviewExperts;
    private CheckBox mReviewDoctors;
    private CheckBox mReviewService;
    private CheckBox mCategoriseService;
    private CheckBox mSharePhotos;
    private CheckBox mVisitProfiles;
    private CheckBox mPickup;
    private ParseUser currentUser;
    private TextView submittedTextView;
    private TextView editFeedback;
    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;
    private RelativeLayout relativeLayout;
    private boolean noCheckBox = false;
    private CollapsingToolbarLayout collapsingLayout;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            checkFieldsForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.feedback_co_layout);
        collapsingLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        collapsingLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        currentUser = ParseUser.getCurrentUser();
        String uri = (String) currentUser.get("picUri");
        Toolbar toolbar = (Toolbar) findViewById(R.id.transparentToolbar);
        setSupportActionBar(toolbar);
        collapsingLayout.setTitle("Message Center");
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (uri != null) {
            ImageView img = (ImageView) findViewById(R.id.profile_image);
            Glide.with(this)
                    .load(uri)
                    .into(img);
        }
        if (!NetworkCheck.checkConnection(FeedbackActivity.this)) {
            nestedScrollView.setVisibility(View.GONE);
            Snackbar.make(coordinatorLayout, "Error in connection", Snackbar.LENGTH_INDEFINITE).show();
            appBarLayout.setExpanded(true);
        } else {
            updateUI();
        }

    }

    private void updateUI() {
        relativeLayout = (RelativeLayout) findViewById(R.id.questionListLayout);
        mExpertArticles = (CheckBox) findViewById(R.id.expert_articles_check);
        mExpertAnswer = (CheckBox) findViewById(R.id.expert_answer_check);
        mReviewBarbers = (CheckBox) findViewById(R.id.review_barbers);
        mReviewDoctors = (CheckBox) findViewById(R.id.review_doctors);
        mReviewExperts = (CheckBox) findViewById(R.id.review_experts);
        mReviewProducts = (CheckBox) findViewById(R.id.review_products);
        mReviewService = (CheckBox) findViewById(R.id.review_services);
        mCategoriseService = (CheckBox) findViewById(R.id.want_categorise_services);
        mVisitProfiles = (CheckBox) findViewById(R.id.want_visit_profiles);
        mSharePhotos = (CheckBox) findViewById(R.id.want_share_photos);
        mPickup = (CheckBox) findViewById(R.id.want_to_pickup);

        newIdeaField = (EditText) findViewById(R.id.new_idea_edit_field);
        newIdeaField.addTextChangedListener(textWatcher);

        submittedTextView = (TextView) findViewById(R.id.submitted_text_view);
        editFeedback = (TextView) findViewById(R.id.editFeedback);

        if (Defaults.isFeedbackSubmitted) {

            invalidateOptionsMenu();
            hideMenuIcon = true;
            findViewById(R.id.before_submit_card).setVisibility(View.GONE);
            findViewById(R.id.after_feedback_layout).setVisibility(View.VISIBLE);
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
            query.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    if (e == null) {
                        Defaults.feedback = object.getString("newIdea");
                        submittedTextView.setText(Defaults.feedback);
                    } else if (e.getCode() == ParseException.CONNECTION_FAILED) {
                        Snackbar.make(coordinatorLayout, "Error in connection", Snackbar.LENGTH_INDEFINITE).show();
                    }
                }
            });

            editFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noCheckBox = true;
                    hideMenuIcon = true;
                    invalidateOptionsMenu();
                    findViewById(R.id.before_submit_card).setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.GONE);
                    findViewById(R.id.after_feedback_layout).setVisibility(View.GONE);
                }
            });
        } else {
            noCheckBox = false;
            findViewById(R.id.before_submit_card).setVisibility(View.VISIBLE);
            findViewById(R.id.after_feedback_layout).setVisibility(View.GONE);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            updateFeedback();
            //Toast.makeText(FeedbackActivity.this, "We will submit", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateFeedback() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!noCheckBox) {
                jsonObject.put("expert_articles", mExpertArticles.isChecked());
                jsonObject.put("expert_answers", mExpertArticles.isChecked());
                jsonObject.put("review_barbers", mReviewBarbers.isChecked());
                jsonObject.put("review_products", mReviewProducts.isChecked());
                jsonObject.put("review_experts", mReviewExperts.isChecked());
                jsonObject.put("review_doctors", mReviewDoctors.isChecked());
                jsonObject.put("review_services", mReviewService.isChecked());
                jsonObject.put("categorise_services", mCategoriseService.isChecked());
                jsonObject.put("share_photos", mSharePhotos.isChecked());
                jsonObject.put("visit_profiles", mVisitProfiles.isChecked());
                jsonObject.put("pickup", mPickup.isChecked());
                currentUser.put("feedback", jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        currentUser.put("newIdea", newIdeaField.getText().toString().trim());
        currentUser.put("isFeedbackSubmitted", true);
        currentUser.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Defaults.isFeedbackSubmitted = true;
                Snackbar.make(coordinatorLayout, "Thanks for submitting feedback", Snackbar.LENGTH_LONG).show();
                updateUI();
            }
        });
    }

    private void checkFieldsForEmptyValues() {
        String ideaString = newIdeaField.getText().toString().trim();
        if (ideaString.equals("")) {
            hideMenuIcon = true;
            invalidateOptionsMenu();
        } else if (!ideaString.equals("")) {
            hideMenuIcon = false;
            invalidateOptionsMenu();
        }
    }

    @Override

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (hideMenuIcon) {
            menu.getItem(0).setVisible(false);
        } else
            menu.getItem(0).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

}
