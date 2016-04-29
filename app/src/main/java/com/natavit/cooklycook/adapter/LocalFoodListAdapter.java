package com.natavit.cooklycook.adapter;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.natavit.cooklycook.R;
import com.natavit.cooklycook.datatype.MutableInteger;
import com.natavit.cooklycook.model.LocalRecipe;
import com.natavit.cooklycook.view.FoodListItem;

import java.util.ArrayList;

/**
 * This adapter is used to control the local recipes which will be shown up by ListView
 */
public class LocalFoodListAdapter extends BaseAdapter {

    private ArrayList<LocalRecipe> recipes;

    MutableInteger lastPositionInteger;

    public LocalFoodListAdapter(MutableInteger lastPositionInteger) {
        this.lastPositionInteger = lastPositionInteger;
    }

    public void setRecipe(ArrayList<LocalRecipe> recipes) {
        this.recipes = recipes;
    }

    @Override
    public int getCount() {
        if (recipes == null)
            return 0;

        return recipes.size();
    }

    @Override
    public Object getItem(int position) {
        return recipes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FoodListItem item;
        if (convertView != null) item = (FoodListItem) convertView;
        else item = new FoodListItem(parent.getContext());

        LocalRecipe recipe = (LocalRecipe) getItem(position);

        item.setNameText(recipe.getName());
        item.setImageUrl(recipe.getImgPath());

        if (position > lastPositionInteger.getValue()) {
            AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(parent.getContext(),
                    R.animator.up_from_bottom);
            set.setTarget(item);
            set.start();
            lastPositionInteger.setValue(position);
        }

        return item;
    }

}
