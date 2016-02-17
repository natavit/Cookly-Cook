package com.natavit.cooklycook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.natavit.cooklycook.R;
import com.natavit.cooklycook.activity.LoginActivity;
import com.natavit.cooklycook.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * Created by Natavit on 2/4/2016 AD.
 */
public class MainFragment extends Fragment implements View.OnClickListener{

    private static int loginType;

    // Facebook variable


    // Google variable
    private GoogleSignInAccount acct;
    private GoogleApiClient googleApiClient;

    // View
    private TextView tvName;
    private TextView tvEmail;
    private TextView tvGender;
    private FancyButton btnLogoutFacebook;
    private FancyButton btnLogoutGoogle;
    private FancyButton btnLogoutGuest;

    private CoordinatorLayout coordinatorLayout;


    public MainFragment() {
        super();
    }

    public static MainFragment newInstance(Parcelable acct, int loginType) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putParcelable("acct", acct);
        args.putInt("loginType", loginType);
        fragment.setArguments(args);
        return fragment;
    }

    public static MainFragment newInstance(int loginType) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt("loginType", loginType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        initInstances(rootView);

        if (!Utils.getInstance().isOnline())
            Utils.getInstance().showSnackBarLong("Offline Mode", coordinatorLayout);

        if (getLoginType() == R.integer.login_type_facebook)
            initFacebookInstances(rootView);
        else if (getLoginType() == R.integer.login_type_google)
            initGoogleInstances(rootView);
        else
            initGuestInstances(rootView);

        return rootView;
    }

    private void initInstances(View rootView) {
        // init instance with rootView.findViewById here
        //setRetainInstance(true);
        loginType = getArguments().getInt("loginType");

        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
        tvName = (TextView) rootView.findViewById(R.id.tvName);
        tvEmail = (TextView) rootView.findViewById(R.id.tvEmail);
        tvGender = (TextView) rootView.findViewById(R.id.tvGender);
    }

    // Init Facebook //
    private void initFacebookInstances(View rootView) {
        btnLogoutFacebook = (FancyButton) rootView.findViewById(R.id.btnLogoutFacebook);

        tvGender.setVisibility(View.VISIBLE);
        btnLogoutFacebook.setVisibility(View.VISIBLE);
        btnLogoutFacebook.setOnClickListener(this);

//        graphRequest();
        tvName.setText(getString(R.string.signed_in_fmt, Profile.getCurrentProfile().getName()));

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
                            tvName.setText(getString(R.string.signed_in_fmt, object.getString("name")));
                            tvEmail.setText(getString(R.string.email, object.getString("email")));
                            tvGender.setText(getString(R.string.gender, object.getString("gender")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            if (Utils.getInstance().isOnline()) {
                                Utils.getInstance().showToast("Session Expired");
                                logOutFacebook();
                            }
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void logOutFacebook() {
        LoginManager.getInstance().logOut();
        startLoginActivity();
    }
    // End Facebook //

    // Init Google //
    private void initGoogleInstances(View rootView) {
        acct = getArguments().getParcelable("acct");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        tvName.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
        tvEmail.setText(getString(R.string.email, acct.getEmail()));
        btnLogoutGoogle = (FancyButton) rootView.findViewById(R.id.btnLogoutGoogle);
        btnLogoutGoogle.setVisibility(View.VISIBLE);
        btnLogoutGoogle.setOnClickListener(this);
    }

    private void logOutGoogle() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            startLoginActivity();
                        }
                    }
                });
    }
    // End Google //

    // Init Guest //
    private void initGuestInstances(View rootView) {
        btnLogoutGuest = (FancyButton) rootView.findViewById(R.id.btnLogoutGuest);
        btnLogoutGuest.setVisibility(View.VISIBLE);
        btnLogoutGuest.setOnClickListener(this);

        tvName.setText(getString(R.string.signed_in_fmt, "Guest"));
        tvEmail.setVisibility(View.GONE);
    }

    private void logOutGuest() {
        startLoginActivity();
    }

    // End Guest //


    private int getLoginType() {
        if (loginType == R.integer.login_type_facebook)
            return R.integer.login_type_facebook;

        if (loginType == R.integer.login_type_google)
            return R.integer.login_type_google;

        return R.integer.login_type_guest;
    }

    private void startLoginActivity() {
        Intent signInIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(signInIntent);
        getActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /*
     * Restore Instance State Here
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogoutFacebook:
                logOutFacebook();
                break;
            case R.id.btnLogoutGoogle:
                logOutGoogle();
                break;
            case R.id.btnLogoutGuest:
                logOutGuest();
                break;
        }
    }
}
