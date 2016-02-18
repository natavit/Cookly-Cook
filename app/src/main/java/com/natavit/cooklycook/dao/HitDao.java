package com.natavit.cooklycook.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Natavit on 1/30/2016 AD.
 */

/**
 *
 * Edamam FoodEdamam API
 *
 */
public class HitDao implements Parcelable {

    @SerializedName("recipe")
    @Expose
    private RecipeDao recipe;

    /**
     *
     * @return
     * The recipe
     */
    public RecipeDao getRecipe() {
        return recipe;
    }

    /**
     *
     * @param recipe
     * The recipe
     */
    public void setRecipe(RecipeDao recipe) {
        this.recipe = recipe;
    }

    protected HitDao(Parcel in) {
        recipe = in.readParcelable(RecipeDao.class.getClassLoader());
    }

    public static final Creator<HitDao> CREATOR = new Creator<HitDao>() {
        @Override
        public HitDao createFromParcel(Parcel in) {
            return new HitDao(in);
        }

        @Override
        public HitDao[] newArray(int size) {
            return new HitDao[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(recipe, flags);
    }
}