package com.natavit.cooklycook.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.natavit.cooklycook.R;
import com.natavit.cooklycook.activity.AddRecipeActivity;
import com.natavit.cooklycook.activity.MoreInfoLocalActivity;
import com.natavit.cooklycook.adapter.LocalFoodListAdapter;
import com.natavit.cooklycook.datatype.MutableInteger;
import com.natavit.cooklycook.manager.LocalFoodListManager;
import com.natavit.cooklycook.model.LocalRecipe;

public class MyRecipeFragment extends Fragment implements View.OnClickListener {

    /**
     * Variable
     */

    public static final int REQUEST_ADD_RECIPE_CODE = 1111;
    public static final int REQUEST_MORE_INFO_CODE = 2222;

    CoordinatorLayout coordinatorLayout;

    ListView listView;

    MutableInteger lastPositionInteger;

    FloatingActionButton fab;

    LocalFoodListAdapter listAdapter;

    /**
     * Function
     */

    public MyRecipeFragment() {
        super();
    }

    public static MyRecipeFragment newInstance() {
        MyRecipeFragment fragment = new MyRecipeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_recipe, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    /**
     * Initialize variable
     * This method is usually called only once as long as it has not been destroyed by the system.
     * Goal: To keep states and values of each variable
     */
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        lastPositionInteger = new MutableInteger(-1);
    }

    /**
     * Initialize view variables
     */
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
        listView = (ListView) rootView.findViewById(R.id.listView);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new LocalFoodListAdapter(lastPositionInteger);
        listAdapter.setRecipe(LocalFoodListManager.getInstance().getLocalRecipes());
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(listViewItemClickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ADD_RECIPE_CODE: {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    LocalFoodListManager.getInstance().loadLocalRecipes();
                    listAdapter.notifyDataSetChanged();
                }
                break;
            }
            case REQUEST_MORE_INFO_CODE: {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    if (data.getExtras().getBoolean("isUpdated")) {
                        listAdapter.notifyDataSetChanged();
                    }
                }
            }
            default:
                listAdapter.notifyDataSetChanged();
                break;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance (Fragment level's variables) State here
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance (Fragment level's variables) State here
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab: {
                Intent intent = new Intent(getContext(), AddRecipeActivity.class);
                startActivityForResult(intent, REQUEST_ADD_RECIPE_CODE);
                break;
            }
        }
    }


    /**
     *
     * Listener
     *
     */

    /**
     * Listen to any click event on each item
     */
    AdapterView.OnItemClickListener listViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position < LocalFoodListManager.getInstance().getCount()) {
                LocalRecipe recipe = LocalFoodListManager.getInstance().getLocalRecipes().get(position);

//                MyRecipeFragmentListener listener = (MyRecipeFragmentListener) getActivity();
//                listener.onLocalRecipeItemClicked(view, recipe);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent(getActivity(), MoreInfoLocalActivity.class);
                    intent.putExtra("recipe", recipe);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(getActivity(), view.findViewById(R.id.ivImg), "ivFood");
                    startActivityForResult(intent, REQUEST_MORE_INFO_CODE, options.toBundle());
                }
                else {
                    Intent intent = new Intent(getActivity(), MoreInfoLocalActivity.class);
                    intent.putExtra("recipe", recipe);
                    startActivity(intent);
                }
            }
        }
    };
}
