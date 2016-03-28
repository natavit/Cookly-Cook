package com.natavit.cooklycook.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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
public class MainActivity extends AppCompatActivity implements MainFragment.FragmentListener, View.OnClickListener {

    /**
     *
     * Variable
     *
     */

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ImageView ivProfileHeader;

    NavigationView navigationView;

    Toolbar toolbar;
    CoordinatorLayout coordinatorLayout;

    FancyButton btnLogout;
    TextView tvProfileName;
    TextView tvProfileEmail;

    AccountManager accountManager;

    /**
     *
     * Function
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        
        initInstances();

        if (savedInstanceState == null) {

            int loginType = getIntent().getIntExtra("loginType", R.integer.login_type_guest);

            if (loginType == R.integer.login_type_google) {
                initGoogleInstances();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.contentContainer, MainFragment.newInstance(null, loginType), "MainFragment")
                        .commit();
            }
            else if (loginType == R.integer.login_type_facebook) {
                initFacebookInstances();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.contentContainer, MainFragment.newInstance(null, loginType), "MainFragment")
                        .commit();
            }
        }

    }

    private void initInstances() {
        accountManager = AccountManager.getInstance();

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

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

    }

    private void setupNavigationView() {
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navSetting: {
//                        item.setChecked(true);
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
        ImageView ivProfileHeader = (ImageView) headerLayout.findViewById(R.id.ivProfileHeader);
        Glide.with(MainActivity.this)
                .load(Profile.getCurrentProfile().getProfilePictureUri(300, 300))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new CropCircleTransformation(MainActivity.this))
//                .error() put image when unsuccessful downloading occurs
                .into(ivProfileHeader);

        btnLogout = (FancyButton) findViewById(R.id.btnLogOut);
        btnLogout.setOnClickListener(this);
    }

    /**
     *
     * Initialize Facebook Variables
     *
     */

    private void initFacebookInstances() {

        String json = accountManager.loadCacheGraphRequest();
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        tvName.setText(getString(R.string.signed_in_fmt, Profile.getCurrentProfile().getName()));

    }

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
//                            tvName.setText(getString(R.string.signed_in_fmt, object.getString("name")));
//                            tvEmail.setText(getString(R.string.email_in_fmt, object.getString("email")));
                            tvProfileName.setText(object.getString("name"));
                            tvProfileEmail.setText(object.getString("email"));

                            // Cache user's information
                            accountManager.saveCacheGraphRequest(object);

                        } catch (NullPointerException e) {
                            if (Utils.getInstance().isOnline()) {
                                Utils.getInstance().showToast("Session Expired");
                                accountManager.logOutFacebook();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,gender");
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

        accountManager.setupGoogleApiClient(MainActivity.this);

//        tvName.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
//        tvEmail.setText(getString(R.string.email_in_fmt, acct.getEmail()));
        tvProfileName.setText(acct.getDisplayName());
        tvProfileEmail.setText(acct.getEmail());
    }

    // End Google //


    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
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

    @Override
    public void onPhotoItemClicked(HitDao dao) {
        Intent intent = new Intent(MainActivity.this, MoreInfoActivity.class);
        intent.putExtra("dao", dao);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogOut: {
                if (accountManager.getLoginType() == R.integer.login_type_facebook) {
                    accountManager.logOutFacebook();
                }
                else if (accountManager.getLoginType() == R.integer.login_type_google) {
                    accountManager.logOutGoogle();
                }
                Intent signInIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(signInIntent);
                break;
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
