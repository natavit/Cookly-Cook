package com.natavit.cooklycook;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.natavit.cooklycook.manager.Contextor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by Natavit on 2/4/2016 AD.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Contextor.getInstance().init(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());

//        printKeyHash();
    }



    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.natavit.cooklycook", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("App", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

}
