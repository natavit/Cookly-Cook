package com.natavit.cooklycook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

    public interface MainFragmentListener {
        void onRecipeItemClicked(View view, HitDao dao);
    }

    /**
     *
     * Variable
     *
     */

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

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
//        args.putInt("loginType", loginType);
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

        return rootView;
    }

    /**
     * Initialize variable
     * This method is usually called only once as long as it has not been destroyed by the system.
     * Goal: To keep states and values of each variable
     */
    private void init(Bundle savedInstanceState) {
        // Initialize Fragment level's variable(s)

        foodName = AccountManager.getInstance().getFoodName();

        foodListManager = new FoodListManager();
        lastPositionInteger = new MutableInteger(-1);
        accountManager = AccountManager.getInstance();

    }

    /**
     * Initialize view variables
     * and is used to check whether this is the first time of creating this fragment
     * if so, get new data
     */
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // init instance with rootView.findViewById here

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

    /**
     * Check whether there is any data available
     * if not, load a new one
     */
    private void refreshData() {
        if (foodListManager.getCount() == 0) {
            reloadData();
        }
    }

    /**
     * Create a connection to Edamam's server to fetch data
     */
    private void reloadData() {
        Call<FoodCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadFoodList(foodName);
        call.enqueue(new FoodListLoadCallback(FoodListLoadCallback.MODE_RELOAD));
    }

    /**
     * To Load another 10 data when a user scrolls down to the last item
     */
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
    public void onResume() {
        super.onResume();
        if (foodName != AccountManager.getInstance().getFoodName()) {
            foodListManager.setDao(null);
            listAdapter.setDao(foodListManager.getDao());
            listAdapter.notifyDataSetChanged();
            foodName = AccountManager.getInstance().getFoodName();
            refreshData();
        }
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

    /**
     *
     * Listener
     *
     */

    /**
     * Check scroll position
     * if the visible item is the last one, then load more data
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
                        if (Utils.getInstance().isOnline())
                            loadMoreData();
                    }
                }
            }
        }
    };

    /**
     * Listen to any click event on each item
     */
    AdapterView.OnItemClickListener listViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position < foodListManager.getCount()) {
                HitDao dao = foodListManager.getDao().getHits().get(position);
                MainFragmentListener listener = (MainFragmentListener) getActivity();
                listener.onRecipeItemClicked(view, dao);
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
