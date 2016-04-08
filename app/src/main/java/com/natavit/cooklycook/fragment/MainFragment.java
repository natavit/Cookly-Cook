package com.natavit.cooklycook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.natavit.cooklycook.R;
import com.natavit.cooklycook.adapter.FoodListAdapter;
import com.natavit.cooklycook.dao.FoodCollectionDao;
import com.natavit.cooklycook.dao.HitDao;
import com.natavit.cooklycook.datatype.MutableInteger;
import com.natavit.cooklycook.manager.AccountManager;
import com.natavit.cooklycook.manager.FoodListManager;
import com.natavit.cooklycook.manager.HttpManager;
import com.natavit.cooklycook.util.Utils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Natavit on 2/4/2016 AD.
 */
public class MainFragment extends Fragment {

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

    CoordinatorLayout coordinatorLayout;

    boolean isLoadingMore = false;

    ListView listView;
    FoodListAdapter listAdapter;

    FoodListManager foodListManager;

    MutableInteger lastPositionInteger;

    AccountManager accountManager;

    String foodName;

    /**
     *
     * Function
     *
     */

    public MainFragment() {
        super();
    }

    public static MainFragment newInstance(int loginType) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
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

        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Initialize Fragment level's variable(s)

        foodName = AccountManager.getInstance().getFoodName();

        foodListManager = new FoodListManager();
        lastPositionInteger = new MutableInteger(-1);
        accountManager = AccountManager.getInstance();

//        SharedPreferences prefs = getContext().getSharedPreferences("dummy",
//                Context.MODE_PRIVATE);
    }

    private void initInstances(View rootView, Bundle savedInstanceState) {
        // init instance with rootView.findViewById here

        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);

        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new FoodListAdapter(lastPositionInteger);
        listAdapter.setDao(foodListManager.getDao());
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(listViewItemClickListener);
        listView.setOnScrollListener(listViewScrollListener);

        if (savedInstanceState == null) {
            refreshData();
        }

    }

    /**
     *
     * Main Functions
     *
     */


    private void refreshData() {
        if (foodListManager.getCount() == 0) {
            reloadData();
        } else {
        }
    }

    private void reloadData() {
        Call<FoodCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadFoodList(foodName);
        call.enqueue(new FoodListLoadCallback(FoodListLoadCallback.MODE_RELOAD));
    }

    private void loadMoreData() {
        if (isLoadingMore) return;

        isLoadingMore = true;

        int nextPage = foodListManager.getNextPage();
        Call<FoodCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadMoreFoodList(foodName, nextPage, nextPage+10);
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
                    Utils.getInstance().showToast(response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<FoodCollectionDao> call, Throwable t) {

            clearLoadingMoreFlagIfCapable(mode);

            Utils.getInstance().showToast(t.toString());
        }

        private void clearLoadingMoreFlagIfCapable(int mode) {
            if (mode == MODE_LOAD_MORE)
                isLoadingMore = false;
        }
    }
}
