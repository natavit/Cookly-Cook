package com.natavit.cooklycook.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.natavit.cooklycook.R;
import com.natavit.cooklycook.dao.HitDao;

/**
 * This activity shows more information of the selected recipe (Online recipe)
 */
public class MoreInfoActivity extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;

    TextView tvFoodName;
    ImageView imageViewHeader;

    HitDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        initInstances();
    }

    /**
     * Initialize view variables
     */
    private void initInstances() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dao = getIntent().getParcelableExtra("dao");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(dao.getRecipe().getLabel());
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(MoreInfoActivity.this, android.R.color.transparent));

        tvFoodName = (TextView) findViewById(R.id.tvFoodName);
        String ingredient = "";
        for (String i : dao.getRecipe().getIngredientLines()) {
            ingredient += i + "\n";
        }
        tvFoodName.setText(ingredient);

        imageViewHeader = (ImageView) findViewById(R.id.imageViewHeader);
        Glide.with(MoreInfoActivity.this)
                .load(dao.getRecipe().getImage())
                .placeholder(R.drawable.loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .error() put image when unsuccessful downloading occurs
                .into(imageViewHeader);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
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
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            supportFinishAfterTransition();
        else
            finish();

    }
}
