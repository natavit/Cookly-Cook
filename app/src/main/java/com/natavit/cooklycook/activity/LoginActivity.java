package com.natavit.cooklycook.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.natavit.cooklycook.R;
import com.natavit.cooklycook.fragment.LoginFragment;

/**
 * This class is the first activity which is stick with the login fragment
 */
public class LoginActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         * To check an instance state
         * OR This condition will be true if and only if
         * it is the first time creating this Activity to prevent duplicated fragment added.
         */
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer, LoginFragment.newInstance(), "LoginFragment")
                    .commit();
        }
    }
}
