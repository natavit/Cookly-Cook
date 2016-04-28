package com.natavit.cooklycook.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.natavit.cooklycook.R;
import com.natavit.cooklycook.model.LocalIngredient;
import com.natavit.cooklycook.model.LocalRecipe;

/**
 * Created by Natavit on 2/4/2016 AD.
 */
public class MoreInfoLocalActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_EDIT_RECIPE_CODE = 1111;

    private static boolean isUpdated = false;

    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    FloatingActionButton fab;

    TextView tvFoodName;
    ImageView imageViewHeader;

    LocalRecipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info_local);
        initInstances();
    }

    /**
     * Initialize view variables
     */
    private void initInstances() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        recipe = getIntent().getParcelableExtra("recipe");

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(recipe.getName());
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(MoreInfoLocalActivity.this, android.R.color.transparent));

        tvFoodName = (TextView) findViewById(R.id.tvFoodName);
        String ingredient = "";
        for (LocalIngredient ing : recipe.getIngredients()) {
            ingredient += ing.getName() + " - " + ing.getAmount() + " " + ing.getUnit() + "\n";
        }
        tvFoodName.setText(ingredient);

        imageViewHeader = (ImageView) findViewById(R.id.imageViewHeader);
        Glide.with(MoreInfoLocalActivity.this)
                .load(recipe.getImgPath())
                .placeholder(R.drawable.loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageViewHeader);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_EDIT_RECIPE_CODE: {
                if (resultCode == RESULT_OK) {

                    isUpdated = true;

                    if (data.getExtras().getParcelable("newRecipe") == null)
                        finish();

                    loadUpdatedRecipe(data);
                }
            }
        }
    }

    /**
     * This method will be called when there is any update on a selected local recipe
     * @param data an Edited data
     */
    private void loadUpdatedRecipe(Intent data) {
        LocalRecipe lr = data.getExtras().getParcelable("newRecipe");
        if (lr == null) return;
        else if (lr.getIngredients() == null) return;
        else {
            collapsingToolbarLayout.setTitle(lr.getName());

            String ingredient = "";
            for (LocalIngredient ing : lr.getIngredients()) {
                ingredient += ing.getName() + " - " + ing.getAmount() + " " + ing.getUnit() + "\n";
            }
            tvFoodName.setText(ingredient);

            Glide.with(MoreInfoLocalActivity.this)
                    .load(lr.getImgPath())
                    .placeholder(R.drawable.loading)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewHeader);

            recipe = lr;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent result = new Intent();
                result.putExtra("isUpdated", isUpdated);
                setResult(RESULT_OK, result);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    supportFinishAfterTransition();
                else
                    finish();

                return true;
            }
            default:
                return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab: {
                Intent intent = new Intent(MoreInfoLocalActivity.this, EditRecipeActivity.class);
                intent.putExtra("recipe", recipe);
                startActivityForResult(intent, REQUEST_EDIT_RECIPE_CODE);
            }
        }
    }

    @Override
    public void onBackPressed() {

        Intent result = new Intent();
        result.putExtra("isUpdated", isUpdated);
        setResult(RESULT_OK, result);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            supportFinishAfterTransition();
        else
            finish();
    }
}
