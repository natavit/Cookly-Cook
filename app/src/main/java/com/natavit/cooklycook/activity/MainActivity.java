package com.natavit.cooklycook.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;
import com.natavit.cooklycook.R;
import com.natavit.cooklycook.adapter.PagerAdapter;
import com.natavit.cooklycook.dao.HitDao;
import com.natavit.cooklycook.fragment.MainFragment;
import com.natavit.cooklycook.manager.AccountManager;
import com.natavit.cooklycook.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Natavit on 2/4/2016 AD.
 */
public class MainActivity extends AppCompatActivity
        implements MainFragment.MainFragmentListener, View.OnClickListener {

    /**
     *
     * Variable
     *
     */

    public static final int REQUEST_SETTING_CODE = 33333;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    NavigationView navigationView;
    ImageView ivProfileHeader;

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    CoordinatorLayout coordinatorLayout;

    FancyButton btnLogout;
    TextView tvProfileName;
    TextView tvProfileEmail;

    String foodName;


    /**
     *
     * Function
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // inside your activity (if you did not enable transitions in your theme)
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            // set an enter transition
            getWindow().setEnterTransition(new Explode());
            // set an exit transition
            getWindow().setExitTransition(new Explode());
        }

        setContentView(R.layout.activity_main);

        initInstances();

        if (!Utils.getInstance().isOnline())
            Utils.getInstance().showSnackBarLong("Offline Mode", coordinatorLayout);

        if (savedInstanceState == null) {
            if (AccountManager.getInstance().getLoginType() == getResources().getInteger(R.integer.login_type_google)) {
                initGoogleInstances();
            }
            else if (AccountManager.getInstance().getLoginType() == getResources().getInteger(R.integer.login_type_facebook)) {
                initFacebookInstances();
            }
        }

    }

    /**
     * Initialize variables
     */
    private void initInstances() {

        foodName = PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                .getString(getString(R.string.pref_food_key), getString(R.string.pref_food_default));

        AccountManager.getInstance().setFoodName(foodName);
        AccountManager.getInstance().setLoginType(
                getIntent().getIntExtra("loginType", getResources().getInteger(R.integer.login_type_facebook)));

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                MainActivity.this,
                drawerLayout,
                R.string.open_drawer,
                R.string.close_drawer
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupNavigationView();
        setupTabs();
    }

    /**
     * Initialize Tab View: Recipes and My Recipes
     */
    private void setupTabs() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Recipes"));
        tabLayout.addTab(tabLayout.newTab().setText("My Recipes"));

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * Initialize Navigation Drawer and its components
     */
    private void setupNavigationView() {
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navSetting: {
//                        item.setChecked(true);
                        startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), REQUEST_SETTING_CODE);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                }
                return false;
            }
        });

        View headerLayout = navigationView.getHeaderView(0);
        tvProfileName = (TextView) headerLayout.findViewById(R.id.tvProfileName);
        tvProfileEmail = (TextView) headerLayout.findViewById(R.id.tvProfileEmail);
        ivProfileHeader = (ImageView) headerLayout.findViewById(R.id.ivProfileHeader);

        btnLogout = (FancyButton) findViewById(R.id.btnLogOut);
        btnLogout.setOnClickListener(this);
    }

    /**
     *
     * Initialize Facebook Variables
     *
     */

    private void initFacebookInstances() {

        String json = AccountManager.getInstance().loadCacheGraphRequest();
        if (json == null) {
            graphRequest();
        }
        else {
            try {
                JSONObject object = new Gson().fromJson(json, JSONObject.class);
//                tvName.setText(getString(R.string.signed_in_fmt, object.getString("name")));
//                tvEmail.setText(getString(R.string.email_in_fmt, object.getString("email")));
                tvProfileName.setText(object.getString("name"));
                tvProfileEmail.setText(object.getString("email"));
                setProfileHeaderImage(Profile.getCurrentProfile().getProfilePictureUri(300, 300));
                AccountManager.getInstance().setName(object.getString("name"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        tvName.setText(getString(R.string.signed_in_fmt, Profile.getCurrentProfile().getName()));

    }

    /**
     * Make a request to fetch a user's information from Facebook
     */
    private void graphRequest() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        try {
                            tvProfileName.setText(object.getString("name"));
                            tvProfileEmail.setText(object.getString("email"));
                            setProfileHeaderImage(Profile.getCurrentProfile().getProfilePictureUri(300, 300));

                            // Cache user's information
                            AccountManager.getInstance().saveCacheGraphRequest(object);
                            AccountManager.getInstance().setName(object.getString("name"));

                        } catch (NullPointerException e) {
                            if (Utils.getInstance().isOnline()) {
                                Utils.getInstance().showToast("Session Expired");
                                AccountManager.getInstance().logOutFacebook();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,gender,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    // End Facebook //

    /**
     *
     * Initialize Google Variables
     *
     */
    private void initGoogleInstances() {

        GoogleSignInAccount acct = getIntent().getParcelableExtra("acct");

        AccountManager.getInstance().setupGoogleApiClient(MainActivity.this);

        tvProfileName.setText(acct.getDisplayName());
        tvProfileEmail.setText(acct.getEmail());
        AccountManager.getInstance().setName(acct.getDisplayName());

        setProfileHeaderImage(acct.getPhotoUrl());


    }

    // End Google //

    /**
     * Set up Profile image located on the Navigation Drawer
     * @param uri of an image
     */
    private void setProfileHeaderImage(Uri uri) {
        Glide.with(MainActivity.this)
                .load(uri)
                .placeholder(R.drawable.profile)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new CropCircleTransformation(MainActivity.this))
//                .error() put image when unsuccessful downloading occurs
                .into(ivProfileHeader);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);

        String fn = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_food_key), getString(R.string.pref_food_default));
        if (!AccountManager.getInstance().getFoodName().toUpperCase().equals(fn.toUpperCase())) {
            AccountManager.getInstance().setFoodName(fn);
            AccountManager.getInstance().clearFoodCache();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("tab", tabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setCurrentItem(savedInstanceState.getInt("tab"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * To listen to any click on item then create a new activity, MoreInfoActivity, to show more information
     * @param view to make a shared element animation
     * @param dao a selected recipe sent to a new activity
     */
    @Override
    public void onRecipeItemClicked(View view, HitDao dao) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(this, MoreInfoActivity.class);
            intent.putExtra("dao", dao);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, view.findViewById(R.id.ivImg), "ivFood");
            startActivity(intent, options.toBundle());
        }
        else {
            Intent intent = new Intent(MainActivity.this, MoreInfoActivity.class);
            intent.putExtra("dao", dao);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogOut: {
                if (AccountManager.getInstance().getLoginType()
                        == getResources().getInteger(R.integer.login_type_facebook)) {
                    AccountManager.getInstance().logOutFacebook();
                }
                else if (AccountManager.getInstance().getLoginType()
                        == getResources().getInteger(R.integer.login_type_google)) {
                    AccountManager.getInstance().logOutGoogle();
                }

                PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                        .edit().clear().commit();

                Intent signInIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(signInIntent);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
