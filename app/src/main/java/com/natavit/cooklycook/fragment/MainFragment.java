package com.natavit.cooklycook.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;
import com.natavit.cooklycook.R;
import com.natavit.cooklycook.adapter.FoodListAdapter;
import com.natavit.cooklycook.dao.FoodCollectionDao;
import com.natavit.cooklycook.dao.HitDao;
import com.natavit.cooklycook.datatype.MutableInteger;
import com.natavit.cooklycook.manager.AccountManager;
import com.natavit.cooklycook.manager.Contextor;
import com.natavit.cooklycook.manager.FoodListManager;
import com.natavit.cooklycook.manager.HttpManager;
import com.natavit.cooklycook.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Natavit on 2/4/2016 AD.
 */
public class MainFragment extends Fragment implements View.OnClickListener{

    /**
     *
     * Interface
     *
     */

    public interface FragmentListener {
        void onPhotoItemClicked(HitDao dao);
    }

    /**
     *
     * Variable
     *
     */

    private static int loginType;

    // Google variable
    private GoogleSignInAccount acct;

    // View
    TextView tvName;
    TextView tvEmail;

//    TextView tvGender;
//    FancyButton btnLogoutFacebook;
//    FancyButton btnLogoutGoogle;
//    FancyButton btnLogoutGuest;

    CoordinatorLayout coordinatorLayout;

    boolean isLoadingMore = false;

    ListView listView;
    FoodListAdapter listAdapter;

    FoodListManager foodListManager;

    MutableInteger lastPositionInteger;

    AccountManager accountManager;

    /**
     *
     * Function
     *
     */

    public MainFragment() {
        super();
    }

    public static MainFragment newInstance(Parcelable acct, int loginType) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        if (acct != null) args.putParcelable("acct", acct);
        args.putInt("loginType", loginType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Fragment level's variables

        init(savedInstanceState);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        initInstances(rootView, savedInstanceState);

        if (!Utils.getInstance().isOnline())
            Utils.getInstance().showSnackBarLong("Offline Mode", coordinatorLayout);

        accountManager.setLoginType(getLoginType());
        if (getLoginType() == R.integer.login_type_facebook) {
            initFacebookInstances(rootView, savedInstanceState);
        }
        else if (getLoginType() == R.integer.login_type_google) {
            initGoogleInstances(rootView, savedInstanceState);
        }
        else {
//            initGuestInstances(rootView, savedInstanceState);
        }

        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Initialize Fragment level's variable(s)
        foodListManager = new FoodListManager();
        lastPositionInteger = new MutableInteger(-1);
        accountManager = AccountManager.getInstance();
//        SharedPreferences prefs = getContext().getSharedPreferences("dummy",
//                Context.MODE_PRIVATE);
    }

    private void initInstances(View rootView, Bundle savedInstanceState) {
        // init instance with rootView.findViewById here
        loginType = getArguments().getInt("loginType");

        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
        tvName = (TextView) rootView.findViewById(R.id.tvName);
        tvEmail = (TextView) rootView.findViewById(R.id.tvEmail);
//        tvGender = (TextView) rootView.findViewById(R.id.tvGender);

        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new FoodListAdapter(lastPositionInteger);
        listAdapter.setDao(foodListManager.getDao());
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(listViewItemClickListener);
        listView.setOnScrollListener(listViewScrollListener);

        if (savedInstanceState == null)
            refreshData();
    }

    /**
     * Initialize Facebook Variables
     * @param rootView
     * @param savedInstanceState
     */
    private void initFacebookInstances(View rootView, Bundle savedInstanceState) {
//        btnLogoutFacebook = (FancyButton) rootView.findViewById(R.id.btnLogoutFacebook);
//
////        tvGender.setVisibility(View.VISIBLE);
//        btnLogoutFacebook.setVisibility(View.VISIBLE);
//        btnLogoutFacebook.setOnClickListener(this);

        String json = accountManager.loadCacheGraphRequest();
        if (json == null) {
            graphRequest();
        }
        else {
            try {
                JSONObject object = new Gson().fromJson(json, JSONObject.class);
                tvName.setText(getString(R.string.signed_in_fmt, object.getString("name")));
                tvEmail.setText(getString(R.string.email_in_fmt, object.getString("email")));
//                tvGender.setText(getString(R.string.gender_in_fmt, object.getString("gender")));
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
                            tvName.setText(getString(R.string.signed_in_fmt, object.getString("name")));
                            tvEmail.setText(getString(R.string.email_in_fmt, object.getString("email")));

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
     * Initialize Google Variables
     * @param rootView
     * @param savedInstanceState
     */
    private void initGoogleInstances(View rootView, Bundle savedInstanceState) {
        acct = getArguments().getParcelable("acct");

        accountManager.setupGoogleApiClient(getActivity());

        tvName.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
        tvEmail.setText(getString(R.string.email_in_fmt, acct.getEmail()));
//        btnLogoutGoogle = (FancyButton) rootView.findViewById(R.id.btnLogoutGoogle);
//        btnLogoutGoogle.setVisibility(View.VISIBLE);
//        btnLogoutGoogle.setOnClickListener(this);
    }


    // End Google //

//    /**
//     * Initialize Guest Variables
//     * @param rootView
//     * @param savedInstanceState
//     */
//    private void initGuestInstances(View rootView, Bundle savedInstanceState) {
//        btnLogoutGuest = (FancyButton) rootView.findViewById(R.id.btnLogoutGuest);
//        btnLogoutGuest.setVisibility(View.VISIBLE);
//        btnLogoutGuest.setOnClickListener(this);
//
//        tvName.setText(getString(R.string.signed_in_fmt, "Guest"));
//        tvEmail.setVisibility(View.GONE);
//    }
//
//    private void logOutGuest() {
//        clearFoodCache();
//        startLoginActivity();
//    }
    // End Guest //


    /**
     *
     * Main Functions
     *
     */

    private int getLoginType() {
        if (loginType == R.integer.login_type_facebook)
            return R.integer.login_type_facebook;

        if (loginType == R.integer.login_type_google)
            return R.integer.login_type_google;

        return R.integer.login_type_guest;
    }

//    private void startLoginActivity() {
//        Intent signInIntent = new Intent(getActivity(), LoginActivity.class);
//        startActivity(signInIntent);
//        getActivity().finish();
//    }

    private void refreshData() {
        if (foodListManager.getCount() == 0) {
            reloadData();
        } else {
        }
    }

    private void reloadData() {
        Call<FoodCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadFoodList("Hamburger");
        call.enqueue(new FoodListLoadCallback(FoodListLoadCallback.MODE_RELOAD));
    }

    private void loadMoreData() {
        if (isLoadingMore) return;

        isLoadingMore = true;

        int nextPage = foodListManager.getNextPage();
        Call<FoodCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadMoreFoodList("Hamburger", nextPage, nextPage+10);
        call.enqueue(new FoodListLoadCallback(FoodListLoadCallback.MODE_LOAD_MORE));
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

        outState.putBundle("foodListManager",
                foodListManager.onSaveInstanceState());

        outState.putBundle("lastPositionInteger",
                lastPositionInteger.onSaveInstanceState());

    }

    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore instance state here
        foodListManager.onRestoreInstanceState(
                savedInstanceState.getBundle("foodListManager"));

        lastPositionInteger.onRestoreInstanceState(
                savedInstanceState.getBundle("lastPositionInteger"));
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
//        switch (v.getId()) {
//            case R.id.btnLogoutFacebook:
//                logOutFacebook();
//                break;
//            case R.id.btnLogoutGoogle:
//                logOutGoogle();
//                break;
//            case R.id.btnLogoutGuest:
//                logOutGuest();
//                break;
//        }
    }

    private void showToast(String message) {
        Toast.makeText(Contextor.getInstance().getContext(),
                message,
                Toast.LENGTH_SHORT)
                .show();
    }

    /**
     *
     * Listener
     *
     */

    AbsListView.OnScrollListener listViewScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (view == listView) {
                //swipeRefreshLayout.setEnabled(firstVisibleItem == 0);

                // Load More
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {

                    // Check if there are available data
                    if (foodListManager.getCount() > 0) {
                        loadMoreData();
                    }
                }
            }
        }
    };

    AdapterView.OnItemClickListener listViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position < foodListManager.getCount()) {
                HitDao dao = foodListManager.getDao().getHits().get(position);
                FragmentListener listener = (FragmentListener) getActivity();
                listener.onPhotoItemClicked(dao);
            }
        }
    };


    /**
     *
     * Inner Class
     *
     */

    class FoodListLoadCallback implements Callback<FoodCollectionDao> {

        public static final int MODE_RELOAD = 1;
        public static final int MODE_LOAD_MORE = 2;

        int mode;

        public FoodListLoadCallback(int mode) {
            this.mode = mode;
        }

        @Override
        public void onResponse(Call<FoodCollectionDao> call, Response<FoodCollectionDao> response) {
//            swipeRefreshLayout.setRefreshing(false);

            if (response.isSuccess()) {
                FoodCollectionDao dao = response.body();

                int firstVisiblePosition = listView.getFirstVisiblePosition();
                View c = listView.getChildAt(0);
                int top = c == null ? 0 : c.getTop();

                if (mode == MODE_LOAD_MORE) {
                    foodListManager.appendDaoAtBottomPosition(dao);
                }
                else {
                    foodListManager.setDao(dao);
                }
                clearLoadingMoreFlagIfCapable(mode);
                listAdapter.setDao(foodListManager.getDao());
                listAdapter.notifyDataSetChanged();

//                showToast("Load Completed");
            } else {

                clearLoadingMoreFlagIfCapable(mode);

                try {
                    showToast(response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<FoodCollectionDao> call, Throwable t) {

            clearLoadingMoreFlagIfCapable(mode);

            showToast(t.toString());
        }

        private void clearLoadingMoreFlagIfCapable(int mode) {
            if (mode == MODE_LOAD_MORE)
                isLoadingMore = false;
        }
    }
}
