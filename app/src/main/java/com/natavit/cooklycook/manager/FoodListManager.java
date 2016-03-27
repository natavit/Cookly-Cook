package com.natavit.cooklycook.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.natavit.cooklycook.dao.FoodCollectionDao;
import com.natavit.cooklycook.dao.HitDao;

import java.util.ArrayList;

/**
 * Created by Natavit on 2/4/2016 AD.
 */
public class FoodListManager {


    private Context mContext;
    private FoodCollectionDao dao;

    private int nextPage = -1;

    public FoodListManager() {
        mContext = Contextor.getInstance().getContext();

        // Load data from Persistent Storage
        loadCache();
    }

    public FoodCollectionDao getDao() {
        return dao;
    }

    public void setDao(FoodCollectionDao dao) {
        this.dao = dao;
        setNextPage(dao.getTo());
        // Save to Persistent Storage
        saveCache();
    }

    public void insertDaoAtTopPosition(FoodCollectionDao newDao) {
        if (dao == null)
            dao = new FoodCollectionDao();
        if (dao.getHits() == null)
            dao.setHits(new ArrayList<HitDao>());
        dao.getHits().addAll(0, newDao.getHits());

        // Save to Persistent Storage
        saveCache();
    }

    public void appendDaoAtBottomPosition(FoodCollectionDao newDao) {
        if (dao == null)
            dao = new FoodCollectionDao();
        if (dao.getHits() == null)
            dao.setHits(new ArrayList<HitDao>());
        dao.getHits().addAll(getCount(), newDao.getHits());
        setNextPage(newDao.getTo());

        // Save to Persistent Storage
        saveCache();
    }

    private void setNextPage(int nextPage) {
        this.nextPage = nextPage+1;
    }

    public int getNextPage() {
        if (dao == null) return 0;
        if (dao.getHits() == null) return 0;
        if (dao.getHits().size() == 0) return 0;

//        int newMaxId = dao.getTo();
//        for (int i=1; i<dao.getHits().size(); i++) {
//            newMaxId = Math.max(maxId, newMaxId);
//        }
        return nextPage;
    }

    public int getCount() {
        if (dao == null) return 0;
        if (dao.getHits() == null) return 0;
        return dao.getHits().size();
    }

    public Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("dao", dao);
        return bundle;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        dao = savedInstanceState.getParcelable("dao");
    }

    private void saveCache() {
        FoodCollectionDao cacheDao = new FoodCollectionDao();

        if (dao != null && dao.getHits() != null)
            cacheDao.setHits(dao.getHits().subList(0, Math.min(20, dao.getHits().size())));

        // Convert object to json
        String json = new Gson().toJson(cacheDao);

        SharedPreferences prefs = mContext.getSharedPreferences("food",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("json", json);
        editor.putInt("page", nextPage);
        editor.apply();
    }

    private void loadCache() {
        SharedPreferences prefs = mContext.getSharedPreferences("food",
                Context.MODE_PRIVATE);
        String json = prefs.getString("json", null);
        int page = prefs.getInt("page", -1);
        if (json == null || page == -1)
            return;

        dao = new Gson().fromJson(json, FoodCollectionDao.class);
        nextPage = page;

    }

    public void clearCache() {
        mContext.getSharedPreferences("food",
                Context.MODE_PRIVATE)
                .edit().clear().commit();
    }
}
