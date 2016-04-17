package com.natavit.cooklycook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
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

    private static final int REQUEST_ADD_RECIPE_CODE = 1111;
    private static final int REQUEST_MORE_INFO_CODE = 2222;

    CoordinatorLayout coordinatorLayout;

    ListView listView;

    MutableInteger lastPositionInteger;

    FloatingActionButton fab;

    LocalFoodListManager localFoodListManager;
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

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        localFoodListManager = LocalFoodListManager.getInstance();
        lastPositionInteger = new MutableInteger(-1);
    }

    @SuppressWarnings("UnusedParameters")
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
        listAdapter.setRecipe(localFoodListManager.getLocalRecipes());
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(listViewItemClickListener);

//        items = new ArrayList<>();
//        updateRecipeList();
//
//        aa = new ArrayAdapter<String>(
//                getContext(),
//                android.R.layout.simple_list_item_1,
//                items);
//
//        listView.setAdapter(aa);
    }

//    private void updateRecipeList() {
//        items.clear();
//        dbHelper = new DBCooklyCook(getContext());
//        db = dbHelper.getReadableDatabase();
//
//        cursor = db.rawQuery("SELECT * FROM " + DBCooklyCook.TABLE_RECIPE, null);
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            items.add(cursor.getString(cursor.getColumnIndex(DBCooklyCook.COL_RECIPE_NAME)));
//            cursor.moveToNext();
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ADD_RECIPE_CODE: {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    localFoodListManager.loadLocalRecipes();
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
            case R.id.fab:
                Intent intent = new Intent(getContext(), AddRecipeActivity.class);
                startActivityForResult(intent, REQUEST_ADD_RECIPE_CODE);
        }
    }


    /**
     *
     * Listener
     *
     */

    AdapterView.OnItemClickListener listViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position < localFoodListManager.getCount()) {
                LocalRecipe recipe = localFoodListManager.getLocalRecipes().get(position);
                Intent intent = new Intent(getContext(), MoreInfoLocalActivity.class);
                intent.putExtra("recipe", recipe);
                startActivityForResult(intent, REQUEST_MORE_INFO_CODE);
            }
        }
    };
}
