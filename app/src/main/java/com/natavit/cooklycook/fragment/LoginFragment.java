package com.natavit.cooklycook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.natavit.cooklycook.R;
import com.natavit.cooklycook.activity.AnimationActivity;
import com.natavit.cooklycook.activity.MainActivity;
import com.natavit.cooklycook.util.Utils;

import java.util.Arrays;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * Created by Natavit on 2/4/2016 AD.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    /**
     *
     * Interface
     *
     */

    private FacebookCallback fbCallback =
            new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    updateFacebookToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    // App code
                    Utils.getInstance().showSnackBarShort("Login Cancel", coordinatorLayout);
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                    if (Utils.getInstance().isOnline())
                        Utils.getInstance().showSnackBarShort("Login Error", coordinatorLayout);
                    else
                        Utils.getInstance().showSnackBarShort("No Internet Connection", coordinatorLayout);
                }
            };


    /**
     *
     * Variable
     *
     */

    private static final String TAG = LoginFragment.class.getName();

    // Facebook variable
    private CallbackManager callbackManager;


    // Google variable
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient googleApiClient;

    // View
    private CoordinatorLayout coordinatorLayout;
    private LinearLayout linearLayoutLoginBtn;

    private FancyButton btnLoginFacebook;
    private FancyButton btnLoginGoogle;
    private TextView btnLoginGuest;

    /**
     *
     * Function
     *
     */

    public LoginFragment() {
        super();
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        initInstances(rootView);

//        initGuestInstances(rootView);
        initFacebookInstances(rootView);
        initGoogleInstances(rootView);

        return rootView;
    }

    private void initInstances(View rootView) {
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);

        linearLayoutLoginBtn = (LinearLayout) rootView.findViewById(R.id.linearLayoutLoginBtn);
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.up_from_bottom);
        linearLayoutLoginBtn.startAnimation(anim);
    }

    // Init Facebook
    // Login type = 1
    private void initFacebookInstances(View rootView) {
        // init instance with rootView.findViewById here
        //setRetainInstance(true);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().
                registerCallback(callbackManager, fbCallback);

        btnLoginFacebook = (FancyButton) rootView.findViewById(R.id.btnLoginFacebook);
        btnLoginFacebook.setOnClickListener(this);
    }

    private void logInFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(
                LoginFragment.this,
                Arrays.asList("public_profile", "email", "user_friends")
        );
    }

    private void updateFacebookToken(AccessToken newAccessToken) {
        if (newAccessToken != null) {
            Intent i = new Intent(getActivity(), MainActivity.class);
            i.putExtra("loginType", R.integer.login_type_facebook);
            startActivity(i);
            getActivity().finish();
        } else {
            Log.e(TAG, "AccessToken is null");
        }
    }
    // End Facebook //

    // Init Google //
    // Login type = 2
    private void initGoogleInstances(View rootView) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Utils.getInstance().showSnackBarLong("Log in failed", coordinatorLayout);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnLoginGoogle = (FancyButton) rootView.findViewById(R.id.btnLoginGoogle);
        btnLoginGoogle.setOnClickListener(this);
    }

    private void logInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Intent i = new Intent(getActivity(), MainActivity.class);
            i.putExtra("loginType", R.integer.login_type_google);
            i.putExtra("acct", acct);
            startActivity(i);
            getActivity().finish();
        }
    }
    // End Google //

    // Init Guest //
    // Login type = 3
//    private void initGuestInstances(View rootView) {
//        btnLoginGuest = (TextView) rootView.findViewById(R.id.btnLoginGuest);
//        btnLoginGuest.setOnClickListener(this);
//    }

    private void logInGuest() {
        // TODO: Login Guest, check login status by SharedPreference
//        Intent i = new Intent(getActivity(), MainActivity.class);
        Intent i = new Intent(getActivity(), AnimationActivity.class);
        i.putExtra("loginType", R.integer.login_type_guest);
        startActivity(i);
//        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Facebook Callback to handle the login or any change related with Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check Facebook Login status
        updateFacebookToken(AccessToken.getCurrentAccessToken());

        // Check Google Login status
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
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
        if (!Utils.getInstance().isOnline()) {
            Utils.getInstance().showSnackBarShort("No Internet Connection", coordinatorLayout);
        }
        else {
            switch (v.getId()) {
                case R.id.btnLoginFacebook: {
                    logInFacebook();
                    break;
                }

                case R.id.btnLoginGoogle:
                    logInGoogle();
                    break;
//                case R.id.btnLoginGuest:
//                    logInGuest();
//                    break;
            }
        }
    }

}
