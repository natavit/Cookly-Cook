package com.natavit.cooklycook.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.natavit.cooklycook.R;
import com.natavit.cooklycook.fragment.LoginFragment;

public class LoginActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initInstance();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer, LoginFragment.newInstance(), "LoginFragment")
                    .commit();
        }
    }


    private void initInstance() {
    }
}
