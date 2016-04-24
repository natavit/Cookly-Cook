package com.natavit.cooklycook.adapter;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;

import com.natavit.cooklycook.R;
import com.natavit.cooklycook.dao.FoodCollectionDao;
import com.natavit.cooklycook.dao.HitDao;
import com.natavit.cooklycook.datatype.MutableInteger;
import com.natavit.cooklycook.view.FoodListItem;

/**
 * Created by Natavit on 2/11/2016 AD.
 */
public class FoodListAdapter extends BaseAdapter {

    FoodCollectionDao dao;

    MutableInteger lastPositionInteger;

    public FoodListAdapter(MutableInteger lastPositionInteger) {
        this.lastPositionInteger = lastPositionInteger;
    }

    public void setDao(FoodCollectionDao dao) {
        this.dao = dao;
    }

    @Override
    public int getCount() {
        if (dao == null)
            return 1;
        if (dao.getHits() == null)
            return 1;

        return dao.getHits().size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return dao.getHits().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2; // 2 Type: Normal view and Progress Bar
    }

    @Override
    public int getItemViewType(int position) {
        return position == getCount() - 1 ? 1 : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == getCount() - 1) {
            ProgressBar item;
            if (convertView != null) {
                item = (ProgressBar) convertView;
            } else {
                LayoutInflater inflater = (LayoutInflater) parent.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                item = (ProgressBar) inflater.inflate(R.layout.progress_bar_load_more, parent, false);
            }

            return item;
        }


        FoodListItem item;
        if (convertView != null) item = (FoodListItem) convertView;
        else item = new FoodListItem(parent.getContext());

        HitDao dao = (HitDao) getItem(position);

        item.setNameText(dao.getRecipe().getLabel());
        item.setImageUrl(dao.getRecipe().getImage());

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
