package com.natavit.cooklycook.manager.http;

import com.natavit.cooklycook.dao.FoodCollectionDao;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Natavit on 2/12/2016 AD.
 */
public interface ApiService {

    @POST("search?app_id=f3c9f4db&app_key=a64b3bb38b5e6ec2cc8e172a6e10b32f&from=0&to=10")
    Call<FoodCollectionDao> loadFoodList(@Query("q") String q);

    @POST("search?app_id=f3c9f4db&app_key=a64b3bb38b5e6ec2cc8e172a6e10b32f")
    Call<FoodCollectionDao> loadMoreFoodList(@Query("q") String q, @Query("from") int from, @Query("to") int to);

}
