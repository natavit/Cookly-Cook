package com.natavit.cooklycook.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Natavit on 4/14/2016 AD.
 */
public class LocalRecipe implements Parcelable {

    private String name;
    private String imgPath;
    private ArrayList<LocalIngredient> ingredients;

    public LocalRecipe() {

    }

    protected LocalRecipe(Parcel in) {
        name = in.readString();
        imgPath = in.readString();
        ingredients = in.createTypedArrayList(LocalIngredient.CREATOR);
    }

    public static final Creator<LocalRecipe> CREATOR = new Creator<LocalRecipe>() {
        @Override
        public LocalRecipe createFromParcel(Parcel in) {
            return new LocalRecipe(in);
        }

        @Override
        public LocalRecipe[] newArray(int size) {
            return new LocalRecipe[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public ArrayList<LocalIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<LocalIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(imgPath);
        dest.writeTypedList(ingredients);
    }
}
