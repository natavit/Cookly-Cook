package com.natavit.cooklycook.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by Natavit on 2/4/2016 AD.
 */
public class AccountManager {

    private static AccountManager instance;

    private static int loginType;

    private static String name;

    private static String foodName;

    private GoogleApiClient googleApiClient;

    public static AccountManager getInstance() {
        if (instance == null)
            instance = new AccountManager();
        return instance;
    }

    private Context mContext;

    private AccountManager() {
        mContext = Contextor.getInstance().getContext();

//        loadCacheGraphRequest();
    }

    public void setLoginType(int type) {
        loginType = type;
    }

    public int getLoginType() {
        return loginType;
    }

    public String getLoginTypeString() {
        return loginType == 1 ? "fb" : "gg";
    }

    public void setFoodName(String name) {
        foodName = name;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    /**
     * Clear food cache from Shared Preferences
     */
    public void clearFoodCache() {
        mContext.getSharedPreferences("food",
                Context.MODE_PRIVATE)
                .edit().clear().commit();
    }

    /**
     *
     * Facebook Part
     *
     * */

    /**
     * Cache Facebook User's Data
     * @param object User's Data
     */
    public void saveCacheGraphRequest(JSONObject object) {
        SharedPreferences prefs = mContext.getSharedPreferences("facebook",
                Context.MODE_PRIVATE);

        String json = new Gson().toJson(object);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("json", json);
        editor.apply();
    }

    /**
     * Restore Cached Facebook User's Data if it is available
     * @return Cached Facebook User's Data
     */
    public String loadCacheGraphRequest() {
        SharedPreferences prefs = mContext.getSharedPreferences("facebook",
                Context.MODE_PRIVATE);

        String json = prefs.getString("json", null);
        if (json == null) {
            return null;
        }

        return json;
    }

    /**
     * Clear all cached Facebook data within Shared Preferences
     */
    private void clearCacheGraphRequest() {
        mContext.getSharedPreferences("facebook",
                Context.MODE_PRIVATE)
                .edit().clear().commit();
    }

    public void logOutFacebook() {
        clearCacheGraphRequest();
        clearFoodCache();
        LoginManager.getInstance().logOut();
    }


    /**
     *
     * Google Part
     *
     * */

    public void setupGoogleApiClient(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage((FragmentActivity) context /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    public void logOutGoogle() {
        clearFoodCache();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            //startLoginActivity();
                        }
                    }
                });
    }

}
