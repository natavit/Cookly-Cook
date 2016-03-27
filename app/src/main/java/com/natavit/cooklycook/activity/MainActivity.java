package com.natavit.cooklycook.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.appevents.AppEventsLogger;
import com.natavit.cooklycook.R;
import com.natavit.cooklycook.dao.HitDao;
import com.natavit.cooklycook.fragment.MainFragment;
import com.natavit.cooklycook.manager.AccountManager;

/**
 * Created by Natavit on 2/4/2016 AD.
 */
public class MainActivity extends AppCompatActivity implements MainFragment.FragmentListener, View.OnClickListener {

    /**
     *
     * Interface
     *
     */

    /**
     *
     * Variable
     *
     */

    // View
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private Button btnLogout;

    AccountManager accountManager;

    /**
     *
     * Function
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initInstances();

        if (savedInstanceState == null) {

            int loginType = getIntent().getIntExtra("loginType", R.integer.login_type_guest);

            if (loginType == R.integer.login_type_google) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.contentContainer, MainFragment.newInstance(
                                getIntent().getParcelableExtra("acct"), loginType),
                                "MainFragment")
                        .commit();
            }
            else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.contentContainer, MainFragment.newInstance(null, loginType), "MainFragment")
                        .commit();
            }
        }

    }

    private void initInstances() {

        accountManager = AccountManager.getInstance();

        toolbar = (Toolbar) findViewById(R.id.toolBar);
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

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        btnLogout = (Button) findViewById(R.id.btnLogOut);
        btnLogout.setOnClickListener(this);
    }

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
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true;

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
            }
        }
    }
}
