package com.natavit.cooklycook.manager.http;

import com.natavit.cooklycook.dao.FoodCollectionDao;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Natavit on 2/12/2016 AD.
 */
public interface ApiService {

    @POST("/search")
    Call<FoodCollectionDao> loadFoodList(@Query("q") String q);

    @POST("/search")
    Call<FoodCollectionDao> loadMoreFoodList(@Query("q") String q, @Query("from") int from, @Query("to") int to);

}
